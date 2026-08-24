package com.aetherteam.aether.world.structurepiece.bronzedungeon;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlockStateProperties;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.data.resources.AetherFeatureStates;
import com.aetherteam.aether.world.structurepiece.AetherStructurePieceTypes;
import com.aetherteam.nitrogen.data.resources.builders.NitrogenConfiguredFeatureBuilders;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class BronzeDungeonSurfaceRuins extends StructurePiece {
   private static final BlockStateProvider BLOCKS = new WeightedStateProvider(
      SimpleWeightedRandomList.builder()
         .add((BlockState)((Block)AetherBlocks.HOLYSTONE.get()).defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true), 3)
         .add((BlockState)((Block)AetherBlocks.MOSSY_HOLYSTONE.get()).defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true))
         .build()
   );
   private static final BlockStateProvider TOPS = new WeightedStateProvider(
      SimpleWeightedRandomList.builder()
         .add((BlockState)((Block)AetherBlocks.HOLYSTONE.get()).defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true), 6)
         .add(((SlabBlock)AetherBlocks.HOLYSTONE_SLAB.get()).defaultBlockState(), 6)
         .add((BlockState)((Block)AetherBlocks.MOSSY_HOLYSTONE.get()).defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true), 3)
         .add(((SlabBlock)AetherBlocks.MOSSY_HOLYSTONE_SLAB.get()).defaultBlockState(), 3)
         .build()
   );
   private static final BlockStateProvider BOTTOMS = new WeightedStateProvider(
      SimpleWeightedRandomList.builder()
         .add((BlockState)((Block)AetherBlocks.HOLYSTONE.get()).defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true), 6)
         .add((BlockState)((SlabBlock)AetherBlocks.HOLYSTONE_SLAB.get()).defaultBlockState().setValue(BlockStateProperties.SLAB_TYPE, SlabType.TOP), 6)
         .add((BlockState)((Block)AetherBlocks.MOSSY_HOLYSTONE.get()).defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true), 3)
         .add((BlockState)((SlabBlock)AetherBlocks.MOSSY_HOLYSTONE_SLAB.get()).defaultBlockState().setValue(BlockStateProperties.SLAB_TYPE, SlabType.TOP), 3)
         .build()
   );
   private static final ConfiguredFeature<?, ? extends Feature<?>> MIXED_FLOWER_PATCH = new ConfiguredFeature(
      Feature.FLOWER,
      NitrogenConfiguredFeatureBuilders.grassPatch(
         new WeightedStateProvider(SimpleWeightedRandomList.builder().add(AetherFeatureStates.PURPLE_FLOWER, 1).add(AetherFeatureStates.WHITE_FLOWER, 1)), 24
      )
   );

   public BronzeDungeonSurfaceRuins(BoundingBox horizontalBounds) {
      super((StructurePieceType)AetherStructurePieceTypes.BRONZE_SURFACE_RUINS.value(), 0, horizontalBounds);
      this.setOrientation(Direction.SOUTH);
   }

   public BronzeDungeonSurfaceRuins(StructurePieceSerializationContext context, CompoundTag nbt) {
      super((StructurePieceType)AetherStructurePieceTypes.BRONZE_SURFACE_RUINS.value(), nbt);
      this.setOrientation(Direction.SOUTH);
   }

   public void postProcess(
      WorldGenLevel level,
      StructureManager manager,
      ChunkGenerator chunkGen,
      RandomSource random,
      BoundingBox chunkBounds,
      ChunkPos chunkPos,
      BlockPos structureBottomCenter
   ) {
      for (int x = this.boundingBox.minX(); x <= this.boundingBox.maxX(); x++) {
         this.generateTunnelWallColumn(level, random, chunkBounds, x, this.boundingBox.minZ());
         this.generateTunnelWallColumn(level, random, chunkBounds, x, this.boundingBox.maxZ());
      }

      for (int z = this.boundingBox.minZ() + 1; z < this.boundingBox.maxZ(); z++) {
         this.generateTunnelWallColumn(level, random, chunkBounds, this.boundingBox.minX(), z);
         this.generateTunnelWallColumn(level, random, chunkBounds, this.boundingBox.maxX(), z);
      }

      BlockPos pieceCenter = this.boundingBox.getCenter();
      if (chunkBounds.isInside(pieceCenter)) {
         BlockPos aboveTopBlock = level.getHeightmapPos(Types.OCEAN_FLOOR_WG, chunkBounds.getCenter());
         MIXED_FLOWER_PATCH.place(level, chunkGen, random, aboveTopBlock);
      }
   }

   private void generateTunnelWallColumn(WorldGenLevel level, RandomSource random, BoundingBox chunkBounds, int x, int z) {
      if (chunkBounds.isInside(x, this.boundingBox.minY(), z)) {
         int trimmer = (x + z & 1) != 1 && !random.nextBoolean() ? 0 : random.nextInt(4) + random.nextInt(4) + 1;
         int wallColumnHeight = Math.max(random.nextInt(2) - random.nextInt(2) - trimmer, -2);
         int ySurfaceAir = level.getHeight(Types.OCEAN_FLOOR_WG, x, z);
         BlockPos wallColumnStart = new BlockPos(x, ySurfaceAir, z);
         this.placeColumnBlocks(level, random, this.scanColumnForPlacement(level, random, wallColumnHeight, ySurfaceAir, wallColumnStart));
         BlockPos topPos = wallColumnStart.above(wallColumnHeight);
         BlockStateProvider topProvider = wallColumnHeight < 1 ? BLOCKS : TOPS;
         level.setBlock(topPos, topProvider.getState(random, topPos), 3);
      }
   }

   private void placeColumnBlocks(WorldGenLevel level, RandomSource random, List<BlockPos> forPlacement) {
      if (!forPlacement.isEmpty()) {
         BlockPos lastPos = ((BlockPos)forPlacement.getFirst()).below();

         for (BlockPos posAt : forPlacement) {
            boolean hasSkippedGap = posAt.getY() - lastPos.getY() != 1;
            if (hasSkippedGap) {
               BlockStateProvider tailProvider = level.getBlockState(posAt).isAir() ? BOTTOMS : BLOCKS;
               level.setBlock(posAt, tailProvider.getState(random, posAt), 3);
               BlockPos capPos = lastPos.above();
               BlockStateProvider capProvider = level.getBlockState(capPos).isAir() ? TOPS : BLOCKS;
               level.setBlock(capPos, capProvider.getState(random, capPos), 3);
            } else {
               level.setBlock(posAt, BLOCKS.getState(random, posAt), 3);
            }

            lastPos = posAt;
         }
      }
   }

   private List<BlockPos> scanColumnForPlacement(WorldGenLevel level, RandomSource random, int wallColumnHeight, int ySurfaceAir, BlockPos wallColumnStart) {
      int depthOffset = Math.round(random.nextFloat() * wallColumnHeight);
      List<BlockPos> toPlace = new ArrayList<>();

      for (int dY = this.boundingBox.minY() - ySurfaceAir; dY < wallColumnHeight; dY++) {
         BlockPos posAt = wallColumnStart.above(dY);
         BlockState blockAt = level.getBlockState(posAt);
         if (dY >= -4
            || (blockAt.is(AetherTags.Blocks.AETHER_ISLAND_BLOCKS) || blockAt.isAir())
               && level.getBlockState(posAt.below(depthOffset)).is(AetherTags.Blocks.AETHER_ISLAND_BLOCKS)) {
            toPlace.add(posAt);
         }
      }

      return toPlace;
   }

   protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
   }
}
