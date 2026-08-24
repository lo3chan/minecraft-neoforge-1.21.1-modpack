package com.yungnickyoung.minecraft.yungsbridges.world.feature;

import com.yungnickyoung.minecraft.yungsbridges.world.feature.config.MultipleAttemptSingleRandomFeatureConfig;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MultipleAttemptSingleRandomFeature extends Feature<MultipleAttemptSingleRandomFeatureConfig> {
   public MultipleAttemptSingleRandomFeature() {
      super(MultipleAttemptSingleRandomFeatureConfig.CODEC);
   }

   public boolean place(FeaturePlaceContext<MultipleAttemptSingleRandomFeatureConfig> context) {
      List<PlacedFeature> placedFeatureList = ((MultipleAttemptSingleRandomFeatureConfig)context.config())
         .getPlacedFeatures()
         .stream()
         .<PlacedFeature>map(Holder::value)
         .collect(Collectors.toList());

      for (int size = placedFeatureList.size(); size > 0; size--) {
         int i = context.random().nextInt(placedFeatureList.size());
         PlacedFeature placedFeature = placedFeatureList.get(i);
         if (placedFeature.place(context.level(), context.chunkGenerator(), context.random(), context.origin())) {
            return true;
         }

         placedFeatureList.remove(i);
      }

      return false;
   }
}
