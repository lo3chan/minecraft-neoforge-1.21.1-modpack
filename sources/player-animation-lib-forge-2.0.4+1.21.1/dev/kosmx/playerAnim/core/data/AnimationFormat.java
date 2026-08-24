package dev.kosmx.playerAnim.core.data;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public enum AnimationFormat {
   JSON_EMOTECRAFT("json"),
   JSON_MC_ANIM("json"),
   QUARK("emote"),
   BINARY("emotecraft"),
   SERVER(null),
   UNKNOWN(null);

   private static final Map<String, AnimationFormat> FORMATS;
   private final String extension;

   @Deprecated(
      forRemoval = true
   )
   public static AnimationFormat byFileName(String fileName) {
      if (fileName != null && !fileName.isEmpty()) {
         int i = fileName.lastIndexOf(46);
         if (i > 0) {
            fileName = fileName.substring(i + 1);
         }

         return byExtension(fileName);
      } else {
         return UNKNOWN;
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static AnimationFormat byExtension(String extension) {
      return extension != null && !extension.isEmpty() ? FORMATS.getOrDefault(extension.toLowerCase(), UNKNOWN) : UNKNOWN;
   }

   @Deprecated(
      forRemoval = true
   )
   private AnimationFormat(String extension) {
      this.extension = extension;
   }

   @Deprecated(
      forRemoval = true
   )
   public String getExtension() {
      return this.extension;
   }

   static {
      AnimationFormat[] formatsValues = values();
      FORMATS = new HashMap<>(formatsValues.length);

      for (AnimationFormat format : formatsValues) {
         if (format.extension != null) {
            FORMATS.putIfAbsent(format.extension, format);
         }
      }
   }
}
