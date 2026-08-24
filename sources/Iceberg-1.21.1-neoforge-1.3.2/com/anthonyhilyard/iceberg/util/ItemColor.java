package com.anthonyhilyard.iceberg.util;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag.Default;

@Deprecated(
   forRemoval = true
)
public class ItemColor {
   public static TextColor findFirstColorCode(Component textComponent) {
      String rawTitle = textComponent.getString();

      for (int i = 0; i < rawTitle.length(); i += 2) {
         if (rawTitle.charAt(i) != 167) {
            return null;
         }

         try {
            ChatFormatting format = ChatFormatting.getByCode(rawTitle.charAt(i + 1));
            if (format != null && format.isColor()) {
               return TextColor.fromLegacyFormat(format);
            }
         } catch (StringIndexOutOfBoundsException var4) {
            return null;
         }
      }

      return null;
   }

   public static TextColor getColorForItem(ItemStack item, TextColor defaultColor) {
      TextColor result = null;
      result = item.getDisplayName().getStyle().getColor();
      if (item.getItem() != null
         && item.getItem().getName(item) != null
         && item.getItem().getName(item).getStyle() != null
         && item.getItem().getName(item).getStyle().getColor() != null) {
         result = item.getItem().getName(item).getStyle().getColor();
      }

      if (!item.getHoverName().getStyle().isEmpty() && item.getHoverName().getStyle().getColor() != null) {
         result = item.getHoverName().getStyle().getColor();
      }

      TextColor formattingColor = findFirstColorCode(item.getHoverName());
      if (formattingColor != null) {
         result = formattingColor;
      }

      ItemColor.ColorCollector colorCollector = new ItemColor.ColorCollector();
      item.getHoverName().getVisualOrderText().accept(colorCollector);
      if (colorCollector.getColor() != null) {
         result = colorCollector.getColor();
      }

      if (result == null || result.equals(item.getDisplayName().getStyle().getColor())) {
         Minecraft mc = Minecraft.getInstance();
         List<Component> lines = null;

         try {
            lines = item.getTooltipLines(TooltipContext.EMPTY, mc.player, Default.ADVANCED);
         } catch (Exception var8) {
         }

         if (lines != null && !lines.isEmpty()) {
            result = lines.get(0).getStyle().getColor();
         }
      }

      if (result == null) {
         result = defaultColor;
      }

      return result;
   }

   private static class ColorCollector implements FormattedCharSink {
      private TextColor color = null;

      public boolean accept(int index, Style style, int codePoint) {
         if (style.getColor() != null) {
            this.color = style.getColor();
            return false;
         } else {
            return true;
         }
      }

      public TextColor getColor() {
         return this.color;
      }
   }
}
