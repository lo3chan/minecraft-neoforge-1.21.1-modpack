package net.joefoxe.hexerei.item.custom.bottles;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item.Properties;

public class BottleTallowItem extends HexBottleItem {
   public static FoodProperties FOOD = new Builder().saturationModifier(1.0F).nutrition(1).alwaysEdible().build();

   public BottleTallowItem(Properties properties) {
      super(properties.food(FOOD));
   }

   @Override
   public Component getTooltip() {
      return Component.translatable("tooltip.hexerei.bottle_tallow_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)));
   }
}
