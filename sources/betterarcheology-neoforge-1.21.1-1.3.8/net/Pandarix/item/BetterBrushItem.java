package net.Pandarix.item;

import java.util.List;
import net.Pandarix.util.BetterBrushTiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.NotNull;

public class BetterBrushItem extends BrushItem {
   private final BetterBrushTiers brushTier;

   public BetterBrushItem(Properties pProperties, BetterBrushTiers pBrushTier) {
      super(pProperties);
      this.brushTier = pBrushTier;
   }

   public int getBrushingSpeed() {
      return this.brushTier.getBrushTickRate();
   }

   @NotNull
   public UseAnim getUseAnimation(@NotNull ItemStack pStack) {
      return UseAnim.BRUSH;
   }

   public boolean isEnchantable(ItemStack itemStack) {
      return itemStack.getItem() instanceof BetterBrushItem;
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      list.add(Component.literal("+" + this.brushTier.getSpeedFactor() + "% Brushing Speed").withStyle(ChatFormatting.DARK_GREEN));
      super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
   }
}
