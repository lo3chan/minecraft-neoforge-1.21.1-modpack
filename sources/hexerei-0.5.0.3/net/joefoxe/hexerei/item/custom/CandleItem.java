package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;

public class CandleItem extends BlockItem {
   public CandleItem(Block block, Properties properties) {
      super(block, properties.component(DataComponents.CUSTOM_DATA, CustomData.EMPTY));
      DispenserBlock.registerBehavior(this, Candle.DISPENSE_ITEM_BEHAVIOR);
   }

   public static void setHeight(ItemStack stack, int height) {
      CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      tag.putInt("height", height);
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
   }

   public static int getHeight(ItemStack stack) {
      CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      if (!tag.isEmpty()) {
         return tag.contains("height", 99) ? tag.getInt("height") : 7;
      } else {
         return 7;
      }
   }

   public static void setLayerFromBlock(ItemStack stack, String layer, String target) {
      CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      if (!tag.contains(target)) {
         tag.put(target, new CompoundTag());
      }

      CompoundTag innerTag = tag.getCompound(target);
      if (layer != null) {
         innerTag.putString("layer", layer);
         innerTag.putBoolean("layerFromBlockLocation", true);
      }

      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
   }

   public static String getBaseLayer(ItemStack stack) {
      CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      return getLayerLoc(tag.contains("base") ? tag.getCompound("base") : tag);
   }

   private static String getLayerLoc(CompoundTag tag) {
      if (tag.isEmpty()) {
         return null;
      } else {
         return tag.contains("layer") ? tag.getString("layer") : null;
      }
   }

   public static String getEffectLocation(ItemStack stack) {
      CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      return !tag.isEmpty() ? tag.getString("effect") : null;
   }

   public static int getColorValue(DyeColor color, ItemStack stack) {
      int dyeCol = HexereiUtil.getDyeColor(stack, 13419416);
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
}
