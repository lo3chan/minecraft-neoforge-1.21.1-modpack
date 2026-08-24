package net.irisshaders.iris.pipeline.programs;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.io.IOException;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

public class FallbackShader extends ShaderInstance {
   private final IrisRenderingPipeline parent;
   private final BlendModeOverride blendModeOverride;
   private final GlFramebuffer writingToBeforeTranslucent;
   private final GlFramebuffer writingToAfterTranslucent;
   @Nullable
   private final Uniform FOG_DENSITY;
   @Nullable
   private final Uniform FOG_IS_EXP2;
   private final int gtexture;
   private final int overlay;
   private final int lightmap;

   public FallbackShader(
      ResourceProvider resourceFactory,
      String string,
      VertexFormat vertexFormat,
      GlFramebuffer writingToBeforeTranslucent,
      GlFramebuffer writingToAfterTranslucent,
      BlendModeOverride blendModeOverride,
      float alphaValue,
      IrisRenderingPipeline parent
   ) throws IOException {
      super(resourceFactory, string, vertexFormat);
      this.parent = parent;
      this.blendModeOverride = blendModeOverride;
      this.writingToBeforeTranslucent = writingToBeforeTranslucent;
      this.writingToAfterTranslucent = writingToAfterTranslucent;
      this.FOG_DENSITY = this.getUniform("FogDensity");
      this.FOG_IS_EXP2 = this.getUniform("FogIsExp2");
      this.gtexture = GlStateManager._glGetUniformLocation(this.getId(), "gtexture");
      this.overlay = GlStateManager._glGetUniformLocation(this.getId(), "overlay");
      this.lightmap = GlStateManager._glGetUniformLocation(this.getId(), "lightmap");
      Uniform ALPHA_TEST_VALUE = this.getUniform("AlphaTestValue");
      if (ALPHA_TEST_VALUE != null) {
         ALPHA_TEST_VALUE.set(alphaValue);
      }
   }

   public void clear() {
      super.clear();
      if (this.blendModeOverride != null) {
         BlendModeOverride.restore();
      }

      Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
   }

   public void apply() {
      if (this.FOG_DENSITY != null && this.FOG_IS_EXP2 != null) {
         float fogDensity = CapturedRenderingState.INSTANCE.getFogDensity();
         if (fogDensity >= 0.0) {
            this.FOG_DENSITY.set(fogDensity);
            this.FOG_IS_EXP2.set(1);
         } else {
            this.FOG_DENSITY.set(0.0F);
            this.FOG_IS_EXP2.set(0);
         }
      }

      IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 0, RenderSystem.getShaderTexture(0));
      IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 1, RenderSystem.getShaderTexture(1));
      IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 2, RenderSystem.getShaderTexture(2));
      ProgramManager.glUseProgram(this.getId());

      for (Uniform uniform : super.uniforms) {
         this.uploadIfNotNull(uniform);
      }

      GlStateManager._glUniform1i(this.gtexture, 0);
      GlStateManager._glUniform1i(this.overlay, 1);
      GlStateManager._glUniform1i(this.lightmap, 2);
      if (this.blendModeOverride != null) {
         this.blendModeOverride.apply();
      }

      if (this.parent.isBeforeTranslucent) {
         this.writingToBeforeTranslucent.bind();
      } else {
         this.writingToAfterTranslucent.bind();
      }
   }

   private void uploadIfNotNull(Uniform uniform) {
      if (uniform != null) {
         uniform.upload();
      }
   }
}
