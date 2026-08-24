package com.yungnickyoung.minecraft.yungsextras.world.feature.swamp;

import com.yungnickyoung.minecraft.yungsextras.world.config.ResourceLocationFeatureConfiguration;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

@ParametersAreNonnullByDefault
public class SwampPillarFeature extends AbstractSwampFeature<ResourceLocationFeatureConfiguration> {
   public SwampPillarFeature() {
      super(ResourceLocationFeatureConfiguration.CODEC);
   }

   public boolean place(FeaturePlaceContext<ResourceLocationFeatureConfiguration> context) {
      WorldGenLevel level = context.level();
      RandomSource randomSource = context.random();
      BlockPos pos = context.origin();
      ResourceLocation location = ((ResourceLocationFeatureConfiguration)context.config()).getLocation();
      MutableBlockPos mutable = pos.mutable();

      while (level.isEmptyBlock(mutable) && mutable.getY() > 2) {
         mutable.move(Direction.DOWN);
      }

      BlockPos surfacePos = mutable.immutable();
      BlockPos cornerPos = surfacePos.offset(-2, 0, -2);
      mutable.set(cornerPos).move(Direction.DOWN, 4);
      if (level.isEmptyBlock(mutable)) {
         return false;
      } else {
         mutable.set(cornerPos).move(Direction.SOUTH, 3).move(Direction.DOWN, 4);
         if (level.isEmptyBlock(mutable)) {
            return false;
         } else {
            mutable.set(cornerPos).move(Direction.EAST, 3).move(Direction.DOWN, 4);
            if (level.isEmptyBlock(mutable)) {
               return false;
            } else {
               mutable.set(cornerPos).move(Direction.SOUTH, 3).move(Direction.EAST, 3).move(Direction.DOWN, 4);
               if (level.isEmptyBlock(mutable)) {
                  return false;
               } else {
                  StructureTemplate template = this.createTemplateFromCenter(location, level, randomSource, surfacePos);
                  return template != null;
               }
            }
         }
      }
   }
}
