package dev.architectury.hooks.level.biome;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings.MobSpawnCost;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;

public interface SpawnProperties {
   float getCreatureProbability();

   Map<MobCategory, List<SpawnerData>> getSpawners();

   Map<EntityType<?>, MobSpawnCost> getMobSpawnCosts();

   public interface Mutable extends SpawnProperties {
      SpawnProperties.Mutable setCreatureProbability(float var1);

      SpawnProperties.Mutable addSpawn(MobCategory var1, SpawnerData var2);

      boolean removeSpawns(BiPredicate<MobCategory, SpawnerData> var1);

      SpawnProperties.Mutable setSpawnCost(EntityType<?> var1, MobSpawnCost var2);

      SpawnProperties.Mutable setSpawnCost(EntityType<?> var1, double var2, double var4);

      SpawnProperties.Mutable clearSpawnCost(EntityType<?> var1);
   }
}
