package com.yungnickyoung.minecraft.betterdungeons.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterdungeons.BetterDungeonsCommon;
import com.yungnickyoung.minecraft.betterdungeons.module.StructureProcessorTypeModule;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NetherBlockProcessor extends StructureProcessor {
   public static final NetherBlockProcessor INSTANCE = new NetherBlockProcessor();
   public static final MapCodec<NetherBlockProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (!BetterDungeonsCommon.CONFIG.general.enableNetherBlocks) {
         if (blockInfoGlobal.state().is(Blocks.SOUL_SAND) || blockInfoGlobal.state().is(Blocks.SOUL_SOIL)) {
            blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.COARSE_DIRT.defaultBlockState(), null);
         } else if (blockInfoGlobal.state().is(Blocks.SOUL_CAMPFIRE)) {
            blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.CAMPFIRE.defaultBlockState(), null);
         } else if (blockInfoGlobal.state().is(Blocks.SOUL_LANTERN)) {
            blockInfoGlobal = new StructureBlockInfo(
               blockInfoGlobal.pos(),
               (BlockState)Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, (Boolean)blockInfoGlobal.state().getValue(LanternBlock.HANGING)),
               null
            );
         }
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.NETHER_BLOCK_PROCESSOR;
   }
}
