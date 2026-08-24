package com.aetherteam.aether.entity.projectile.weapon;

import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.network.packet.serverbound.HammerProjectileLaunchPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.PacketDistributor;

public class HammerProjectile extends ThrowableProjectile {
   private static final EntityDataAccessor<Boolean> DATA_JEB_ID = SynchedEntityData.defineId(HammerProjectile.class, EntityDataSerializers.BOOLEAN);
   private int ticksInAir = 0;

   public HammerProjectile(EntityType<? extends HammerProjectile> type, Level level) {
      super(type, level);
   }

   public HammerProjectile(LivingEntity owner, Level level) {
      super((EntityType)AetherEntityTypes.HAMMER_PROJECTILE.get(), owner, level);
   }

   public HammerProjectile(Level level) {
      super((EntityType)AetherEntityTypes.HAMMER_PROJECTILE.get(), level);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_JEB_ID, false);
   }

   public void tick() {
      super.tick();
      if (!this.onGround()) {
         this.ticksInAir++;
      }

      if (this.ticksInAir > 500 && !this.level().isClientSide()) {
         this.discard();
      }

      if (this.level().isClientSide()) {
         this.level().addParticle(ParticleTypes.CLOUD, this.getX(), this.getY() + 0.2, this.getZ(), 0.0, 0.0, 0.0);
      }
   }

   public void shoot(float rotationPitch, float rotationYaw, float velocity, float inaccuracy) {
      float x = -Mth.sin(rotationYaw * 0.017453292F) * Mth.cos(rotationPitch * 0.017453292F);
      float y = -Mth.sin(rotationPitch * 0.017453292F);
      float z = Mth.cos(rotationYaw * 0.017453292F) * Mth.cos(rotationPitch * 0.017453292F);
      super.shoot(x, y, z, velocity, inaccuracy);
   }

   protected void onHit(HitResult result) {
      super.onHit(result);
      if (!this.level().isClientSide()) {
         this.level().broadcastEntityEvent(this, (byte)3);
         this.discard();
      }
   }

   protected void onHitEntity(EntityHitResult result) {
      Entity target = result.getEntity();
      if (!this.level().isClientSide()) {
         this.launchTarget(target);
         this.level().broadcastEntityEvent(this, (byte)70);
      } else {
         PacketDistributor.sendToServer(new HammerProjectileLaunchPacket(target.getId(), this.getId()), new CustomPacketPayload[0]);
         this.spawnParticles();
      }
   }

   protected void onHitBlock(BlockHitResult result) {
      super.onHitBlock(result);

      for (Entity target : this.level().getEntities(this, this.getBoundingBox().inflate(5.0))) {
         if (!this.level().isClientSide()) {
            this.launchTarget(target);
         } else {
            PacketDistributor.sendToServer(new HammerProjectileLaunchPacket(target.getId(), this.getId()), new CustomPacketPayload[0]);
         }
      }

      if (!this.level().isClientSide()) {
         this.level().broadcastEntityEvent(this, (byte)70);
      } else {
         this.spawnParticles();
      }
   }

   private void spawnParticles() {
      for (int j = 0; j < 8; j++) {
         this.level().addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
         this.level().addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
         this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
         this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
         this.level().addParticle(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
      }
   }

   public void launchTarget(Entity target) {
      if (target != this.getOwner() && (this.getOwner() == null || target != this.getOwner().getVehicle()) && target instanceof LivingEntity livingEntity) {
         livingEntity.hurt(this.damageSources().thrown(this, this.getOwner()), 7.0F);
         livingEntity.push(this.getDeltaMovement().x(), 0.6, this.getDeltaMovement().z());
      }
   }

   protected double getDefaultGravity() {
      return 0.0;
   }

   public void setIsJeb(boolean isJeb) {
      this.getEntityData().set(DATA_JEB_ID, isJeb);
   }

   public boolean getIsJeb() {
      return (Boolean)this.getEntityData().get(DATA_JEB_ID);
   }

   public void handleEntityEvent(byte id) {
      if (id == 70) {
         this.spawnParticles();
      } else {
         super.handleEntityEvent(id);
      }
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putInt("TicksInAir", this.ticksInAir);
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.contains("TicksInAir")) {
         this.ticksInAir = tag.getInt("TicksInAir");
      }
   }
}
