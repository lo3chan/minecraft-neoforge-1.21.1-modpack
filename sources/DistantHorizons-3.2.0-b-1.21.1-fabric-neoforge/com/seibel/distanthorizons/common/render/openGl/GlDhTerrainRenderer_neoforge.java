package com.seibel.distanthorizons.common.render.openGl;

import com.seibel.distanthorizons.common.render.openGl.terrain.GlDhTerrainShaderProgram_neoforge;
import com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding.LodBufferContainer;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.util.objects.SortedArraySet;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IProfilerWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhTerrainRenderer;

public class GlDhTerrainRenderer_neoforge implements IDhTerrainRenderer {
   public static final GlDhTerrainRenderer_neoforge INSTANCE = new GlDhTerrainRenderer_neoforge();
   private GlDhTerrainShaderProgram_neoforge terrainShaderProgram = null;

   private GlDhTerrainRenderer_neoforge() {
   }

   public GlDhTerrainShaderProgram_neoforge getTerrainShaderProgram() {
      if (this.terrainShaderProgram == null) {
         this.terrainShaderProgram = new GlDhTerrainShaderProgram_neoforge();
      }

      return this.terrainShaderProgram;
   }

   @Override
   public void render(RenderParams renderEventParam, boolean opaquePass, SortedArraySet<LodBufferContainer> bufferContainers, IProfilerWrapper profiler) {
      this.getTerrainShaderProgram();
      this.terrainShaderProgram.tryInit();
      this.terrainShaderProgram.render(renderEventParam, opaquePass, bufferContainers, profiler);
   }
}
