package net.mehvahdjukaar.moonlight.api.util;

import java.net.URI;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class TextHelper {
   private static final Pattern CAMEL_CASE_BOUNDARY = Pattern.compile("(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])(?![A-Z]s(?![a-z]))");

   public static String getReadableName(String name) {
      return Arrays.stream(name.replace(":", "_").split("_"))
         .flatMap(word -> Arrays.stream(CAMEL_CASE_BOUNDARY.split(word)))
         .filter(word -> !word.isEmpty())
         .<CharSequence>map(StringUtils::capitalize)
         .collect(Collectors.joining(" "));
   }

   public static Component getReadableComponent(String key, String... arguments) {
      Component translated = Component.translatable(key, arguments);
      if (!translated.getString().equals(key)) {
         return translated;
      } else {
         StringBuilder aa = new StringBuilder();

         for (String s : arguments) {
            aa.append("_").append(s);
         }

         return Component.literal(getReadableName(key + aa));
      }
   }

   public static String formatNumber(double v) {
      return v == Math.rint(v) && !Double.isInfinite(v) ? String.valueOf((long)v) : String.valueOf(v);
   }

   @Nullable
   public static String urlHost(String url) {
      try {
         String host = URI.create(url.trim()).getHost();
         return host == null ? null : host.toLowerCase(Locale.ROOT);
      } catch (Exception var2) {
         return null;
      }
   }
}
