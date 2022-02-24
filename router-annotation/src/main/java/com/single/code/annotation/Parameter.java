package com.single.code.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 创建时间：2021/4/23
 * 创建人：singleCode
 * 功能描述：
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Parameter {
    /**
     * getIntent().getIntExtra("count",0);
     * @return count
     */
    String name() default "";
}
