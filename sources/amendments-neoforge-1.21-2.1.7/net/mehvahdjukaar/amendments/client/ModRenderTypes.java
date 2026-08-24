package net.mehvahdjukaar.amendments.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.function.Function;
import net.mehvahdjukaar.amendments.Amendments;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;

public abstract class ModRenderTypes extends RenderType {
   public static final VertexFormat POSITION_COLOR_TEX = VertexFormat.builder()
      .add("Position", VertexFormatElement.POSITION)
      .add("Color", VertexFormatElement.COLOR)
      .add("UV0", VertexFormatElement.UV0)
      .build();
   public static final Function<ResourceLocation, RenderType> ENTITY_LIT = Util.memoize(
      resourceLocation -> RenderType.create(
         Amendments.res("entity_lit").toString(),
         POSITION_COLOR_TEX,
         Mode.QUADS,
         256,
         true,
         false,
         CompositeState.builder()
            .setShaderState(RENDERTYPE_CRUMBLING_SHADER)
            .setTextureState(new TextureStateShard(resourceLocation, false, false))
            .setTransparencyState(NO_TRANSPARENCY)
            .setOverlayState(OVERLAY)
            .createCompositeState(true)
      )
   );

   public ModRenderTypes(
      String name, VertexFormat format, Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState
   ) {
      super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
   }
}
