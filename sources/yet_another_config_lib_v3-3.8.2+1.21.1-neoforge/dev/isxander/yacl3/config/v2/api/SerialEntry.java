package dev.isxander.yacl3.config.v2.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface SerialEntry {
   String value() default "";

   String comment() default "";

   boolean required() default true;

   boolean nullable() default false;
}
