package com.single.code.annotation;

/**
 * 创建时间：2021/4/23
 * 创建人：singleCode
 * 功能描述：
 **/
public @interface SRouter {
    String path();
    String group() default "";
}
