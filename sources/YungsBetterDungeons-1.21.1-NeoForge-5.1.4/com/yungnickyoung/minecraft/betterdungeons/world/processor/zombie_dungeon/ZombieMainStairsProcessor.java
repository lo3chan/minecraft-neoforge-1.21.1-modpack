package com.yungnickyoung.minecraft.betterdungeons.world.processor.zombie_dungeon;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterdungeons.BetterDungeonsCommon;
import com.yungnickyoung.minecraft.betterdungeons.module.StructureProcessorTypeModule;
import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import com.yungnickyoung.minecraft.yungsapi.world.structure.processor.ISafeWorldModifier;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ZombieMainStairsProcessor extends StructureProcessor implements ISafeWorldModifier {
   public static final ZombieMainStairsProcessor INSTANCE = new ZombieMainStairsProcessor();
   public static final MapCodec<ZombieMainStairsProcessor> CODEC = MapCodec.unit(() -> INSTANCE);
   private static final BlockStateRandomizer STAIR_SELECTOR = new BlockStateRandomizer(Blocks.COBBLESTONE_STAIRS.defaultBlockState())
      .addBlock(Blocks.MOSSY_COBBLESTONE_STAIRS.defaultBlockState(), 0.4F)
      .addBlock(Blocks.COBBLESTONE_SLAB.defaultBlockState(), 0.1F)
      .addBlock(Blocks.MOSSY_COBBLESTONE_SLAB.defaultBlockState(), 0.1F)
      .addBlock(Blocks.CAVE_AIR.defaultBlockState(), 0.1F)
      .addBlock(Blocks.COBBLESTONE.defaultBlockState(), 0.1F)
      .addBlock(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 0.1F);
   private static final BlockStateRandomizer COBBLE_SELECTOR = new BlockStateRandomizer(Blocks.COBBLESTONE.defaultBlockState())
      .addBlock(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 0.3F);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().getBlock() == Blocks.WARPED_STAIRS) {
         MutableBlockPos temp = blockInfoGlobal.pos().mutable();
         Direction facing = structurePlacementData.getRotation().rotate((Direction)blockInfoGlobal.state().getValue(StairBlock.FACING));
         Rotation rotation = structurePlacementData.getRotation().getRotated(Rotation.CLOCKWISE_180);
         int maxLength = BetterDungeonsCommon.CONFIG.zombieDungeons.zombieDungeonMaxSurfaceStaircaseLength;
         BlockPos maxSurfacePos = blockInfoGlobal.pos().relative(facing, maxLength).relative(Direction.UP, maxLength);
         temp.move(facing, maxLength);
         int surfaceHeight = levelReader.getHeight(Types.WORLD_SURFACE_WG, temp.getX(), temp.getZ());
         if (surfaceHeight >= maxSurfacePos.getY() || surfaceHeight <= blockInfoGlobal.pos().getY()) {
            return new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.CAVE_AIR.defaultBlockState(), null);
         }

         RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos());
         MutableBlockPos leftPos = new BlockPos(blockInfoGlobal.pos().relative(facing.getCounterClockWise())).mutable();
         MutableBlockPos middlePos = new BlockPos(blockInfoGlobal.pos()).mutable();
         MutableBlockPos rightPos = new BlockPos(blockInfoGlobal.pos().relative(facing.getClockWise())).mutable();

         for (int i = 0; i < maxLength; i++) {
            int middleSurfaceHeight = levelReader.getHeight(Types.WORLD_SURFACE_WG, middlePos.getX(), middlePos.getZ());
            if (middleSurfaceHeight < middlePos.getY()) {
               break;
            }

            BlockState tempBlock = STAIR_SELECTOR.get(random);
            if (!this.isBlockStateAirSafe(levelReader, leftPos)) {
               if (this.isMaterialLiquidSafe(levelReader, leftPos.relative(facing))) {
                  this.setBlockStateSafeWithPlacement(
                     levelReader, Blocks.COBBLESTONE.defaultBlockState(), leftPos, structurePlacementData.getMirror(), rotation
                  );
               } else {
                  this.setBlockStateSafeWithPlacement(levelReader, tempBlock, leftPos, structurePlacementData.getMirror(), rotation);
               }
            }

            tempBlock = STAIR_SELECTOR.get(random);
            if (!this.isBlockStateAirSafe(levelReader, middlePos)) {
               if (this.isMaterialLiquidSafe(levelReader, middlePos.relative(facing))) {
                  this.setBlockStateSafeWithPlacement(
                     levelReader, Blocks.COBBLESTONE.defaultBlockState(), middlePos, structurePlacementData.getMirror(), rotation
                  );
               } else {
                  this.setBlockStateSafeWithPlacement(levelReader, tempBlock, middlePos, structurePlacementData.getMirror(), rotation);
               }
            }

            tempBlock = STAIR_SELECTOR.get(random);
            if (!this.isBlockStateAirSafe(levelReader, rightPos)) {
               if (this.isMaterialLiquidSafe(levelReader, rightPos.relative(facing))) {
                  this.setBlockStateSafeWithPlacement(
                     levelReader, Blocks.COBBLESTONE.defaultBlockState(), rightPos, structurePlacementData.getMirror(), rotation
                  );
               } else {
                  this.setBlockStateSafeWithPlacement(levelReader, tempBlock, rightPos, structurePlacementData.getMirror(), rotation);
               }
            }

            for (int y = middlePos.getY() + 1; y <= middlePos.getY() + 3; y++) {
               temp.set(leftPos.getX(), y, leftPos.getZ());
               this.setBlockStateSafeWithPlacement(levelReader, Blocks.CAVE_AIR.defaultBlockState(), temp, structurePlacementData.getMirror(), rotation);
               temp.set(middlePos.getX(), y, middlePos.getZ());
               this.setBlockStateSafeWithPlacement(levelReader, Blocks.CAVE_AIR.defaultBlockState(), temp, structurePlacementData.getMirror(), rotation);
               temp.set(rightPos.getX(), y, rightPos.getZ());
               this.setBlockStateSafeWithPlacement(levelReader, Blocks.CAVE_AIR.defaultBlockState(), temp, structurePlacementData.getMirror(), rotation);
            }

            float cobbleChance = (float)(maxLength - i) / maxLength;
            cobbleChance = Math.max(cobbleChance, 0.25F);
            temp.set(leftPos.getX(), leftPos.getY() + 4, leftPos.getZ());
            Optional<BlockState> tempOptional = this.getBlockStateSafe(levelReader, temp);
            if (tempOptional.isEmpty() || tempOptional.get().liquid() || random.nextFloat() < cobbleChance && tempOptional.get().isSolid()) {
               this.setBlockStateSafeWithPlacement(levelReader, COBBLE_SELECTOR.get(random), temp, structurePlacementData.getMirror(), rotation);
            }

            temp.set(middlePos.getX(), middlePos.getY() + 4, middlePos.getZ());
            tempOptional = this.getBlockStateSafe(levelReader, temp);
            if (tempOptional.isEmpty() || tempOptional.get().liquid() || random.nextFloat() < cobbleChance && tempOptional.get().isSolid()) {
               this.setBlockStateSafeWithPlacement(levelReader, COBBLE_SELECTOR.get(random), temp, structurePlacementData.getMirror(), rotation);
            }

            temp.set(rightPos.getX(), rightPos.getY() + 4, rightPos.getZ());
            tempOptional = this.getBlockStateSafe(levelReader, temp);
            if (tempOptional.isEmpty() || tempOptional.get().liquid() || random.nextFloat() < cobbleChance && tempOptional.get().isSolid()) {
               this.setBlockStateSafeWithPlacement(levelReader, COBBLE_SELECTOR.get(random), temp, structurePlacementData.getMirror(), rotation);
            }

            temp.set(leftPos.relative(facing.getCounterClockWise()));

            for (int y = 0; y <= 4; y++) {
               tempOptional = this.getBlockStateSafe(levelReader, temp);
               if (tempOptional.isEmpty() || tempOptional.get().liquid() || random.nextFloat() < cobbleChance && tempOptional.get().isSolid()) {
                  this.setBlockStateSafeWithPlacement(levelReader, COBBLE_SELECTOR.get(random), temp, structurePlacementData.getMirror(), rotation);
               }

               temp.move(Direction.UP);
            }

            temp.set(rightPos.relative(facing.getClockWise()));

            for (int y = 0; y <= 4; y++) {
               tempOptional = this.getBlockStateSafe(levelReader, temp);
               if (tempOptional.isEmpty() || tempOptional.get().liquid() || random.nextFloat() < cobbleChance && tempOptional.get().isSolid()) {
                  this.setBlockStateSafeWithPlacement(levelReader, COBBLE_SELECTOR.get(random), temp, structurePlacementData.getMirror(), rotation);
               }

               temp.move(Direction.UP);
            }

            leftPos.move(facing).move(Direction.UP);
            middlePos.move(facing).move(Direction.UP);
            rightPos.move(facing).move(Direction.UP);
         }

         leftPos.move(facing.getOpposite()).move(Direction.DOWN);
         middlePos.move(facing.getOpposite()).move(Direction.DOWN);
         rightPos.move(facing.getOpposite()).move(Direction.DOWN);
         BlockStateRandomizer tombSelector = new BlockStateRandomizer(Blocks.COBBLESTONE.defaultBlockState())
            .addBlock(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 0.4F);
         this.setBlockStateSafeWithPlacement(
            levelReader, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), leftPos.relative(Direction.UP, 2), structurePlacementData.getMirror(), rotation
         );
         this.setBlockStateSafeWithPlacement(
            levelReader, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), middlePos.relative(Direction.UP, 2), structurePlacementData.getMirror(), rotation
         );
         this.setBlockStateSafeWithPlacement(
            levelReader, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), rightPos.relative(Direction.UP, 2), structurePlacementData.getMirror(), rotation
         );
         this.setBlockStateSafeWithPlacement(
            levelReader, Blocks.SMOOTH_STONE.defaultBlockState(), leftPos.relative(Direction.UP, 3), structurePlacementData.getMirror(), rotation
         );
         this.setBlockStateSafeWithPlacement(
            levelReader, Blocks.SMOOTH_STONE.defaultBlockState(), middlePos.relative(Direction.UP, 3), structurePlacementData.getMirror(), rotation
         );
         this.setBlockStateSafeWithPlacement(
            levelReader, Blocks.SMOOTH_STONE.defaultBlockState(), rightPos.relative(Direction.UP, 3), structurePlacementData.getMirror(), rotation
         );
         BlockState lanternBlock = BetterDungeonsCommon.CONFIG.general.enableNetherBlocks
            ? (BlockState)Blocks.SOUL_LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true)
            : (BlockState)Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true);
         if (random.nextFloat() < 0.25F) {
            this.setBlockStateSafeWithPlacement(levelReader, lanternBlock, leftPos.relative(Direction.UP, 1), structurePlacementData.getMirror(), rotation);
         } else if (random.nextFloat() < 0.25F) {
            this.setBlockStateSafeWithPlacement(levelReader, lanternBlock, rightPos.relative(Direction.UP, 1), structurePlacementData.getMirror(), rotation);
         }

         leftPos.move(facing.getCounterClockWise());
         rightPos.move(facing.getClockWise());
         this.setColumn(levelReader, tombSelector, leftPos.relative(Direction.DOWN), random);
         this.setColumn(levelReader, tombSelector, rightPos.relative(Direction.DOWN), random);
         this.setBlockStateSafeWithPlacement(levelReader, Blocks.POLISHED_ANDESITE.defaultBlockState(), leftPos, structurePlacementData.getMirror(), rotation);
         this.setBlockStateSafeWithPlacement(levelReader, Blocks.POLISHED_ANDESITE.defaultBlockState(), rightPos, structurePlacementData.getMirror(), rotation);
         leftPos.move(Direction.UP);
         rightPos.move(Direction.UP);
         this.setBlockStateSafeWithPlacement(levelReader, Blocks.POLISHED_ANDESITE.defaultBlockState(), leftPos, structurePlacementData.getMirror(), rotation);
         this.setBlockStateSafeWithPlacement(levelReader, Blocks.POLISHED_ANDESITE.defaultBlockState(), rightPos, structurePlacementData.getMirror(), rotation);
         leftPos.move(Direction.UP);
         rightPos.move(Direction.UP);
         this.setBlockStateSafeWithPlacement(levelReader, Blocks.SMOOTH_STONE.defaultBlockState(), leftPos, structurePlacementData.getMirror(), rotation);
         this.setBlockStateSafeWithPlacement(levelReader, Blocks.SMOOTH_STONE.defaultBlockState(), rightPos, structurePlacementData.getMirror(), rotation);
         leftPos.move(Direction.UP);
         rightPos.move(Direction.UP);
         this.setBlockStateRandom(levelReader, Blocks.SMOOTH_STONE.defaultBlockState(), leftPos, structurePlacementData.getMirror(), rotation, random, 0.5F);
         this.setBlockStateRandom(levelReader, Blocks.SMOOTH_STONE.defaultBlockState(), rightPos, structurePlacementData.getMirror(), rotation, random, 0.5F);
         leftPos.move(Direction.DOWN).move(Direction.DOWN).move(Direction.DOWN).move(facing.getOpposite());
         rightPos.move(Direction.DOWN).move(Direction.DOWN).move(Direction.DOWN).move(facing.getOpposite());
         this.setColumn(levelReader, tombSelector, leftPos.relative(Direction.DOWN), random);
         this.setColumn(levelReader, tombSelector, rightPos.relative(Direction.DOWN), random);
         this.setBlockStateSafeWithPlacement(levelReader, tombSelector.get(random), leftPos, structurePlacementData.getMirror(), rotation);
         this.setBlockStateSafeWithPlacement(levelReader, tombSelector.get(random), rightPos, structurePlacementData.getMirror(), rotation);
         leftPos.move(Direction.UP);
         rightPos.move(Direction.UP);
         this.setBlockStateSafeWithPlacement(levelReader, tombSelector.get(random), leftPos, structurePlacementData.getMirror(), rotation);
         this.setBlockStateSafeWithPlacement(levelReader, tombSelector.get(random), rightPos, structurePlacementData.getMirror(), rotation);
         leftPos.move(Direction.UP);
         rightPos.move(Direction.UP);
         this.setBlockStateRandom(levelReader, tombSelector.get(random), leftPos, structurePlacementData.getMirror(), rotation, random, 0.5F);
         this.setBlockStateRandom(levelReader, tombSelector.get(random), rightPos, structurePlacementData.getMirror(), rotation, random, 0.5F);
         leftPos.move(Direction.DOWN).move(Direction.DOWN).move(facing.getOpposite());
         rightPos.move(Direction.DOWN).move(Direction.DOWN).move(facing.getOpposite());
         this.setColumn(levelReader, tombSelector, leftPos.relative(Direction.DOWN), random);
         this.setColumn(levelReader, tombSelector, rightPos.relative(Direction.DOWN), random);
         this.setBlockStateSafeWithPlacement(levelReader, tombSelector.get(random), leftPos, structurePlacementData.getMirror(), rotation);
         this.setBlockStateSafeWithPlacement(levelReader, tombSelector.get(random), rightPos, structurePlacementData.getMirror(), rotation);
         leftPos.move(Direction.UP);
         rightPos.move(Direction.UP);
         this.setBlockStateRandom(levelReader, tombSelector.get(random), leftPos, structurePlacementData.getMirror(), rotation, random, 0.5F);
         this.setBlockStateSafeWithPlacement(levelReader, tombSelector.get(random), rightPos, structurePlacementData.getMirror(), rotation);
         leftPos.move(Direction.UP);
         rightPos.move(Direction.UP);
         this.setBlockStateRandom(levelReader, tombSelector.get(random), rightPos, structurePlacementData.getMirror(), rotation, random, 0.5F);
         leftPos.move(Direction.DOWN).move(Direction.DOWN).move(facing.getOpposite());
         rightPos.move(Direction.DOWN).move(Direction.DOWN).move(facing.getOpposite());
         this.setColumn(levelReader, tombSelector, leftPos.relative(Direction.DOWN), random);
         this.setColumn(levelReader, tombSelector, rightPos.relative(Direction.DOWN), random);
         this.setBlockStateRandom(levelReader, tombSelector.get(random), leftPos, structurePlacementData.getMirror(), rotation, random, 0.5F);
         this.setBlockStateSafeWithPlacement(levelReader, tombSelector.get(random), rightPos, structurePlacementData.getMirror(), rotation);
         leftPos.move(Direction.UP);
         rightPos.move(Direction.UP);
         this.setBlockStateRandom(levelReader, tombSelector.get(random), rightPos, structurePlacementData.getMirror(), rotation, random, 0.5F);
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), STAIR_SELECTOR.get(random), blockInfoGlobal.nbt());
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.ZOMBIE_MAIN_STAIRS_PROCESSOR;
   }

   private void setBlockStateSafeWithPlacement(LevelReader levelReader, BlockState blockState, BlockPos pos, Mirror mirror, Rotation rotation) {
      if (mirror != Mirror.NONE) {
         blockState = blockState.mirror(mirror);
      }

      if (rotation != Rotation.NONE) {
         blockState = blockState.rotate(rotation);
      }

      this.setBlockStateSafe(levelReader, pos, blockState);
   }

   private void setBlockStateRandom(
      LevelReader levelReader, BlockState blockState, BlockPos pos, Mirror mirror, Rotation rotation, RandomSource random, float chance
   ) {
      if (random.nextFloat() < chance) {
         this.setBlockStateSafeWithPlacement(levelReader, blockState, pos, mirror, rotation);
      }
   }

   private void setColumn(LevelReader levelReader, BlockStateRandomizer selector, BlockPos pos, RandomSource random) {
      MutableBlockPos mutable = pos.mutable();

      for (Optional<BlockState> currBlock = this.getBlockStateSafe(levelReader, mutable);
         mutable.getY() > levelReader.getMinBuildHeight() && (currBlock.isEmpty() || currBlock.get().isAir() || currBlock.get().liquid());
         currBlock = this.getBlockStateSafe(levelReader, mutable)
      ) {
         this.setBlockStateSafe(levelReader, mutable, selector.get(random));
         mutable.move(Direction.DOWN);
      }
   }
}
