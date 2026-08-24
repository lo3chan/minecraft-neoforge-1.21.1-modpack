package net.bobophones.bobolib.item;

import java.util.List;
import net.bobophones.bobolib.util.BU;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.block.Block;

public class ItemNameBlockItemWithDesc extends ItemNameBlockItem {
   public ItemNameBlockItemWithDesc(Block block, Properties props) {
      super(block, props);
   }

   public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag advanced) {
      list.add(Component.translatable("item." + BU.reg_loc(this) + ".desc").withStyle(ChatFormatting.GRAY));
   }
}
