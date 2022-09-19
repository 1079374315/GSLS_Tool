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
public @interface GT_HttpCallBuild {
    //如果你的项目无法支持 method.getParameters(); 方法 (通过 JDK1.8 获取方法形参的名称)，那就需要使用该注解并加入 gt-DataBinding.jar 包
}