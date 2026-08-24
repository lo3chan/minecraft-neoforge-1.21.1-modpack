package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.yungnickyoung.minecraft.bettermineshafts.mixin.BoundingBoxAccessor;
import com.yungnickyoung.minecraft.bettermineshafts.module.StructurePieceTypeModule;
import com.yungnickyoung.minecraft.bettermineshafts.world.config.BetterMineshaftConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class SideRoomDungeon extends BetterMineshaftPiece {
   private static final int SECONDARY_AXIS_LEN = 9;
   private static final int Y_AXIS_LEN = 4;
   private static final int MAIN_AXIS_LEN = 9;
   private static final int LOCAL_X_END = 8;
   private static final int LOCAL_Y_END = 3;
   private static final int LOCAL_Z_END = 8;

   public SideRoomDungeon(CompoundTag compoundTag) {
      super(StructurePieceTypeModule.SIDE_ROOM_DUNGEON, compoundTag);
   }

   public SideRoomDungeon(int pieceChainLen, BoundingBox blockBox, Direction direction, BetterMineshaftConfiguration config) {
      super(StructurePieceTypeModule.SIDE_ROOM_DUNGEON, pieceChainLen, config, blockBox);
      this.setOrientation(direction);
   }

   @Override
   protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag compoundTag) {
      super.addAdditionalSaveData(structurePieceSerializationContext, compoundTag);
   }

   public static BoundingBox determineBoxPosition(StructurePieceAccessor structurePieceAccessor, int x, int y, int z, Direction direction) {
      BoundingBox blockBox = new BoundingBox(x, y, z, x, y + 4 - 1, z);
      switch (direction) {
         case NORTH:
         default:
            ((BoundingBoxAccessor)blockBox).setMaxX(x + 4);
            ((BoundingBoxAccessor)blockBox).setMinX(x - 4);
            ((BoundingBoxAccessor)blockBox).setMinZ(z - 8);
            break;
         case SOUTH:
            ((BoundingBoxAccessor)blockBox).setMaxX(x + 4);
            ((BoundingBoxAccessor)blockBox).setMinX(x - 4);
            ((BoundingBoxAccessor)blockBox).setMaxZ(z + 8);
            break;
         case WEST:
            ((BoundingBoxAccessor)blockBox).setMinX(x - 8);
            ((BoundingBoxAccessor)blockBox).setMaxZ(z + 4);
            ((BoundingBoxAccessor)blockBox).setMinZ(z - 4);
            break;
         case EAST:
            ((BoundingBoxAccessor)blockBox).setMaxX(x + 8);
            ((BoundingBoxAccessor)blockBox).setMaxZ(z + 4);
            ((BoundingBoxAccessor)blockBox).setMinZ(z - 4);
      }

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
      this.fill(world, box, randomSource, 0, 0, 0, 8, 3, 8, this.config.blockStateRandomizers.brickRandomizer);
      this.fill(world, box, 1, 1, 1, 7, 2, 7, AIR);
      this.generateLegs(world, randomSource, box);
      BlockState LADDER = (BlockState)Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.NORTH);
      this.fill(world, box, 4, 1, 1, 4, 3, 1, LADDER);
      BlockPos spawnerPos = this.getWorldPos(4, 1, 5);
      world.setBlock(spawnerPos, Blocks.SPAWNER.defaultBlockState(), 2);
      BlockEntity blockEntity = world.getBlockEntity(spawnerPos);
      if (blockEntity instanceof SpawnerBlockEntity) {
         ((SpawnerBlockEntity)blockEntity).setEntityId(EntityType.CAVE_SPIDER, randomSource);
      }

      this.chanceReplaceAir(world, box, randomSource, 0.9F, 3, 1, 4, 5, 2, 6, Blocks.COBWEB.defaultBlockState());
      this.chanceReplaceAir(world, box, randomSource, 0.1F, 1, 1, 1, 7, 2, 8, Blocks.COBWEB.defaultBlockState());
      this.createChest(world, box, randomSource, 1, 1, 7, BuiltInLootTables.ABANDONED_MINESHAFT);
      if (randomSource.nextInt(2) == 0) {
         this.createChest(world, box, randomSource, 7, 1, 7, BuiltInLootTables.ABANDONED_MINESHAFT);
      }

      this.addBiomeDecorations(world, box, randomSource, 0, 0, 0, 8, 2, 8);
   }

   private void generateLegs(WorldGenLevel world, RandomSource randomSource, BoundingBox box) {
      this.generateLeg(world, randomSource, box, 1, 1, this.config.blockStateRandomizers.brickRandomizer);
      this.generateLeg(world, randomSource, box, 1, 7, this.config.blockStateRandomizers.brickRandomizer);
      this.generateLeg(world, randomSource, box, 7, 1, this.config.blockStateRandomizers.brickRandomizer);
      this.generateLeg(world, randomSource, box, 7, 7, this.config.blockStateRandomizers.brickRandomizer);
   }
}
