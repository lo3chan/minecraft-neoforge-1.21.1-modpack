package com.yungnickyoung.minecraft.yungsextras.world.feature.swamp;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.yungnickyoung.minecraft.yungsextras.module.FeatureProcessorModule;
import com.yungnickyoung.minecraft.yungsextras.world.feature.AbstractNbtFeature;
import com.yungnickyoung.minecraft.yungsextras.world.processor.INbtFeatureProcessor;
import java.util.List;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public abstract class AbstractSwampFeature<C extends FeatureConfiguration> extends AbstractNbtFeature<C> {
   public AbstractSwampFeature(Codec<C> codec) {
      super(codec);
   }

   @Override
   protected List<INbtFeatureProcessor> useProcessors() {
      return Lists.newArrayList(new INbtFeatureProcessor[]{FeatureProcessorModule.SWAMP_FEATURE_PROCESSOR});
   }
}
