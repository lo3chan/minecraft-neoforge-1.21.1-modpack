package de.cristelknight.cristellib.config;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import com.mojang.serialization.Codec;
import de.cristelknight.cristellib.Constants;
import de.cristelknight.cristellib.PlatformHelper;
import de.cristelknight.cristellib.StructureConfig;
import de.cristelknight.cristellib.StructureConfigPlacement;
import de.cristelknight.cristellib.StructureConfigToggle;
import de.cristelknight.cristellib.config.simple.ConfigRegistry;
import de.cristelknight.cristellib.config.simple.datafixer.DataFixer;
import de.cristelknight.cristellib.config.structure.placement.PlacementConfig;
import de.cristelknight.cristellib.config.structure.toggle.NestedToggleConfig;
import de.cristelknight.cristellib.config.structure.toggle.ToggleConfig;
import de.cristelknight.cristellib.config.structure.toggle.ToggleConfigTransformer;
import de.cristelknight.cristellib.util.jankson.JanksonOps;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;

public class ConfigManager {
   public static final Path CONFIG_DIR = PlatformHelper.getConfigDirectory();
   public static final Path CONFIG_LIB = CONFIG_DIR.resolve("cristellib");

   public static void createToggleConfig(StructureConfigToggle config) {
      Map<String, NestedToggleConfig> nestedStructureMap;
      if (config.getToggleConfigs() == null) {
         nestedStructureMap = ToggleConfigTransformer.mapToNestedStructures(config);
      } else {
         nestedStructureMap = ToggleConfigTransformer.mapToNestedStructuresWithValues(config);
      }

      writeConfig(config, NestedToggleConfig.TOGGLE_CODEC, nestedStructureMap);
   }

   public static Map<ResourceLocation, ToggleConfig> readToggleConfig(StructureConfigToggle config) {
      Map<String, NestedToggleConfig> externalMap = FileWriter.readFromJanksonPath(config.getPath(), NestedToggleConfig.TOGGLE_CODEC);
      return externalMap.entrySet()
         .stream()
         .collect(Collectors.toMap(entry -> config.toDefaultId(entry.getKey()), entry -> new ToggleConfig(entry.getValue())));
   }

   public static void createPlacementConfig(StructureConfigPlacement config) {
      Map<ResourceLocation, PlacementConfig> internalMap = config.getPlacementConfigs() == null
         ? config.getDefaultStructurePlacements()
         : config.getPlacementConfigs();
      Map<String, PlacementConfig> externalMap = internalMap.entrySet()
         .stream()
         .collect(Collectors.toMap(entry -> config.toDefaultString(entry.getKey()), Entry::getValue));
      writeConfig(config, PlacementConfig.PLACEMENT_CODEC, externalMap);
   }

   public static Map<ResourceLocation, PlacementConfig> readPlacementConfig(StructureConfigPlacement config) {
      Map<String, PlacementConfig> externalMap = FileWriter.readFromJanksonPath(config.getPath(), PlacementConfig.PLACEMENT_CODEC);
      return externalMap.entrySet().stream().collect(Collectors.toMap(entry -> config.toDefaultId(entry.getKey()), Entry::getValue));
   }

   public static <T> void writeConfig(StructureConfig config, Codec<T> codec, T from) {
      FileWriter.writeToFile(config.getPath(), codec, config.getComments(), from, createHeader(config.getHeader()), true);
   }

   public static <T> T readFromJanksonPathWithFix(Path path, Codec<T> codec, Consumer<T> writeAfterFix) {
      JsonElement load;
      try {
         load = FileWriter.JANKSON.load(path.toFile());
      } catch (Exception var6) {
         throw new IllegalArgumentException(
            Constants.getWithPrefix(String.format("Couldn't load %s, crashing instead. Maybe try to delete the config files!", path))
         );
      }

      boolean gotFixed = load instanceof JsonObject object && DataFixer.appliedFixer(ConfigRegistry.getClazzFromCodec(codec), object);
      T config = FileWriter.loadFromElement(
         String.format("Couldn't read %s, crashing instead. Maybe try to delete the config files!", path), codec, JanksonOps.INSTANCE, load
      );
      if (gotFixed) {
         writeAfterFix.accept(config);
      }

      return config;
   }

   public static String createHeader(String header) {
      if (header != null && !header.isEmpty()) {
         if (!header.endsWith("\n")) {
            header = header + "\n";
         }

         return "/*\n" + header + "*/\n";
      } else {
         return "";
      }
   }
}
