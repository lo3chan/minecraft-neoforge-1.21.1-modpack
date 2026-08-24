package traben.entity_texture_features.mixin.mixins;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer.Double;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFVertexConsumer;

@Mixin({Double.class})
public class MixinVertexMultiConsumer$Double implements ETFVertexConsumer {
   @Shadow
   @Final
   private VertexConsumer first;
   @Shadow
   @Final
   private VertexConsumer second;

   @Override
   public ETFTexture etf$getETFTexture() {
      if (this.second instanceof ETFVertexConsumer etfSecond) {
         return etfSecond.etf$getETFTexture();
      } else {
         return this.first instanceof ETFVertexConsumer etfFirst ? etfFirst.etf$getETFTexture() : null;
      }
   }

   @Override
   public MultiBufferSource etf$getProvider() {
      if (this.second instanceof ETFVertexConsumer etfSecond) {
         return etfSecond.etf$getProvider();
      } else {
         return this.first instanceof ETFVertexConsumer etfFirst ? etfFirst.etf$getProvider() : null;
      }
   }

   @Override
   public RenderType etf$getRenderLayer() {
      if (this.second instanceof ETFVertexConsumer etfSecond) {
         return etfSecond.etf$getRenderLayer();
      } else {
         return this.first instanceof ETFVertexConsumer etfFirst ? etfFirst.etf$getRenderLayer() : null;
      }
   }

   @Override
   public void etf$initETFVertexConsumer(MultiBufferSource provider, RenderType renderLayer) {
   }
}
