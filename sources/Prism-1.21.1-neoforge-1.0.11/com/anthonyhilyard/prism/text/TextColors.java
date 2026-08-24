package com.anthonyhilyard.prism.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

public class TextColors {
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
}
