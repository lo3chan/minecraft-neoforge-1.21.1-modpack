package com.anthonyhilyard.prism.item;

import com.anthonyhilyard.prism.Prism;
import com.anthonyhilyard.prism.text.DynamicColor;
import com.anthonyhilyard.prism.text.TextColors;
import com.anthonyhilyard.prism.util.IColor;
import com.anthonyhilyard.prism.util.WebColors;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag.Default;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ItemColors {
   private static boolean logItemColorError = true;

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

      TextColor formattingColor = TextColors.findFirstColorCode(item.getHoverName());
      if (formattingColor != null) {
         result = formattingColor;
      }

      ItemColors.ColorCollector colorCollector = new ItemColors.ColorCollector();
      item.getHoverName().getVisualOrderText().accept(colorCollector);
      if (colorCollector.getColor() != null) {
         result = colorCollector.getColor();
      }

      if (result == null || result.equals(item.getDisplayName().getStyle().getColor())) {
         Minecraft mc = Minecraft.getInstance();

         try {
            List<Component> lines = item.getTooltipLines(TooltipContext.EMPTY, mc.player, Default.ADVANCED);
            if (!lines.isEmpty() && lines.get(0).getStyle().getColor() != null) {
               result = lines.get(0).getStyle().getColor();
            }
         } catch (Exception var7) {
            if (logItemColorError) {
               logItemColorError = false;
               Prism.LOGGER.error("Error getting tooltip for item: " + item.toString());
               Prism.LOGGER.error(ExceptionUtils.getStackTrace(var7));
            }
         }
      }

      if (result == null) {
         result = defaultColor;
      }

      return (TextColor)(result == null ? (TextColor)WebColors.getColor("transparent") : new DynamicColor((IColor)result));
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
