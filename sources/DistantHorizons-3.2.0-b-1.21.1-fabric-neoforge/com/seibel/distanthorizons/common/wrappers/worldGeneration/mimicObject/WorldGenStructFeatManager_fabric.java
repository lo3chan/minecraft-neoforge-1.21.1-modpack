package com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.seibel.distanthorizons.common.wrappers.McObjectConverter_fabric;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.class_1923;
import net.minecraft.class_2338;
import net.minecraft.class_2791;
import net.minecraft.class_2806;
import net.minecraft.class_3195;
import net.minecraft.class_3233;
import net.minecraft.class_3449;
import net.minecraft.class_4076;
import net.minecraft.class_5138;
import net.minecraft.class_5281;
import net.minecraft.class_5285;
import net.minecraft.class_6832;

public class WorldGenStructFeatManager_fabric extends class_5138 {
   final class_5281 genLevel;
   class_5285 worldOptions;
   class_6832 structureCheck;

   public WorldGenStructFeatManager_fabric(class_5285 worldOptions, class_5281 genLevel, class_6832 structureCheck) {
      super(genLevel, worldOptions, structureCheck);
      this.genLevel = genLevel;
      this.worldOptions = worldOptions;
   }

   public WorldGenStructFeatManager_fabric forWorldGenRegion(class_3233 worldGenRegion) {
      return worldGenRegion == this.genLevel ? this : new WorldGenStructFeatManager_fabric(this.worldOptions, worldGenRegion, this.structureCheck);
   }

   private class_2791 _getChunk(int x, int z, class_2806 status) {
      return this.genLevel == null ? null : this.genLevel.method_8402(x, z, status, false);
   }

   public boolean method_38852(class_2338 blockPos) {
      class_4076 sectionPos = class_4076.method_18682(blockPos);
      class_2791 chunk = this._getChunk(sectionPos.method_18674(), sectionPos.method_18687(), class_2806.field_16422);
      return chunk == null ? false : chunk.method_38871();
   }

   public List<class_3449> method_41035(class_1923 chunkPos, Predicate<class_3195> predicate) {
      DhChunkPos dhChunkPos = McObjectConverter_fabric.convert(chunkPos);
      class_2791 chunk = this._getChunk(dhChunkPos.getX(), dhChunkPos.getZ(), class_2806.field_16422);
      if (chunk == null) {
         return List.of();
      } else {
         Map<class_3195, LongSet> map = chunk.method_12179();
         Builder<class_3449> builder = ImmutableList.builder();

         for (Entry<class_3195, LongSet> entry : map.entrySet()) {
            class_3195 configuredStructureFeature = entry.getKey();
            if (predicate.test(configuredStructureFeature)) {
               LongSet var10002 = entry.getValue();
               this.method_41032(configuredStructureFeature, var10002, builder::add);
            }
         }

         return builder.build();
      }
   }

   public List<class_3449> method_38853(class_4076 sectionPos, class_3195 structure) {
      class_2791 chunk = this._getChunk(sectionPos.method_18674(), sectionPos.method_18687(), class_2806.field_16422);
      if (chunk == null) {
         return (List<class_3449>)Stream.<class_3449>empty();
      } else {
         LongSet longSet = chunk.method_12180(structure);
         Builder<class_3449> builder = ImmutableList.builder();
         this.method_41032(structure, longSet, builder::add);
         return builder.build();
      }
   }

   public Map<class_3195, LongSet> method_41037(class_2338 blockPos) {
      class_4076 sectionPos = class_4076.method_18682(blockPos);
      class_2791 chunk = this._getChunk(sectionPos.method_18674(), sectionPos.method_18687(), class_2806.field_16422);
      return chunk == null ? (Map)Stream.empty() : chunk.method_12179();
   }
}
