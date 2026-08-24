package com.yungnickyoung.minecraft.betterdungeons.world.processor.small_nether_dungeon;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterdungeons.BetterDungeonsCommon;
import com.yungnickyoung.minecraft.betterdungeons.module.StructureProcessorTypeModule;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmallNetherDungeonHeadProcessor extends StructureProcessor {
   public static final SmallNetherDungeonHeadProcessor INSTANCE = new SmallNetherDungeonHeadProcessor();
   public static final MapCodec<SmallNetherDungeonHeadProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().getBlock() instanceof AbstractSkullBlock
         && (!BetterDungeonsCommon.CONFIG.general.enableHeads || structurePlacementData.getRandom(blockInfoGlobal.pos()).nextFloat() > 0.167)) {
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.AIR.defaultBlockState(), null);
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.SMALL_NETHER_DUNGEON_HEAD_PROCESSOR;
   }
}
