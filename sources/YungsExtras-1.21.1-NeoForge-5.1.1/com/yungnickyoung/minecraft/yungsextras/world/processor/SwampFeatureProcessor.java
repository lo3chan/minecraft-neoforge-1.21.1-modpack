package com.yungnickyoung.minecraft.yungsextras.world.processor;

import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class SwampFeatureProcessor implements INbtFeatureProcessor {
   private static final BlockStateRandomizer STONE_BRICK_SELECTOR = new BlockStateRandomizer(Blocks.STONE_BRICKS.defaultBlockState())
      .addBlock(Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), 0.45F)
      .addBlock(Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 0.45F);
   private static final BlockStateRandomizer STONE_BRICK_STAIRS_SELECTOR = new BlockStateRandomizer(Blocks.STONE_BRICK_STAIRS.defaultBlockState())
      .addBlock(Blocks.MOSSY_STONE_BRICK_STAIRS.defaultBlockState(), 0.6F);
   private static final List<Block> CANDLES = List.of(
      Blocks.CANDLE,
      Blocks.WHITE_CANDLE,
      Blocks.GRAY_CANDLE,
      Blocks.LIGHT_GRAY_CANDLE,
      Blocks.BROWN_CANDLE,
      Blocks.GREEN_CANDLE,
      Blocks.PURPLE_CANDLE,
      Blocks.BLACK_CANDLE
   );

   @Override
   public void processTemplate(
      StructureTemplate template,
      WorldGenLevel level,
      RandomSource randomSource,
      BlockPos cornerPos,
      BlockPos centerPos,
      StructurePlaceSettings placementSettings
   ) {
      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.GRAY_STAINED_GLASS)) {
         level.setBlock(blockInfo.pos(), STONE_BRICK_SELECTOR.get(randomSource), 2);
         MutableBlockPos mutable = blockInfo.pos().mutable().move(Direction.DOWN);

         for (BlockState blockState = level.getBlockState(mutable);
            blockState.isAir() || blockState.liquid() || blockState.canBeReplaced();
            blockState = level.getBlockState(mutable)
         ) {
            level.setBlock(mutable, STONE_BRICK_SELECTOR.get(randomSource), 2);
            mutable.move(Direction.DOWN);
         }
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.LIGHT_GRAY_STAINED_GLASS)) {
         level.setBlock(
            blockInfo.pos(),
            (BlockState)STONE_BRICK_STAIRS_SELECTOR.get(randomSource)
               .setValue(BlockStateProperties.HORIZONTAL_FACING, Plane.HORIZONTAL.getRandomDirection(randomSource)),
            2
         );
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.STONE_BRICKS)) {
         level.setBlock(blockInfo.pos(), STONE_BRICK_SELECTOR.get(randomSource), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.STONE_BRICK_STAIRS)) {
         level.setBlock(
            blockInfo.pos(),
            (BlockState)((BlockState)((BlockState)((BlockState)STONE_BRICK_STAIRS_SELECTOR.get(randomSource)
                        .setValue(StairBlock.FACING, (Direction)blockInfo.state().getValue(StairBlock.FACING)))
                     .setValue(StairBlock.HALF, (Half)blockInfo.state().getValue(StairBlock.HALF)))
                  .setValue(StairBlock.SHAPE, (StairsShape)blockInfo.state().getValue(StairBlock.SHAPE)))
               .setValue(StairBlock.WATERLOGGED, (Boolean)blockInfo.state().getValue(StairBlock.WATERLOGGED)),
            2
         );
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.GREEN_CANDLE)) {
         int numCandles = randomSource.nextInt(4) + 1;
         boolean lit = randomSource.nextFloat() < 0.1F;
         level.setBlock(
            blockInfo.pos(),
            (BlockState)((BlockState)getRandomCandle(randomSource).defaultBlockState().setValue(CandleBlock.CANDLES, numCandles))
               .setValue(CandleBlock.LIT, lit),
            2
         );
      }
   }

   private static Block getRandomCandle(RandomSource randomSource) {
      int i = randomSource.nextInt(CANDLES.size());
      return CANDLES.get(i);
   }
}
