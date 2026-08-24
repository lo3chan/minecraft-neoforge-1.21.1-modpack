package com.aetherteam.aether.data.resources.builders;

import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.data.resources.AetherMobCategory;
import com.aetherteam.aether.data.resources.registries.AetherBiomes;
import com.aetherteam.aether.data.resources.registries.AetherPlacedFeatures;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.core.HolderGetter;
import net.minecraft.sounds.Musics;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.Biome.BiomeBuilder;
import net.minecraft.world.level.biome.Biome.TemperatureModifier;
import net.minecraft.world.level.biome.BiomeGenerationSettings.Builder;
import net.minecraft.world.level.biome.BiomeSpecialEffects.GrassColorModifier;
import net.minecraft.world.level.biome.Climate.Parameter;
import net.minecraft.world.level.biome.Climate.ParameterList;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class AetherBiomeBuilders {
   public static Biome skyrootMeadowBiome(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
      return makeDefaultBiome(
         new Builder(placedFeatures, worldCarvers).addFeature(Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.SKYROOT_MEADOW_TREES_PLACEMENT)
      );
   }

   public static Biome skyrootGroveBiome(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
      return makeDefaultBiome(
         new Builder(placedFeatures, worldCarvers).addFeature(Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.SKYROOT_GROVE_TREES_PLACEMENT)
      );
   }

   public static Biome skyrootWoodlandBiome(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
      return makeDefaultBiome(
         new Builder(placedFeatures, worldCarvers).addFeature(Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.SKYROOT_WOODLAND_TREES_PLACEMENT)
      );
   }

   public static Biome skyrootForestBiome(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
      return makeDefaultBiome(
         new Builder(placedFeatures, worldCarvers).addFeature(Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.SKYROOT_FOREST_TREES_PLACEMENT)
      );
   }

   public static Biome makeDefaultBiome(Builder builder) {
      return fullDefinition(
         false,
         0.8F,
         0.0F,
         new net.minecraft.world.level.biome.BiomeSpecialEffects.Builder()
            .fogColor(9671612)
            .skyColor(12632319)
            .waterColor(4159204)
            .waterFogColor(329011)
            .grassColorOverride(11665355)
            .foliageColorOverride(11665355)
            .grassColorModifier(GrassColorModifier.NONE)
            .backgroundMusic(Musics.createGameMusic(AetherSoundEvents.MUSIC_AETHER))
            .build(),
         new net.minecraft.world.level.biome.MobSpawnSettings.Builder()
            .addMobCharge((EntityType)AetherEntityTypes.COCKATRICE.get(), 0.5, 0.15)
            .addMobCharge((EntityType)AetherEntityTypes.ZEPHYR.get(), 0.6, 0.16)
            .addMobCharge((EntityType)AetherEntityTypes.AECHOR_PLANT.get(), 0.4, 0.11)
            .addMobCharge((EntityType)AetherEntityTypes.BLUE_SWET.get(), 0.5, 0.1)
            .addMobCharge((EntityType)AetherEntityTypes.GOLDEN_SWET.get(), 0.5, 0.1)
            .addMobCharge((EntityType)AetherEntityTypes.WHIRLWIND.get(), 0.4, 0.1)
            .addMobCharge((EntityType)AetherEntityTypes.EVIL_WHIRLWIND.get(), 0.4, 0.1)
            .addMobCharge((EntityType)AetherEntityTypes.AERWHALE.get(), 0.5, 0.11)
            .addSpawn(AetherMobCategory.AETHER_DARKNESS_MONSTER, new SpawnerData((EntityType)AetherEntityTypes.COCKATRICE.get(), 8, 1, 1))
            .addSpawn(AetherMobCategory.AETHER_SKY_MONSTER, new SpawnerData((EntityType)AetherEntityTypes.ZEPHYR.get(), 20, 1, 1))
            .addSpawn(AetherMobCategory.AETHER_SURFACE_MONSTER, new SpawnerData((EntityType)AetherEntityTypes.AECHOR_PLANT.get(), 7, 1, 1))
            .addSpawn(AetherMobCategory.AETHER_SURFACE_MONSTER, new SpawnerData((EntityType)AetherEntityTypes.BLUE_SWET.get(), 6, 1, 1))
            .addSpawn(AetherMobCategory.AETHER_SURFACE_MONSTER, new SpawnerData((EntityType)AetherEntityTypes.GOLDEN_SWET.get(), 6, 1, 1))
            .addSpawn(AetherMobCategory.AETHER_SURFACE_MONSTER, new SpawnerData((EntityType)AetherEntityTypes.WHIRLWIND.get(), 3, 1, 1))
            .addSpawn(AetherMobCategory.AETHER_SURFACE_MONSTER, new SpawnerData((EntityType)AetherEntityTypes.EVIL_WHIRLWIND.get(), 1, 1, 1))
            .addSpawn(AetherMobCategory.AETHER_AERWHALE, new SpawnerData((EntityType)AetherEntityTypes.AERWHALE.get(), 10, 1, 1))
            .creatureGenerationProbability(0.25F)
            .addSpawn(MobCategory.CREATURE, new SpawnerData((EntityType)AetherEntityTypes.PHYG.get(), 10, 3, 4))
            .addSpawn(MobCategory.CREATURE, new SpawnerData((EntityType)AetherEntityTypes.SHEEPUFF.get(), 12, 3, 4))
            .addSpawn(MobCategory.CREATURE, new SpawnerData((EntityType)AetherEntityTypes.FLYING_COW.get(), 12, 2, 5))
            .addSpawn(MobCategory.CREATURE, new SpawnerData((EntityType)AetherEntityTypes.AERBUNNY.get(), 11, 3, 3))
            .addSpawn(MobCategory.CREATURE, new SpawnerData((EntityType)AetherEntityTypes.MOA.get(), 8, 1, 3))
            .build(),
         builder.addFeature(Decoration.RAW_GENERATION, AetherPlacedFeatures.QUICKSOIL_SHELF_PLACEMENT)
            .addFeature(Decoration.LAKES, AetherPlacedFeatures.WATER_LAKE_PLACEMENT)
            .addFeature(Decoration.UNDERGROUND_ORES, AetherPlacedFeatures.ORE_AETHER_DIRT_PLACEMENT)
            .addFeature(Decoration.UNDERGROUND_ORES, AetherPlacedFeatures.ORE_ICESTONE_PLACEMENT)
            .addFeature(Decoration.UNDERGROUND_ORES, AetherPlacedFeatures.ORE_AMBROSIUM_PLACEMENT)
            .addFeature(Decoration.UNDERGROUND_ORES, AetherPlacedFeatures.ORE_ZANITE_PLACEMENT)
            .addFeature(Decoration.UNDERGROUND_ORES, AetherPlacedFeatures.ORE_GRAVITITE_BURIED_PLACEMENT)
            .addFeature(Decoration.UNDERGROUND_ORES, AetherPlacedFeatures.ORE_GRAVITITE_PLACEMENT)
            .addFeature(Decoration.FLUID_SPRINGS, AetherPlacedFeatures.WATER_SPRING_PLACEMENT)
            .addFeature(Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.HOLIDAY_TREE_PLACEMENT)
            .addFeature(Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.GRASS_PATCH_PLACEMENT)
            .addFeature(Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.TALL_GRASS_PATCH_PLACEMENT)
            .addFeature(Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.WHITE_FLOWER_PATCH_PLACEMENT)
            .addFeature(Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.PURPLE_FLOWER_PATCH_PLACEMENT)
            .addFeature(Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.BERRY_BUSH_PATCH_PLACEMENT)
            .addFeature(Decoration.TOP_LAYER_MODIFICATION, AetherPlacedFeatures.CRYSTAL_ISLAND_PLACEMENT)
            .addFeature(Decoration.TOP_LAYER_MODIFICATION, AetherPlacedFeatures.COLD_AERCLOUD_PLACEMENT)
            .addFeature(Decoration.TOP_LAYER_MODIFICATION, AetherPlacedFeatures.BLUE_AERCLOUD_PLACEMENT)
            .addFeature(Decoration.TOP_LAYER_MODIFICATION, AetherPlacedFeatures.GOLDEN_AERCLOUD_PLACEMENT)
            .build(),
         TemperatureModifier.NONE
      );
   }

   public static Biome fullDefinition(
      boolean precipitation,
      float temperature,
      float downfall,
      BiomeSpecialEffects effects,
      MobSpawnSettings spawnSettings,
      BiomeGenerationSettings generationSettings,
      TemperatureModifier temperatureModifier
   ) {
      return new BiomeBuilder()
         .hasPrecipitation(precipitation)
         .temperature(temperature)
         .downfall(downfall)
         .specialEffects(effects)
         .mobSpawnSettings(spawnSettings)
         .generationSettings(generationSettings)
         .temperatureAdjustment(temperatureModifier)
         .build();
   }

   public static BiomeSource buildAetherBiomeSource(HolderGetter<Biome> biomes) {
      Parameter fullRange = Parameter.span(-1.0F, 1.0F);
      Parameter temps1 = Parameter.span(-1.0F, -0.8F);
      Parameter temps2 = Parameter.span(-0.8F, 0.0F);
      Parameter temps3 = Parameter.span(0.0F, 0.4F);
      Parameter temps4 = Parameter.span(0.4F, 0.93F);
      Parameter temps5 = Parameter.span(0.93F, 0.94F);
      Parameter temps6 = Parameter.span(0.94F, 1.0F);
      return MultiNoiseBiomeSource.createFromList(
         new ParameterList(
            List.of(
               Pair.of(new ParameterPoint(temps1, fullRange, fullRange, fullRange, fullRange, fullRange, 0L), biomes.getOrThrow(AetherBiomes.SKYROOT_MEADOW)),
               Pair.of(
                  new ParameterPoint(temps2, Parameter.span(-1.0F, 0.0F), fullRange, fullRange, fullRange, fullRange, 0L),
                  biomes.getOrThrow(AetherBiomes.SKYROOT_MEADOW)
               ),
               Pair.of(
                  new ParameterPoint(temps2, Parameter.span(0.0F, 1.0F), fullRange, fullRange, fullRange, fullRange, 0L),
                  biomes.getOrThrow(AetherBiomes.SKYROOT_FOREST)
               ),
               Pair.of(
                  new ParameterPoint(temps3, Parameter.span(-1.0F, 0.0F), fullRange, fullRange, fullRange, fullRange, 0L),
                  biomes.getOrThrow(AetherBiomes.SKYROOT_GROVE)
               ),
               Pair.of(
                  new ParameterPoint(temps3, Parameter.span(0.0F, 0.8F), fullRange, fullRange, fullRange, fullRange, 0L),
                  biomes.getOrThrow(AetherBiomes.SKYROOT_FOREST)
               ),
               Pair.of(
                  new ParameterPoint(temps3, Parameter.span(0.8F, 1.0F), fullRange, fullRange, fullRange, fullRange, 0L),
                  biomes.getOrThrow(AetherBiomes.SKYROOT_GROVE)
               ),
               Pair.of(
                  new ParameterPoint(temps4, Parameter.span(-1.0F, -0.1F), fullRange, fullRange, fullRange, fullRange, 0L),
                  biomes.getOrThrow(AetherBiomes.SKYROOT_GROVE)
               ),
               Pair.of(
                  new ParameterPoint(temps4, Parameter.span(-0.1F, 1.0F), fullRange, fullRange, fullRange, fullRange, 0L),
                  biomes.getOrThrow(AetherBiomes.SKYROOT_FOREST)
               ),
               Pair.of(
                  new ParameterPoint(temps5, Parameter.span(-1.0F, -0.6F), fullRange, fullRange, fullRange, fullRange, 0L),
                  biomes.getOrThrow(AetherBiomes.SKYROOT_MEADOW)
               ),
               Pair.of(
                  new ParameterPoint(temps5, Parameter.span(-0.6F, -0.3F), fullRange, fullRange, fullRange, fullRange, 0L),
                  biomes.getOrThrow(AetherBiomes.SKYROOT_GROVE)
               ),
               Pair.of(
                  new ParameterPoint(temps5, Parameter.span(-0.3F, 1.0F), fullRange, fullRange, fullRange, fullRange, 0L),
                  biomes.getOrThrow(AetherBiomes.SKYROOT_FOREST)
               ),
               Pair.of(
                  new ParameterPoint(temps6, Parameter.span(-1.0F, -0.1F), fullRange, fullRange, fullRange, fullRange, 0L),
                  biomes.getOrThrow(AetherBiomes.SKYROOT_MEADOW)
               ),
               Pair.of(
                  new ParameterPoint(temps6, Parameter.span(-0.1F, 0.8F), fullRange, fullRange, fullRange, fullRange, 0L),
                  biomes.getOrThrow(AetherBiomes.SKYROOT_WOODLAND)
               ),
               Pair.of(
                  new ParameterPoint(temps5, Parameter.span(0.8F, 1.0F), fullRange, fullRange, fullRange, fullRange, 0L),
                  biomes.getOrThrow(AetherBiomes.SKYROOT_FOREST)
               )
            )
         )
      );
   }
}
