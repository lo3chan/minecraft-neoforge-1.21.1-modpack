package dev.tr7zw.waveycapes.mixin;

import dev.tr7zw.waveycapes.support.ModSupport;
import dev.tr7zw.waveycapes.support.SupportManager;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntityRenderer.class})
public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
   @Inject(
      method = {"addLayer(Lnet/minecraft/client/renderer/entity/layers/RenderLayer;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void addLayer(RenderLayer<T, M> renderLayer, CallbackInfoReturnable<Boolean> info) {
      if (renderLayer instanceof CapeLayer) {
         info.cancel();
      } else {
         for (ModSupport support : SupportManager.getSupportedMods()) {
            if (support.blockFeatureRenderer(renderLayer)) {
               info.cancel();
               return;
            }
         }
      }
   }
}
