package traben.entity_texture_features.compat;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.apache.logging.log4j.util.TriConsumer;

public abstract class SodiumGetBufferInjector {
   private static final TriConsumer<MultiBufferSource, RenderType, VertexConsumer> INSTANCE = get();

   public static void inject(MultiBufferSource provider, RenderType renderLayer, VertexConsumer vertexConsumer) {
      if (INSTANCE != null) {
         INSTANCE.accept(provider, renderLayer, vertexConsumer);
      }
   }

   private static TriConsumer<MultiBufferSource, RenderType, VertexConsumer> get() {
      return null;
   }
}
