package traben.entity_model_features.mixin.mixins.rendering;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin({EntityRenderDispatcher.class})
public abstract class MixinEntityRenderDispatcher {
   private static final String SHADOW_RENDER_ETF = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V";
   private static final String RENDER_ETF = "render";

   @Shadow
   public abstract double distanceToSqr(double var1, double var3, double var5);

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private <E extends Entity> void emf$grabContext(CallbackInfo ci, @Local(argsOnly = true) E entity) {
      EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntityRenderState)ETFEntityRenderState.forEntity((ETFEntity)entity));
   }

   @Inject(
      method = {"render"},
      at = {@At("RETURN")}
   )
   private <E extends Entity> void emf$endOfRender(CallbackInfo ci, @Local(argsOnly = true) E entity) {
      if (EMFAnimationEntityContext.doAnnounceModels()) {
         EMFAnimationEntityContext.anounceModels((EMFEntityRenderState)ETFEntityRenderState.forEntity((ETFEntity)entity));
      }
   }

   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V",
         shift = Shift.BEFORE
      )}
   )
   private void emf$modifyShadowTranslate(CallbackInfo ci, @Local(argsOnly = true) PoseStack matrices) {
      if (EMFAnimationEntityContext.getShadowX() != 0.0F || EMFAnimationEntityContext.getShadowZ() != 0.0F) {
         matrices.translate(EMFAnimationEntityContext.getShadowX(), 0.0F, EMFAnimationEntityContext.getShadowZ());
      }
   }

   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V",
         shift = Shift.AFTER
      )}
   )
   private void emf$undoModifyShadowTranslate(CallbackInfo ci, @Local(argsOnly = true) PoseStack matrices) {
      if (EMFAnimationEntityContext.getShadowX() != 0.0F || EMFAnimationEntityContext.getShadowZ() != 0.0F) {
         matrices.translate(-EMFAnimationEntityContext.getShadowX(), 0.0F, -EMFAnimationEntityContext.getShadowZ());
      }
   }

   @ModifyArg(
      method = {"render"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"
      ),
      index = 3
   )
   private float emf$modifyShadowOpacity(float opacity) {
      if (!Float.isNaN(EMFAnimationEntityContext.getShadowOpacity())) {
         double g = this.distanceToSqr(EMFAnimationEntityContext.getEntityX(), EMFAnimationEntityContext.getEntityY(), EMFAnimationEntityContext.getEntityZ());
         return (float)((1.0 - g / 256.0) * EMFAnimationEntityContext.getShadowOpacity());
      } else {
         return opacity;
      }
   }

   @ModifyArg(
      method = {"render"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"
      ),
      index = 6
   )
   private float emf$modifyShadowSize(float size) {
      return !Float.isNaN(EMFAnimationEntityContext.getShadowSize()) ? Math.min(size * EMFAnimationEntityContext.getShadowSize(), 32.0F) : size;
   }
}
