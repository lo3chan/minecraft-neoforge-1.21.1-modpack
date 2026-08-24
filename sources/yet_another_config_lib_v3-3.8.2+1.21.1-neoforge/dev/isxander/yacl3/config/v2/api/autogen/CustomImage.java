package dev.isxander.yacl3.config.v2.api.autogen;

import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.impl.autogen.EmptyCustomImageFactory;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.CompletableFuture;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CustomImage {
   String value() default "";

   int width() default 0;

   int height() default 0;

   Class<? extends CustomImage.CustomImageFactory<?>> factory() default EmptyCustomImageFactory.class;

   public interface CustomImageFactory<T> {
      CompletableFuture<ImageRenderer> createImage(T var1, ConfigField<T> var2, OptionAccess var3);
   }
}
