package net.joefoxe.hexerei.item.custom.bottles;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;

public class HexBottleItem extends Item {
   public HexBottleItem(Properties pProperties) {
      super(pProperties);
   }

   public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entityLiving) {
      if (!world.isClientSide && entityLiving instanceof ServerPlayer player) {
         ItemStack itemstack3 = new ItemStack(Items.GLASS_BOTTLE);
         ItemStack itemstack = entityLiving.getItemInHand(InteractionHand.MAIN_HAND);
         if (itemstack.isEmpty()) {
            player.setItemInHand(InteractionHand.MAIN_HAND, itemstack3);
         } else if (!player.getInventory().add(itemstack3)) {
            player.drop(itemstack3, false);
         } else {
            player.initMenu(player.containerMenu);
         }
      }

      return super.finishUsingItem(stack, world, entityLiving);
   }

   public UseAnim getUseAnimation(ItemStack pStack) {
      return UseAnim.DRINK;
   }

   public Component getTooltip() {
      return Component.translatable("");
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(this.getTooltip());
      } else {
         tooltipComponents.add(
            Component.translatable(
                  "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
      }

      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }
}
