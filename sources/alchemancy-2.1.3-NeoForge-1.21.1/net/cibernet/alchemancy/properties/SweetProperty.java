package net.cibernet.alchemancy.properties;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public class SweetProperty extends Property {
   @Override
   public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data) {
      return dataType == DataComponents.FOOD && data instanceof FoodProperties foodProperties
         ? new FoodProperties(
            foodProperties.nutrition(),
            foodProperties.saturation() * 1.5F,
            true,
            foodProperties.eatSeconds(),
            foodProperties.usingConvertsTo(),
            foodProperties.effects()
         )
         : super.modifyDataComponent(stack, dataType, data);
   }

   @Override
   public int getPriority() {
      return 50;
   }

   @Override
   public int getColor(ItemStack stack) {
      return 16435711;
   }
}
