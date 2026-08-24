package de.cristelknight.cristellib.util;

import de.cristelknight.cristellib.Constants;
import de.cristelknight.cristellib.CristelLibRegistry;
import de.cristelknight.cristellib.ModLoadingUtil;
import de.cristelknight.cristellib.PlatformHelper;
import de.cristelknight.cristellib.StructureConfig;
import de.cristelknight.cristellib.autoconfig.ACConfig;
import de.cristelknight.cristellib.autoconfig.ACInfoData;
import de.cristelknight.cristellib.autoconfig.ModFinder;
import de.cristelknight.cristellib.config.ConfigManager;
import de.cristelknight.cristellib.data.ReadData;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;

public class Util {
   public static final String CLOTH_ID = PlatformHelper.getPlatform().equals(Platform.FABRIC) ? "cloth-config" : "cloth_config";
   private static final Set<String> SKIP_MODS = Set.of("neoforge", "java", "cristellib", "modmenu", CLOTH_ID, "cloth-basic-math");

   public static boolean isClothConfigLoaded() {
      return ModLoadingUtil.isModLoaded(CLOTH_ID);
   }

   public static <V> V getFirst(Collection<V> collection) {
      Iterator<V> it = collection.iterator();
      return it.hasNext() ? it.next() : null;
   }

   public static <T extends Comparable<T>> List<T> sortedKeyList(Map<T, ?> map) {
      return map.keySet().stream().sorted().toList();
   }

   public static void readData(Map<String, Set<StructureConfig>> configs, CristelLibRegistry registry) {
      updateOldFiles();
      Map<String, Set<String>> modIdAndSets = new HashMap<>();
      Map<String, ACInfoData> autoConfigInfoData = new HashMap<>();

      for (String modId : ModLoadingUtil.getModIds()) {
         if (!SKIP_MODS.contains(modId)) {
            ReadData.readData(modId, autoConfigInfoData, configs, modIdAndSets);
         }
      }

      ACInfoData.currentData = autoConfigInfoData;
      ACConfig.update();

      for (String modIdx : modIdAndSets.keySet()) {
         if (!ModFinder.shouldSkipModForACAfter(modIdx, configs.keySet())) {
            ModFinder.addAutoConfigs(modIdx, modIdAndSets.get(modIdx), configs, registry);
         }
      }
   }

   private static void updateOldFiles() {
      try {
         Path oldPath = ConfigManager.CONFIG_LIB.resolve("data");
         updateDirectories("structure_config", oldPath);
         updateDirectories("data_pack", oldPath);
         updateDirectories("copy_file", oldPath);
         if (Files.exists(oldPath)) {
            Files.move(oldPath, ConfigManager.CONFIG_LIB.resolve("~OUTDATED DIRECTORY~ data"));
         }
      } catch (IOException var1) {
         Constants.LOG.error("Failed to update custom configs in <instance>/config/cristellib/data/", var1);
      }
   }

   private static void updateDirectories(String subPath, Path oldPath) throws IOException {
      Path oldSubPath = oldPath.resolve(subPath);
      Path oldOldSubPath = oldPath.resolve(subPath + "s");
      if (Files.exists(oldSubPath)) {
         FileUtils.copyDirectory(oldSubPath.toFile(), ConfigManager.CONFIG_LIB.resolve(subPath).toFile());
      } else if (Files.exists(oldOldSubPath)) {
         FileUtils.copyDirectory(oldOldSubPath.toFile(), ConfigManager.CONFIG_LIB.resolve(subPath).toFile());
      }
   }
}
