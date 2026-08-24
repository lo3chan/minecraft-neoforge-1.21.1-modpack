package net.bobophones.bobolib.item;

import java.util.List;
import net.bobophones.bobolib.util.BU;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class ItemWithDesc extends Item {
   public ItemWithDesc(Properties props) {
      super(props);
   }

   public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag advanced) {
      list.add(Component.translatable("item." + BU.reg_loc(this) + ".desc").withStyle(ChatFormatting.GRAY));
   }
}
