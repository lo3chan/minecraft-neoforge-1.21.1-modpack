package com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.element;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.module.StructurePoolElementTypeModule;
import com.yungnickyoung.minecraft.yungsapi.world.structure.condition.StructureCondition;
import com.yungnickyoung.minecraft.yungsapi.world.structure.modifier.StructureModifier;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.adaptations.EnhancedTerrainAdaptation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class YungJigsawSinglePoolElement extends YungJigsawPoolElement {
   private static final Codec<Either<ResourceLocation, StructureTemplate>> TEMPLATE_CODEC = Codec.of(
      YungJigsawSinglePoolElement::encodeTemplate, ResourceLocation.CODEC.map(Either::left)
   );
   public static final MapCodec<YungJigsawSinglePoolElement> CODEC = RecordCodecBuilder.mapCodec(
      builder -> builder.group(
            templateCodec(),
            processorsCodec(),
            projectionCodec(),
            overrideLiquidSettingsCodec(),
            nameCodec(),
            maxCountCodec(),
            minRequiredDepthCodec(),
            maxPossibleDepthCodec(),
            isPriorityCodec(),
            ignoreBoundsCodec(),
            conditionCodec(),
            enhancedTerrainAdaptationCodec(),
            ResourceLocation.CODEC.optionalFieldOf("deadend_pool").forGetter(element -> element.deadendPool),
            StructureModifier.CODEC.listOf().optionalFieldOf("modifiers", new ArrayList()).forGetter(element -> element.modifiers)
         )
         .apply(builder, YungJigsawSinglePoolElement::new)
   );
   public final Either<ResourceLocation, StructureTemplate> template;
   public final Holder<StructureProcessorList> processors;
   public final Optional<LiquidSettings> overrideLiquidSettings;
   public final Optional<ResourceLocation> deadendPool;
   public final List<StructureModifier> modifiers;

   public YungJigsawSinglePoolElement(
      Either<ResourceLocation, StructureTemplate> template,
      Holder<StructureProcessorList> processors,
      Projection projection,
      Optional<LiquidSettings> overrideLiquidSettings,
      Optional<String> name,
      Optional<Integer> maxCount,
      Optional<Integer> minRequiredDepth,
      Optional<Integer> maxPossibleDepth,
      boolean isPriority,
      boolean ignoreBounds,
      StructureCondition condition,
      Optional<EnhancedTerrainAdaptation> enhancedTerrainAdaptation,
      Optional<ResourceLocation> deadendPool,
      List<StructureModifier> modifiers
   ) {
      super(projection, name, maxCount, minRequiredDepth, maxPossibleDepth, isPriority, ignoreBounds, condition, enhancedTerrainAdaptation);
      this.template = template;
      this.processors = processors;
      this.overrideLiquidSettings = overrideLiquidSettings;
      this.deadendPool = deadendPool;
      this.modifiers = modifiers;
   }

   public Vec3i getSize(StructureTemplateManager structureTemplateManager, Rotation rotation) {
      StructureTemplate structureTemplate = this.getTemplate(structureTemplateManager);
      return structureTemplate.getSize(rotation);
   }

   public List<StructureBlockInfo> getShuffledJigsawBlocks(
      StructureTemplateManager structureTemplateManager, BlockPos blockPos, Rotation rotation, RandomSource randomSource
   ) {
      StructureTemplate structureTemplate = this.getTemplate(structureTemplateManager);
      ObjectArrayList<StructureBlockInfo> jigsawBlocks = structureTemplate.filterBlocks(
         blockPos, new StructurePlaceSettings().setRotation(rotation), Blocks.JIGSAW, true
      );
      Util.shuffle(jigsawBlocks, randomSource);
      return jigsawBlocks;
   }

   public BoundingBox getBoundingBox(StructureTemplateManager structureTemplateManager, BlockPos blockPos, Rotation rotation) {
      StructureTemplate structureTemplate = this.getTemplate(structureTemplateManager);
      return structureTemplate.getBoundingBox(new StructurePlaceSettings().setRotation(rotation), blockPos);
   }

   public boolean place(
      StructureTemplateManager structureTemplateManager,
      WorldGenLevel worldGenLevel,
      StructureManager structureManager,
      ChunkGenerator chunkGenerator,
      BlockPos pos,
      BlockPos pivotPos,
      Rotation rotation,
      BoundingBox boundingBox,
      RandomSource randomSource,
      LiquidSettings liquidSettings,
      boolean replaceJigsaws
   ) {
      StructureTemplate structureTemplate = this.getTemplate(structureTemplateManager);
      StructurePlaceSettings structurePlaceSettings = this.getSettings(rotation, boundingBox, liquidSettings, replaceJigsaws);
      if (!structureTemplate.placeInWorld(worldGenLevel, pos, pivotPos, structurePlaceSettings, randomSource, 18)) {
         return false;
      } else {
         for (StructureBlockInfo structureBlockInfo : StructureTemplate.processBlockInfos(
            worldGenLevel, pos, pivotPos, structurePlaceSettings, this.getDataMarkers(structureTemplateManager, pos, rotation, false)
         )) {
            this.handleDataMarker(worldGenLevel, structureBlockInfo, pos, rotation, randomSource, boundingBox);
         }

         return true;
      }
   }

   public Optional<ResourceLocation> getDeadendPool() {
      return this.deadendPool;
   }

   public boolean hasModifiers() {
      return !this.modifiers.isEmpty();
   }

   public StructureTemplate getTemplate(StructureTemplateManager structureTemplateManager) {
      return (StructureTemplate)this.template.map(structureTemplateManager::getOrCreate, Function.identity());
   }

   public StructurePoolElementType<?> getType() {
      return StructurePoolElementTypeModule.YUNG_SINGLE_ELEMENT;
   }

   public String toString() {
      return String.format(
         "YungJigsawSingle[%s][%s][%s][%s]",
         this.name.orElse("<unnamed>"),
         this.template,
         this.maxCount.isPresent() ? this.maxCount.get() : "no max count",
         this.isPriority
      );
   }

   private StructurePlaceSettings getSettings(Rotation rotation, BoundingBox boundingBox, LiquidSettings liquidSettings, boolean replaceJigsaws) {
      StructurePlaceSettings structurePlaceSettings = new StructurePlaceSettings();
      structurePlaceSettings.setBoundingBox(boundingBox);
      structurePlaceSettings.setRotation(rotation);
      structurePlaceSettings.setKnownShape(true);
      structurePlaceSettings.setIgnoreEntities(false);
      structurePlaceSettings.addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
      structurePlaceSettings.setFinalizeEntities(true);
      structurePlaceSettings.setLiquidSettings(this.overrideLiquidSettings.orElse(liquidSettings));
      if (!replaceJigsaws) {
         structurePlaceSettings.addProcessor(JigsawReplacementProcessor.INSTANCE);
      }

      ((StructureProcessorList)this.processors.value()).list().forEach(structurePlaceSettings::addProcessor);
      this.getProjection().getProcessors().forEach(structurePlaceSettings::addProcessor);
      return structurePlaceSettings;
   }

   private List<StructureBlockInfo> getDataMarkers(
      StructureTemplateManager structureTemplateManager, BlockPos blockPos, Rotation rotation, boolean isPositionLocal
   ) {
      StructureTemplate structureTemplate = this.getTemplate(structureTemplateManager);
      List<StructureBlockInfo> structureBlocks = structureTemplate.filterBlocks(
         blockPos, new StructurePlaceSettings().setRotation(rotation), Blocks.STRUCTURE_BLOCK, isPositionLocal
      );
      List<StructureBlockInfo> dataBlocks = Lists.newArrayList();

      for (StructureBlockInfo block : structureBlocks) {
         StructureMode structureMode = StructureMode.valueOf(block.nbt().getString("mode"));
         if (structureMode == StructureMode.DATA) {
            dataBlocks.add(block);
         }
      }

      return dataBlocks;
   }

   public static <E extends YungJigsawSinglePoolElement> RecordCodecBuilder<E, Holder<StructureProcessorList>> processorsCodec() {
      return StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter(element -> element.processors);
   }

   public static <E extends YungJigsawSinglePoolElement> RecordCodecBuilder<E, Either<ResourceLocation, StructureTemplate>> templateCodec() {
      return TEMPLATE_CODEC.fieldOf("location").forGetter(element -> element.template);
   }

   public static <E extends YungJigsawSinglePoolElement> RecordCodecBuilder<E, Optional<LiquidSettings>> overrideLiquidSettingsCodec() {
      return LiquidSettings.CODEC.optionalFieldOf("override_liquid_settings").forGetter(element -> element.overrideLiquidSettings);
   }

   private static <T> DataResult<T> encodeTemplate(Either<ResourceLocation, StructureTemplate> either, DynamicOps<T> ops, T template) {
      Optional<ResourceLocation> optional = either.left();
      return !optional.isPresent()
         ? DataResult.error(() -> "Can not serialize a runtime pool element")
         : ResourceLocation.CODEC.encode(optional.get(), ops, template);
   }
}
