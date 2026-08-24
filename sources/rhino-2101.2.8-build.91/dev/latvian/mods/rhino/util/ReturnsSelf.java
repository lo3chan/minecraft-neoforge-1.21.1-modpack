package dev.latvian.mods.rhino.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Repeatable(ReturnsSelfContainer.class)
public @interface ReturnsSelf {
   Class<?> value() default Object.class;

   boolean copy() default false;
}
