package com.mrcrayfish.configured.impl.neoforge;

import com.mrcrayfish.configured.api.IAllowedEnums;
import java.util.HashSet;
import java.util.Set;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.ValueSpec;

public class NeoForgeEnumValue<T extends Enum<T>> extends NeoForgeValue<T> implements IAllowedEnums<T> {
   public NeoForgeEnumValue(EnumValue<T> configValue, ValueSpec valueSpec) {
      super(configValue, valueSpec);
   }

   @Override
   public Set<T> getAllowedValues() {
      Set<T> allowedValues = new HashSet<>();
      T[] enums = this.initialValue.getDeclaringClass().getEnumConstants();

      for (T e : enums) {
         if (this.valueSpec.test(e)) {
            allowedValues.add(e);
         }
      }

      return allowedValues;
   }
}
