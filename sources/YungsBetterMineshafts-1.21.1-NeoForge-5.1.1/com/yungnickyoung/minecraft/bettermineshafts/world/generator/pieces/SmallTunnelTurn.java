package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.yungnickyoung.minecraft.bettermineshafts.module.StructurePieceTypeModule;
import com.yungnickyoung.minecraft.bettermineshafts.world.config.BetterMineshaftConfiguration;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.BetterMineshaftGenerator;
import com.yungnickyoung.minecraft.yungsapi.world.util.BoundingBoxHelper;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class SmallTunnelTurn extends BetterMineshaftPiece {
   private final SmallTunnelTurn.TurnDirection turnDirection;
   private static final int SECONDARY_AXIS_LEN = 5;
   private static final int Y_AXIS_LEN = 5;
   private static final int MAIN_AXIS_LEN = 5;
   private static final int LOCAL_X_END = 4;
   private static final int LOCAL_Y_END = 4;
   private static final int LOCAL_Z_END = 4;

   public SmallTunnelTurn(CompoundTag compoundTag) {
      super(StructurePieceTypeModule.SMALL_TUNNEL_TURN, compoundTag);
      this.turnDirection = SmallTunnelTurn.TurnDirection.valueOf(compoundTag.getInt("TurnDirection"));
   }

   public SmallTunnelTurn(int chunkPieceLen, RandomSource randomSource, BoundingBox blockBox, Direction direction, BetterMineshaftConfiguration config) {
      super(StructurePieceTypeModule.SMALL_TUNNEL_TURN, chunkPieceLen, config, blockBox);
      this.setOrientation(direction);
      this.turnDirection = randomSource.nextBoolean() ? SmallTunnelTurn.TurnDirection.LEFT : SmallTunnelTurn.TurnDirection.RIGHT;
   }

   @Override
   protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag compoundTag) {
      super.addAdditionalSaveData(structurePieceSerializationContext, compoundTag);
      compoundTag.putInt("TurnDirection", this.turnDirection.value);
   }

   public static BoundingBox determineBoxPosition(StructurePieceAccessor structurePieceAccessor, int x, int y, int z, Direction direction) {
      BoundingBox blockBox = BoundingBoxHelper.boxFromCoordsWithRotation(x, y, z, 5, 5, 5, direction);
      StructurePiece intersectingPiece = structurePieceAccessor.findCollisionPiece(blockBox);
      return intersectingPiece != null ? null : blockBox;
   }

   @Override
   public void addChildren(StructurePiece structurePiece, StructurePieceAccessor structurePieceAccessor, RandomSource randomSource) {
      Direction direction = this.getOrientation();
      if (direction != null) {
         Direction nextDirection = this.turnDirection == SmallTunnelTurn.TurnDirection.LEFT ? direction.getCounterClockWise() : direction.getClockWise();
         switch (nextDirection) {
            case NORTH:
            default:
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.minX(),
                  this.boundingBox.minY(),
                  this.boundingBox.minZ() - 1,
                  nextDirection,
                  this.genDepth
               );
               break;
            case SOUTH:
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.maxX(),
                  this.boundingBox.minY(),
                  this.boundingBox.maxZ() + 1,
                  nextDirection,
                  this.genDepth
               );
               break;
            case WEST:
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.minX() - 1,
                  this.boundingBox.minY(),
                  this.boundingBox.maxZ(),
                  nextDirection,
                  this.genDepth
               );
               break;
            case EAST:
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.maxX() + 1,
                  this.boundingBox.minY(),
                  this.boundingBox.minZ(),
                  nextDirection,
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
      Direction direction = this.getOrientation();
      this.chanceReplaceNonAir(world, box, randomSource, this.config.replacementRate, 0, 1, 0, 4, 4, 4, this.config.blockStateRandomizers.mainRandomizer);
      this.chanceReplaceNonAir(world, box, randomSource, this.config.replacementRate, 0, 0, 0, 4, 0, 4, this.config.blockStateRandomizers.floorRandomizer);
      this.fill(world, box, 1, 1, 0, 3, 3, 3, AIR);
      this.replaceAirOrChains(world, box, 1, 0, 0, 3, 0, 3, this.config.blockStates.mainBlockState);
      this.fill(world, box, 2, 1, 0, 2, 1, 1, Blocks.RAIL.defaultBlockState());
      if (this.turnDirection == SmallTunnelTurn.TurnDirection.LEFT) {
         if (direction != Direction.NORTH && direction != Direction.EAST) {
            this.generateRightTurn(world, box);
         } else {
            this.generateLeftTurn(world, box);
         }
      } else if (direction != Direction.NORTH && direction != Direction.EAST) {
         this.generateLeftTurn(world, box);
      } else {
         this.generateRightTurn(world, box);
      }

      this.addBiomeDecorations(world, box, randomSource, 0, 0, 0, 4, 3, 4);
      this.addVines(world, box, randomSource, this.config.decorationChances.vineChance, 1, 0, 1, 3, 4, 3);
      this.generatePillarsOrChains(world, box, randomSource);
   }

   private void generateLeftTurn(WorldGenLevel world, BoundingBox box) {
      this.fill(world, box, 0, 1, 1, 0, 3, 3, AIR);
      this.replaceAirOrChains(world, box, 0, 0, 1, 0, 0, 3, this.config.blockStates.mainBlockState);
      this.fill(world, box, 2, 1, 2, 2, 1, 2, (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.SOUTH_WEST));
      this.fill(world, box, 0, 1, 2, 1, 1, 2, (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.EAST_WEST));
   }

   private void generateRightTurn(WorldGenLevel world, BoundingBox box) {
      this.fill(world, box, 4, 1, 1, 4, 3, 3, AIR);
      this.replaceAirOrChains(world, box, 4, 0, 1, 4, 0, 3, this.config.blockStates.mainBlockState);
      this.fill(world, box, 2, 1, 2, 2, 1, 2, (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.SOUTH_EAST));
      this.fill(world, box, 3, 1, 2, 4, 1, 2, (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.EAST_WEST));
   }

   private void generatePillarsOrChains(WorldGenLevel world, BoundingBox box, RandomSource randomSource) {
      this.generatePillarDownOrChainUp(world, randomSource, box, 1, 0, 1);
      this.generatePillarDownOrChainUp(world, randomSource, box, 3, 0, 1);
      this.generatePillarDownOrChainUp(world, randomSource, box, 1, 0, 3);
      this.generatePillarDownOrChainUp(world, randomSource, box, 3, 0, 3);
   }

   public static enum TurnDirection {
      LEFT(0),
      RIGHT(1);

      private final int value;

      private TurnDirection(int value) {
         this.value = value;
      }

      public static SmallTunnelTurn.TurnDirection valueOf(int value) {
         return Arrays.stream(values()).filter(dir -> dir.value == value).findFirst().get();
      }
   }
}
