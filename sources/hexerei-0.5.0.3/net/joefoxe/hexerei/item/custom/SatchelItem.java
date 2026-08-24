package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;

public class SatchelItem extends BroomAttachmentItem {
   public SatchelItem(Properties properties) {
      super(properties);
   }

   public static int getColorValue(DyeColor color, ItemStack stack) {
      int dyeCol = HexereiUtil.getDyeColor(stack, 4797474);
      return color == null && dyeCol != -1 ? dyeCol : color.getTextureDiffuseColor();
   }

   public static DyeColor getDyeColorNamed(ItemStack stack) {
      return HexereiUtil.getDyeColorNamed(stack.getHoverName().getString(), 0);
   }

   public interface ItemHandlerConsumer {
      void register(ItemColor var1, ItemLike... var2);
   }
}
