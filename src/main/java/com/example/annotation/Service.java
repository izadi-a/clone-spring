package com.example.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component // Mark it as a @Component as well
public @interface Service {
    // You could add a name attribute here if you want to identify beans by a specific name.
    String value() default "";
}