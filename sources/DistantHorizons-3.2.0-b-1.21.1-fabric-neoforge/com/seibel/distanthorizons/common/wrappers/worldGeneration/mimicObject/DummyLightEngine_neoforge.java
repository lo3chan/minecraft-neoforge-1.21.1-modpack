package com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.lighting.LayerLightEventListener;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.lighting.LayerLightEventListener.DummyLightLayerEventListener;
import org.jetbrains.annotations.Nullable;

public class DummyLightEngine_neoforge extends LevelLightEngine {
   public DummyLightEngine_neoforge(LightGetterAdaptor_neoforge genRegion) {
      super(genRegion, false, false);
   }

   public int runLightUpdates() {
      return 0;
   }

   public void setLightEnabled(ChunkPos $$0, boolean $$1) {
   }

   public void propagateLightSources(ChunkPos arg) {
   }

   public boolean lightOnInSection(SectionPos $$0) {
      return false;
   }

   public void queueSectionData(LightLayer lightLayer, SectionPos sectionPos, @Nullable DataLayer dataLayer) {
   }

   public void checkBlock(BlockPos blockPos) {
   }

   public boolean hasLightWork() {
      return false;
   }

   public void updateSectionStatus(SectionPos sectionPos, boolean bl) {
   }

   public LayerLightEventListener getLayerListener(LightLayer lightLayer) {
      return DummyLightLayerEventListener.INSTANCE;
   }

   public int getRawBrightness(BlockPos blockPos, int i) {
      return 0;
   }

   public void lightChunk(ChunkAccess chunkAccess, boolean needLightBlockUpdate) {
   }

   public String getDebugData(LightLayer lightLayer, SectionPos sectionPos) {
      throw new UnsupportedOperationException("This should never be used!");
   }

   public void retainData(ChunkPos chunkPos, boolean bl) {
   }

   public int getLightSectionCount() {
      throw new UnsupportedOperationException("This should never be used!");
   }

   public int getMinLightSection() {
      throw new UnsupportedOperationException("This should never be used!");
   }

   public int getMaxLightSection() {
      throw new UnsupportedOperationException("This should never be used!");
   }
}
