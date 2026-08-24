package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeapRandomly;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.BunfungusAIBeg;
import com.github.alexthe666.alexsmobs.entity.ai.BunfungusAIMelee;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class EntityBunfungus extends PathfinderMob implements IAnimatedEntity {
   public static final Animation ANIMATION_SLAM = Animation.create(20);
   public static final Animation ANIMATION_BELLY = Animation.create(10);
   public static final Animation ANIMATION_EAT = Animation.create(20);
   private static final EntityDataAccessor<Boolean> JUMP_ACTIVE = SynchedEntityData.defineId(EntityBunfungus.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(EntityBunfungus.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> BEGGING = SynchedEntityData.defineId(EntityBunfungus.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> CARROTED = SynchedEntityData.defineId(EntityBunfungus.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> TRANSFORMS_IN = SynchedEntityData.defineId(EntityBunfungus.class, EntityDataSerializers.INT);
   public float jumpProgress;
   public float prevJumpProgress;
   public float reboundProgress;
   public float prevReboundProgress;
   public float sleepProgress;
   public float prevSleepProgress;
   public float interestedProgress;
   public float prevInterestedProgress;
   private int animationTick;
   private Animation currentAnimation;
   public int prevTransformTime;
   public static final int MAX_TRANSFORM_TIME = 50;

   protected EntityBunfungus(EntityType t, Level lvl) {
      super(t, lvl);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 80.0)
         .add(Attributes.ATTACK_DAMAGE, 8.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.MOVEMENT_SPEED, 0.20999999344348907);
   }

   public void playAmbientSound() {
      if (!this.isSleeping()) {
         super.playAmbientSound();
      }
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.BUNFUNGUS_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.BUNFUNGUS_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.BUNFUNGUS_HURT.get();
   }

   public boolean removeWhenFarAway(double p_27598_) {
      return false;
   }

   public static boolean canBunfungusSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      return worldIn.getBlockState(pos.below()).canOcclude();
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.mungusSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new GroundPathNavigatorWide(this, worldIn);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new BunfungusAIMelee(this));
      this.goalSelector.addGoal(2, new BunfungusAIBeg(this, 1.0));
      this.goalSelector.addGoal(3, new AnimalAIWanderRanged(this, 60, 1.0, 16, 7) {
         @Override
         public boolean canUse() {
            return super.canUse() && EntityBunfungus.this.canUseComplexAI();
         }
      });
      this.goalSelector.addGoal(4, new AnimalAILeapRandomly(this, 60, 7) {
         @Override
         public boolean canUse() {
            return super.canUse() && EntityBunfungus.this.canUseComplexAI();
         }
      });
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 10.0F) {
         public boolean canUse() {
            return super.canUse() && EntityBunfungus.this.canUseComplexAI();
         }
      });
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this) {
         public boolean canUse() {
            return super.canUse() && EntityBunfungus.this.canUseComplexAI();
         }
      });
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector
         .addGoal(
            3,
            new NearestAttackableTargetGoal(
               this,
               Mob.class,
               5,
               false,
               false,
               AMCompat.selector(
                  mob -> mob instanceof Enemy
                     && !(mob instanceof Creeper)
                     && (!AMCompat.isAquatic(mob) || !mob.isInWaterOrBubble())
                     && !mob.getType().builtInRegistryHolder().is(AMTagRegistry.BUNFUNGUS_IGNORES)
               )
            )
         );
   }

   private boolean canUseComplexAI() {
      return !this.isRabbitForm() && !this.isSleeping();
   }

   protected float getWaterSlowDown() {
      return 0.98F;
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(JUMP_ACTIVE, false);
      builder.define(SLEEPING, false);
      builder.define(BEGGING, false);
      builder.define(CARROTED, false);
      builder.define(TRANSFORMS_IN, 0);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public void tick() {
      super.tick();
      this.prevJumpProgress = this.jumpProgress;
      this.prevReboundProgress = this.reboundProgress;
      this.prevSleepProgress = this.sleepProgress;
      this.prevInterestedProgress = this.interestedProgress;
      this.prevTransformTime = this.transformsIn();
      if (!this.level().isClientSide()) {
         this.entityData.set(JUMP_ACTIVE, !this.onGround());
      }

      if ((Boolean)this.entityData.get(JUMP_ACTIVE) && !this.isInWaterOrBubble()) {
         if (this.jumpProgress < 5.0F) {
            this.jumpProgress += 0.5F;
            if (this.reboundProgress > 0.0F) {
               this.reboundProgress--;
            }
         }

         if (this.jumpProgress >= 5.0F && this.reboundProgress < 5.0F) {
            this.reboundProgress += 0.5F;
         }
      } else {
         if (this.reboundProgress > 0.0F) {
            this.reboundProgress = Math.max(this.reboundProgress - 1.0F, 0.0F);
         }

         if (this.jumpProgress > 0.0F) {
            this.jumpProgress = Math.max(this.jumpProgress - 1.0F, 0.0F);
         }
      }

      if (this.isSleepingPose()) {
         if (this.sleepProgress < 5.0F) {
            this.sleepProgress++;
         }
      } else if (this.sleepProgress > 0.0F) {
         this.sleepProgress--;
      }

      if (this.isBegging()) {
         if (this.interestedProgress < 5.0F) {
            this.interestedProgress++;
         }
      } else if (this.interestedProgress > 0.0F) {
         this.interestedProgress--;
      }

      if (!this.level().isClientSide()) {
         LivingEntity target = this.getTarget();
         if (target != null && target.isAlive()) {
            if (this.isSleeping()) {
               this.setSleeping(false);
            }

            double dist = this.distanceTo(target);
            boolean flag = false;
            if (this.getAnimationTick() == 5) {
               if (dist < 3.5 && this.getAnimation() == ANIMATION_BELLY) {
                  for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0))) {
                     if ((entity == target || entity instanceof Monster)
                        && !entity.getType().builtInRegistryHolder().is(AMTagRegistry.BUNFUNGUS_IGNORE_AOE_ATTACKS)) {
                        flag = true;
                        this.launch(entity);
                        entity.hurt(this.damageSources().mobAttack(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                     }
                  }
               } else if (dist < 2.5 && this.getAnimation() == ANIMATION_SLAM) {
                  for (LivingEntity entityx : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0))) {
                     if ((entityx == target || entityx instanceof Monster)
                        && !entityx.getType().builtInRegistryHolder().is(AMTagRegistry.BUNFUNGUS_IGNORE_AOE_ATTACKS)) {
                        flag = true;
                        AMCompat.knockback(entityx, 0.20000000298023224, entityx.getX() - this.getX(), entityx.getZ() - this.getZ());
                        entityx.hurt(this.damageSources().mobAttack(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                     }
                  }
               }
            }

            if (flag) {
               this.playSound(AMSoundRegistry.BUNFUNGUS_ATTACK.get(), this.getSoundVolume(), this.getVoicePitch());
            }
         }

         if (this.tickCount % 40 == 0) {
            this.heal(1.0F);
         }
      }

      if (this.getAnimation() == NO_ANIMATION && this.isCarrot(this.getItemInHand(InteractionHand.MAIN_HAND))) {
         this.setAnimation(ANIMATION_EAT);
      }

      if (this.getAnimation() == ANIMATION_EAT) {
         if (this.getAnimationTick() % 4 == 0) {
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
         }

         if (this.getAnimationTick() >= 18) {
            ItemStack stack = this.getItemInHand(InteractionHand.MAIN_HAND);
            if (!stack.isEmpty()) {
               stack.shrink(1);
               this.setCarroted(true);
               this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1000));
               this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1000, 1));
               this.heal(8.0F);
            }
         } else {
            for (int i = 0; i < 3; i++) {
               double d2 = this.random.nextGaussian() * 0.02;
               double d0 = this.random.nextGaussian() * 0.02;
               double d1 = this.random.nextGaussian() * 0.02;
               this.level()
                  .addParticle(
                     new ItemParticleOption(ParticleTypes.ITEM, this.getItemInHand(InteractionHand.MAIN_HAND)),
                     this.getX() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
                     this.getY() + this.getBbHeight() * 0.5F + this.random.nextFloat() * this.getBbHeight() * 0.5F,
                     this.getZ() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
                     d0,
                     d1,
                     d2
                  );
            }
         }
      }

      if (!this.level().isClientSide() && this.transformsIn() > 0) {
         this.setTransformsIn(this.transformsIn() - 1);
      }

      if (this.level().isClientSide()) {
         if (this.isRabbitForm()) {
            for (int i = 0; i < 3; i++) {
               double d2 = this.random.nextGaussian() * 0.02;
               double d0 = this.random.nextGaussian() * 0.02;
               double d1 = this.random.nextGaussian() * 0.02;
               float f1 = (50 - this.transformsIn()) / 50.0F;
               float scale = f1 * 0.5F + 0.15F;
               this.level()
                  .addParticle(
                     (ParticleOptions)AMParticleRegistry.BUNFUNGUS_TRANSFORMATION.get(),
                     this.getRandomX(scale),
                     this.getY(this.random.nextDouble() * scale),
                     this.getRandomZ(scale),
                     d0,
                     d1,
                     d2
                  );
            }
         }

         if (this.isSleeping() && this.random.nextFloat() < 0.3F) {
            double d0 = this.random.nextGaussian() * 0.02;
            float radius = this.getBbWidth() * (0.7F + this.random.nextFloat() * 0.1F);
            float angle = 0.017453292F * this.yBodyRot;
            double extraX = radius * Mth.sin(3.1415927F + angle) + this.random.nextFloat() * 0.5F - 0.25F;
            double extraZ = radius * Mth.cos(angle) + this.random.nextFloat() * 0.5F - 0.25F;
            ParticleOptions data = this.random.nextFloat() < 0.3F
               ? (ParticleOptions)AMParticleRegistry.BUNFUNGUS_TRANSFORMATION.get()
               : (ParticleOptions)AMParticleRegistry.FUNGUS_BUBBLE.get();
            this.level().addParticle(data, this.getX() + extraX, this.getY() + this.random.nextFloat() * 0.1F, this.getZ() + extraZ, 0.0, d0, 0.0);
         }
      } else if (this.level().isDay() && this.getTarget() == null && !this.isBegging() && !this.isInWaterOrBubble()) {
         if (this.tickCount % 10 == 0 && this.getRandom().nextInt(300) == 0) {
            this.setSleeping(true);
         }
      } else if (this.isSleeping()) {
         this.setSleeping(false);
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   private void launch(LivingEntity target) {
      if (target.onGround()) {
         double d0 = target.getX() - this.getX();
         double d1 = target.getZ() - this.getZ();
         double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
         float f = 6.0F + this.random.nextFloat() * 2.0F;
         target.push(d0 / d2 * f, 0.6F + this.random.nextFloat() * 0.7F, d1 / d2 * f);
      }
   }

   public boolean isSleeping() {
      return (Boolean)this.entityData.get(SLEEPING);
   }

   public void setSleeping(boolean sleeping) {
      this.entityData.set(SLEEPING, sleeping);
   }

   public boolean isSleepingPose() {
      return this.isSleeping() || this.getAnimation() == ANIMATION_SLAM && this.getAnimationTick() < 10;
   }

   public boolean isCarroted() {
      return (Boolean)this.entityData.get(CARROTED);
   }

   public void setCarroted(boolean head) {
      this.entityData.set(CARROTED, head);
   }

   public boolean isBegging() {
      return (Boolean)this.entityData.get(BEGGING) && this.getAnimation() != ANIMATION_EAT;
   }

   public void setBegging(boolean begging) {
      this.entityData.set(BEGGING, begging);
   }

   public int transformsIn() {
      return Math.min((Integer)this.entityData.get(TRANSFORMS_IN), 50);
   }

   public boolean isRabbitForm() {
      return this.transformsIn() > 0;
   }

   public void setTransformsIn(int time) {
      this.entityData.set(TRANSFORMS_IN, time);
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      InteractionResult type = super.mobInteract(player, hand);
      InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
      if (interactionresult != InteractionResult.SUCCESS
         && type != InteractionResult.SUCCESS
         && this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()
         && this.isCarrot(itemstack)
         && this.getMainHandItem().isEmpty()) {
         ItemStack cop = itemstack.copy();
         cop.setCount(1);
         this.setItemInHand(InteractionHand.MAIN_HAND, cop);
         if (!player.isCreative()) {
            itemstack.shrink(1);
         }
      }

      return type;
   }

   public void travel(Vec3 travelVector) {
      if (!this.isRabbitForm() && !this.isSleeping()) {
         super.travel(travelVector);
      } else {
         super.travel(Vec3.ZERO);
      }
   }

   @Override
   public int getAnimationTick() {
      return this.animationTick;
   }

   @Override
   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   @Override
   public Animation getAnimation() {
      return this.currentAnimation;
   }

   @Override
   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   @Override
   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_EAT, ANIMATION_BELLY, ANIMATION_SLAM};
   }

   public boolean isCarrot(ItemStack stack) {
      return stack.is(AMTagRegistry.BUNFUNGUS_FOODSTUFFS);
   }

   public boolean defendsMungusAgainst(LivingEntity lastHurtByMob) {
      return !(lastHurtByMob instanceof Player) || this.isCarroted();
   }

   public void onJump() {
   }
}
