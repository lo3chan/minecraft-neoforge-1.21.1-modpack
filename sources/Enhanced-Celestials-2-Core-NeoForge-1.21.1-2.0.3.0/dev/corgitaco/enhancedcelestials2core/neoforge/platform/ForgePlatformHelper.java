package dev.corgitaco.enhancedcelestials2core.neoforge.platform;

import com.google.auto.service.AutoService;
import dev.corgitaco.enhancedcelestials2core.platform.services.IPlatformHelper;
import java.nio.file.Path;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

@AutoService({IPlatformHelper.class})
public class ForgePlatformHelper implements IPlatformHelper {
   @Override
   public String getPlatformName() {
      return "NeoForge";
   }

   @Override
   public boolean isModLoaded(String modId) {
      return ModList.get().isLoaded(modId);
   }

   @Override
   public String modName(String modId) {
      return ModList.get().getModContainerById(modId).map(container -> container.getModInfo().getDisplayName()).orElse(null);
   }

   @Override
   public String getModVersion(String modId) {
      return ModList.get().getModContainerById(modId).map(container -> container.getModInfo().getVersion().toString()).orElse(null);
   }

   @Override
   public boolean isDevelopmentEnvironment() {
      return !FMLLoader.isProduction();
   }

   @Override
   public Path configDir() {
      return FMLPaths.CONFIGDIR.get();
   }
}
