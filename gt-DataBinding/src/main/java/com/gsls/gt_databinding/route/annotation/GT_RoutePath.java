package com.gsls.gt_databinding.route.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GT_RoutePath {

    /**
     * 路由总部名字
     */
    String name() default "RoutePath";//路由总部接口名字，默认路由总部名字为 RoutePath
    String module() default "";//默认当前路由总部是那个模块 注解类路径

}
