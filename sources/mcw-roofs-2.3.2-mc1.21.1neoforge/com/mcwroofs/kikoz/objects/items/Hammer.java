package com.mcwroofs.kikoz.objects.items;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class Hammer extends Item {
   public Hammer(Object properties) {
      super(new Properties());
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> comp, TooltipFlag flag) {
      comp.add(this.getDescription().withStyle(ChatFormatting.GRAY));
   }

   @OnlyIn(Dist.CLIENT)
   public MutableComponent getDescription() {
      return Component.translatable("mcwroofs.hammer.desc");
   }
}
