package traben.entity_model_features.mixin.mixins.rendering.model;

import net.minecraft.client.model.WolfModel;
import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.utils.IEMFWolfCollarHolder;

@Mixin({WolfModel.class})
public class MixinWolfEntityModel<T extends Wolf> implements IEMFWolfCollarHolder<T> {
   @Unique
   WolfModel<T> emf$collarModel = null;

   @Inject(
      method = {"setupAnim(Lnet/minecraft/world/entity/animal/Wolf;FFFFF)V"},
      at = {@At("HEAD")}
   )
   private void smf$setAngles(T wolfEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
      if (this.emf$hasCollarModel()) {
         this.emf$collarModel.setupAnim(wolfEntity, f, g, h, i, j);
      }
   }

   @Inject(
      method = {"prepareMobModel(Lnet/minecraft/world/entity/animal/Wolf;FFF)V"},
      at = {@At("HEAD")}
   )
   private void smf$animateModel(T wolfEntity, float f, float g, float h, CallbackInfo ci) {
      if (this.emf$hasCollarModel()) {
         this.emf$collarModel.prepareMobModel(wolfEntity, f, g, h);
      }
   }

   @Override
   public WolfModel<T> emf$getCollarModel() {
      return this.emf$collarModel;
   }

   @Override
   public void emf$setCollarModel(WolfModel<T> model) {
      this.emf$collarModel = model;
   }
}
