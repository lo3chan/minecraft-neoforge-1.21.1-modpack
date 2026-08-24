package dev.isxander.yacl3.api.controller;

public interface NumberFieldControllerBuilder<T extends Number, B extends NumberFieldControllerBuilder<T, B>> extends ValueFormattableController<T, B> {
   B min(T var1);

   B max(T var1);

   B range(T var1, T var2);
}
