package com.seibel.distanthorizons.common.render.openGl.postProcessing.fog;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiHeightFogDirection;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiHeightFogMixMode;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiFogRenderParam;
import com.seibel.distanthorizons.api.objects.math.DhApiMat4f;
import com.seibel.distanthorizons.common.render.openGl.GlDhMetaRenderer_neoforge;
import com.seibel.distanthorizons.common.render.openGl.glObject.shader.GlShaderProgram;
import com.seibel.distanthorizons.common.render.openGl.postProcessing.GlScreenQuad;
import com.seibel.distanthorizons.common.render.openGl.util.GlAbstractShaderRenderer;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.util.math.DhMat4f;
import org.lwjgl.opengl.GL33;

public class GlDhFogShader_neoforge extends GlAbstractShaderRenderer {
   public static final GlDhFogShader_neoforge INSTANCE = new GlDhFogShader_neoforge();
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   public int frameBuffer;
   private DhMat4f inverseMvmProjMatrix;
   private DhApiFogRenderParam fogRenderParams;
   public int uDepthMap;
   public int uInvMvmProj;
   public int uFogColor;
   public int uFogScale;
   public int uFogVerticalScale;
   public int uFogDebugMode;
   public int uFogFalloffType;
   public int uFarFogStart;
   public int uFarFogLength;
   public int uFarFogMin;
   public int uFarFogRange;
   public int uFarFogDensity;
   public int uHeightFogStart;
   public int uHeightFogLength;
   public int uHeightFogMin;
   public int uHeightFogRange;
   public int uHeightFogDensity;
   public int uHeightFogEnabled;
   public int uHeightFogFalloffType;
   public int uHeightBasedOnCamera;
   public int uHeightFogBaseHeight;
   public int uHeightFogAppliesUp;
   public int uHeightFogAppliesDown;
   public int uUseSphericalFog;
   public int uHeightFogMixingMode;
   public int uCameraBlockYPos;

   @Override
   public void onInit() {
      this.shader = new GlShaderProgram(
         "assets/distanthorizons/shaders/shared/gl/quad_apply.vert", "assets/distanthorizons/shaders/fog/gl/fog.frag", "vPosition"
      );
      this.uDepthMap = this.shader.getUniformLocation("uDepthMap");
      this.uInvMvmProj = this.shader.getUniformLocation("uInvMvmProj");
      this.uFogScale = this.shader.getUniformLocation("uFogScale");
      this.uFogVerticalScale = this.shader.getUniformLocation("uFogVerticalScale");
      this.uFogColor = this.shader.getUniformLocation("uFogColor");
      this.uFogDebugMode = this.shader.getUniformLocation("uFogDebugMode");
      this.uFogFalloffType = this.shader.getUniformLocation("uFogFalloffType");
      this.uFarFogStart = this.shader.getUniformLocation("uFarFogStart");
      this.uFarFogLength = this.shader.getUniformLocation("uFarFogLength");
      this.uFarFogMin = this.shader.getUniformLocation("uFarFogMin");
      this.uFarFogRange = this.shader.getUniformLocation("uFarFogRange");
      this.uFarFogDensity = this.shader.getUniformLocation("uFarFogDensity");
      this.uHeightFogStart = this.shader.getUniformLocation("uHeightFogStart");
      this.uHeightFogLength = this.shader.getUniformLocation("uHeightFogLength");
      this.uHeightFogMin = this.shader.getUniformLocation("uHeightFogMin");
      this.uHeightFogRange = this.shader.getUniformLocation("uHeightFogRange");
      this.uHeightFogDensity = this.shader.getUniformLocation("uHeightFogDensity");
      this.uHeightFogEnabled = this.shader.getUniformLocation("uHeightFogEnabled");
      this.uHeightFogFalloffType = this.shader.getUniformLocation("uHeightFogFalloffType");
      this.uHeightBasedOnCamera = this.shader.getUniformLocation("uHeightBasedOnCamera");
      this.uHeightFogBaseHeight = this.shader.getUniformLocation("uHeightFogBaseHeight");
      this.uHeightFogAppliesUp = this.shader.getUniformLocation("uHeightFogAppliesUp");
      this.uHeightFogAppliesDown = this.shader.getUniformLocation("uHeightFogAppliesDown");
      this.uUseSphericalFog = this.shader.getUniformLocation("uUseSphericalFog");
      this.uHeightFogMixingMode = this.shader.getUniformLocation("uHeightFogMixingMode");
      this.uCameraBlockYPos = this.shader.getUniformLocation("uCameraBlockYPos");
   }

   @Override
   protected void onApplyUniforms(RenderParams renderParams) {
      int lodDrawDistance = Config.Client.Advanced.Graphics.Quality.lodChunkRenderDistanceRadius.get() * 16;
      this.shader.setUniform(this.uInvMvmProj, this.inverseMvmProjMatrix);
      this.shader.setUniform(this.uFogColor, this.fogRenderParams.getFogColor());
      this.shader.setUniform(this.uFogScale, 1.0F / lodDrawDistance);
      this.shader.setUniform(this.uFogVerticalScale, 1.0F / renderParams.clientLevelWrapper.getMaxHeight());
      this.shader.setUniform(this.uFogDebugMode, 0);
      this.shader.setUniform(this.uFogFalloffType, this.fogRenderParams.getFarFogFalloff().value);
      this.shader.setUniform(this.uFarFogStart, this.fogRenderParams.getFarFogStartPercent());
      this.shader.setUniform(this.uFarFogLength, this.fogRenderParams.getFarFogEndPercent() - this.fogRenderParams.getFarFogStartPercent());
      this.shader.setUniform(this.uFarFogMin, this.fogRenderParams.getFarFogMinThickness());
      this.shader.setUniform(this.uFarFogRange, this.fogRenderParams.getFarFogMaxThickness() - this.fogRenderParams.getFarFogMinThickness());
      this.shader.setUniform(this.uFarFogDensity, this.fogRenderParams.getFarFogDensity());
      EDhApiHeightFogMixMode heightFogMixingMode = this.fogRenderParams.getHeightFogMixingMode();
      boolean heightFogEnabled = heightFogMixingMode != EDhApiHeightFogMixMode.SPHERICAL && heightFogMixingMode != EDhApiHeightFogMixMode.CYLINDRICAL;
      boolean useSphericalFog = heightFogMixingMode == EDhApiHeightFogMixMode.SPHERICAL;
      EDhApiHeightFogDirection heightFogDirection = this.fogRenderParams.getHeightFogDirection();
      this.shader.setUniform(this.uHeightFogStart, this.fogRenderParams.getHeightFogStartPercent());
      this.shader.setUniform(this.uHeightFogLength, this.fogRenderParams.getHeightFogEndPercent() - this.fogRenderParams.getHeightFogStartPercent());
      this.shader.setUniform(this.uHeightFogMin, this.fogRenderParams.getFarFogMinThickness());
      this.shader.setUniform(this.uHeightFogRange, this.fogRenderParams.getFarFogMaxThickness() - this.fogRenderParams.getFarFogMinThickness());
      this.shader.setUniform(this.uHeightFogDensity, this.fogRenderParams.getFarFogDensity());
      this.shader.setUniform(this.uHeightFogEnabled, heightFogEnabled);
      this.shader.setUniform(this.uHeightFogFalloffType, this.fogRenderParams.getHeightFogFalloff().value);
      this.shader.setUniform(this.uHeightFogBaseHeight, this.fogRenderParams.getHeightFogBaseHeight());
      this.shader.setUniform(this.uHeightBasedOnCamera, heightFogDirection.basedOnCamera);
      this.shader.setUniform(this.uHeightFogAppliesUp, heightFogDirection.fogAppliesUp);
      this.shader.setUniform(this.uHeightFogAppliesDown, heightFogDirection.fogAppliesDown);
      this.shader.setUniform(this.uUseSphericalFog, useSphericalFog);
      this.shader.setUniform(this.uHeightFogMixingMode, heightFogMixingMode.value);
      this.shader.setUniform(this.uCameraBlockYPos, (float)renderParams.exactCameraPosition.y);
   }

   public void prepUniformObjects(DhApiMat4f modelViewProjectionMatrix, DhApiFogRenderParam fogRenderParams) {
      this.inverseMvmProjMatrix = new DhMat4f(modelViewProjectionMatrix);
      this.inverseMvmProjMatrix.invert();
      this.fogRenderParams = fogRenderParams;
   }

   @Override
   protected void onRender() {
      GLMC.glBindFramebuffer(36160, this.frameBuffer);
      GLMC.disableScissorTest();
      GLMC.disableDepthTest();
      GLMC.disableBlend();
      GLMC.glActiveTexture(33984);
      GLMC.glBindTexture(GlDhMetaRenderer_neoforge.INSTANCE.getActiveDepthTextureId());
      GL33.glUniform1i(this.uDepthMap, 0);
      if (MC_RENDER.runningLegacyOpenGL()) {
         float[] clearColorValues = new float[4];
         GL33.glGetFloatv(3106, clearColorValues);
         GL33.glClearColor(clearColorValues[0], clearColorValues[1], clearColorValues[2], 0.0F);
         GL33.glClear(16640);
      }

      GlScreenQuad.INSTANCE.render();
   }
}
