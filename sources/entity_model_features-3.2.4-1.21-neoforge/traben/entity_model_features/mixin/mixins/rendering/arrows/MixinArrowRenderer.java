package traben.entity_model_features.mixin.mixins.rendering.arrows;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.utils.IEMFCustomModelHolder;

@Mixin({TippableArrowRenderer.class})
public abstract class MixinArrowRenderer implements IEMFCustomModelHolder {
   @Unique
   private EMFModelPartRoot emf$model = null;

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void emf$findModel(CallbackInfo ci) {
      ModelLayerLocation layer = new ModelLayerLocation(EMFUtils.res("minecraft", "arrow"), "main");
      this.emf$setModel(EMFUtils.getArrowOrNull(layer));
   }

   @Override
   public EMFModelPartRoot emf$getModel() {
      return this.emf$model;
   }

   @Override
   public void emf$setModel(EMFModelPartRoot model) {
      this.emf$model = model;
   }
}
