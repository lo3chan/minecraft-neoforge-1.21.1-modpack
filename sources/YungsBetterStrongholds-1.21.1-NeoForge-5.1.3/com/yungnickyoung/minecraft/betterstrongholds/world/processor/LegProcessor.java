package com.yungnickyoung.minecraft.betterstrongholds.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterstrongholds.module.StructureProcessorTypeModule;
import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import com.yungnickyoung.minecraft.yungsapi.world.structure.processor.ISafeWorldModifier;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class LegProcessor extends StructureProcessor implements ISafeWorldModifier {
   public static final LegProcessor INSTANCE = new LegProcessor();
   public static final MapCodec<LegProcessor> CODEC = MapCodec.unit(() -> INSTANCE);
   private final BlockStateRandomizer stoneBrickSelector = new BlockStateRandomizer(Blocks.STONE_BRICKS.defaultBlockState())
      .addBlock(Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), 0.3F)
      .addBlock(Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 0.2F)
      .addBlock(Blocks.INFESTED_STONE_BRICKS.defaultBlockState(), 0.05F);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().is(Blocks.YELLOW_STAINED_GLASS) || blockInfoGlobal.state().is(Blocks.ORANGE_STAINED_GLASS)) {
         if (levelReader instanceof WorldGenRegion worldGenRegion && !worldGenRegion.getCenter().equals(new ChunkPos(blockInfoGlobal.pos()))) {
            return blockInfoGlobal;
         }

         RandomSource randomSource = structurePlacementData.getRandom(blockInfoGlobal.pos());
         blockInfoGlobal = blockInfoGlobal.state().is(Blocks.YELLOW_STAINED_GLASS)
            ? new StructureBlockInfo(blockInfoGlobal.pos(), this.stoneBrickSelector.get(randomSource), null)
            : new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.CYAN_TERRACOTTA.defaultBlockState(), null);
         MutableBlockPos mutable = blockInfoGlobal.pos().mutable().move(Direction.DOWN);
         BlockState currBlockState = levelReader.getBlockState(mutable);

         for (int yBelow = 1;
            mutable.getY() > levelReader.getMinBuildHeight()
               && mutable.getY() < levelReader.getMaxBuildHeight()
               && (currBlockState.isAir() || !levelReader.getFluidState(mutable).isEmpty());
            yBelow++
         ) {
            levelReader.getChunk(mutable).setBlockState(mutable, this.stoneBrickSelector.get(randomSource), false);
            if (yBelow != 1) {
               if (yBelow == 2) {
                  for (Direction direction : Plane.HORIZONTAL) {
                     MutableBlockPos tempMutable = mutable.relative(direction).mutable();
                     this.setBlockStateSafe(
                        levelReader,
                        tempMutable,
                        (BlockState)((BlockState)((BlockState)Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.HALF, Half.TOP))
                              .setValue(StairBlock.FACING, direction.getOpposite()))
                           .setValue(StairBlock.WATERLOGGED, levelReader.getFluidState(tempMutable).is(FluidTags.WATER))
                     );
                     tempMutable.move(direction);
                     this.setBlockStateSafe(
                        levelReader,
                        tempMutable,
                        (BlockState)((BlockState)Blocks.STONE_BRICK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP))
                           .setValue(SlabBlock.WATERLOGGED, levelReader.getFluidState(tempMutable).is(FluidTags.WATER))
                     );
                  }
               }
            } else {
               for (Direction direction : Plane.HORIZONTAL) {
                  MutableBlockPos tempMutable = mutable.relative(direction).mutable();
                  this.setBlockStateSafe(
                     levelReader,
                     tempMutable,
                     (BlockState)((BlockState)((BlockState)Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.HALF, Half.TOP))
                           .setValue(StairBlock.FACING, direction.getOpposite()))
                        .setValue(StairBlock.WATERLOGGED, levelReader.getFluidState(tempMutable).is(FluidTags.WATER))
                  );
                  tempMutable.move(direction);
                  this.setBlockStateSafe(
                     levelReader,
                     tempMutable,
                     (BlockState)((BlockState)((BlockState)Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.HALF, Half.TOP))
                           .setValue(StairBlock.FACING, direction))
                        .setValue(StairBlock.WATERLOGGED, levelReader.getFluidState(tempMutable).is(FluidTags.WATER))
                  );
                  tempMutable.move(direction).move(Direction.UP);
                  Optional<BlockState> aboveBlockState = this.getBlockStateSafe(levelReader, tempMutable);
                  Block aboveBlock = aboveBlockState.<Block>map(BlockStateBase::getBlock).orElse(null);
                  tempMutable.move(direction).move(Direction.DOWN);
                  Optional<BlockState> belowBlockState = this.getBlockStateSafe(levelReader, tempMutable);
                  Block belowBlock = belowBlockState.<Block>map(BlockStateBase::getBlock).orElse(null);
                  if (belowBlock == Blocks.STONE_BRICK_STAIRS
                     || aboveBlock == Blocks.STONE_BRICKS
                     || aboveBlock == Blocks.CRACKED_STONE_BRICKS
                     || aboveBlock == Blocks.MOSSY_STONE_BRICKS
                     || aboveBlock == Blocks.INFESTED_STONE_BRICKS) {
                     tempMutable.move(direction.getOpposite());
                     this.setBlockStateSafe(levelReader, tempMutable, Blocks.STONE_BRICKS.defaultBlockState());
                  }
               }
            }

            mutable.move(Direction.DOWN);
            currBlockState = levelReader.getBlockState(mutable);
         }
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.LEG_PROCESSOR;
   }
}
