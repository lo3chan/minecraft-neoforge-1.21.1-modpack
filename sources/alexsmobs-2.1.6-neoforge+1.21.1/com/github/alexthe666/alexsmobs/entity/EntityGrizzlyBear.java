package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.GrizzlyBearAIAprilFools;
import com.github.alexthe666.alexsmobs.entity.ai.GrizzlyBearAIBeehive;
import com.github.alexthe666.alexsmobs.entity.ai.GrizzlyBearAIFleeBees;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIFollowOwner;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAITempt;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityGrizzlyBear extends TamableAnimal implements NeutralMob, IAnimatedEntity, ITargetsDroppedItems, IFollower {
   public static final Animation ANIMATION_MAUL = Animation.create(20);
   public static final Animation ANIMATION_SNIFF = Animation.create(12);
   public static final Animation ANIMATION_SWIPE_R = Animation.create(15);
   public static final Animation ANIMATION_SWIPE_L = Animation.create(20);
   private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.defineId(EntityGrizzlyBear.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityGrizzlyBear.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> HONEYED = SynchedEntityData.defineId(EntityGrizzlyBear.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> EATING = SynchedEntityData.defineId(EntityGrizzlyBear.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SNOWY = SynchedEntityData.defineId(EntityGrizzlyBear.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> APRIL_FOOLS_MODE = SynchedEntityData.defineId(EntityGrizzlyBear.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityGrizzlyBear.class, EntityDataSerializers.INT);
   private static final UniformInt angerLogic = TimeUtil.rangeOfSeconds(20, 39);
   public float prevStandProgress;
   public float prevSitProgress;
   public float standProgress;
   public float sitProgress;
   public int maxStandTime = 75;
   public boolean forcedSit = false;
   private int animationTick;
   private Animation currentAnimation;
   private int standingTime = 0;
   private int sittingTime = 0;
   private int maxSitTime = 75;
   private int eatingTime = 0;
   private int angerTime;
   private UUID angerTarget;
   private int honeyedTime;
   @Nullable
   private UUID salmonThrowerID = null;
   private static final Supplier<Ingredient> TEMPTATION_ITEMS = AMCompat.lazyIngredient(() -> AMCompat.ingredientOf(AMTagRegistry.GORILLA_FOODSTUFFS));
   public int timeUntilNextFur = this.random.nextInt(24000) + 24000;
   protected static final EntityDimensions STANDING_SIZE = EntityDimensions.scalable(1.7F, 2.75F);
   private boolean recalcSize = false;
   private int snowTimer = 0;
   private boolean permSnow = false;

   protected EntityGrizzlyBear(EntityType type, Level worldIn) {
      super(type, worldIn);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 55.0)
         .add(Attributes.ATTACK_DAMAGE, 8.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.6000000238418579)
         .add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   public EntityDimensions getDefaultDimensions(Pose poseIn) {
      return this.isStanding() ? STANDING_SIZE.scale(this.getScale()) : super.getDefaultDimensions(poseIn);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.grizzlyBearSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public boolean hurt(DamageSource source, float amount) {
      if (AMCompat.isInvulnerableTo(this, source)) {
         return false;
      } else {
         Entity entity = source.getEntity();
         this.setOrderedToSit(false);
         if (entity != null && this.isTame() && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
            amount = (amount + 1.0F) / 3.0F;
         }

         return super.hurt(source, amount);
      }
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.GRIZZLY_BEAR_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.GRIZZLY_BEAR_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.GRIZZLY_BEAR_DIE.get();
   }

   public void positionRider(Entity passenger, MoveFunction moveFunc) {
      if (this.hasPassenger(passenger)) {
         float sitAdd = -0.065F * this.sitProgress;
         float standAdd = -0.07F * this.standProgress;
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
      float f = Math.min(0.25F, this.walkAnimation.speed());
      float f1 = this.walkAnimation.position();
      float sitAdd = 0.01F * this.sitProgress;
      float standAdd = 0.07F * this.standProgress;
      return this.getBbHeight() - 0.3 + 0.12F * Mth.cos(f1 * 0.7F) * 0.7F * f + sitAdd + standAdd;
   }

   public void playAmbientSound() {
      if (!this.isFreddy()) {
         super.playAmbientSound();
      }
   }

   protected float getWaterSlowDown() {
      return this.isVehicle() ? 0.9F : 0.98F;
   }

   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(angerLogic.sample(this.random));
   }

   public int getRemainingPersistentAngerTime() {
      return this.angerTime;
   }

   public void setRemainingPersistentAngerTime(int time) {
      this.angerTime = time;
   }

   public UUID getPersistentAngerTarget() {
      return this.angerTarget;
   }

   public void setPersistentAngerTarget(@Nullable UUID target) {
      this.angerTarget = target;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.getMsgId() != null && source.getMsgId().equals("sting") || source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(2, new TameableAIFollowOwner(this, 1.2, 5.0F, 2.0F, false));
      this.goalSelector.addGoal(3, new GrizzlyBearAIAprilFools(this));
      this.goalSelector.addGoal(4, new EntityGrizzlyBear.MeleeAttackGoal());
      this.goalSelector.addGoal(4, new EntityGrizzlyBear.PanicGoal());
      this.goalSelector.addGoal(5, new TameableAITempt(this, 1.1, TEMPTATION_ITEMS.get(), false));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25));
      this.goalSelector.addGoal(5, new GrizzlyBearAIBeehive(this));
      this.goalSelector.addGoal(6, new GrizzlyBearAIFleeBees(this, 14.0F, 1.0, 1.0));
      this.goalSelector.addGoal(6, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.75));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
      this.targetSelector.addGoal(3, new EntityGrizzlyBear.HurtByTargetGoal());
      this.targetSelector.addGoal(4, new CreatureAITargetItems(this, false));
      this.targetSelector.addGoal(5, new EntityGrizzlyBear.AttackPlayerGoal());
      this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, this::isAngryAt));
      this.targetSelector.addGoal(7, new NonTameRandomTargetGoal(this, Fox.class, false, AMCompat.selector(null)));
      this.targetSelector.addGoal(8, new NonTameRandomTargetGoal(this, Wolf.class, false, AMCompat.selector(null)));
      this.targetSelector.addGoal(7, new ResetUniversalAngerTargetGoal(this, false));
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Honeyed", this.isHoneyed());
      compound.putBoolean("Snowy", this.isSnowy());
      compound.putBoolean("Standing", this.isStanding());
      compound.putBoolean("BearSitting", this.isSitting());
      compound.putBoolean("ForcedToSit", this.forcedSit);
      compound.putBoolean("SnowPerm", this.permSnow);
      compound.putInt("FurTime", this.timeUntilNextFur);
      compound.putInt("BearCommand", this.getCommand());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setHoneyed(AMCompat.getBoolean(compound, "Honeyed"));
      this.setSnowy(AMCompat.getBoolean(compound, "Snowy"));
      this.setStanding(AMCompat.getBoolean(compound, "Standing"));
      this.setOrderedToSit(AMCompat.getBoolean(compound, "BearSitting"));
      this.setCommand(AMCompat.getInt(compound, "BearCommand"));
      this.forcedSit = AMCompat.getBoolean(compound, "ForcedToSit");
      this.permSnow = AMCompat.getBoolean(compound, "SnowPerm");
      this.timeUntilNextFur = AMCompat.getInt(compound, "FurTime");
   }

   public boolean isFood(ItemStack stack) {
      Item item = stack.getItem();
      return this.isTame() && stack.is(AMTagRegistry.GRIZZLY_BREEDABLES);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      if (id == 67) {
         AlexsMobs.PROXY.onEntityStatus(this, id);
      } else if (id == 68) {
         AlexsMobs.PROXY.spawnSpecialParticle(0);
      } else {
         super.handleEntityEvent(id);
      }
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      for (Entity passenger : this.getPassengers()) {
         if (passenger instanceof Player player) {
            return player;
         }
      }

      return null;
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      Item item = itemstack.getItem();
      InteractionResult type = super.mobInteract(player, hand);
      if (item == Items.SNOW && !this.isSnowy() && !this.level().isClientSide()) {
         this.usePlayerItem(player, hand, itemstack);
         this.permSnow = true;
         this.setSnowy(true);
         this.gameEvent(GameEvent.ENTITY_INTERACT);
         this.playSound(SoundEvents.SNOW_PLACE, this.getSoundVolume(), this.getVoicePitch());
         return InteractionResult.SUCCESS;
      } else if (item instanceof ShovelItem && this.isSnowy() && !this.level().isClientSide()) {
         this.permSnow = false;
         if (!player.isCreative()) {
            AMCompat.hurtItem(itemstack, 1, this.getRandom(), player instanceof ServerPlayer ? (ServerPlayer)player : null);
         }

         this.setSnowy(false);
         this.gameEvent(GameEvent.ENTITY_INTERACT);
         this.playSound(SoundEvents.SNOW_BREAK, this.getSoundVolume(), this.getVoicePitch());
         return InteractionResult.SUCCESS;
      } else {
         InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
         if (interactionresult == InteractionResult.SUCCESS
            || type == InteractionResult.SUCCESS
            || !this.isTame()
            || !this.isOwnedBy(player)
            || this.isFood(itemstack)) {
            return type;
         } else if (!player.isShiftKeyDown() && !this.isBaby()) {
            player.startRiding(this);
            return InteractionResult.SUCCESS;
         } else {
            this.setCommand((this.getCommand() + 1) % 3);
            if (this.getCommand() == 3) {
               this.setCommand(0);
            }

            player.displayClientMessage(Component.translatable("entity.alexsmobs.all.command_" + this.getCommand(), new Object[]{this.getName()}), true);
            boolean sit = this.getCommand() == 2;
            if (sit) {
               this.forcedSit = true;
               this.setOrderedToSit(true);
               return InteractionResult.SUCCESS;
            } else {
               this.forcedSit = false;
               this.setOrderedToSit(false);
               return InteractionResult.SUCCESS;
            }
         }
      }
   }

   protected Vec3 getRiddenInput(Player player, Vec3 deltaIn) {
      if (player.zza != 0.0F) {
         float f = player.zza < 0.0F ? 0.5F : 1.0F;
         return new Vec3(player.xxa * 0.25F, 0.0, player.zza * 0.5F * f);
      } else {
         this.setSprinting(false);
         return Vec3.ZERO;
      }
   }

   protected void tickRidden(Player player, Vec3 vec3) {
      super.tickRidden(player, vec3);
      if (player.zza != 0.0F || player.xxa != 0.0F) {
         this.setRot(player.getYRot(), player.getXRot() * 0.25F);
         this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
         AMCompat.setMaxUpStep(this, 1.0F);
         this.getNavigation().stop();
         this.setTarget(null);
         this.setSprinting(true);
      }
   }

   protected float getRiddenSpeed(Player rider) {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
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

   public void tick() {
      super.tick();
      if (this.isBaby() || this.getEyeHeight() > this.getBbHeight()) {
         this.refreshDimensions();
      }

      if (!this.isStanding() && this.getBbHeight() >= 2.75F) {
         this.refreshDimensions();
      }

      this.prevStandProgress = this.standProgress;
      this.prevSitProgress = this.sitProgress;
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

      if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && this.canTargetItem(this.getItemInHand(InteractionHand.MAIN_HAND))) {
         this.setEating(true);
         this.setOrderedToSit(true);
         this.setStanding(false);
      }

      if (this.recalcSize) {
         this.recalcSize = false;
         this.refreshDimensions();
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

         if (this.eatingTime % 5 == 0) {
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
         }

         if (this.eatingTime > 100) {
            ItemStack stack = this.getItemInHand(InteractionHand.MAIN_HAND);
            if (!stack.isEmpty()) {
               if (stack.is(AMTagRegistry.GRIZZLY_HONEY)) {
                  this.setHoneyed(true);
                  this.heal(10.0F);
                  this.honeyedTime = 700;
               } else {
                  this.heal(4.0F);
               }

               if (stack.is(AMTagRegistry.GRIZZLY_TAMEABLES) && !this.isTame() && this.salmonThrowerID != null) {
                  if (this.getRandom().nextFloat() < 0.3F) {
                     AMCompat.setTame(this, true);
                     AMCompat.setOwnerUUID(this, this.salmonThrowerID);
                     Player player = this.level().getPlayerByUUID(this.salmonThrowerID);
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

      if (this.isStanding() && ++this.standingTime > this.maxStandTime) {
         this.setStanding(false);
         this.standingTime = 0;
         this.maxStandTime = 75 + this.random.nextInt(50);
      }

      if (this.isSitting() && !this.forcedSit && ++this.sittingTime > this.maxSitTime) {
         this.setOrderedToSit(false);
         this.sittingTime = 0;
         this.maxSitTime = 75 + this.random.nextInt(50);
      }

      if (!this.level().isClientSide() && this.getAnimation() == NO_ANIMATION && !this.isStanding() && !this.isSitting() && this.random.nextInt(1500) == 0) {
         this.maxSitTime = 300 + this.random.nextInt(250);
         this.setOrderedToSit(true);
      }

      if (!this.forcedSit && this.isSitting() && (this.getTarget() != null || this.isStanding()) && !this.isEating()) {
         this.setOrderedToSit(false);
      }

      if (this.getAnimation() == NO_ANIMATION && this.getAprilFoolsFlag() < 1 && this.random.nextInt(this.isStanding() ? 350 : 2500) == 0) {
         this.setAnimation(ANIMATION_SNIFF);
      }

      if (this.isSitting()) {
         this.getNavigation().stop();
      }

      LivingEntity attackTarget = this.getTarget();
      if (this.getControllingPassenger() != null && this.getControllingPassenger() instanceof Player) {
         Player rider = (Player)this.getControllingPassenger();
         if (rider.getLastHurtMob() != null && this.distanceTo(rider.getLastHurtMob()) < this.getBbWidth() + 3.0F && !this.isAlliedTo(rider.getLastHurtMob())) {
            UUID preyUUID = rider.getLastHurtMob().getUUID();
            if (!this.getUUID().equals(preyUUID)) {
               attackTarget = rider.getLastHurtMob();
               if (this.getAnimation() == NO_ANIMATION || this.getAnimation() == ANIMATION_SNIFF) {
                  this.setAnimation(this.random.nextBoolean() ? ANIMATION_MAUL : (this.random.nextBoolean() ? ANIMATION_SWIPE_L : ANIMATION_SWIPE_R));
               }
            }
         }
      }

      if (attackTarget != null) {
         if (!this.level().isClientSide()) {
            this.setSprinting(true);
         }

         if (this.distanceTo(attackTarget) < attackTarget.getBbWidth() + this.getBbWidth() + 2.5F) {
            if (this.getAnimation() == ANIMATION_MAUL && this.getAnimationTick() % 5 == 0 && this.getAnimationTick() > 3) {
               AMCompat.doHurtTarget(this, attackTarget);
            }

            if (this.getAnimation() == ANIMATION_SWIPE_L && this.getAnimationTick() == 7) {
               AMCompat.doHurtTarget(this, attackTarget);
               float rot = this.getYRot() + 90.0F;
               AMCompat.knockback(attackTarget, 0.5, Mth.sin(rot * 0.017453292F), -Mth.cos(rot * 0.017453292F));
            }

            if (this.getAnimation() == ANIMATION_SWIPE_R && this.getAnimationTick() == 7) {
               AMCompat.doHurtTarget(this, attackTarget);
               float rot = this.getYRot() - 90.0F;
               AMCompat.knockback(attackTarget, 0.5, Mth.sin(rot * 0.017453292F), -Mth.cos(rot * 0.017453292F));
            }
         }
      } else if (!this.level().isClientSide() && this.getControllingPassenger() == null) {
         this.setSprinting(false);
      }

      if (!this.level().isClientSide() && this.isHoneyed() && --this.honeyedTime <= 0) {
         this.setHoneyed(false);
         this.honeyedTime = 0;
      }

      if (this.forcedSit && !this.isVehicle() && this.isTame()) {
         this.setOrderedToSit(true);
      }

      if (this.isVehicle() && this.isSitting()) {
         this.setOrderedToSit(false);
      }

      if (!this.level().isClientSide() && this.isAlive() && this.isTame() && !this.isBaby() && --this.timeUntilNextFur <= 0) {
         AMCompat.spawnAtLocation(this, (ItemLike)AMItemRegistry.BEAR_FUR.get());
         this.timeUntilNextFur = this.random.nextInt(24000) + 24000;
      }

      if (this.snowTimer > 0) {
         this.snowTimer--;
      }

      if (this.snowTimer == 0 && !this.level().isClientSide()) {
         this.snowTimer = 200 + this.random.nextInt(400);
         if (this.isSnowy()) {
            if (!this.permSnow
               && (
                  !this.level().isClientSide()
                     || this.getRemainingFireTicks() > 0
                     || this.isInWaterOrBubble()
                     || !isSnowingAt(this.level(), this.blockPosition().above())
               )) {
               this.setSnowy(false);
            }
         } else if (!this.level().isClientSide() && isSnowingAt(this.level(), this.blockPosition())) {
            this.setSnowy(true);
         }
      }

      if (this.isFreddy()) {
         this.setStanding(true);
         this.standingTime = 0;
         this.maxStandTime = 40;
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public static boolean isSnowingAt(Level world, BlockPos position) {
      if (!world.isRaining()) {
         return false;
      } else if (!world.canSeeSky(position)) {
         return false;
      } else {
         return world.getHeightmapPos(Types.MOTION_BLOCKING, position).getY() > position.getY()
            ? false
            : ((Biome)world.getBiome(position).value()).getPrecipitationAt(position) == Precipitation.SNOW;
      }
   }

   public boolean isAlliedTo(Entity entityIn) {
      if (this.isTame()) {
         LivingEntity livingentity = this.getOwner();
         if (entityIn == livingentity) {
            return true;
         }

         if (entityIn instanceof TamableAnimal) {
            return ((TamableAnimal)entityIn).isOwnedBy(livingentity);
         }

         if (livingentity != null) {
            return livingentity.isAlliedTo(entityIn);
         }
      }

      return super.isAlliedTo(entityIn);
   }

   public void setOrderedToSit(boolean sit) {
      this.entityData.set(SITTING, sit);
   }

   public boolean isSitting() {
      return (Boolean)this.entityData.get(SITTING);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(STANDING, false);
      builder.define(SITTING, false);
      builder.define(HONEYED, false);
      builder.define(SNOWY, false);
      builder.define(EATING, false);
      builder.define(APRIL_FOOLS_MODE, 0);
      builder.define(COMMAND, 0);
   }

   public boolean isEating() {
      return (Boolean)this.entityData.get(EATING);
   }

   public void setEating(boolean eating) {
      this.entityData.set(EATING, eating);
   }

   public boolean isHoneyed() {
      return (Boolean)this.entityData.get(HONEYED);
   }

   public void setHoneyed(boolean honeyed) {
      this.entityData.set(HONEYED, honeyed);
   }

   public boolean isSnowy() {
      return (Boolean)this.entityData.get(SNOWY);
   }

   public void setSnowy(boolean honeyed) {
      this.entityData.set(SNOWY, honeyed);
   }

   public boolean isStanding() {
      return (Boolean)this.entityData.get(STANDING);
   }

   public void setStanding(boolean standing) {
      this.entityData.set(STANDING, standing);
      this.recalcSize = true;
   }

   public int getAprilFoolsFlag() {
      return (Integer)this.entityData.get(APRIL_FOOLS_MODE);
   }

   public void setAprilFoolsFlag(int i) {
      this.entityData.set(APRIL_FOOLS_MODE, i);
   }

   public int getCommand() {
      return (Integer)this.entityData.get(COMMAND);
   }

   public void setCommand(int command) {
      this.entityData.set(COMMAND, command);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob p_241840_2_) {
      return AMCompat.create(AMEntityRegistry.GRIZZLY_BEAR.get(), world);
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
      if (animation == ANIMATION_MAUL) {
         this.maxStandTime = 21;
         this.setStanding(true);
      }

      if (animation == ANIMATION_SWIPE_R || animation == ANIMATION_SWIPE_L) {
         this.maxStandTime = 2 + this.random.nextInt(5);
         this.setStanding(true);
      }
   }

   @Override
   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_MAUL, ANIMATION_SNIFF, ANIMATION_SWIPE_R, ANIMATION_SWIPE_L};
   }

   public boolean shouldMove() {
      return !this.isSitting();
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      if (spawnDataIn == null) {
         spawnDataIn = new AgeableMobGroupData(1.0F);
      }

      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return stack.is(AMTagRegistry.GRIZZLY_FOODSTUFFS);
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
      if (targetEntity.getItem().is(AMTagRegistry.GRIZZLY_TAMEABLES) && thrower != null && this.isHoneyed()) {
         this.salmonThrowerID = thrower.getUUID();
      } else {
         this.salmonThrowerID = null;
      }
   }

   public boolean isEatingHeldItem() {
      return false;
   }

   public boolean isFreddy() {
      return this.getAprilFoolsFlag() > 1;
   }

   @Override
   public boolean shouldFollow() {
      return this.getAprilFoolsFlag() == 0 && this.getCommand() == 1;
   }

   class AttackPlayerGoal extends NearestAttackableTargetGoal<Player> {
      public AttackPlayerGoal() {
         super(EntityGrizzlyBear.this, Player.class, 3, true, true, null);
      }

      public boolean canUse() {
         return !EntityGrizzlyBear.this.isBaby() && EntityGrizzlyBear.this.getAprilFoolsFlag() < 1 && !EntityGrizzlyBear.this.isHoneyed()
            ? super.canUse()
            : false;
      }

      protected double getFollowDistance() {
         return 5.0;
      }
   }

   class HurtByTargetGoal extends net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal {
      public HurtByTargetGoal() {
         super(EntityGrizzlyBear.this, new Class[0]);
      }

      public void start() {
         super.start();
         if (EntityGrizzlyBear.this.isBaby()) {
            this.alertOthers();
            this.stop();
         }
      }

      protected void alertOther(Mob mobIn, LivingEntity targetIn) {
         if (mobIn instanceof EntityGrizzlyBear && !mobIn.isBaby()) {
            super.alertOther(mobIn, targetIn);
         }
      }
   }

   class MeleeAttackGoal extends net.minecraft.world.entity.ai.goal.MeleeAttackGoal {
      public MeleeAttackGoal() {
         super(EntityGrizzlyBear.this, 1.25, true);
      }

      protected void checkAndPerformAttack(LivingEntity enemy) {
         this.amCheckAndPerformAttack(enemy, this.mob.distanceToSqr(enemy.getX(), enemy.getY(), enemy.getZ()));
      }

      protected void amCheckAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
         double d0 = this.getAttackReachSqr(enemy);
         if (distToEnemySqr <= d0
            && (
               EntityGrizzlyBear.this.getAnimation() == IAnimatedEntity.NO_ANIMATION
                  || EntityGrizzlyBear.this.getAnimation() == EntityGrizzlyBear.ANIMATION_SNIFF
            )) {
            EntityGrizzlyBear.this.setAnimation(
               EntityGrizzlyBear.this.random.nextBoolean()
                  ? EntityGrizzlyBear.ANIMATION_MAUL
                  : (EntityGrizzlyBear.this.random.nextBoolean() ? EntityGrizzlyBear.ANIMATION_SWIPE_L : EntityGrizzlyBear.ANIMATION_SWIPE_R)
            );
         }
      }

      public void stop() {
         EntityGrizzlyBear.this.setStanding(false);
         super.stop();
      }

      protected double getAttackReachSqr(LivingEntity attackTarget) {
         return 3.0F + attackTarget.getBbWidth();
      }
   }

   class PanicGoal extends net.minecraft.world.entity.ai.goal.PanicGoal {
      public PanicGoal() {
         super(EntityGrizzlyBear.this, 2.0);
      }

      public boolean canUse() {
         return (EntityGrizzlyBear.this.isBaby() || EntityGrizzlyBear.this.isOnFire()) && super.canUse();
      }
   }
}
