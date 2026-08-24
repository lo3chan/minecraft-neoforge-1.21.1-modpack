package net.mehvahdjukaar.moonlight.core.client;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.function.Function;
import net.mehvahdjukaar.moonlight.api.client.CoreShaderContainer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class MLRenderTypes extends RenderType {
   public static final CoreShaderContainer TEXT_COLOR_SHADER = new CoreShaderContainer(GameRenderer::getPositionTexColorShader);
   public static final CoreShaderContainer PARTICLE_TRANSLUCENT_SHADER = new CoreShaderContainer(GameRenderer::getParticleShader);
   public static final Function<ResourceLocation, RenderType> COLOR_TEXT = Util.memoize(
      p -> {
         CompositeState compositeState = CompositeState.builder()
            .setShaderState(new ShaderStateShard(TEXT_COLOR_SHADER))
            .setTextureState(new TextureStateShard(p, false, true))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setLightmapState(LIGHTMAP)
            .createCompositeState(false);
         return create("moonlight_text_color_mipped", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, Mode.QUADS, 256, false, true, compositeState);
      }
   );
   public static final Function<ResourceLocation, RenderType> TEXT_MIP = Util.memoize(
      p -> {
         CompositeState compositeState = CompositeState.builder()
            .setShaderState(RENDERTYPE_TEXT_SHADER)
            .setTextureState(new TextureStateShard(p, false, true))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setLightmapState(LIGHTMAP)
            .createCompositeState(false);
         return create("moonlight_text_mipped", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, Mode.QUADS, 256, false, true, compositeState);
      }
   );
   public static final Function<ResourceLocation, RenderType> ENTITY_SOLID_MIP = Util.memoize(
      resourceLocation -> {
         CompositeState compositeState = CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER)
            .setTextureState(new TextureStateShard(resourceLocation, false, true))
            .setTransparencyState(NO_TRANSPARENCY)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(true);
         return create("moonlight_entity_solid_mipped", DefaultVertexFormat.NEW_ENTITY, Mode.QUADS, 256, true, false, compositeState);
      }
   );
   public static final Function<ResourceLocation, RenderType> ENTITY_CUTOUT_MIP = Util.memoize(
      resourceLocation -> {
         CompositeState compositeState = CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_CUTOUT_SHADER)
            .setTextureState(new TextureStateShard(resourceLocation, false, true))
            .setTransparencyState(NO_TRANSPARENCY)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(true);
         return RenderType.create("moonlight_entity_cutout_mipped", DefaultVertexFormat.NEW_ENTITY, Mode.QUADS, 256, true, false, compositeState);
      }
   );
   public static final ParticleRenderType PARTICLE_ADDITIVE_TRANSLUCENCY_RENDER_TYPE = new ParticleRenderType() {
      public BufferBuilder begin(Tesselator builder, TextureManager textureManager) {
         Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
         RenderSystem.activeTexture(33986);
         RenderSystem.activeTexture(33984);
         RenderSystem.setShader(MLRenderTypes.PARTICLE_TRANSLUCENT_SHADER);
         RenderSystem.depthMask(false);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
         return builder.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      @Override
      public String toString() {
         return "PARTICLE_SHEET_ADDITIVE_TRANSLUCENT";
      }
   };

   public MLRenderTypes(
      String pName,
      VertexFormat pFormat,
      Mode pMode,
      int pBufferSize,
      boolean pAffectsCrumbling,
      boolean pSortOnUpload,
      Runnable pSetupState,
      Runnable pClearState
   ) {
      super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
   }
}
