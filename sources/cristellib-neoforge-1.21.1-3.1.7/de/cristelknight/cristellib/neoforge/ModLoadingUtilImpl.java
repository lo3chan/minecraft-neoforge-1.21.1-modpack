package de.cristelknight.cristellib.neoforge;

import de.cristelknight.cristellib.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class ModLoadingUtilImpl {
   public static List<String> getModIds() {
      ModList modList = ModList.get();
      List<String> modIds = new ArrayList<>();
      if (modList != null) {
         for (IModInfo modInfo : modList.getMods()) {
            modIds.add(modInfo.getModId());
         }
      } else {
         for (IModInfo modInfo : FMLLoader.getLoadingModList().getMods()) {
            modIds.add(modInfo.getModId());
         }
      }

      return modIds;
   }

   public static boolean isModLoaded(String modId) {
      ModList modList = ModList.get();
      return modList != null ? modList.isLoaded(modId) : isModPreLoaded(modId);
   }

   public static Optional<Integer> compare(String modId, String version) {
      if (isModLoaded(modId)) {
         ModList modList = ModList.get();
         ArtifactVersion modVersion;
         if (modList != null) {
            modVersion = ((ModContainer)modList.getModContainerById(modId).get()).getModInfo().getVersion();
         } else {
            modVersion = getPreLoadedModVersion(modId);
         }

         ArtifactVersion min = new DefaultArtifactVersion(version);
         return Optional.of(modVersion.compareTo(min));
      } else {
         return Optional.empty();
      }
   }

   private static boolean isModPreLoaded(String modId) {
      return getPreLoadedModInfo(modId) != null;
   }

   @Nullable
   public static ModInfo getPreLoadedModInfo(String modId) {
      for (ModInfo info : FMLLoader.getLoadingModList().getMods()) {
         if (info.getModId().equals(modId)) {
            return info;
         }
      }

      return null;
   }

   private static ArtifactVersion getPreLoadedModVersion(String modId) {
      ModInfo info = getPreLoadedModInfo(modId);
      if (info == null) {
         throw new RuntimeException(Constants.getWithPrefix("Couldn't find mod: " + modId));
      } else {
         return info.getVersion();
      }
   }
}
