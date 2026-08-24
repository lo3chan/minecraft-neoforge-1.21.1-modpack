package dev.isxander.yacl3.config.v2.api.autogen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Boolean {
   Boolean.Formatter formatter() default Boolean.Formatter.TRUE_FALSE;

   boolean colored() default false;

   public static enum Formatter {
      YES_NO,
      TRUE_FALSE,
      ON_OFF,
      CUSTOM;
   }
}
