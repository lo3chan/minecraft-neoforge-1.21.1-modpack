package com.aetherteam.aether.world.feature;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.data.resources.AetherFeatureStates;
import com.aetherteam.aether.data.resources.registries.AetherConfiguredFeatures;
import com.aetherteam.aether.mixin.mixins.common.accessor.ChunkAccessAccessor;
import com.aetherteam.aether.world.BlockLogicUtil;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class CrystalIslandFeature extends Feature<NoneFeatureConfiguration> {
   public CrystalIslandFeature(Codec<NoneFeatureConfiguration> codec) {
      super(codec);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      WorldGenLevel level = context.level();
      BlockPos pos = context.origin();
      PlacedFeature feature = (PlacedFeature)PlacementUtils.inlinePlaced(
            level.holderOrThrow(AetherConfiguredFeatures.CRYSTAL_TREE_CONFIGURATION), new PlacementModifier[0]
         )
         .value();
      if (feature.place(level, context.chunkGenerator(), context.random(), context.origin().above())) {
         for (int i = 0; i < 3; i++) {
            BlockState state;
            if (i == 0) {
               state = AetherFeatureStates.AETHER_GRASS_BLOCK;
            } else {
               state = AetherFeatureStates.HOLYSTONE;
            }

            int offset = i;
            this.setIslandBlock(level, pos.below(offset), state);
            Arrays.stream(Direction.values()).toList().subList(2, 6).forEach(direction -> {
               this.setIslandBlock(level, pos.relative(direction).below(offset), state);
               if (offset != 2) {
                  this.setIslandBlock(level, pos.relative(direction, 2).below(offset), state);
                  this.setIslandBlock(level, pos.relative(direction).relative(direction.getClockWise()).below(offset), state);
               }
            });
         }

         return true;
      } else {
         return false;
      }
   }

   private void setIslandBlock(WorldGenLevel level, BlockPos pos, BlockState testState) {
      if (!(level instanceof WorldGenRegion region && BlockLogicUtil.isOutOfBounds(pos, region.getCenter()))) {
         BlockState newState = testState;
         if (level.getChunkSource() instanceof ServerChunkCache serverChunkCache
            && serverChunkCache.getGenerator() instanceof NoiseBasedChunkGenerator noiseBasedChunkGenerator) {
            NoiseGeneratorSettings settingsHolder = (NoiseGeneratorSettings)noiseBasedChunkGenerator.generatorSettings().value();
            RuleSource surfaceRule = settingsHolder.surfaceRule();
            ChunkAccess chunkAccess = level.getChunk(pos);
            NoiseChunk noisechunk = ((ChunkAccessAccessor)chunkAccess).aether$getNoiseChunk();
            if (noisechunk != null) {
               CarvingContext carvingcontext = new CarvingContext(
                  noiseBasedChunkGenerator,
                  level.registryAccess(),
                  chunkAccess.getHeightAccessorForGeneration(),
                  noisechunk,
                  serverChunkCache.randomState(),
                  surfaceRule
               );
               Optional<BlockState> state = carvingcontext.topMaterial(level.getBiomeManager()::getNoiseBiomeAtPosition, chunkAccess, pos, false);
               if (state.isPresent()
                  && testState.is(AetherTags.Blocks.AETHER_DIRT)
                  && !testState.is((Block)AetherBlocks.AETHER_DIRT.get())
                  && state.get().is(AetherTags.Blocks.AETHER_DIRT)) {
                  newState = state.get();
               }
            }
         }

         this.setBlock(level, pos, newState);
      }
   }
}
