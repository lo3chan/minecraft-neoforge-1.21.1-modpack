package traben.entity_model_features.mixin.mixins.rendering;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin({BlockEntityRenderDispatcher.class})
public class MixinBlockEntityRenderDispatcher {
   @Inject(
      method = {"tryRender"},
      at = {@At("HEAD")}
   )
   private static void emf$grabEntity2(BlockEntity blockEntity, Runnable runnable, CallbackInfo ci) {
      EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntityRenderState)ETFEntityRenderState.forEntity((ETFEntity)blockEntity));
   }
}
