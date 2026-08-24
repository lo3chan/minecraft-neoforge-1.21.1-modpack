package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.yungnickyoung.minecraft.bettermineshafts.BetterMineshaftsCommon;
import com.yungnickyoung.minecraft.bettermineshafts.module.StructurePieceTypeModule;
import com.yungnickyoung.minecraft.bettermineshafts.world.config.BetterMineshaftConfiguration;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.BetterMineshaftGenerator;
import com.yungnickyoung.minecraft.yungsapi.world.util.BoundingBoxHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class SideRoom extends BetterMineshaftPiece {
   private boolean hasDownstairs;
   private static final int SECONDARY_AXIS_LEN = 10;
   private static final int Y_AXIS_LEN = 5;
   private static final int MAIN_AXIS_LEN = 5;
   private static final int LOCAL_X_END = 9;
   private static final int LOCAL_Y_END = 4;
   private static final int LOCAL_Z_END = 4;

   public SideRoom(CompoundTag compoundTag) {
      super(StructurePieceTypeModule.SIDE_ROOM, compoundTag);
      this.hasDownstairs = compoundTag.getBoolean("hasDownstairs");
   }

   public SideRoom(int pieceChainLen, BoundingBox blockBox, Direction direction, BetterMineshaftConfiguration config) {
      super(StructurePieceTypeModule.SIDE_ROOM, pieceChainLen, config, blockBox);
      this.setOrientation(direction);
   }

   @Override
   protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag compoundTag) {
      super.addAdditionalSaveData(structurePieceSerializationContext, compoundTag);
      compoundTag.putBoolean("hasDownstairs", this.hasDownstairs);
   }

   public static BoundingBox determineBoxPosition(StructurePieceAccessor structurePiecesHolder, int x, int y, int z, Direction direction) {
      BoundingBox blockBox = BoundingBoxHelper.boxFromCoordsWithRotation(x, y, z, 10, 5, 5, direction);
      StructurePiece intersectingPiece = structurePiecesHolder.findCollisionPiece(blockBox);
      return intersectingPiece != null ? null : blockBox;
   }

   @Override
   public void addChildren(StructurePiece structurePiece, StructurePieceAccessor structurePieceAccessor, RandomSource randomSource) {
      if (randomSource.nextFloat() < BetterMineshaftsCommon.CONFIG.spawnRates.workstationDungeonSpawnRate) {
         Direction direction = this.getOrientation();
         if (direction == null) {
            return;
         }

         StructurePiece newDungeonPiece = null;
         switch (direction) {
            case NORTH:
               newDungeonPiece = BetterMineshaftGenerator.generateAndAddSideRoomDungeonPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.minX() + 6,
                  this.boundingBox.minY() - 4,
                  this.boundingBox.maxZ(),
                  this.getOrientation(),
                  this.genDepth
               );
               break;
            case SOUTH:
               newDungeonPiece = BetterMineshaftGenerator.generateAndAddSideRoomDungeonPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.minX() + 6,
                  this.boundingBox.minY() - 4,
                  this.boundingBox.minZ(),
                  this.getOrientation(),
                  this.genDepth
               );
               break;
            case WEST:
               newDungeonPiece = BetterMineshaftGenerator.generateAndAddSideRoomDungeonPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.maxX(),
                  this.boundingBox.minY() - 4,
                  this.boundingBox.minZ() + 6,
                  this.getOrientation(),
                  this.genDepth
               );
               break;
            case EAST:
               newDungeonPiece = BetterMineshaftGenerator.generateAndAddSideRoomDungeonPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.minX(),
                  this.boundingBox.minY() - 4,
                  this.boundingBox.minZ() + 6,
                  this.getOrientation(),
                  this.genDepth
               );
         }

         if (newDungeonPiece != null) {
            this.hasDownstairs = true;
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
      this.fill(world, box, randomSource, 0, 0, 0, 9, 1, 4, this.config.blockStateRandomizers.brickRandomizer);
      this.chanceReplaceNonAir(world, box, randomSource, 1.0F, 0, 2, 0, 9, 3, 4, this.config.blockStateRandomizers.brickRandomizer);
      this.fill(world, box, 1, 1, 1, 8, 3, 4, AIR);
      boolean[][] ceiling = new boolean[10][5];

      for (int x = 0; x <= 9; x++) {
         for (int z = 0; z <= 4; z++) {
            BlockState currState = this.getBlockAtFixed(world, x, 4, z, box);
            if (currState != null && currState != AIR && currState != Blocks.AIR.defaultBlockState()) {
               this.placeBlock(world, this.config.blockStateRandomizers.brickRandomizer.get(randomSource), x, 4, z, box);
               ceiling[x][z] = true;
            }
         }
      }

      if (!this.hasDownstairs) {
         this.generateLegs(world, randomSource, box);
      }

      if (randomSource.nextInt(2) == 0) {
         this.placeBlock(world, (BlockState)Blocks.FURNACE.defaultBlockState().setValue(FurnaceBlock.FACING, Direction.NORTH), 2, 1, 1, box);
         BlockEntity blockEntity = world.getBlockEntity(this.getWorldPos(2, 1, 1));
         if (blockEntity instanceof FurnaceBlockEntity) {
            ((FurnaceBlockEntity)blockEntity).setItem(1, new ItemStack(Items.COAL, randomSource.nextInt(33)));
         }
      }

      if (randomSource.nextInt(2) == 0) {
         this.placeBlock(world, (BlockState)Blocks.FURNACE.defaultBlockState().setValue(FurnaceBlock.FACING, Direction.NORTH), 1, 1, 1, box);
         BlockEntity blockEntity = world.getBlockEntity(this.getWorldPos(1, 1, 1));
         if (blockEntity instanceof FurnaceBlockEntity) {
            ((FurnaceBlockEntity)blockEntity).setItem(1, new ItemStack(Items.COAL, randomSource.nextInt(33)));
         }
      }

      this.chanceAddBlock(world, randomSource, 0.5F, Blocks.CRAFTING_TABLE.defaultBlockState(), 3, 1, 1, box);
      if (randomSource.nextInt(4) == 0) {
         this.addBarrel(world, box, randomSource, 8, 1, 1, BuiltInLootTables.ABANDONED_MINESHAFT);
      }

      if (this.hasDownstairs) {
         this.placeBlock(world, (BlockState)Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.NORTH), 6, 0, 1, box);
         BlockState trapdoorBlockState = this.config.blockStates.trapdoorBlockState;
         if (trapdoorBlockState.hasProperty(TrapDoorBlock.FACING)) {
            trapdoorBlockState = (BlockState)trapdoorBlockState.setValue(TrapDoorBlock.FACING, Direction.NORTH);
         }

         this.placeBlock(world, trapdoorBlockState, 6, 1, 1, box);
      }

      this.generateIronBarSupports(world, box, randomSource, ceiling);
      this.addBiomeDecorations(world, box, randomSource, 0, 0, 0, 9, 3, 4);
      this.addVines(world, box, randomSource, this.config.decorationChances.vineChance, 1, 0, 1, 8, 4, 3);
   }

   private void generateLegs(WorldGenLevel world, RandomSource randomSource, BoundingBox box) {
      this.generateLeg(world, randomSource, box, 1, 1, this.config.blockStateRandomizers.brickRandomizer);
      this.generateLeg(world, randomSource, box, 1, 3, this.config.blockStateRandomizers.brickRandomizer);
      this.generateLeg(world, randomSource, box, 8, 1, this.config.blockStateRandomizers.brickRandomizer);
      this.generateLeg(world, randomSource, box, 8, 3, this.config.blockStateRandomizers.brickRandomizer);
   }

   private void generateIronBarSupports(WorldGenLevel world, BoundingBox box, RandomSource randomSource, boolean[][] ceiling) {
      List<Integer> invalidXs = new ArrayList<>();

      for (int z = 2; z <= 3; z++) {
         for (int x = 2; x <= 7; x++) {
            if (!invalidXs.contains(x) && ceiling[x][z] && randomSource.nextInt(5) == 0) {
               this.fill(world, box, x, 1, z, x, 3, z, Blocks.IRON_BARS.defaultBlockState());
               invalidXs.add(x);
               x++;
            }
         }
      }
   }
}
