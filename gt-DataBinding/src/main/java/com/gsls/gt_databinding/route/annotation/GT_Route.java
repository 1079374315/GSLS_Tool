package com.gsls.gt_databinding.route.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GT_Route {

    /**
     * 唯一路径
     */
    String value();//默认为注解类的 全路径+类名

    /**
     * 拦截器,是否登录(登录了就跳转成功，否则跳转登录页面，)，是否拥有权限(否则开始动态申请权限,申请成功就返回执行跳转)，
     * @return
     */
    String[] interceptors() default {};

    /**
     * 注释
     */
    String extras() default "GT库，不止这一个好用的库 官网: https://github.com/1079374315/GT";



}
