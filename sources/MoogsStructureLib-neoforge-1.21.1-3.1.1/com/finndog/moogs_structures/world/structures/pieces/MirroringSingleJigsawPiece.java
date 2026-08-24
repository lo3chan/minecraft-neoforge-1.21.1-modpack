package com.finndog.moogs_structures.world.structures.pieces;

import com.finndog.moogs_structures.mixins.structures.SinglePoolElementAccessor;
import com.finndog.moogs_structures.modinit.MoogsStructuresStructurePieces;
import com.finndog.moogs_structures.world.structures.terrainadaptation.EnhancedTerrainAdaptation;
import com.finndog.moogs_structures.world.structures.terrainadaptation.PoolElementAdaptationOverride;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class MirroringSingleJigsawPiece extends SinglePoolElement implements PoolElementAdaptationOverride {
   public static final MapCodec<MirroringSingleJigsawPiece> CODEC = RecordCodecBuilder.mapCodec(
      jigsawPieceInstance -> jigsawPieceInstance.group(
            templateCodec(), processorsCodec(), projectionCodec(), mirrorCodec(), overrideLiquidSettingsCodec(), adaptationOverrideCodec()
         )
         .apply(jigsawPieceInstance, MirroringSingleJigsawPiece::new)
   );
   protected final Mirror mirror;
   protected final Optional<EnhancedTerrainAdaptation> adaptationOverride;

   protected static <E extends MirroringSingleJigsawPiece> RecordCodecBuilder<E, Mirror> mirrorCodec() {
      return Codec.STRING.fieldOf("mirror").xmap(Mirror::valueOf, Enum::toString).forGetter(jigsawPieceInstance -> jigsawPieceInstance.mirror);
   }

   protected static <E extends MirroringSingleJigsawPiece> RecordCodecBuilder<E, Optional<EnhancedTerrainAdaptation>> adaptationOverrideCodec() {
      return EnhancedTerrainAdaptation.CODEC
         .optionalFieldOf("enhanced_terrain_adaptation")
         .forGetter(MirroringSingleJigsawPiece::moogs_structures_getAdaptationOverride);
   }

   public MirroringSingleJigsawPiece(SinglePoolElement singleJigsawPiece, Mirror mirror, Optional<LiquidSettings> liquidSettings) {
      this(
         ((SinglePoolElementAccessor)singleJigsawPiece).moogs_structures_getTemplate(),
         ((SinglePoolElementAccessor)singleJigsawPiece).moogs_structures_getProcessors(),
         singleJigsawPiece.getProjection(),
         mirror,
         liquidSettings,
         Optional.empty()
      );
   }

   protected MirroringSingleJigsawPiece(
      Either<ResourceLocation, StructureTemplate> locationTemplateEither,
      Holder<StructureProcessorList> processorListSupplier,
      Projection placementBehaviour,
      Mirror mirror,
      Optional<LiquidSettings> liquidSettings,
      Optional<EnhancedTerrainAdaptation> adaptationOverride
   ) {
      super(locationTemplateEither, processorListSupplier, placementBehaviour, liquidSettings);
      this.mirror = mirror;
      this.adaptationOverride = adaptationOverride;
   }

   @Override
   public Optional<EnhancedTerrainAdaptation> moogs_structures_getAdaptationOverride() {
      return this.adaptationOverride;
   }

   private StructureTemplate getTemplate(StructureTemplateManager templateManager) {
      return (StructureTemplate)this.template.map(templateManager::getOrCreate, Function.identity());
   }

   public List<StructureBlockInfo> getShuffledJigsawBlocks(StructureTemplateManager templateManager, BlockPos blockPos, Rotation rotation, RandomSource random) {
      StructureTemplate template = this.getTemplate(templateManager);
      ObjectArrayList<StructureBlockInfo> list = template.filterBlocks(
         blockPos, new StructurePlaceSettings().setRotation(rotation).setMirror(this.mirror), Blocks.JIGSAW, true
      );
      Util.shuffle(list, random);
      return list;
   }

   public BoundingBox getBoundingBox(StructureTemplateManager templateManager, BlockPos blockPos, Rotation rotation) {
      StructureTemplate template = this.getTemplate(templateManager);
      return template.getBoundingBox(new StructurePlaceSettings().setRotation(rotation).setMirror(this.mirror), blockPos);
   }

   public boolean place(
      StructureTemplateManager templateManager,
      WorldGenLevel worldGenLevel,
      StructureManager StructureTemplateManager,
      ChunkGenerator chunkGenerator,
      BlockPos blockPos,
      BlockPos blockPos1,
      Rotation rotation,
      BoundingBox mutableBoundingBox,
      RandomSource random,
      LiquidSettings liquidSettings,
      boolean doNotReplaceJigsaw
   ) {
      StructureTemplate template = this.getTemplate(templateManager);
      StructurePlaceSettings placementsettings = this.getSettings(rotation, mutableBoundingBox, liquidSettings, doNotReplaceJigsaw);
      if (!template.placeInWorld(worldGenLevel, blockPos, blockPos1, placementsettings, random, 18)) {
         return false;
      } else {
         for (StructureBlockInfo template$blockinfo : StructureTemplate.processBlockInfos(
            worldGenLevel, blockPos, blockPos1, placementsettings, this.getDataMarkers(templateManager, blockPos, rotation, false)
         )) {
            this.handleDataMarker(worldGenLevel, template$blockinfo, blockPos, rotation, random, mutableBoundingBox);
         }

         return true;
      }
   }

   protected StructurePlaceSettings getSettings(Rotation rotation, BoundingBox mutableBoundingBox, LiquidSettings liquidSettings, boolean doNotReplaceJigsaw) {
      StructurePlaceSettings placementsettings = new StructurePlaceSettings();
      placementsettings.setBoundingBox(mutableBoundingBox);
      placementsettings.setRotation(rotation);
      placementsettings.setMirror(this.mirror);
      placementsettings.setIgnoreEntities(false);
      placementsettings.addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
      placementsettings.setFinalizeEntities(true);
      placementsettings.setLiquidSettings(this.overrideLiquidSettings.orElse(liquidSettings));
      if (!doNotReplaceJigsaw) {
         placementsettings.addProcessor(JigsawReplacementProcessor.INSTANCE);
      }

      ((StructureProcessorList)this.processors.value()).list().forEach(placementsettings::addProcessor);
      this.getProjection().getProcessors().forEach(placementsettings::addProcessor);
      return placementsettings;
   }

   public StructurePoolElementType<?> getType() {
      return MoogsStructuresStructurePieces.MIRROR_SINGLE.get();
   }

   @Override
   public String toString() {
      return "Mirror_Single[" + this.template + "]";
   }
}
