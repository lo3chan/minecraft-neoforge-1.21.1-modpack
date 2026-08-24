package traben.entity_model_features.mixin.mixins;

import net.minecraft.client.ResourceLoadStateTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;

@Mixin({ResourceLoadStateTracker.class})
public abstract class MixinResourceReloadEnd {
   @Inject(
      method = {"finishReload"},
      at = {@At("HEAD")}
   )
   private void emf$reloadFinish(CallbackInfo ci) {
      if (!EMF.testForForgeLoadingError()) {
         EMFManager.getInstance().modifyEBEIfRequired();
         EMFManager.getInstance().reloadEnd();
         EMF.isLoadingPhase = false;
      }
   }
}
