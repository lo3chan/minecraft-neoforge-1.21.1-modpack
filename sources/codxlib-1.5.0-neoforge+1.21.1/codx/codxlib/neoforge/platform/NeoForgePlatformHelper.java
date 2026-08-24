package codx.codxlib.neoforge.platform;

import codx.codxlib.api.Environment;
import codx.codxlib.api.LoadedMod;
import codx.codxlib.platform.IPlatformHelper;
import java.nio.file.Path;
import java.util.List;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;

public class NeoForgePlatformHelper implements IPlatformHelper {
   @Override
   public String getModVersion(String modId) {
      return ModList.get().getModContainerById(modId).map(c -> c.getModInfo().getVersion().toString()).orElse("unknown");
   }

   @Override
   public String getMinecraftVersion() {
      return ModList.get().getModContainerById("minecraft").map(c -> c.getModInfo().getVersion().toString()).orElse("unknown");
   }

   @Override
   public String getLoaderName() {
      return "NeoForge";
   }

   @Override
   public Path getConfigDir() {
      return FMLPaths.CONFIGDIR.get();
   }

   @Override
   public Path getGameDir() {
      return FMLPaths.GAMEDIR.get();
   }

   @Override
   public boolean isModLoaded(String modId) {
      return ModList.get().isLoaded(modId);
   }

   @Override
   public List<LoadedMod> getLoadedMods() {
      return ModList.get().getMods().stream().map(info -> new LoadedMod(info.getModId(), info.getDisplayName(), info.getVersion().toString())).toList();
   }

   @Override
   public Environment getEnvironment() {
      return FMLEnvironment.dist.isClient() ? Environment.CLIENT : Environment.SERVER;
   }

   @Override
   public void registerBuiltinResourcePacks() {
   }
}
