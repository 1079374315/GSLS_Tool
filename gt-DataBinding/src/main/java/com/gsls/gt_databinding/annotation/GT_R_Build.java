package com.gsls.gt_databinding.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要获取当前注解类的源码
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GT_R_Build {
    String value() default R;//可指定R文件 class名
    String type() default "";//可自定义R文件 build类型

    //默认 R 文件名
    String R = "R2";
    //目前可供 build类型(可自定义build类型)
    String debug = "debug";
    String release = "release";
}