package com.anthonyhilyard.prism.util;

import com.anthonyhilyard.prism.text.DynamicColor;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.network.chat.TextColor;

public class ConfigHelper {
   public static List<ConfigHelper.ColorFormatDocumentation> colorFormatDocumentation() {
      return colorFormatDocumentation(false);
   }

   public static List<ConfigHelper.ColorFormatDocumentation> colorFormatDocumentation(boolean forKey) {
      return !forKey
         ? Arrays.asList(
            new ConfigHelper.ColorFormatDocumentation(
               "Hex color code",
               "A hex color code is preceded by # or 0x and must be quoted.  Supports 3, 4, 6, or 8 digit codes in the formats RGB, ARGB, RRGGBB, AARRGGBB.",
               "\"#F4C\"",
               "\"0xFEE0\"",
               "\"#40FF2E\"",
               "\"#CC00E2EE\""
            ),
            new ConfigHelper.ColorFormatDocumentation(
               "Decimal color code",
               "A decimal color code, which is just a hex color code converted to decimal.  May or may not be quoted.",
               "15614720",
               "\"4278251143\""
            ),
            new ConfigHelper.ColorFormatDocumentation(
               "Minecraft color name", "One of the standard 16 Minecraft color names.  Must be quoted.", "\"red\"", "\"dark_purple\"", "\"gold\""
            ),
            new ConfigHelper.ColorFormatDocumentation(
               "Web color name",
               "One of the standard 140 web/HTML color names or \"transparent\".  Must be quoted.",
               "\"chartreuse\"",
               "\"darkorange\"",
               "\"deeppink\"",
               "\"deepskyblue\""
            ),
            new ConfigHelper.ColorFormatDocumentation(
               "Modifiers",
               "Colors specified in any of the above formats can be modified by using modifiers.\nModifiers are specified after any color in the format \"<+, -, or =><h, s, v, r, g, b, or a><amount>\".\nThe letters represent h - hue, s - saturation, v - value, r - red, g - green, b - blue, a - alpha.\nValid amounts are 0 to 255 for all types except hue, which accepts 0 to 359.",
               "\"red+h15\"",
               "\"#saddlebrown-v20+s5\"",
               "\"10_aqua_aqua+v15-h5\"",
               "\"#F4C-r15-v10=a40\""
            ),
            new ConfigHelper.ColorFormatDocumentation(
               "Animated color",
               "An animated color that fades from one to another in sequence.\nA string in the format \"<duration in seconds>_<list of color definitions separated by underscores>\".  Must be quoted.",
               "\"10_black_#7FFF00\"",
               "\"5.5_gold_orange_orangered\"",
               "\"20_red_orange_yellow_green_blue_purple\""
            )
         )
         : Arrays.asList(
            new ConfigHelper.ColorFormatDocumentation(
               "Hex color code",
               "A hex color code is preceded by # or 0x and must be quoted.  Supports 3, 4, 6, or 8 digit codes in the formats RGB, ARGB, RRGGBB, or AARRGGBB.",
               "\"#F4C\"",
               "\"0xFEE0\"",
               "\"#40FF2E\"",
               "\"#CC00E2EE\""
            ),
            new ConfigHelper.ColorFormatDocumentation(
               "Decimal color code",
               "A decimal color code, which is just a hex color code converted to decimal.  May or may not be quoted.",
               "15614720",
               "\"4278251143\""
            ),
            new ConfigHelper.ColorFormatDocumentation(
               "Minecraft color name", "One of the standard 16 Minecraft color names.  May or may not be quoted.", "\"red\"", "\"dark_purple\"", "\"gold\""
            ),
            new ConfigHelper.ColorFormatDocumentation(
               "Web color name",
               "One of the standard 140 web/HTML color names or \"transparent\".  May or may not be quoted.",
               "\"chartreuse\"",
               "\"darkorange\"",
               "\"deeppink\"",
               "\"deepskyblue\""
            ),
            new ConfigHelper.ColorFormatDocumentation(
               "Modifiers",
               "Colors specified in any of the above formats can be modified by using modifiers.\nModifiers are specified after any color in the format \"<+, -, or =><h, s, v, r, g, b, or a><amount>\".\nThe letters represent h - hue, s - saturation, v - value, r - red, g - green, b - blue, a - alpha.\nValid amounts are 0 to 255 for all types except hue, which accepts 0 to 359.\nUsing any modifiers will REQUIRE the key to be quoted.\n",
               "\"red+h15\"",
               "\"#saddlebrown-v20+s5\"",
               "\"10_aqua_aqua+v15-h5\"",
               "\"#F4C-r15-v10=a40\""
            ),
            new ConfigHelper.ColorFormatDocumentation(
               "Animated color",
               "An animated color that fades from one to another in sequence.\nA string in the format \"<duration in seconds>_<list of color definitions separated by underscores>\".  May or may not be quoted.",
               "\"10_black_#7FFF00\"",
               "\"5.5_gold_orange_orangered\"",
               "\"20_red_orange_yellow_green_blue_purple\""
            )
         );
   }

   public static TextColor applyModifiers(List<String> modifiers, int color) {
      return TextColor.fromRgb(applyModifiers(modifiers, DynamicColor.fromRgb(color)).getValue());
   }

   public static TextColor applyModifiers(List<String> modifiers, TextColor color) {
      return applyModifiers(modifiers, color.getValue());
   }

   public static DynamicColor applyModifiers(List<String> modifiers, DynamicColor color) {
      Map<Character, BiFunction<Integer, Integer, Integer>> modifierFuncs = Map.of('+', (v, a) -> v + a, '-', (v, a) -> v - a, '=', (v, a) -> a);

      for (String modifier : modifiers) {
         if (modifier.length() >= 3) {
            char type = modifier.toLowerCase().charAt(1);

            int amount;
            BiFunction<Integer, Integer, Integer> mod;
            try {
               amount = Integer.parseInt(modifier.substring(2));
               mod = modifierFuncs.get(modifier.charAt(0));
            } catch (Exception var9) {
               continue;
            }

            if (mod != null) {
               switch (type) {
                  case 'a':
                     color = DynamicColor.fromARGB(mod.apply(color.alpha(), amount), color.red(), color.green(), color.blue());
                     break;
                  case 'b':
                     color = DynamicColor.fromARGB(color.alpha(), color.red(), color.green(), mod.apply(color.blue(), amount));
                  case 'c':
                  case 'd':
                  case 'e':
                  case 'f':
                  case 'i':
                  case 'j':
                  case 'k':
                  case 'l':
                  case 'm':
                  case 'n':
                  case 'o':
                  case 'p':
                  case 'q':
                  case 't':
                  case 'u':
                  default:
                     break;
                  case 'g':
                     color = DynamicColor.fromARGB(color.alpha(), color.red(), mod.apply(color.green(), amount), color.blue());
                     break;
                  case 'h':
                     color = DynamicColor.fromAHSV(color.alpha(), mod.apply(color.hue(), amount), color.saturation(), color.value());
                     break;
                  case 'r':
                     color = DynamicColor.fromARGB(color.alpha(), mod.apply(color.red(), amount), color.green(), color.blue());
                     break;
                  case 's':
                     color = DynamicColor.fromAHSV(color.alpha(), color.hue(), mod.apply(color.saturation(), amount), color.value());
                     break;
                  case 'v':
                     color = DynamicColor.fromAHSV(color.alpha(), color.hue(), color.saturation(), mod.apply(color.value(), amount));
               }
            }
         }
      }

      return color;
   }

   public static IColor parseColor(Object value, boolean allowAlpha) {
      List<Object> unparsedColors = null;
      List<IColor> colors = Lists.newArrayList();
      float duration = 10.0F;
      if (value instanceof String string && string.contains("_")) {
         List<String> entries = List.of(string.split("_"));

         try {
            duration = Float.parseFloat(entries.get(0));
            unparsedColors = entries.stream().skip(1L).map(s -> (Object)s).toList();
         } catch (NumberFormatException var16) {
            unparsedColors = entries.stream().map(s -> (Object)s).toList();
         }
      } else if (value instanceof List<?> list && !list.isEmpty()) {
         List<Object> entries = null;

         try {
            entries = Collections.unmodifiableList((List<? extends Object>)list);
            if (entries.get(0) instanceof String string) {
               duration = Float.parseFloat(string);
            } else if (entries.get(0) instanceof Number number) {
               duration = number.floatValue();
            }

            unparsedColors = entries.subList(1, entries.size());
         } catch (NumberFormatException var14) {
            unparsedColors = entries;
         } catch (ClassCastException var15) {
         }
      } else {
         unparsedColors = Lists.newArrayList();
         unparsedColors.add(value);
      }

      if (unparsedColors != null && !unparsedColors.isEmpty()) {
         for (Object unparsedValue : unparsedColors) {
            if (unparsedValue == null) {
               return null;
            }

            boolean isTransparent = false;
            TextColor color = null;
            List<String> modifiers = List.of();
            if (unparsedValue instanceof String string) {
               String var25 = string.replace("~", "").replace("-", "~-").replace("+", "~+");
               if (var25.contains("~")) {
                  modifiers = Lists.newArrayList(var25.split("~"));
                  if (modifiers.size() > 1) {
                     var25 = modifiers.remove(0);
                  }
               }

               if (isValidColorCode(var25)) {
                  boolean isHex = var25.startsWith("0x") || var25.startsWith("#");
                  String colorString = var25.toLowerCase().replace("0x", "").replace("#", "");
                  if (isHex && colorString.length() == 3 || colorString.length() == 4) {
                     colorString = colorString.replaceAll(".", "$0$0");
                  }

                  color = (TextColor)TextColor.parseColor("#" + colorString).result().orElse(null);
               } else if (var25.toLowerCase().contentEquals("transparent")) {
                  isTransparent = true;
                  color = TextColor.fromRgb(0);
               } else {
                  color = MinecraftColors.getColor(var25);
                  if (color == null) {
                     color = (TextColor & IColor)WebColors.getColor(var25);
                  }
               }
            } else if (unparsedValue instanceof Number number) {
               color = TextColor.fromRgb(number.intValue());
            }

            if (color != null && !isTransparent && color.getValue() > 0 && color.getValue() <= 16777215) {
               color = TextColor.fromRgb(color.getValue() | 0xFF000000);
            }

            if (color != null) {
               color = applyModifiers(modifiers, color);
            }

            if (!allowAlpha && color.getValue() > 16777215) {
               color = TextColor.fromRgb(color.getValue() | 0xFF000000);
            }

            if (color != null) {
               colors.add((IColor)color);
            }
         }

         if (colors.size() == 1) {
            return colors.get(0);
         } else {
            return colors.size() > 1 ? new DynamicColor(colors, duration) : null;
         }
      } else {
         return null;
      }
   }

   public static IColor parseColor(Object value) {
      return parseColor(value, true);
   }

   public static boolean validateColor(Object value) {
      return parseColor(value) != null;
   }

   private static boolean isValidColorCode(Object value) {
      if (value == null) {
         return false;
      } else if (value instanceof String string) {
         if (string.isEmpty()) {
            return false;
         } else {
            boolean isHex = false;
            if (string.startsWith("0x") || string.startsWith("#")) {
               isHex = true;
               string = string.replace("0x", "").replace("#", "");
            }

            if (isHex) {
               try {
                  Long number = Long.parseUnsignedLong(string, 16);
                  if (Long.compareUnsigned(number, 0L) < 0 || Long.compareUnsigned(number, -1L) > 0) {
                     return false;
                  }
               } catch (NumberFormatException var6) {
                  return false;
               }

               return string.length() == 3 || string.length() == 4 || string.length() == 6 || string.length() == 8;
            } else {
               try {
                  long number = Long.parseUnsignedLong(string);
                  if (Long.compareUnsigned(number, 0L) < 0 || Long.compareUnsigned(number, -1L) > 0) {
                     return false;
                  }
               } catch (NumberFormatException var7) {
                  return false;
               }

               return string.length() <= 10;
            }
         }
      } else if (!(value instanceof Number number)) {
         return false;
      } else {
         Long colorValue = number.longValue();
         return Long.compareUnsigned(colorValue, 0L) >= 0 && Long.compareUnsigned(colorValue, -1L) <= 0;
      }
   }

   static String formatColorName(String input) {
      return input.toLowerCase().replace(" ", "").replace("_", "");
   }

   public record ColorFormatDocumentation(String name, String description, List<String> examples) {
      public ColorFormatDocumentation(String name, String description, String... examples) {
         this(name, description, Arrays.asList(examples));
      }
   }
}
