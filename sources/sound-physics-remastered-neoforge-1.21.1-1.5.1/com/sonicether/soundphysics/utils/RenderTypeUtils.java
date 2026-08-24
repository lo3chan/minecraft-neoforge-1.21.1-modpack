package com.sonicether.soundphysics.utils;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.OptionalDouble;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.LineStateShard;
import net.minecraft.client.renderer.RenderType.CompositeRenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;

public class RenderTypeUtils {
   public static final CompositeRenderType DEBUG_LINE_STRIP_SEETHROUGH = RenderType.create(
      "debug_line_strip_seethrough",
      DefaultVertexFormat.POSITION_COLOR,
      Mode.DEBUG_LINE_STRIP,
      1536,
      false,
      false,
      CompositeState.builder()
         .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
         .setLineState(new LineStateShard(OptionalDouble.of(1.0)))
         .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
         .setCullState(RenderStateShard.NO_CULL)
         .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
         .createCompositeState(false)
   );
   public static final RenderType DEBUG_LINE_STRIP = RenderType.debugLineStrip(1.0);
}
