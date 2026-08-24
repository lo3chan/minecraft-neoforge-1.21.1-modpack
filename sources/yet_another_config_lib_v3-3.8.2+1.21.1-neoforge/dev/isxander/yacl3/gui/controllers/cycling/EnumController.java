package dev.isxander.yacl3.gui.controllers.cycling;

import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import java.util.Arrays;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.util.OptionEnum;

public class EnumController<T extends Enum<T>> extends CyclingListController<T> {
   public static <T extends Enum<T>> Function<T, Component> getDefaultFormatter() {
      return value -> {
         if (value instanceof NameableEnum nameableEnum) {
            return nameableEnum.getDisplayName();
         } else {
            return (Component)(value instanceof OptionEnum translatableOption ? translatableOption.getCaption() : Component.literal(value.toString()));
         }
      };
   }

   public EnumController(Option<T> option, Class<T> enumClass) {
      this(option, getDefaultFormatter(), enumClass.getEnumConstants());
   }

   public EnumController(Option<T> option, Function<T, Component> valueFormatter, T[] availableValues) {
      super(option, Arrays.asList(availableValues), valueFormatter);
   }

   public static <T extends Enum<T>> EnumController<T> createInternal(Option<T> option, ValueFormatter<T> formatter, T[] values) {
      return new EnumController<>(option, formatter::format, values);
   }
}
