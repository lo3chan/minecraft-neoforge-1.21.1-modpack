package com.aetherteam.aether.entity.projectile.crystal;

import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.data.resources.registries.AetherDamageTypes;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.entity.monster.dungeon.boss.SunSpirit;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class FireCrystal extends AbstractCrystal {
   private double xPower;
   private double yPower;
   private double zPower;

   public FireCrystal(EntityType<? extends FireCrystal> entityType, Level level) {
      super(entityType, level);
   }

   public FireCrystal(Level level, Entity shooter) {
      this((EntityType<? extends FireCrystal>)AetherEntityTypes.FIRE_CRYSTAL.get(), level);
      this.setOwner(shooter);
      this.setPos(shooter.getX(), shooter.getY() + 1.0, shooter.getZ());
      float rotation = this.random.nextFloat() * 360.0F;
      this.xPower = Mth.sin(rotation) * 0.5;
      this.zPower = -Mth.cos(rotation) * 0.5;
      this.yPower = Mth.sin(this.random.nextFloat() * 360.0F) * 0.45;
      double verticalOffset = 1.0 - Math.abs(this.yPower);
      this.xPower *= verticalOffset;
      this.zPower *= verticalOffset;
      this.setDeltaMovement(this.xPower, this.yPower, this.zPower);
   }

   @Override
   protected void tickMovement() {
      if (!this.level().isClientSide()
         && (
            this.getOwner() == null
               || !this.getOwner().isAlive()
               || this.getOwner() instanceof SunSpirit sunSpirit && sunSpirit.getDungeon() != null && sunSpirit.getDungeon().dungeonPlayers().isEmpty()
         )) {
         if (this.getImpactExplosionSoundEvent() != null) {
            this.playSound(this.getImpactExplosionSoundEvent(), 1.0F, 1.0F);
         }

         this.discard();
      }

      super.tickMovement();
   }

   protected void onHitEntity(EntityHitResult result) {
      if (result.getEntity() instanceof LivingEntity livingEntity
         && livingEntity.hurt(AetherDamageTypes.indirectEntityDamageSource(this.level(), AetherDamageTypes.FIRE_CRYSTAL, this, this.getOwner()), 15.0F)) {
         livingEntity.igniteForSeconds(6.0F);
         if (this.getImpactExplosionSoundEvent() != null) {
            this.level()
               .playSound(
                  null,
                  this.getX(),
                  this.getY(),
                  this.getZ(),
                  this.getImpactExplosionSoundEvent(),
                  SoundSource.HOSTILE,
                  2.0F,
                  this.random.nextFloat() - this.random.nextFloat() * 0.2F + 1.2F
               );
         }

         if (!this.level().isClientSide()) {
            this.discard();
         }
      }
   }

   protected void onHitBlock(BlockHitResult result) {
      this.markHurt();
      switch (result.getDirection()) {
         case NORTH:
         case SOUTH:
            this.zPower = -this.zPower;
            break;
         case UP:
         case DOWN:
            this.yPower = -this.yPower;
            break;
         case WEST:
         case EAST:
            this.xPower = -this.xPower;
      }

      this.setDeltaMovement(this.xPower, this.yPower, this.zPower);
   }

   public boolean hurt(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else {
         this.markHurt();
         Entity entity = source.getEntity();
         if (entity != null) {
            if (!this.level().isClientSide()) {
               Vec3 vec3 = entity.getLookAngle();
               this.setDeltaMovement(vec3);
               this.xPower = vec3.x() * 0.25;
               this.yPower = vec3.y() * 0.15;
               this.zPower = vec3.z() * 0.25;
               this.ticksInAir += (int)(amount * 10.0F);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   protected ParticleOptions getExplosionParticle() {
      return ParticleTypes.FLAME;
   }

   @Nullable
   @Override
   protected SoundEvent getImpactExplosionSoundEvent() {
      return (SoundEvent)AetherSoundEvents.ENTITY_FIRE_CRYSTAL_EXPLODE.get();
   }

   @Override
   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putDouble("XSpeed", this.xPower);
      tag.putDouble("YSpeed", this.yPower);
      tag.putDouble("ZSpeed", this.zPower);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.xPower = tag.getDouble("XSpeed");
      this.yPower = tag.getDouble("YSpeed");
      this.zPower = tag.getDouble("ZSpeed");
   }
}
