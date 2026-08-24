package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.citadel.server.entity.collision.ICustomCollisions;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIPanicBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.ai.MovementControllerCustomCollisions;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EntityTiger extends Animal implements ICustomCollisions, IAnimatedEntity, NeutralMob, ITargetsDroppedItems {
   public static final Animation ANIMATION_PAW_R = Animation.create(15);
   public static final Animation ANIMATION_PAW_L = Animation.create(15);
   public static final Animation ANIMATION_TAIL_FLICK = Animation.create(45);
   public static final Animation ANIMATION_LEAP = Animation.create(20);
   private static final EntityDataAccessor<Boolean> WHITE = SynchedEntityData.defineId(EntityTiger.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(EntityTiger.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityTiger.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(EntityTiger.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> STEALTH_MODE = SynchedEntityData.defineId(EntityTiger.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> HOLDING = SynchedEntityData.defineId(EntityTiger.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> ANGER_TIME = SynchedEntityData.defineId(EntityTiger.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> LAST_SCARED_MOB_ID = SynchedEntityData.defineId(EntityTiger.class, EntityDataSerializers.INT);
   private static final UniformInt ANGRY_TIMER = TimeUtil.rangeOfSeconds(40, 80);
   private static final Predicate<LivingEntity> NO_BLESSING_EFFECT = mob -> !mob.hasEffect(AMCompat.effect(AMEffectRegistry.TIGERS_BLESSING.get()));
   public float prevSitProgress;
   public float sitProgress;
   public float prevSleepProgress;
   public float sleepProgress;
   public float prevHoldProgress;
   public float holdProgress;
   public float prevStealthProgress;
   public float stealthProgress;
   private int animationTick;
   private Animation currentAnimation;
   private boolean hasSpedUp = false;
   private UUID lastHurtBy;
   private int sittingTime;
   private int maxSitTime;
   private int holdTime = 0;
   private int prevScaredMobId = -1;
   private boolean dontSitFlag = false;

   protected EntityTiger(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
      this.moveControl = new MovementControllerCustomCollisions(this);
   }

   public static boolean canTigerSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
      return worldIn.getRawBrightness(pos, 0) > 8;
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 50.0)
         .add(Attributes.ATTACK_DAMAGE, 12.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.FOLLOW_RANGE, 86.0);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.tigerSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
      return worldIn.getFluidState(pos.below()).isEmpty() && worldIn.getFluidState(pos).is(FluidTags.WATER) ? 0.0F : super.getWalkTargetValue(pos, worldIn);
   }

   public boolean checkSpawnObstruction(LevelReader worldIn) {
      return !worldIn.containsAnyLiquid(this.getBoundingBox());
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("TigerSitting", this.isSitting());
      compound.putBoolean("TigerSleeping", this.isSleeping());
      compound.putBoolean("White", this.isWhite());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setSitting(AMCompat.getBoolean(compound, "TigerSitting"));
      this.setSleeping(AMCompat.getBoolean(compound, "TigerSleeping"));
      this.setWhite(AMCompat.getBoolean(compound, "White"));
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(WHITE, false);
      builder.define(RUNNING, false);
      builder.define(SITTING, false);
      builder.define(STEALTH_MODE, false);
      builder.define(HOLDING, false);
      builder.define(SLEEPING, false);
      builder.define(ANGER_TIME, 0);
      builder.define(LAST_SCARED_MOB_ID, -1);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new AnimalAIPanicBaby(this, 1.25));
      this.goalSelector.addGoal(3, new EntityTiger.AIMelee());
      this.goalSelector.addGoal(5, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(7, new AnimalAIWanderRanged(this, 60, 1.0, 14, 7));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 25.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false, 10));
      this.targetSelector.addGoal(2, new EntityTiger.AngerGoal(this));
      this.targetSelector.addGoal(3, new EntityTiger.AttackPlayerGoal());
      this.targetSelector
         .addGoal(
            4,
            new NearestAttackableTargetGoal(
               this, LivingEntity.class, 220, false, false, AMCompat.selector(AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.TIGER_TARGETS))
            ) {
               public boolean canUse() {
                  return !EntityTiger.this.isBaby() && super.canUse();
               }
            }
         );
      this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal(this, true));
   }

   protected SoundEvent getAmbientSound() {
      return this.isStealth()
         ? super.getAmbientSound()
         : (this.getRemainingPersistentAngerTime() > 0 ? AMSoundRegistry.TIGER_ANGRY.get() : AMSoundRegistry.TIGER_IDLE.get());
   }

   public int getAmbientSoundInterval() {
      return this.getRemainingPersistentAngerTime() > 0 ? 40 : 80;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.TIGER_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.TIGER_HURT.get();
   }

   protected float getWaterSlowDown() {
      return 0.99F;
   }

   public boolean shouldMove() {
      return !this.isSitting() && !this.isSleeping() && !this.isHolding();
   }

   public double getVisibilityPercent(@Nullable Entity lookingEntity) {
      return this.isStealth() ? 0.2 : super.getVisibilityPercent(lookingEntity);
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.TIGER_BREEDABLES);
   }

   public void awardKillScore(LivingEntity entity, int score, DamageSource src) {
      this.heal(5.0F);
      super.awardKillScore(entity, score, src);
   }

   public void travel(Vec3 vec3d) {
      if (!this.shouldMove()) {
         if (this.getNavigation().getPath() != null) {
            this.getNavigation().stop();
         }

         vec3d = Vec3.ZERO;
      }

      super.travel(vec3d);
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new EntityTiger.Navigator(this, worldIn);
   }

   public boolean isWhite() {
      return (Boolean)this.entityData.get(WHITE);
   }

   public void setWhite(boolean white) {
      this.entityData.set(WHITE, white);
   }

   public boolean isRunning() {
      return (Boolean)this.entityData.get(RUNNING);
   }

   public void setRunning(boolean running) {
      this.entityData.set(RUNNING, running);
   }

   public boolean isSitting() {
      return (Boolean)this.entityData.get(SITTING);
   }

   public void setSitting(boolean bar) {
      this.entityData.set(SITTING, bar);
   }

   public boolean isStealth() {
      return (Boolean)this.entityData.get(STEALTH_MODE);
   }

   public void setStealth(boolean bar) {
      this.entityData.set(STEALTH_MODE, bar);
   }

   public boolean isHolding() {
      return (Boolean)this.entityData.get(HOLDING);
   }

   public void setHolding(boolean running) {
      this.entityData.set(HOLDING, running);
   }

   public boolean isSleeping() {
      return (Boolean)this.entityData.get(SLEEPING);
   }

   public void setSleeping(boolean sleeping) {
      this.entityData.set(SLEEPING, sleeping);
   }

   public int getRemainingPersistentAngerTime() {
      return (Integer)this.entityData.get(ANGER_TIME);
   }

   public void setRemainingPersistentAngerTime(int time) {
      this.entityData.set(ANGER_TIME, time);
   }

   public UUID getPersistentAngerTarget() {
      return this.lastHurtBy;
   }

   public void setPersistentAngerTarget(@Nullable UUID target) {
      this.lastHurtBy = target;
   }

   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(ANGRY_TIMER.sample(this.random));
   }

   protected void customServerAiStep() {
      if (!this.level().isClientSide()) {
         this.updatePersistentAnger((ServerLevel)this.level(), false);
      }
   }

   public boolean isColliding(BlockPos pos, BlockState blockstate) {
      return blockstate.getBlock() != Blocks.BAMBOO && !blockstate.is(BlockTags.LEAVES) && super.isColliding(pos, blockstate);
   }

   public Vec3 collide(Vec3 vec3) {
      return ICustomCollisions.getAllowedMovementForEntity(this, vec3);
   }

   public void tick() {
      super.tick();
      this.prevSitProgress = this.sitProgress;
      this.prevSleepProgress = this.sleepProgress;
      this.prevHoldProgress = this.holdProgress;
      this.prevStealthProgress = this.stealthProgress;
      boolean sitting = this.isSitting();
      boolean sleeping = this.isSleeping();
      boolean holding = this.isHolding();
      boolean stealth = this.isStealth();
      if (sitting) {
         if (this.sitProgress < 5.0F) {
            this.sitProgress++;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (sleeping) {
         if (this.sleepProgress < 5.0F) {
            this.sleepProgress++;
         }
      } else if (this.sleepProgress > 0.0F) {
         this.sleepProgress--;
      }

      if (holding) {
         if (this.holdProgress < 5.0F) {
            this.holdProgress++;
         }
      } else if (this.holdProgress > 0.0F) {
         this.holdProgress--;
      }

      if (stealth) {
         if (this.stealthProgress < 10.0F) {
            this.stealthProgress += 0.25F;
         }
      } else if (this.stealthProgress > 0.0F) {
         this.stealthProgress--;
      }

      if (!this.level().isClientSide()) {
         if (this.isRunning() && !this.hasSpedUp) {
            this.hasSpedUp = true;
            AMCompat.setMaxUpStep(this, 1.0F);
            this.setSprinting(true);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.4000000059604645);
         }

         if (!this.isRunning() && this.hasSpedUp) {
            this.hasSpedUp = false;
            AMCompat.setMaxUpStep(this, 0.6F);
            this.setSprinting(false);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
         }

         if ((this.isSitting() || this.isSleeping())
            && (++this.sittingTime > this.maxSitTime || this.getTarget() != null || this.isInLove() || this.dontSitFlag || this.isInWaterOrBubble())) {
            this.setSitting(false);
            this.setSleeping(false);
            this.sittingTime = 0;
            this.maxSitTime = 100 + this.random.nextInt(50);
         }

         if (this.getTarget() == null
            && !this.dontSitFlag
            && this.getDeltaMovement().lengthSqr() < 0.03
            && this.getAnimation() == NO_ANIMATION
            && !this.isSleeping()
            && !this.isSitting()
            && !this.isInWaterOrBubble()
            && this.random.nextInt(100) == 0) {
            this.sittingTime = 0;
            if (this.getRandom().nextBoolean()) {
               this.maxSitTime = 100 + this.random.nextInt(550);
               this.setSitting(true);
               this.setSleeping(false);
            } else {
               this.maxSitTime = 200 + this.random.nextInt(550);
               this.setSitting(false);
               this.setSleeping(true);
            }
         }

         if (this.getDeltaMovement().lengthSqr() < 0.03
            && this.getAnimation() == NO_ANIMATION
            && !this.isSleeping()
            && !this.isSitting()
            && this.random.nextInt(100) == 0) {
            this.setAnimation(ANIMATION_TAIL_FLICK);
         }
      }

      if (this.isHolding()) {
         this.setSprinting(false);
         this.setRunning(false);
         Entity target = this.getTarget();
         if (!this.level().isClientSide() && target != null && target.isAlive()) {
            this.setXRot(0.0F);
            float radius = 1.0F + target.getBbWidth() * 0.5F;
            float angle = 0.017453292F * this.yBodyRot;
            double extraX = radius * Mth.sin(3.1415927F + angle);
            double extraZ = radius * Mth.cos(angle);
            double extraY = -0.5;
            Vec3 minus = new Vec3(this.getX() + extraX - target.getX(), this.getY() + -0.5 - target.getY(), this.getZ() + extraZ - target.getZ());
            target.setDeltaMovement(minus);
            target.hasImpulse = true;
            if (this.holdTime % 20 == 0) {
               target.hurt(this.damageSources().mobAttack(this), 5 + this.getRandom().nextInt(2));
            }

            if (target.distanceTo(this) > 8.0F) {
               this.setHolding(false);
               this.holdTime = 150;
            }
         }

         this.holdTime++;
         if (this.holdTime > 100) {
            this.holdTime = 0;
            this.setHolding(false);
         }
      } else {
         this.holdTime = 0;
      }

      if (this.prevScaredMobId != (Integer)this.entityData.get(LAST_SCARED_MOB_ID) && this.level().isClientSide()) {
         Entity e = this.level().getEntity((Integer)this.entityData.get(LAST_SCARED_MOB_ID));
         if (e != null) {
            double d2 = this.random.nextGaussian() * 0.1;
            double d0 = this.random.nextGaussian() * 0.1;
            double d1 = this.random.nextGaussian() * 0.1;
            this.level()
               .addParticle(
                  (ParticleOptions)AMParticleRegistry.SHOCKED.get(),
                  e.getX(),
                  e.getEyeY() + e.getBbHeight() * 0.15F + this.random.nextFloat() * e.getBbHeight() * 0.15F,
                  e.getZ(),
                  d0,
                  d1,
                  d2
               );
         }
      }

      if (this.getTarget() != null && this.getTarget().hasEffect(AMCompat.effect(AMEffectRegistry.TIGERS_BLESSING.get()))) {
         this.setTarget(null);
         this.setLastHurtByMob(null);
      }

      this.prevScaredMobId = (Integer)this.entityData.get(LAST_SCARED_MOB_ID);
      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean prev = super.hurt(source, amount);
      if (prev) {
         if (source.getEntity() != null && source.getEntity() instanceof LivingEntity) {
            LivingEntity hurter = (LivingEntity)source.getEntity();
            if (hurter.hasEffect(AMCompat.effect(AMEffectRegistry.TIGERS_BLESSING.get()))) {
               hurter.removeEffect(AMCompat.effect(AMEffectRegistry.TIGERS_BLESSING.get()));
            }
         }

         return prev;
      } else {
         return prev;
      }
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public BlockPos getLightPosition() {
      BlockPos pos = AMBlockPos.fromVec3(this.position());
      return !this.level().getBlockState(pos).canOcclude() ? pos.above() : pos;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      boolean whiteOther = p_241840_2_ instanceof EntityTiger && ((EntityTiger)p_241840_2_).isWhite();
      EntityTiger baby = AMCompat.create(AMEntityRegistry.TIGER.get(), p_241840_1_);
      double whiteChance = 0.1;
      if (this.isWhite() && whiteOther) {
         whiteChance = 0.8;
      }

      if (this.isWhite() != whiteOther) {
         whiteChance = 0.4;
      }

      baby.setWhite(this.random.nextDouble() < whiteChance);
      return baby;
   }

   @Override
   public boolean canPassThrough(BlockPos mutablePos, BlockState blockstate, VoxelShape voxelshape) {
      return blockstate.getBlock() == Blocks.BAMBOO || blockstate.is(BlockTags.LEAVES);
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
      return new Animation[]{ANIMATION_PAW_R, ANIMATION_PAW_L, ANIMATION_LEAP, ANIMATION_TAIL_FLICK};
   }

   @Override
   public int getAnimationTick() {
      return this.animationTick;
   }

   @Override
   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public void push(Entity entityIn) {
      if (!this.isHolding() || entityIn != this.getTarget()) {
         super.push(entityIn);
      }
   }

   protected void doPush(Entity entityIn) {
      if (!this.isHolding() || entityIn != this.getTarget()) {
         super.doPush(entityIn);
      }
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return AMCompat.isEdible(stack.getItem())
         && AMCompat.getFoodProperties(stack.getItem()) != null
         && AMCompat.isMeat(stack.getItem())
         && stack.getItem() != Items.ROTTEN_FLESH;
   }

   @Override
   public double getMaxDistToItem() {
      return 3.0;
   }

   @Override
   public void onGetItem(ItemEntity e) {
      this.dontSitFlag = false;
      ItemStack stack = e.getItem();
      if (AMCompat.isEdible(stack.getItem())
         && AMCompat.getFoodProperties(stack.getItem()) != null
         && AMCompat.isMeat(stack.getItem())
         && stack.getItem() != Items.ROTTEN_FLESH) {
         this.gameEvent(GameEvent.EAT);
         this.playSound(SoundEvents.CAT_EAT, this.getVoicePitch(), this.getSoundVolume());
         this.heal(5.0F);
         Entity thrower = e.getOwner();
         if (thrower != null && this.random.nextFloat() < this.getChanceForEffect(stack) && this.level().getPlayerByUUID(thrower.getUUID()) != null) {
            Player player = this.level().getPlayerByUUID(thrower.getUUID());
            player.addEffect(new MobEffectInstance(AMCompat.effect(AMEffectRegistry.TIGERS_BLESSING.get()), 12000));
            this.setTarget(null);
            this.setLastHurtByMob(null);
         }
      }
   }

   @Override
   public void onFindTarget(ItemEntity e) {
      this.dontSitFlag = true;
      this.setSitting(false);
      this.setSleeping(false);
   }

   public double getChanceForEffect(ItemStack stack) {
      if (stack.getItem() == Items.PORKCHOP || stack.getItem() == Items.COOKED_PORKCHOP) {
         return 0.4000000059604645;
      } else {
         return stack.getItem() != Items.CHICKEN && stack.getItem() != Items.COOKED_CHICKEN ? 0.10000000149011612 : 0.30000001192092896;
      }
   }

   public void jumpFromGround() {
      if (!this.isSleeping() && !this.isSitting()) {
         super.jumpFromGround();
      }
   }

   private class AIMelee extends Goal {
      private final EntityTiger tiger;
      private int jumpAttemptCooldown = 0;

      public AIMelee() {
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
         this.tiger = EntityTiger.this;
      }

      public boolean canUse() {
         return this.tiger.getTarget() != null && this.tiger.getTarget().isAlive();
      }

      public void tick() {
         if (this.jumpAttemptCooldown > 0) {
            this.jumpAttemptCooldown--;
         }

         LivingEntity target = this.tiger.getTarget();
         if (target != null && target.isAlive()) {
            double dist = this.tiger.distanceTo(target);
            if (dist < 10.0 && this.tiger.getLastHurtByMob() != null && this.tiger.getLastHurtByMob().isAlive()) {
               this.tiger.setStealth(false);
            } else if (dist > 20.0) {
               this.tiger.setRunning(false);
               this.tiger.setStealth(true);
            }

            if (dist <= 20.0) {
               this.tiger.setStealth(false);
               this.tiger.setRunning(true);
               if ((Integer)this.tiger.entityData.get(EntityTiger.LAST_SCARED_MOB_ID) != target.getId()) {
                  this.tiger.entityData.set(EntityTiger.LAST_SCARED_MOB_ID, target.getId());
                  target.addEffect(new MobEffectInstance(AMCompat.effect(AMEffectRegistry.FEAR.get()), 100, 0, true, false));
               }
            }

            if (dist < 12.0
               && this.tiger.getAnimation() == IAnimatedEntity.NO_ANIMATION
               && this.tiger.onGround()
               && this.jumpAttemptCooldown == 0
               && !this.tiger.isHolding()) {
               this.tiger.setAnimation(EntityTiger.ANIMATION_LEAP);
               this.jumpAttemptCooldown = 70;
            }

            if ((this.jumpAttemptCooldown > 0 || this.tiger.isInWaterOrBubble())
               && !this.tiger.isHolding()
               && this.tiger.getAnimation() == IAnimatedEntity.NO_ANIMATION
               && dist < 4.0F + target.getBbWidth()) {
               this.tiger.setAnimation(this.tiger.getRandom().nextBoolean() ? EntityTiger.ANIMATION_PAW_L : EntityTiger.ANIMATION_PAW_R);
            }

            if (dist < 4.0F + target.getBbWidth()
               && (this.tiger.getAnimation() == EntityTiger.ANIMATION_PAW_L || this.tiger.getAnimation() == EntityTiger.ANIMATION_PAW_R)
               && this.tiger.getAnimationTick() == 8) {
               target.hurt(this.tiger.damageSources().mobAttack(this.tiger), 7 + this.tiger.getRandom().nextInt(5));
            }

            if (this.tiger.getAnimation() == EntityTiger.ANIMATION_LEAP) {
               this.tiger.getNavigation().stop();
               Vec3 vec = target.position().subtract(this.tiger.position());
               this.tiger.setYRot(-((float)Mth.atan2(vec.x, vec.z)) * 57.295776F);
               this.tiger.yBodyRot = this.tiger.getYRot();
               if (this.tiger.getAnimationTick() >= 5 && this.tiger.getAnimationTick() < 11 && this.tiger.onGround()) {
                  Vec3 vector3d1 = new Vec3(target.getX() - this.tiger.getX(), 0.0, target.getZ() - this.tiger.getZ());
                  if (vector3d1.lengthSqr() > 1.0E-7) {
                     vector3d1 = vector3d1.normalize().scale(Math.min(dist, 15.0) * 0.20000000298023224);
                  }

                  this.tiger
                     .setDeltaMovement(
                        vector3d1.x,
                        vector3d1.y + 0.30000001192092896 + 0.10000000149011612 * Mth.clamp(target.getEyeY() - this.tiger.getY(), 0.0, 2.0),
                        vector3d1.z
                     );
               }

               if (dist < target.getBbWidth() + 3.0F && this.tiger.getAnimationTick() >= 15) {
                  target.hurt(this.tiger.damageSources().mobAttack(this.tiger), 2.0F);
                  this.tiger.setRunning(false);
                  this.tiger.setStealth(false);
                  this.tiger.setHolding(true);
               }
            } else if (target != null) {
               this.tiger.getNavigation().moveTo(target, this.tiger.isStealth() ? 0.75 : 1.0);
            }
         }
      }

      public void stop() {
         this.tiger.setStealth(false);
         this.tiger.setRunning(false);
         this.tiger.setHolding(false);
      }
   }

   class AngerGoal extends HurtByTargetGoal {
      AngerGoal(EntityTiger beeIn) {
         super(beeIn, new Class[0]);
      }

      public boolean canContinueToUse() {
         return EntityTiger.this.isAngry() && super.canContinueToUse();
      }

      public void start() {
         super.start();
         if (EntityTiger.this.isBaby()) {
            this.alertOthers();
            this.stop();
         }
      }

      protected void alertOther(Mob mobIn, LivingEntity targetIn) {
         if (!mobIn.isBaby()) {
            super.alertOther(mobIn, targetIn);
         }
      }
   }

   class AttackPlayerGoal extends NearestAttackableTargetGoal<Player> {
      public AttackPlayerGoal() {
         super(EntityTiger.this, Player.class, 100, false, true, AMCompat.selector(EntityTiger.NO_BLESSING_EFFECT));
      }

      public boolean canUse() {
         return EntityTiger.this.isBaby() ? false : super.canUse();
      }

      protected double getFollowDistance() {
         return 4.0;
      }
   }

   static class Navigator extends GroundPathNavigatorWide {
      public Navigator(Mob mob, Level world) {
         super(mob, world, 1.2F);
      }

      protected PathFinder createPathFinder(int i) {
         this.nodeEvaluator = new EntityTiger.TigerNodeEvaluator();
         return new PathFinder(this.nodeEvaluator, i);
      }
   }

   static class TigerNodeEvaluator extends WalkNodeEvaluator {
      public PathType getPathType(PathfindingContext context, int x, int y, int z) {
         PathType typeIn = super.getPathType(context, x, y, z);
         BlockPos pos = new BlockPos(x, y, z);
         return typeIn != PathType.LEAVES && context.getBlockState(pos).getBlock() != Blocks.BAMBOO ? typeIn : PathType.OPEN;
      }
   }
}
