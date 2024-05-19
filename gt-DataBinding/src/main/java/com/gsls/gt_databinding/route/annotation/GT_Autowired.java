package com.gsls.gt_databinding.route.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GT_Autowired {

    // Mark param's name or service name.
    String value() default "";//如果为空，默认就只变量名的名称为 key

    // Description of the field
    String desc() default "";
}
