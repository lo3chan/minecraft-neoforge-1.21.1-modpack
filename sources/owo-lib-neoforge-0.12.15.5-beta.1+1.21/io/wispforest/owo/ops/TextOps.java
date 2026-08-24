package io.wispforest.owo.ops;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public final class TextOps {
   private TextOps() {
   }

   public static MutableComponent concat(Component prefix, Component text) {
      return Component.empty().append(prefix).append(text);
   }

   public static MutableComponent withColor(String text, int color) {
      return Component.literal(text).setStyle(Style.EMPTY.withColor(color));
   }

   public static MutableComponent translateWithColor(String text, int color) {
      return Component.translatable(text).setStyle(Style.EMPTY.withColor(color));
   }

   public static MutableComponent withFormatting(String text, ChatFormatting... formatting) {
      String[] textPieces = text.split("§");
      if (formatting.length != textPieces.length) {
         return withColor("unmatched format specifiers - this is a bug", 16711807);
      } else {
         MutableComponent textBase = Component.literal(textPieces[0]).withStyle(formatting[0]);

         for (int i = 1; i < textPieces.length; i++) {
            textBase.append(Component.literal(textPieces[i]).withStyle(formatting[i]));
         }

         return textBase;
      }
   }

   public static MutableComponent withColor(String text, int... colors) {
      String[] textPieces = text.split("§");
      if (colors.length != textPieces.length) {
         return withColor("unmatched color specifiers - this is a bug", 16711807);
      } else {
         MutableComponent textBase = withColor(textPieces[0], colors[0]);

         for (int i = 1; i < textPieces.length; i++) {
            textBase.append(withColor(textPieces[i], colors[i]));
         }

         return textBase;
      }
   }

   public static int width(Font renderer, Iterable<Component> texts) {
      int width = 0;

      for (Component text : texts) {
         width = Math.max(width, renderer.width(text));
      }

      return width;
   }

   public static int widthOrdered(Font renderer, Iterable<FormattedCharSequence> texts) {
      int width = 0;

      for (FormattedCharSequence text : texts) {
         width = Math.max(width, renderer.width(text));
      }

      return width;
   }

   public static int color(ChatFormatting formatting) {
      return formatting.getColor() == null ? 0 : formatting.getColor();
   }
}
