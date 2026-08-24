package me.flashyreese.mods.sodiumextra.client.util;

import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.Util;
import net.minecraft.Util.OS;

public final class MacReducedResolution {
   private static boolean openGlBackend;

   public static boolean isEnabled() {
      return Util.getPlatform() == OS.OSX && SodiumExtraClientMod.options().extraSettings.reduceResolutionOnMac;
   }

   public static int reduce(int value) {
      return Math.max(1, value / 2);
   }

   public static void useOpenGlBackend() {
      openGlBackend = true;
   }

   public static boolean shouldReduceFramebuffer() {
      return isEnabled() && !openGlBackend;
   }

   public static boolean shouldUseWindowSizeForInitialFramebuffer() {
      return isEnabled() && openGlBackend;
   }

   public static boolean shouldScalePresentation(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight) {
      return isEnabled() && (sourceWidth < targetWidth || sourceHeight < targetHeight);
   }
}
