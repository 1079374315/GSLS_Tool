package com.gsls.gt_databinding.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 只有被 id号 标识的才会被识别绑定，包括 <include 标签
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GT_DataBinding {
    //设置的参数
    String setLayout();      //设置绑定布局
    String setBindingType(); //设置绑定类型

    //提供选择的绑定类型
    String ACTIVITY = "ClassType.ACTIVITY";
    String FRAGMENT = "ClassType.FRAGMENT";
    String DIALOG_FRAGMENT = "ClassType.DIALOG_FRAGMENT_X";
    String FLOATING_WINDOW = "ClassType.FLOATING_WINDOW";
    String POPUP_WINDOW = "ClassType.POPUP_WINDOW";
    String ADAPTER = "ClassType.ADAPTER";
    String VIEW = "ClassType.VIEW";
    String BASE_VIEW = "ClassType.BASE_VIEW";

    String WEB_VIEW = "ClassType.WEB_VIEW";
    String NOTIFICATION = "ClassType.NOTIFICATION";

}