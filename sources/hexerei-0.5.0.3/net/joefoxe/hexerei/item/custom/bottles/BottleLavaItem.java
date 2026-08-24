package net.joefoxe.hexerei.item.custom.bottles;

import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class BottleLavaItem extends HexBottleItem {
   public static FoodProperties FOOD = new Builder().saturationModifier(0.0F).nutrition(0).alwaysEdible().build();
   static Random rand = new Random();

   public BottleLavaItem(Properties properties) {
      super(properties.food(FOOD));
   }

   @Override
   public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entityLiving) {
      if (!world.isClientSide && entityLiving instanceof ServerPlayer player) {
         entityLiving.setRemainingFireTicks(entityLiving.getRemainingFireTicks() + 10);
      }

      return super.finishUsingItem(stack, world, entityLiving);
   }

   public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
      super.inventoryTick(stack, world, entity, itemSlot, isSelected);
      if (rand.nextDouble() > 0.5) {
         stack.hurtAndBreak(1, (Player)entity, LivingEntity.getSlotForHand(InteractionHand.MAIN_HAND));
      }
   }

   @Override
   public Component getTooltip() {
      return Component.translatable("tooltip.hexerei.bottle_lava_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)));
   }
}
