package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIMeleeNearby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIPanicBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIRide;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import javax.annotation.Nullable;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class EntityTusklin extends Animal implements IAnimatedEntity {
   public static final Animation ANIMATION_RUT = Animation.create(26);
   public static final Animation ANIMATION_GORE_L = Animation.create(25);
   public static final Animation ANIMATION_GORE_R = Animation.create(25);
   public static final Animation ANIMATION_FLING = Animation.create(15);
   public static final Animation ANIMATION_BUCK = Animation.create(15);
   private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(EntityTusklin.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> PASSIVETICKS = SynchedEntityData.defineId(EntityTusklin.class, EntityDataSerializers.INT);
   private int animationTick;
   private Animation currentAnimation;
   private int ridingTime = 0;
   private int entityToLaunchId = -1;
   private int conversionTime = 0;

   protected EntityTusklin(EntityType<? extends Animal> type, Level level) {
      super(type, level);
      AMCompat.setMaxUpStep(this, 1.1F);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.tusklinSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static boolean canTusklinSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
      return worldIn.getRawBrightness(pos, 0) > 8
         && (worldIn.getBlockState(pos.below()).isSolid() || worldIn.getBlockState(pos.below()).is(AMTagRegistry.TUSKLIN_SPAWNS));
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 40.0)
         .add(Attributes.ATTACK_DAMAGE, 9.0)
         .add(Attributes.MOVEMENT_SPEED, 0.30000001192092896)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.8999999761581421);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.TUSKLIN_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.TUSKLIN_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.TUSKLIN_HURT.get();
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new AdvancedPathNavigateNoTeleport(this, worldIn, true);
   }

   public boolean isInNether() {
      return this.level().dimension() == Level.NETHER && !this.isNoAi();
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new AnimalAIMeleeNearby(this, 15, 1.25));
      this.goalSelector.addGoal(3, new TameableAIRide(this, 2.0, false) {
         @Override
         public boolean shouldMoveForward() {
            return true;
         }

         @Override
         public boolean shouldMoveBackwards() {
            return false;
         }
      });
      this.goalSelector.addGoal(4, new AnimalAIPanicBaby(this, 1.25));
      this.goalSelector.addGoal(5, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(7, new AnimalAIWanderRanged(this, 120, 0.6000000238418579, 14, 7));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 15.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new AnimalAIHurtByTargetNotBaby(this).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, 100, true, false, AMCompat.selector(this::isAngryAt)));
   }

   public boolean isAngryAt(LivingEntity p_21675_) {
      return this.canAttack(p_21675_);
   }

   protected Vec3 getRiddenInput(Player player, Vec3 deltaIn) {
      return new Vec3(0.0, 0.0, 1.0);
   }

   protected void tickRidden(Player player, Vec3 vec3) {
      super.tickRidden(player, vec3);
      this.setRot(player.getYRot(), player.getXRot() * 0.25F);
      this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
      AMCompat.setMaxUpStep(this, 1.0F);
      this.getNavigation().stop();
      this.setTarget(null);
      this.setSprinting(true);
   }

   protected float getRiddenSpeed(Player rider) {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      if (this.isSaddled()) {
         for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Player player) {
               return player;
            }
         }
      }

      return null;
   }

   public boolean canAttack(LivingEntity entity) {
      boolean prev = super.canAttack(entity);
      return !(entity instanceof Player)
            || this.getLastHurtByMob() != null && this.getLastHurtByMob().equals(entity)
            || this.getPassiveTicks() <= 0
               && !this.isMushroom(entity.getItemInHand(InteractionHand.MAIN_HAND))
               && !this.isMushroom(entity.getItemInHand(InteractionHand.OFF_HAND))
         ? prev
         : false;
   }

   public boolean doHurtTarget(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION) {
         int anim = this.random.nextInt(3);
         switch (anim) {
            case 0:
               this.setAnimation(ANIMATION_FLING);
               break;
            case 1:
               this.setAnimation(ANIMATION_GORE_L);
               break;
            case 2:
               this.setAnimation(ANIMATION_GORE_R);
         }
      }

      return true;
   }

   public void positionRider(Entity passenger, MoveFunction moveFunc) {
      if (this.hasPassenger(passenger)) {
         float radius = 0.4F;
         if (this.getAnimation() == ANIMATION_GORE_L || this.getAnimation() == ANIMATION_GORE_R) {
            if (this.getAnimationTick() <= 4) {
               radius -= this.getAnimationTick() * 0.1F;
            } else {
               radius -= -0.4F + Math.min(this.getAnimationTick() - 4, 4) * 0.1F;
            }
         }

         if (this.getAnimation() == ANIMATION_BUCK) {
            if (this.getAnimationTick() < 5) {
               radius -= this.getAnimationTick() * 0.1F;
            } else if (this.getAnimationTick() < 10) {
               radius -= 0.4F - (this.getAnimationTick() - 5) * 0.1F;
            }
         }

         float angle = 0.017453292F * this.yBodyRot;
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         passenger.setPos(
            this.getX() + extraX, this.getY() + this.getPassengersRidingOffset() + AMPlatform.myRidingOffset(passenger, this), this.getZ() + extraZ
         );
      }
   }

   public double getPassengersRidingOffset() {
      float f = this.walkAnimation.position();
      float f1 = this.walkAnimation.speed();
      float f2 = 0.0F;
      if (this.getAnimation() == ANIMATION_FLING) {
         if (this.getAnimationTick() <= 3.0F) {
            f2 = this.getAnimationTick() * -0.1F;
         } else {
            f2 = -0.3F + Mth.clamp(this.getAnimationTick() - 3, 0, 3) * 0.1F;
         }
      }

      if (this.getAnimation() == ANIMATION_BUCK) {
         if (this.getAnimationTick() < 5) {
            f2 = this.getAnimationTick() * 0.2F * 0.8F;
         } else if (this.getAnimationTick() < 10) {
            f2 = (0.8F - (this.getAnimationTick() - 5) * 0.2F) * 0.8F;
         }
      }

      return this.getBbHeight() - 0.3 + (float)Math.abs(Math.sin(f * 0.7F) * f1 * 0.0625 * 1.600000023841858) + f2;
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      Item item = itemstack.getItem();
      if (item == Items.SADDLE && !this.isSaddled() && !this.isBaby()) {
         if (!player.isCreative()) {
            itemstack.shrink(1);
         }

         this.setSaddled(true);
         return InteractionResult.SUCCESS;
      } else if (item == AMItemRegistry.PIGSHOES.get() && this.getShoeStack().isEmpty() && !this.isBaby()) {
         this.setShoeStack(itemstack.copy());
         if (!player.isCreative()) {
            itemstack.shrink(1);
         }

         return InteractionResult.SUCCESS;
      } else if (!this.isMushroom(itemstack) || this.getPassiveTicks() > 0 && !(this.getHealth() < this.getMaxHealth())) {
         InteractionResult type = super.mobInteract(player, hand);
         if (type != InteractionResult.SUCCESS
            && !this.isFood(itemstack)
            && !player.isShiftKeyDown()
            && !this.isBaby()
            && this.isSaddled()
            && this.getAnimation() != ANIMATION_BUCK) {
            player.startRiding(this);
            return InteractionResult.SUCCESS;
         } else {
            return type;
         }
      } else {
         if (!player.isCreative()) {
            itemstack.shrink(1);
         }

         this.heal(6.0F);
         this.setPassiveTicks(this.getPassiveTicks() + 1200);
         return InteractionResult.SUCCESS;
      }
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.TUSKLIN_BREEDABLES);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SADDLED, false);
      builder.define(PASSIVETICKS, 0);
   }

   public void addAdditionalSaveData(CompoundTag p_31808_) {
      super.addAdditionalSaveData(p_31808_);
      if (!this.getShoeStack().isEmpty()) {
         AMCompat.put(p_31808_, "ShoeItem", AMCompat.saveItem(this.level().registryAccess(), this.getShoeStack()));
      }

      p_31808_.putInt("PassiveTicks", this.getPassiveTicks());
      p_31808_.putBoolean("Saddle", this.isSaddled());
   }

   public void readAdditionalSaveData(CompoundTag p_31795_) {
      super.readAdditionalSaveData(p_31795_);
      this.setSaddled(AMCompat.getBoolean(p_31795_, "Saddle"));
      this.setPassiveTicks(AMCompat.getInt(p_31795_, "PassiveTicks"));
      CompoundTag compoundtag = AMCompat.getCompound(p_31795_, "ShoeItem");
      if (compoundtag != null && !compoundtag.isEmpty()) {
         ItemStack itemstack = AMCompat.loadItem(this.level().registryAccess(), compoundtag);
         if (itemstack.isEmpty()) {
            AlexsMobs.LOGGER.warn("Unable to load item from: {}", compoundtag);
         }

         this.setShoeStack(itemstack);
      }
   }

   public boolean isMushroom(ItemStack stack) {
      return stack.is(AMTagRegistry.TUSKLIN_FOODSTUFFS);
   }

   public int getPassiveTicks() {
      return (Integer)this.entityData.get(PASSIVETICKS);
   }

   private void setPassiveTicks(int passiveTicks) {
      this.entityData.set(PASSIVETICKS, passiveTicks);
   }

   public boolean isSaddled() {
      return (Boolean)this.entityData.get(SADDLED);
   }

   public void setSaddled(boolean saddled) {
      this.entityData.set(SADDLED, saddled);
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (this.isSaddled() && !this.level().isClientSide()) {
         AMCompat.spawnAtLocation(this, Items.SADDLE);
      }

      if (!this.getShoeStack().isEmpty() && !this.level().isClientSide()) {
         AMCompat.spawnAtLocation(this, this.getShoeStack().copy());
      }

      this.setSaddled(false);
      this.setShoeStack(ItemStack.EMPTY);
   }

   public ItemStack getShoeStack() {
      return this.getItemBySlot(EquipmentSlot.FEET);
   }

   public void setShoeStack(ItemStack shoe) {
      this.setItemSlot(EquipmentSlot.FEET, shoe);
   }

   public void tick() {
      super.tick();
      if (this.isInNether()) {
         this.conversionTime++;
         if (this.conversionTime > 300 && !this.level().isClientSide()) {
            Hoglin hoglin = (Hoglin)this.convertTo(EntityType.HOGLIN, false);
            if (hoglin != null) {
               hoglin.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
               this.dropEquipment();
               this.level().addFreshEntity(hoglin);
               this.remove(RemovalReason.DISCARDED);
            }
         }
      }

      if (this.entityToLaunchId != -1 && this.isAlive()) {
         Entity launch = this.level().getEntity(this.entityToLaunchId);
         this.ejectPassengers();
         this.entityToLaunchId = -1;
         if (launch != null && !launch.isPassenger() && launch instanceof LivingEntity) {
            launch.setPos(this.getEyePosition().add(0.0, 1.0, 0.0));
            float rot = 180.0F + this.getYRot();
            float strength = (float)(this.getLaunchStrength() * (1.0 - ((LivingEntity)launch).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
            float x = Mth.sin(rot * 0.017453292F);
            float z = -Mth.cos(rot * 0.017453292F);
            if (!(strength <= 0.0)) {
               launch.hasImpulse = true;
               Vec3 vec3 = this.getDeltaMovement();
               Vec3 vec31 = vec3.add(new Vec3(x, 0.0, z).normalize().scale(strength));
               launch.setDeltaMovement(vec31.x, strength, vec31.z);
            }
         }
      }

      if (this.getAnimation() == ANIMATION_BUCK && this.getAnimationTick() >= 5) {
         Entity passenger = this.getControllingPassenger();
         if (passenger instanceof LivingEntity) {
            this.entityToLaunchId = passenger.getId();
         }
      }

      if (!this.level().isClientSide()) {
         if (this.isVehicle()) {
            this.ridingTime++;
            if (this.ridingTime >= this.getMaxRidingTime() && this.getAnimation() != ANIMATION_BUCK) {
               this.setAnimation(ANIMATION_BUCK);
            }
         } else {
            this.ridingTime = 0;
         }

         if (this.isAlive() && this.ridingTime > 0 && this.getDeltaMovement().horizontalDistanceSqr() > 0.1) {
            for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0))) {
               if (!(entity instanceof EntityTusklin) && !entity.isPassengerOfSameVehicle(this)) {
                  entity.hurt(this.damageSources().mobAttack(this), 4.0F + this.random.nextFloat() * 3.0F);
                  if (entity.onGround()) {
                     double d0 = entity.getX() - this.getX();
                     double d1 = entity.getZ() - this.getZ();
                     double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
                     float f = 0.5F;
                     entity.push(d0 / d2 * f, f, d1 / d2 * f);
                  }
               }
            }

            AMCompat.setMaxUpStep(this, 2.0F);
         } else {
            AMCompat.setMaxUpStep(this, 1.1F);
         }

         if (this.getTarget() != null
            && this.hasLineOfSight(this.getTarget())
            && this.distanceTo(this.getTarget()) < this.getTarget().getBbWidth() + this.getBbWidth() + 1.8F) {
            if (this.getAnimation() == ANIMATION_FLING && this.getAnimationTick() == 6) {
               this.getTarget().hurt(this.damageSources().mobAttack(this), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
               this.knockbackTarget(this.getTarget(), 0.9F, 0.0F);
            }

            if (this.getAnimation() == ANIMATION_GORE_L && this.getAnimationTick() == 6) {
               this.getTarget().hurt(this.damageSources().mobAttack(this), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
               this.knockbackTarget(this.getTarget(), 0.5F, -90.0F);
            }

            if (this.getAnimation() == ANIMATION_GORE_R && this.getAnimationTick() == 6) {
               this.getTarget().hurt(this.damageSources().mobAttack(this), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
               this.knockbackTarget(this.getTarget(), 0.5F, 90.0F);
            }
         }
      }

      if (this.getAnimation() == ANIMATION_RUT
         && this.getAnimationTick() == 23
         && this.level().getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK)
         && this.getRandom().nextInt(3) == 0) {
         if (this.isBaby() && this.level().getBlockState(this.blockPosition()).canBeReplaced() && this.random.nextInt(3) == 0) {
            this.level().setBlockAndUpdate(this.blockPosition(), Blocks.BROWN_MUSHROOM.defaultBlockState());
            this.gameEvent(GameEvent.BLOCK_DESTROY);
            this.playSound(SoundEvents.CROP_PLANTED, this.getSoundVolume(), this.getVoicePitch());
         }

         this.level().levelEvent(2001, this.blockPosition().below(), Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
         this.level().setBlock(this.blockPosition().below(), Blocks.DIRT.defaultBlockState(), 2);
         this.heal(5.0F);
      }

      if (!this.level().isClientSide()
         && this.getAnimation() == NO_ANIMATION
         && this.getRandom().nextInt(this.isBaby() ? 140 : 70) == 0
         && (this.getLastHurtByMob() == null || this.distanceTo(this.getLastHurtByMob()) > 30.0F)
         && this.level().getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK)
         && this.getRandom().nextInt(3) == 0) {
         this.setAnimation(ANIMATION_RUT);
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   private float getLaunchStrength() {
      return this.getShoeStack().is(AMItemRegistry.PIGSHOES.get()) ? 0.4F : 0.9F;
   }

   private int getMaxRidingTime() {
      return this.getShoeStack().is(AMItemRegistry.PIGSHOES.get()) ? 160 : 60;
   }

   private void knockbackTarget(LivingEntity entity, float strength, float angle) {
      float rot = this.getYRot() + angle;
      if (entity != null) {
         AMCompat.knockback(entity, strength, Mth.sin(rot * 0.017453292F), -Mth.cos(rot * 0.017453292F));
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

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      if (spawnDataIn == null) {
         spawnDataIn = new AgeableMobGroupData(0.34F);
      }

      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   @Override
   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_RUT, ANIMATION_GORE_L, ANIMATION_GORE_R, ANIMATION_FLING, ANIMATION_BUCK};
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
      return AMCompat.create(AMEntityRegistry.TUSKLIN.get(), this.level());
   }
}
