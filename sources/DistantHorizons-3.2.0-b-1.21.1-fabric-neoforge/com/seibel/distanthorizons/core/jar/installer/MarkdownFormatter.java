package com.seibel.distanthorizons.core.jar.installer;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class MarkdownFormatter {
   public static List<String> splitString(String text, int n) {
      return Arrays.asList(text.split("(?<=\\G.{" + n + "})"));
   }

   private static String replaceRegex(String text, String regex, String start, String end) {
      while (Pattern.compile(regex).matcher(text).find()) {
         text = text.replaceFirst(regex, start).replaceFirst(regex, end);
      }

      return text;
   }

   public abstract static class GenericFormat {
      public String convertFrom(String original) {
         System.out.println("Function \"convertFrom\" for \"" + this.getClass().getSimpleName() + "\" not done yet");
         return null;
      }

      public String convertTo(String original) {
         System.out.println("Function \"convertTo\" for \"" + this.getClass().getSimpleName() + "\" not done yet");
         return null;
      }
   }

   public static class HTMLFormat extends MarkdownFormatter.GenericFormat {
      @Override
      public String convertTo(String original) {
         original = original.replaceAll("\\\\\\n", "<br>").replaceAll("\\n", "<br>");
         original = MarkdownFormatter.replaceRegex(original, "\\*\\*", "<b>", "</b>");
         return MarkdownFormatter.replaceRegex(original, "~~", "<del>", "</del>");
      }
   }

   public static class MinecraftFormat extends MarkdownFormatter.GenericFormat {
      @Override
      public String convertTo(String original) {
         original = original.replaceAll("<br>|<br/>", "\n");
         original = MarkdownFormatter.replaceRegex(original, "\\*\\*", "§l", "§r");
         return MarkdownFormatter.replaceRegex(original, "~~", "§m", "§r");
      }
   }
}
