package dev.isxander.yacl3.config.v2.api.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ListGroup {
   Class<? extends ListGroup.ValueFactory<?>> valueFactory();

   Class<? extends ListGroup.ControllerFactory<?>> controllerFactory();

   int maxEntries() default 0;

   int minEntries() default 0;

   boolean addEntriesToBottom() default false;

   public interface ControllerFactory<T> {
      ControllerBuilder<T> createController(ListGroup var1, ConfigField<List<T>> var2, OptionAccess var3, Option<T> var4);
   }

   public interface ValueFactory<T> {
      T provideNewValue();
   }
}
