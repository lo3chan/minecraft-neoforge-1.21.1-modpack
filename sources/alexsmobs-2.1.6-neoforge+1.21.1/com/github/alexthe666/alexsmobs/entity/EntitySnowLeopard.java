package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIPanicBaby;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.SnowLeopardAIMelee;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntitySnowLeopard extends Animal implements IAnimatedEntity, ITargetsDroppedItems {
   public static final Animation ANIMATION_ATTACK_R = Animation.create(13);
   public static final Animation ANIMATION_ATTACK_L = Animation.create(13);
   private int animationTick;
   private Animation currentAnimation;
   public float prevSneakProgress;
   public float sneakProgress;
   public float prevTackleProgress;
   public float tackleProgress;
   public float prevSitProgress;
   public float sitProgress;
   private static final EntityDataAccessor<Boolean> TACKLING = SynchedEntityData.defineId(EntitySnowLeopard.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(EntitySnowLeopard.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntitySnowLeopard.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SL_SNEAKING = SynchedEntityData.defineId(EntitySnowLeopard.class, EntityDataSerializers.BOOLEAN);
   private boolean hasSlowedDown = false;
   private int sittingTime = 0;
   private int maxSitTime = 75;
   public float prevSleepProgress;
   public float sleepProgress;

   protected EntitySnowLeopard(EntityType type, Level worldIn) {
      super(type, worldIn);
      AMCompat.setMaxUpStep(this, 2.0F);
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new AdvancedPathNavigateNoTeleport(this, worldIn, false);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.snowLeopardSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static <T extends Mob> boolean canSnowLeopardSpawn(
      EntityType<EntitySnowLeopard> snowleperd, LevelAccessor worldIn, MobSpawnType reason, BlockPos p_223317_3_, RandomSource random
   ) {
      return worldIn.getBlockState(p_223317_3_.below()).is(AMTagRegistry.SNOW_LEOPARD_SPAWNS) && worldIn.getRawBrightness(p_223317_3_, 0) > 8;
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.SNOW_LEOPARD_BREEDABLES);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new AnimalAIPanicBaby(this, 1.25));
      this.goalSelector.addGoal(3, new SnowLeopardAIMelee(this));
      this.goalSelector.addGoal(5, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0, 70));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 15.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new AnimalAIHurtByTargetNotBaby(this));
      this.targetSelector
         .addGoal(
            2,
            new NearestAttackableTargetGoal(
               this, LivingEntity.class, 10, false, true, AMCompat.selector(AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.SNOW_LEOPARD_TARGETS))
            )
         );
      this.targetSelector.addGoal(3, new CreatureAITargetItems(this, false, 30));
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 30.0)
         .add(Attributes.ATTACK_DAMAGE, 6.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3499999940395355)
         .add(Attributes.FOLLOW_RANGE, 64.0);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.SNOW_LEOPARD_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.SNOW_LEOPARD_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.SNOW_LEOPARD_HURT.get();
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SITTING, false);
      builder.define(SLEEPING, false);
      builder.define(SL_SNEAKING, false);
      builder.define(TACKLING, false);
   }

   public boolean isSitting() {
      return (Boolean)this.entityData.get(SITTING);
   }

   public void setSitting(boolean bar) {
      this.entityData.set(SITTING, bar);
   }

   public boolean isTackling() {
      return (Boolean)this.entityData.get(TACKLING);
   }

   public void setTackling(boolean bar) {
      this.entityData.set(TACKLING, bar);
   }

   public boolean isSLSneaking() {
      return (Boolean)this.entityData.get(SL_SNEAKING);
   }

   public void setSlSneaking(boolean bar) {
      this.entityData.set(SL_SNEAKING, bar);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return AMCompat.create(AMEntityRegistry.SNOW_LEOPARD.get(), serverWorld);
   }

   public void tick() {
      super.tick();
      this.prevSitProgress = this.sitProgress;
      this.prevSneakProgress = this.sneakProgress;
      this.prevTackleProgress = this.tackleProgress;
      this.prevSleepProgress = this.sleepProgress;
      boolean sitting = this.isSitting();
      boolean slSneaking = this.isSLSneaking();
      boolean tackling = this.isTackling();
      boolean sleeping = this.isSleeping();
      if (sitting) {
         if (this.sitProgress < 5.0F) {
            this.sitProgress += 0.5F;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress -= 0.5F;
      }

      if (slSneaking) {
         if (this.sneakProgress < 5.0F) {
            this.sneakProgress += 0.5F;
         }
      } else if (this.sneakProgress > 0.0F) {
         this.sneakProgress -= 0.5F;
      }

      if (tackling) {
         if (this.tackleProgress < 3.0F) {
            this.tackleProgress++;
         }
      } else if (this.tackleProgress > 0.0F) {
         this.tackleProgress--;
      }

      if (sleeping) {
         if (this.sleepProgress < 5.0F) {
            this.sleepProgress += 0.5F;
         }
      } else if (this.sleepProgress > 0.0F) {
         this.sleepProgress -= 0.5F;
      }

      if (slSneaking && !this.hasSlowedDown) {
         this.hasSlowedDown = true;
         this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
      }

      if (!slSneaking && this.hasSlowedDown) {
         this.hasSlowedDown = false;
         this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3499999940395355);
      }

      if (tackling) {
         this.yBodyRot = this.getYRot();
      }

      if (!this.level().isClientSide()) {
         if (this.getTarget() != null && (this.isSitting() || this.isSleeping())) {
            this.setSitting(false);
            this.setSleeping(false);
         }

         if ((this.isSitting() || this.isSleeping())
            && (++this.sittingTime > this.maxSitTime || this.getTarget() != null || this.isInLove() || this.isInWaterOrBubble())) {
            this.setSitting(false);
            this.setSleeping(false);
            this.sittingTime = 0;
            this.maxSitTime = 100 + this.random.nextInt(50);
         }

         if (this.getTarget() == null
            && this.getDeltaMovement().lengthSqr() < 0.03
            && this.getAnimation() == NO_ANIMATION
            && !this.isSleeping()
            && !this.isSitting()
            && !this.isInWaterOrBubble()
            && this.random.nextInt(340) == 0) {
            this.sittingTime = 0;
            if (this.getRandom().nextInt(2) != 0) {
               this.maxSitTime = 200 + this.random.nextInt(800);
               this.setSitting(true);
               this.setSleeping(false);
            } else {
               this.maxSitTime = 2000 + this.random.nextInt(2600);
               this.setSitting(false);
               this.setSleeping(true);
            }
         }
      }

      LivingEntity attackTarget = this.getTarget();
      if (attackTarget != null && this.distanceTo(attackTarget) < attackTarget.getBbWidth() + this.getBbWidth() + 0.6 && this.hasLineOfSight(attackTarget)) {
         if (this.getAnimation() == ANIMATION_ATTACK_L && this.getAnimationTick() == 7) {
            AMCompat.doHurtTarget(this, attackTarget);
            float rot = this.getYRot() + 90.0F;
            AMCompat.knockback(attackTarget, 0.5, Mth.sin(rot * 0.017453292F), -Mth.cos(rot * 0.017453292F));
         }

         if (this.getAnimation() == ANIMATION_ATTACK_R && this.getAnimationTick() == 7) {
            AMCompat.doHurtTarget(this, attackTarget);
            float rot = this.getYRot() - 90.0F;
            AMCompat.knockback(attackTarget, 0.5, Mth.sin(rot * 0.017453292F), -Mth.cos(rot * 0.017453292F));
         }
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean prev = super.hurt(source, amount);
      if (prev) {
         this.sittingTime = 0;
         this.setSleeping(false);
         this.setSitting(false);
      }

      return prev;
   }

   public void travel(Vec3 vec3d) {
      if (this.isSitting() || this.isSleeping()) {
         if (this.getNavigation().getPath() != null) {
            this.getNavigation().stop();
         }

         vec3d = Vec3.ZERO;
      }

      super.travel(vec3d);
   }

   protected boolean isImmobile() {
      return super.isImmobile();
   }

   public boolean isSleeping() {
      return (Boolean)this.entityData.get(SLEEPING);
   }

   public void setSleeping(boolean sleeping) {
      this.entityData.set(SLEEPING, sleeping);
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
   public int getAnimationTick() {
      return this.animationTick;
   }

   @Override
   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   @Override
   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_ATTACK_L, ANIMATION_ATTACK_R};
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return AMCompat.isEdible(stack.getItem()) && AMCompat.getFoodProperties(stack.getItem()) != null && AMCompat.isMeat(stack.getItem());
   }

   @Override
   public void onGetItem(ItemEntity e) {
      this.heal(5.0F);
   }
}
