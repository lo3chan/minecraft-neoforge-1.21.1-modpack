package de.cristelknight.cristellib.data;

import de.cristelknight.cristellib.Constants;
import de.cristelknight.cristellib.PlatformHelper;
import de.cristelknight.cristellib.autoconfig.ModFinder;
import de.cristelknight.cristellib.config.ConfigManager;
import de.cristelknight.cristellib.util.FileHelper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PathFinder {
   private static final Pattern STRUCTURE_SET = Pattern.compile("data/[^/]+/worldgen/structure_set/.*");

   public static PathFinder.PathFinderData getSubPathsInMod(String modId, Set<String> modsWithConfig) {
      Set<String> autoConfig = new HashSet<>();
      Set<String> structureConfig = new HashSet<>();
      Set<String> dataPack = new HashSet<>();
      Set<String> structureSets = ModFinder.shouldSkipModForACPre(modId, modsWithConfig) ? null : new HashSet<>();

      try {
         if (modId.equals("minecraft")) {
            Predicate<Path> filter = path -> Files.isRegularFile(path) && path.toString().endsWith(".json");
            walk(ConfigManager.CONFIG_LIB.resolve("structure_config"), filter, structureConfig::add);
            walk(ConfigManager.CONFIG_LIB.resolve("data_pack"), filter, dataPack::add);
         } else {
            PlatformHelper.findInModFiles(
               modId, "data", path -> path.toString().endsWith(".json"), p -> categorizePath(p, autoConfig, structureConfig, dataPack, structureSets)
            );
         }
      } catch (IOException var7) {
         throw new RuntimeException(Constants.getWithPrefix("Error while trying to walk through mod files"), var7);
      }

      return new PathFinder.PathFinderData(autoConfig, structureConfig, dataPack, structureSets == null ? Set.of() : structureSets);
   }

   private static void categorizePath(String path, Set<String> autoConfig, Set<String> structureConfig, Set<String> dataPack, Set<String> structureSets) {
      path = FileHelper.normalizeResourcePath(path);
      if (path.startsWith("data/")) {
         if (structureSets != null && STRUCTURE_SET.matcher(path).matches()) {
            structureSets.add(path);
         } else if (path.startsWith("data/cristellib/")) {
            if (path.startsWith("data/cristellib/structure_config/")) {
               structureConfig.add(path);
            } else if (path.startsWith("data/cristellib/data_pack/")) {
               dataPack.add(path);
            } else if (path.startsWith("data/cristellib/auto_config/")) {
               autoConfig.add(path);
            }
         }
      }
   }

   public static void walk(Path root, Predicate<Path> fileFilter, Consumer<String> consumer) throws IOException {
      if (root != null && Files.exists(root)) {
         try (Stream<Path> stream = Files.walk(root, 2147483647)) {
            for (Path subPath : stream::iterator) {
               if (fileFilter.test(subPath)) {
                  consumer.accept(subPath.toString());
               }
            }
         }
      }
   }

   public record PathFinderData(Set<String> autoConfig, Set<String> structureConfig, Set<String> dataPack, Set<String> structureSets) {
   }
}
