package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.yungnickyoung.minecraft.bettermineshafts.module.StructurePieceTypeModule;
import com.yungnickyoung.minecraft.bettermineshafts.world.config.BetterMineshaftConfiguration;
import com.yungnickyoung.minecraft.yungsapi.world.util.BoundingBoxHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class ZombieVillagerRoom extends BetterMineshaftPiece {
   private static final int SECONDARY_AXIS_LEN = 7;
   private static final int Y_AXIS_LEN = 5;
   private static final int MAIN_AXIS_LEN = 7;
   private static final int LOCAL_X_END = 6;
   private static final int LOCAL_Y_END = 4;
   private static final int LOCAL_Z_END = 6;

   public ZombieVillagerRoom(CompoundTag compoundTag) {
      super(StructurePieceTypeModule.ZOMBIE_VILLAGER_ROOM, compoundTag);
   }

   public ZombieVillagerRoom(int chunkPieceLen, BoundingBox blockBox, Direction direction, BetterMineshaftConfiguration config) {
      super(StructurePieceTypeModule.ZOMBIE_VILLAGER_ROOM, chunkPieceLen, config, blockBox);
      this.setOrientation(direction);
   }

   @Override
   protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag compoundTag) {
      super.addAdditionalSaveData(structurePieceSerializationContext, compoundTag);
   }

   public static BoundingBox determineBoxPosition(StructurePieceAccessor structurePieceAccessor, int x, int y, int z, Direction direction) {
      BoundingBox blockBox = BoundingBoxHelper.boxFromCoordsWithRotation(x, y, z, 7, 5, 7, direction);
      StructurePiece intersectingPiece = structurePieceAccessor.findCollisionPiece(blockBox);
      return intersectingPiece != null ? null : blockBox;
   }

   @Override
   public void addChildren(StructurePiece structurePiece, StructurePieceAccessor structurePieceAccessor, RandomSource randomSource) {
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
      this.fill(world, box, 1, 0, 0, 5, 2, 0, Blocks.STONE.defaultBlockState());
      this.fill(world, box, 0, 0, 1, 0, 2, 5, Blocks.STONE.defaultBlockState());
      this.fill(world, box, 6, 0, 1, 6, 2, 5, Blocks.STONE.defaultBlockState());
      this.fill(world, box, 1, 0, 6, 5, 2, 6, Blocks.STONE.defaultBlockState());
      this.chanceReplaceNonAir(world, box, randomSource, 0.4F, 1, 0, 0, 5, 2, 0, Blocks.COBBLESTONE.defaultBlockState());
      this.chanceReplaceNonAir(world, box, randomSource, 0.4F, 0, 0, 1, 0, 2, 5, Blocks.COBBLESTONE.defaultBlockState());
      this.chanceReplaceNonAir(world, box, randomSource, 0.4F, 6, 0, 1, 6, 2, 5, Blocks.COBBLESTONE.defaultBlockState());
      this.chanceReplaceNonAir(world, box, randomSource, 0.4F, 1, 0, 6, 5, 2, 6, Blocks.COBBLESTONE.defaultBlockState());
      this.chanceReplaceNonAir(world, box, randomSource, 0.1F, 1, 0, 0, 5, 2, 0, Blocks.STONE_BRICKS.defaultBlockState());
      this.chanceReplaceNonAir(world, box, randomSource, 0.1F, 0, 0, 1, 0, 2, 5, Blocks.STONE_BRICKS.defaultBlockState());
      this.chanceReplaceNonAir(world, box, randomSource, 0.1F, 6, 0, 1, 6, 2, 5, Blocks.STONE_BRICKS.defaultBlockState());
      this.chanceReplaceNonAir(world, box, randomSource, 0.1F, 1, 0, 6, 5, 2, 6, Blocks.STONE_BRICKS.defaultBlockState());
      this.fill(world, box, 2, 3, 0, 4, 3, 0, Blocks.STONE_SLAB.defaultBlockState());
      this.fill(world, box, 0, 3, 2, 0, 3, 4, Blocks.STONE_SLAB.defaultBlockState());
      this.fill(world, box, 6, 3, 2, 6, 3, 4, Blocks.STONE_SLAB.defaultBlockState());
      this.fill(world, box, 2, 3, 6, 4, 3, 6, Blocks.STONE_SLAB.defaultBlockState());
      this.chanceFill(world, box, randomSource, 0.5F, 2, 3, 0, 4, 3, 0, Blocks.COBBLESTONE_SLAB.defaultBlockState());
      this.chanceFill(world, box, randomSource, 0.5F, 0, 3, 2, 0, 3, 4, Blocks.COBBLESTONE_SLAB.defaultBlockState());
      this.chanceFill(world, box, randomSource, 0.5F, 6, 3, 2, 6, 3, 4, Blocks.COBBLESTONE_SLAB.defaultBlockState());
      this.chanceFill(world, box, randomSource, 0.5F, 2, 3, 6, 4, 3, 6, Blocks.COBBLESTONE_SLAB.defaultBlockState());
      this.fill(
         world,
         box,
         2,
         3,
         1,
         4,
         3,
         1,
         (BlockState)((BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH))
            .setValue(StairBlock.HALF, Half.TOP)
      );
      this.fill(
         world,
         box,
         1,
         3,
         2,
         1,
         3,
         4,
         (BlockState)((BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST))
            .setValue(StairBlock.HALF, Half.TOP)
      );
      this.fill(
         world,
         box,
         2,
         3,
         5,
         4,
         3,
         5,
         (BlockState)((BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH))
            .setValue(StairBlock.HALF, Half.TOP)
      );
      this.fill(
         world,
         box,
         5,
         3,
         2,
         5,
         3,
         4,
         (BlockState)((BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST))
            .setValue(StairBlock.HALF, Half.TOP)
      );
      this.placeBlock(
         world,
         (BlockState)((BlockState)((BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT))
               .setValue(StairBlock.HALF, Half.TOP))
            .setValue(StairBlock.FACING, Direction.SOUTH),
         1,
         3,
         1,
         box
      );
      this.placeBlock(
         world,
         (BlockState)((BlockState)((BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.SHAPE, StairsShape.INNER_LEFT))
               .setValue(StairBlock.HALF, Half.TOP))
            .setValue(StairBlock.FACING, Direction.NORTH),
         1,
         3,
         5,
         box
      );
      this.placeBlock(
         world,
         (BlockState)((BlockState)((BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.SHAPE, StairsShape.INNER_LEFT))
               .setValue(StairBlock.HALF, Half.TOP))
            .setValue(StairBlock.FACING, Direction.SOUTH),
         5,
         3,
         1,
         box
      );
      this.placeBlock(
         world,
         (BlockState)((BlockState)((BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT))
               .setValue(StairBlock.HALF, Half.TOP))
            .setValue(StairBlock.FACING, Direction.NORTH),
         5,
         3,
         5,
         box
      );
      this.fill(world, box, 2, 4, 2, 4, 4, 4, Blocks.STONE_SLAB.defaultBlockState());
      this.chanceFill(world, box, randomSource, 0.5F, 2, 4, 2, 4, 4, 4, Blocks.COBBLESTONE_SLAB.defaultBlockState());
      this.placeBlock(world, AIR, 3, 4, 3, box);
      this.placeBlock(world, (BlockState)Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP), 3, 4, 3, box);
      this.fill(world, box, 1, 0, 1, 5, 0, 5, Blocks.STONE.defaultBlockState());
      this.chanceFill(world, box, randomSource, 0.4F, 1, 0, 1, 5, 0, 5, Blocks.COBBLESTONE.defaultBlockState());
      this.chanceFill(world, box, randomSource, 0.1F, 1, 0, 1, 5, 0, 5, Blocks.STONE_BRICKS.defaultBlockState());
      this.fill(world, box, 1, 1, 1, 5, 2, 5, AIR);
      this.fill(world, box, 2, 3, 2, 4, 3, 4, AIR);
      this.fill(world, box, 3, 1, 0, 3, 2, 0, AIR);
      this.placeBlock(
         world,
         (BlockState)((BlockState)Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.NORTH))
            .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER),
         3,
         1,
         0,
         box
      );
      this.placeBlock(
         world,
         (BlockState)((BlockState)Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.NORTH))
            .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER),
         3,
         2,
         0,
         box
      );
      this.fill(world, box, 6, 2, 2, 6, 2, 4, Blocks.IRON_BARS.defaultBlockState());
      this.placeBlock(
         world,
         (BlockState)((BlockState)Blocks.BLACK_BED.defaultBlockState().setValue(BedBlock.FACING, Direction.NORTH)).setValue(BedBlock.PART, BedPart.FOOT),
         1,
         1,
         4,
         box
      );
      this.placeBlock(
         world,
         (BlockState)((BlockState)Blocks.BLACK_BED.defaultBlockState().setValue(BedBlock.FACING, Direction.NORTH)).setValue(BedBlock.PART, BedPart.HEAD),
         1,
         1,
         5,
         box
      );
      this.placeBlock(
         world,
         (BlockState)((BlockState)Blocks.BLACK_BED.defaultBlockState().setValue(BedBlock.FACING, Direction.NORTH)).setValue(BedBlock.PART, BedPart.FOOT),
         5,
         1,
         4,
         box
      );
      this.placeBlock(
         world,
         (BlockState)((BlockState)Blocks.BLACK_BED.defaultBlockState().setValue(BedBlock.FACING, Direction.NORTH)).setValue(BedBlock.PART, BedPart.HEAD),
         5,
         1,
         5,
         box
      );
      BlockPos spawnerPos = this.getWorldPos(3, 0, 3);
      world.setBlock(spawnerPos, Blocks.SPAWNER.defaultBlockState(), 2);
      BlockEntity blockEntity = world.getBlockEntity(spawnerPos);
      if (blockEntity instanceof SpawnerBlockEntity) {
         ((SpawnerBlockEntity)blockEntity).setEntityId(EntityType.ZOMBIE_VILLAGER, randomSource);
      }

      this.placeBlock(world, Blocks.COBBLESTONE_WALL.defaultBlockState(), 1, 1, 1, box);
      this.placeBlock(world, Blocks.REDSTONE_TORCH.defaultBlockState(), 1, 2, 1, box);
      this.addBarrel(world, box, randomSource, 5, 1, 1, BuiltInLootTables.ABANDONED_MINESHAFT);
      this.placeBlock(world, Blocks.STONE_BUTTON.defaultBlockState(), 2, 2, 1, box);
      this.placeBlock(world, Blocks.ANVIL.defaultBlockState(), 5, 1, 2, box);
      if (randomSource.nextFloat() < 0.33F) {
         this.placeBlock(world, Blocks.SMITHING_TABLE.defaultBlockState(), 2, 1, 5, box);
      } else if (randomSource.nextFloat() < 0.67F) {
         this.placeBlock(world, Blocks.CRAFTING_TABLE.defaultBlockState(), 2, 1, 5, box);
      } else {
         this.placeBlock(world, Blocks.BLAST_FURNACE.defaultBlockState(), 2, 1, 5, box);
      }

      this.chanceFill(world, box, randomSource, 0.3F, 2, 3, 2, 4, 3, 4, Blocks.COBWEB.defaultBlockState());
   }
}
