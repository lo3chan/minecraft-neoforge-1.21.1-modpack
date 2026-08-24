package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHerdPanic;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class EntityGazelle extends Animal implements IAnimatedEntity, IHerdPanic {
   private int animationTick;
   private Animation currentAnimation;
   public static final Animation ANIMATION_FLICK_EARS = Animation.create(20);
   public static final Animation ANIMATION_FLICK_TAIL = Animation.create(14);
   public static final Animation ANIMATION_EAT_GRASS = Animation.create(30);
   private boolean hasSpedUp = false;
   private int revengeCooldown = 0;
   private static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(EntityGazelle.class, EntityDataSerializers.BOOLEAN);

   protected EntityGazelle(EntityType type, Level worldIn) {
      super(type, worldIn);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new AnimalAIHerdPanic(this, 1.1));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.1, AMCompat.ingredientOf(AMTagRegistry.GAZELLE_BREEDABLES), false));
      this.goalSelector.addGoal(5, new AnimalAIWanderRanged(this, 100, 1.0, 25, 7));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 15.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.GAZELLE_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.GAZELLE_HURT.get();
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.gazelleSpawnRolls, this.getRandom(), spawnReasonIn) && super.checkSpawnRules(worldIn, spawnReasonIn);
   }

   public int getMaxSpawnClusterSize() {
      return 8;
   }

   public boolean isMaxGroupSizeReached(int sizeIn) {
      return false;
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean prev = super.hurt(source, amount);
      if (prev) {
         double range = 15.0;
         int fleeTime = 100 + this.getRandom().nextInt(150);
         this.revengeCooldown = fleeTime;

         for (EntityGazelle gaz : this.level().getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(range, range / 2.0, range))) {
            gaz.revengeCooldown = fleeTime;
         }
      }

      return prev;
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(RUNNING, false);
   }

   public boolean isRunning() {
      return (Boolean)this.entityData.get(RUNNING);
   }

   public void setRunning(boolean running) {
      this.entityData.set(RUNNING, running);
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.GAZELLE_BREEDABLES);
   }

   @Override
   public int getAnimationTick() {
      return this.animationTick;
   }

   @Override
   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.getAnimation() == NO_ANIMATION
            && this.getRandom().nextInt(70) == 0
            && (this.getLastHurtByMob() == null || this.distanceTo(this.getLastHurtByMob()) > 30.0F)) {
            if (this.level().getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK) && this.getRandom().nextInt(3) == 0) {
               this.setAnimation(ANIMATION_EAT_GRASS);
            } else {
               this.setAnimation(this.getRandom().nextBoolean() ? ANIMATION_FLICK_EARS : ANIMATION_FLICK_TAIL);
            }
         }

         if (this.revengeCooldown >= 0) {
            this.revengeCooldown--;
         }

         if (this.revengeCooldown == 0 && this.getLastHurtByMob() != null) {
            this.setLastHurtByMob(null);
         }

         this.setRunning(this.revengeCooldown > 0);
         if (this.isRunning() && !this.hasSpedUp) {
            this.hasSpedUp = true;
            this.setSprinting(true);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.4749999940395355);
         }

         if (!this.isRunning() && this.hasSpedUp) {
            this.hasSpedUp = false;
            this.setSprinting(false);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
         }
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("GazelleRunning", this.isRunning());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setRunning(AMCompat.getBoolean(compound, "GazelleRunning"));
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
      return new Animation[]{ANIMATION_FLICK_EARS, ANIMATION_FLICK_TAIL, ANIMATION_EAT_GRASS};
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0).add(Attributes.ATTACK_DAMAGE, 2.0).add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      return AMCompat.create(AMEntityRegistry.GAZELLE.get(), p_241840_1_);
   }

   @Override
   public void onPanic() {
   }

   @Override
   public boolean canPanic() {
      return true;
   }
}
