package at.petrak.hexcasting.client.render.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class HexRenderTypes extends RenderType {
   private HexRenderTypes(String string, VertexFormat vertexFormat, Mode mode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
      super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
      throw new UnsupportedOperationException("Should not be instantiated");
   }

   public static RenderType getGrayscaleLayer(ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }
}
