package com.yungnickyoung.minecraft.betterdungeons.world.processor.zombie_dungeon;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterdungeons.module.StructureProcessorTypeModule;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ZombieRotProcessor extends StructureProcessor {
   public static final ZombieRotProcessor INSTANCE = new ZombieRotProcessor();
   public static final MapCodec<ZombieRotProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if ((
            blockInfoGlobal.state().getBlock() == Blocks.COBBLESTONE
               || blockInfoGlobal.state().getBlock() == Blocks.CYAN_TERRACOTTA
               || blockInfoGlobal.state().getBlock() == Blocks.COBBLESTONE_STAIRS
         )
         && levelReader.getBlockState(blockInfoGlobal.pos()).isAir()) {
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.CAVE_AIR.defaultBlockState(), null);
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.ZOMBIE_ROT_PROCESSOR;
   }
}
