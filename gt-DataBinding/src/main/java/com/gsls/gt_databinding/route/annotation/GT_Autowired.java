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
    Class<Object> defaultValue() default Object.class;//如果为空，默认就只变量名的名称为 key

    // If required, app will be crash when value is null.
    // Primitive type wont be check!
    boolean required() default false;

    // Description of the field
    String desc() default "";
}
