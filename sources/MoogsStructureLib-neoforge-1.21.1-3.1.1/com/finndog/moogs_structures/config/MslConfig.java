package com.finndog.moogs_structures.config;

import com.finndog.moogs_structures.MoogsStructuresCommon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;

public final class MslConfig {
   private static final String FILE_NAME = "moogs_structures.json";
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
   private static final MslConfig INSTANCE = new MslConfig();
   private Map<String, Map<String, Boolean>> presets = new TreeMap<>();
   private double universalSpacing = 1.0;
   private Map<String, Double> perModSpacing = new TreeMap<>();
   private Map<String, Double> perStructureSpacing = new TreeMap<>();
   private Set<String> disabledStructures = new TreeSet<>();
   private Set<String> hiddenButtons = new TreeSet<>();
   private volatile Set<ResourceLocation> disabledSnapshot = Set.of();
   private int generation = 0;
   private Path file;

   public static MslConfig get() {
      return INSTANCE;
   }

   private MslConfig() {
   }

   public synchronized void loadAndSync(Path configDir, Map<String, Map<String, Boolean>> discoveredPresets) {
      this.file = configDir.resolve("moogs_structures.json");
      MslConfig.Stored stored = readStored(this.file);
      Map<String, Map<String, Boolean>> mergedPresets = new TreeMap<>();

      for (Entry<String, Map<String, Boolean>> mod : discoveredPresets.entrySet()) {
         Map<String, Boolean> storedForMod = stored.presets.getOrDefault(mod.getKey(), Map.of());
         Map<String, Boolean> out = new TreeMap<>();

         for (Entry<String, Boolean> preset : mod.getValue().entrySet()) {
            out.put(preset.getKey(), storedForMod.getOrDefault(preset.getKey(), preset.getValue()));
         }

         if (!out.isEmpty()) {
            mergedPresets.put(mod.getKey(), out);
         }
      }

      this.presets = mergedPresets;
      this.universalSpacing = stored.universalSpacing;
      this.perModSpacing = stored.perModSpacing;
      this.perStructureSpacing = stored.perStructureSpacing;
      this.disabledStructures = stored.disabledStructures;
      this.hiddenButtons = stored.hiddenButtons;
      this.disabledSnapshot = buildSnapshot(stored.disabledStructures);
      this.generation++;
      this.writeFile();
   }

   private static Set<ResourceLocation> buildSnapshot(Set<String> ids) {
      Set<ResourceLocation> out = new HashSet<>();

      for (String id : ids) {
         ResourceLocation rl = ResourceLocation.tryParse(id);
         if (rl != null) {
            out.add(rl);
         }
      }

      return Set.copyOf(out);
   }

   public boolean presetEnabled(String modid, String presetId, boolean defaultValue) {
      Map<String, Boolean> forMod = this.presets.get(modid);
      return forMod == null ? defaultValue : forMod.getOrDefault(presetId, defaultValue);
   }

   public synchronized void setAndSave(String modid, String presetId, boolean value) {
      if (this.file != null) {
         this.presets.computeIfAbsent(modid, k -> new TreeMap<>()).put(presetId, value);
         this.writeFile();
      }
   }

   public int spacingGeneration() {
      return this.generation;
   }

   public double getUniversalSpacingMultiplier() {
      return this.universalSpacing;
   }

   public double getModSpacingMultiplier(String modid) {
      return this.perModSpacing.getOrDefault(modid, 1.0);
   }

   public double getStructureSpacingMultiplier(String structureSetId) {
      return this.perStructureSpacing.getOrDefault(structureSetId, 1.0);
   }

   public double getEffectiveSpacingMultiplier(String structureSetId) {
      double m = this.universalSpacing;
      if (structureSetId != null) {
         int colon = structureSetId.indexOf(58);
         String namespace = colon > 0 ? structureSetId.substring(0, colon) : structureSetId;
         m *= this.perModSpacing.getOrDefault(namespace, 1.0);
         m *= this.perStructureSpacing.getOrDefault(structureSetId, 1.0);
      }

      return m;
   }

   public synchronized void setUniversalSpacingAndSave(double value) {
      if (this.file != null) {
         this.universalSpacing = value;
         this.writeFile();
      }
   }

   public synchronized void setModSpacingAndSave(String modid, double value) {
      if (this.file != null) {
         this.perModSpacing.put(modid, value);
         this.writeFile();
      }
   }

   public synchronized void setStructureSpacingAndSave(String structureSetId, double value) {
      if (this.file != null) {
         this.perStructureSpacing.put(structureSetId, value);
         this.writeFile();
      }
   }

   public boolean isStructureDisabled(ResourceLocation structureId) {
      return this.disabledSnapshot.contains(structureId);
   }

   public boolean hasAnyDisabled() {
      return !this.disabledSnapshot.isEmpty();
   }

   public boolean isDisabledForScreen(String structureId) {
      return this.disabledStructures.contains(structureId);
   }

   public synchronized void setStructureDisabledAndSave(String structureId, boolean disabled) {
      if (this.file != null) {
         if (disabled) {
            this.disabledStructures.add(structureId);
         } else {
            this.disabledStructures.remove(structureId);
         }

         this.writeFile();
      }
   }

   public boolean isButtonHidden(String buttonId) {
      return this.hiddenButtons.contains(buttonId);
   }

   public synchronized void setButtonHiddenAndSave(String buttonId, boolean hidden) {
      if (this.file != null) {
         if (hidden) {
            this.hiddenButtons.add(buttonId);
         } else {
            this.hiddenButtons.remove(buttonId);
         }

         this.writeFile();
      }
   }

   private static MslConfig.Stored readStored(Path file) {
      Map<String, Map<String, Boolean>> presets = new HashMap<>();
      double universal = 1.0;
      Map<String, Double> perMod = new TreeMap<>();
      Map<String, Double> perStructure = new TreeMap<>();
      Set<String> disabled = new TreeSet<>();
      Set<String> hiddenButtons = new TreeSet<>();
      if (Files.exists(file)) {
         try (Reader r = Files.newBufferedReader(file)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            if (root.has("disabled_structures") && root.get("disabled_structures").isJsonArray()) {
               JsonArray arr = root.getAsJsonArray("disabled_structures");

               for (int i = 0; i < arr.size(); i++) {
                  if (arr.get(i).isJsonPrimitive()) {
                     disabled.add(arr.get(i).getAsString());
                  }
               }
            }

            if (root.has("hidden_buttons") && root.get("hidden_buttons").isJsonArray()) {
               JsonArray arr = root.getAsJsonArray("hidden_buttons");

               for (int ix = 0; ix < arr.size(); ix++) {
                  if (arr.get(ix).isJsonPrimitive()) {
                     hiddenButtons.add(arr.get(ix).getAsString());
                  }
               }
            }

            if (root.has("presets") && root.get("presets").isJsonObject()) {
               JsonObject presetsObj = root.getAsJsonObject("presets");

               for (String modid : presetsObj.keySet()) {
                  if (presetsObj.get(modid).isJsonObject()) {
                     JsonObject byPreset = presetsObj.getAsJsonObject(modid);
                     Map<String, Boolean> forMod = new HashMap<>();

                     for (String presetId : byPreset.keySet()) {
                        if (byPreset.get(presetId).isJsonPrimitive() && byPreset.get(presetId).getAsJsonPrimitive().isBoolean()) {
                           forMod.put(presetId, byPreset.get(presetId).getAsBoolean());
                        }
                     }

                     presets.put(modid, forMod);
                  }
               }
            }

            if (root.has("spacing") && root.get("spacing").isJsonObject()) {
               JsonObject spacing = root.getAsJsonObject("spacing");
               if (spacing.has("universal_multiplier")) {
                  universal = spacing.get("universal_multiplier").getAsDouble();
               }

               readMultiplierMap(spacing, "per_mod", perMod);
               readMultiplierMap(spacing, "per_structure", perStructure);
            }
         } catch (RuntimeException | IOException var19) {
            MoogsStructuresCommon.LOGGER
               .warn("Moogs Structures: failed to read {} - defaults will be used ({}: {})", file, var19.getClass().getSimpleName(), var19.getMessage());
         }
      }

      return new MslConfig.Stored(presets, universal, perMod, perStructure, disabled, hiddenButtons);
   }

   private static void readMultiplierMap(JsonObject parent, String key, Map<String, Double> out) {
      if (parent.has(key) && parent.get(key).isJsonObject()) {
         JsonObject obj = parent.getAsJsonObject(key);

         for (String k : obj.keySet()) {
            if (obj.get(k).isJsonPrimitive() && obj.get(k).getAsJsonPrimitive().isNumber()) {
               out.put(k, obj.get(k).getAsDouble());
            }
         }
      }
   }

   private synchronized void writeFile() {
      try {
         Files.createDirectories(this.file.getParent());
         JsonObject root = new JsonObject();
         root.addProperty(
            "_comment",
            "Presets replace vanilla structures with Moogs ones. Spacing multipliers make structures rarer (higher) or denser (lower); effective = universal x per_mod x per_structure. Changes apply on world reload and only affect newly generated chunks."
         );
         JsonObject presetsObj = new JsonObject();

         for (Entry<String, Map<String, Boolean>> mod : this.presets.entrySet()) {
            JsonObject byPreset = new JsonObject();

            for (Entry<String, Boolean> preset : mod.getValue().entrySet()) {
               byPreset.addProperty(preset.getKey(), preset.getValue());
            }

            presetsObj.add(mod.getKey(), byPreset);
         }

         root.add("presets", presetsObj);
         JsonObject spacing = new JsonObject();
         spacing.addProperty("universal_multiplier", this.universalSpacing);
         JsonObject perMod = new JsonObject();
         this.perModSpacing.forEach(perMod::addProperty);
         spacing.add("per_mod", perMod);
         JsonObject perStructure = new JsonObject();
         this.perStructureSpacing.forEach(perStructure::addProperty);
         spacing.add("per_structure", perStructure);
         root.add("spacing", spacing);
         JsonArray disabled = new JsonArray();
         this.disabledStructures.forEach(disabled::add);
         root.add("disabled_structures", disabled);
         JsonArray hidden = new JsonArray();
         this.hiddenButtons.forEach(hidden::add);
         root.add("hidden_buttons", hidden);

         try (Writer w = Files.newBufferedWriter(this.file)) {
            GSON.toJson(root, w);
         }
      } catch (RuntimeException | IOException var13) {
         MoogsStructuresCommon.LOGGER.warn("Moogs Structures: failed to write {} ({}: {})", this.file, var13.getClass().getSimpleName(), var13.getMessage());
      }
   }

   private record Stored(
      Map<String, Map<String, Boolean>> presets,
      double universalSpacing,
      Map<String, Double> perModSpacing,
      Map<String, Double> perStructureSpacing,
      Set<String> disabledStructures,
      Set<String> hiddenButtons
   ) {
   }
}
