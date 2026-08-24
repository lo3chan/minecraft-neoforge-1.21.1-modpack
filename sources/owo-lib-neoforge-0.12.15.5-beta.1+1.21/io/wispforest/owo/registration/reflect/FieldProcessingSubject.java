package io.wispforest.owo.registration.reflect;

import java.lang.reflect.Field;

public interface FieldProcessingSubject<T> {
   Class<T> getTargetFieldType();

   default boolean shouldProcessField(T value, String identifier, Field field) {
      return true;
   }

   default void afterFieldProcessing() {
   }
}
