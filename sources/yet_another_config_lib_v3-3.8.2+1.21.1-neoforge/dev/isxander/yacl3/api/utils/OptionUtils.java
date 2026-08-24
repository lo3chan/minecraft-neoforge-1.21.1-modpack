package dev.isxander.yacl3.api.utils;

import com.google.common.collect.UnmodifiableIterator;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class OptionUtils {
   public static Stream<Option<?>> getFlatOptions(YetAnotherConfigLib yacl) {
      return yacl.categories()
         .stream()
         .flatMap(category -> category.groups().stream())
         .flatMap(group -> group instanceof ListOption<?> list ? Stream.of(list) : group.options().stream());
   }

   public static void consumeOptions(YetAnotherConfigLib yacl, Function<Option<?>, Boolean> consumer) {
      UnmodifiableIterator var2 = yacl.categories().iterator();

      while (var2.hasNext()) {
         ConfigCategory category = (ConfigCategory)var2.next();
         UnmodifiableIterator var4 = category.groups().iterator();

         while (var4.hasNext()) {
            OptionGroup group = (OptionGroup)var4.next();
            if (group instanceof ListOption<?> list) {
               if (consumer.apply(list)) {
                  return;
               }
            } else {
               UnmodifiableIterator var7 = group.options().iterator();

               while (var7.hasNext()) {
                  Option<?> option = (Option<?>)var7.next();
                  if (consumer.apply(option)) {
                     return;
                  }
               }
            }
         }
      }
   }

   public static void forEachOptions(YetAnotherConfigLib yacl, Consumer<Option<?>> consumer) {
      consumeOptions(yacl, opt -> {
         consumer.accept(opt);
         return false;
      });
   }
}
