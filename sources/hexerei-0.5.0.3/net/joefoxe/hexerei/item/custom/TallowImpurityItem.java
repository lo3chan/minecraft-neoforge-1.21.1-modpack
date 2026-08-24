package net.joefoxe.hexerei.item.custom;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;

public class TallowImpurityItem extends Item {
   public static FoodProperties FOOD = new Builder().saturationModifier(1.0F).nutrition(1).alwaysEdible().build();

   public TallowImpurityItem(Properties properties) {
      super(properties.food(FOOD));
   }

   public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entityLiving) {
      return super.finishUsingItem(stack, world, entityLiving);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      tooltipComponents.add(Component.translatable("tooltip.hexerei.tallow_impurity_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }
}
