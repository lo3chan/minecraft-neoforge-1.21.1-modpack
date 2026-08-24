package org.dimdev.limlib.api.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;

public class RenderLayerFactory {
   public static RenderType create(
      String name, VertexFormat vertexFormat, Mode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, CompositeState phases
   ) {
      return RenderType.create(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, phases);
   }
}
