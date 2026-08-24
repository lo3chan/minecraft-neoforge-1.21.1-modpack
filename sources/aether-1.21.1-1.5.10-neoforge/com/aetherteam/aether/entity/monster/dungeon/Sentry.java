package com.aetherteam.aether.entity.monster.dungeon;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.client.AetherSoundEvents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Slime.SlimeAttackGoal;
import net.minecraft.world.entity.monster.Slime.SlimeFloatGoal;
import net.minecraft.world.entity.monster.Slime.SlimeKeepOnJumpingGoal;
import net.minecraft.world.entity.monster.Slime.SlimeRandomDirectionGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Block;

public class Sentry extends Slime {
   private static final EntityDataAccessor<Boolean> DATA_AWAKE_ID = SynchedEntityData.defineId(Sentry.class, EntityDataSerializers.BOOLEAN);
   private float timeSpotted = 0.0F;

   public Sentry(EntityType<? extends Sentry> type, Level level) {
      super(type, level);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new Sentry.SentryFloatGoal(this));
      this.goalSelector.addGoal(2, new Sentry.SentryAttackGoal(this));
      this.goalSelector.addGoal(3, new Sentry.SentryRandomDirectionGoal(this));
      this.goalSelector.addGoal(5, new Sentry.SentryKeepOnJumpingGoal(this));
      this.targetSelector
         .addGoal(1, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, entity -> Math.abs(entity.getY() - this.getY()) <= 4.0));
   }

   public static Builder createMobAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.6).add(Attributes.ATTACK_DAMAGE);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_AWAKE_ID, false);
   }

   public void tick() {
      if (this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), 8.0, EntitySelector.NO_SPECTATORS) != null) {
         if (!this.isAwake()) {
            if (this.timeSpotted >= 24.0F) {
               this.setAwake(true);
            }

            this.timeSpotted++;
         }
      } else {
         this.setAwake(false);
      }

      super.tick();
   }

   public void jumpFromGround() {
      if (this.isAwake()) {
         super.jumpFromGround();
      }
   }

   public void push(Entity entity) {
      super.push(entity);
      if (entity instanceof LivingEntity livingEntity && !(entity instanceof Sentry)) {
         this.explodeAt(livingEntity);
      }
   }

   public void playerTouch(Player player) {
      if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player)) {
         this.explodeAt(player);
      }
   }

   protected void explodeAt(LivingEntity entity) {
      DamageSource damageSource = this.damageSources().mobAttack(this);
      if (this.distanceToSqr(entity) < 1.5
         && this.isAwake()
         && this.hasLineOfSight(entity)
         && entity.hurt(damageSource, 1.0F)
         && this.tickCount > 20
         && this.isAlive()) {
         entity.push(0.3, 0.4, 0.3);
         this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1.0F, ExplosionInteraction.MOB);
         this.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 0.2F * (this.getRandom().nextFloat() - this.getRandom().nextFloat()) + 1.0F);
         if (this.level() instanceof ServerLevel level) {
            level.broadcastEntityEvent(this, (byte)70);
            level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0.5);
            EnchantmentHelper.doPostAttackEffects(level, entity, damageSource);
         }

         this.discard();
      }
   }

   public boolean isAwake() {
      return (Boolean)this.getEntityData().get(DATA_AWAKE_ID);
   }

   public void setAwake(boolean awake) {
      this.getEntityData().set(DATA_AWAKE_ID, awake);
   }

   public void setSize(int size, boolean resetHealth) {
   }

   protected ParticleOptions getParticleType() {
      return new BlockParticleOption(ParticleTypes.BLOCK, ((Block)AetherBlocks.SENTRY_STONE.get()).defaultBlockState());
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return (SoundEvent)AetherSoundEvents.ENTITY_SENTRY_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_SENTRY_DEATH.get();
   }

   protected SoundEvent getSquishSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_SENTRY_JUMP.get();
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_SENTRY_AMBIENT.get();
   }

   protected SoundEvent getJumpSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_SENTRY_SQUISH.get();
   }

   protected boolean shouldDespawnInPeaceful() {
      return true;
   }

   public void handleEntityEvent(byte id) {
      if (id == 70) {
         for (int i = 0; i < 40; i++) {
            double x = this.getX() + this.getRandom().nextFloat() * 0.25;
            double y = this.getY() + 0.5;
            double z = this.getZ() + this.getRandom().nextFloat() * 0.25;
            float f1 = this.getRandom().nextFloat() * 360.0F;
            this.level().addParticle(ParticleTypes.POOF, x, y, z, -Math.sin(0.017453292F * f1) * 0.75, 0.125, Math.cos(0.017453292F * f1) * 0.75);
         }
      } else {
         super.handleEntityEvent(id);
      }
   }

   static class SentryAttackGoal extends SlimeAttackGoal {
      private final Sentry sentry;

      public SentryAttackGoal(Sentry sentry) {
         super(sentry);
         this.sentry = sentry;
      }

      public boolean canUse() {
         return this.sentry.isAwake() && super.canUse();
      }

      public boolean canContinueToUse() {
         return this.sentry.isAwake() && super.canContinueToUse();
      }
   }

   static class SentryFloatGoal extends SlimeFloatGoal {
      private final Sentry sentry;

      public SentryFloatGoal(Sentry sentry) {
         super(sentry);
         this.sentry = sentry;
      }

      public boolean canUse() {
         return this.sentry.isAwake() && super.canUse();
      }

      public boolean canContinueToUse() {
         return this.sentry.isAwake() && super.canContinueToUse();
      }
   }

   static class SentryKeepOnJumpingGoal extends SlimeKeepOnJumpingGoal {
      private final Sentry sentry;

      public SentryKeepOnJumpingGoal(Sentry sentry) {
         super(sentry);
         this.sentry = sentry;
      }

      public boolean canUse() {
         return this.sentry.isAwake() && super.canUse();
      }

      public boolean canContinueToUse() {
         return this.sentry.isAwake() && super.canContinueToUse();
      }
   }

   static class SentryRandomDirectionGoal extends SlimeRandomDirectionGoal {
      private final Sentry sentry;

      public SentryRandomDirectionGoal(Sentry sentry) {
         super(sentry);
         this.sentry = sentry;
      }

      public boolean canUse() {
         return this.sentry.isAwake() && super.canUse();
      }

      public boolean canContinueToUse() {
         return this.sentry.isAwake() && super.canContinueToUse();
      }
   }
}
