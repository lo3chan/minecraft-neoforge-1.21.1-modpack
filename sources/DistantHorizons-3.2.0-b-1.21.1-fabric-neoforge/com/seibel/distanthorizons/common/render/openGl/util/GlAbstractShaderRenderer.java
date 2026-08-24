package com.seibel.distanthorizons.common.render.openGl.util;

import com.seibel.distanthorizons.common.render.openGl.glObject.shader.GlShaderProgram;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import org.lwjgl.opengl.GL33;

public abstract class GlAbstractShaderRenderer {
   protected static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   protected GlShaderProgram shader;
   protected boolean init = false;

   protected GlAbstractShaderRenderer() {
   }

   public void init() {
      if (!this.init) {
         this.init = true;
         this.onInit();
      }
   }

   protected void onInit() {
   }

   protected void onApplyUniforms(RenderParams renderParams) {
   }

   protected void onRender() {
   }

   public void render(RenderParams renderParams) {
      this.init();
      this.shader.bind();
      this.onApplyUniforms(renderParams);
      int width = MC_RENDER.getTargetFramebufferViewportWidth();
      int height = MC_RENDER.getTargetFramebufferViewportHeight();
      GL33.glViewport(0, 0, width, height);
      this.onRender();
      this.shader.unbind();
   }

   public void free() {
      if (this.shader != null) {
         this.shader.free();
      }
   }
}
