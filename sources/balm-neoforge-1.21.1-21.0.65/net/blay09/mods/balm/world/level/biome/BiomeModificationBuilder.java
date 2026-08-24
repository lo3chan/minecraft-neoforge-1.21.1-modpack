package net.blay09.mods.balm.world.level.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public interface BiomeModificationBuilder {
   void addFeature(Decoration var1, ResourceKey<PlacedFeature> var2);

   void addSpawn(MobCategory var1, SpawnerData var2);

   void setSpawnCost(EntityType<?> var1, double var2, double var4);
}
