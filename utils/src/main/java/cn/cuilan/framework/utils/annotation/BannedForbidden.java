package cn.cuilan.framework.utils.annotation;

import cn.cuilan.framework.utils.enumUtils.BannedTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BannedForbidden {

    BannedTypeEnum[] value() default {BannedTypeEnum.ALL};

}
