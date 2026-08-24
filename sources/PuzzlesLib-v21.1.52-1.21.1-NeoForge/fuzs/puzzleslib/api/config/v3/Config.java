package fuzs.puzzleslib.api.config.v3;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Config {
   String name() default "";

   String[] description() default {};

   String[] category() default {};

   boolean worldRestart() default false;

   boolean gameRestart() default false;

   @Target({ElementType.FIELD})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface AllowedValues {
      String[] values();
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface DoubleRange {
      double min() default 5.0E-324;

      double max() default 1.7976931348623157E308;
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface IntRange {
      int min() default -2147483648;

      int max() default 2147483647;
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface LongRange {
      long min() default -9223372036854775808L;

      long max() default 9223372036854775807L;
   }
}
