package com.finndog.moogs_structures.config;

import com.finndog.moogs_structures.MoogsStructuresCommon;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;

public final class ReplaceVanillaManager {
   private static final Map<String, Map<String, Boolean>> PRESET_DEFAULTS = new TreeMap<>();
   private static final List<ReplaceVanillaManager.PresetInfo> PRESETS = new ArrayList<>();
   private static final Map<String, ReplaceVanillaManager.Replacement> BY_VANILLA_KEY = new HashMap<>();
   private static final Map<ResourceLocation, ReplaceVanillaManager.Replacement> BY_VANILLA_STRUCTURE = new HashMap<>();

   private ReplaceVanillaManager() {
   }

   public static void init() {
      PRESET_DEFAULTS.clear();
      PRESETS.clear();
      BY_VANILLA_KEY.clear();
      BY_VANILLA_STRUCTURE.clear();
      Map<String, String> manifests = PlatformConfig.INSTANCE.getOptionalPackManifests();

      for (Entry<String, String> entry : manifests.entrySet()) {
         try {
            parseManifest(entry.getKey(), entry.getValue());
         } catch (RuntimeException var4) {
            MoogsStructuresCommon.LOGGER
               .warn(
                  "Moogs Structures: could not parse replace_vanilla.json for '{}' ({}: {})",
                  entry.getKey(),
                  var4.getClass().getSimpleName(),
                  var4.getMessage()
               );
         }
      }

      reloadConfig();
   }

   public static void reloadConfig() {
      MslConfig.get().loadAndSync(PlatformConfig.INSTANCE.getConfigDir(), PRESET_DEFAULTS);
   }

   private static void parseManifest(String modid, String json) {
      JsonElement rootEl = JsonParser.parseString(json);
      if (rootEl.isJsonObject()) {
         JsonObject root = rootEl.getAsJsonObject();
         if (root.has("presets") && root.get("presets").isJsonArray()) {
            for (JsonElement presetEl : root.getAsJsonArray("presets")) {
               if (presetEl.isJsonObject()) {
                  JsonObject preset = presetEl.getAsJsonObject();
                  String presetId = preset.has("id") ? preset.get("id").getAsString() : null;
                  if (presetId == null) {
                     MoogsStructuresCommon.LOGGER.warn("Moogs Structures: skipping preset with no id in '{}' replace_vanilla.json", modid);
                  } else {
                     boolean defaultEnabled = preset.has("default_enabled") && preset.get("default_enabled").getAsBoolean();
                     PRESET_DEFAULTS.computeIfAbsent(modid, k -> new TreeMap<>()).put(presetId, defaultEnabled);
                     String name = preset.has("name") ? preset.get("name").getAsString() : presetId;
                     String description = preset.has("description") ? preset.get("description").getAsString() : "";
                     PRESETS.add(new ReplaceVanillaManager.PresetInfo(modid, presetId, name, description, defaultEnabled));
                     if (preset.has("replacements") && preset.get("replacements").isJsonArray()) {
                        for (JsonElement el : preset.getAsJsonArray("replacements")) {
                           if (el.isJsonObject()) {
                              JsonObject obj = el.getAsJsonObject();
                              String vanillaKey = obj.has("vanilla_key") ? obj.get("vanilla_key").getAsString() : null;
                              ResourceLocation vanillaStructure = obj.has("vanilla_structure")
                                 ? ResourceLocation.tryParse(obj.get("vanilla_structure").getAsString())
                                 : null;
                              ResourceLocation replacementStructure = obj.has("replacement_structure")
                                 ? ResourceLocation.tryParse(obj.get("replacement_structure").getAsString())
                                 : null;
                              if (vanillaKey != null && vanillaStructure != null) {
                                 ReplaceVanillaManager.Replacement r = new ReplaceVanillaManager.Replacement(
                                    modid, presetId, defaultEnabled, vanillaStructure, replacementStructure
                                 );
                                 BY_VANILLA_KEY.put(modid + "/" + vanillaKey, r);
                                 BY_VANILLA_STRUCTURE.put(vanillaStructure, r);
                              } else {
                                 MoogsStructuresCommon.LOGGER.warn("Moogs Structures: skipping malformed replacement in preset '{}' of '{}'", presetId, modid);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean isEnabled(String modid, String vanillaKey) {
      ReplaceVanillaManager.Replacement r = BY_VANILLA_KEY.get(modid + "/" + vanillaKey);
      return r != null && MslConfig.get().presetEnabled(r.modid(), r.presetId(), r.defaultEnabled());
   }

   public static boolean shouldCancelVanilla(ResourceLocation vanillaStructure) {
      return getActiveReplacement(vanillaStructure).isPresent();
   }

   public static Optional<ReplaceVanillaManager.Replacement> getActiveReplacement(ResourceLocation vanillaStructure) {
      ReplaceVanillaManager.Replacement r = BY_VANILLA_STRUCTURE.get(vanillaStructure);
      return r != null && MslConfig.get().presetEnabled(r.modid(), r.presetId(), r.defaultEnabled()) ? Optional.of(r) : Optional.empty();
   }

   public static boolean hasAnyBindings() {
      return !BY_VANILLA_STRUCTURE.isEmpty();
   }

   public static List<ReplaceVanillaManager.PresetInfo> getPresets() {
      return List.copyOf(PRESETS);
   }

   public static boolean isPresetEnabled(ReplaceVanillaManager.PresetInfo preset) {
      return MslConfig.get().presetEnabled(preset.modid(), preset.presetId(), preset.defaultEnabled());
   }

   public static void setPresetEnabled(ReplaceVanillaManager.PresetInfo preset, boolean value) {
      MslConfig.get().setAndSave(preset.modid(), preset.presetId(), value);
   }

   public record PresetInfo(String modid, String presetId, String name, String description, boolean defaultEnabled) {
   }

   public record Replacement(String modid, String presetId, boolean defaultEnabled, ResourceLocation vanillaStructure, ResourceLocation replacementStructure) {
   }
}
