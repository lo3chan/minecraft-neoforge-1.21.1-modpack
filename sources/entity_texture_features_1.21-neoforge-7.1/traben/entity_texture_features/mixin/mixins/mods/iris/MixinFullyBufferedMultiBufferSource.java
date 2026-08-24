package traben.entity_texture_features.mixin.mixins.mods.iris;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.irisshaders.batchedentityrendering.impl.FullyBufferedMultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.features.ETFRenderContext;

@Pseudo
@Mixin({FullyBufferedMultiBufferSource.class})
public class MixinFullyBufferedMultiBufferSource {
   @ModifyVariable(
      method = {"getBuffer"},
      at = @At("HEAD"),
      index = 1,
      argsOnly = true
   )
   private RenderType etf$modifyRenderLayer(RenderType value) {
      RenderType newLayer = ETFRenderContext.modifyRenderLayerIfRequired(value);
      return newLayer == null ? value : newLayer;
   }

   @Inject(
      method = {"getBuffer"},
      at = {@At("RETURN")}
   )
   private void etf$injectIntoGetBufferReturn(RenderType renderLayer, CallbackInfoReturnable<VertexConsumer> cir) {
      ETFRenderContext.insertETFDataIntoVertexConsumer((MultiBufferSource)this, renderLayer, (VertexConsumer)cir.getReturnValue());
   }
}
