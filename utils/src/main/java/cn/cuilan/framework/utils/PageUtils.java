package cn.cuilan.framework.utils;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageUtils {
    public static final int pageSize = 25;
    public static final int pageMaxSize = Integer.MAX_VALUE;
    public static final int ROW_WARN= 128;

    public static Map<String, Object> create(Long total, List<?> data) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("total", total);
        map.put("list", data);
        return map;
    }

    public static Double getScore(Long afterTime) {
        double max = 100000000000000000000d;
        if (null == afterTime) {
            return max;
        }
        return afterTime.doubleValue();
    }

    public static Page page(Integer pageNumber, Integer pageSize, Long total){
        return new Page(pageNumber, pageSize, total);
    }

    @Data
    public static class Page {
        private Long offset;
        private Long limit;
        private Integer pageNumber;
        private Integer pageSize;
        private Long total;
        private Integer pages;

        public Page() {
        }

        public Page(Integer pageNumber, Integer pageSize) {
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
            if (pageNumber == null) {
                this.pageNumber = 1;
            }
            if (pageSize == null) {
                this.pageNumber = 25;
            }

            this.offset = (pageNumber - 1) * pageSize * 1L;
            this.limit= this.pageSize * 1L;
        }

        public Page(Integer pageNumber, Integer pageSize, Long total) {
            this(pageNumber, pageSize);
            this.total = total;
            pages(this.total);
        }

//        public Page(Long offset, Long limit, Integer pageNumber, Integer pageSize, Long total) {
//            this(pageNumber, pageSize, total);
//            if (offset != null) {
//                this.offset = offset;
//            }
//            if (limit != null) {
//                this.limit = limit;
//            }
//        }

        public void pages(Long total) {
            this.total = total;
            if (total == null) {
                pages = 0;
                return;
            }
            if (pageSize > 0) {
                pages = (int) (total / pageSize + ((total % pageSize == 0) ? 0 : 1));
            } else {
                pages = 0;
            }

        }
    }

}
