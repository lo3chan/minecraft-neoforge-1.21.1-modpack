package dev.isxander.yacl3.gui.controllers.dropdown;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import java.util.Arrays;
import java.util.stream.Stream;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class EnumDropdownController<E extends Enum<E>> extends AbstractDropdownController<E> {
   protected final ValueFormatter<E> formatter;

   public EnumDropdownController(Option<E> option, ValueFormatter<E> formatter) {
      super(
         option, Arrays.stream(option.pendingValue().getDeclaringClass().getEnumConstants()).map(formatter::format).<String>map(Component::getString).toList()
      );
      this.formatter = formatter;
   }

   @Override
   public String getString() {
      return (R)this.formatter.format(this.option().pendingValue()).getString();
   }

   @Override
   public void setFromString(String value) {
      this.option().requestSet(this.getEnumFromString(value));
   }

   private E getEnumFromString(String value) {
      value = value.toLowerCase();

      for (E constant : (Enum[])this.option().pendingValue().getDeclaringClass().getEnumConstants()) {
         if (this.formatter.format(constant).getString().toLowerCase().equals(value)) {
            return constant;
         }
      }

      return this.option().pendingValue();
   }

   @Override
   public boolean isValueValid(String value) {
      value = value.toLowerCase();

      for (String constant : this.getAllowedValues()) {
         if (constant.equals(value)) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected String getValidValue(String value, int offset) {
      return this.getValidEnumConstants(value).skip(offset).findFirst().orElseGet(this::getString);
   }

   @NotNull
   protected Stream<String> getValidEnumConstants(String value) {
      String valueLowerCase = value.toLowerCase();
      return this.getAllowedValues().stream().filter(constant -> constant.toLowerCase().contains(valueLowerCase)).sorted((s1, s2) -> {
         String s1LowerCase = s1.toLowerCase();
         String s2LowerCase = s2.toLowerCase();
         if (s1LowerCase.startsWith(valueLowerCase) && !s2LowerCase.startsWith(valueLowerCase)) {
            return -1;
         } else {
            return !s1LowerCase.startsWith(valueLowerCase) && s2LowerCase.startsWith(valueLowerCase) ? 1 : s1.compareTo(s2);
         }
      });
   }

   @Override
   public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
      return new EnumDropdownControllerElement<>(this, screen, widgetDimension);
   }
}
