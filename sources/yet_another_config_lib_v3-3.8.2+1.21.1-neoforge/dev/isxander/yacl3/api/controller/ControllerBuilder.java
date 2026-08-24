package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Controller;
import org.jetbrains.annotations.ApiStatus.Internal;

@FunctionalInterface
public interface ControllerBuilder<T> {
   @Internal
   Controller<T> build();
}
