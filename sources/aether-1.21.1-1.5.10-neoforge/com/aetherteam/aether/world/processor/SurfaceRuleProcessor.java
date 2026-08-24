package com.aetherteam.aether.world.processor;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.mixin.mixins.common.accessor.ChunkAccessAccessor;
import com.aetherteam.aether.world.BlockLogicUtil;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.Nullable;

public class SurfaceRuleProcessor extends StructureProcessor {
   public static final SurfaceRuleProcessor INSTANCE = new SurfaceRuleProcessor();
   public static final MapCodec<SurfaceRuleProcessor> CODEC = MapCodec.unit(INSTANCE);

   @Nullable
   public StructureBlockInfo process(
      LevelReader level,
      BlockPos origin,
      BlockPos centerBottom,
      StructureBlockInfo originalBlockInfo,
      StructureBlockInfo modifiedBlockInfo,
      StructurePlaceSettings settings,
      @Nullable StructureTemplate template
   ) {
      if (level instanceof WorldGenLevel worldGenLevel) {
         if (worldGenLevel instanceof WorldGenRegion region && BlockLogicUtil.isOutOfBounds(modifiedBlockInfo.pos(), region.getCenter())) {
            return modifiedBlockInfo;
         }

         if (worldGenLevel.getChunkSource() instanceof ServerChunkCache serverChunkCache
            && serverChunkCache.getGenerator() instanceof NoiseBasedChunkGenerator noiseBasedChunkGenerator) {
            NoiseGeneratorSettings settingsHolder = (NoiseGeneratorSettings)noiseBasedChunkGenerator.generatorSettings().value();
            RuleSource surfaceRule = settingsHolder.surfaceRule();
            ChunkAccess chunkAccess = worldGenLevel.getChunk(modifiedBlockInfo.pos());
            NoiseChunk noisechunk = ((ChunkAccessAccessor)chunkAccess).aether$getNoiseChunk();
            if (noisechunk != null) {
               CarvingContext carvingcontext = new CarvingContext(
                  noiseBasedChunkGenerator,
                  worldGenLevel.registryAccess(),
                  chunkAccess.getHeightAccessorForGeneration(),
                  noisechunk,
                  serverChunkCache.randomState(),
                  surfaceRule
               );
               Optional<BlockState> state = carvingcontext.topMaterial(
                  worldGenLevel.getBiomeManager()::getNoiseBiomeAtPosition, chunkAccess, modifiedBlockInfo.pos(), false
               );
               if (state.isPresent()
                  && modifiedBlockInfo.state().is(AetherTags.Blocks.AETHER_DIRT)
                  && !modifiedBlockInfo.state().is((Block)AetherBlocks.AETHER_DIRT.get())
                  && state.get().is(AetherTags.Blocks.AETHER_DIRT)) {
                  return new StructureBlockInfo(modifiedBlockInfo.pos(), state.get(), null);
               }
            }
         }
      }

      return modifiedBlockInfo;
   }

   protected StructureProcessorType<?> getType() {
      return (StructureProcessorType<?>)AetherStructureProcessors.SURFACE_RULE.get();
   }
}
