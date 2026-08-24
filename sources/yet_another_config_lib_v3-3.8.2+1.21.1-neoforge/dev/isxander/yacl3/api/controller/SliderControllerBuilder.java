package dev.isxander.yacl3.api.controller;

public interface SliderControllerBuilder<T extends Number, B extends SliderControllerBuilder<T, B>> extends ValueFormattableController<T, B> {
   B range(T var1, T var2);

   B step(T var1);
}
