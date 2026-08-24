package com.aetherteam.aether.data.resources.builders;

import java.util.Map;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;

public class AetherStructureBuilders {
   public static StructureSettings structure(HolderSet<Biome> biomes, Decoration step, TerrainAdjustment adjustment) {
      return structure(biomes, Map.of(), step, adjustment);
   }

   public static StructureSettings structure(
      HolderSet<Biome> biomes, Map<MobCategory, StructureSpawnOverride> mobs, Decoration step, TerrainAdjustment adjustment
   ) {
      return new StructureSettings(biomes, mobs, step, adjustment);
   }
}
