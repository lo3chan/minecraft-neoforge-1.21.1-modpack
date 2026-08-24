package com.yungnickyoung.minecraft.betterjungletemples.world.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.betterjungletemples.module.StructureProcessorTypeModule;
import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext.Builder;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PillarProcessor extends StructureProcessor {
   public static final MapCodec<PillarProcessor> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            BlockState.CODEC.fieldOf("target_block").forGetter(config -> config.targetBlock),
            BlockStateRandomizer.CODEC.fieldOf("target_block_output").forGetter(config -> config.targetBlockOutput),
            BlockStateRandomizer.CODEC.fieldOf("pillar_states").forGetter(config -> config.pillarStates),
            Direction.CODEC.optionalFieldOf("direction", Direction.DOWN).forGetter(processor -> processor.direction),
            Codec.INT.optionalFieldOf("pillar_length", -1).forGetter(config -> config.length)
         )
         .apply(instance, instance.stable(PillarProcessor::new))
   );
   public final BlockState targetBlock;
   public final BlockStateRandomizer targetBlockOutput;
   public final BlockStateRandomizer pillarStates;
   public final Direction direction;
   public final int length;

   private PillarProcessor(BlockState targetBlock, BlockStateRandomizer targetBlockOutput, BlockStateRandomizer pillarStates, Direction direction, int length) {
      this.targetBlock = targetBlock;
      this.targetBlockOutput = targetBlockOutput;
      this.pillarStates = pillarStates;
      this.direction = direction;
      this.length = length;
   }

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().is(this.targetBlock.getBlock())) {
         if (levelReader instanceof WorldGenRegion worldGenRegion && !worldGenRegion.getCenter().equals(new ChunkPos(blockInfoGlobal.pos()))) {
            return blockInfoGlobal;
         }

         RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos());
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), this.targetBlockOutput.get(random), blockInfoGlobal.nbt());
         MutableBlockPos mutable = blockInfoGlobal.pos().mutable().move(Direction.DOWN);

         for (BlockState currBlockState = levelReader.getBlockState(mutable);
            mutable.getY() > levelReader.getMinBuildHeight()
               && mutable.getY() < levelReader.getMaxBuildHeight()
               && (currBlockState.isAir() || !levelReader.getFluidState(mutable).isEmpty());
            currBlockState = levelReader.getBlockState(mutable)
         ) {
            StructureContext ctx = new Builder().pieceMinY(mutable.getY()).pieceMaxY(mutable.getY()).build();
            levelReader.getChunk(mutable).setBlockState(mutable, this.pillarStates.get(random, ctx), false);
            mutable.move(Direction.DOWN);
         }
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.PILLAR_PROCESSOR;
   }
}
