package com.finndog.moogs_structures.config;

import com.finndog.moogs_structures.MoogsStructuresCommon;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import net.minecraft.SharedConstants;

public final class StructureListManager {
   private static final List<StructureListManager.ModGroup> GROUPS = new ArrayList<>();
   private static final String PLACEMENT_SPREAD = "moogs_structures:advanced_random_spread";
   private static final String PLACEMENT_RINGS = "moogs_structures:conditional_concentric_rings";
   private static String cachedMcVersion;

   private StructureListManager() {
   }

   public static void init() {
      Map<String, Map<String, String>> setsByMod = new TreeMap<>();

      for (String modid : PlatformConfig.INSTANCE.getAllModIds()) {
         Map<String, String> msl = mslSetsOnly(PlatformConfig.INSTANCE.getStructureSetJsons(modid));
         if (!msl.isEmpty()) {
            setsByMod.put(modid, msl);
         }
      }

      populate(PlatformConfig.INSTANCE.getOptionalPackManifests(), setsByMod);
   }

   public static void reload(Map<String, String> manifests, Map<String, Map<String, String>> setsByNamespace) {
      populate(manifests, setsByNamespace);
   }

   private static void populate(Map<String, String> manifests, Map<String, Map<String, String>> setsByMod) {
      GROUPS.clear();
      Map<String, JsonObject> markers = parseMarkers(manifests);
      Set<String> modids = new TreeSet<>(setsByMod.keySet());
      markers.forEach((modidx, block) -> {
         if (block.has("entries")) {
            modids.add(modidx);
         }
      });

      for (String modid : modids) {
         try {
            JsonObject marker = markers.get(modid);
            String template = marker != null ? resolveTemplate(marker) : null;
            List<StructureListManager.StructureEntry> entries = marker != null && marker.has("entries") && marker.get("entries").isJsonArray()
               ? parseExplicitEntries(marker.getAsJsonArray("entries"), template)
               : deriveEntries(setsByMod.getOrDefault(modid, Map.of()), template);
            if (!entries.isEmpty()) {
               String modName = marker != null && marker.has("mod_name")
                  ? marker.get("mod_name").getAsString()
                  : orElse(PlatformConfig.INSTANCE.getModName(modid), modid);
               GROUPS.add(new StructureListManager.ModGroup(modid, modName, entries));
            }
         } catch (RuntimeException var10) {
            MoogsStructuresCommon.LOGGER
               .warn("Moogs Structures: could not build structure list for '{}' ({}: {})", modid, var10.getClass().getSimpleName(), var10.getMessage());
         }
      }

      GROUPS.sort(Comparator.comparing(StructureListManager.ModGroup::modName));
   }

   private static Map<String, JsonObject> parseMarkers(Map<String, String> manifests) {
      Map<String, JsonObject> out = new HashMap<>();

      for (Entry<String, String> e : manifests.entrySet()) {
         try {
            JsonElement root = JsonParser.parseString(e.getValue());
            if (root.isJsonObject() && root.getAsJsonObject().has("structures") && root.getAsJsonObject().get("structures").isJsonObject()) {
               out.put(e.getKey(), root.getAsJsonObject().getAsJsonObject("structures"));
            }
         } catch (RuntimeException var5) {
         }
      }

      return out;
   }

   private static Map<String, String> mslSetsOnly(Map<String, String> sets) {
      Map<String, String> out = new LinkedHashMap<>();

      for (Entry<String, String> e : sets.entrySet()) {
         if (isMslStructureSet(e.getValue())) {
            out.put(e.getKey(), e.getValue());
         }
      }

      return out;
   }

   public static boolean isMslStructureSet(String rawJson) {
      try {
         JsonObject set = JsonParser.parseString(rawJson).getAsJsonObject();
         if (set.has("placement") && set.get("placement").isJsonObject()) {
            String type = optString(set.getAsJsonObject("placement"), "type");
            return "moogs_structures:advanced_random_spread".equals(type) || "moogs_structures:conditional_concentric_rings".equals(type);
         } else {
            return false;
         }
      } catch (RuntimeException var3) {
         return false;
      }
   }

   private static String orElse(String value, String fallback) {
      return value != null ? value : fallback;
   }

   private static List<StructureListManager.StructureEntry> parseExplicitEntries(JsonArray arr, String template) {
      List<StructureListManager.StructureEntry> entries = new ArrayList<>();

      for (JsonElement el : arr) {
         if (el.isJsonObject()) {
            JsonObject obj = el.getAsJsonObject();
            if (obj.has("structure")) {
               String structureId = obj.get("structure").getAsString();
               String name = obj.has("name") ? obj.get("name").getAsString() : structureId;
               String spacingKey = obj.has("spacing_key") ? obj.get("spacing_key").getAsString() : null;
               String previewUrl = obj.has("preview_url") ? obj.get("preview_url").getAsString() : buildUrl(template, structureId);
               entries.add(new StructureListManager.StructureEntry(structureId, name, previewUrl, spacingKey));
            }
         }
      }

      return entries;
   }

   private static List<StructureListManager.StructureEntry> deriveEntries(Map<String, String> setJsons, String template) {
      List<StructureListManager.StructureEntry> entries = new ArrayList<>();

      for (Entry<String, String> e : setJsons.entrySet()) {
         String setId = e.getKey();

         try {
            JsonObject set = JsonParser.parseString(e.getValue()).getAsJsonObject();
            String type = set.has("placement") && set.get("placement").isJsonObject() ? optString(set.getAsJsonObject("placement"), "type") : null;
            List<String> structs = new ArrayList<>();
            if (set.has("structures") && set.get("structures").isJsonArray()) {
               for (JsonElement se : set.getAsJsonArray("structures")) {
                  if (se.isJsonObject() && se.getAsJsonObject().has("structure")) {
                     structs.add(se.getAsJsonObject().get("structure").getAsString());
                  }
               }
            }

            boolean spread = "moogs_structures:advanced_random_spread".equals(type);
            if (spread || "moogs_structures:conditional_concentric_rings".equals(type)) {
               boolean single = structs.size() == 1;
               String spacingKey = spread ? setId : null;
               String name = titleCase(single ? pathOf(structs.get(0)) : pathOf(setId));
               String previewUrl = single ? buildUrl(template, structs.get(0)) : null;
               entries.add(new StructureListManager.StructureEntry(setId, name, previewUrl, spacingKey));
            }
         } catch (RuntimeException var14) {
            MoogsStructuresCommon.LOGGER
               .warn(
                  "Moogs Structures: could not derive a config row from structure_set '{}' ({}: {})",
                  setId,
                  var14.getClass().getSimpleName(),
                  var14.getMessage()
               );
         }
      }

      entries.sort(Comparator.comparing(StructureListManager.StructureEntry::name));
      return entries;
   }

   private static String optString(JsonObject obj, String key) {
      return obj.has(key) && obj.get(key).isJsonPrimitive() ? obj.get(key).getAsString() : null;
   }

   private static String pathOf(String id) {
      int colon = id.indexOf(58);
      return colon >= 0 ? id.substring(colon + 1) : id;
   }

   private static String titleCase(String path) {
      int slash = path.lastIndexOf(47);
      String seg = slash >= 0 ? path.substring(slash + 1) : path;
      StringBuilder sb = new StringBuilder();

      for (String word : seg.replace('_', ' ').trim().split("\\s+")) {
         if (!word.isEmpty()) {
            if (sb.length() > 0) {
               sb.append(' ');
            }

            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
         }
      }

      return sb.toString();
   }

   private static String resolveTemplate(JsonObject structures) {
      if (structures.has("preview_url_template")) {
         return structures.get("preview_url_template").getAsString();
      } else {
         return structures.has("mod_slug") ? "https://previews.moogsmods.com/" + structures.get("mod_slug").getAsString() + "/{mc_version}/{structure}" : null;
      }
   }

   private static String buildUrl(String template, String structureId) {
      if (template == null) {
         return null;
      } else {
         int colon = structureId.indexOf(58);
         String path = colon >= 0 ? structureId.substring(colon + 1) : structureId;
         return template.replace("{structure}", path).replace("{mc_version}", mcVersion());
      }
   }

   private static String mcVersion() {
      if (cachedMcVersion == null) {
         try {
            cachedMcVersion = SharedConstants.getCurrentVersion().getName();
         } catch (Throwable var1) {
            cachedMcVersion = "";
         }
      }

      return cachedMcVersion;
   }

   public static List<StructureListManager.ModGroup> getGroups() {
      return List.copyOf(GROUPS);
   }

   public record ModGroup(String modid, String modName, List<StructureListManager.StructureEntry> structures) {
   }

   public record StructureEntry(String structureId, String name, String previewUrl, String spacingKey) {
   }
}
