package com.aetherteam.aether.entity.projectile.crystal;

import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.client.particle.AetherParticleTypes;
import com.aetherteam.aether.data.resources.registries.AetherDamageTypes;
import com.aetherteam.aether.entity.EntityUtil;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ThunderCrystal extends AbstractCrystal {
   private Entity target;

   public ThunderCrystal(EntityType<? extends ThunderCrystal> entityType, Level level) {
      super(entityType, level);
   }

   public ThunderCrystal(EntityType<? extends ThunderCrystal> entityType, Level level, Entity shooter, Entity target) {
      super(entityType, level);
      this.setOwner(shooter);
      this.target = target;
      this.setPos(shooter.getX(), shooter.getEyeY(), shooter.getZ());
   }

   protected void onHitEntity(EntityHitResult result) {
      if (result.getEntity() instanceof LivingEntity livingTarget && livingTarget != this.getOwner()) {
         livingTarget.hurt(AetherDamageTypes.indirectEntityDamageSource(this.level(), AetherDamageTypes.THUNDER_CRYSTAL, this, this.getOwner()), 5.0F);
         this.knockback(0.1, this.position().subtract(livingTarget.position()));
         livingTarget.knockback(0.25, this.getX() - livingTarget.getX(), this.getZ() - livingTarget.getZ());
      }
   }

   @Override
   public void tickMovement() {
      if (!this.level().isClientSide()) {
         if (this.target != null && this.target.isAlive() && this.getOwner() != null && this.getOwner().isAlive()) {
            if (this.ticksInAir > this.getLifeSpan()) {
               if (this.target != null && this.target.isAlive()) {
                  EntityUtil.summonLightningFromProjectile(this);
               }

               if (this.getImpactExplosionSoundEvent() != null) {
                  this.playSound(this.getImpactExplosionSoundEvent(), 1.0F, 1.0F);
               }
            } else {
               Vec3 motion = this.getDeltaMovement().scale(0.9);
               Vec3 targetMotion = new Vec3(this.target.getX() - this.getX(), this.target.getEyeY() - 0.1 - this.getY(), this.target.getZ() - this.getZ())
                  .normalize();
               this.setDeltaMovement(motion.add(targetMotion.scale(0.02)));
            }
         } else {
            if (this.getImpactExplosionSoundEvent() != null) {
               this.playSound(this.getImpactExplosionSoundEvent(), 1.0F, 1.0F);
            }

            this.discard();
         }
      }

      this.checkInsideBlocks();
      Vec3 motion = this.getDeltaMovement();
      this.setPos(this.getX() + motion.x(), this.getY() + motion.y(), this.getZ() + motion.z());
   }

   public boolean hurt(DamageSource source, float amount) {
      if (!this.level().isClientSide() && source.getSourcePosition() != null && this.level() instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2, 0.2, 0.2, 0.0);
         this.knockback(0.15 + amount / 8.0F, this.position().subtract(source.getSourcePosition()));
      }

      this.ticksInAir += (int)(amount * 10.0F);
      return true;
   }

   public void knockback(double strength, Vec3 target) {
      this.hasImpulse = true;
      Vec3 vec3 = this.getDeltaMovement();
      Vec3 vec31 = target.normalize().scale(strength);
      this.setDeltaMovement(vec3.x() / 2.0 + vec31.x(), vec3.y() / 2.0 + vec31.y(), vec3.z() / 2.0 + vec31.z());
   }

   public void checkDespawn() {
      if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
         this.discard();
      }
   }

   @Override
   protected ParticleOptions getExplosionParticle() {
      return (ParticleOptions)AetherParticleTypes.FROZEN.get();
   }

   @Nullable
   @Override
   protected SoundEvent getImpactExplosionSoundEvent() {
      return (SoundEvent)AetherSoundEvents.ENTITY_THUNDER_CRYSTAL_EXPLODE.get();
   }

   @Override
   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      if (this.target != null) {
         tag.putInt("Target", this.target.getId());
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.contains("Target")) {
         this.target = this.level().getEntity(tag.getInt("Target"));
      }
   }
}
