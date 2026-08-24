package com.yungnickyoung.minecraft.yungsbridges.world.feature;

import com.google.common.collect.Lists;
import com.yungnickyoung.minecraft.yungsbridges.YungsBridgesCommon;
import com.yungnickyoung.minecraft.yungsbridges.module.FeatureProcessorModule;
import com.yungnickyoung.minecraft.yungsbridges.services.Services;
import com.yungnickyoung.minecraft.yungsbridges.world.feature.config.BridgeFeatureConfig;
import com.yungnickyoung.minecraft.yungsbridges.world.processor.ITemplateFeatureProcessor;
import java.util.List;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class BridgeFeature extends AbstractTemplateFeature<BridgeFeatureConfig> {
   private static final List<ITemplateFeatureProcessor> PROCESSORS = Lists.newArrayList(
      new ITemplateFeatureProcessor[]{
         FeatureProcessorModule.LEG_PROCESSOR,
         FeatureProcessorModule.LOG_BIOME_PROCESSOR,
         FeatureProcessorModule.STAIR_BIOME_PROCESSOR,
         FeatureProcessorModule.PLANKS_BIOME_PROCESSOR,
         FeatureProcessorModule.SLAB_BIOME_PROCESSOR,
         FeatureProcessorModule.STONE_VARIATION_PROCESSOR,
         FeatureProcessorModule.LANTERN_ROT_PROCESSOR,
         FeatureProcessorModule.OPTIONAL_WALL_PROCESSOR,
         FeatureProcessorModule.OPTIONAL_STONE_BRICK_PROCESSOR,
         FeatureProcessorModule.OPTIONAL_SLAB_PROCESSOR,
         FeatureProcessorModule.OPTIONAL_STAIR_PROCESSOR,
         FeatureProcessorModule.FENCE_BIOME_PROCESSOR
      }
   );

   public BridgeFeature() {
      super(BridgeFeatureConfig.CODEC);
   }

   @Override
   protected List<ITemplateFeatureProcessor> useProcessors() {
      return PROCESSORS;
   }

   public boolean place(FeaturePlaceContext<BridgeFeatureConfig> context) {
      MutableBlockPos startPos = context.origin().mutable();
      startPos.setY(context.level().getSeaLevel());
      StructurePlaceSettings placementSettings = new StructurePlaceSettings();
      if (!((BridgeFeatureConfig)context.config()).isZAxis) {
         placementSettings.setRotation(Rotation.COUNTERCLOCKWISE_90);
      }

      StructureTemplate template = this.createTemplateWithPlacement(
         ((BridgeFeatureConfig)context.config()).id, context.level(), context.random(), startPos, placementSettings
      );
      if (Services.PLATFORM.isDevelopmentEnvironment()) {
         YungsBridgesCommon.LOGGER
            .info(
               "Bridge at {} {} {} - {}, {}",
               context.origin().getX(),
               context.origin().getY(),
               context.origin().getZ(),
               ((BridgeFeatureConfig)context.config()).id,
               ((BridgeFeatureConfig)context.config()).isZAxis
            );
      }

      return template != null;
   }
}
