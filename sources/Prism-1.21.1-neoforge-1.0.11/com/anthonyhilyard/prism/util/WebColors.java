package com.anthonyhilyard.prism.util;

import com.anthonyhilyard.prism.text.DynamicColor;
import com.google.common.collect.Maps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class WebColors {
   private static final Map<String, IColor> webColorMap;

   public static IColor getColor(String colorName) {
      colorName = ConfigHelper.formatColorName(colorName);
      IColor result = null;
      if (webColorMap.containsKey(colorName)) {
         result = DynamicColor.fromColor(webColorMap.get(colorName));
      }

      return result;
   }

   static {
      Map<String, IColor> loadedColors = Maps.newHashMap();

      String line;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(WebColors.class.getClassLoader().getResourceAsStream("webcolors.csv")))) {
         while ((line = reader.readLine()) != null) {
            String[] components = line.split(",");
            if (components.length >= 2) {
               final String name = components[0];
               final int value = Integer.parseUnsignedInt(components[1], 16);
               loadedColors.put(ConfigHelper.formatColorName(name), new IColor() {
                  @Override
                  public String getName() {
                     return name;
                  }

                  @Override
                  public int getIntValue() {
                     return value;
                  }

                  @Override
                  public boolean isAnimated() {
                     return false;
                  }
               });
            }
         }
      } catch (IOException var8) {
      }

      webColorMap = Map.copyOf(loadedColors);
   }
}
