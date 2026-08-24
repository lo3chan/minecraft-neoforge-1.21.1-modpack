package com.aetherteam.aether.entity.miscellaneous;

import com.aetherteam.aether.entity.EntityUtil;
import com.aetherteam.aether.mixin.mixins.common.accessor.ServerGamePacketListenerImplAccessor;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Parachute extends Entity {
   public Parachute(EntityType<? extends Parachute> type, Level level) {
      super(type, level);
      this.blocksBuilding = true;
      this.setDeltaMovement(Vec3.ZERO);
      this.xo = this.getX();
      this.yo = this.getY();
      this.zo = this.getZ();
   }

   protected void defineSynchedData(Builder builder) {
   }

   public void tick() {
      super.tick();
      LivingEntity passenger = this.getControllingPassenger();
      if (passenger != null) {
         this.checkSlowFallDistance();
         this.moveParachute(passenger);
         this.spawnExplosionParticle();
         if (this.onGround() || this.isInFluidType() || this.verticalCollisionBelow) {
            passenger.checkSlowFallDistance();
            this.ejectPassengers();
            this.die();
         }
      } else {
         this.die();
      }
   }

   public void moveParachute(LivingEntity passenger) {
      if (this.isVehicle()) {
         this.setYRot(passenger.getYRot());
         this.yRotO = this.getYRot();
         this.setXRot(passenger.getXRot() * 0.5F);
         this.setRot(this.getYRot(), this.getXRot());
         float x = passenger.xxa * 0.5F;
         float z = passenger.zza;
         if (z <= 0.0F) {
            z *= 0.25F;
         }

         Vec3 travelVec = new Vec3(x, passenger.yya, z);
         AttributeInstance gravity = passenger.getAttribute(Attributes.GRAVITY);
         double gravityModifier = gravity != null ? gravity.getValue() : 0.08;
         Vec3 movement = this.calculateMovement(travelVec);
         double y = movement.y();
         if (!this.isNoGravity()) {
            y -= gravityModifier;
         }

         y *= 0.98;
         double fallSpeed = Math.max(gravityModifier * -3.125, -0.25);
         this.setDeltaMovement(movement.x() * 0.9100000262260437, Math.max(y, fallSpeed), movement.z() * 0.9100000262260437);
         if (passenger instanceof ServerPlayer serverPlayer) {
            ServerGamePacketListenerImplAccessor serverGamePacketListenerImplAccessor = (ServerGamePacketListenerImplAccessor)serverPlayer.connection;
            serverGamePacketListenerImplAccessor.aether$setAboveGroundTickCount(0);
            serverGamePacketListenerImplAccessor.aether$setAboveGroundVehicleTickCount(0);
         }
      }
   }

   public Vec3 calculateMovement(Vec3 vec3) {
      float speed = 0.03F;
      this.moveRelative(speed, vec3);
      this.move(MoverType.SELF, this.getDeltaMovement());
      return this.getDeltaMovement();
   }

   public void die() {
      this.spawnExplosionParticle();
      if (!this.level().isClientSide()) {
         this.kill();
      }
   }

   public void spawnExplosionParticle() {
      if (!this.level().isClientSide()) {
         this.level().broadcastEntityEvent(this, (byte)70);
      }
   }

   protected boolean canRide(Entity entity) {
      return true;
   }

   public boolean canRiderInteract() {
      return false;
   }

   public boolean shouldRiderSit() {
      return false;
   }

   public Vec3 getPassengerRidingPosition(Entity entity) {
      return super.getPassengerRidingPosition(entity).add(0.0, 0.6, 0.0);
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
      Direction direction = this.getMotionDirection();
      if (direction.getAxis() == Axis.Y) {
         return this.position().add(0.0, 0.5, 0.0);
      } else {
         Vec3 dismountLocation = this.position().add(0.0, 0.5, 0.0);
         return !DismountHelper.canDismountTo(this.level(), passenger, passenger.getType().getDimensions().makeBoundingBox(dismountLocation))
            ? this.position().add(0.0, 1.0, 0.0).add(new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ()).scale(0.5).reverse())
            : dismountLocation;
      }
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      return this.getFirstPassenger() instanceof LivingEntity rider ? rider : null;
   }

   public boolean isAttackable() {
      return false;
   }

   public boolean isPickable() {
      return !this.isRemoved();
   }

   public boolean displayFireAnimation() {
      return false;
   }

   public void handleEntityEvent(byte id) {
      if (id == 70) {
         EntityUtil.spawnMovementExplosionParticles(this);
      } else {
         super.handleEntityEvent(id);
      }
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
   }
}
