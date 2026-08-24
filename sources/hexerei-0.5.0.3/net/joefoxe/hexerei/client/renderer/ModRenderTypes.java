package net.joefoxe.hexerei.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.BiFunction;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.CullStateShard;
import net.minecraft.client.renderer.RenderStateShard.LightmapStateShard;
import net.minecraft.client.renderer.RenderStateShard.LineStateShard;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard;
import net.minecraft.client.renderer.RenderStateShard.WriteMaskStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;

public class ModRenderTypes extends RenderType {
   public static List<RenderType> renderTypes = new ArrayList<>();
   public static List<RenderType> normalRenderTypes = new ArrayList<>();
   public static List<RenderType> particleRenderTypes = new ArrayList<>();
   private static final LineStateShard THICK_LINE = new LineStateShard(OptionalDouble.of(10.0));
   public static final RenderType BLOCK_HIGHLIGHT_FACE = create(
      "block_highlight",
      DefaultVertexFormat.POSITION_COLOR,
      Mode.QUADS,
      256,
      false,
      false,
      CompositeState.builder()
         .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
         .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setTextureState(NO_TEXTURE)
         .setDepthTestState(NO_DEPTH_TEST)
         .setCullState(NO_CULL)
         .setLightmapState(NO_LIGHTMAP)
         .setWriteMaskState(COLOR_WRITE)
         .createCompositeState(false)
   );
   public static final RenderType HUE_SLIDER = registerRenderType(
      create(
         "hue_slider",
         DefaultVertexFormat.NEW_ENTITY,
         Mode.QUADS,
         1536,
         true,
         true,
         CompositeState.builder()
            .setShaderState(new ShaderStateShard(() -> ClientEvents.hueSliderShader))
            .setTextureState(new TextureStateShard(ResourceLocation.parse("hexerei:textures/book/blank.png"), false, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(true)
      )
   );
   public static final RenderType SLIDER = registerRenderType(
      create(
         "slider",
         DefaultVertexFormat.NEW_ENTITY,
         Mode.QUADS,
         1536,
         true,
         true,
         CompositeState.builder()
            .setShaderState(new ShaderStateShard(() -> ClientEvents.sliderShader))
            .setTextureState(new TextureStateShard(ResourceLocation.parse("hexerei:textures/book/blank.png"), false, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(true)
      )
   );
   public static final BiFunction<ResourceLocation, Boolean, RenderType> BOOK_TRANSLUCENT = Util.memoize(
      (location, bool) -> {
         CompositeState rendertype$compositestate = CompositeState.builder()
            .setShaderState(new ShaderStateShard(() -> ClientEvents.bookTranslucentShader))
            .setTextureState(new TextureStateShard(location, false, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(bool);
         return create("book_translucent", DefaultVertexFormat.NEW_ENTITY, Mode.QUADS, 1536, true, true, rendertype$compositestate);
      }
   );
   public static final RenderType MOON_PHASE = RenderType.create(
      "moon_phase",
      DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
      Mode.QUADS,
      786432,
      false,
      true,
      CompositeState.builder()
         .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
         .setTextureState(new TextureStateShard(HexereiUtil.getResource("textures/gui/moon_phases.png"), false, false))
         .setLightmapState(RenderStateShard.LIGHTMAP)
         .setTransparencyState(new TransparencyStateShard("translucent_transparency", () -> {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
         }, () -> {
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
         }))
         .createCompositeState(false)
   );
   public static final RenderType BLOCK_HILIGHT_LINE = create(
      "block_hilight_line",
      DefaultVertexFormat.POSITION_COLOR,
      Mode.LINES,
      256,
      false,
      false,
      CompositeState.builder()
         .setLineState(THICK_LINE)
         .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
         .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setTextureState(NO_TEXTURE)
         .setDepthTestState(NO_DEPTH_TEST)
         .setCullState(NO_CULL)
         .setLightmapState(NO_LIGHTMAP)
         .setWriteMaskState(COLOR_WRITE)
         .createCompositeState(false)
   );
   private static final RenderType FLUID = RenderType.create(
      "hexerei:fluid",
      DefaultVertexFormat.NEW_ENTITY,
      Mode.QUADS,
      256,
      false,
      true,
      CompositeState.builder()
         .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER)
         .setTextureState(BLOCK_SHEET_MIPPED)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setLightmapState(LIGHTMAP)
         .setOverlayState(OVERLAY)
         .createCompositeState(true)
   );
   private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT = Util.memoize(
      (p_286156_, p_286157_) -> {
         CompositeState rendertype$compositestate = CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
            .setTextureState(new TextureStateShard(p_286156_, false, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(p_286157_);
         return create("entity_translucent", DefaultVertexFormat.NEW_ENTITY, Mode.QUADS, 256, true, true, rendertype$compositestate);
      }
   );

   public ModRenderTypes(
      String name, VertexFormat format, Mode drawMode, int bufferSize, boolean useDelegate, boolean needsSorting, Runnable pre, Runnable post
   ) {
      super(name, format, drawMode, bufferSize, useDelegate, needsSorting, pre, post);
   }

   public static RenderType hueSlider(ResourceLocation location) {
      return HUE_SLIDER;
   }

   public static RenderType slider(ResourceLocation location) {
      return SLIDER;
   }

   public static RenderType bookTranslucent(ResourceLocation location) {
      return BOOK_TRANSLUCENT.apply(location, true);
   }

   public static RenderType getFluid() {
      return FLUID;
   }

   public static RenderType createGenericRenderType(
      String name, VertexFormat format, Mode mode, ShaderStateShard shader, TransparencyStateShard transparency, ResourceLocation texture
   ) {
      RenderType type = RenderType.create(
         "hexerei:" + name,
         format,
         mode,
         256,
         false,
         false,
         CompositeState.builder()
            .setShaderState(shader)
            .setWriteMaskState(new WriteMaskStateShard(true, true))
            .setLightmapState(new LightmapStateShard(false))
            .setTransparencyState(transparency)
            .setTextureState(new TextureStateShard(texture, false, false))
            .setCullState(new CullStateShard(true))
            .createCompositeState(true)
      );
      return type;
   }

   public static RenderType registerRenderType(RenderType type, boolean isParticle) {
      renderTypes.add(type);
      if (isParticle) {
         particleRenderTypes.add(type);
      } else {
         normalRenderTypes.add(type);
      }

      return type;
   }

   public static RenderType registerRenderType(RenderType type) {
      return registerRenderType(type, false);
   }
}
