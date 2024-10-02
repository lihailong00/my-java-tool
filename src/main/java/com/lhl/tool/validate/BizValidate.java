package com.lhl.tool.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface BizValidate {

    /**
     * 动态校验类名
     * @return
     */
    String bizValidator() default "";

    /**
     * 动态校验方法名
     * @return
     */
    String validateMethod() default "";

    /**
     * 是否忽略校验
     * @return
     */
    boolean ignore() default false;
}
