package com.yungnickyoung.minecraft.betterfortresses.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterfortresses.module.StructureProcessorTypeModule;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StairPillarProcessor extends StructureProcessor {
   public static final StairPillarProcessor INSTANCE = new StairPillarProcessor();
   public static final MapCodec<StairPillarProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().is(Blocks.STONE_BRICK_STAIRS) || blockInfoGlobal.state().is(Blocks.PURPUR_STAIRS)) {
         Direction facing = (Direction)blockInfoGlobal.state().getValue(StairBlock.FACING);
         Half half = (Half)blockInfoGlobal.state().getValue(StairBlock.HALF);
         StairsShape shape = (StairsShape)blockInfoGlobal.state().getValue(StairBlock.SHAPE);
         BlockState output;
         if (blockInfoGlobal.state().is(Blocks.PURPUR_STAIRS)) {
            output = Blocks.RED_NETHER_BRICKS.defaultBlockState();
         } else {
            output = (BlockState)((BlockState)((BlockState)Blocks.RED_NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, facing))
                  .setValue(StairBlock.HALF, half))
               .setValue(StairBlock.SHAPE, shape);
         }

         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), output, blockInfoGlobal.nbt());
         MutableBlockPos mutable = blockInfoGlobal.pos().mutable().move(facing);
         if (levelReader instanceof WorldGenRegion worldGenRegion && !worldGenRegion.getCenter().equals(new ChunkPos(mutable))) {
            return blockInfoGlobal;
         }

         levelReader.getChunk(mutable)
            .setBlockState(
               mutable,
               (BlockState)((BlockState)((BlockState)Blocks.RED_NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, facing.getOpposite()))
                     .setValue(StairBlock.HALF, half))
                  .setValue(StairBlock.SHAPE, shape),
               false
            );
         mutable.move(Direction.DOWN);

         for (BlockState currBlockState = levelReader.getBlockState(mutable);
            mutable.getY() > levelReader.getMinBuildHeight()
               && mutable.getY() < levelReader.getMaxBuildHeight()
               && (currBlockState.isAir() || !levelReader.getFluidState(mutable).isEmpty());
            currBlockState = levelReader.getBlockState(mutable)
         ) {
            levelReader.getChunk(mutable).setBlockState(mutable, Blocks.RED_NETHER_BRICKS.defaultBlockState(), false);
            mutable.move(Direction.DOWN);
         }
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.STAIR_PILLAR_PROCESSOR;
   }
}
