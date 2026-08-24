package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.CyclingListControllerBuilderImpl;

public interface CyclingListControllerBuilder<T> extends ValueFormattableController<T, CyclingListControllerBuilder<T>> {
   CyclingListControllerBuilder<T> values(T... var1);

   CyclingListControllerBuilder<T> values(Iterable<? extends T> var1);

   static <T> CyclingListControllerBuilder<T> create(Option<T> option) {
      return new CyclingListControllerBuilderImpl<>(option);
   }
}
