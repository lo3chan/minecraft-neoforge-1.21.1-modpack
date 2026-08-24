package DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SerdeSkipDeserializingIf {
   SerdeSkipDeserializingIf.SkipDeIf[] value();

   Class<?> customClass() default Object.class;

   String customCheck() default "";

   public static enum SkipDeIf {
      IS_MISSING,
      IS_NULL,
      IS_EMPTY,
      CUSTOM;
   }
}
