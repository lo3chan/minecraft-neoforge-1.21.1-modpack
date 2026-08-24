package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.DropdownStringControllerBuilderImpl;
import java.util.List;

public interface DropdownStringControllerBuilder extends StringControllerBuilder {
   DropdownStringControllerBuilder values(List<String> var1);

   DropdownStringControllerBuilder values(String... var1);

   DropdownStringControllerBuilder allowEmptyValue(boolean var1);

   DropdownStringControllerBuilder allowAnyValue(boolean var1);

   static DropdownStringControllerBuilder create(Option<String> option) {
      return new DropdownStringControllerBuilderImpl(option);
   }
}
