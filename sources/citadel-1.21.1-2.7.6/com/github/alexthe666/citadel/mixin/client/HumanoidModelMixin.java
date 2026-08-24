package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.client.event.EventPosePlayerHand;
import java.util.function.Function;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HumanoidModel.class})
public abstract class HumanoidModelMixin extends Model {
   public HumanoidModelMixin(Function<ResourceLocation, RenderType> p_103110_) {
      super(p_103110_);
   }

   @Inject(
      at = {@At("HEAD")},
      remap = true,
      method = {"poseRightArm"},
      cancellable = true
   )
   private void citadel_poseRightArm(LivingEntity entity, CallbackInfo ci) {
      EventPosePlayerHand event = new EventPosePlayerHand(entity, (HumanoidModel)this, false);
      NeoForge.EVENT_BUS.post(event);
      if (event.getResult() == TriState.TRUE) {
         ci.cancel();
      }
   }

   @Inject(
      at = {@At("HEAD")},
      remap = true,
      method = {"poseLeftArm"},
      cancellable = true
   )
   private void citadel_poseLeftArm(LivingEntity entity, CallbackInfo ci) {
      EventPosePlayerHand event = new EventPosePlayerHand(entity, (HumanoidModel)this, true);
      NeoForge.EVENT_BUS.post(event);
      if (event.getResult() == TriState.TRUE) {
         ci.cancel();
      }
   }
}
