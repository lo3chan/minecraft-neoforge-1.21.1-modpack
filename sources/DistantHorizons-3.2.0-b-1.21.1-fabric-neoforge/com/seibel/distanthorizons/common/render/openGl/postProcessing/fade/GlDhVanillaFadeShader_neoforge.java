package com.seibel.distanthorizons.common.render.openGl.postProcessing.fade;

import com.seibel.distanthorizons.api.objects.math.DhApiMat4f;
import com.seibel.distanthorizons.common.render.openGl.GlDhMetaRenderer_neoforge;
import com.seibel.distanthorizons.common.render.openGl.glObject.shader.GlShaderProgram;
import com.seibel.distanthorizons.common.render.openGl.postProcessing.GlScreenQuad;
import com.seibel.distanthorizons.common.render.openGl.util.GlAbstractShaderRenderer;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.util.RenderUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import org.lwjgl.opengl.GL33;

public class GlDhVanillaFadeShader_neoforge extends GlAbstractShaderRenderer {
   public static GlDhVanillaFadeShader_neoforge INSTANCE = new GlDhVanillaFadeShader_neoforge();
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   public int frameBuffer = -1;
   private DhApiMat4f inverseMcMvmProjMatrix;
   private DhApiMat4f inverseDhMvmProjMatrix;
   private float levelMaxHeight;
   public int uMcDepthTexture = -1;
   public int uDhDepthTexture = -1;
   public int uCombinedMcDhColorTexture = -1;
   public int uDhColorTexture = -1;
   public int uDhInvMvmProj = -1;
   public int uMcInvMvmProj = -1;
   public int uStartFadeBlockDistance = -1;
   public int uEndFadeBlockDistance = -1;
   public int uMaxLevelHeight = -1;
   public int uOnlyRenderLods = -1;

   @Override
   public void onInit() {
      this.shader = new GlShaderProgram(
         "assets/distanthorizons/shaders/shared/gl/quad_apply.vert", "assets/distanthorizons/shaders/fade/gl/vanilla_fade.frag", "vPosition"
      );
      this.uDhInvMvmProj = this.shader.tryGetUniformLocation("uDhInvMvmProj");
      this.uMcInvMvmProj = this.shader.tryGetUniformLocation("uMcInvMvmProj");
      this.uMcDepthTexture = this.shader.tryGetUniformLocation("uMcDepthTexture");
      this.uDhDepthTexture = this.shader.tryGetUniformLocation("uDhDepthTexture");
      this.uCombinedMcDhColorTexture = this.shader.tryGetUniformLocation("uCombinedMcDhColorTexture");
      this.uDhColorTexture = this.shader.tryGetUniformLocation("uDhColorTexture");
      this.uStartFadeBlockDistance = this.shader.tryGetUniformLocation("uStartFadeBlockDistance");
      this.uEndFadeBlockDistance = this.shader.tryGetUniformLocation("uEndFadeBlockDistance");
      this.uMaxLevelHeight = this.shader.tryGetUniformLocation("uMaxLevelHeight");
      this.uOnlyRenderLods = this.shader.tryGetUniformLocation("uOnlyRenderLods");
   }

   @Override
   protected void onApplyUniforms(RenderParams renderParams) {
      this.shader.setUniform(this.uMcInvMvmProj, this.inverseMcMvmProjMatrix);
      this.shader.setUniform(this.uDhInvMvmProj, this.inverseDhMvmProjMatrix);
      float dhNearClipDistance = RenderUtil.getNearClipPlaneInBlocks();
      dhNearClipDistance += 16.0F;
      float fadeStartDistance = dhNearClipDistance * 1.5F;
      float fadeEndDistance = dhNearClipDistance * 1.9F;
      this.shader.setUniform(this.uStartFadeBlockDistance, fadeStartDistance);
      this.shader.setUniform(this.uEndFadeBlockDistance, fadeEndDistance);
      this.shader.setUniform(this.uMaxLevelHeight, this.levelMaxHeight);
      this.shader.setUniform(this.uOnlyRenderLods, Config.Client.Advanced.Debugging.lodOnlyMode.get());
   }

   public void setProjectionMatrix(RenderParams renderParams) {
      this.inverseMcMvmProjMatrix = renderParams.mcInverseMvmProjectionMatrix;
      this.inverseDhMvmProjMatrix = renderParams.dhInverseMvmProjectionMatrix;
   }

   public void setLevelMaxHeight(int levelMaxHeight) {
      this.levelMaxHeight = levelMaxHeight;
   }

   @Override
   protected void onRender() {
      int depthTextureId = GlDhMetaRenderer_neoforge.INSTANCE.getActiveDepthTextureId();
      int colorTextureId = GlDhMetaRenderer_neoforge.INSTANCE.getActiveColorTextureId();
      if (depthTextureId != -1 && colorTextureId != -1) {
         GLMC.glBindFramebuffer(36160, this.frameBuffer);
         GLMC.disableScissorTest();
         GLMC.disableDepthTest();
         GLMC.disableBlend();
         GLMC.glActiveTexture(33984);
         GLMC.glBindTexture(MC_RENDER.getGlDepthTextureId());
         GL33.glUniform1i(this.uMcDepthTexture, 0);
         GLMC.glActiveTexture(33985);
         GLMC.glBindTexture(depthTextureId);
         GL33.glUniform1i(this.uDhDepthTexture, 1);
         GLMC.glActiveTexture(33986);
         GLMC.glBindTexture(MC_RENDER.getGlColorTextureId());
         GL33.glUniform1i(this.uCombinedMcDhColorTexture, 2);
         GLMC.glActiveTexture(33987);
         GLMC.glBindTexture(colorTextureId);
         GL33.glUniform1i(this.uDhColorTexture, 3);
         GlScreenQuad.INSTANCE.render();
      }
   }
}
