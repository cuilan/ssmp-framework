package cn.cuilan.ssmp.aspect;

import cn.cuilan.ssmp.common.BaseIdTimeEntity;
import cn.cuilan.ssmp.exception.BaseException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.annotations.Param;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Parameter;

/**
 * 分页插件AOP
 */
@Aspect
public class MapperAspect {

    private static final String PAGE_NUM = "pageNum";
    private static final String PAGE_SIZE = "pageSize";

    @Around("execution(* cn.cuilan.ssmp.*.*Mapper.insert(..))")
    public Object insert(ProceedingJoinPoint pjp) {
        try {
            Object arg = pjp.getArgs()[0];
            if (!(arg instanceof BaseIdTimeEntity)) {
                return pjp.proceed(pjp.getArgs());
            }
            BaseIdTimeEntity entity = (BaseIdTimeEntity) arg;
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
            BaseIdTimeEntity entity = (BaseIdTimeEntity) arg;
            if (entity.getUpdateTime() == null) {
                entity.setUpdateTime(System.currentTimeMillis());
            }
            return pjp.proceed(pjp.getArgs());

        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }

    }

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
                obj = new Page(pageNum, pageSize);
            }
            return obj;

        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
