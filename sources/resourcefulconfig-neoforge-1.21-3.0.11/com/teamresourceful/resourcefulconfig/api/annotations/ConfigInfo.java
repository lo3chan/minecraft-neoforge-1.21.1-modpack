package com.teamresourceful.resourcefulconfig.api.annotations;

import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigInfo;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigInfo {
   String icon() default "box";

   @Nls(
      capitalization = Capitalization.Title
   )
   String title() default "";

   String titleTranslation() default "";

   String description() default "";

   String descriptionTranslation() default "";

   ConfigInfo.Link[] links() default {};

   @Target({ElementType.TYPE})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Color {
      @Pattern("^#(?:[0-9a-fA-F]{3}){1,2}$")
      String value();
   }

   @Target({ElementType.TYPE})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Gradient {
      @Pattern("\\d+deg")
      String value();

      @Pattern("^#(?:[0-9a-fA-F]{3}){1,2}$")
      String first();

      @Pattern("^#(?:[0-9a-fA-F]{3}){1,2}$")
      String second();
   }

   @Retention(RetentionPolicy.RUNTIME)
   public @interface Link {
      @Pattern("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)")
      String value();

      String icon();

      @Nls(
         capitalization = Capitalization.Title
      )
      String text();

      String textTranslation() default "";
   }

   @Target({ElementType.TYPE})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Provider {
      Class<? extends ResourcefulConfigInfo> value();
   }
}
