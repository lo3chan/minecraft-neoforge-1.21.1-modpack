package net.joefoxe.hexerei.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.OverlayStateShard;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;

public class CustomRenderType extends RenderType {
   public static final RenderType TRANSPARENCY_FIX2 = create(
      "transparent_fix",
      DefaultVertexFormat.BLOCK,
      Mode.QUADS,
      256,
      true,
      true,
      CompositeState.builder()
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
         .setWriteMaskState(COLOR_WRITE)
         .createCompositeState(true)
   );
   public static final Function<ResourceLocation, RenderType> TRANSPARENCY_FIX = Util.memoize(
      p_173200_ -> create(
         "transparent_fix",
         DefaultVertexFormat.POSITION_TEX_COLOR,
         Mode.QUADS,
         256,
         true,
         true,
         CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
            .setTextureState(new TextureStateShard(p_173200_, true, true))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .createCompositeState(true)
      )
   );
   public static final RenderType ADD = create("cutout", DefaultVertexFormat.BLOCK, Mode.QUADS, 2097152, true, true, addState(RENDERTYPE_CUTOUT_SHADER));

   public CustomRenderType(
      String name, VertexFormat vertexFormat, Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear
   ) {
      super(name, vertexFormat, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
   }

   private static CompositeState addState(ShaderStateShard shard) {
      return CompositeState.builder()
         .setLightmapState(LIGHTMAP)
         .setShaderState(shard)
         .setTextureState(BLOCK_SHEET_MIPPED)
         .setTransparencyState(NO_TRANSPARENCY)
         .setOverlayState(OverlayStateShard.OVERLAY)
         .createCompositeState(true);
   }
}
