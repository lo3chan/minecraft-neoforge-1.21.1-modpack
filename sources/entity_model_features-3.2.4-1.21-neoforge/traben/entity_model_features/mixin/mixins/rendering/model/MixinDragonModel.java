package traben.entity_model_features.mixin.mixins.rendering.model;

import net.minecraft.client.renderer.entity.EnderDragonRenderer.DragonModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMFManager;

@Mixin({DragonModel.class})
public abstract class MixinDragonModel {
   @Inject(
      method = {"renderToBuffer"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V",
         shift = Shift.BEFORE
      )}
   )
   private void emf$allowMultiPartRender(CallbackInfo ci) {
      EMFManager.getInstance().entityRenderCount++;
   }

   @Inject(
      method = {"renderToBuffer"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/EnderDragonRenderer$DragonModel;renderSide(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFLnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;I)V",
         shift = Shift.BEFORE,
         ordinal = 0
      )}
   )
   private void emf$allowMultiPartRender2(CallbackInfo ci) {
      EMFManager.getInstance().entityRenderCount++;
   }
}
