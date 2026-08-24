package dev.isxander.yacl3.gui.controllers.dropdown;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.string.IStringController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractDropdownController<T> implements IStringController<T> {
   protected final Option<T> option;
   private final List<String> allowedValues;
   public final boolean allowEmptyValue;
   public final boolean allowAnyValue;

   protected AbstractDropdownController(Option<T> option, List<String> allowedValues, boolean allowEmptyValue, boolean allowAnyValue) {
      this.option = option;
      this.allowedValues = allowedValues;
      this.allowEmptyValue = allowEmptyValue;
      this.allowAnyValue = allowAnyValue;
   }

   protected AbstractDropdownController(Option<T> option, List<String> allowedValues) {
      this(option, allowedValues, false, false);
   }

   protected AbstractDropdownController(Option<T> option) {
      this(option, Collections.emptyList());
   }

   @Override
   public Option<T> option() {
      return this.option;
   }

   public List<String> getAllowedValues() {
      return this.getAllowedValues("");
   }

   public List<String> getAllowedValues(String inputField) {
      List<String> values = new ArrayList<>(this.allowedValues);
      if (this.allowEmptyValue && !values.contains("")) {
         values.add("");
      }

      if (this.allowAnyValue && !inputField.isBlank() && !this.allowedValues.contains(inputField)) {
         values.add(inputField);
      }

      String currentValue = this.getString();
      if (this.allowAnyValue && !this.allowedValues.contains(currentValue)) {
         values.add(currentValue);
      }

      return values;
   }

   public boolean isValueValid(String value) {
      return value.isBlank() ? this.allowEmptyValue : this.allowAnyValue || this.getAllowedValues().contains(value);
   }

   protected String getValidValue(String value) {
      return this.getValidValue(value, 0);
   }

   protected String getValidValue(String value, int offset) {
      if (offset == -1) {
         return this.getString();
      } else {
         String valueLowerCase = value.toLowerCase();
         return this.getAllowedValues(value).stream().filter(val -> val.toLowerCase().contains(valueLowerCase)).sorted((s1, s2) -> {
            String s1LowerCase = s1.toLowerCase();
            String s2LowerCase = s2.toLowerCase();
            if (s1LowerCase.startsWith(valueLowerCase) && !s2LowerCase.startsWith(valueLowerCase)) {
               return -1;
            } else {
               return !s1LowerCase.startsWith(valueLowerCase) && s2LowerCase.startsWith(valueLowerCase) ? 1 : s1.compareTo(s2);
            }
         }).skip(offset).findFirst().orElseGet(this::getString);
      }
   }
}
