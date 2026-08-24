package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIRideParent;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.AnteaterAIRaidNest;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class EntityAnteater extends Animal implements NeutralMob, IAnimatedEntity, ITargetsDroppedItems {
   public static final Animation ANIMATION_SLASH_R = Animation.create(20);
   public static final Animation ANIMATION_TOUNGE_IDLE = Animation.create(10);
   public static final Animation ANIMATION_SLASH_L = Animation.create(20);
   private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.defineId(EntityAnteater.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> LEANING_DOWN = SynchedEntityData.defineId(EntityAnteater.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> ANT_ON_TONGUE = SynchedEntityData.defineId(EntityAnteater.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> ANGER_TIME = SynchedEntityData.defineId(EntityAnteater.class, EntityDataSerializers.INT);
   public float prevStandProgress;
   public float standProgress;
   public float prevTongueProgress;
   public float tongueProgress;
   public float prevLeaningProgress;
   public float leaningProgress;
   public int eatAntCooldown = 0;
   public int ticksAntOnTongue = 0;
   private int animationTick;
   private Animation currentAnimation;
   private int maxStandTime = 75;
   private int standingTime = 0;
   private int antsEatenRecently = 0;
   private int heldItemTime;
   private UUID lastHurtBy;
   private static final UniformInt ANGRY_TIMER = TimeUtil.rangeOfSeconds(30, 60);

   protected EntityAnteater(EntityType type, Level world) {
      super(type, world);
      AMCompat.setMaxUpStep(this, 1.0F);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.ATTACK_DAMAGE, 6.0).add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   public static boolean canAnteaterSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
      return worldIn.getRawBrightness(pos, 0) > 8;
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.anteaterSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new EntityAnteater.AIMelee());
      this.goalSelector.addGoal(3, new AnteaterAIRaidNest(this));
      this.goalSelector.addGoal(4, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(5, new AnimalAIRideParent(this, 1.25));
      this.goalSelector.addGoal(6, new TemptGoal(this, 1.2, AMCompat.ingredientOf(AMTagRegistry.ANTEATER_FOODSTUFFS), false));
      this.goalSelector.addGoal(7, new AnimalAIWanderRanged(this, 110, 1.0, 10, 7));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10.0F));
      this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false, false, 25, 16));
      this.targetSelector.addGoal(2, new AnimalAIHurtByTargetNotBaby(this));
      this.targetSelector.addGoal(3, new EntityAnteater.AITargetAnts());
   }

   public Vec3 getPassengerRidingPosition(Entity passenger) {
      return new Vec3(this.getX(), this.getY() + this.getBbHeight() * 0.75, this.getZ());
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return super.isInvulnerableTo(source) || source.getDirectEntity() != null && source.getDirectEntity() instanceof EntityLeafcutterAnt;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.ANTEATER_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.ANTEATER_HURT.get();
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Standing", this.isStanding());
      compound.putInt("AntCooldown", this.eatAntCooldown);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setStanding(AMCompat.getBoolean(compound, "Standing"));
      this.eatAntCooldown = AMCompat.getInt(compound, "AntCooldown");
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.ANTEATER_BREEDABLES);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(STANDING, Boolean.FALSE);
      builder.define(ANT_ON_TONGUE, Boolean.FALSE);
      builder.define(LEANING_DOWN, Boolean.FALSE);
      builder.define(ANGER_TIME, 0);
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

   public boolean isStanding() {
      return (Boolean)this.entityData.get(STANDING);
   }

   public void setStanding(boolean standing) {
      this.entityData.set(STANDING, standing);
   }

   public boolean hasAntOnTongue() {
      return (Boolean)this.entityData.get(ANT_ON_TONGUE);
   }

   public void setAntOnTongue(boolean standing) {
      this.entityData.set(ANT_ON_TONGUE, standing);
   }

   public boolean canCollideWith(Entity entity) {
      return !(entity instanceof EntityLeafcutterAnt) && super.canCollideWith(entity);
   }

   public void push(Entity entity) {
      if (!(entity instanceof EntityLeafcutterAnt)) {
         super.push(entity);
      }
   }

   public boolean isLeaning() {
      return (Boolean)this.entityData.get(LEANING_DOWN);
   }

   public void setLeaning(boolean leaning) {
      this.entityData.set(LEANING_DOWN, leaning);
   }

   protected boolean isImmobile() {
      return super.isImmobile();
   }

   protected void customServerAiStep() {
      if (!this.level().isClientSide()) {
         this.updatePersistentAnger((ServerLevel)this.level(), false);
      }
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      InteractionResult type = super.mobInteract(player, hand);
      boolean isFoodstuff = itemstack.is(AMTagRegistry.ANTEATER_FOODSTUFFS);
      if (isFoodstuff) {
         ItemStack rippedStack = itemstack.copy();
         rippedStack.setCount(1);
         this.stopBeingAngry();
         this.heal(4.0F);
         this.setItemInHand(InteractionHand.MAIN_HAND, rippedStack);
         if (itemstack.is(AMTagRegistry.ANTEATER_BREEDABLES)) {
            return type;
         } else {
            this.usePlayerItem(player, hand, itemstack);
            return InteractionResult.SUCCESS;
         }
      } else {
         return type;
      }
   }

   public void tick() {
      super.tick();
      this.prevStandProgress = this.standProgress;
      this.prevTongueProgress = this.tongueProgress;
      this.prevLeaningProgress = this.leaningProgress;
      if (this.isStanding()) {
         if (this.standProgress < 5.0F) {
            this.standProgress++;
         }
      } else if (this.standProgress > 0.0F) {
         this.standProgress--;
      }

      boolean isTongueOut = this.getAnimation() == ANIMATION_TOUNGE_IDLE;
      if (isTongueOut) {
         if (this.tongueProgress < 5.0F) {
            this.tongueProgress++;
         }
      } else if (this.tongueProgress > 0.0F) {
         this.tongueProgress--;
      }

      if (this.isLeaning()) {
         if (this.leaningProgress < 5.0F) {
            this.leaningProgress++;
         }
      } else if (this.leaningProgress > 0.0F) {
         this.leaningProgress--;
      }

      if (this.isStanding() && ++this.standingTime > this.maxStandTime) {
         this.setStanding(false);
         this.standingTime = 0;
         this.maxStandTime = 75 + this.random.nextInt(50);
      }

      if (this.isPassenger() && this.getVehicle() instanceof EntityAnteater mount) {
         if (this.isBaby()) {
            this.setYRot(mount.yBodyRot);
            this.yHeadRot = mount.yBodyRot;
            this.yBodyRot = mount.yBodyRot;
         } else {
            this.removeVehicle();
         }
      }

      if (this.eatAntCooldown > 0) {
         this.eatAntCooldown--;
      }

      if (this.antsEatenRecently >= 3 && this.eatAntCooldown <= 0) {
         this.resetAntCooldown();
      }

      if (this.ticksAntOnTongue > 10 && this.hasAntOnTongue()) {
         this.heal(6.0F);
         this.gameEvent(GameEvent.EAT);
         this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
         this.setAntOnTongue(false);
      }

      if (this.hasAntOnTongue()) {
         this.ticksAntOnTongue++;
      } else {
         this.ticksAntOnTongue = 0;
      }

      if (!this.level().isClientSide() && this.getTongueStickOut() > 0.6F && !this.hasAntOnTongue() && this.antsEatenRecently < 3) {
         EntityLeafcutterAnt closestAnt = null;

         for (EntityLeafcutterAnt entity : this.level().getEntitiesOfClass(EntityLeafcutterAnt.class, this.getBoundingBox().inflate(2.5999999046325684))) {
            if (closestAnt == null || entity.distanceTo(this) < closestAnt.distanceTo(this) && this.hasLineOfSight(entity)) {
               closestAnt = entity;
            }
         }

         if (closestAnt != null) {
            closestAnt.remove(RemovalReason.KILLED);
            this.ticksAntOnTongue = 0;
            this.setAntOnTongue(true);
            this.antsEatenRecently++;
         }
      }

      if (!this.getMainHandItem().isEmpty()) {
         this.heldItemTime++;
         if (this.heldItemTime > 10 && this.getTongueStickOut() < 0.3F && this.canTargetItem(this.getMainHandItem())) {
            this.heldItemTime = 0;
            this.heal(4.0F);
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            this.gameEvent(GameEvent.EAT);
            if (AMCompat.hasCraftingRemainder(this.getMainHandItem())) {
               AMCompat.spawnAtLocation(this, AMCompat.craftingRemainder(this.getMainHandItem()));
            }

            this.stopBeingAngry();
            this.getMainHandItem().shrink(1);
         }
      } else {
         this.heldItemTime = 0;
      }

      if (!this.level().isClientSide()) {
         if (this.getRandom().nextInt(300) == 0) {
            this.setAnimation(ANIMATION_TOUNGE_IDLE);
         }

         LivingEntity attackTarget = this.getTarget();
         if (attackTarget != null && this.distanceTo(attackTarget) < attackTarget.getBbWidth() + this.getBbWidth() + 2.0F && this.getAnimationTick() == 7) {
            if (this.getAnimation() == ANIMATION_SLASH_L) {
               AMCompat.doHurtTarget(this, attackTarget);
               float rot = this.getYRot() + 90.0F;
               AMCompat.knockback(attackTarget, 0.5, Mth.sin(rot * 0.017453292F), -Mth.cos(rot * 0.017453292F));
            } else if (this.getAnimation() == ANIMATION_SLASH_R) {
               AMCompat.doHurtTarget(this, attackTarget);
               float rot = this.getYRot() - 90.0F;
               AMCompat.knockback(attackTarget, 0.5, Mth.sin(rot * 0.017453292F), -Mth.cos(rot * 0.017453292F));
            }
         }
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public void resetAntCooldown() {
      this.eatAntCooldown = 600 + this.random.nextInt(1000);
      this.antsEatenRecently = 0;
   }

   public void standFor(int time) {
      this.setStanding(true);
      this.maxStandTime = time;
   }

   public float getTongueStickOut() {
      if (this.tongueProgress > 0.0F) {
         double tongueM = Math.min(Math.sin(this.tickCount * 0.15F), 0.0);
         return (float)(-tongueM) * (this.tongueProgress * 0.2F);
      } else {
         return 0.0F;
      }
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
      return AMCompat.create(AMEntityRegistry.ANTEATER.get(), this.level());
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
   public boolean canTargetItem(ItemStack stack) {
      return !this.hasAntOnTongue() && stack.is(AMTagRegistry.INSECT_ITEMS);
   }

   @Override
   public void onGetItem(ItemEntity e) {
      ItemStack duplicate = e.getItem().copy();
      duplicate.setCount(1);
      if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level().isClientSide()) {
         AMCompat.spawnAtLocation(this, this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
      }

      this.setAnimation(ANIMATION_TOUNGE_IDLE);
      this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
   }

   @Override
   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_SLASH_L, ANIMATION_SLASH_R, ANIMATION_TOUNGE_IDLE};
   }

   private boolean shouldTargetAnts() {
      return !this.isAngry();
   }

   public boolean isPeter() {
      String name = ChatFormatting.stripFormatting(this.getName().getString());
      if (name == null) {
         return false;
      } else {
         String lowercaseName = name.toLowerCase(Locale.ROOT);
         return lowercaseName.contains("peter") || lowercaseName.contains("petr") || lowercaseName.contains("zot");
      }
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      if (spawnDataIn == null) {
         spawnDataIn = new AgeableMobGroupData(0.5F);
      }

      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   private class AIMelee extends Goal {
      public AIMelee() {
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         return EntityAnteater.this.getTarget() != null && EntityAnteater.this.getTarget().isAlive() && !EntityAnteater.this.isBaby();
      }

      public void tick() {
         LivingEntity enemy = EntityAnteater.this.getTarget();
         if (enemy != null) {
            double attackReachSqr = this.getAttackReachSqr(enemy);
            double distToEnemySqr = EntityAnteater.this.distanceTo(enemy);
            EntityAnteater.this.lookAt(enemy, 100.0F, 5.0F);
            if (enemy instanceof EntityLeafcutterAnt) {
               if (distToEnemySqr <= attackReachSqr + 1.5) {
                  EntityAnteater.this.setAnimation(EntityAnteater.ANIMATION_TOUNGE_IDLE);
               } else {
                  EntityAnteater.this.lookAt(enemy, 5.0F, 5.0F);
               }

               EntityAnteater.this.getNavigation().moveTo(enemy, 1.0);
            } else {
               if (distToEnemySqr <= attackReachSqr) {
                  EntityAnteater.this.getNavigation().moveTo(enemy, 1.0);
                  EntityAnteater.this.setAnimation(
                     EntityAnteater.this.getRandom().nextBoolean() ? EntityAnteater.ANIMATION_SLASH_L : EntityAnteater.ANIMATION_SLASH_R
                  );
               }

               double x = enemy.getX() - EntityAnteater.this.getX();
               double z = enemy.getZ() - EntityAnteater.this.getZ();
               float f = (float)(Mth.atan2(z, x) * 57.2957763671875) - 90.0F;
               EntityAnteater.this.setYRot(f);
               EntityAnteater.this.yBodyRot = f;
               EntityAnteater.this.setStanding(true);
            }
         }
      }

      public void stop() {
         EntityAnteater.this.setStanding(false);
         super.stop();
      }

      protected double getAttackReachSqr(LivingEntity attackTarget) {
         return 2.0F + attackTarget.getBbWidth();
      }
   }

   private class AITargetAnts extends NearestAttackableTargetGoal {
      private static final Predicate<EntityLeafcutterAnt> QUEEN_ANT = entity -> !entity.isQueen();

      public AITargetAnts() {
         super(EntityAnteater.this, EntityLeafcutterAnt.class, 30, true, false, AMCompat.selector(QUEEN_ANT));
      }

      public boolean canUse() {
         return EntityAnteater.this.shouldTargetAnts()
            && !EntityAnteater.this.isBaby()
            && !EntityAnteater.this.hasAntOnTongue()
            && !EntityAnteater.this.isStanding()
            && super.canUse();
      }

      public boolean canContinueToUse() {
         return EntityAnteater.this.shouldTargetAnts()
            && !EntityAnteater.this.hasAntOnTongue()
            && !EntityAnteater.this.isStanding()
            && super.canContinueToUse();
      }
   }
}
