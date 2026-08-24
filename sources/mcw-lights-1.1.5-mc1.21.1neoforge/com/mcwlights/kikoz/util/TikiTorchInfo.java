package com.mcwlights.kikoz.util;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class TikiTorchInfo extends DyeableInfo {
   public TikiTorchInfo(Block block, Properties prop) {
      super(block, prop);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      list.add(this.getDescription().withStyle(ChatFormatting.GRAY));
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public MutableComponent getDescription() {
      return Component.translatable("mcwlights.lights.tikitorchinfo");
   }

   public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
      return 300;
   }
}
