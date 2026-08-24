package DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Repeatable(SerdeDefaultsContainer.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SerdeDefault {
   Class<?> cls() default Object.class;

   String provider();

   SerdePhase phase() default SerdePhase.BOTH;

   SerdeDefault.WhenValue[] whenValue() default {SerdeDefault.WhenValue.IS_MISSING};

   public static enum WhenValue {
      IS_MISSING,
      IS_NULL,
      IS_EMPTY;
   }
}
