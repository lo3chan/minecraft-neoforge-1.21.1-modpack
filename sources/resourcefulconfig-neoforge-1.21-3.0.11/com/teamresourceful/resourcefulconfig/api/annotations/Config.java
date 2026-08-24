package com.teamresourceful.resourcefulconfig.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Range;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {
   @Pattern("^[a-z0-9_/-]+$")
   String value();

   @Range(
      from = 0L,
      to = 2147483647L
   )
   int version() default 0;

   Class<?>[] categories() default {};
}
