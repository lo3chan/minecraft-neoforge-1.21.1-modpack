package com.yungnickyoung.minecraft.betterstrongholds.world.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.betterstrongholds.module.StructureProcessorTypeModule;
import com.yungnickyoung.minecraft.yungsapi.world.structure.processor.ISafeWorldModifier;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class RedstoneProcessor extends StructureProcessor implements ISafeWorldModifier {
   public static final RedstoneProcessor INSTANCE = new RedstoneProcessor(Blocks.STONE_BRICKS);
   public static final MapCodec<RedstoneProcessor> CODEC = RecordCodecBuilder.mapCodec(
      codecBuilder -> codecBuilder.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("below_block").orElse(Blocks.STONE_BRICKS).forGetter(processor -> processor.belowBlock)
         )
         .apply(codecBuilder, codecBuilder.stable(RedstoneProcessor::new))
   );
   private final Block belowBlock;

   private RedstoneProcessor(Block belowBlock) {
      this.belowBlock = belowBlock;
   }

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().is(Blocks.REDSTONE_WIRE)) {
         Optional<BlockState> belowBlockState = this.getBlockStateSafe(levelReader, blockInfoGlobal.pos().below());
         if (belowBlockState.isEmpty() || !belowBlockState.get().isFaceSturdy(levelReader, blockInfoGlobal.pos().below(), Direction.UP)) {
            this.setBlockStateSafe(levelReader, blockInfoGlobal.pos().below(), this.belowBlock.defaultBlockState());
         }
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.REDSTONE_PROCESSOR;
   }
}
