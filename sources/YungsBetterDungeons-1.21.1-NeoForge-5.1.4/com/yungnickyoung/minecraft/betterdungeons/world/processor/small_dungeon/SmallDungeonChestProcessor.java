package com.yungnickyoung.minecraft.betterdungeons.world.processor.small_dungeon;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterdungeons.BetterDungeonsCommon;
import com.yungnickyoung.minecraft.betterdungeons.module.StructureProcessorTypeModule;
import com.yungnickyoung.minecraft.betterdungeons.world.DungeonContext;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmallDungeonChestProcessor extends StructureProcessor {
   public static final SmallDungeonChestProcessor INSTANCE = new SmallDungeonChestProcessor();
   public static final MapCodec<SmallDungeonChestProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().getBlock() instanceof ChestBlock) {
         DungeonContext context = DungeonContext.peek();
         int chestCount = DungeonContext.peek().getChestCount();
         if (chestCount < BetterDungeonsCommon.CONFIG.smallDungeons.chestMinCount) {
            context.incrementChestCount();
         } else {
            if (chestCount >= BetterDungeonsCommon.CONFIG.smallDungeons.chestMaxCount) {
               return new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.CAVE_AIR.defaultBlockState(), null);
            }

            RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos());
            if (random.nextFloat() > 0.2F) {
               return new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.CAVE_AIR.defaultBlockState(), null);
            }

            context.incrementChestCount();
         }
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.SMALL_DUNGEON_CHEST_PROCESSOR;
   }
}
