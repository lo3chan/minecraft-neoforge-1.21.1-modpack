package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.yungnickyoung.minecraft.bettermineshafts.module.StructurePieceTypeModule;
import com.yungnickyoung.minecraft.bettermineshafts.world.config.BetterMineshaftConfiguration;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.BetterMineshaftGenerator;
import com.yungnickyoung.minecraft.yungsapi.world.util.BoundingBoxHelper;
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
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class SmallTunnelStairs extends BetterMineshaftPiece {
   private static final int SECONDARY_AXIS_LEN = 5;
   private static final int Y_AXIS_LEN = 9;
   private static final int MAIN_AXIS_LEN = 5;
   private static final int LOCAL_X_END = 4;
   private static final int LOCAL_Y_END = 8;
   private static final int LOCAL_Z_END = 4;

   public SmallTunnelStairs(CompoundTag compoundTag) {
      super(StructurePieceTypeModule.SMALL_TUNNEL_STAIRS, compoundTag);
   }

   public SmallTunnelStairs(int chunkPieceLen, BoundingBox blockBox, Direction direction, BetterMineshaftConfiguration config) {
      super(StructurePieceTypeModule.SMALL_TUNNEL_STAIRS, chunkPieceLen, config, blockBox);
      this.setOrientation(direction);
   }

   @Override
   protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag compoundTag) {
      super.addAdditionalSaveData(structurePieceSerializationContext, compoundTag);
   }

   public static BoundingBox determineBoxPosition(StructurePieceAccessor structurePieceAccessor, int x, int y, int z, Direction direction) {
      BoundingBox blockBox = BoundingBoxHelper.boxFromCoordsWithRotation(x, y, z, 5, 9, 5, direction);
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
                  this.boundingBox.minX(),
                  this.boundingBox.minY() + 4,
                  this.boundingBox.minZ() - 1,
                  direction,
                  this.genDepth
               );
               break;
            case SOUTH:
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.maxX(),
                  this.boundingBox.minY() + 4,
                  this.boundingBox.maxZ() + 1,
                  direction,
                  this.genDepth
               );
               break;
            case WEST:
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.minX() - 1,
                  this.boundingBox.minY() + 4,
                  this.boundingBox.maxZ(),
                  direction,
                  this.genDepth
               );
               break;
            case EAST:
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.maxX() + 1,
                  this.boundingBox.minY() + 4,
                  this.boundingBox.minZ(),
                  direction,
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
      this.chanceReplaceNonAir(world, box, randomSource, this.config.replacementRate, 0, 1, 0, 4, 8, 4, this.config.blockStateRandomizers.mainRandomizer);
      this.chanceReplaceNonAir(world, box, randomSource, this.config.replacementRate, 0, 0, 0, 4, 0, 4, this.config.blockStateRandomizers.floorRandomizer);

      for (int i = 0; i < 5; i++) {
         this.fill(world, box, 1, i + 1, i, 3, i + 1 + 3, i, AIR);
         this.chanceReplaceNonAir(world, box, randomSource, 0.5F, 1, i, i, 3, i, i, this.config.blockStateRandomizers.mainRandomizer);
         this.replaceAirOrChains(world, box, 1, i, i, 3, i, i, this.config.blockStates.mainBlockState);
         if (i == 0) {
            this.chanceReplaceAir(
               world,
               randomSource,
               0.5F,
               (BlockState)((BlockState)Blocks.POWERED_RAIL.defaultBlockState().setValue(PoweredRailBlock.POWERED, true))
                  .setValue(PoweredRailBlock.SHAPE, RailShape.ASCENDING_NORTH),
               2,
               i + 1,
               i,
               box
            );
         } else if (i == 4) {
            this.chanceReplaceAir(
               world, randomSource, 0.5F, (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.NORTH_SOUTH), 2, i + 1, i, box
            );
         } else {
            this.chanceReplaceAir(
               world, randomSource, 0.5F, (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.ASCENDING_NORTH), 2, i + 1, i, box
            );
         }
      }

      this.addBiomeDecorations(world, box, randomSource, 1, 0, 0, 3, 7, 4);
      this.addVines(world, box, randomSource, this.config.decorationChances.vineChance, 1, 0, 1, 3, 8, 3);
   }
}
