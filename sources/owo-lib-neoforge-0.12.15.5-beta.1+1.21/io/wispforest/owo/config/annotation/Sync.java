package io.wispforest.owo.config.annotation;

import io.wispforest.owo.config.Option;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Sync {
   Option.SyncMode value();
}
