package com.yungnickyoung.minecraft.yungsextras.world.feature;

import com.mojang.serialization.Codec;
import com.yungnickyoung.minecraft.yungsextras.YungsExtrasCommon;
import com.yungnickyoung.minecraft.yungsextras.world.processor.INbtFeatureProcessor;
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

public abstract class AbstractNbtFeature<C extends FeatureConfiguration> extends Feature<C> {
   protected List<INbtFeatureProcessor> processors = this.useProcessors();

   public AbstractNbtFeature(Codec<C> codec) {
      super(codec);
   }

   protected StructureTemplate createTemplateFromCenter(ResourceLocation id, WorldGenLevel level, RandomSource rand, BlockPos centerPos) {
      return this.createTemplateFromCenterWithPlacement(id, level, rand, centerPos, new StructurePlaceSettings());
   }

   protected StructureTemplate createTemplateFromCenterWithPlacement(
      ResourceLocation id, WorldGenLevel level, RandomSource randomSource, BlockPos centerPos, StructurePlaceSettings placement
   ) {
      Optional<StructureTemplate> templateOptional = level.getLevel().getStructureManager().get(id);
      if (templateOptional.isEmpty()) {
         YungsExtrasCommon.LOGGER.warn("Failed to create invalid feature {}", id);
         return null;
      } else {
         StructureTemplate template = templateOptional.get();
         BlockPos cornerPos = centerPos.offset(-template.getSize().getX() / 2, 0, -template.getSize().getZ() / 2);
         template.placeInWorld(level, cornerPos, centerPos, placement, randomSource, 2);
         this.processors.forEach(processor -> processor.processTemplate(template, level, randomSource, cornerPos, centerPos, placement));
         return template;
      }
   }

   protected StructureTemplate createTemplateFromCorner(ResourceLocation id, WorldGenLevel level, RandomSource randomSource, BlockPos cornerPos) {
      return this.createTemplateFromCornerWithPlacement(id, level, randomSource, cornerPos, new StructurePlaceSettings());
   }

   protected StructureTemplate createTemplateFromCornerWithPlacement(
      ResourceLocation id, WorldGenLevel level, RandomSource randomSource, BlockPos cornerPos, StructurePlaceSettings placement
   ) {
      Optional<StructureTemplate> templateOptional = level.getLevel().getStructureManager().get(id);
      if (templateOptional.isEmpty()) {
         YungsExtrasCommon.LOGGER.warn("Failed to create invalid feature {}", id);
         return null;
      } else {
         StructureTemplate template = templateOptional.get();
         BlockPos centerPos = cornerPos.offset(template.getSize().getX() / 2, 0, template.getSize().getZ() / 2);
         template.placeInWorld(level, cornerPos, centerPos, placement, randomSource, 2);
         this.processors.forEach(processor -> processor.processTemplate(template, level, randomSource, cornerPos, centerPos, placement));
         return template;
      }
   }

   protected List<INbtFeatureProcessor> useProcessors() {
      return new ArrayList<>();
   }
}
