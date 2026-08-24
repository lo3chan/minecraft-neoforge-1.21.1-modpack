package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.items.ItemStackHandler;

public class MixingCauldronItem extends BlockItem {
   public MixingCauldronItem(Block block, Properties properties) {
      super(block, properties);
   }

   public static int getColorValue(DyeColor color, ItemStack stack) {
      int dyeCol = HexereiUtil.getDyeColor(stack, 16760348);
      return color == null && dyeCol != -1 ? dyeCol : color.getTextureDiffuseColor();
   }

   public static int getDyeColorNamed(String name) {
      if (HexereiUtil.getDyeColorNamed(name) != null) {
         float f3 = ClientEvents.getClientTicks() / 10.0F * 4.0F % 16.0F / 16.0F;
         DyeColor col1 = HexereiUtil.getDyeColorNamed(name, 0);
         DyeColor col2 = HexereiUtil.getDyeColorNamed(name, 1);
         float[] afloat1 = HexereiUtil.rgbIntToFloatArray(col1.getTextureDiffuseColor());
         float[] afloat2 = HexereiUtil.rgbIntToFloatArray(col2.getTextureDiffuseColor());
         float f = afloat1[0] * (1.0F - f3) + afloat2[0] * f3;
         float f1 = afloat1[1] * (1.0F - f3) + afloat2[1] * f3;
         float f2 = afloat1[2] * (1.0F - f3) + afloat2[2] * f3;
         return HexereiUtil.getColorValue(f, f1, f2);
      } else {
         return 0;
      }
   }

   public static DyeColor getDyeColorNamed(ItemStack stack) {
      return HexereiUtil.getDyeColorNamed(stack.getHoverName().getString());
   }

   public InteractionResult place(BlockPlaceContext context) {
      return super.place(context);
   }

   public ItemStackHandler createHandler() {
      return new ItemStackHandler(36) {
         public int getSlotLimit(int slot) {
            return 64;
         }
      };
   }
}
