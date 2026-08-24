package pl.skidam.automodpack_loader_core.loader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.language.IModInfo.DependencyType;
import net.neoforged.neoforgespi.language.IModInfo.ModVersion;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.loader.LoaderManagerService;
import pl.skidam.automodpack_core.utils.ClientCacheUtils;
import pl.skidam.automodpack_core.utils.FileInspection;

public class LoaderManager implements LoaderManagerService {
   private Collection<FileInspection.Mod> modList = new ArrayList<>();
   private int lastLoadingModListSize = -1;

   @Override
   public LoaderManagerService.ModPlatform getPlatformType() {
      return LoaderManagerService.ModPlatform.NEOFORGE;
   }

   @Override
   public boolean isModLoaded(String modId) {
      LoadingModList loadingModList;
      try {
         loadingModList = FMLLoader.getLoadingModList();
      } catch (IllegalStateException var4) {
         return false;
      }

      return loadingModList.getModFileById(modId) != null;
   }

   @Override
   public Collection<FileInspection.Mod> getModList() {
      if (GlobalVariables.preload) {
         return this.modList;
      } else {
         List<ModInfo> modInfo = FMLLoader.getLoadingModList().getMods();
         if (!this.modList.isEmpty() && this.lastLoadingModListSize == modInfo.size()) {
            return this.modList;
         } else {
            this.lastLoadingModListSize = modInfo.size();
            Collection<FileInspection.Mod> modList = new ArrayList<>();

            for (ModInfo info : modInfo) {
               try {
                  String modID = info.getModId();
                  Path path = this.getModPath(modID);
                  if (path != null && !path.toString().isEmpty() && Files.exists(path)) {
                     String hash = ClientCacheUtils.computeHashIfNeeded(path);
                     if (hash != null) {
                        List<String> dependencies = info.getDependencies()
                           .stream()
                           .filter(d -> d.getType() == DependencyType.REQUIRED)
                           .<String>map(ModVersion::getModId)
                           .toList();
                        FileInspection.Mod mod = new FileInspection.Mod(
                           modID, hash, List.of(), info.getOwningFile().versionString(), path, LoaderManagerService.EnvironmentType.UNIVERSAL, dependencies
                        );
                        modList.add(mod);
                     }
                  }
               } catch (Exception var10) {
               }
            }

            return this.modList = modList;
         }
      }
   }

   @Override
   public String getLoaderVersion() {
      return FMLLoader.versionInfo().neoForgeVersion();
   }

   private Path getModPath(String modId) {
      if (this.isDevelopmentEnvironment()) {
         return null;
      } else {
         if (this.isModLoaded(modId)) {
            ModFileInfo modInfo = FMLLoader.getLoadingModList().getModFileById(modId);
            List<IModInfo> mods = modInfo.getMods();
            if (!mods.isEmpty()) {
               return ((IModInfo)mods.getFirst()).getOwningFile().getFile().getFilePath().toAbsolutePath();
            }
         }

         return null;
      }
   }

   @Override
   public LoaderManagerService.EnvironmentType getEnvironmentType() {
      return FMLLoader.getDist() == Dist.CLIENT ? LoaderManagerService.EnvironmentType.CLIENT : LoaderManagerService.EnvironmentType.SERVER;
   }

   @Override
   public String getModVersion(String modId) {
      if (GlobalVariables.preload) {
         return modId.equals("minecraft") ? FMLLoader.versionInfo().mcVersion() : null;
      } else {
         ModInfo modInfo = FMLLoader.getLoadingModList().getMods().stream().filter(mod -> mod.getModId().equals(modId)).findFirst().orElse(null);
         return modInfo == null ? null : modInfo.getVersion().toString();
      }
   }

   @Override
   public boolean isDevelopmentEnvironment() {
      return !FMLLoader.isProduction();
   }
}
