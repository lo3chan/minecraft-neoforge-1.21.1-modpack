package net.diebuddies.mixins.ocean;

import net.diebuddies.compat.SableCreate;
import net.diebuddies.compat.ValkyrienSkies;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.EntityOcean;
import net.diebuddies.physics.ocean.OceanWorld;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Camera.class})
public class MixinCamera {
   @Shadow
   private Vec3 position;
   @Shadow
   private boolean initialized;
   @Shadow
   private Entity entity;
   @Shadow
   private BlockGetter level;

   @Shadow
   private void setPosition(double x, double y, double z) {
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"getFluidInCamera"},
      cancellable = true
   )
   private void getFluidInCamera(CallbackInfoReturnable<FogType> info) {
      if (this.initialized && ConfigClient.areOceanPhysicsEnabled()) {
         FogType fogType = (FogType)info.getReturnValue();
         Vec3 position = ((Camera)this).getPosition();
         OceanWorld oceanWorld = PhysicsMod.getInstance(this.entity.level()).getPhysicsWorld().getOceanWorld();
         if ((fogType == FogType.WATER || fogType == FogType.NONE) && oceanWorld.isInsideOceanRange(position.x(), position.y(), position.z())) {
            if (oceanWorld.isInsideOceanWater(position.x(), position.y(), position.z())) {
               info.setReturnValue(FogType.WATER);
            } else {
               info.setReturnValue(FogType.NONE);
            }
         }
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"setup"}
   )
   public void setup(BlockGetter blockGetter, Entity entity, boolean thirdPerson, boolean thirdPersonInverted, float renderPercent, CallbackInfo info) {
      if (ConfigClient.areOceanPhysicsEnabled() && entity != null && entity.level() != null && entity.level() instanceof ClientLevel) {
         Camera camera = (Camera)this;
         Vec3 position = camera.getPosition();
         EntityOcean entityOcean = (EntityOcean)entity;
         if (StarterClient.valkyrienSkies && entity.getVehicle() == null && ValkyrienSkies.hasShipMount(entity) != null) {
            this.setPosition(position.x(), position.y(), position.z());
         } else if (StarterClient.sable && entity.getVehicle() == null && SableCreate.hasShipMount(entity) != null) {
            this.setPosition(position.x(), position.y(), position.z());
         } else {
            this.setPosition(position.x(), position.y() + entityOcean.getPhysicsYOffset(renderPercent), position.z());
         }
      }
   }
}
