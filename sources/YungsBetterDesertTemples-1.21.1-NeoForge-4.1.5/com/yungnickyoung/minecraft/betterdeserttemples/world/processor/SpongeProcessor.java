package com.yungnickyoung.minecraft.betterdeserttemples.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterdeserttemples.module.StructureProcessorModule;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SpongeProcessor extends StructureProcessor {
   public static final SpongeProcessor INSTANCE = new SpongeProcessor();
   public static final MapCodec<SpongeProcessor> CODEC = MapCodec.unit(() -> INSTANCE);
   private static final List<Block> CANDLES = List.of(
      Blocks.CANDLE, Blocks.WHITE_CANDLE, Blocks.GRAY_CANDLE, Blocks.LIGHT_GRAY_CANDLE, Blocks.BROWN_CANDLE, Blocks.ORANGE_CANDLE
   );

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      Block block = blockInfoGlobal.state().getBlock();
      if (block == Blocks.SPONGE || block == Blocks.WET_SPONGE || block == Blocks.CANDLE) {
         RandomSource randomSource = structurePlacementData.getRandom(blockInfoGlobal.pos());
         if (randomSource.nextFloat() < 0.8F) {
            int numCandles = 1;
            float r = randomSource.nextFloat();
            if (r < 0.1F) {
               numCandles = 2;
            } else if (r < 0.15F) {
               numCandles = 3;
            } else if (r < 0.2F) {
               numCandles = 4;
            }

            boolean lit = randomSource.nextFloat() < 0.4F;
            BlockState newBlockState = (BlockState)((BlockState)getRandomCandle(randomSource).defaultBlockState().setValue(CandleBlock.CANDLES, numCandles))
               .setValue(CandleBlock.LIT, lit);
            blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), newBlockState, blockInfoGlobal.nbt());
         } else {
            blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.AIR.defaultBlockState(), null);
         }
      }

      return blockInfoGlobal;
   }

   private static Block getRandomCandle(RandomSource randomSource) {
      int i = randomSource.nextInt(CANDLES.size());
      return CANDLES.get(i);
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorModule.SPONGE_PROCESSOR;
   }
}
