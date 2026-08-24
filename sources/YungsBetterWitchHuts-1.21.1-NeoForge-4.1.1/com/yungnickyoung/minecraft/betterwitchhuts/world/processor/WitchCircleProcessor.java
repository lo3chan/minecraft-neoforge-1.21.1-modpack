package com.yungnickyoung.minecraft.betterwitchhuts.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterwitchhuts.module.StructureProcessorTypeModule;
import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
public class WitchCircleProcessor extends StructureProcessor {
   public static final WitchCircleProcessor INSTANCE = new WitchCircleProcessor();
   public static final MapCodec<WitchCircleProcessor> CODEC = MapCodec.unit(() -> INSTANCE);
   private static final BlockStateRandomizer BRICKS_RANDOMIZER = new BlockStateRandomizer(Blocks.STONE_BRICKS.defaultBlockState())
      .addBlock(Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), 0.6F)
      .addBlock(Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 0.1F);
   private static final BlockStateRandomizer STONE_RANDOMIZER = new BlockStateRandomizer(Blocks.COBBLESTONE.defaultBlockState())
      .addBlock(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 0.6F)
      .addBlock(Blocks.COARSE_DIRT.defaultBlockState(), 0.1F);
   private static final BlockStateRandomizer STAIRS_RANDOMIZER = new BlockStateRandomizer(Blocks.STONE_BRICK_STAIRS.defaultBlockState())
      .addBlock(Blocks.MOSSY_STONE_BRICK_STAIRS.defaultBlockState(), 0.6F);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      RandomSource randomSource = structurePlacementData.getRandom(blockInfoGlobal.pos());
      if (blockInfoGlobal.state().getBlock() == Blocks.STONE_BRICKS) {
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), BRICKS_RANDOMIZER.get(randomSource), blockInfoGlobal.nbt());
      } else if (blockInfoGlobal.state().getBlock() == Blocks.MOSSY_COBBLESTONE) {
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), STONE_RANDOMIZER.get(randomSource), blockInfoGlobal.nbt());
      } else if (blockInfoGlobal.state().getBlock() == Blocks.STONE_BRICK_STAIRS) {
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), STAIRS_RANDOMIZER.get(randomSource), blockInfoGlobal.nbt());
      } else if (blockInfoGlobal.state().getBlock() == Blocks.GRAY_STAINED_GLASS) {
         if (levelReader instanceof WorldGenRegion worldGenRegion && !worldGenRegion.getCenter().equals(new ChunkPos(blockInfoGlobal.pos()))) {
            return blockInfoGlobal;
         }

         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), BRICKS_RANDOMIZER.get(randomSource), blockInfoGlobal.nbt());
         MutableBlockPos mutable = blockInfoGlobal.pos().mutable().move(Direction.DOWN);

         for (BlockState currBlockState = levelReader.getBlockState(mutable);
            mutable.getY() > levelReader.getMinBuildHeight()
               && mutable.getY() < levelReader.getMaxBuildHeight()
               && (currBlockState.isAir() || !levelReader.getFluidState(mutable).isEmpty());
            currBlockState = levelReader.getBlockState(mutable)
         ) {
            levelReader.getChunk(mutable).setBlockState(mutable, BRICKS_RANDOMIZER.get(randomSource), false);
            mutable.move(Direction.DOWN);
         }
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.WITCH_CIRCLE_PROCESSOR;
   }
}
