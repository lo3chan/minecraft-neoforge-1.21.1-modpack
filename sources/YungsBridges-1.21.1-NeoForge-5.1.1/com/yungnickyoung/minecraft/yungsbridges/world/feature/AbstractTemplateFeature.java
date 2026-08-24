package com.yungnickyoung.minecraft.yungsbridges.world.feature;

import com.mojang.serialization.Codec;
import com.yungnickyoung.minecraft.yungsbridges.YungsBridgesCommon;
import com.yungnickyoung.minecraft.yungsbridges.world.processor.ITemplateFeatureProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public abstract class AbstractTemplateFeature<C extends FeatureConfiguration> extends Feature<C> {
   protected List<ITemplateFeatureProcessor> processors = this.useProcessors();

   public AbstractTemplateFeature(Codec<C> codec) {
      super(codec);
   }

   protected StructureTemplate createTemplate(ResourceLocation id, WorldGenLevel level, RandomSource randomSource, BlockPos pos) {
      return this.createTemplateWithPlacement(id, level, randomSource, pos, new StructurePlaceSettings());
   }

   protected StructureTemplate createTemplateWithPlacement(
      ResourceLocation id, WorldGenLevel level, RandomSource randomSource, BlockPos cornerPos, StructurePlaceSettings placement
   ) {
      Optional<StructureTemplate> templateOptional = level.getLevel().getStructureManager().get(id);
      if (templateOptional.isEmpty()) {
         YungsBridgesCommon.LOGGER.warn("Failed to create invalid feature {}", id);
         return null;
      } else {
         StructureTemplate template = templateOptional.get();
         BlockPos centerPos = cornerPos.offset(template.getSize().getX() / 2, 0, template.getSize().getZ() / 2);
         template.placeInWorld(level, cornerPos, centerPos, placement, randomSource, 2);
         this.processors.forEach(processor -> processor.processTemplate(template, level, randomSource, cornerPos, centerPos, placement));
         return template;
      }
   }

   protected List<ITemplateFeatureProcessor> useProcessors() {
      return new ArrayList<>();
   }
}
