package com.github.alexthe666.alexsmobs.mixin.client;

import com.github.alexthe666.alexsmobs.citadel.client.event.EventPosePlayerHand;
import java.util.function.Function;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HumanoidModel.class})
public abstract class HumanoidModelMixin extends Model {
   public HumanoidModelMixin(Function<ResourceLocation, RenderType> renderTypeFunction) {
      super(renderTypeFunction);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"Lnet/minecraft/client/model/HumanoidModel;poseRightArm(Lnet/minecraft/world/entity/LivingEntity;)V"},
      cancellable = true
   )
   private void alexsmobs_poseRightArm(LivingEntity entity, CallbackInfo ci) {
      EventPosePlayerHand event = new EventPosePlayerHand(entity, (HumanoidModel)this, false);
      event.post();
      if (event.isHandled()) {
         ci.cancel();
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"Lnet/minecraft/client/model/HumanoidModel;poseLeftArm(Lnet/minecraft/world/entity/LivingEntity;)V"},
      cancellable = true
   )
   private void alexsmobs_poseLeftArm(LivingEntity entity, CallbackInfo ci) {
      EventPosePlayerHand event = new EventPosePlayerHand(entity, (HumanoidModel)this, true);
      event.post();
      if (event.isHandled()) {
         ci.cancel();
      }
   }
}
