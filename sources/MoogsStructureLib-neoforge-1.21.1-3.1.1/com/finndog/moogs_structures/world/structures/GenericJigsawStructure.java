package com.finndog.moogs_structures.world.structures;

import com.finndog.moogs_structures.modinit.MoogsStructuresStructures;
import com.finndog.moogs_structures.utils.GeneralUtils;
import com.finndog.moogs_structures.world.structures.codecs.YRangeAllowance;
import com.finndog.moogs_structures.world.structures.pieces.PieceLimitedJigsawManager;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationStub;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

public class GenericJigsawStructure extends Structure {
   public static final MapCodec<GenericJigsawStructure> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            Codec.intRange(0, 128).fieldOf("size").forGetter(structure -> structure.size),
            YRangeAllowance.CODEC.optionalFieldOf("y_allowance").forGetter(structure -> structure.yAllowance),
            HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
            Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
            Codec.BOOL.fieldOf("cannot_spawn_in_liquid").orElse(false).forGetter(structure -> structure.cannotSpawnInLiquid),
            Codec.intRange(1, 100).optionalFieldOf("terrain_height_radius_check").forGetter(structure -> structure.terrainHeightCheckRadius),
            Codec.intRange(1, 1000).optionalFieldOf("allowed_terrain_height_range").forGetter(structure -> structure.allowedTerrainHeightRange),
            Codec.intRange(1, 100).optionalFieldOf("valid_biome_radius_check").forGetter(structure -> structure.biomeRadius),
            ResourceLocation.CODEC
               .listOf()
               .fieldOf("pools_that_ignore_boundaries")
               .orElse(new ArrayList())
               .xmap(HashSet::new, ArrayList::new)
               .forGetter(structure -> structure.poolsThatIgnoreBoundaries),
            Codec.intRange(1, 128).optionalFieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
            StringRepresentable.fromEnum(GenericJigsawStructure.BURYING_TYPE::values)
               .optionalFieldOf("burying_type")
               .forGetter(structure -> structure.buryingType),
            Codec.BOOL.fieldOf("use_bounding_box_hack").orElse(false).forGetter(structure -> structure.useBoundingBoxHack),
            LiquidSettings.CODEC.optionalFieldOf("liquid_settings", JigsawStructure.DEFAULT_LIQUID_SETTINGS).forGetter(structure -> structure.liquidSettings)
         )
         .apply(instance, GenericJigsawStructure::new)
   );
   public final Holder<StructureTemplatePool> startPool;
   public final int size;
   public final Optional<YRangeAllowance> yAllowance;
   public final HeightProvider startHeight;
   public final Optional<Types> projectStartToHeightmap;
   public final boolean cannotSpawnInLiquid;
   public final Optional<Integer> terrainHeightCheckRadius;
   public final Optional<Integer> allowedTerrainHeightRange;
   public final Optional<Integer> biomeRadius;
   public final HashSet<ResourceLocation> poolsThatIgnoreBoundaries;
   public final Optional<Integer> maxDistanceFromCenter;
   public final Optional<GenericJigsawStructure.BURYING_TYPE> buryingType;
   public final boolean useBoundingBoxHack;
   public final LiquidSettings liquidSettings;

   public GenericJigsawStructure(
      StructureSettings config,
      Holder<StructureTemplatePool> startPool,
      int size,
      Optional<YRangeAllowance> yAllowance,
      HeightProvider startHeight,
      Optional<Types> projectStartToHeightmap,
      boolean cannotSpawnInLiquid,
      Optional<Integer> terrainHeightCheckRadius,
      Optional<Integer> allowedTerrainHeightRange,
      Optional<Integer> biomeRadius,
      HashSet<ResourceLocation> poolsThatIgnoreBoundaries,
      Optional<Integer> maxDistanceFromCenter,
      Optional<GenericJigsawStructure.BURYING_TYPE> buryingType,
      boolean useBoundingBoxHack,
      LiquidSettings liquidSettings
   ) {
      super(config);
      this.startPool = startPool;
      this.size = size;
      this.yAllowance = yAllowance;
      this.startHeight = startHeight;
      this.projectStartToHeightmap = projectStartToHeightmap;
      this.cannotSpawnInLiquid = cannotSpawnInLiquid;
      this.terrainHeightCheckRadius = terrainHeightCheckRadius;
      this.allowedTerrainHeightRange = allowedTerrainHeightRange;
      this.biomeRadius = biomeRadius;
      this.poolsThatIgnoreBoundaries = poolsThatIgnoreBoundaries;
      this.maxDistanceFromCenter = maxDistanceFromCenter;
      this.buryingType = buryingType;
      this.useBoundingBoxHack = useBoundingBoxHack;
      this.liquidSettings = liquidSettings;
      if (yAllowance.isPresent()
         && yAllowance.get().maxYAllowed.isPresent()
         && yAllowance.get().minYAllowed.isPresent()
         && yAllowance.get().maxYAllowed.get() < yAllowance.get().minYAllowed.get()) {
         throw new RuntimeException(
            "    Moog's Structure Lib: maxYAllowed cannot be less than minYAllowed.\n    Please correct this error as there's no way to spawn this structure properly\n        Structure pool of problematic structure: %s\n"
               .formatted(startPool.value())
         );
      }
   }

   protected boolean extraSpawningChecks(GenerationContext context, BlockPos blockPos) {
      ChunkPos chunkPos = context.chunkPos();
      if (this.biomeRadius.isPresent() && !(context.biomeSource() instanceof CheckerboardColumnBiomeSource)) {
         int validBiomeRange = this.biomeRadius.get();
         int sectionY = blockPos.getY();
         if (this.projectStartToHeightmap.isPresent()) {
            sectionY += GeneralUtils.getCachedFreeHeight(
                  context.chunkGenerator(),
                  blockPos.getX() + 7,
                  blockPos.getZ() + 7,
                  this.projectStartToHeightmap.get(),
                  context.heightAccessor(),
                  context.randomState()
               )
               - 1;
         }

         sectionY = QuartPos.fromBlock(sectionY);

         for (int curChunkX = chunkPos.x - validBiomeRange; curChunkX <= chunkPos.x + validBiomeRange; curChunkX++) {
            for (int curChunkZ = chunkPos.z - validBiomeRange; curChunkZ <= chunkPos.z + validBiomeRange; curChunkZ++) {
               Holder<Biome> biome = context.biomeSource()
                  .getNoiseBiome(QuartPos.fromSection(curChunkX), sectionY, QuartPos.fromSection(curChunkZ), context.randomState().sampler());
               if (!context.validBiome().test(biome)) {
                  return false;
               }
            }
         }
      }

      if (this.cannotSpawnInLiquid) {
         BlockPos centerOfChunk = chunkPos.getMiddleBlockPosition(0);
         ChunkGenerator chunkGenerator = context.chunkGenerator();
         NoiseColumn columnOfBlocks = chunkGenerator.getBaseColumn(centerOfChunk.getX(), centerOfChunk.getZ(), context.heightAccessor(), context.randomState());
         BlockState topBlock = Blocks.AIR.defaultBlockState();

         for (int i = chunkGenerator.getMinY() + chunkGenerator.getGenDepth(); i > chunkGenerator.getMinY(); i--) {
            BlockState block = columnOfBlocks.getBlock(i);
            if (!block.isAir()) {
               topBlock = block;
               break;
            }
         }

         if (!topBlock.getFluidState().isEmpty()) {
            return false;
         }
      }

      if (this.terrainHeightCheckRadius.isPresent()
         && (this.allowedTerrainHeightRange.isPresent() || this.yAllowance.isPresent() && this.yAllowance.get().minYAllowed.isPresent())) {
         int maxTerrainHeight = -2147483648;
         int minTerrainHeight = 2147483647;
         int terrainCheckRange = this.terrainHeightCheckRadius.get();

         for (int curChunkX = chunkPos.x - terrainCheckRange; curChunkX <= chunkPos.x + terrainCheckRange; curChunkX++) {
            for (int curChunkZx = chunkPos.z - terrainCheckRange; curChunkZx <= chunkPos.z + terrainCheckRange; curChunkZx++) {
               int height = GeneralUtils.getCachedFreeHeight(
                  context.chunkGenerator(),
                  (curChunkX << 4) + 7,
                  (curChunkZx << 4) + 7,
                  this.projectStartToHeightmap.orElse(Types.WORLD_SURFACE_WG),
                  context.heightAccessor(),
                  context.randomState()
               );
               maxTerrainHeight = Math.max(maxTerrainHeight, height);
               minTerrainHeight = Math.min(minTerrainHeight, height);
               if (this.yAllowance.isPresent() && this.yAllowance.get().minYAllowed.isPresent() && minTerrainHeight < this.yAllowance.get().minYAllowed.get()) {
                  return false;
               }

               if (this.yAllowance.isPresent() && this.yAllowance.get().maxYAllowed.isPresent() && minTerrainHeight > this.yAllowance.get().maxYAllowed.get()) {
                  return false;
               }
            }
         }

         if (this.allowedTerrainHeightRange.isPresent() && maxTerrainHeight - minTerrainHeight > this.allowedTerrainHeightRange.get()) {
            return false;
         }
      }

      return true;
   }

   public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
      int offsetY = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
      BlockPos blockpos = new BlockPos(context.chunkPos().getMinBlockX(), offsetY, context.chunkPos().getMinBlockZ());
      if (!this.extraSpawningChecks(context, blockpos)) {
         return Optional.empty();
      } else {
         int topClipOff = 2147483647;
         int bottomClipOff = -2147483648;
         if (this.yAllowance.isPresent()) {
            if (this.yAllowance.get().maxYAllowed.isPresent()) {
               topClipOff = Math.min(topClipOff, this.yAllowance.get().maxYAllowed.get());
            }

            if (this.yAllowance.get().minYAllowed.isPresent()) {
               bottomClipOff = Math.max(bottomClipOff, this.yAllowance.get().minYAllowed.get());
            }
         }

         int finalTopClipOff = topClipOff;
         int finalBottomClipOff = bottomClipOff;
         return PieceLimitedJigsawManager.assembleJigsawStructure(
            context,
            this.startPool,
            this.size,
            context.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(this),
            blockpos,
            this.useBoundingBoxHack,
            this.projectStartToHeightmap,
            topClipOff,
            bottomClipOff,
            this.poolsThatIgnoreBoundaries,
            this.maxDistanceFromCenter,
            this.buryingType,
            this.liquidSettings,
            (structurePiecesBuilder, pieces) -> this.postLayoutAdjustments(
               structurePiecesBuilder, context, offsetY, blockpos, finalTopClipOff, finalBottomClipOff, pieces
            )
         );
      }
   }

   protected void postLayoutAdjustments(
      StructurePiecesBuilder structurePiecesBuilder,
      GenerationContext context,
      int offsetY,
      BlockPos blockpos,
      int topClipOff,
      int bottomClipOff,
      List<PoolElementStructurePiece> pieces
   ) {
      GeneralUtils.centerAllPieces(blockpos, pieces);
      if (!this.buryingType.isEmpty()) {
         if (this.buryingType.get() == GenericJigsawStructure.BURYING_TYPE.LOWEST_CORNER) {
            Types heightMapToUse = this.projectStartToHeightmap.orElse(Types.WORLD_SURFACE_WG);
            BoundingBox box = pieces.get(0).getBoundingBox();
            int highestLandPos = GeneralUtils.getCachedFreeHeight(
                  context.chunkGenerator(), box.minX(), box.minZ(), heightMapToUse, context.heightAccessor(), context.randomState()
               )
               - 1;
            highestLandPos = Math.min(
               highestLandPos,
               GeneralUtils.getCachedFreeHeight(
                     context.chunkGenerator(), box.minX(), box.maxZ(), heightMapToUse, context.heightAccessor(), context.randomState()
                  )
                  - 1
            );
            highestLandPos = Math.min(
               highestLandPos,
               GeneralUtils.getCachedFreeHeight(
                     context.chunkGenerator(), box.maxX(), box.minZ(), heightMapToUse, context.heightAccessor(), context.randomState()
                  )
                  - 1
            );
            highestLandPos = Math.min(
               highestLandPos,
               GeneralUtils.getCachedFreeHeight(
                     context.chunkGenerator(), box.maxX(), box.maxZ(), heightMapToUse, context.heightAccessor(), context.randomState()
                  )
                  - 1
            );
            if (this.cannotSpawnInLiquid || heightMapToUse != Types.OCEAN_FLOOR_WG && heightMapToUse != Types.OCEAN_FLOOR) {
               highestLandPos = Math.max(highestLandPos, context.chunkGenerator().getSeaLevel());
            } else {
               int maxHeightForSubmerging = context.chunkGenerator().getSeaLevel() - box.getYSpan();
               highestLandPos = Math.min(highestLandPos, maxHeightForSubmerging);
            }

            this.offsetToNewHeight(context, offsetY, pieces, box, highestLandPos);
         } else if (this.buryingType.get() == GenericJigsawStructure.BURYING_TYPE.AVERAGE_LAND) {
            BoundingBox box = pieces.get(0).getBoundingBox();
            BlockPos centerPos = new BlockPos(box.getCenter());
            int radius = (int)Math.sqrt(box.getLength().getX() * box.getLength().getX() + box.getLength().getZ() * box.getLength().getZ()) / 2;
            Types heightMapToUse = this.projectStartToHeightmap.orElse(Types.WORLD_SURFACE_WG);
            List<Integer> landHeights = new ArrayList<>();

            for (int xOffset = -radius; xOffset <= radius; xOffset += radius / 2) {
               for (int zOffset = -radius; zOffset <= radius; zOffset += radius / 2) {
                  int landHeight = GeneralUtils.getCachedFreeHeight(
                        context.chunkGenerator(),
                        centerPos.getX() + xOffset,
                        centerPos.getZ() + zOffset,
                        heightMapToUse,
                        context.heightAccessor(),
                        context.randomState()
                     )
                     - 1;
                  landHeights.add(landHeight);
               }
            }

            int minYAllowed;
            int maxYAllowed;
            if (this.yAllowance.isPresent()) {
               minYAllowed = this.yAllowance.get().minYAllowed.orElse(-2147483648);
               maxYAllowed = this.yAllowance.get().maxYAllowed.orElse(2147483647);
            } else {
               maxYAllowed = 2147483647;
               minYAllowed = -2147483648;
            }

            OptionalDouble avgHeightOptional = landHeights.stream()
               .filter(height -> height > minYAllowed && height < maxYAllowed)
               .mapToInt(Integer::intValue)
               .average();
            if (this.yAllowance.isPresent()) {
               if (this.yAllowance.get().maxYAllowed.isPresent() && avgHeightOptional.isEmpty()) {
                  avgHeightOptional = OptionalDouble.of(this.yAllowance.get().maxYAllowed.get().intValue());
               }

               if (this.yAllowance.get().minYAllowed.isPresent() && avgHeightOptional.isEmpty()) {
                  avgHeightOptional = OptionalDouble.of(this.yAllowance.get().minYAllowed.get().intValue());
               }
            }

            if (avgHeightOptional.isPresent()) {
               double avgHeight = avgHeightOptional.getAsDouble();
               if (this.cannotSpawnInLiquid && heightMapToUse != Types.OCEAN_FLOOR_WG && heightMapToUse != Types.OCEAN_FLOOR) {
                  avgHeight = Math.max(avgHeight, (double)context.chunkGenerator().getSeaLevel());
                  if (this.yAllowance.isPresent() && this.yAllowance.get().maxYAllowed.isPresent()) {
                     avgHeight = Math.max(avgHeight, (double)this.yAllowance.get().maxYAllowed.get().intValue());
                  }
               }

               int parentHeight = pieces.get(0).getBoundingBox().minY();
               int offsetAmount = (int)avgHeight - parentHeight + offsetY;
               pieces.forEach(child -> child.move(0, offsetAmount, 0));
            } else {
               pieces.clear();
            }
         } else if (this.buryingType.get() == GenericJigsawStructure.BURYING_TYPE.LOWEST_SIDE) {
            Types heightMapToUse = this.projectStartToHeightmap.orElse(Types.WORLD_SURFACE_WG);
            BoundingBox box = pieces.get(0).getBoundingBox();
            BlockPos centerPos = box.getCenter();
            int highestLandPos = 2147483647;
            Optional<Integer> minYAllowedx = Optional.empty();
            if (this.yAllowance.isPresent()) {
               minYAllowedx = this.yAllowance.get().minYAllowed;
            }

            highestLandPos = this.terrainHeight(context, heightMapToUse, box.minX(), centerPos.getZ(), minYAllowedx, highestLandPos);
            highestLandPos = this.terrainHeight(context, heightMapToUse, centerPos.getX(), box.maxZ(), minYAllowedx, highestLandPos);
            highestLandPos = this.terrainHeight(context, heightMapToUse, centerPos.getX(), box.minZ(), minYAllowedx, highestLandPos);
            highestLandPos = this.terrainHeight(context, heightMapToUse, box.maxX(), centerPos.getZ(), minYAllowedx, highestLandPos);
            if (minYAllowedx.isPresent() && highestLandPos == 2147483647) {
               highestLandPos = minYAllowedx.get();
            }

            if (this.cannotSpawnInLiquid || heightMapToUse != Types.OCEAN_FLOOR_WG && heightMapToUse != Types.OCEAN_FLOOR) {
               highestLandPos = Math.max(highestLandPos, context.chunkGenerator().getSeaLevel());
            } else {
               int maxHeightForSubmerging = context.chunkGenerator().getSeaLevel() - box.getYSpan();
               highestLandPos = Math.min(highestLandPos, maxHeightForSubmerging);
            }

            this.offsetToNewHeight(context, offsetY, pieces, box, highestLandPos);
         }
      }
   }

   private int terrainHeight(GenerationContext context, Types heightMapToUse, int x, int z, Optional<Integer> minYAllowed, int highestLandPos) {
      int landPos = context.chunkGenerator().getFirstOccupiedHeight(x, z, heightMapToUse, context.heightAccessor(), context.randomState());
      if (minYAllowed.isPresent()) {
         if (landPos >= minYAllowed.get()) {
            highestLandPos = landPos;
         }
      } else {
         highestLandPos = Math.min(highestLandPos, landPos);
      }

      return highestLandPos;
   }

   private void offsetToNewHeight(GenerationContext context, int offsetY, List<PoolElementStructurePiece> pieces, BoundingBox box, int highestLandPos) {
      if (this.yAllowance.isPresent()) {
         if (this.yAllowance.get().maxYAllowed.isPresent() && box.maxY() + offsetY < this.yAllowance.get().minYAllowed.get()) {
            highestLandPos = Math.max(highestLandPos, this.yAllowance.get().maxYAllowed.get());
         }

         if (this.yAllowance.get().minYAllowed.isPresent() && box.minY() + offsetY < this.yAllowance.get().minYAllowed.get()) {
            highestLandPos = Math.min(highestLandPos, this.yAllowance.get().minYAllowed.get());
         }
      }

      WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
      random.setLargeFeatureSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
      int heightDiff = highestLandPos - box.minY();

      for (StructurePiece structurePiece : pieces) {
         structurePiece.move(0, heightDiff + offsetY, 0);
      }
   }

   public StructureType<?> type() {
      return MoogsStructuresStructures.GENERIC_JIGSAW_STRUCTURE.get();
   }

   public static enum BURYING_TYPE implements StringRepresentable {
      LOWEST_CORNER("LOWEST_CORNER"),
      AVERAGE_LAND("AVERAGE_LAND"),
      LOWEST_SIDE("LOWEST_SIDE");

      private final String name;
      private static final Map<String, GenericJigsawStructure.BURYING_TYPE> BY_NAME = (Map<String, GenericJigsawStructure.BURYING_TYPE>)Util.make(
         Maps.newHashMap(), hashMap -> {
            GenericJigsawStructure.BURYING_TYPE[] var1 = values();

            for (GenericJigsawStructure.BURYING_TYPE type : var1) {
               hashMap.put(type.name, type);
            }
         }
      );

      private BURYING_TYPE(String name) {
         this.name = name;
      }

      public static GenericJigsawStructure.BURYING_TYPE byName(String name) {
         return BY_NAME.get(name.toUpperCase(Locale.ROOT));
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
