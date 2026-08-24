package dev.isxander.yacl3.config.v2.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;

public interface ReadOnlyFieldAccess<T> {
   T get();

   String name();

   Type type();

   Class<T> typeClass();

   <A extends Annotation> Optional<A> getAnnotation(Class<A> var1);
}
