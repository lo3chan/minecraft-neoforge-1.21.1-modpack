package com.finndog.moogs_structures.utils;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public final class MixinUtils {
   private static final int STRUCTURE_PIECE_MARGIN = 4;

   private MixinUtils() {
   }

   public static boolean isPositionInTaggedStructure(WorldGenRegion worldGenRegion, BlockPos pos, TagKey<Structure> structureTagKey) {
      Registry<Structure> structureRegistry = worldGenRegion.registryAccess().registryOrThrow(Registries.STRUCTURE);
      SectionPos sectionPos = SectionPos.of(pos);
      ChunkAccess chunkAccess = worldGenRegion.getChunk(sectionPos.x(), sectionPos.z(), ChunkStatus.STRUCTURE_REFERENCES);
      if (!chunkAccess.getHighestGeneratedStatus().isOrAfter(ChunkStatus.STRUCTURE_REFERENCES)) {
         return false;
      } else {
         Map<Structure, LongSet> allReferencesInChunk = chunkAccess.getAllReferences();

         for (Entry<Structure, LongSet> entry : allReferencesInChunk.entrySet()) {
            Structure structure = entry.getKey();
            LongSet references = entry.getValue();
            Optional<ResourceKey<Structure>> structureKey = structureRegistry.getResourceKey(structure);
            boolean isTaggedStructure = structureKey.isPresent() && structureRegistry.getHolderOrThrow(structureKey.get()).is(structureTagKey);
            if (isTaggedStructure
               && isAnyReferenceValidStartForStructure(worldGenRegion, structure, references, structureStart -> isPositionNearAnyPiece(structureStart, pos))) {
               return true;
            }
         }

         return false;
      }
   }

   private static boolean isPositionNearAnyPiece(StructureStart structureStart, BlockPos pos) {
      if (!structureStart.getBoundingBox().isInside(pos)) {
         return false;
      } else {
         for (StructurePiece piece : structureStart.getPieces()) {
            if (piece.getBoundingBox().inflatedBy(4).isInside(pos)) {
               return true;
            }
         }

         return false;
      }
   }

   private static boolean isAnyReferenceValidStartForStructure(
      WorldGenRegion worldGenRegion, Structure structure, LongSet references, Predicate<StructureStart> filter
   ) {
      StructureManager structureManager = worldGenRegion.getLevel().structureManager();
      LongIterator var5 = references.iterator();

      while (var5.hasNext()) {
         long reference = (Long)var5.next();
         SectionPos structureStartSectionPos = SectionPos.of(new ChunkPos(reference), worldGenRegion.getMinSection());
         if (worldGenRegion.hasChunk(structureStartSectionPos.x(), structureStartSectionPos.z())) {
            ChunkAccess structureStartChunkAccess = worldGenRegion.getChunk(
               structureStartSectionPos.x(), structureStartSectionPos.z(), ChunkStatus.STRUCTURE_STARTS
            );
            StructureStart structureStart = structureManager.getStartForStructure(structureStartSectionPos, structure, structureStartChunkAccess);
            if (structureStart != null && structureStart.isValid() && filter.test(structureStart)) {
               return true;
            }
         }
      }

      return false;
   }
}
