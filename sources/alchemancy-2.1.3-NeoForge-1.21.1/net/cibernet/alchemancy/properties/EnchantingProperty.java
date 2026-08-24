package net.cibernet.alchemancy.properties;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public class EnchantingProperty extends Property {
   @Override
   public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data) {
      return dataType == DataComponents.REPAIR_COST ? 0 : data;
   }

   @Override
   public int modifyEnchantmentValue(int originalValue, int result) {
      return Math.max(20, result * 2);
   }

   @Override
   public int getColor(ItemStack stack) {
      return 1856424;
   }
}
