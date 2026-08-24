package dev.worldgen.lithostitched.mixin.common;

import java.util.Map;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.MobSpawnCost;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({MobSpawnSettings.class})
public interface MobSpawnSettingsAccessor {
   @Accessor("spawners")
   Map<MobCategory, WeightedRandomList<SpawnerData>> getSpawners();

   @Accessor("spawners")
   void setSpawners(Map<MobCategory, WeightedRandomList<SpawnerData>> var1);

   @Accessor("mobSpawnCosts")
   Map<EntityType<?>, MobSpawnCost> lithostitched$getSpawnCosts();

   @Accessor("mobSpawnCosts")
   void lithostitched$setSpawnCosts(Map<EntityType<?>, MobSpawnCost> var1);
}
