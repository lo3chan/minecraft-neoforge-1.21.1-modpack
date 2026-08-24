package dev.architectury.hooks.forge;

import net.minecraft.world.item.DyeColor;

public class DyeColorHooksImpl {
   public static int getColorValue(DyeColor dyeColor) {
      return dyeColor.getTextureDiffuseColor();
   }
}
