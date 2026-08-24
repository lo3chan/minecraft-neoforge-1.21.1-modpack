package traben.entity_texture_features.utils;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;

public interface ETFVertexConsumer {
   @Nullable
   ETFTexture etf$getETFTexture();

   @Nullable
   MultiBufferSource etf$getProvider();

   @Nullable
   RenderType etf$getRenderLayer();

   void etf$initETFVertexConsumer(MultiBufferSource var1, RenderType var2);
}
