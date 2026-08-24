package io.wispforest.owo.registration.reflect;

import java.lang.reflect.Field;
import net.minecraft.core.Registry;

public interface AutoRegistryContainer<T> extends FieldProcessingSubject<T> {
   Registry<T> getRegistry();

   default void postProcessField(String namespace, T value, String identifier, Field field) {
   }

   static <T> void register(Class<? extends AutoRegistryContainer<T>> container, String namespace, boolean recurse) {
      FieldRegistrationHandler.register(container, namespace, recurse);
   }

   static <T> Class<T> conform(Class<?> input) {
      return (Class<T>)input;
   }
}
