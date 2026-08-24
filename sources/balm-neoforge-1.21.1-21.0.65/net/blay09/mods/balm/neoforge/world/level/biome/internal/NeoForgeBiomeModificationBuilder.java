package net.blay09.mods.balm.neoforge.world.level.biome.internal;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.world.level.biome.BiomeModificationBuilder;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record NeoForgeBiomeModificationBuilder(Builder builder) implements BiomeModificationBuilder {
   private static final Logger logger = LoggerFactory.getLogger(NeoForgeBiomeModificationBuilder.class);

   @Override
   public void addFeature(Decoration step, ResourceKey<PlacedFeature> placedFeature) {
      MinecraftServer server = Balm.getHooks().getServer();
      if (server != null) {
         RegistryLookup<PlacedFeature> placedFeatures = server.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE);
         this.builder.getGenerationSettings().addFeature(step, placedFeatures.getOrThrow(placedFeature));
      } else {
         logger.error("Failed to add feature {} to biome, no server is available", placedFeature);
      }
   }

   @Override
   public void addSpawn(MobCategory spawnGroup, SpawnerData spawnEntry) {
      this.builder.getMobSpawnSettings().addSpawn(spawnGroup, spawnEntry);
   }

   @Override
   public void setSpawnCost(EntityType<?> entityType, double mass, double gravityLimit) {
      this.builder.getMobSpawnSettings().addMobCharge(entityType, mass, gravityLimit);
   }
}
