package com.yungnickyoung.minecraft.yungsextras.world.feature.desert;

import com.google.common.collect.Lists;
import com.yungnickyoung.minecraft.yungsextras.module.FeatureProcessorModule;
import com.yungnickyoung.minecraft.yungsextras.world.config.DesertWellFeatureConfiguration;
import com.yungnickyoung.minecraft.yungsextras.world.feature.AbstractNbtFeature;
import com.yungnickyoung.minecraft.yungsextras.world.processor.INbtFeatureProcessor;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

@ParametersAreNonnullByDefault
public class DesertWellFeature extends AbstractNbtFeature<DesertWellFeatureConfiguration> {
   public DesertWellFeature() {
      super(DesertWellFeatureConfiguration.CODEC);
   }

   @Override
   protected List<INbtFeatureProcessor> useProcessors() {
      return Lists.newArrayList(new INbtFeatureProcessor[]{FeatureProcessorModule.DESERT_WELL_PROCESSOR});
   }

   public boolean place(FeaturePlaceContext<DesertWellFeatureConfiguration> context) {
      WorldGenLevel level = context.level();
      RandomSource randomSource = context.random();
      BlockPos pos = context.origin();
      int radius = ((DesertWellFeatureConfiguration)context.config()).getRadius();
      ResourceLocation location = ((DesertWellFeatureConfiguration)context.config()).getLocation();
      MutableBlockPos mutable = pos.mutable();

      while (level.isEmptyBlock(mutable) && mutable.getY() > 7) {
         mutable.move(Direction.DOWN);
      }

      BlockPos surfacePos = mutable.immutable();
      Block block = level.getBlockState(surfacePos).getBlock();
      if (!block.defaultBlockState().is(BlockTags.SAND)) {
         return false;
      } else {
         for (int i = 1; i <= 7; i++) {
            mutable.set(surfacePos).move(Direction.DOWN, i);
            if (level.isEmptyBlock(mutable)) {
               return false;
            }

            mutable.set(surfacePos).move(Direction.DOWN, i).move(Direction.NORTH, radius).move(Direction.EAST, radius);
            if (level.isEmptyBlock(mutable)) {
               return false;
            }

            mutable.set(surfacePos).move(Direction.DOWN, i).move(Direction.EAST, radius).move(Direction.SOUTH, radius);
            if (level.isEmptyBlock(mutable)) {
               return false;
            }

            mutable.set(surfacePos).move(Direction.DOWN, i).move(Direction.SOUTH, radius).move(Direction.WEST, radius);
            if (level.isEmptyBlock(mutable)) {
               return false;
            }

            mutable.set(surfacePos).move(Direction.DOWN, i).move(Direction.WEST, radius).move(Direction.NORTH, radius);
            if (level.isEmptyBlock(mutable)) {
               return false;
            }
         }

         StructureTemplate template = this.createTemplateFromCenter(location, level, randomSource, surfacePos.relative(Direction.DOWN, 6));
         return template != null;
      }
   }
}
