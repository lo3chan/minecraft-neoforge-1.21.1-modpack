package com.seibel.distanthorizons.common.render.openGl;

import com.seibel.distanthorizons.api.enums.config.EDhApiRenderingApi;
import com.seibel.distanthorizons.common.render.openGl.generic.GlGenericObjectRenderer;
import com.seibel.distanthorizons.common.render.openGl.generic.GlGenericObjectVertexContainer;
import com.seibel.distanthorizons.common.render.openGl.glObject.GlDummyUniformData;
import com.seibel.distanthorizons.common.render.openGl.glObject.buffer.GLVertexBuffer;
import com.seibel.distanthorizons.common.render.openGl.postProcessing.fade.GlDhFarFadeRenderer_neoforge;
import com.seibel.distanthorizons.common.render.openGl.postProcessing.fade.GlVanillaFadeRenderer_neoforge;
import com.seibel.distanthorizons.common.render.openGl.postProcessing.fog.GlDhFogRenderer_neoforge;
import com.seibel.distanthorizons.common.render.openGl.postProcessing.ssao.GlDhSSAORenderer_neoforge;
import com.seibel.distanthorizons.common.render.openGl.test.GlTestTriangleRenderer_neoforge;
import com.seibel.distanthorizons.core.render.EDhRenderDepth;
import com.seibel.distanthorizons.core.render.renderer.AbstractDebugWireframeRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.AbstractDhRenderApiDefinition;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.objects.IDhGenericObjectVertexBufferContainer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.objects.ILodContainerUniformBufferWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.objects.IVertexBufferWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhFarFadeRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhFogRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhGenericRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhMetaRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhSsaoRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhTerrainRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhTestTriangleRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhVanillaFadeRenderer;

public class GlDhRenderApiDefinition_neoforge extends AbstractDhRenderApiDefinition {
   @Override
   public String getEngineName() {
      return "OpenGL";
   }

   @Override
   public EDhRenderDepth getRenderDepth() {
      return EDhRenderDepth.FORWARD_Z;
   }

   @Override
   public EDhApiRenderingApi getRenderApi() {
      return EDhApiRenderingApi.OPEN_GL;
   }

   @Override
   public boolean isNativeRenderer() {
      return true;
   }

   @Override
   public IDhMetaRenderer getMetaRenderer() {
      return GlDhMetaRenderer_neoforge.INSTANCE;
   }

   @Override
   public IDhTerrainRenderer getTerrainRenderer() {
      return GlDhTerrainRenderer_neoforge.INSTANCE;
   }

   @Override
   public IDhSsaoRenderer getSsaoRenderer() {
      return GlDhSSAORenderer_neoforge.INSTANCE;
   }

   @Override
   public IDhFogRenderer getFogRenderer() {
      return GlDhFogRenderer_neoforge.INSTANCE;
   }

   @Override
   public IDhFarFadeRenderer getFarFadeRenderer() {
      return GlDhFarFadeRenderer_neoforge.INSTANCE;
   }

   @Override
   public AbstractDebugWireframeRenderer getDebugWireframeRenderer() {
      return GlDhDebugWireframeRenderer.INSTANCE;
   }

   @Override
   public IDhVanillaFadeRenderer getVanillaFadeRenderer() {
      return GlVanillaFadeRenderer_neoforge.INSTANCE;
   }

   @Override
   public IDhTestTriangleRenderer getTestTriangleRenderer() {
      return GlTestTriangleRenderer_neoforge.INSTANCE;
   }

   @Override
   public IDhGenericRenderer createGenericRenderer() {
      return new GlGenericObjectRenderer();
   }

   @Override
   public IVertexBufferWrapper createVboWrapper(String name) {
      return new GLVertexBuffer();
   }

   @Override
   public ILodContainerUniformBufferWrapper createLodContainerUniformWrapper() {
      return new GlDummyUniformData();
   }

   @Override
   public IDhGenericObjectVertexBufferContainer createGenericVboContainer() {
      return new GlGenericObjectVertexContainer();
   }
}
