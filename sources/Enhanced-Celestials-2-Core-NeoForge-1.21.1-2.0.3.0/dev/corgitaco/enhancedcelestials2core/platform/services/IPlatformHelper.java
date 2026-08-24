package dev.corgitaco.enhancedcelestials2core.platform.services;

import dev.corgitaco.enhancedcelestials2core.platform.Services;
import java.nio.file.Path;

public interface IPlatformHelper {
   IPlatformHelper PLATFORM = Services.load(IPlatformHelper.class);

   String getPlatformName();

   boolean isModLoaded(String var1);

   String modName(String var1);

   String getModVersion(String var1);

   boolean isDevelopmentEnvironment();

   Path configDir();
}
