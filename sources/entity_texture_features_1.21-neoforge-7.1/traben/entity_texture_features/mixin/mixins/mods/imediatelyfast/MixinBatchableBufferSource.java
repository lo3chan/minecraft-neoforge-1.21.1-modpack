package traben.entity_texture_features.mixin.mixins.mods.imediatelyfast;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.raphimc.immediatelyfast.feature.core.BatchableBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.compat.SodiumGetBufferInjector;
import traben.entity_texture_features.features.ETFRenderContext;

@Pseudo
@Mixin(
   value = {BatchableBufferSource.class},
   priority = 800
)
public class MixinBatchableBufferSource {
   @ModifyVariable(
      method = {"getBuffer"},
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true,
      require = 0
   )
   private RenderType etf$modifyRenderLayer2(RenderType value) {
      return ETFRenderContext.modifyRenderLayerIfRequired(value);
   }

   @Inject(
      method = {"getBuffer"},
      at = {@At("RETURN")},
      require = 0
   )
   private void etf$injectIntoGetBufferReturn(RenderType renderLayer, CallbackInfoReturnable<VertexConsumer> cir) {
      if (ETFRenderContext.isCurrentlyRenderingEntity()) {
         VertexConsumer returned = (VertexConsumer)cir.getReturnValue();
         ETFRenderContext.insertETFDataIntoVertexConsumer((MultiBufferSource)this, renderLayer, returned);
         SodiumGetBufferInjector.inject((MultiBufferSource)this, renderLayer, returned);
      }
   }
}
