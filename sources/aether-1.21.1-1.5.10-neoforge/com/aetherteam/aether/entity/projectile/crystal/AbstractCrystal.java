package com.aetherteam.aether.entity.projectile.crystal;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.event.EventHooks;

public abstract class AbstractCrystal extends Projectile {
   protected int ticksInAir = 0;

   protected AbstractCrystal(EntityType<? extends AbstractCrystal> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
   }

   protected void defineSynchedData(Builder builder) {
   }

   public void tick() {
      super.tick();
      if (!this.onGround()) {
         this.ticksInAir++;
      }

      if (this.ticksInAir > this.getLifeSpan() && !this.level().isClientSide()) {
         this.discard();
      }

      HitResult result = ProjectileUtil.getHitResultOnMoveVector(this, x$0 -> this.canHitEntity(x$0));
      boolean flag = false;
      if (result.getType() != Type.MISS && !flag && !EventHooks.onProjectileImpact(this, result)) {
         this.onHit(result);
      }

      this.checkInsideBlocks();
      this.tickMovement();
   }

   public void remove(RemovalReason reason) {
      this.spawnExplosionParticles();
      super.remove(reason);
   }

   protected void tickMovement() {
      Vec3 vector3d = this.getDeltaMovement();
      double d2 = this.getX() + vector3d.x();
      double d0 = this.getY() + vector3d.y();
      double d1 = this.getZ() + vector3d.z();
      this.updateRotation();
      this.setPos(d2, d0, d1);
   }

   public void spawnExplosionParticles() {
      if (this.level() instanceof ServerLevel level) {
         for (int i = 0; i < 20; i++) {
            double x = (this.random.nextFloat() - 0.5F) * 0.5;
            double y = (this.random.nextFloat() - 0.5F) * 0.5;
            double z = (this.random.nextFloat() - 0.5F) * 0.5;
            level.sendParticles(this.getExplosionParticle(), this.getX(), this.getY(), this.getZ(), 1, x, y, z, 0.0);
         }
      }
   }

   protected abstract ParticleOptions getExplosionParticle();

   @Nullable
   protected SoundEvent getImpactExplosionSoundEvent() {
      return null;
   }

   public int getLifeSpan() {
      return 300;
   }

   public boolean isPickable() {
      return true;
   }

   public boolean isOnFire() {
      return false;
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
