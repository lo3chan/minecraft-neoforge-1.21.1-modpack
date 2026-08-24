package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWadeSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.ai.ShoebillAIFish;
import com.github.alexthe666.alexsmobs.entity.ai.ShoebillAIFlightFlee;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EntityShoebill extends Animal implements IAnimatedEntity, ITargetsDroppedItems {
   public static final Animation ANIMATION_FISH = Animation.create(40);
   public static final Animation ANIMATION_BEAKSHAKE = Animation.create(20);
   public static final Animation ANIMATION_ATTACK = Animation.create(20);
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityShoebill.class, EntityDataSerializers.BOOLEAN);
   public float prevFlyProgress;
   public float flyProgress;
   public int revengeCooldown = 0;
   private int animationTick;
   private Animation currentAnimation;
   private boolean isLandNavigator;
   public int fishingCooldown = 1200 + this.random.nextInt(1200);
   public int lureLevel = 0;
   public int luckLevel = 0;
   public static final Predicate<LivingEntity> TARGET_BABY = animal -> animal.isBaby();

   protected EntityShoebill(EntityType type, Level world) {
      super(type, world);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
      this.switchNavigator(false);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.shoebillSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 10.0)
         .add(Attributes.ATTACK_DAMAGE, 4.0)
         .add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.SHOEBILL_HURT.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.SHOEBILL_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.SHOEBILL_HURT.get();
   }

   public boolean isFood(ItemStack stack) {
      return false;
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean prev = super.hurt(source, amount);
      if (prev && source.getEntity() != null && !(source.getEntity() instanceof AbstractFish)) {
         double range = 15.0;
         int fleeTime = 100 + this.getRandom().nextInt(150);
         this.revengeCooldown = fleeTime;

         for (EntityShoebill gaz : this.level().getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(range, range / 2.0, range))) {
            gaz.revengeCooldown = fleeTime;
         }
      }

      return prev;
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigatorWide(this, this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new FlightMoveController(this, 0.7F, false);
         this.navigation = new DirectPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FLYING, false);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new AnimalAIWadeSwimming(this));
      this.goalSelector.addGoal(1, new ShoebillAIFish(this));
      this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, true));
      this.goalSelector.addGoal(4, new ShoebillAIFlightFlee(this));
      this.goalSelector.addGoal(5, new TemptGoal(this, 1.1, AMCompat.ingredientOf(AMTagRegistry.SHOEBILL_FOODSTUFFS), false));
      this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1.0, 1400));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.targetSelector.addGoal(1, new EntityAINearestTarget3D(this, AbstractFish.class, 30, false, true, null));
      this.targetSelector.addGoal(2, new CreatureAITargetItems(this, false, 10));
      this.targetSelector.addGoal(3, new HurtByTargetGoal(this, new Class[]{Player.class}).setAlertOthers(new Class[0]));
      this.targetSelector
         .addGoal(4, new NearestAttackableTargetGoal(this, EntityAlligatorSnappingTurtle.class, 40, false, false, AMCompat.selector(TARGET_BABY)));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Turtle.class, 40, false, false, AMCompat.selector(TARGET_BABY)));
      this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, EntityCrocodile.class, 40, false, false, AMCompat.selector(TARGET_BABY)));
      this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, EntityCaiman.class, 40, false, false, AMCompat.selector(TARGET_BABY)));
      this.targetSelector.addGoal(8, new EntityAINearestTarget3D(this, EntityTerrapin.class, 100, false, true, null));
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).getType() != Type.MISS;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public void tick() {
      super.tick();
      if (this.isInWater()) {
         AMCompat.setMaxUpStep(this, 1.2F);
      } else {
         AMCompat.setMaxUpStep(this, 0.6F);
      }

      this.prevFlyProgress = this.flyProgress;
      boolean flying = this.isFlying();
      if (flying) {
         if (this.flyProgress < 5.0F) {
            this.flyProgress++;
         }
      } else if (this.flyProgress > 0.0F) {
         this.flyProgress--;
      }

      if (this.revengeCooldown > 0) {
         this.revengeCooldown--;
      }

      if (this.revengeCooldown == 0 && this.getLastHurtByMob() != null) {
         this.setLastHurtByMob(null);
      }

      if (!this.level().isClientSide()) {
         if (this.fishingCooldown > 0) {
            this.fishingCooldown--;
         }

         if (this.getAnimation() == NO_ANIMATION && this.getRandom().nextInt(700) == 0) {
            this.setAnimation(ANIMATION_BEAKSHAKE);
         }

         if (flying) {
            if (this.isLandNavigator) {
               this.switchNavigator(false);
            }
         } else if (!this.isLandNavigator) {
            this.switchNavigator(true);
         }

         if (this.revengeCooldown > 0 && !this.isFlying() && (this.onGround() || this.isInWater())) {
            this.setFlying(false);
         }

         if (this.isFlying()) {
            this.setNoGravity(true);
         } else {
            this.setNoGravity(false);
         }
      }

      if (!this.level().isClientSide()
         && this.getTarget() != null
         && this.getAnimation() == ANIMATION_ATTACK
         && this.getAnimationTick() == 9
         && this.hasLineOfSight(this.getTarget())) {
         AMCompat.knockback(this.getTarget(), 0.30000001192092896, this.getTarget().getX() - this.getX(), this.getTarget().getZ() - this.getZ());
         this.getTarget().hurt(this.damageSources().mobAttack(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Flying", this.isFlying());
      compound.putInt("FishingTimer", this.fishingCooldown);
      compound.putInt("FishingLuck", this.luckLevel);
      compound.putInt("FishingLure", this.lureLevel);
      compound.putInt("RevengeCooldownTimer", this.revengeCooldown);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFlying(AMCompat.getBoolean(compound, "Flying"));
      this.fishingCooldown = AMCompat.getInt(compound, "FishingTimer");
      this.luckLevel = AMCompat.getInt(compound, "FishingLuck");
      this.lureLevel = AMCompat.getInt(compound, "FishingLure");
      this.revengeCooldown = AMCompat.getInt(compound, "RevengeCooldownTimer");
   }

   protected float getWaterSlowDown() {
      return 0.98F;
   }

   public boolean doHurtTarget(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_ATTACK);
      }

      return true;
   }

   @Override
   public boolean isFlying() {
      return (Boolean)this.entityData.get(FLYING);
   }

   @Override
   public void setFlying(boolean flying) {
      this.entityData.set(FLYING, flying);
   }

   @Override
   public int getAnimationTick() {
      return this.animationTick;
   }

   @Override
   public void setAnimationTick(int i) {
      this.animationTick = i;
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
      return new Animation[]{ANIMATION_FISH, ANIMATION_BEAKSHAKE, ANIMATION_ATTACK};
   }

   public InteractionResult mobInteract(Player p_230254_1_, InteractionHand p_230254_2_) {
      ItemStack lvt_3_1_ = p_230254_1_.getItemInHand(p_230254_2_);
      if (lvt_3_1_.is(AMTagRegistry.SHOEBILL_LUCK_FOODS) && this.isAlive()) {
         if (this.luckLevel >= 10) {
            if (this.getAnimation() == NO_ANIMATION) {
               this.setAnimation(ANIMATION_BEAKSHAKE);
            }

            return InteractionResult.SUCCESS;
         } else {
            this.luckLevel = Mth.clamp(this.luckLevel + 1, 0, 10);

            for (int i = 0; i < 6 + this.random.nextInt(3); i++) {
               double d2 = this.random.nextGaussian() * 0.02;
               double d0 = this.random.nextGaussian() * 0.02;
               double d1 = this.random.nextGaussian() * 0.02;
               this.level()
                  .addParticle(
                     new ItemParticleOption(ParticleTypes.ITEM, lvt_3_1_),
                     this.getX() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
                     this.getY() + this.getBbHeight() * 0.5F + this.random.nextFloat() * this.getBbHeight() * 0.5F,
                     this.getZ() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
                     d0,
                     d1,
                     d2
                  );
            }

            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
            lvt_3_1_.shrink(1);
            return AMCompat.sidedSuccess(this.level().isClientSide());
         }
      } else if (!lvt_3_1_.is(AMTagRegistry.SHOEBILL_LURE_FOODS) || !this.isAlive()) {
         return super.mobInteract(p_230254_1_, p_230254_2_);
      } else if (this.lureLevel >= 10) {
         if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_BEAKSHAKE);
         }

         return InteractionResult.SUCCESS;
      } else {
         this.lureLevel = Mth.clamp(this.lureLevel + 1, 0, 10);
         this.fishingCooldown = Mth.clamp(this.fishingCooldown - 200, 200, 2400);

         for (int i = 0; i < 6 + this.random.nextInt(3); i++) {
            double d2 = this.random.nextGaussian() * 0.02;
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            this.level()
               .addParticle(
                  new ItemParticleOption(ParticleTypes.ITEM, lvt_3_1_),
                  this.getX() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
                  this.getY() + this.getBbHeight() * 0.5F + this.random.nextFloat() * this.getBbHeight() * 0.5F,
                  this.getZ() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
                  d0,
                  d1,
                  d2
               );
         }

         lvt_3_1_.shrink(1);
         this.gameEvent(GameEvent.EAT);
         this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
         return AMCompat.sidedSuccess(this.level().isClientSide());
      }
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return AMCompat.create(AMEntityRegistry.SHOEBILL.get(), serverWorld);
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return stack.is(AMTagRegistry.SHOEBILL_FOODSTUFFS)
         || stack.is(AMTagRegistry.SHOEBILL_LUCK_FOODS) && this.luckLevel < 10
         || stack.is(AMTagRegistry.SHOEBILL_LURE_FOODS) && this.lureLevel < 10;
   }

   public void resetFishingCooldown() {
      this.fishingCooldown = Math.max(1200 + this.random.nextInt(1200) - this.lureLevel * 120, 200);
   }

   @Override
   public void onGetItem(ItemEntity e) {
      this.gameEvent(GameEvent.EAT);
      this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
      if (e.getItem().is(AMTagRegistry.SHOEBILL_LUCK_FOODS)) {
         this.luckLevel = Mth.clamp(this.luckLevel + 1, 0, 10);
      } else if (e.getItem().is(AMTagRegistry.SHOEBILL_LURE_FOODS)) {
         this.lureLevel = Mth.clamp(this.lureLevel + 1, 0, 10);
      }

      this.heal(5.0F);
   }
}
