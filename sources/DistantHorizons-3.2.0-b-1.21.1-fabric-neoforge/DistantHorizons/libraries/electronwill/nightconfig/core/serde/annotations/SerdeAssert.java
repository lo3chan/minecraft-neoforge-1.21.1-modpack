package DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Repeatable(SerdeAssertsContainer.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SerdeAssert {
   SerdeAssert.AssertThat[] value();

   Class<?> customClass() default Object.class;

   String customCheck() default "";

   SerdePhase phase() default SerdePhase.BOTH;

   public static enum AssertThat {
      NOT_NULL,
      NOT_EMPTY,
      CUSTOM;
   }
}
