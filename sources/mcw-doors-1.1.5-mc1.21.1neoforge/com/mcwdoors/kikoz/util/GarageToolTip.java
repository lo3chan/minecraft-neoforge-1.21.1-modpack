package com.mcwdoors.kikoz.util;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class GarageToolTip extends BlockItem {
   public GarageToolTip(Block block, Properties prop) {
      super(block, prop);
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, TooltipContext pContext, List<Component> comp, TooltipFlag flag) {
      comp.add(this.getDescription().withStyle(ChatFormatting.GRAY));
   }

   @OnlyIn(Dist.CLIENT)
   public MutableComponent getDescription() {
      return Component.translatable("mcwdoors.garage.desc");
   }
}
