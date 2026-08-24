package com.aetherteam.aether.world.processor;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.world.BlockLogicUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.Nullable;

public class VerticalGradientProcessor extends StructureProcessor {
   public static final VerticalGradientProcessor INSTANCE = new VerticalGradientProcessor();
   public static final MapCodec<VerticalGradientProcessor> CODEC = MapCodec.unit(INSTANCE);

   @Nullable
   public StructureBlockInfo process(
      LevelReader level,
      BlockPos origin,
      BlockPos centerBottom,
      StructureBlockInfo originalBlockInfo,
      StructureBlockInfo modifiedBlockInfo,
      StructurePlaceSettings settings,
      @Nullable StructureTemplate template
   ) {
      if (level instanceof WorldGenLevel worldGenLevel) {
         if (worldGenLevel instanceof WorldGenRegion region && BlockLogicUtil.isOutOfBounds(modifiedBlockInfo.pos(), region.getCenter())) {
            return modifiedBlockInfo;
         }

         if (modifiedBlockInfo.state().is((Block)AetherBlocks.AETHER_DIRT.get())) {
            BlockPos below = modifiedBlockInfo.pos().below();
            if (worldGenLevel.getBlockState(below).is(AetherTags.Blocks.HOLYSTONE)) {
               RandomSource random = settings.getRandom(below);
               if (random.nextBoolean()) {
                  worldGenLevel.setBlock(below, ((Block)AetherBlocks.AETHER_DIRT.get()).defaultBlockState(), 2);
               }
            }
         }
      }

      return modifiedBlockInfo;
   }

   protected StructureProcessorType<?> getType() {
      return (StructureProcessorType<?>)AetherStructureProcessors.VERTICAL_GRADIENT.get();
   }
}
