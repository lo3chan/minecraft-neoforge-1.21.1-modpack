package traben.entity_model_features.mixin.mixins;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.parts.EMFModelPartRoot;

@Mixin({Model.class})
public class MixinModel implements IEMFModel {
   @Unique
   private EMFModelPartRoot emf$thisEMFModelRoot = null;

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void emf$discoverEMFModel(CallbackInfo ci) {
      if (!EMF.testForForgeLoadingError()) {
         this.emf$thisEMFModelRoot = EMFManager.lastCreatedRootModelPart;
         EMFManager.lastCreatedRootModelPart = null;
      }
   }

   @Override
   public boolean emf$isEMFModel() {
      return this.emf$thisEMFModelRoot != null;
   }

   @Override
   public EMFModelPartRoot emf$getEMFRootModel() {
      return this.emf$thisEMFModelRoot;
   }

   @Inject(
      method = {"renderType"},
      at = {@At("HEAD")}
   )
   private void emf$discoverEMFModel(CallbackInfoReturnable<RenderType> cir) {
      EMFAnimationEntityContext.setLayerFactory(((Model)this).renderType);
   }
}
