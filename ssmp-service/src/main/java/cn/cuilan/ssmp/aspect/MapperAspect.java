package cn.cuilan.ssmp.aspect;

import cn.cuilan.ssmp.annotation.RedisCached;
import cn.cuilan.ssmp.common.BaseIdEntity;
import cn.cuilan.ssmp.common.BaseIdTimeEntity;
import cn.cuilan.ssmp.exception.BaseException;
import cn.cuilan.ssmp.mapper.CachedMapper;
import cn.cuilan.ssmp.redis.EntityRedisPrefix;
import cn.cuilan.ssmp.redis.RedisUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * AOP 环绕通知，Mapper 层方法增强
 * insert: 设置默认创建时间、更新时间
 * update: 仅支持 updateById 方法进行更新，并设置更新时间
 * <p>
 * 对查询方法增加: 分页插件
 *
 * @author zhang.yan
 * @date 2019-12-31
 */
@Slf4j
@Aspect
public class MapperAspect {

    // 页码
    private static final String PAGE_NUM = "pageNum";

    // 分页大小
    private static final String PAGE_SIZE = "pageSize";

    @Autowired
    private RedisUtils redisUtils;

    /**
     * AOP环绕通知，Mapper insert方法，默认插入时添加创建时间、更新时间
     */
    @Around("execution(* cn.cuilan.ssmp.*.*Mapper.insert(..))")
    public Object insert(ProceedingJoinPoint pjp) {
        try {
            Object arg = pjp.getArgs()[0];
            if (!(arg instanceof BaseIdTimeEntity)) {
                return pjp.proceed(pjp.getArgs());
            }
            BaseIdTimeEntity<?> entity = (BaseIdTimeEntity<?>) arg;
            if (entity.getCreateTime() == null) {
                Long now = System.currentTimeMillis();
                entity.setCreateTime(now);
                entity.setUpdateTime(now);
            }
            return pjp.proceed(pjp.getArgs());
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    /**
     * AOP环绕通知，Mapper update方法。
     * 更新方法仅支持updateById，其余update方法禁用，
     * 并设置更新时间。
     */
    @Around("execution(* cn.cuilan.ssmp.*.*Mapper.update*(..))")
    public Object update(ProceedingJoinPoint pjp) {
        try {
            MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
            if (!"updateById".equals(methodSignature.getMethod().getName())) {
                throw new RuntimeException("更新只能使用 BaseMapper.updateById");
            }
            Object arg = pjp.getArgs()[0];
            if (!(arg instanceof BaseIdTimeEntity)) {
                return pjp.proceed(pjp.getArgs());
            }
            BaseIdTimeEntity<?> entity = (BaseIdTimeEntity<?>) arg;
            if (entity.getUpdateTime() == null) {
                entity.setUpdateTime(System.currentTimeMillis());
            }
            return pjp.proceed(pjp.getArgs());
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    /**
     * AOP环绕通知，Mapper所有方法。
     * 如果查询方法中包含 @Param 注解，并且包含pageNum、pageSize两个参数，
     * 则设置 PageHelper 分页查询，参数设置在 ThreadLocal 中，线程安全。
     */
    @Around("execution(* cn.cuilan.ssmp.*.*Mapper.*(..))")
    public Object pagingGet(ProceedingJoinPoint pjp) {
        try {
            MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
            Integer pageNum = null;
            Integer pageSize = null;
            for (int i = 0; i < methodSignature.getMethod().getParameterCount(); i++) {
                Parameter parameter = methodSignature.getMethod().getParameters()[i];
                Param param = parameter.getAnnotation(Param.class);
                if (param != null) {
                    String name = param.value();
                    if (PAGE_NUM.equals(name)) {
                        pageNum = (Integer) pjp.getArgs()[i];
                    }
                    if (PAGE_SIZE.equals(name)) {
                        pageSize = (Integer) pjp.getArgs()[i];
                    }
                }
            }
            if (pageNum == null || pageSize == null) {
                return pjp.proceed(pjp.getArgs());
            }
            // 页码从1开始，数据库中从0开始
            if (pageNum - 1 < 0) {
                throw new BaseException("页码必须从1开始");
            }
            // 仅在需要分页的查询方法之前调用静态方法 startPage, 之后的一个查询方法将会被分页
            PageHelper.startPage(pageNum, pageSize);
            Object obj = pjp.proceed(pjp.getArgs());
            if (obj == null) {
                // noinspection rawtypes
                obj = new Page(pageNum, pageSize);
            }
            return obj;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @SuppressWarnings("unchecked")
    @Around("execution(* cn.cuilan.ssmp.*.*Mapper.selectCacheById(..))")
    public Object selectByIdCached(ProceedingJoinPoint pjp) {
        try {
            Object arg = pjp.getArgs()[0];
            Long id = (Long) arg;

            Class<CachedMapper<?>> mapperClass = (Class<CachedMapper<?>>) pjp.getTarget().getClass().getGenericInterfaces()[0];
            Class<BaseIdEntity<?>> entityClass = (Class<BaseIdEntity<?>>) ((ParameterizedType) mapperClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];

            RedisCached redisCached = entityClass.getAnnotation(RedisCached.class);
            if (redisCached == null) {
                return pjp.proceed(pjp.getArgs());
            }

            EntityRedisPrefix key = redisCached.value();
            String value = redisUtils.getString(key, id.toString());
            if (StringUtils.isNotBlank(value)) {
                JSONObject json = JSON.parseObject(value);
                return json.toJavaObject(entityClass);
            }

            Method selectById = BaseMapper.class.getDeclaredMethod("selectById", Serializable.class);
            BaseIdEntity<?> entity = (BaseIdEntity<?>) selectById.invoke(pjp.getTarget(), id);
            redisUtils.saveString(key, id.toString(), JSON.toJSONString(entity));
            return entity;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    /**
     * 增强 Mapper，提供批量插入的能力
     */
    @Around("execution(* cn.cuilan.ssmp.mapper.CommonMapper.saveBatch(..))")
    public Object saveBatch(ProceedingJoinPoint pjp) {
        return batchOperation(pjp);
    }

    // 批量更新
    @Around("execution(* cn.cuilan.ssmp.mapper.CommonMapper.updateBatchById(..))")
    public Object updateBatchById(ProceedingJoinPoint pjp) {
        return batchOperation(pjp);
    }

    // 批量更新或插入
    @Around("execution(* cn.cuilan.ssmp.mapper.CommonMapper.saveOrUpdateBatch(..))")
    public Object saveOrUpdateBatch(ProceedingJoinPoint pjp) {
        return batchOperation(pjp);
    }

    // 批量操作
    public boolean batchOperation(ProceedingJoinPoint pjp) {
        try {
            MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
            String methodName = methodSignature.getMethod().getName();
            // 是否为批量更新操作
            boolean isUpdateMethod = false;
            if ("saveOrUpdateBatch".equals(methodName) || "updateBatchById".equals(methodName)) {
                isUpdateMethod = true;
            }

            Object[] args = pjp.getArgs();
            Object arg = args[0];
            // 批量操作默认大小
            int batchSize = 1000;
            if (args.length == 2) {
                batchSize = (int) args[1];
            }
            if (!(arg instanceof Collection)) {
                throw new RuntimeException("批量操作失败，参数不正确");
            }
            Collection<?> c = (Collection<?>) arg;
            if (c.size() <= 0) {
                throw new RuntimeException("批量操作失败，参数不正确");
            }
            List<BaseIdEntity<?>> entityList = new ArrayList(c);
            Class<?> clazz = entityList.get(0).getClass();
            try (SqlSession batchSqlSession = SqlHelper.sqlSessionBatch(clazz)) {
                int i = 0;
                for (BaseIdEntity<?> entity : entityList) {
                    if (!(entity instanceof BaseIdTimeEntity)) {
                        continue;
                    }
                    BaseIdTimeEntity<?> timeEntity = (BaseIdTimeEntity<?>) entity;
                    Long now = System.currentTimeMillis();
                    if (isUpdateMethod) {
                        timeEntity.setUpdateTime(now);
                        MapperMethod.ParamMap<BaseIdEntity<?>> param = new MapperMethod.ParamMap<>();
                        param.put(Constants.ENTITY, timeEntity);
                        batchSqlSession.update(SqlHelper.table(clazz).getSqlStatement(SqlMethod.UPDATE_BY_ID.getMethod()), param);
                    } else {
                        if (timeEntity.getCreateTime() == null) {
                            timeEntity.setCreateTime(now);
                            timeEntity.setUpdateTime(now);
                        }
                        batchSqlSession.insert(SqlHelper.table(clazz).getSqlStatement(SqlMethod.INSERT_ONE.getMethod()), timeEntity);
                    }
                    if (i >= 1 && i % batchSize == 0) {
                        batchSqlSession.flushStatements();
                    }
                    i++;
                }
                batchSqlSession.flushStatements();
            }
            log.info("CommonMapper<{}> {} 批量操作, size: {}", clazz.getSimpleName(), methodName, entityList.size());

            // 更新缓存
            if (clazz.getAnnotation(RedisCached.class) != null) {
                EntityRedisPrefix key = clazz.getAnnotation(RedisCached.class).value();
                for (Object obj : c) {
                    BaseIdTimeEntity<?> entity = (BaseIdTimeEntity<?>) obj;
                    redisUtils.deleteKey(key, entity.getId().toString());
                    redisUtils.saveString(key, entity.getId().toString(), JSON.toJSONString(entity));
                }
            }
            return true;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
