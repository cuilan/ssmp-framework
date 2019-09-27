package cn.cuilan.base.cache.scenario;

import cn.cuilan.base.cache.AbstractCache;
import cn.cuilan.base.cache.LocalCache;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class PageCache<T> extends AbstractCache<String, PageCache.Loader<T>> {

    public PageResult<T> paging(Object singleArg, Integer pageNumber, Integer pageSize) {
        return paging(Collections.singletonList(singleArg), pageNumber, null, null, pageSize);
    }

    public PageResult<T> paging(Object singleArg, Long afterTime, Long sinceTime, Integer pageSize) {
        return paging(Collections.singletonList(singleArg), null, afterTime, sinceTime, pageSize);
    }

    public PageResult<T> paging(List<Object> argList, Integer pageNumber, Long lesserThis, Long greaterThis, Integer pageSize) {
        String rawKey = remoteKey(argList.stream().map(String::valueOf).collect(Collectors.joining(",")));
        String field = pageNumber + "," + lesserThis + "," + greaterThis + "," + pageSize;
        PageResult pageResult = remoteCache.hget(rawKey, field);
        if (pageResult != null) {
            return pageResult;
        }

        pageResult = loader.load(new PageParam(pageNumber, lesserThis, greaterThis, pageSize, argList));
        remoteCache.hset(rawKey, field, pageResult);
        remoteCache.expire(rawKey, remoteExpireSecond, TimeUnit.SECONDS);
        return pageResult;
    }

    public boolean clear(String singleArg) {
        return clear(Arrays.asList(singleArg));
    }

    public boolean clear(String firstArg, String secondArg) {
        return clear(Arrays.asList(firstArg, secondArg));
    }

    public boolean clear(List<Object> argList) {
        String rawKey = remoteKey(argList.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(",")));
        return remoteCache.del(rawKey);
    }

    @Override
    public void del(String key) {
        remoteCache.del(remoteKey(key));
    }

    @Override
    protected void setLocalCache(LocalCache localCache) {

    }

    @FunctionalInterface
    public interface Loader<T> extends AbstractCache.Loader<PageParam, PageResult<T>> {

        @Override
        PageResult<T> load(PageParam param);
    }

    @Data
    public static class PageParam {
        private Integer pageNum;
        private Long lesserThis;
        private Long greaterThis;
        private Integer pageSize;
        private List<Object> args;

        private Integer offset;
        private Integer limit;

        public PageParam(Integer pageNum, Long lesserThis, Long greaterThis, Integer pageSize, List<Object> args) {
            this.pageNum = pageNum;
            this.lesserThis = lesserThis;
            this.greaterThis = greaterThis;
            this.pageSize = pageSize;
            this.args = args;

            Integer offset = (getPageNum() - 1) * getPageSize();
            if (offset < 0) {
                this.offset = 0;
            } else {
                this.offset = offset;
            }

            this.limit = this.pageSize;
        }

        public String getFirstArg() {
            if (args.get(0) == null) {
                return null;
            }
            return args.get(0).toString();
        }

    }

    @Data
    public static class PageResult<T> {
        private List<T> dataList;
        private long total;
    }
}
