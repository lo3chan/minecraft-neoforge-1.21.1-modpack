package com.teamresourceful.resourcefulconfig.api.loader;

import com.google.common.base.Suppliers;
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public interface ConfigParser {
   Supplier<List<ConfigParser>> PARSERS = Suppliers.memoize(() -> {
      List<ConfigParser> parsers = new ArrayList<>();
      ServiceLoader.load(ConfigParser.class).forEach(parsers::add);
      parsers.sort((o1, o2) -> Integer.compare(o2.priority(), o1.priority()));
      return parsers;
   });

   int priority();

   @Nullable
   ResourcefulConfig parse(Class<?> var1);

   static ResourcefulConfig tryParse(Class<?> clazz) {
      for (ConfigParser parser : PARSERS.get()) {
         ResourcefulConfig config = parser.parse(clazz);
         if (config != null) {
            return config;
         }
      }

      throw new IllegalArgumentException("No parser found for class " + clazz.getName());
   }
}
