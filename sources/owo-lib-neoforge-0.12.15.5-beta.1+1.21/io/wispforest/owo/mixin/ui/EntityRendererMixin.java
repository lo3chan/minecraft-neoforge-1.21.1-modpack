package io.wispforest.owo.mixin.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.wispforest.owo.util.pond.OwoEntityRenderDispatcherExtension;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityRenderer.class})
public class EntityRendererMixin<T extends Entity> {
   @Shadow
   @Final
   protected EntityRenderDispatcher entityRenderDispatcher;

   @Inject(
      method = {"renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void cancelLabel(T entity, Component text, PoseStack matrices, MultiBufferSource vertexConsumers, int light, float tickDelta, CallbackInfo ci) {
      if (!((OwoEntityRenderDispatcherExtension)this.entityRenderDispatcher).owo$showNametag()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V",
         shift = Shift.AFTER
      )}
   )
   private void adjustLabelRotation(
      T entity, Component text, PoseStack matrices, MultiBufferSource vertexConsumers, int light, float tickDelta, CallbackInfo ci
   ) {
      if (((OwoEntityRenderDispatcherExtension)this.entityRenderDispatcher).owo$counterRotate()) {
         matrices.mulPose(new Quaternionf(this.entityRenderDispatcher.cameraOrientation()).invert());
         matrices.mulPose(Axis.YP.rotationDegrees(180.0F));
      }
   }
}
