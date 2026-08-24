package net.mehvahdjukaar.moonlight.api.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;

public record SpawnBoxSettings(Map<MobCategory, Map<String, WeightedRandomList<SpawnerData>>> spawnOverrides) {
   public static final Codec<SpawnBoxSettings> CODEC = SpawnBoxSettings.Entry.CODEC
      .listOf()
      .flatXmap(SpawnBoxSettings::fromEntries, spawnBoxSettings -> DataResult.success(spawnBoxSettings.toEntryList()));
   public static final SpawnBoxSettings EMPTY = new SpawnBoxSettings(Map.of());

   private static DataResult<SpawnBoxSettings> fromEntries(List<SpawnBoxSettings.Entry> entries) {
      Map<MobCategory, Map<String, WeightedRandomList<SpawnerData>>> map = new HashMap<>();

      for (SpawnBoxSettings.Entry e : entries) {
         map.computeIfAbsent(e.category, k -> new HashMap<>());
         Map<String, WeightedRandomList<SpawnerData>> catMap = map.get(e.category);
         if (catMap.containsKey(e.name)) {
            return DataResult.error(() -> "Duplicate spawn box entry for category " + e.category + " and name " + e.name);
         }

         catMap.put(e.name, e.spawns);
      }

      return DataResult.success(new SpawnBoxSettings(map));
   }

   private List<SpawnBoxSettings.Entry> toEntryList() {
      return this.spawnOverrides
         .entrySet()
         .stream()
         .flatMap(
            catEntry -> catEntry.getValue()
               .entrySet()
               .stream()
               .map(nameEntry -> new SpawnBoxSettings.Entry(catEntry.getKey(), nameEntry.getKey(), nameEntry.getValue()))
         )
         .toList();
   }

   public boolean hasCategory(MobCategory category) {
      return this.spawnOverrides.containsKey(category);
   }

   public WeightedRandomList<SpawnerData> get(String boxID, MobCategory category) {
      Map<String, WeightedRandomList<SpawnerData>> map = this.spawnOverrides.get(category);
      return map != null ? map.get(boxID) : null;
   }

   public boolean isEmpty() {
      return this.spawnOverrides.isEmpty();
   }

   private record Entry(MobCategory category, String name, WeightedRandomList<SpawnerData> spawns) {
      public static final Codec<SpawnBoxSettings.Entry> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               MobCategory.CODEC.fieldOf("category").forGetter(SpawnBoxSettings.Entry::category),
               Codec.STRING.fieldOf("name").forGetter(SpawnBoxSettings.Entry::name),
               WeightedRandomList.codec(SpawnerData.CODEC).fieldOf("spawns").forGetter(SpawnBoxSettings.Entry::spawns)
            )
            .apply(instance, SpawnBoxSettings.Entry::new)
      );
   }
}
