package dev.corgitaco.enhancedcelestials2shaders.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LunarEventUtils {
   public static final Logger LOGGER = LoggerFactory.getLogger("LunarShaders");
   public static final String DEFAULT_EVENT = "default";
   public static final float ACTIVE_BLEND_THRESHOLD = 0.01F;
   public static final int SYNC_INTERVAL_TICKS = 10;
   public static final long SERVER_SYNC_TIMEOUT_MS = 5000L;
   private static final float[] DEFAULT_COLOR_INTERNAL = new float[]{1.0F, 1.0F, 1.0F};

   private LunarEventUtils() {
   }

   public static float[] getDefaultColor() {
      return copyArray(DEFAULT_COLOR_INTERNAL);
   }

   public static String formatEventName(String eventId) {
      if (eventId != null && !eventId.isEmpty() && !"default".equals(eventId)) {
         String id = eventId;
         int colonIndex = eventId.indexOf(58);
         if (colonIndex >= 0 && colonIndex < eventId.length() - 1) {
            id = eventId.substring(colonIndex + 1);
         }

         StringBuilder sb = new StringBuilder(id.length() + 4);
         boolean capitalizeNext = true;

         for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            if (c == '_' || c == '-') {
               sb.append(' ');
               capitalizeNext = true;
            } else if (capitalizeNext) {
               sb.append(Character.toUpperCase(c));
               capitalizeNext = false;
            } else {
               sb.append(Character.toLowerCase(c));
            }
         }

         return sb.toString().trim();
      } else {
         return "Default";
      }
   }

   public static boolean isActiveEvent(String eventId, float blend) {
      return blend > 0.01F && eventId != null && !"default".equals(eventId);
   }

   public static float clampBlend(float blend) {
      return Math.max(0.0F, Math.min(1.0F, blend));
   }

   public static float clampColor(float value) {
      return Math.max(0.0F, Math.min(2.0F, value));
   }

   public static float[] copyArray(float[] src) {
      return src != null && src.length >= 3 ? new float[]{src[0], src[1], src[2]} : new float[]{1.0F, 1.0F, 1.0F};
   }

   public static void copyInto(float[] src, float[] dst) {
      if (src != null && dst != null && src.length >= 3 && dst.length >= 3) {
         dst[0] = src[0];
         dst[1] = src[1];
         dst[2] = src[2];
      }
   }

   public static String truncate(String s, int maxLength) {
      if (s == null) {
         return "";
      } else {
         return s.length() > maxLength ? s.substring(0, maxLength) : s;
      }
   }

   public static boolean shouldLog(int counter, int interval) {
      return (counter & interval - 1) == 0;
   }

   public static void logInfo(String message, Object... args) {
      LOGGER.info("[LunarShaders] " + message, args);
   }

   public static void logDebug(String message, Object... args) {
      LOGGER.debug("[LunarShaders] " + message, args);
   }

   public static void logWarn(String message, Object... args) {
      LOGGER.warn("[LunarShaders] " + message, args);
   }

   public static void logError(String message, Object... args) {
      LOGGER.error("[LunarShaders] " + message, args);
   }
}
