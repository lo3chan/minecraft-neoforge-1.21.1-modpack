package com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.seibel.distanthorizons.common.wrappers.McObjectConverter_neoforge;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class WorldGenStructFeatManager_neoforge extends StructureManager {
   final WorldGenLevel genLevel;
   WorldOptions worldOptions;
   StructureCheck structureCheck;

   public WorldGenStructFeatManager_neoforge(WorldOptions worldOptions, WorldGenLevel genLevel, StructureCheck structureCheck) {
      super(genLevel, worldOptions, structureCheck);
      this.genLevel = genLevel;
      this.worldOptions = worldOptions;
   }

   public WorldGenStructFeatManager_neoforge forWorldGenRegion(WorldGenRegion worldGenRegion) {
      return worldGenRegion == this.genLevel ? this : new WorldGenStructFeatManager_neoforge(this.worldOptions, worldGenRegion, this.structureCheck);
   }

   private ChunkAccess _getChunk(int x, int z, ChunkStatus status) {
      return this.genLevel == null ? null : this.genLevel.getChunk(x, z, status, false);
   }

   public boolean hasAnyStructureAt(BlockPos blockPos) {
      SectionPos sectionPos = SectionPos.of(blockPos);
      ChunkAccess chunk = this._getChunk(sectionPos.x(), sectionPos.z(), ChunkStatus.STRUCTURE_REFERENCES);
      return chunk == null ? false : chunk.hasAnyStructureReferences();
   }

   public List<StructureStart> startsForStructure(ChunkPos chunkPos, Predicate<Structure> predicate) {
      DhChunkPos dhChunkPos = McObjectConverter_neoforge.convert(chunkPos);
      ChunkAccess chunk = this._getChunk(dhChunkPos.getX(), dhChunkPos.getZ(), ChunkStatus.STRUCTURE_REFERENCES);
      if (chunk == null) {
         return List.of();
      } else {
         Map<Structure, LongSet> map = chunk.getAllReferences();
         Builder<StructureStart> builder = ImmutableList.builder();

         for (Entry<Structure, LongSet> entry : map.entrySet()) {
            Structure configuredStructureFeature = entry.getKey();
            if (predicate.test(configuredStructureFeature)) {
               LongSet var10002 = entry.getValue();
               this.fillStartsForStructure(configuredStructureFeature, var10002, builder::add);
            }
         }

         return builder.build();
      }
   }

   public List<StructureStart> startsForStructure(SectionPos sectionPos, Structure structure) {
      ChunkAccess chunk = this._getChunk(sectionPos.x(), sectionPos.z(), ChunkStatus.STRUCTURE_REFERENCES);
      if (chunk == null) {
         return (List<StructureStart>)Stream.<StructureStart>empty();
      } else {
         LongSet longSet = chunk.getReferencesForStructure(structure);
         Builder<StructureStart> builder = ImmutableList.builder();
         this.fillStartsForStructure(structure, longSet, builder::add);
         return builder.build();
      }
   }

   public Map<Structure, LongSet> getAllStructuresAt(BlockPos blockPos) {
      SectionPos sectionPos = SectionPos.of(blockPos);
      ChunkAccess chunk = this._getChunk(sectionPos.x(), sectionPos.z(), ChunkStatus.STRUCTURE_REFERENCES);
      return chunk == null ? (Map)Stream.empty() : chunk.getAllReferences();
   }
}
