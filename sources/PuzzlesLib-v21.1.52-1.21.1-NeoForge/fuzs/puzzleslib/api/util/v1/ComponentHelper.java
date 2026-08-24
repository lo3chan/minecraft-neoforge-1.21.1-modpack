package fuzs.puzzleslib.api.util.v1;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public final class ComponentHelper {
   private ComponentHelper() {
   }

   public static Component getAsComponent(String text) {
      return StyleCombiningCharSink.of(text, Style.EMPTY).getAsComponent();
   }

   public static Component getAsComponent(FormattedText formattedText) {
      return StyleCombiningCharSink.of(formattedText, Style.EMPTY).getAsComponent();
   }

   public static String getAsString(FormattedText formattedText) {
      return StyleCombiningCharSink.of(formattedText, Style.EMPTY).getAsString();
   }

   public static Component getAsComponent(FormattedCharSequence formattedCharSequence) {
      return StyleCombiningCharSink.of(formattedCharSequence, Style.EMPTY).getAsComponent();
   }

   public static String getAsString(FormattedCharSequence formattedCharSequence) {
      return StyleCombiningCharSink.of(formattedCharSequence, Style.EMPTY).getAsString();
   }

   public static Component getAsComponent(String text, Style style) {
      return Component.literal(text).withStyle(style);
   }

   public static String getAsString(String text, Style style) {
      return !style.isEmpty() ? getAsString(style) + text + ChatFormatting.RESET : text;
   }

   public static Style getDefaultStyle(Component component) {
      Set<Component> visitedComponents = new HashSet<>();

      while (!component.getSiblings().isEmpty() && component.getStyle().isEmpty()) {
         if (visitedComponents.contains(component)) {
            return Style.EMPTY;
         }

         visitedComponents.add(component);
         component = (Component)component.getSiblings().getFirst();
      }

      return component.getStyle();
   }

   public static Style getDefaultStyle(String text) {
      Objects.requireNonNull(text, "text is null");
      Component component = getAsComponent(text);
      if (!text.isEmpty() && component.getString().isEmpty()) {
         StringBuilder stringBuilder = new StringBuilder(text);
         int index = text.indexOf(ChatFormatting.RESET.toString());
         stringBuilder.insert(index != -1 ? index : text.length(), " ");
         return getDefaultStyle(getAsComponent(stringBuilder.toString()));
      } else {
         return getDefaultStyle(component);
      }
   }

   public static Style getDefaultStyle(FormattedText formattedText) {
      return getDefaultStyle(getAsComponent(formattedText));
   }

   public static Style getDefaultStyle(FormattedCharSequence formattedCharSequence) {
      return getDefaultStyle(getAsComponent(formattedCharSequence));
   }

   public static String getAsString(Style style) {
      Objects.requireNonNull(style, "style is null");
      if (style.isEmpty()) {
         return "";
      } else {
         StringBuilder stringBuilder = new StringBuilder();
         getLegacyFormat(style, chatFormatting -> stringBuilder.append(chatFormatting.toString()));
         return stringBuilder.toString();
      }
   }

   public static void getLegacyFormat(Style style, Consumer<ChatFormatting> chatFormattingConsumer) {
      Objects.requireNonNull(style, "style is null");
      if (!style.isEmpty()) {
         if (style.getColor() != null) {
            ChatFormatting color = ChatFormatting.getByName(style.getColor().serialize());
            if (color != null) {
               chatFormattingConsumer.accept(color);
            }
         }

         if (style.isBold()) {
            chatFormattingConsumer.accept(ChatFormatting.BOLD);
         }

         if (style.isItalic()) {
            chatFormattingConsumer.accept(ChatFormatting.ITALIC);
         }

         if (style.isUnderlined()) {
            chatFormattingConsumer.accept(ChatFormatting.UNDERLINE);
         }

         if (style.isStrikethrough()) {
            chatFormattingConsumer.accept(ChatFormatting.STRIKETHROUGH);
         }

         if (style.isObfuscated()) {
            chatFormattingConsumer.accept(ChatFormatting.OBFUSCATED);
         }
      }
   }

   public static Style sanitizeLegacyFormat(Style style) {
      Objects.requireNonNull(style, "style is null");
      if (style.isEmpty()) {
         return style;
      } else {
         if (!style.isBold()) {
            style = style.withBold(null);
         }

         if (!style.isItalic()) {
            style = style.withItalic(null);
         }

         if (!style.isUnderlined()) {
            style = style.withUnderlined(null);
         }

         if (!style.isStrikethrough()) {
            style = style.withStrikethrough(null);
         }

         if (!style.isObfuscated()) {
            style = style.withObfuscated(null);
         }

         return style;
      }
   }
}
