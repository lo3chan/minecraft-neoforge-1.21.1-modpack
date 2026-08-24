package traben.entity_model_features.mixin.mixins.rendering;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFSubmitData;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.utils.EMFEntity;

@Mixin({LivingEntityRenderer.class})
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
   @Shadow
   protected M model;
   private static final String RENDER = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V";

   @Shadow
   public abstract M getModel();

   protected MixinLivingEntityRenderer(Context ctx) {
      super(ctx);
   }

   @ModifyExpressionValue(
      method = {"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;isSpectator()Z"
      )}
   )
   private boolean armOverrides(boolean original, @Local PoseStack pose) {
      if (!original) {
         IEMFModel model = (IEMFModel)this.getModel();
         if (model.emf$isEMFModel()) {
            model.emf$getEMFRootModel().checkArmOverrides(pose);
         }
      }

      return original;
   }

   @Inject(
      method = {"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V",
         shift = Shift.BEFORE
      )}
   )
   private void emf$Animate(T livingEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {
      if (((IEMFModel)this.model).emf$isEMFModel()) {
         EMFModelPartRoot root = ((IEMFModel)this.model).emf$getEMFRootModel();
         if (root != null && ((EMFConfig)EMF.config().getConfig()).getVanillaHologramModeFor((EMFEntity)livingEntity) != EMFConfig.VanillaModelRenderMode.OFF) {
            root.tryRenderVanillaRootNormally(
               matrixStack, vertexConsumerProvider.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(livingEntity))), i, OverlayTexture.NO_OVERLAY
            );
         }
      }
   }

   @ModifyExpressionValue(
      method = {"getRenderType"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getTextureLocation(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/resources/ResourceLocation;"
      )}
   )
   private ResourceLocation emf$getTextureRedirect(ResourceLocation original) {
      if (((IEMFModel)this.model).emf$isEMFModel()) {
         EMFModelPartRoot root = ((IEMFModel)this.model).emf$getEMFRootModel();
         if (root != null) {
            ResourceLocation texture = root.getTopLevelJemTexture();
            if (texture != null) {
               return texture;
            }
         }
      }

      return original;
   }

   @Inject(
      method = {"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Ljava/util/List;iterator()Ljava/util/Iterator;"
      )}
   )
   private void emf$grabEntity(CallbackInfo ci, @Share("iteration") LocalRef<EMFAnimationEntityContext.IterationContext> emf$heldIteration) {
      emf$heldIteration.set(EMFAnimationEntityContext.getIterationContext());
      EMFSubmitData.AWAITING_isLayerModelPhase = true;
      EMFAnimationEntityContext.setLayerPhase();
      EMFSubmitData.AWAITING_isMainModelPhase = false;
      if (this.getModel() instanceof IEMFModel emf && emf.emf$isEMFModel()) {
         emf.emf$getEMFRootModel().isMainModel = true;
      }
   }

   @Inject(
      method = {"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Ljava/util/Iterator;next()Ljava/lang/Object;"
      )}
   )
   private void emf$eachFeatureLoop(CallbackInfo ci, @Share("iteration") LocalRef<EMFAnimationEntityContext.IterationContext> emf$heldIteration) {
      if (emf$heldIteration.get() != null
         && EMFManager.getInstance().entityRenderCount != ((EMFAnimationEntityContext.IterationContext)emf$heldIteration.get()).entityRenderCount()) {
         EMFAnimationEntityContext.setIterationContext((EMFAnimationEntityContext.IterationContext)emf$heldIteration.get());
      }

      EMFManager.getInstance().entityRenderCount++;
      EMFSubmitData.AWAITING_isLayerModelPhase = true;
      EMFAnimationEntityContext.setLayerPhase();
   }

   @Inject(
      method = {"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = {@At("HEAD")}
   )
   private void emf$preRender(CallbackInfo ci) {
      EMFSubmitData.AWAITING_isLayerModelPhase = false;
      EMFSubmitData.AWAITING_isMainModelPhase = true;
   }

   @Inject(
      method = {"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = {@At("TAIL")}
   )
   private void emf$postRender(CallbackInfo ci) {
      EMFSubmitData.AWAITING_isLayerModelPhase = false;
      EMFSubmitData.AWAITING_isMainModelPhase = false;
      EMFAnimationEntityContext.unsetLayerPhase();
   }
}
