package com.yungnickyoung.minecraft.yungsapi.world.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.api.YungJigsawManager;
import com.yungnickyoung.minecraft.yungsapi.module.StructureTypeModule;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.adaptations.EnhancedTerrainAdaptation;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.adaptations.EnhancedTerrainAdaptationType;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationStub;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import org.jetbrains.annotations.NotNull;

public class YungJigsawStructure extends Structure {
   public static final int MAX_TOTAL_STRUCTURE_RADIUS = 128;
   public static final MapCodec<YungJigsawStructure> CODEC = RecordCodecBuilder.mapCodec(
         builder -> builder.group(
               settingsCodec(builder),
               StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
               ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
               Codec.intRange(0, 128).fieldOf("size").forGetter(structure -> structure.maxDepth),
               HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
               IntProvider.codec(0, 15).optionalFieldOf("x_offset_in_chunk", ConstantInt.of(0)).forGetter(structure -> structure.xOffsetInChunk),
               IntProvider.codec(0, 15).optionalFieldOf("z_offset_in_chunk", ConstantInt.of(0)).forGetter(structure -> structure.zOffsetInChunk),
               Codec.BOOL.optionalFieldOf("use_expansion_hack", false).forGetter(structure -> structure.useExpansionHack),
               Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
               Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
               Codec.INT.optionalFieldOf("max_y").forGetter(structure -> structure.maxY),
               Codec.INT.optionalFieldOf("min_y").forGetter(structure -> structure.minY),
               EnhancedTerrainAdaptationType.ADAPTATION_CODEC
                  .optionalFieldOf("enhanced_terrain_adaptation", EnhancedTerrainAdaptation.NONE)
                  .forGetter(structure -> structure.enhancedTerrainAdaptation),
               DimensionPadding.CODEC.optionalFieldOf("dimension_padding", DimensionPadding.ZERO).forGetter(structure -> structure.dimensionPadding),
               LiquidSettings.CODEC.optionalFieldOf("liquid_settings", LiquidSettings.APPLY_WATERLOGGING).forGetter(structure -> structure.liquidSettings)
            )
            .apply(builder, YungJigsawStructure::new)
      )
      .validate(YungJigsawStructure::validateRange);
   public final Holder<StructureTemplatePool> startPool;
   private final Optional<ResourceLocation> startJigsawName;
   public final int maxDepth;
   public final HeightProvider startHeight;
   public final IntProvider xOffsetInChunk;
   public final IntProvider zOffsetInChunk;
   public final boolean useExpansionHack;
   public final Optional<Types> projectStartToHeightmap;
   public final int maxDistanceFromCenter;
   public final Optional<Integer> maxY;
   public final Optional<Integer> minY;
   public final EnhancedTerrainAdaptation enhancedTerrainAdaptation;
   private final DimensionPadding dimensionPadding;
   private final LiquidSettings liquidSettings;

   public YungJigsawStructure(
      StructureSettings structureSettings,
      Holder<StructureTemplatePool> startPool,
      Optional<ResourceLocation> startJigsawName,
      int maxDepth,
      HeightProvider startHeight,
      IntProvider xOffsetInChunk,
      IntProvider zOffsetInChunk,
      boolean useExpansionHack,
      Optional<Types> projectStartToHeightmap,
      int maxBlockDistanceFromCenter,
      Optional<Integer> maxY,
      Optional<Integer> minY,
      EnhancedTerrainAdaptation enhancedTerrainAdaptation,
      DimensionPadding dimensionPadding,
      LiquidSettings liquidSettings
   ) {
      super(structureSettings);
      this.startPool = startPool;
      this.startJigsawName = startJigsawName;
      this.maxDepth = maxDepth;
      this.startHeight = startHeight;
      this.xOffsetInChunk = xOffsetInChunk;
      this.zOffsetInChunk = zOffsetInChunk;
      this.useExpansionHack = useExpansionHack;
      this.projectStartToHeightmap = projectStartToHeightmap;
      this.maxDistanceFromCenter = maxBlockDistanceFromCenter;
      this.maxY = maxY;
      this.minY = minY;
      this.enhancedTerrainAdaptation = enhancedTerrainAdaptation;
      this.dimensionPadding = dimensionPadding;
      this.liquidSettings = liquidSettings;
   }

   private static DataResult<YungJigsawStructure> validateRange(YungJigsawStructure structure) {
      if (structure.terrainAdaptation() != TerrainAdjustment.NONE && structure.enhancedTerrainAdaptation != EnhancedTerrainAdaptation.NONE) {
         return DataResult.error(() -> "YUNG Structure cannot use both vanilla terrain_adaptation and enhanced_terrain_adaptation");
      } else {
         int vanillaEdgeBuffer = switch (structure.terrainAdaptation()) {
            case NONE -> 0;
            case BURY, BEARD_THIN, BEARD_BOX, ENCAPSULATE -> 12;
            default -> throw new MatchException(null, null);
         };
         if (structure.maxDistanceFromCenter + vanillaEdgeBuffer > 128) {
            return DataResult.error(() -> "YUNG Structure's max_distance_from_center must not exceed 116 when using vanilla terrain_adaptation");
         } else {
            int enhancedEdgeBuffer = structure.enhancedTerrainAdaptation.getKernelRadius();
            return structure.maxDistanceFromCenter + enhancedEdgeBuffer > 128
               ? DataResult.error(
                  () -> "YUNG Structure's max_distance_from_center + kernel radius (equal to half the enhanced_terrain_adaptation's kernel size) must not exceed 128"
               )
               : DataResult.success(structure);
         }
      }
   }

   @NotNull
   public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
      ChunkPos chunkPos = context.chunkPos();
      RandomSource randomSource = context.random();
      int xOffset = this.xOffsetInChunk.sample(randomSource);
      int zOffset = this.zOffsetInChunk.sample(randomSource);
      int startY = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
      BlockPos startPos = new BlockPos(chunkPos.getBlockX(xOffset), startY, chunkPos.getBlockZ(zOffset));
      return YungJigsawManager.assembleJigsawStructure(
         context,
         this.startPool,
         this.startJigsawName,
         this.maxDepth,
         startPos,
         this.useExpansionHack,
         this.projectStartToHeightmap,
         this.maxDistanceFromCenter,
         this.maxY,
         this.minY,
         this.dimensionPadding,
         this.liquidSettings
      );
   }

   @NotNull
   public BoundingBox adjustBoundingBox(@NotNull BoundingBox boundingBox) {
      return super.adjustBoundingBox(boundingBox).inflatedBy(this.enhancedTerrainAdaptation.getKernelRadius());
   }

   @NotNull
   public StructureType<?> type() {
      return StructureTypeModule.YUNG_JIGSAW;
   }
}
