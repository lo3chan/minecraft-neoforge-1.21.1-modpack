package com.anthonyhilyard.iceberg.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSink;

public class StringRecomposer {
   public static List<FormattedText> recompose(List<ClientTooltipComponent> components) {
      List<FormattedText> recomposedLines = new ArrayList<>();

      for (ClientTooltipComponent component : components) {
         if (component instanceof ClientTextTooltip) {
            StringRecomposer.RecomposerSink recomposer = new StringRecomposer.RecomposerSink();
            ((ClientTextTooltip)component).text.accept(recomposer);
            recomposedLines.add(recomposer.getFormattedText());
         }
      }

      return recomposedLines;
   }

   private static class RecomposerSink implements FormattedCharSink {
      private StringBuilder builder = new StringBuilder();
      private MutableComponent text = Component.literal("").withStyle(Style.EMPTY);

      public boolean accept(int index, Style style, int charCode) {
         this.builder.append(Character.toChars(charCode));
         if (!style.equals(this.text.getStyle())) {
            this.text.append(Component.literal(this.builder.toString()).withStyle(style));
            this.builder.setLength(0);
         }

         return true;
      }

      public FormattedText getFormattedText() {
         this.text.append(Component.literal(this.builder.toString()).withStyle(this.text.getStyle()));
         return this.text;
      }
   }
}
