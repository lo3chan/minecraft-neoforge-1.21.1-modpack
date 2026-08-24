package com.mcwfurnitures.kikoz.objects;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class CraftingItem extends Item {
   public CraftingItem(Properties properties) {
      super(properties);
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      list.add(this.getDescription().withStyle(ChatFormatting.GRAY));
   }

   @OnlyIn(Dist.CLIENT)
   public MutableComponent getDescription() {
      return Component.translatable("mcwfurnitures.furnitureitem.desc");
   }

   public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
      return 300;
   }
}
