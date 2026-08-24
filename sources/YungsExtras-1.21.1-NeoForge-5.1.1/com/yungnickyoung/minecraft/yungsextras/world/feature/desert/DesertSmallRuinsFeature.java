package com.yungnickyoung.minecraft.yungsextras.world.feature.desert;

import com.yungnickyoung.minecraft.yungsextras.world.feature.AbstractNbtFeature;
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
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

@ParametersAreNonnullByDefault
public class DesertSmallRuinsFeature extends AbstractNbtFeature<NoneFeatureConfiguration> {
   private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("yungsextras", "desert/misc/ruins_0");

   public DesertSmallRuinsFeature() {
      super(NoneFeatureConfiguration.CODEC);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      WorldGenLevel level = context.level();
      RandomSource randomSource = context.random();
      BlockPos pos = context.origin();
      MutableBlockPos mutable = pos.mutable();

      while (level.isEmptyBlock(mutable) && mutable.getY() > 2) {
         mutable.move(Direction.DOWN);
      }

      BlockPos surfacePos = mutable.immutable();
      BlockPos cornerPos = surfacePos.offset(-2, 0, -2);
      Block block = level.getBlockState(surfacePos).getBlock();
      if (!block.defaultBlockState().is(BlockTags.SAND)) {
         return false;
      } else {
         mutable.set(cornerPos);
         if (!level.getBlockState(mutable).isSolid()) {
            return false;
         } else {
            mutable.set(cornerPos).move(Direction.SOUTH, 3);
            if (!level.getBlockState(mutable).isSolid()) {
               return false;
            } else {
               mutable.set(cornerPos).move(Direction.EAST, 3);
               if (!level.getBlockState(mutable).isSolid()) {
                  return false;
               } else {
                  mutable.set(cornerPos).move(Direction.SOUTH, 3).move(Direction.EAST, 3);
                  if (!level.getBlockState(mutable).isSolid()) {
                     return false;
                  } else {
                     StructureTemplate template = this.createTemplateFromCenter(ID, level, randomSource, surfacePos);
                     return template != null;
                  }
               }
            }
         }
      }
   }
}
