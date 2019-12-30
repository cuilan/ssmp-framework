package cn.cuilan.ssmp.admin.annotation;

import java.lang.annotation.*;

/**
 * web请求日志注解
 *
 * @author zhang.yan
 * @date 2019/7/4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface WebLog {

    /**
     * 日志详细信息
     *
     * @return 日志详细信息
     */
    String value() default "";

}
