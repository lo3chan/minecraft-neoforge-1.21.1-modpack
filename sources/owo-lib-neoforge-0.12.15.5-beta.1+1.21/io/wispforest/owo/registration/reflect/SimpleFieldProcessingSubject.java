package io.wispforest.owo.registration.reflect;

import java.lang.reflect.Field;

public interface SimpleFieldProcessingSubject<T> extends FieldProcessingSubject<T> {
   void processField(T var1, String var2, Field var3);
}
