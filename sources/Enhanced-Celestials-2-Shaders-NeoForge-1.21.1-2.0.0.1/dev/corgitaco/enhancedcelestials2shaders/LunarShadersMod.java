package dev.corgitaco.enhancedcelestials2shaders;

import dev.corgitaco.enhancedcelestials2shaders.api.LunarEventUtils;
import dev.corgitaco.enhancedcelestials2shaders.platform.PlatformHelper;

public final class LunarShadersMod {
   public static final String MOD_ID = "enhancedcelestials2shaders";
   public static final String MOD_NAME = "Enhanced Celestials Shader Support";
   public static final String VERSION = "2.1.0";
   private static boolean initialized = false;
   private static PlatformHelper platform = null;

   public static void init(PlatformHelper platformHelper) {
      if (initialized) {
         LunarEventUtils.logWarn("LunarShadersMod already initialized!");
      } else {
         platform = platformHelper;
         LunarEventUtils.logInfo("===========================================");
         LunarEventUtils.logInfo("{} v{} initializing...", "Enhanced Celestials Shader Support", "2.1.0");
         LunarEventUtils.logInfo("Platform: {} on MC {}", platform.getPlatformName(), platform.getMinecraftVersion());
         LunarEventUtils.logInfo("===========================================");
         boolean ecLoaded = platform.isModLoaded("enhancedcelestials2core");
         LunarEventUtils.logInfo("Enhanced Celestials: {}", ecLoaded ? "FOUND" : "NOT FOUND");
         initialized = true;
         LunarEventUtils.logInfo("{} initialization complete!", "Enhanced Celestials Shader Support");
      }
   }

   public static PlatformHelper getPlatform() {
      return platform;
   }

   public static boolean isInitialized() {
      return initialized;
   }
}
