package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIRideParent;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.GorillaAIChargeLooker;
import com.github.alexthe666.alexsmobs.entity.ai.GorillaAIFollowCaravan;
import com.github.alexthe666.alexsmobs.entity.ai.GorillaAIForageLeaves;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAITempt;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class EntityGorilla extends TamableAnimal implements IAnimatedEntity, ITargetsDroppedItems {
   public static final Animation ANIMATION_BREAKBLOCK_R = Animation.create(20);
   public static final Animation ANIMATION_BREAKBLOCK_L = Animation.create(20);
   public static final Animation ANIMATION_POUNDCHEST = Animation.create(40);
   public static final Animation ANIMATION_ATTACK = Animation.create(20);
   protected static final EntityDimensions SILVERBACK_SIZE = EntityDimensions.scalable(1.35F, 1.95F);
   private static final EntityDataAccessor<Boolean> SILVERBACK = SynchedEntityData.defineId(EntityGorilla.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.defineId(EntityGorilla.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityGorilla.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> EATING = SynchedEntityData.defineId(EntityGorilla.class, EntityDataSerializers.BOOLEAN);
   public int maxStandTime = 75;
   public float prevStandProgress;
   public float prevSitProgress;
   public float standProgress;
   public float sitProgress;
   public boolean forcedSit = false;
   private int animationTick;
   private Animation currentAnimation;
   private int standingTime = 0;
   private int eatingTime;
   @Nullable
   private EntityGorilla caravanHead;
   @Nullable
   private EntityGorilla caravanTail;
   private int sittingTime = 0;
   private int maxSitTime = 75;
   @Nullable
   private UUID bananaThrowerID = null;
   private boolean hasSilverbackAttributes = false;
   public int poundChestCooldown = 0;

   protected EntityGorilla(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.setPathfindingMalus(PathType.LEAVES, 0.0F);
      AMCompat.setMaxUpStep(this, 1.1F);
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new AdvancedPathNavigateNoTeleport(this, worldIn, false);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 30.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.ARMOR, 0.0)
         .add(Attributes.ATTACK_DAMAGE, 7.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
         .add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   public static boolean isTameableFood(ItemStack stack) {
      return stack.is(AMTagRegistry.BANANAS);
   }

   public static boolean canGorillaSpawn(
      EntityType<EntityGorilla> gorilla, LevelAccessor worldIn, MobSpawnType reason, BlockPos p_223317_3_, RandomSource random
   ) {
      BlockState blockstate = worldIn.getBlockState(p_223317_3_.below());
      return (blockstate.is(AMTagRegistry.GORILLA_SPAWNS) || blockstate.is(Blocks.AIR)) && worldIn.getRawBrightness(p_223317_3_, 0) > 8;
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.gorillaSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public boolean isFood(ItemStack stack) {
      Item item = stack.getItem();
      return this.isTame() && stack.is(AMTagRegistry.GORILLA_BREEDABLES);
   }

   public int getMaxSpawnClusterSize() {
      return 8;
   }

   public boolean isMaxGroupSizeReached(int sizeIn) {
      return false;
   }

   public boolean hurt(DamageSource source, float amount) {
      if (AMCompat.isInvulnerableTo(this, source)) {
         return false;
      } else {
         Entity entity = source.getEntity();
         this.setOrderedToSit(false);
         if (entity != null && this.isTame() && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
            amount = (amount + 1.0F) / 2.0F;
         }

         return super.hurt(source, amount);
      }
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
      this.goalSelector.addGoal(2, new GorillaAIFollowCaravan(this, 0.8));
      this.goalSelector.addGoal(3, new GorillaAIChargeLooker(this, 1.6));
      this.goalSelector.addGoal(4, new TameableAITempt(this, 1.1, AMCompat.ingredientOf(AMTagRegistry.GORILLA_TAMEABLES), false));
      this.goalSelector.addGoal(4, new AnimalAIRideParent(this, 1.25));
      this.goalSelector.addGoal(6, new EntityGorilla.AIWalkIdle(this, 0.8));
      this.goalSelector.addGoal(5, new GorillaAIForageLeaves(this));
      this.goalSelector.addGoal(5, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.GORILLA_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.GORILLA_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.GORILLA_HURT.get();
   }

   public boolean doHurtTarget(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_ATTACK);
      }

      return true;
   }

   public void travel(Vec3 vec3d) {
      if (this.isSitting()) {
         if (this.getNavigation().getPath() != null) {
            this.getNavigation().stop();
         }

         vec3d = Vec3.ZERO;
      }

      super.travel(vec3d);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      if (spawnDataIn instanceof AgeableMobGroupData lvt_6_1_) {
         if (lvt_6_1_.getGroupSize() == 0) {
            this.setSilverback(true);
         }
      } else {
         this.setSilverback(this.getRandom().nextBoolean());
      }

      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   @Nullable
   public EntityGorilla getNearestSilverback(LevelAccessor world, double dist) {
      List<? extends EntityGorilla> list = world.getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(dist, dist / 2.0, dist));
      if (list.isEmpty()) {
         return null;
      } else {
         EntityGorilla gorilla = null;
         double d0 = 1.7976931348623157E308;

         for (EntityGorilla gorrila2 : list) {
            if (gorrila2.isSilverback()) {
               double d1 = this.distanceToSqr(gorrila2);
               if (!(d1 > d0)) {
                  d0 = d1;
                  gorilla = gorrila2;
               }
            }
         }

         return gorilla;
      }
   }

   public EntityDimensions getDefaultDimensions(Pose poseIn) {
      return this.isSilverback() && !this.isBaby() ? SILVERBACK_SIZE.scale(this.getScale()) : super.getDefaultDimensions(poseIn);
   }

   public void positionRider(Entity passenger, MoveFunction moveFunc) {
      if (this.hasPassenger(passenger)) {
         this.setOrderedToSit(false);
         if (passenger instanceof EntityGorilla babyGorilla) {
            babyGorilla.setStanding(this.isStanding());
            babyGorilla.setOrderedToSit(this.isSitting());
            babyGorilla.yBodyRot = this.yBodyRot;
         }

         float sitAdd = -0.03F * this.sitProgress;
         float standAdd = -0.03F * this.standProgress;
         float radius = standAdd + sitAdd;
         float angle = 0.017453292F * this.yBodyRot;
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         passenger.setPos(
            this.getX() + extraX, this.getY() + this.getPassengersRidingOffset() + AMPlatform.myRidingOffset(passenger, this), this.getZ() + extraZ
         );
      }
   }

   public double getPassengersRidingOffset() {
      return this.getBbHeight() * 0.6499999761581421 * this.getGorillaScale() * (this.isSilverback() ? 0.75F : 1.0F);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SILVERBACK, false);
      builder.define(STANDING, false);
      builder.define(SITTING, false);
      builder.define(EATING, false);
   }

   public boolean isSilverback() {
      return (Boolean)this.entityData.get(SILVERBACK);
   }

   public void setSilverback(boolean silver) {
      this.entityData.set(SILVERBACK, silver);
   }

   public boolean isStanding() {
      return (Boolean)this.entityData.get(STANDING);
   }

   public void setStanding(boolean standing) {
      this.entityData.set(STANDING, standing);
   }

   public boolean isSitting() {
      return (Boolean)this.entityData.get(SITTING);
   }

   public void setOrderedToSit(boolean sit) {
      this.entityData.set(SITTING, sit);
   }

   public boolean isEating() {
      return (Boolean)this.entityData.get(EATING);
   }

   public void setEating(boolean eating) {
      this.entityData.set(EATING, eating);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Silverback", this.isSilverback());
      compound.putBoolean("Standing", this.isStanding());
      compound.putBoolean("GorillaSitting", this.isSitting());
      compound.putBoolean("ForcedToSit", this.forcedSit);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setSilverback(AMCompat.getBoolean(compound, "Silverback"));
      this.setStanding(AMCompat.getBoolean(compound, "Standing"));
      this.setOrderedToSit(AMCompat.getBoolean(compound, "GorillaSitting"));
      this.forcedSit = AMCompat.getBoolean(compound, "ForcedToSit");
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      Item item = itemstack.getItem();
      if (itemstack.getItem() == Items.NAME_TAG) {
         return super.mobInteract(player, hand);
      } else if (this.isTame() && isTameableFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
         this.heal(5.0F);
         this.usePlayerItem(player, hand, itemstack);
         this.gameEvent(GameEvent.EAT);
         this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
         return InteractionResult.SUCCESS;
      } else {
         InteractionResult type = super.mobInteract(player, hand);
         InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
         if (interactionresult == InteractionResult.SUCCESS
            || type == InteractionResult.SUCCESS
            || !this.isTame()
            || !this.isOwnedBy(player)
            || this.isFood(itemstack)) {
            return type;
         } else if (this.isSitting()) {
            this.forcedSit = false;
            this.setOrderedToSit(false);
            return InteractionResult.SUCCESS;
         } else {
            this.forcedSit = true;
            this.setOrderedToSit(true);
            return InteractionResult.SUCCESS;
         }
      }
   }

   @Override
   public Animation getAnimation() {
      return this.currentAnimation;
   }

   @Override
   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
      if (animation == ANIMATION_POUNDCHEST) {
         this.maxStandTime = 45;
         this.setStanding(true);
      }

      if (animation == ANIMATION_ATTACK) {
         this.maxStandTime = 10;
         this.setStanding(true);
      }
   }

   public void tick() {
      super.tick();
      if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && this.canTargetItem(this.getItemInHand(InteractionHand.MAIN_HAND))) {
         this.setEating(true);
         this.setOrderedToSit(true);
         this.setStanding(false);
      }

      if (this.isEating() && !this.canTargetItem(this.getItemInHand(InteractionHand.MAIN_HAND))) {
         this.setEating(false);
         this.eatingTime = 0;
         if (!this.forcedSit) {
            this.setOrderedToSit(true);
         }
      }

      if (this.isEating()) {
         this.eatingTime++;
         if (!this.getMainHandItem().is(ItemTags.LEAVES)) {
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

         if (this.eatingTime % 5 == 0) {
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.PANDA_EAT, this.getSoundVolume(), this.getVoicePitch());
         }

         if (this.eatingTime > 100) {
            ItemStack stack = this.getItemInHand(InteractionHand.MAIN_HAND);
            if (!stack.isEmpty()) {
               this.heal(4.0F);
               if (isTameableFood(stack) && this.bananaThrowerID != null) {
                  if (this.getRandom().nextFloat() < 0.3F) {
                     AMCompat.setTame(this, true);
                     AMCompat.setOwnerUUID(this, this.bananaThrowerID);
                     Player player = this.level().getPlayerByUUID(this.bananaThrowerID);
                     if (player instanceof ServerPlayer) {
                        CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)player, this);
                     }

                     this.level().broadcastEntityEvent(this, (byte)7);
                  } else {
                     this.level().broadcastEntityEvent(this, (byte)6);
                  }
               }

               if (AMCompat.hasCraftingRemainder(stack)) {
                  AMCompat.spawnAtLocation(this, AMCompat.craftingRemainder(stack));
               }

               stack.shrink(1);
            }

            this.eatingTime = 0;
         }
      }

      this.prevSitProgress = this.sitProgress;
      this.prevStandProgress = this.standProgress;
      if (this.isSitting()) {
         if (this.sitProgress < 10.0F) {
            this.sitProgress++;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (this.isStanding()) {
         if (this.standProgress < 10.0F) {
            this.standProgress++;
         }
      } else if (this.standProgress > 0.0F) {
         this.standProgress--;
      }

      if (this.isPassenger() && this.getVehicle() instanceof EntityGorilla) {
         if (!this.isBaby()) {
            this.removeVehicle();
         } else {
            EntityGorilla mount = (EntityGorilla)this.getVehicle();
            this.setYRot(mount.yBodyRot);
            this.yHeadRot = mount.yBodyRot;
            this.yBodyRot = mount.yBodyRot;
         }
      }

      if (this.isStanding() && ++this.standingTime > this.maxStandTime) {
         this.setStanding(false);
         this.standingTime = 0;
         this.maxStandTime = 75 + this.random.nextInt(50);
      }

      if (!this.forcedSit && this.isSitting() && ++this.sittingTime > this.maxSitTime) {
         this.setOrderedToSit(false);
         this.sittingTime = 0;
         this.maxSitTime = 75 + this.random.nextInt(50);
      }

      if (!this.forcedSit && this.isSitting() && (this.getTarget() != null || this.isStanding()) && !this.isEating()) {
         this.setOrderedToSit(false);
      }

      if (!this.level().isClientSide() && this.getAnimation() == NO_ANIMATION && !this.isStanding() && !this.isSitting() && this.random.nextInt(1500) == 0) {
         this.maxSitTime = 300 + this.random.nextInt(250);
         this.setOrderedToSit(true);
      }

      if (this.forcedSit && !this.isVehicle() && this.isTame()) {
         this.setOrderedToSit(true);
      }

      if (this.sitProgress == 0.0F
         && this.poundChestCooldown <= 0
         && this.isSilverback()
         && this.random.nextInt(800) == 0
         && this.getAnimation() == NO_ANIMATION
         && !this.isSitting()
         && !this.isNoAi()
         && this.getMainHandItem().isEmpty()) {
         this.setAnimation(ANIMATION_POUNDCHEST);
      }

      if (!this.level().isClientSide() && this.getTarget() != null && this.getAnimation() == ANIMATION_ATTACK && this.getAnimationTick() == 10) {
         float f1 = this.getYRot() * 0.017453292F;
         this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f1) * 0.02F, 0.0, Mth.cos(f1) * 0.02F));
         AMCompat.knockback(this.getTarget(), 1.0, this.getTarget().getX() - this.getX(), this.getTarget().getZ() - this.getZ());
         this.getTarget().hurt(this.damageSources().mobAttack(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
      }

      if (!this.hasSilverbackAttributes && this.isSilverback() && !this.isBaby()) {
         this.hasSilverbackAttributes = true;
         this.refreshDimensions();
         this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(50.0);
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(10.0);
         this.heal(50.0F);
      }

      if (this.hasSilverbackAttributes && !this.isSilverback() && !this.isBaby()) {
         this.hasSilverbackAttributes = false;
         this.refreshDimensions();
         this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0);
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8.0);
         this.heal(30.0F);
      }

      if (this.poundChestCooldown > 0) {
         this.poundChestCooldown--;
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      return null;
   }

   public PathNavigation getNavigation() {
      return this.navigation;
   }

   @Nullable
   public Entity getControlledVehicle() {
      return this.getVehicle() instanceof EntityGorilla ? null : super.getControlledVehicle();
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
   public boolean canTargetItem(ItemStack stack) {
      return stack.is(AMTagRegistry.GORILLA_FOODSTUFFS);
   }

   @Override
   public void onGetItem(ItemEntity targetEntity) {
      ItemStack duplicate = targetEntity.getItem().copy();
      duplicate.setCount(1);
      if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level().isClientSide()) {
         AMCompat.spawnAtLocation(this, this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
      }

      this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
      Entity thrower = targetEntity.getOwner();
      if (isTameableFood(targetEntity.getItem()) && thrower != null && !this.isTame()) {
         this.bananaThrowerID = thrower.getUUID();
      }
   }

   @Override
   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_BREAKBLOCK_R, ANIMATION_BREAKBLOCK_L, ANIMATION_POUNDCHEST, ANIMATION_ATTACK};
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      return AMCompat.create(AMEntityRegistry.GORILLA.get(), p_241840_1_);
   }

   public void leaveCaravan() {
      if (this.caravanHead != null) {
         this.caravanHead.caravanTail = null;
      }

      this.caravanHead = null;
   }

   public void joinCaravan(EntityGorilla caravanHeadIn) {
      this.caravanHead = caravanHeadIn;
      this.caravanHead.caravanTail = this;
   }

   public boolean hasCaravanTrail() {
      return this.caravanTail != null;
   }

   public boolean inCaravan() {
      return this.caravanHead != null;
   }

   @Nullable
   public EntityGorilla getCaravanHead() {
      return this.caravanHead;
   }

   public float getGorillaScale() {
      return this.isBaby() ? 0.5F : (this.isSilverback() ? 1.3F : 1.0F);
   }

   public boolean isDonkeyKong() {
      String s = ChatFormatting.stripFormatting(this.getName().getString());
      return s != null && (s.toLowerCase().contains("donkey") && s.toLowerCase().contains("kong") || s.equalsIgnoreCase("dk"));
   }

   public boolean isFunkyKong() {
      String s = ChatFormatting.stripFormatting(this.getName().getString());
      return s != null && s.toLowerCase().contains("funky") && s.toLowerCase().contains("kong");
   }

   private class AIWalkIdle extends RandomStrollGoal {
      public AIWalkIdle(EntityGorilla entityGorilla, double v) {
         super(entityGorilla, v);
      }

      public boolean canUse() {
         this.interval = EntityGorilla.this.isSilverback() ? 10 : 120;
         return super.canUse();
      }

      @Nullable
      protected Vec3 getPosition() {
         return LandRandomPos.getPos(this.mob, EntityGorilla.this.isSilverback() ? 25 : 10, 7);
      }
   }
}
