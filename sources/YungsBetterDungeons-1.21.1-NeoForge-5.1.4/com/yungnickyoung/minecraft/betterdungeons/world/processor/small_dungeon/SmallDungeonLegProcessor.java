package com.yungnickyoung.minecraft.betterdungeons.world.processor.small_dungeon;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterdungeons.module.StructureProcessorTypeModule;
import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
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
@MethodsReturnNonnullByDefault
public class SmallDungeonLegProcessor extends StructureProcessor {
   public static final SmallDungeonLegProcessor INSTANCE = new SmallDungeonLegProcessor();
   public static final MapCodec<SmallDungeonLegProcessor> CODEC = MapCodec.unit(() -> INSTANCE);
   private static final BlockStateRandomizer STONE_BRICK_SELECTOR = new BlockStateRandomizer(Blocks.STONE_BRICKS.defaultBlockState())
      .addBlock(Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), 0.5F)
      .addBlock(Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 0.2F);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().getBlock() == Blocks.YELLOW_STAINED_GLASS) {
         if (levelReader instanceof WorldGenRegion worldGenRegion && !worldGenRegion.getCenter().equals(new ChunkPos(blockInfoGlobal.pos()))) {
            return blockInfoGlobal;
         }

         RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos());
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.MOSSY_COBBLESTONE.defaultBlockState(), null);
         MutableBlockPos mutable = blockInfoGlobal.pos().mutable().move(Direction.DOWN);

         for (BlockState currBlockState = levelReader.getBlockState(mutable);
            mutable.getY() > levelReader.getMinBuildHeight()
               && mutable.getY() < levelReader.getMaxBuildHeight()
               && (currBlockState.isAir() || !levelReader.getFluidState(mutable).isEmpty());
            currBlockState = levelReader.getBlockState(mutable)
         ) {
            levelReader.getChunk(mutable).setBlockState(mutable, STONE_BRICK_SELECTOR.get(random), false);
            mutable.move(Direction.DOWN);
         }
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.SMALL_DUNGEON_LEG_PROCESSOR;
   }
}
