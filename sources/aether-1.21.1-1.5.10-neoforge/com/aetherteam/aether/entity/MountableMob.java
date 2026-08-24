package com.aetherteam.aether.entity;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.mixin.mixins.common.accessor.ServerGamePacketListenerImplAccessor;
import com.aetherteam.aether.network.packet.serverbound.StepHeightPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.network.PacketDistributor;

public interface MountableMob {
   ResourceLocation MOUNT_HEIGHT_LOCATION = ResourceLocation.fromNamespaceAndPath("aether", "mounted_step_height_increase");
   ResourceLocation DEFAULT_HEIGHT_LOCATION = ResourceLocation.fromNamespaceAndPath("aether", "default_step_height_increase");
   AttributeModifier STEP_HEIGHT_MODIFIER = new AttributeModifier(MOUNT_HEIGHT_LOCATION, 0.4, Operation.ADD_VALUE);
   AttributeModifier DEFAULT_STEP_HEIGHT_MODIFIER = new AttributeModifier(DEFAULT_HEIGHT_LOCATION, -0.1, Operation.ADD_VALUE);

   default void riderTick(Mob vehicle) {
      if (vehicle.getControllingPassenger() instanceof Player player) {
         this.setPlayerJumped(((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).isJumping());
      }
   }

   default <T extends Mob & MountableMob> void tick(T vehicle) {
      if (vehicle.isAlive()) {
         Entity entity = vehicle.getControllingPassenger();
         if (vehicle.isVehicle() && entity instanceof LivingEntity passenger) {
            if (vehicle.getPlayerJumped() && !vehicle.isMountJumping() && vehicle.canJump()) {
               vehicle.setMountJumping(true);
               vehicle.setPlayerJumped(false);
            }

            if (vehicle.onGround()) {
               vehicle.setPlayerJumped(false);
               vehicle.setMountJumping(false);
            }

            if (passenger instanceof ServerPlayer serverPlayer) {
               ServerGamePacketListenerImplAccessor serverGamePacketListenerImplAccessor = (ServerGamePacketListenerImplAccessor)serverPlayer.connection;
               serverGamePacketListenerImplAccessor.aether$setAboveGroundTickCount(0);
               serverGamePacketListenerImplAccessor.aether$setAboveGroundVehicleTickCount(0);
            }
         }
      }
   }

   default <T extends Mob & MountableMob> void travel(T vehicle, Vec3 motion) {
      Entity entity = vehicle.getControllingPassenger();
      if (vehicle.isVehicle() && entity instanceof LivingEntity passenger) {
         vehicle.setYRot(passenger.getYRot() % 360.0F);
         vehicle.yRotO = vehicle.getYRot();
         vehicle.setXRot(passenger.getXRot() * 0.5F % 360.0F);
         vehicle.setYBodyRot(vehicle.getYRot());
         vehicle.setYHeadRot(vehicle.yBodyRot);
         float f = passenger.xxa * 0.5F;
         float f1 = passenger.zza;
         if (f1 <= 0.0F) {
            f1 *= 0.25F;
         }

         if (vehicle.getPlayerJumped() && !vehicle.isMountJumping() && vehicle.canJump()) {
            double jumpStrength = vehicle.getMountJumpStrength() * this.jumpFactor();
            vehicle.setDeltaMovement(vehicle.getDeltaMovement().x(), jumpStrength, vehicle.getDeltaMovement().z());
            if (vehicle.hasEffect(MobEffects.JUMP)) {
               MobEffectInstance jumpBoost = vehicle.getEffect(MobEffects.JUMP);
               if (jumpBoost != null) {
                  vehicle.push(0.0, 0.1 * (jumpBoost.getAmplifier() + 1), 0.0);
               }
            }

            vehicle.hasImpulse = true;
            vehicle.onJump(vehicle);
         } else if (vehicle.getPlayerJumped() && vehicle.isMountJumping() && vehicle.canJump() && Swim.shouldSwim(vehicle)) {
            vehicle.jumpInFluid(vehicle.level().getFluidState(vehicle.getOnPos()).getFluidType());
            vehicle.hasImpulse = true;
            vehicle.onJump(vehicle);
         }

         AttributeInstance stepHeight = vehicle.getAttribute(Attributes.STEP_HEIGHT);
         if (stepHeight != null) {
            if (stepHeight.hasModifier(vehicle.getDefaultStepHeightModifier().id())) {
               stepHeight.removeModifier(vehicle.getDefaultStepHeightModifier().id());
            }

            if (!stepHeight.hasModifier(vehicle.getMountStepHeightModifier().id())) {
               stepHeight.addTransientModifier(vehicle.getMountStepHeightModifier());
            }

            if (vehicle.level().isClientSide()) {
               PacketDistributor.sendToServer(new StepHeightPacket(vehicle.getId()), new CustomPacketPayload[0]);
            }
         }

         if (vehicle.isControlledByLocalInstance()) {
            vehicle.setSpeed(vehicle.getSteeringSpeed());
            this.travelWithInput(new Vec3(f, motion.y, f1));
         } else if (passenger instanceof Player) {
            vehicle.setDeltaMovement(Vec3.ZERO);
         }

         vehicle.calculateEntityAnimation(false);
      } else {
         AttributeInstance stepHeightx = vehicle.getAttribute(Attributes.STEP_HEIGHT);
         if (stepHeightx != null) {
            if (stepHeightx.hasModifier(vehicle.getMountStepHeightModifier().id())) {
               stepHeightx.removeModifier(vehicle.getMountStepHeightModifier().id());
            }

            if (!stepHeightx.hasModifier(vehicle.getDefaultStepHeightModifier().id())) {
               stepHeightx.addTransientModifier(vehicle.getDefaultStepHeightModifier());
            }
         }

         this.travelWithInput(motion);
      }
   }

   void travelWithInput(Vec3 var1);

   boolean getPlayerJumped();

   void setPlayerJumped(boolean var1);

   boolean canJump();

   double getMountJumpStrength();

   boolean isMountJumping();

   void setMountJumping(boolean var1);

   float getSteeringSpeed();

   double jumpFactor();

   default void onJump(Mob vehicle) {
      CommonHooks.onLivingJump(vehicle);
   }

   default AttributeModifier getMountStepHeightModifier() {
      return STEP_HEIGHT_MODIFIER;
   }

   default AttributeModifier getDefaultStepHeightModifier() {
      return DEFAULT_STEP_HEIGHT_MODIFIER;
   }
}
