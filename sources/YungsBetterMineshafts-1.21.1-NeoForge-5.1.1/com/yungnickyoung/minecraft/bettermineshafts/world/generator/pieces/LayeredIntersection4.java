package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.yungnickyoung.minecraft.bettermineshafts.mixin.BoundingBoxAccessor;
import com.yungnickyoung.minecraft.bettermineshafts.module.StructurePieceTypeModule;
import com.yungnickyoung.minecraft.bettermineshafts.world.config.BetterMineshaftConfiguration;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.BetterMineshaftGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class LayeredIntersection4 extends BetterMineshaftPiece {
   private static final int SECONDARY_AXIS_LEN = 7;
   private static final int Y_AXIS_LEN = 9;
   private static final int MAIN_AXIS_LEN = 7;
   private static final int LOCAL_X_END = 6;
   private static final int LOCAL_Y_END = 8;
   private static final int LOCAL_Z_END = 6;

   public LayeredIntersection4(CompoundTag compoundTag) {
      super(StructurePieceTypeModule.LAYERED_INTERSECTION_4, compoundTag);
   }

   public LayeredIntersection4(int chainLength, BoundingBox blockBox, Direction direction, BetterMineshaftConfiguration config) {
      super(StructurePieceTypeModule.LAYERED_INTERSECTION_4, chainLength, config, blockBox);
      this.setOrientation(direction);
   }

   @Override
   protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag compoundTag) {
      super.addAdditionalSaveData(structurePieceSerializationContext, compoundTag);
   }

   public static BoundingBox determineBoxPosition(StructurePieceAccessor structurePieceAccessor, int x, int y, int z, Direction direction) {
      BoundingBox blockBox = new BoundingBox(x, y - 3, z, x, y - 3 + 9 - 1, z);
      BoundingBoxAccessor blockBoxAccessor = (BoundingBoxAccessor)blockBox;
      switch (direction) {
         case NORTH:
         default:
            blockBoxAccessor.setMaxX(x + 5);
            blockBoxAccessor.setMinX(x - 1);
            blockBoxAccessor.setMinZ(z - 6);
            break;
         case SOUTH:
            blockBoxAccessor.setMaxX(x + 1);
            blockBoxAccessor.setMinX(x - 5);
            blockBoxAccessor.setMaxZ(z + 6);
            break;
         case WEST:
            blockBoxAccessor.setMinX(x - 6);
            blockBoxAccessor.setMaxZ(z);
            blockBoxAccessor.setMinZ(z - 5);
            break;
         case EAST:
            blockBoxAccessor.setMaxX(x + 6);
            blockBoxAccessor.setMaxZ(z + 4);
            blockBoxAccessor.setMinZ(z - 1);
      }

      StructurePiece intersectingPiece = structurePieceAccessor.findCollisionPiece(blockBox);
      return intersectingPiece != null ? null : blockBox;
   }

   @Override
   public void addChildren(StructurePiece structurePiece, StructurePieceAccessor structurePieceAccessor, RandomSource randomSource) {
      Direction direction = this.getOrientation();
      if (direction != null) {
         switch (direction) {
            case NORTH:
            default:
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.minX() + 1,
                  this.boundingBox.minY() + 3,
                  this.boundingBox.minZ() - 1,
                  Direction.NORTH,
                  this.genDepth
               );
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.maxX() + 1,
                  this.boundingBox.minY() + 3,
                  this.boundingBox.maxZ() - 5,
                  Direction.EAST,
                  this.genDepth
               );
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.minX() - 1,
                  this.boundingBox.minY() + 3,
                  this.boundingBox.maxZ() - 1,
                  Direction.WEST,
                  this.genDepth
               );
               break;
            case SOUTH:
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.maxX() - 1,
                  this.boundingBox.minY() + 3,
                  this.boundingBox.maxZ() + 1,
                  Direction.SOUTH,
                  this.genDepth
               );
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.maxX() + 1,
                  this.boundingBox.minY() + 3,
                  this.boundingBox.minZ() + 1,
                  Direction.EAST,
                  this.genDepth
               );
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.minX() - 1,
                  this.boundingBox.minY() + 3,
                  this.boundingBox.minZ() + 5,
                  Direction.WEST,
                  this.genDepth
               );
               break;
            case WEST:
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.minX() - 1,
                  this.boundingBox.minY() + 3,
                  this.boundingBox.maxZ(),
                  Direction.WEST,
                  this.genDepth
               );
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.maxX() - 5,
                  this.boundingBox.minY() + 3,
                  this.boundingBox.minZ() - 1,
                  Direction.NORTH,
                  this.genDepth
               );
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.maxX() - 1,
                  this.boundingBox.minY() + 3,
                  this.boundingBox.maxZ() + 1,
                  Direction.SOUTH,
                  this.genDepth
               );
               break;
            case EAST:
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.maxX() + 1,
                  this.boundingBox.minY() + 3,
                  this.boundingBox.minZ() + 1,
                  Direction.EAST,
                  this.genDepth
               );
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.minX() + 5,
                  this.boundingBox.minY() + 3,
                  this.boundingBox.maxZ() + 1,
                  Direction.SOUTH,
                  this.genDepth
               );
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.minX() + 1,
                  this.boundingBox.minY() + 3,
                  this.boundingBox.minZ() - 1,
                  Direction.NORTH,
                  this.genDepth
               );
         }
      }
   }

   public void postProcess(
      WorldGenLevel world,
      StructureManager structureManager,
      ChunkGenerator chunkGenerator,
      RandomSource randomSource,
      BoundingBox box,
      ChunkPos chunkPos,
      BlockPos blockPos
   ) {
      this.chanceReplaceNonAir(world, box, randomSource, this.config.replacementRate, 0, 1, 0, 6, 8, 6, this.config.blockStateRandomizers.mainRandomizer);
      this.chanceReplaceNonAir(world, box, randomSource, this.config.replacementRate, 0, 0, 0, 6, 0, 6, this.config.blockStateRandomizers.floorRandomizer);
      this.fill(world, box, 2, 1, 2, 4, 1, 4, AIR);
      this.fill(world, box, 2, 2, 1, 4, 2, 5, AIR);
      this.fill(world, box, 2, 3, 0, 4, 6, 6, AIR);
      this.fill(world, box, 0, 3, 2, 6, 6, 4, AIR);
      this.fill(world, box, 2, 2, 0, 2, 3, 1, this.config.blockStates.mainBlockState);
      this.chanceReplaceNonAir(world, box, randomSource, 0.1F, 2, 2, 0, 2, 3, 1, this.config.blockStateRandomizers.brickRandomizer);
      this.fill(world, box, 4, 2, 0, 4, 3, 1, this.config.blockStates.mainBlockState);
      this.chanceReplaceNonAir(world, box, randomSource, 0.1F, 4, 2, 0, 4, 3, 1, this.config.blockStateRandomizers.brickRandomizer);
      this.fill(world, box, 2, 2, 5, 2, 3, 6, this.config.blockStates.mainBlockState);
      this.chanceReplaceNonAir(world, box, randomSource, 0.1F, 2, 2, 5, 2, 3, 6, this.config.blockStateRandomizers.brickRandomizer);
      this.fill(world, box, 4, 2, 5, 4, 3, 6, this.config.blockStates.mainBlockState);
      this.chanceReplaceNonAir(world, box, randomSource, 0.1F, 4, 2, 5, 4, 3, 6, this.config.blockStateRandomizers.brickRandomizer);
      this.placeBlock(world, this.config.blockStates.mainBlockState, 3, 2, 0, box);
      this.placeBlock(world, this.config.blockStates.mainBlockState, 3, 1, 1, box);
      this.placeBlock(world, this.config.blockStates.mainBlockState, 3, 0, 2, box);
      this.placeBlock(world, this.config.blockStates.mainBlockState, 3, 0, 3, box);
      this.placeBlock(world, this.config.blockStates.mainBlockState, 3, 0, 4, box);
      this.placeBlock(world, this.config.blockStates.mainBlockState, 3, 1, 5, box);
      this.placeBlock(world, this.config.blockStates.mainBlockState, 3, 2, 6, box);
      this.chanceReplaceAir(
         world,
         randomSource,
         0.5F,
         (BlockState)Blocks.RAIL.defaultBlockState().setValue(BlockStateProperties.RAIL_SHAPE, RailShape.ASCENDING_SOUTH),
         3,
         3,
         0,
         box
      );
      this.chanceReplaceAir(
         world,
         randomSource,
         0.5F,
         (BlockState)Blocks.RAIL.defaultBlockState().setValue(BlockStateProperties.RAIL_SHAPE, RailShape.ASCENDING_SOUTH),
         3,
         2,
         1,
         box
      );
      this.chanceReplaceAir(
         world,
         randomSource,
         0.5F,
         (BlockState)Blocks.RAIL.defaultBlockState().setValue(BlockStateProperties.RAIL_SHAPE, RailShape.ASCENDING_SOUTH),
         3,
         1,
         2,
         box
      );
      this.chanceReplaceAir(
         world,
         randomSource,
         0.5F,
         (BlockState)((BlockState)Blocks.POWERED_RAIL.defaultBlockState().setValue(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.NORTH_SOUTH))
            .setValue(BlockStateProperties.POWERED, true),
         3,
         1,
         3,
         box
      );
      this.chanceReplaceAir(
         world,
         randomSource,
         0.5F,
         (BlockState)Blocks.RAIL.defaultBlockState().setValue(BlockStateProperties.RAIL_SHAPE, RailShape.ASCENDING_NORTH),
         3,
         1,
         4,
         box
      );
      this.chanceReplaceAir(
         world,
         randomSource,
         0.5F,
         (BlockState)Blocks.RAIL.defaultBlockState().setValue(BlockStateProperties.RAIL_SHAPE, RailShape.ASCENDING_NORTH),
         3,
         2,
         5,
         box
      );
      this.chanceReplaceAir(
         world,
         randomSource,
         0.5F,
         (BlockState)Blocks.RAIL.defaultBlockState().setValue(BlockStateProperties.RAIL_SHAPE, RailShape.ASCENDING_NORTH),
         3,
         3,
         6,
         box
      );
      this.fill(world, box, 0, 3, 2, 1, 3, 4, this.config.blockStates.mainBlockState);
      this.fill(world, box, 2, 4, 2, 4, 4, 4, this.config.blockStates.mainBlockState);
      this.fill(world, box, 5, 3, 2, 6, 3, 4, this.config.blockStates.mainBlockState);
      this.chanceReplaceAir(
         world,
         randomSource,
         0.5F,
         (BlockState)((BlockState)Blocks.POWERED_RAIL.defaultBlockState().setValue(PoweredRailBlock.SHAPE, RailShape.EAST_WEST))
            .setValue(PoweredRailBlock.POWERED, true),
         0,
         4,
         3,
         box
      );
      this.chanceReplaceAir(
         world, randomSource, 0.5F, (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.ASCENDING_EAST), 1, 4, 3, box
      );
      this.chanceFill(
         world, box, randomSource, 0.5F, 2, 5, 3, 4, 5, 3, (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.EAST_WEST)
      );
      this.chanceReplaceAir(
         world, randomSource, 0.5F, (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.ASCENDING_WEST), 5, 4, 3, box
      );
      this.chanceReplaceAir(
         world,
         randomSource,
         0.5F,
         (BlockState)((BlockState)Blocks.POWERED_RAIL.defaultBlockState().setValue(PoweredRailBlock.SHAPE, RailShape.EAST_WEST))
            .setValue(PoweredRailBlock.POWERED, true),
         6,
         4,
         3,
         box
      );
      this.addBiomeDecorations(world, box, randomSource, 0, 0, 0, 6, 7, 6);
      this.addVines(world, box, randomSource, this.config.decorationChances.vineChance, 1, 0, 1, 5, 8, 5);
   }
}
