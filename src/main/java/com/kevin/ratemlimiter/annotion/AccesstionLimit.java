package com.kevin.ratemlimiter.annotion;

import java.lang.annotation.*;

/**
 * @author kevin
 * @version 1.0
 * @date 2021-07-03 0:39
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface AccesstionLimit {
    String methodKey() default "";

    int limit();
}
