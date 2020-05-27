package cn.cuilan.ssmp.aspect;

import cn.cuilan.ssmp.common.BaseIdEntity;
import cn.cuilan.ssmp.common.BaseIdTimeEntity;
import cn.cuilan.ssmp.exception.BaseException;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
@Aspect
public class MapperAspect {

    // 页码
    private static final String PAGE_NUM = "pageNum";

    // 分页大小
    private static final String PAGE_SIZE = "pageSize";

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

    /**
     * 增强 Mapper，提供批量插入的能力
     */
    @SuppressWarnings("unchecked")
    @Around("execution(* cn.cuilan.ssmp.mapper.CommonMapper.saveBatch(..))")
    public Object saveBatch(ProceedingJoinPoint pjp) {
        Object arg = pjp.getArgs()[0];
        List<BaseIdEntity<Long>> entityList = new ArrayList<>((Collection<? extends BaseIdEntity<Long>>) arg);
        if (entityList.size() == 0) {
            throw new RuntimeException("saveBatch 参数 Collection 不能为空.");
        }
        Class<?> clazz = entityList.get(0).getClass();

        int batchSize = 1000;
        String sqlStatement = SqlHelper.table(clazz).getSqlStatement(SqlMethod.INSERT_ONE.getMethod());
        try (SqlSession batchSqlSession = SqlHelper.sqlSessionBatch(clazz)) {
            int i = 0;
            for (BaseIdEntity<Long> anEntityList : entityList) {
                batchSqlSession.insert(sqlStatement, anEntityList);
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        }
        return true;
    }
}
