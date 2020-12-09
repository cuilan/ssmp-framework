package cn.cuilan.ssmp.annotation;

import cn.cuilan.ssmp.redis.EntityRedisPrefix;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Redis缓存注解，为实体对象增强缓存查询能力
 *
 * @author zhang.yan
 * @date 2020/5/27
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCached {

    EntityRedisPrefix value();

}
