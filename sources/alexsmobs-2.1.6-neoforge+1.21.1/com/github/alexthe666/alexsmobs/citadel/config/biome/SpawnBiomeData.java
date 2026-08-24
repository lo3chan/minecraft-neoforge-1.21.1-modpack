package com.github.alexthe666.alexsmobs.citadel.config.biome;

import com.github.alexthe666.alexsmobs.citadel.Citadel;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.Biome;

@Deprecated(
   since = "2.6.2"
)
public class SpawnBiomeData {
   private List<List<SpawnBiomeData.SpawnBiomeEntry>> biomes = new ArrayList<>();

   public SpawnBiomeData() {
   }

   private SpawnBiomeData(SpawnBiomeData.SpawnBiomeEntry[][] biomesRead) {
      this.biomes = new ArrayList<>();

      for (SpawnBiomeData.SpawnBiomeEntry[] innerArray : biomesRead) {
         for (SpawnBiomeData.SpawnBiomeEntry entry : innerArray) {
            if (entry != null && entry.type == BiomeEntryType.BIOME_TAG && entry.value != null) {
               entry.value = conventionTag(entry.value);
            }
         }

         this.biomes.add(Arrays.asList(innerArray));
      }
   }

   public SpawnBiomeData addBiomeEntry(BiomeEntryType type, boolean negate, String value, int pool) {
      if (this.biomes.isEmpty() || this.biomes.size() < pool + 1) {
         this.biomes.add(new ArrayList<>());
      }

      this.biomes.get(pool).add(new SpawnBiomeData.SpawnBiomeEntry(type, negate, value));
      return this;
   }

   public boolean matches(@Nullable Holder<Biome> biomeHolder, ResourceLocation registryName) {
      for (List<SpawnBiomeData.SpawnBiomeEntry> all : this.biomes) {
         boolean overall = true;

         for (SpawnBiomeData.SpawnBiomeEntry cond : all) {
            if (!cond.matches(biomeHolder, registryName)) {
               overall = false;
            }
         }

         if (overall) {
            return true;
         }
      }

      return false;
   }

   private static String conventionTag(String value) {
      if (value.startsWith("c:")) {
         String cPath = value.substring("c:".length());

         return switch (cPath) {
            case "is_snowy", "is_plains", "is_wasteland" -> "alexsmobs:" + cPath;
            default -> value;
         };
      } else if (!value.startsWith("forge:")) {
         return value;
      } else {
         String path = value.substring("forge:".length());

         return switch (path) {
            case "is_dense/overworld" -> "c:is_dense_vegetation/overworld";
            case "is_coniferous" -> "c:is_tree/coniferous";
            case "is_snowy", "is_plains", "is_wasteland" -> "alexsmobs:" + path;
            default -> "c:" + path;
         };
      }
   }

   public static class Deserializer implements JsonDeserializer<SpawnBiomeData>, JsonSerializer<SpawnBiomeData> {
      public SpawnBiomeData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonobject = json.getAsJsonObject();
         SpawnBiomeData.SpawnBiomeEntry[][] biomesRead = (SpawnBiomeData.SpawnBiomeEntry[][])GsonHelper.getAsObject(
            jsonobject, "biomes", new SpawnBiomeData.SpawnBiomeEntry[0][0], context, SpawnBiomeData.SpawnBiomeEntry[][].class
         );
         return new SpawnBiomeData(biomesRead);
      }

      public JsonElement serialize(SpawnBiomeData src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("biomes", context.serialize(src.biomes));
         return jsonobject;
      }
   }

   private class SpawnBiomeEntry {
      BiomeEntryType type;
      boolean negate;
      String value;

      public SpawnBiomeEntry(BiomeEntryType type, boolean remove, String value) {
         this.type = type;
         this.negate = remove;
         this.value = type == BiomeEntryType.BIOME_TAG ? SpawnBiomeData.conventionTag(value) : value;
      }

      public boolean matches(@Nullable Holder<Biome> biomeHolder, ResourceLocation registryName) {
         if (this.type.isDepreciated()) {
            Citadel.LOGGER.warn("biome config: BIOME_DICT and BIOME_CATEGORY are no longer valid in 1.19+. Please use BIOME_TAG instead.");
            return false;
         } else if (this.type == BiomeEntryType.BIOME_TAG) {
            return biomeHolder.tags().anyMatch(biomeTagKey -> biomeTagKey.location() != null && biomeTagKey.location().toString().equals(this.value))
               ? !this.negate
               : this.negate;
         } else {
            return registryName.toString().equals(this.value) ? !this.negate : this.negate;
         }
      }
   }
}
