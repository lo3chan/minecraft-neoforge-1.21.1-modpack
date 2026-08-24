package DistantHorizons.libraries.electronwill.nightconfig.core.file;

import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.StringUtils;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class FormatDetector {
   private static final Map<String, Supplier<ConfigFormat<?>>> registry = new ConcurrentHashMap<>();

   public static void registerExtension(String fileExtension, ConfigFormat<?> format) {
      registry.put(fileExtension, () -> format);
   }

   public static void registerExtension(String fileExtension, Supplier<ConfigFormat<?>> formatSupplier) {
      registry.put(fileExtension, formatSupplier);
   }

   public static ConfigFormat<?> detect(File file) {
      return detectByName(file.getName());
   }

   public static ConfigFormat<?> detect(Path file) {
      return detectByName(file.getFileName().toString());
   }

   public static ConfigFormat<?> detectByName(String fileName) {
      List<String> splitted = StringUtils.split(fileName, '.');
      String fileExtension = splitted.get(splitted.size() - 1);
      Supplier<ConfigFormat<?>> supplier = registry.get(fileExtension);
      return supplier == null ? null : supplier.get();
   }

   private static void tryLoad(String className) {
      try {
         Class.forName(className);
      } catch (ClassNotFoundException var2) {
      }
   }

   private FormatDetector() {
   }

   static {
      tryLoad("DistantHorizons.libraries.electronwill.nightconfig.toml.TomlFormat");
      tryLoad("DistantHorizons.libraries.electronwill.nightconfig.hocon.HoconFormat");
      tryLoad("DistantHorizons.libraries.electronwill.nightconfig.json.JsonFormat");
      tryLoad("DistantHorizons.libraries.electronwill.nightconfig.yaml.YamlFormat");
   }
}
