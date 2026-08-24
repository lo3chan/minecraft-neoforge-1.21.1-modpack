package de.cristelknight.cristellib.config.structure.toggle;

import de.cristelknight.cristellib.StructureConfigToggle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;

public class ToggleConfigTransformer {
   public static Map<String, Boolean> stringBooleanMap(NestedToggleConfig toggleConfig, String parent) {
      Map<String, Boolean> map = new HashMap<>();

      for (Entry<String, NestedToggleConfig.Entry> entry : toggleConfig.entries().entrySet()) {
         String key = entry.getKey();
         NestedToggleConfig.Entry object = entry.getValue();
         String finalKey = parent.isEmpty() ? key : parent + "/" + key;
         if (object.isBoolean()) {
            map.put(finalKey, object.value());
         } else {
            map.putAll(stringBooleanMap(object.nested(), finalKey));
         }
      }

      return map;
   }

   public static Map<String, NestedToggleConfig> mapToNestedStructures(StructureConfigToggle structureConfig) {
      Map<ResourceLocation, List<ResourceLocation>> defaultStructures = structureConfig.getDefaultStructureToggles();
      Map<String, NestedToggleConfig> nestedStructures = new HashMap<>();

      for (Entry<ResourceLocation, List<ResourceLocation>> mapEntry : defaultStructures.entrySet()) {
         ResourceLocation location = mapEntry.getKey();
         List<ResourceLocation> stringList = mapEntry.getValue();
         Map<String, NestedToggleConfig.Entry> entries = new HashMap<>();

         for (ResourceLocation structure : stringList) {
            String structureName = structureConfig.toDefaultString(structure);
            putStructureName(structureName, true, entries);
         }

         NestedToggleConfig nestedStructure = new NestedToggleConfig(entries);
         nestedStructures.put(structureConfig.toDefaultString(location), nestedStructure);
      }

      return nestedStructures;
   }

   public static Map<String, NestedToggleConfig> mapToNestedStructuresWithValues(StructureConfigToggle structureConfig) {
      Map<ResourceLocation, ToggleConfig> sets = structureConfig.getToggleConfigs();
      Map<String, NestedToggleConfig> nestedStructures = new HashMap<>();

      for (Entry<ResourceLocation, ToggleConfig> mapEntry : sets.entrySet()) {
         ResourceLocation location = mapEntry.getKey();
         ToggleConfig stringList = mapEntry.getValue();
         Map<String, NestedToggleConfig.Entry> entries = new HashMap<>();

         for (Entry<String, Boolean> entry : stringList.setStructureInfo().entrySet()) {
            String structure = entry.getKey();
            boolean value = entry.getValue();
            putStructureName(structure, value, entries);
         }

         NestedToggleConfig nestedStructure = new NestedToggleConfig(entries);
         nestedStructures.put(structureConfig.toDefaultString(location), nestedStructure);
      }

      return nestedStructures;
   }

   public static void putStructureName(String structureName, boolean value, Map<String, NestedToggleConfig.Entry> entries) {
      if (structureName.contains("/")) {
         String[] parts = structureName.split("/", 2);
         String key = parts[0];
         String restOfStructureName = parts[1];
         boolean containsNestedStructure = entries.containsKey(key);
         Map<String, NestedToggleConfig.Entry> nestedEntries;
         if (containsNestedStructure) {
            nestedEntries = entries.get(key).nested().entries();
         } else {
            nestedEntries = new HashMap<>();
         }

         putStructureName(restOfStructureName, value, nestedEntries);
         if (!containsNestedStructure) {
            entries.put(key, NestedToggleConfig.Entry.ofNested(new NestedToggleConfig(nestedEntries)));
         }
      } else {
         entries.put(structureName, NestedToggleConfig.Entry.ofBoolean(value));
      }
   }
}
