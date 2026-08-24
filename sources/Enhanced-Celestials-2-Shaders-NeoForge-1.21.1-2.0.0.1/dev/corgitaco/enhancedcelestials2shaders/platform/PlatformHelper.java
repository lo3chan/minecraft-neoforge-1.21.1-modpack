package dev.corgitaco.enhancedcelestials2shaders.platform;

import net.minecraft.world.level.Level;

public interface PlatformHelper {
   PlatformHelper INSTANCE = null;

   String getPlatformName();

   String getMinecraftVersion();

   boolean isModLoaded(String var1);

   boolean isClient();

   boolean isDedicatedServer();

   String getGameDirectory();

   String getConfigDirectory();

   boolean areShadersActive();

   Level getClientLevel();
}
