package com.teamresourceful.resourcefulconfig.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.intellij.lang.annotations.Language;

public class ConfigOption {
   @Target({ElementType.FIELD})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Color {
      int[] presets() default {};

      boolean alpha() default false;
   }

   @Target({ElementType.FIELD})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Draggable {
      String[] value() default {};
   }

   @Target({ElementType.FIELD, ElementType.TYPE})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Hidden {
   }

   @Target({ElementType.FIELD})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Keybind {
   }

   @Target({ElementType.FIELD})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Multiline {
   }

   @Target({ElementType.FIELD})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Range {
      double min();

      double max();
   }

   @Target({ElementType.FIELD})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Regex {
      @Language("RegExp")
      String value();
   }

   @Target({ElementType.FIELD})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface SearchTerm {
      String[] value();
   }

   @Target({ElementType.FIELD})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Select {
      String value() default "Select";
   }

   @Target({ElementType.FIELD})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Separator {
      String value();

      String description() default "";
   }

   @Target({ElementType.FIELD})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Slider {
   }
}
