package net.raphimc.immediatelyfast.neoforge;

import java.nio.file.Path;
import java.util.Optional;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import net.raphimc.immediatelyfast.service.PlatformService;

public class NeoForgePlatformService implements PlatformService {
   @Override
   public Path getConfigDirectory() {
      return FMLPaths.CONFIGDIR.get();
   }

   @Override
   public Optional<String> getModVersion(String id) {
      return ModList.get() != null
         ? ModList.get().getModContainerById(id).map(m -> m.getModInfo().getVersion().toString())
         : Optional.ofNullable(FMLLoader.getLoadingModList().getModFileById(id)).map(ModFileInfo::versionString);
   }
}
