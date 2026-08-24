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
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class LayeredIntersection5 extends BetterMineshaftPiece {
   private static final int SECONDARY_AXIS_LEN = 5;
   private static final int Y_AXIS_LEN = 10;
   private static final int MAIN_AXIS_LEN = 5;
   private static final int LOCAL_X_END = 4;
   private static final int LOCAL_Y_END = 9;
   private static final int LOCAL_Z_END = 4;

   public LayeredIntersection5(CompoundTag compoundTag) {
      super(StructurePieceTypeModule.LAYERED_INTERSECTION_5, compoundTag);
   }

   public LayeredIntersection5(int pieceChainLen, BoundingBox blockBox, Direction direction, BetterMineshaftConfiguration config) {
      super(StructurePieceTypeModule.LAYERED_INTERSECTION_5, pieceChainLen, config, blockBox);
      this.setOrientation(direction);
   }

   @Override
   protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag compoundTag) {
      super.addAdditionalSaveData(structurePieceSerializationContext, compoundTag);
   }

   public static BoundingBox determineBoxPosition(StructurePieceAccessor structurePieceAccessor, int x, int y, int z, Direction direction) {
      BoundingBox blockBox = new BoundingBox(x, y, z, x, y + 10 - 1, z);
      switch (direction) {
         case NORTH:
         default:
            ((BoundingBoxAccessor)blockBox).setMaxX(x + 4);
            ((BoundingBoxAccessor)blockBox).setMinZ(z - 4);
            break;
         case SOUTH:
            ((BoundingBoxAccessor)blockBox).setMinX(x - 4);
            ((BoundingBoxAccessor)blockBox).setMaxZ(z + 4);
            break;
         case WEST:
            ((BoundingBoxAccessor)blockBox).setMinX(x - 4);
            ((BoundingBoxAccessor)blockBox).setMinZ(z - 4);
            break;
         case EAST:
            ((BoundingBoxAccessor)blockBox).setMaxX(x + 4);
            ((BoundingBoxAccessor)blockBox).setMaxZ(z + 4);
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
            case SOUTH:
            default:
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.maxX() + 1,
                  this.boundingBox.minY(),
                  this.boundingBox.minZ(),
                  Direction.EAST,
                  this.genDepth
               );
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.minX() - 1,
                  this.boundingBox.minY(),
                  this.boundingBox.maxZ(),
                  Direction.WEST,
                  this.genDepth
               );
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.maxX() + 1,
                  this.boundingBox.minY() + 5,
                  this.boundingBox.minZ(),
                  Direction.EAST,
                  this.genDepth
               );
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.minX() - 1,
                  this.boundingBox.minY() + 5,
                  this.boundingBox.maxZ(),
                  Direction.WEST,
                  this.genDepth
               );
               break;
            case WEST:
            case EAST:
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.maxX(),
                  this.boundingBox.minY(),
                  this.boundingBox.maxZ() + 1,
                  Direction.SOUTH,
                  this.genDepth
               );
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.minX(),
                  this.boundingBox.minY(),
                  this.boundingBox.minZ() - 1,
                  Direction.NORTH,
                  this.genDepth
               );
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.maxX(),
                  this.boundingBox.minY() + 5,
                  this.boundingBox.maxZ() + 1,
                  Direction.SOUTH,
                  this.genDepth
               );
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.minX(),
                  this.boundingBox.minY() + 5,
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
      this.chanceReplaceNonAir(world, box, randomSource, this.config.replacementRate, 0, 1, 0, 4, 9, 4, this.config.blockStateRandomizers.mainRandomizer);
      this.chanceReplaceNonAir(world, box, randomSource, this.config.replacementRate, 0, 0, 0, 4, 0, 4, this.config.blockStateRandomizers.floorRandomizer);
      this.fill(world, box, 0, 1, 0, 4, 8, 3, AIR);
      this.fill(world, box, 0, 5, 4, 4, 8, 4, AIR);
      this.replaceAirOrChains(world, box, 0, 0, 0, 4, 0, 3, this.config.blockStates.mainBlockState);
      this.chanceReplaceAir(
         world, randomSource, 0.5F, (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.NORTH_SOUTH), 2, 1, 0, box
      );
      this.chanceReplaceAir(
         world, randomSource, 0.5F, (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.NORTH_SOUTH), 2, 1, 1, box
      );
      this.chanceReplaceAir(world, randomSource, 0.5F, (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.EAST_WEST), 0, 1, 2, box);
      this.chanceReplaceAir(world, randomSource, 0.5F, (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.EAST_WEST), 1, 1, 2, box);
      this.chanceReplaceAir(world, randomSource, 0.5F, (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.EAST_WEST), 3, 1, 2, box);
      this.chanceReplaceAir(world, randomSource, 0.5F, (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.EAST_WEST), 4, 1, 2, box);
      this.fill(world, box, 0, 5, 0, 4, 5, 4, this.config.blockStates.mainBlockState);
      this.chanceReplaceNonAir(world, box, randomSource, 0.5F, 0, 5, 0, 4, 5, 4, this.config.blockStateRandomizers.mainRandomizer);
      this.fill(world, box, 1, 5, 1, 3, 5, 3, AIR);
      this.fill(world, box, randomSource, 1, 1, 1, 1, 8, 1, this.config.blockStateRandomizers.legRandomizer);
      this.fill(world, box, randomSource, 3, 1, 1, 3, 8, 1, this.config.blockStateRandomizers.legRandomizer);
      this.fill(world, box, randomSource, 1, 1, 3, 1, 8, 3, this.config.blockStateRandomizers.legRandomizer);
      this.fill(world, box, randomSource, 3, 1, 3, 3, 8, 3, this.config.blockStateRandomizers.legRandomizer);
      BlockState LADDER = (BlockState)Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH);
      this.fill(world, box, 2, 1, 3, 2, 5, 3, LADDER);
      this.addBiomeDecorations(world, box, randomSource, 0, 0, 0, 4, 8, 4);
      this.addVines(world, box, randomSource, this.config.decorationChances.vineChance, 1, 0, 1, 3, 9, 3);
      this.generatePillarsOrChains(world, box, randomSource);
   }

   private void generatePillarsOrChains(WorldGenLevel world, BoundingBox boundingBox, RandomSource randomSource) {
      this.generatePillarDownOrChainUp(world, randomSource, boundingBox, 1, 1, 0, 8, this.config.blockStates.smallLegBlockState);
      this.generatePillarDownOrChainUp(world, randomSource, boundingBox, 3, 1, 0, 8, this.config.blockStates.smallLegBlockState);
      this.generatePillarDownOrChainUp(world, randomSource, boundingBox, 1, 3, 0, 8, this.config.blockStates.smallLegBlockState);
      this.generatePillarDownOrChainUp(world, randomSource, boundingBox, 3, 3, 0, 8, this.config.blockStates.smallLegBlockState);
   }
}
