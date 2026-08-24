package traben.entity_model_features.mixin.mixins.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin({BlockEntityWithoutLevelRenderer.class})
public class MixinBlockEntityWithoutLevelRenderer {
   @Inject(
      method = {"renderByItem"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/blockentity/SkullBlockRenderer;renderSkull(Lnet/minecraft/core/Direction;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/SkullModelBase;Lnet/minecraft/client/renderer/RenderType;)V",
         shift = Shift.BEFORE
      )}
   )
   private void emf$setRenderFactory(
      ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, CallbackInfo ci
   ) {
      EMFAnimationEntityContext.setLayerFactory(RenderType::entityCutoutNoCullZOffset);
      EMFManager.getInstance().entityRenderCount++;
      this.setPlayerEntity();
   }

   @Inject(
      method = {"renderByItem"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/model/TridentModel;renderType(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"
      )}
   )
   private void emf$setTrident(CallbackInfo ci) {
      EMFManager.getInstance().entityRenderCount++;
      this.setPlayerEntity();
   }

   @Inject(
      method = {"renderByItem"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/model/ShieldModel;renderType(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;",
         shift = Shift.BEFORE
      )}
   )
   private void emf$setTrident(
      ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, CallbackInfo ci
   ) {
      EMFManager.getInstance().entityRenderCount++;
      this.setPlayerEntity();
   }

   @Inject(
      method = {"renderByItem"},
      at = {@At("RETURN")}
   )
   private void emf$reset(CallbackInfo ci) {
      EMFAnimationEntityContext.reset();
   }

   @Unique
   private void setPlayerEntity() {
      if (Minecraft.getInstance().player != null) {
         ETFEntityRenderState state = ETFEntityRenderState.forEntity((ETFEntity)Minecraft.getInstance().player);
         EMFAnimationEntityContext.setCurrentEntityNoIteration((EMFEntityRenderState)state);
      }
   }
}
