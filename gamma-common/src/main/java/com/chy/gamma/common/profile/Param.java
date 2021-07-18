package com.chy.gamma.common.profile;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Param {

    String value() default "";

    boolean nullable() default false;

}
