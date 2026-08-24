package com.anthonyhilyard.prism.util;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;

public class MinecraftColors {
   private static final Map<String, Integer> minecraftColorMap;

   public static TextColor getColor(String colorName) {
      colorName = ConfigHelper.formatColorName(colorName);
      TextColor result = null;
      if (minecraftColorMap.containsKey(colorName)) {
         result = TextColor.fromRgb(minecraftColorMap.get(colorName));
      }

      return result;
   }

   static {
      Map<String, Integer> loadedColors = Maps.newHashMap();

      for (ChatFormatting color : ChatFormatting.values()) {
         if (color.isColor()) {
            loadedColors.put(ConfigHelper.formatColorName(color.getName()), color.getColor());
         }
      }

      minecraftColorMap = Map.copyOf(loadedColors);
   }
}
