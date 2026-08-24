package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormattableController;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.config.v2.api.ReadOnlyFieldAccess;
import dev.isxander.yacl3.config.v2.api.autogen.CustomFormat;
import dev.isxander.yacl3.config.v2.api.autogen.FormatTranslation;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class AutoGenUtils {
   public static <T> void addCustomFormatterToController(ControllerBuilder<T> controller, ReadOnlyFieldAccess<T> field) {
      Optional<CustomFormat> formatter = field.getAnnotation(CustomFormat.class);
      Optional<FormatTranslation> translation = field.getAnnotation(FormatTranslation.class);
      if (formatter.isPresent() && translation.isPresent()) {
         throw new YACLAutoGenException("'%s': Cannot use both @CustomFormatter and @FormatTranslation on the same field.".formatted(field.name()));
      } else if (!formatter.isEmpty() || !translation.isEmpty()) {
         if (!(controller instanceof ValueFormattableController<T, ?> typedBuilder)) {
            throw new YACLAutoGenException(
               "Attempted to use @CustomFormatter or @FormatTranslation on an option factory for field '%s' that uses a controller that does not support this."
                  .formatted(field.name())
            );
         } else {
            formatter.ifPresent(
               formatterClass -> {
                  try {
                     typedBuilder.formatValue((ValueFormatter<T>)formatterClass.value().getConstructor().newInstance());
                  } catch (Exception var4x) {
                     throw new YACLAutoGenException(
                        "'%s': Failed to instantiate formatter class %s.".formatted(field.name(), formatterClass.value().getName()), var4x
                     );
                  }
               }
            );
            translation.ifPresent(annotation -> typedBuilder.formatValue(v -> Component.translatable(annotation.value(), new Object[]{v})));
         }
      }
   }

   public static <T> T constructNoArgsClass(Class<T> clazz, Supplier<String> constructorNotFoundConsumer, Supplier<String> constructorFailedConsumer) {
      try {
         return clazz.getConstructor().newInstance();
      } catch (NoSuchMethodException var4) {
         throw new YACLAutoGenException(constructorNotFoundConsumer.get(), var4);
      } catch (Exception var5) {
         throw new YACLAutoGenException(constructorFailedConsumer.get(), var5);
      }
   }
}
