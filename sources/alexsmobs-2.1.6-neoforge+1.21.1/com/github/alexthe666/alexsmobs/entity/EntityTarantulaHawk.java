package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.FlyingAIFollowOwner;
import com.github.alexthe666.alexsmobs.message.MessageTarantulaHawkSting;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
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
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EntityTarantulaHawk extends TamableAnimal implements IFollower {
   public static final int STING_DURATION = 2400;
   protected static final EntityDimensions FLIGHT_SIZE = EntityDimensions.fixed(0.9F, 1.5F);
   private static final EntityDataAccessor<Float> FLY_ANGLE = SynchedEntityData.defineId(EntityTarantulaHawk.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> NETHER = SynchedEntityData.defineId(EntityTarantulaHawk.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityTarantulaHawk.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DRAGGING = SynchedEntityData.defineId(EntityTarantulaHawk.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityTarantulaHawk.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DIGGING = SynchedEntityData.defineId(EntityTarantulaHawk.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SCARED = SynchedEntityData.defineId(EntityTarantulaHawk.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(EntityTarantulaHawk.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityTarantulaHawk.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(EntityTarantulaHawk.class, EntityDataSerializers.BOOLEAN);
   public float prevFlyAngle;
   public float prevSitProgress;
   public float sitProgress;
   public float prevDragProgress;
   public float dragProgress;
   public float prevFlyProgress;
   public float flyProgress;
   public float prevAttackProgress;
   public float attackProgress;
   public float prevDigProgress;
   public float digProgress;
   private boolean isLandNavigator;
   private boolean flightSize = false;
   private int timeFlying = 0;
   private boolean bredBuryFlag = false;
   private int spiderFeedings = 0;
   private int dragTime = 0;

   protected EntityTarantulaHawk(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.switchNavigator(false);
   }

   public static boolean canTarantulaHawkSpawn(
      EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return worldIn.getBlockState(pos.below()).is(AMTagRegistry.TARANTULA_HAWK_SPAWNS) && worldIn.getRawBrightness(pos, 0) > 8
         || isBiomeNether(worldIn, pos)
         || AMConfig.fireproofTarantulaHawk;
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 18.0)
         .add(Attributes.ARMOR, 4.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.MOVEMENT_SPEED, 0.30000001192092896)
         .add(Attributes.ATTACK_DAMAGE, 5.0);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.tarantulaHawkSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      if (isBiomeNether(worldIn, this.blockPosition())) {
         this.setNether(true);
      }

      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   private static boolean isBiomeNether(LevelAccessor worldIn, BlockPos position) {
      return worldIn.getBiome(position).is(AMTagRegistry.SPAWNS_NETHER_TARANTULA_HAWKS);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(2, new FlyingAIFollowOwner(this, 1.0, 10.0F, 2.0F, false));
      this.goalSelector.addGoal(3, new EntityTarantulaHawk.AIFleeRoadrunners());
      this.goalSelector.addGoal(4, new EntityTarantulaHawk.AIMelee());
      this.goalSelector.addGoal(5, new EntityTarantulaHawk.AIBury());
      this.goalSelector.addGoal(6, new BreedGoal(this, 1.0));
      this.goalSelector
         .addGoal(
            7,
            new TemptGoal(
               this,
               1.1,
               AMCompat.ingredientOfTags(
                  AMTagRegistry.TARANTULA_HAWK_BREEDABLES, AMTagRegistry.TARANTULA_HAWK_TAMEABLES, AMTagRegistry.TARANTULA_HAWK_FOODSTUFFS
               ),
               false
            )
         );
      this.goalSelector.addGoal(8, new EntityTarantulaHawk.AIWalkIdle());
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
      this.targetSelector.addGoal(3, new AnimalAIHurtByTargetNotBaby(this));
      this.targetSelector.addGoal(4, new EntityAINearestTarget3D(this, Spider.class, 15, true, true, null) {
         public boolean canUse() {
            return super.canUse() && !EntityTarantulaHawk.this.isBaby() && !EntityTarantulaHawk.this.isSitting();
         }
      });
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.TARANTULA_HAWK_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.TARANTULA_HAWK_HURT.get();
   }

   public boolean fireImmune() {
      return this.isNether() || AMConfig.fireproofTarantulaHawk;
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigation(this, this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new EntityTarantulaHawk.MoveController();
         this.navigation = new DirectPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FLY_ANGLE, 0.0F);
      builder.define(NETHER, false);
      builder.define(FLYING, false);
      builder.define(SITTING, false);
      builder.define(DRAGGING, false);
      builder.define(DIGGING, false);
      builder.define(SCARED, false);
      builder.define(ANGRY, false);
      builder.define(ATTACK_TICK, 0);
      builder.define(COMMAND, 0);
   }

   public boolean hurt(DamageSource source, float amount) {
      return source.getEntity() instanceof LivingEntity
            && AMCompat.isArthropod((LivingEntity)source.getEntity())
            && ((LivingEntity)source.getEntity()).hasEffect(AMCompat.effect(AMEffectRegistry.DEBILITATING_STING.get()))
         ? false
         : super.hurt(source, amount);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("HawkSitting", this.isSitting());
      compound.putBoolean("Nether", this.isNether());
      compound.putBoolean("Digging", this.isDigging());
      compound.putBoolean("Flying", this.isFlying());
      compound.putInt("Command", this.getCommand());
      compound.putInt("SpiderFeedings", this.spiderFeedings);
      compound.putBoolean("BreedFlag", this.bredBuryFlag);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setOrderedToSit(AMCompat.getBoolean(compound, "HawkSitting"));
      this.setNether(AMCompat.getBoolean(compound, "Nether"));
      this.setDigging(AMCompat.getBoolean(compound, "Digging"));
      this.setFlying(AMCompat.getBoolean(compound, "Flying"));
      this.setCommand(AMCompat.getInt(compound, "Command"));
      this.spiderFeedings = AMCompat.getInt(compound, "SpiderFeedings");
      this.bredBuryFlag = AMCompat.getBoolean(compound, "BreedFlag");
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

   public float getFlyAngle() {
      return (Float)this.entityData.get(FLY_ANGLE);
   }

   public void setFlyAngle(float progress) {
      this.entityData.set(FLY_ANGLE, progress);
   }

   public boolean isFlying() {
      return (Boolean)this.entityData.get(FLYING);
   }

   public void setFlying(boolean flying) {
      if (!flying || !this.isBaby()) {
         this.entityData.set(FLYING, flying);
      }
   }

   public boolean isNether() {
      return (Boolean)this.entityData.get(NETHER);
   }

   public void setNether(boolean sit) {
      this.entityData.set(NETHER, sit);
   }

   public boolean isScared() {
      return (Boolean)this.entityData.get(SCARED);
   }

   public void setScared(boolean sit) {
      this.entityData.set(SCARED, sit);
   }

   public boolean isSitting() {
      return (Boolean)this.entityData.get(SITTING);
   }

   public void setOrderedToSit(boolean sit) {
      this.entityData.set(SITTING, sit);
   }

   public boolean isDragging() {
      return (Boolean)this.entityData.get(DRAGGING);
   }

   public void setDragging(boolean sit) {
      this.entityData.set(DRAGGING, sit);
   }

   public boolean isDigging() {
      return (Boolean)this.entityData.get(DIGGING);
   }

   public void setDigging(boolean sit) {
      this.entityData.set(DIGGING, sit);
   }

   public EntityDimensions getDefaultDimensions(Pose poseIn) {
      return this.isFlying() && !this.isBaby() ? FLIGHT_SIZE : super.getDefaultDimensions(poseIn);
   }

   public void tick() {
      this.prevFlyAngle = this.getFlyAngle();
      super.tick();
      this.prevAttackProgress = this.attackProgress;
      this.prevFlyProgress = this.flyProgress;
      this.prevSitProgress = this.sitProgress;
      this.prevDragProgress = this.dragProgress;
      this.prevDigProgress = this.digProgress;
      boolean flying = this.isFlying();
      boolean sitting = this.isSitting();
      boolean dragging = this.isDragging();
      boolean digging = this.isDigging();
      if (flying) {
         if (this.flyProgress < 5.0F) {
            this.flyProgress++;
         }
      } else if (this.flyProgress > 0.0F) {
         this.flyProgress--;
      }

      if (sitting) {
         if (this.sitProgress < 5.0F) {
            this.sitProgress++;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (dragging) {
         if (this.dragProgress < 5.0F) {
            this.dragProgress++;
         }
      } else if (this.dragProgress > 0.0F) {
         this.dragProgress--;
      }

      if (digging) {
         if (this.digProgress < 5.0F) {
            this.digProgress++;
         }
      } else if (this.digProgress > 0.0F) {
         this.digProgress--;
      }

      if (this.flightSize && !flying) {
         this.refreshDimensions();
         this.flightSize = false;
      }

      if (!this.flightSize && this.isFlying()) {
         this.refreshDimensions();
         this.flightSize = true;
      }

      float threshold = 0.015F;
      if (this.isFlying() && this.yRotO - this.getYRot() > threshold) {
         this.setFlyAngle(this.getFlyAngle() + 5.0F);
      } else if (this.isFlying() && this.yRotO - this.getYRot() < -threshold) {
         this.setFlyAngle(this.getFlyAngle() - 5.0F);
      } else if (this.getFlyAngle() > 0.0F) {
         this.setFlyAngle(Math.max(this.getFlyAngle() - 4.0F, 0.0F));
      } else if (this.getFlyAngle() < 0.0F) {
         this.setFlyAngle(Math.min(this.getFlyAngle() + 4.0F, 0.0F));
      }

      this.setFlyAngle(Mth.clamp(this.getFlyAngle(), -30.0F, 30.0F));
      if (!this.level().isClientSide()) {
         if (this.isFlying() && this.isLandNavigator) {
            this.switchNavigator(false);
         }

         if (!this.isFlying() && !this.isLandNavigator) {
            this.switchNavigator(true);
         }

         if (this.isFlying()) {
            if (this.timeFlying % 25 == 0) {
               this.playSound(AMSoundRegistry.TARANTULA_HAWK_WING.get(), this.getSoundVolume(), this.getVoicePitch());
            }

            this.timeFlying++;
            this.setNoGravity(true);
            if (this.isSitting() || this.isPassenger() || this.isInLove()) {
               this.setFlying(false);
            }
         } else {
            this.timeFlying = 0;
            this.setNoGravity(false);
         }

         if (this.getTarget() != null && this.getTarget() instanceof Player && !this.isTame()) {
            this.entityData.set(ANGRY, true);
         } else {
            this.entityData.set(ANGRY, false);
         }
      }

      if ((Integer)this.entityData.get(ATTACK_TICK) > 0) {
         this.entityData.set(ATTACK_TICK, (Integer)this.entityData.get(ATTACK_TICK) - 1);
         if (this.attackProgress < 5.0F) {
            this.attackProgress++;
         }
      } else if (this.attackProgress > 0.0F) {
         this.attackProgress--;
      }

      if (this.isDigging() && this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).canOcclude()) {
         BlockPos posit = this.getBlockPosBelowThatAffectsMyMovement();
         BlockState understate = this.level().getBlockState(posit);

         for (int i = 0; i < 4 + this.random.nextInt(2); i++) {
            double particleX = posit.getX() + this.random.nextFloat();
            double particleY = posit.getY() + 1.0F;
            double particleZ = posit.getZ() + this.random.nextFloat();
            double motX = this.random.nextGaussian() * 0.02;
            double motY = 0.1F + this.random.nextFloat() * 0.2F;
            double motZ = this.random.nextGaussian() * 0.02;
            this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, understate), particleX, particleY, particleZ, motX, motY, motZ);
         }
      }

      if (this.tickCount > 0 && this.tickCount % 300 == 0 && this.getHealth() < this.getMaxHealth()) {
         this.heal(1.0F);
      }

      if (!this.level().isClientSide() && this.isDragging() && this.getPassengers().isEmpty() && !this.isDigging()) {
         this.dragTime++;
         if (this.dragTime > 5000) {
            this.dragTime = 0;

            for (Entity e : this.getPassengers()) {
               e.hurt(this.damageSources().mobAttack(this), 10.0F);
            }

            this.ejectPassengers();
            this.setDragging(false);
         }
      }
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      InteractionResult type = super.mobInteract(player, hand);
      if (!this.isTame() && itemstack.is(AMTagRegistry.TARANTULA_HAWK_TAMEABLES)) {
         this.usePlayerItem(player, hand, itemstack);
         this.gameEvent(GameEvent.EAT);
         this.playSound(SoundEvents.STRIDER_EAT, this.getSoundVolume(), this.getVoicePitch());
         this.spiderFeedings++;
         if ((this.spiderFeedings < 15 || this.getRandom().nextInt(6) != 0) && this.spiderFeedings <= 25) {
            this.level().broadcastEntityEvent(this, (byte)6);
         } else {
            this.tame(player);
            this.level().broadcastEntityEvent(this, (byte)7);
         }

         return InteractionResult.SUCCESS;
      } else if (this.isTame() && itemstack.is(AMTagRegistry.TARANTULA_HAWK_FOODSTUFFS)) {
         if (this.getHealth() < this.getMaxHealth()) {
            this.usePlayerItem(player, hand, itemstack);
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.STRIDER_EAT, this.getSoundVolume(), this.getVoicePitch());
            this.heal(5.0F);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      } else {
         InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
         if (interactionresult != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS && this.isTame() && this.isOwnedBy(player)) {
            if (player.isShiftKeyDown()) {
               if (this.getMainHandItem().isEmpty()) {
                  ItemStack cop = itemstack.copy();
                  cop.setCount(1);
                  this.setItemInHand(InteractionHand.MAIN_HAND, cop);
                  itemstack.shrink(1);
                  return InteractionResult.SUCCESS;
               }

               AMCompat.spawnAtLocation(this, this.getMainHandItem().copy());
               this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
               return InteractionResult.SUCCESS;
            }

            if (!this.isFood(itemstack)) {
               this.setCommand(this.getCommand() + 1);
               if (this.getCommand() == 3) {
                  this.setCommand(0);
               }

               player.displayClientMessage(Component.translatable("entity.alexsmobs.all.command_" + this.getCommand(), new Object[]{this.getName()}), true);
               boolean sit = this.getCommand() == 2;
               if (sit) {
                  this.setOrderedToSit(true);
                  return InteractionResult.SUCCESS;
               }

               this.setOrderedToSit(false);
               return InteractionResult.SUCCESS;
            }
         }

         return type;
      }
   }

   public boolean isFood(ItemStack stack) {
      Item item = stack.getItem();
      return this.isTame() && stack.is(AMTagRegistry.TARANTULA_HAWK_BREEDABLES);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      return null;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.CACTUS) || super.isInvulnerableTo(source);
   }

   public void spawnChildFromBreeding(ServerLevel world, Animal animalEntity) {
      this.bredBuryFlag = true;
      ServerPlayer serverplayerentity = this.getLoveCause();
      if (serverplayerentity == null && animalEntity.getLoveCause() != null) {
         serverplayerentity = animalEntity.getLoveCause();
      }

      if (serverplayerentity != null) {
         serverplayerentity.awardStat(Stats.ANIMALS_BRED);
         CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this, animalEntity, this);
      }

      this.setAge(6000);
      animalEntity.setAge(6000);
      this.resetLove();
      animalEntity.resetLove();
      world.broadcastEntityEvent(this, (byte)7);
      world.broadcastEntityEvent(this, (byte)18);
      if (AMCompat.gameRule(world, AMCompat.Rule.MOB_LOOT)) {
         world.addFreshEntity(new ExperienceOrb(world, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
      }
   }

   @Override
   public void followEntity(TamableAnimal tameable, LivingEntity owner, double followSpeed) {
      if (this.distanceTo(owner) > 5.0F) {
         this.setFlying(true);
         this.getMoveControl().setWantedPosition(owner.getX(), owner.getY() + owner.getBbHeight(), owner.getZ(), followSpeed);
      } else {
         if (this.onGround()) {
            this.setFlying(false);
         }

         if (this.isFlying() && !this.isOverWater()) {
            BlockPos vec = this.getCrowGround(this.blockPosition());
            if (vec != null) {
               this.getMoveControl().setWantedPosition(vec.getX(), vec.getY(), vec.getZ(), followSpeed);
            }
         } else {
            this.getNavigation().moveTo(owner, followSpeed);
         }
      }
   }

   public void positionRider(Entity passenger, MoveFunction moveFunc) {
      this.setXRot(0.0F);
      float radius = 1.0F + passenger.getBbWidth() * 0.5F;
      float angle = 0.017453292F * (this.yBodyRot - 180.0F);
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      double extraY = 0.0;
      passenger.setPos(this.getX() + extraX, this.getY() + extraY, this.getZ() + extraZ);
   }

   private boolean isOverWater() {
      BlockPos position = this.blockPosition();

      while (position.getY() > 0 && this.level().isEmptyBlock(position)) {
         position = position.below();
      }

      return !this.level().getFluidState(position).isEmpty() || position.getY() <= 0;
   }

   public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
      float radius = -9.45F - this.getRandom().nextInt(24) - radiusAdd;
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.x() + extraX), 0, (int)(fleePos.z() + extraZ));
      BlockPos ground = this.getCrowGround(radialPos);
      int distFromGround = (int)this.getY() - ground.getY();
      int flightHeight = 4 + this.getRandom().nextInt(10);
      BlockPos newPos = ground.above(distFromGround > 8 ? flightHeight : this.getRandom().nextInt(6) + 1);
      return !this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1.0 ? Vec3.atCenterOf(newPos) : null;
   }

   private BlockPos getCrowGround(BlockPos in) {
      BlockPos position = new BlockPos(in.getX(), (int)this.getY(), in.getZ());

      while (position.getY() > -64 && !this.level().getBlockState(position).isSolid() && this.level().getFluidState(position).isEmpty()) {
         position = position.below();
      }

      return position;
   }

   public Vec3 getBlockGrounding(Vec3 fleePos) {
      float radius = -9.45F - this.getRandom().nextInt(24);
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.x() + extraX), (int)this.getY(), (int)(fleePos.z() + extraZ));
      BlockPos ground = this.getCrowGround(radialPos);
      if (ground.getY() == -64) {
         return this.position();
      } else {
         ground = this.blockPosition();

         while (ground.getY() > -62 && !this.level().getBlockState(ground).isSolid()) {
            ground = ground.below();
         }

         return !this.isTargetBlocked(Vec3.atCenterOf(ground.above())) ? Vec3.atCenterOf(ground) : null;
      }
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).getType() != Type.MISS;
   }

   private Vec3 getOrbitVec(Vec3 vector3d, float gatheringCircleDist, boolean orbitClockwise) {
      float angle = 0.034906585F * (orbitClockwise ? -this.tickCount : this.tickCount);
      double extraX = gatheringCircleDist * Mth.sin(angle);
      double extraZ = gatheringCircleDist * Mth.cos(angle);
      if (vector3d != null) {
         Vec3 pos = new Vec3(vector3d.x() + extraX, vector3d.y() + this.random.nextInt(2) + 4.0, vector3d.z() + extraZ);
         if (this.level().isEmptyBlock(AMBlockPos.fromVec3(pos))) {
            return pos;
         }
      }

      return null;
   }

   public int getCommand() {
      return (Integer)this.entityData.get(COMMAND);
   }

   public void setCommand(int command) {
      this.entityData.set(COMMAND, command);
   }

   private BlockPos genSandPos(BlockPos parent) {
      LevelAccessor world = this.level();
      Random random = new Random();
      int range = 24;

      for (int i = 0; i < 15; i++) {
         BlockPos sandAir = parent.offset(random.nextInt(range) - range / 2, -5, random.nextInt(range) - range / 2);

         while (!world.isEmptyBlock(sandAir) && sandAir.getY() < 255) {
            sandAir = sandAir.above();
         }

         BlockState state = world.getBlockState(sandAir.below());
         if (state.is(BlockTags.SAND)) {
            return sandAir.below();
         }
      }

      return null;
   }

   @Override
   public boolean shouldFollow() {
      return this.getCommand() == 1 && !this.isDragging() && !this.isDigging() && (this.getTarget() == null || !this.getTarget().isAlive());
   }

   public boolean isAngry() {
      return (Boolean)this.entityData.get(ANGRY);
   }

   private class AIBury extends Goal {
      private final EntityTarantulaHawk hawk;
      private BlockPos buryPos = null;
      private int digTime = 0;
      private double stageX;
      private double stageY;
      private double stageZ;

      private AIBury() {
         this.hawk = EntityTarantulaHawk.this;
      }

      public boolean canUse() {
         if (this.hawk.isDragging() && this.hawk.getTarget() != null) {
            BlockPos pos = this.hawk.genSandPos(this.hawk.blockPosition());
            if (pos != null) {
               this.buryPos = pos;
               return true;
            }
         }

         return false;
      }

      public boolean canContinueToUse() {
         return this.hawk.isDragging()
            && this.digTime < 200
            && this.hawk.getTarget() != null
            && this.buryPos != null
            && EntityTarantulaHawk.this.level().getBlockState(this.buryPos).is(BlockTags.SAND);
      }

      public void start() {
         this.digTime = 0;
         this.stageX = this.hawk.getX();
         this.stageY = this.hawk.getY();
         this.stageZ = this.hawk.getZ();
      }

      public void stop() {
         this.digTime = 0;
         this.hawk.setDigging(false);
         this.hawk.setDragging(false);
         this.hawk.setTarget(null);
         this.hawk.setLastHurtByMob(null);
      }

      public void tick() {
         this.hawk.setFlying(false);
         this.hawk.setDragging(true);
         LivingEntity target = this.hawk.getTarget();
         if (this.hawk.distanceToSqr(Vec3.atCenterOf(this.buryPos)) < 9.0 && !this.hawk.isDigging()) {
            this.hawk.setDigging(true);
            this.stageX = target.getX();
            this.stageY = target.getY();
            this.stageZ = target.getZ();
         }

         if (this.hawk.isDigging()) {
            target.noPhysics = true;
            this.digTime++;
            this.hawk.ejectPassengers();
            target.setPos(this.stageX, this.stageY - Math.min(3.0F, this.digTime * 0.05F), this.stageZ);
            this.hawk.getNavigation().moveTo(this.stageX, this.stageY, this.stageZ, 0.8500000238418579);
         } else {
            this.hawk.getNavigation().moveTo(this.buryPos.getX(), this.buryPos.getY(), this.buryPos.getZ(), 0.5);
         }
      }
   }

   private class AIFleeRoadrunners extends Goal {
      private int searchCooldown = 0;
      private LivingEntity fear = null;
      private Vec3 fearVec = null;

      public boolean canUse() {
         if (this.searchCooldown <= 0) {
            this.searchCooldown = 100 + EntityTarantulaHawk.this.random.nextInt(100);

            for (EntityRoadrunner roadrunner : EntityTarantulaHawk.this.level()
               .getEntitiesOfClass(EntityRoadrunner.class, EntityTarantulaHawk.this.getBoundingBox().inflate(15.0, 32.0, 15.0))) {
               if (this.fear == null || EntityTarantulaHawk.this.distanceTo(this.fear) > EntityTarantulaHawk.this.distanceTo(roadrunner)) {
                  this.fear = roadrunner;
               }
            }
         } else {
            this.searchCooldown--;
         }

         return EntityTarantulaHawk.this.isAlive() && this.fear != null;
      }

      public boolean canContinueToUse() {
         return this.fear != null && this.fear.isAlive() && EntityTarantulaHawk.this.distanceTo(this.fear) < 32.0F;
      }

      public void start() {
         super.start();
         EntityTarantulaHawk.this.setScared(true);
      }

      public void tick() {
         if (this.fear != null) {
            if (this.fearVec == null || EntityTarantulaHawk.this.distanceToSqr(this.fearVec) < 4.0) {
               this.fearVec = EntityTarantulaHawk.this.getBlockInViewAway(this.fearVec == null ? this.fear.position() : this.fearVec, 12.0F);
            }

            if (this.fearVec != null) {
               EntityTarantulaHawk.this.setFlying(true);
               EntityTarantulaHawk.this.getMoveControl().setWantedPosition(this.fearVec.x, this.fearVec.y, this.fearVec.z, 1.100000023841858);
            }
         }
      }

      public void stop() {
         EntityTarantulaHawk.this.setScared(false);
         this.fear = null;
         this.fearVec = null;
      }
   }

   private class AIMelee extends Goal {
      private final EntityTarantulaHawk hawk;
      private int orbitCooldown = 0;
      private boolean clockwise = false;
      private Vec3 orbitVec = null;
      private BlockPos sandPos = null;

      public AIMelee() {
         this.hawk = EntityTarantulaHawk.this;
      }

      public boolean canUse() {
         return this.hawk.getTarget() != null
            && !this.hawk.isSitting()
            && !this.hawk.isScared()
            && this.hawk.getTarget().isAlive()
            && !this.hawk.isDragging()
            && !this.hawk.isDigging()
            && !this.hawk.getTarget().noPhysics
            && !this.hawk.getTarget().isPassenger();
      }

      public void start() {
         this.hawk.setDragging(false);
         this.clockwise = EntityTarantulaHawk.this.random.nextBoolean();
      }

      public void tick() {
         LivingEntity target = this.hawk.getTarget();
         boolean paralized = target != null
            && AMCompat.isArthropod(target)
            && !target.noPhysics
            && target.hasEffect(AMCompat.effect(AMEffectRegistry.DEBILITATING_STING.get()));
         boolean paralizedWithChild = paralized && target.getEffect(AMCompat.effect(AMEffectRegistry.DEBILITATING_STING.get())).getAmplifier() > 0;
         if (this.sandPos == null || !EntityTarantulaHawk.this.level().getBlockState(this.sandPos).is(BlockTags.SAND)) {
            this.sandPos = this.hawk.genSandPos(target.blockPosition());
         }

         if (this.orbitCooldown > 0) {
            this.orbitCooldown--;
            this.hawk.setFlying(true);
            if (target != null && (this.orbitVec == null || this.hawk.distanceToSqr(this.orbitVec) < 4.0 || !this.hawk.getMoveControl().hasWanted())) {
               this.orbitVec = this.hawk
                  .getOrbitVec(target.position().add(0.0, target.getBbHeight(), 0.0), (float)(10 + EntityTarantulaHawk.this.random.nextInt(2)), false);
               if (this.orbitVec != null) {
                  this.hawk.getMoveControl().setWantedPosition(this.orbitVec.x, this.orbitVec.y, this.orbitVec.z, 1.0);
               }
            }
         } else if ((paralized && !this.hawk.isTame() || paralizedWithChild && this.hawk.bredBuryFlag) && this.sandPos != null) {
            if (this.hawk.onGround()) {
               this.hawk.setFlying(false);
               this.hawk.getNavigation().moveTo(target, 1.0);
            } else {
               Vec3 vector3d = this.hawk.getBlockGrounding(this.hawk.position());
               if (vector3d != null && this.hawk.isFlying()) {
                  this.hawk.getMoveControl().setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0);
               }
            }

            if (this.hawk.distanceTo(target) < target.getBbWidth() + 1.5F && !target.isPassenger()) {
               this.hawk.setDragging(true);
               this.hawk.setFlying(false);
               AMCompat.startRiding(target, this.hawk, true);
            }
         } else if (target != null && !paralizedWithChild) {
            double dist = this.hawk.distanceTo(target);
            if (dist < 10.0 && !this.hawk.isFlying()) {
               if (this.hawk.onGround()) {
                  this.hawk.setFlying(false);
               }

               this.hawk.getNavigation().moveTo(target, 1.0);
            } else {
               this.hawk.setFlying(true);
               this.hawk.getMoveControl().setWantedPosition(target.getX(), target.getEyeY(), target.getZ(), 1.0);
            }

            if (dist < target.getBbWidth() + 2.5F) {
               if ((Integer)this.hawk.entityData.get(EntityTarantulaHawk.ATTACK_TICK) == 0 && this.hawk.attackProgress == 0.0F) {
                  this.hawk.entityData.set(EntityTarantulaHawk.ATTACK_TICK, 7);
               }

               if (this.hawk.attackProgress == 5.0F) {
                  AMCompat.doHurtTarget(this.hawk, target);
                  if (this.hawk.bredBuryFlag && target.getHealth() <= 1.0F) {
                     target.heal(5.0F);
                  }

                  target.addEffect(
                     new MobEffectInstance(
                        AMCompat.effect(AMEffectRegistry.DEBILITATING_STING.get()), AMCompat.isArthropod(target) ? 2400 : 600, this.hawk.bredBuryFlag ? 1 : 0
                     )
                  );
                  if (!this.hawk.level().isClientSide() && AMCompat.isArthropod(target)) {
                     AlexsMobs.sendMSGToAll(new MessageTarantulaHawkSting(this.hawk.getId(), target.getId()));
                  }

                  this.orbitCooldown = AMCompat.isArthropod(target)
                     ? 200 + EntityTarantulaHawk.this.random.nextInt(200)
                     : 10 + EntityTarantulaHawk.this.random.nextInt(20);
               }
            }
         }
      }

      public void stop() {
         this.orbitCooldown = 0;
         this.hawk.bredBuryFlag = false;
         this.clockwise = EntityTarantulaHawk.this.random.nextBoolean();
         this.orbitVec = null;
         if (this.hawk.getPassengers().isEmpty()) {
            this.hawk.setTarget(null);
         }
      }
   }

   private class AIWalkIdle extends Goal {
      protected final EntityTarantulaHawk hawk;
      protected double x;
      protected double y;
      protected double z;
      private boolean flightTarget = false;

      public AIWalkIdle() {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.hawk = EntityTarantulaHawk.this;
      }

      public boolean canUse() {
         if (this.hawk.isVehicle()
            || this.hawk.isScared()
            || this.hawk.isDragging()
            || EntityTarantulaHawk.this.getCommand() == 1
            || this.hawk.getTarget() != null && this.hawk.getTarget().isAlive()
            || this.hawk.isPassenger()
            || this.hawk.isSitting()) {
            return false;
         } else if (this.hawk.getRandom().nextInt(30) != 0 && !this.hawk.isFlying()) {
            return false;
         } else {
            if (this.hawk.onGround()) {
               this.flightTarget = EntityTarantulaHawk.this.random.nextBoolean();
            } else {
               this.flightTarget = EntityTarantulaHawk.this.random.nextInt(5) > 0 && this.hawk.timeFlying < 200;
            }

            Vec3 lvt_1_1_ = this.getPosition();
            if (lvt_1_1_ == null) {
               return false;
            } else {
               this.x = lvt_1_1_.x;
               this.y = lvt_1_1_.y;
               this.z = lvt_1_1_.z;
               return true;
            }
         }
      }

      public void tick() {
         if (this.flightTarget) {
            this.hawk.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
         } else {
            this.hawk.getNavigation().moveTo(this.x, this.y, this.z, 1.0);
         }

         if (!this.flightTarget && EntityTarantulaHawk.this.isFlying() && this.hawk.onGround()) {
            this.hawk.setFlying(false);
         }

         if (EntityTarantulaHawk.this.isFlying() && this.hawk.onGround() && this.hawk.timeFlying > 10) {
            this.hawk.setFlying(false);
         }
      }

      @Nullable
      protected Vec3 getPosition() {
         Vec3 vector3d = this.hawk.position();
         if (this.hawk.isOverWater()) {
            this.flightTarget = true;
         }

         if (this.flightTarget) {
            return this.hawk.timeFlying >= 50 && !this.hawk.isOverWater()
               ? this.hawk.getBlockGrounding(vector3d)
               : this.hawk.getBlockInViewAway(vector3d, 0.0F);
         } else {
            return LandRandomPos.getPos(this.hawk, 10, 7);
         }
      }

      public boolean canContinueToUse() {
         if (this.hawk.isSitting() || EntityTarantulaHawk.this.getCommand() == 1) {
            return false;
         } else {
            return this.flightTarget
               ? this.hawk.isFlying() && this.hawk.distanceToSqr(this.x, this.y, this.z) > 2.0
               : !this.hawk.getNavigation().isDone() && !this.hawk.isVehicle();
         }
      }

      public void start() {
         if (this.flightTarget) {
            this.hawk.setFlying(true);
            this.hawk.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
         } else {
            this.hawk.getNavigation().moveTo(this.x, this.y, this.z, 1.0);
         }
      }

      public void stop() {
         this.hawk.getNavigation().stop();
         super.stop();
      }
   }

   class MoveController extends MoveControl {
      private final Mob parentEntity = EntityTarantulaHawk.this;

      public MoveController() {
         super(EntityTarantulaHawk.this);
      }

      public void tick() {
         if (this.operation == Operation.MOVE_TO) {
            Vec3 vector3d = new Vec3(this.wantedX - this.parentEntity.getX(), this.wantedY - this.parentEntity.getY(), this.wantedZ - this.parentEntity.getZ());
            double d0 = vector3d.length();
            double width = this.parentEntity.getBoundingBox().getSize();
            if (d0 < width) {
               this.operation = Operation.WAIT;
               this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().scale(0.5));
            } else {
               float angle = 0.017453292F * (this.parentEntity.yBodyRot + 90.0F);
               float radius = (float)Math.sin(this.parentEntity.tickCount * 0.2F) * 2.0F;
               double extraX = radius * Mth.sin(3.1415927F + angle);
               double extraZ = radius * Mth.cos(angle);
               Vec3 vector3d1 = vector3d.scale(this.speedModifier * 0.05 / d0);
               Vec3 strafPlus = new Vec3(extraX, 0.0, extraZ).scale(0.003 * Math.min(d0, 100.0));
               this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(strafPlus));
               this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(vector3d1));
               this.parentEntity.setYRot(-((float)Mth.atan2(vector3d1.x, vector3d1.z)) * 57.295776F);
               if (!EntityTarantulaHawk.this.isDragging()) {
                  this.parentEntity.yBodyRot = this.parentEntity.getYRot();
               }
            }
         }
      }
   }
}
