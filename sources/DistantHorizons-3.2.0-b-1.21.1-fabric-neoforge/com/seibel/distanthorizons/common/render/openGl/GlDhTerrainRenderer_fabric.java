package com.seibel.distanthorizons.common.render.openGl;

import com.seibel.distanthorizons.common.render.openGl.terrain.GlDhTerrainShaderProgram_fabric;
import com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding.LodBufferContainer;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.util.objects.SortedArraySet;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IProfilerWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhTerrainRenderer;

public class GlDhTerrainRenderer_fabric implements IDhTerrainRenderer {
   public static final GlDhTerrainRenderer_fabric INSTANCE = new GlDhTerrainRenderer_fabric();
   private GlDhTerrainShaderProgram_fabric terrainShaderProgram = null;

   private GlDhTerrainRenderer_fabric() {
   }

   public GlDhTerrainShaderProgram_fabric getTerrainShaderProgram() {
      if (this.terrainShaderProgram == null) {
         this.terrainShaderProgram = new GlDhTerrainShaderProgram_fabric();
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
