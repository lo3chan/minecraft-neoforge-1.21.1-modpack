package com.seibel.distanthorizons.core.wrapperInterfaces.render;

import com.seibel.distanthorizons.api.enums.config.EDhApiRenderingApi;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.jar.EPlatform;
import com.seibel.distanthorizons.core.render.EDhRenderDepth;
import com.seibel.distanthorizons.core.render.renderer.AbstractDebugWireframeRenderer;
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
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;

public abstract class AbstractDhRenderApiDefinition implements IBindable {
   private final boolean useSingleIbo = EPlatform.get() != EPlatform.MACOS;

   public abstract String getEngineName();

   public boolean useSingleIbo() {
      return this.useSingleIbo;
   }

   public abstract EDhRenderDepth getRenderDepth();

   public abstract EDhApiRenderingApi getRenderApi();

   public abstract boolean isNativeRenderer();

   public abstract IDhMetaRenderer getMetaRenderer();

   public abstract IDhTerrainRenderer getTerrainRenderer();

   public abstract IDhSsaoRenderer getSsaoRenderer();

   public abstract IDhFogRenderer getFogRenderer();

   public abstract IDhFarFadeRenderer getFarFadeRenderer();

   public abstract AbstractDebugWireframeRenderer getDebugWireframeRenderer();

   public abstract IDhVanillaFadeRenderer getVanillaFadeRenderer();

   public abstract IDhTestTriangleRenderer getTestTriangleRenderer();

   public void bindRenderers() {
      SingletonInjector.INSTANCE.bind(AbstractDhRenderApiDefinition.class, this);
      SingletonInjector.INSTANCE.bind(IDhMetaRenderer.class, this.getMetaRenderer());
      SingletonInjector.INSTANCE.bind(IDhTerrainRenderer.class, this.getTerrainRenderer());
      SingletonInjector.INSTANCE.bind(IDhSsaoRenderer.class, this.getSsaoRenderer());
      SingletonInjector.INSTANCE.bind(IDhFogRenderer.class, this.getFogRenderer());
      SingletonInjector.INSTANCE.bind(IDhFarFadeRenderer.class, this.getFarFadeRenderer());
      SingletonInjector.INSTANCE.bind(AbstractDebugWireframeRenderer.class, this.getDebugWireframeRenderer());
      SingletonInjector.INSTANCE.bind(IDhVanillaFadeRenderer.class, this.getVanillaFadeRenderer());
      SingletonInjector.INSTANCE.bind(IDhTestTriangleRenderer.class, this.getTestTriangleRenderer());
   }

   public abstract IDhGenericRenderer createGenericRenderer();

   public abstract IVertexBufferWrapper createVboWrapper(String string);

   public abstract ILodContainerUniformBufferWrapper createLodContainerUniformWrapper();

   public abstract IDhGenericObjectVertexBufferContainer createGenericVboContainer();
}
