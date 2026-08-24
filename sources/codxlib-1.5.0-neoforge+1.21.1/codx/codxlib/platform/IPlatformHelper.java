package codx.codxlib.platform;

import codx.codxlib.api.Environment;
import codx.codxlib.api.LoadedMod;
import java.nio.file.Path;
import java.util.List;

public interface IPlatformHelper {
   String getModVersion(String var1);

   String getMinecraftVersion();

   String getLoaderName();

   Path getConfigDir();

   Path getGameDir();

   boolean isModLoaded(String var1);

   List<LoadedMod> getLoadedMods();

   Environment getEnvironment();

   void registerBuiltinResourcePacks();
}
