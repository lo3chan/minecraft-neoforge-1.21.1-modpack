package com.anthonyhilyard.iceberg.neoforge.services;

import com.anthonyhilyard.iceberg.services.IPlatformHelper;
import java.util.List;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class NeoForgePlatformHelper implements IPlatformHelper {
   private List<String> cachedModList = null;

   @Override
   public String getPlatformName() {
      return "NeoForge";
   }

   @Override
   public boolean isModLoaded(String modId) {
      if (modId == null || modId.isEmpty()) {
         return false;
      } else {
         return ModList.get() != null ? ModList.get().isLoaded(modId) : LoadingModList.get().getModFileById(modId) != null;
      }
   }

   @Override
   public List<String> getAllModIds() {
      if (this.cachedModList == null) {
         if (ModList.get() != null) {
            this.cachedModList = ModList.get().applyForEachModContainer(mod -> mod.getModId()).toList();
         } else {
            this.cachedModList = LoadingModList.get().getMods().stream().<String>map(ModInfo::getModId).toList();
         }
      }

      return this.cachedModList;
   }

   @Override
   public boolean modVersionMeets(String modId, String versionString) {
      if (!versionString.contains("<") && !versionString.contains(">") && !versionString.contains("=") && !versionString.contains("~")) {
         boolean result = false;
         if (this.isModLoaded(modId)) {
            try {
               result = ((ModContainer)ModList.get().getModContainerById(modId).get())
                     .getModInfo()
                     .getVersion()
                     .compareTo(new DefaultArtifactVersion(versionString))
                  >= 0;
            } catch (Exception var5) {
            }
         }

         return result;
      } else {
         return false;
      }
   }
}
