package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalSwimMoveControllerSink;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIFollowOwnerWater;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityMimicOctopus extends TamableAnimal implements ISemiAquatic, IFollower, Bucketable {
   private static final EntityDataAccessor<Boolean> STOP_CHANGE = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> UPGRADED = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> MIMIC_ORDINAL = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> PREV_MIMIC_ORDINAL = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> MOISTNESS = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Optional<BlockState>> MIMICKED_BLOCK = SynchedEntityData.defineId(
      EntityMimicOctopus.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE
   );
   private static final EntityDataAccessor<Optional<BlockState>> PREV_MIMICKED_BLOCK = SynchedEntityData.defineId(
      EntityMimicOctopus.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE
   );
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> LAST_SCARED_MOB_ID = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> UPGRADED_LASER_ENTITY_ID = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.INT);
   public EntityMimicOctopus.MimicState localMimicState = EntityMimicOctopus.MimicState.OVERLAY;
   public float transProgress = 0.0F;
   public float prevTransProgress = 0.0F;
   public float colorShiftProgress = 0.0F;
   public float prevColorShiftProgress = 0.0F;
   public float groundProgress = 5.0F;
   public float prevGroundProgress = 0.0F;
   public float sitProgress = 0.0F;
   public float prevSitProgress = 0.0F;
   private boolean isLandNavigator;
   private int moistureAttackTime = 0;
   private int camoCooldown = 120 + this.random.nextInt(1200);
   private int mimicCooldown = 0;
   private int stopMimicCooldown = -1;
   private int fishFeedings;
   private int mimicreamFeedings;
   private int exclaimTime = 0;
   private BlockState localMimic;
   private LivingEntity laserTargetEntity;
   private int guardianLaserTime;

   protected EntityMimicOctopus(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
      this.switchNavigator(false);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 16.0)
         .add(Attributes.ARMOR, 0.0)
         .add(Attributes.ATTACK_DAMAGE, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   public static boolean canMimicOctopusSpawn(
      EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      BlockPos downPos = pos;

      while (downPos.getY() > 1 && !worldIn.getFluidState(downPos).isEmpty()) {
         downPos = downPos.below();
      }

      boolean spawnBlock = worldIn.getBlockState(downPos).is(AMTagRegistry.MIMIC_OCTOPUS_SPAWNS);
      return spawnBlock && downPos.getY() < worldIn.getSeaLevel() + 1;
   }

   public static EntityMimicOctopus.MimicState getStateForItem(ItemStack stack) {
      if (stack.is(AMTagRegistry.MIMIC_OCTOPUS_CREEPER_ITEMS)) {
         return EntityMimicOctopus.MimicState.CREEPER;
      } else if (stack.is(AMTagRegistry.MIMIC_OCTOPUS_GUARDIAN_ITEMS)) {
         return EntityMimicOctopus.MimicState.GUARDIAN;
      } else {
         return stack.is(AMTagRegistry.MIMIC_OCTOPUS_PUFFERFISH_ITEMS) ? EntityMimicOctopus.MimicState.PUFFERFISH : null;
      }
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.MIMIC_OCTOPUS_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.MIMIC_OCTOPUS_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.MIMIC_OCTOPUS_HURT.get();
   }

   public boolean checkSpawnObstruction(LevelReader worldIn) {
      return worldIn.isUnobstructed(this);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.mimicOctopusSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      this.entityData.set(PREV_MIMIC_ORDINAL, 0);
      this.setMimickedBlock(null);
      this.setMimicState(EntityMimicOctopus.MimicState.OVERLAY);
      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.entityData.set(MIMIC_ORDINAL, AMCompat.getInt(compound, "MimicState"));
      this.setUpgraded(AMCompat.getBoolean(compound, "Upgraded"));
      this.setOrderedToSit(AMCompat.getBoolean(compound, "Sitting"));
      this.setStopChange(AMCompat.getBoolean(compound, "StopChange"));
      this.setCommand(AMCompat.getInt(compound, "OctoCommand"));
      this.setMoistness(AMCompat.getInt(compound, "Moistness"));
      this.setFromBucket(AMCompat.getBoolean(compound, "FromBucket"));
      BlockState blockstate = null;
      if (AMCompat.contains(compound, "MimickedBlockState", 10)) {
         blockstate = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), AMCompat.getCompound(compound, "MimickedBlockState"));
         if (blockstate.isAir()) {
            blockstate = null;
         }
      }

      this.setMimickedBlock(blockstate);
      this.camoCooldown = AMCompat.getInt(compound, "CamoCooldown");
      this.mimicCooldown = AMCompat.getInt(compound, "MimicCooldown");
      this.stopMimicCooldown = AMCompat.getInt(compound, "StopMimicCooldown");
      this.fishFeedings = AMCompat.getInt(compound, "FishFeedings");
      this.mimicreamFeedings = AMCompat.getInt(compound, "MimicreamFeedings");
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("MimicState", this.getMimicState().ordinal());
      compound.putBoolean("Upgraded", this.isUpgraded());
      compound.putBoolean("Sitting", this.isSitting());
      compound.putInt("OctoCommand", this.getCommand());
      compound.putInt("Moistness", this.getMoistness());
      compound.putBoolean("FromBucket", this.fromBucket());
      compound.putBoolean("StopChange", this.isStopChange());
      BlockState blockstate = this.getMimickedBlock();
      if (blockstate != null) {
         AMCompat.put(compound, "MimickedBlockState", NbtUtils.writeBlockState(blockstate));
      }

      compound.putInt("CamoCooldown", this.camoCooldown);
      compound.putInt("MimicCooldown", this.mimicCooldown);
      compound.putInt("StopMimicCooldown", this.stopMimicCooldown);
      compound.putInt("FishFeedings", this.fishFeedings);
      compound.putInt("MimicreamFeedings", this.mimicreamFeedings);
   }

   @Nonnull
   public ItemStack getBucketItemStack() {
      ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.MIMIC_OCTOPUS_BUCKET.get());
      if (this.hasCustomName()) {
         AMCompat.setHoverName(stack, this.getCustomName());
      }

      return stack;
   }

   public void saveToBucketTag(@Nonnull ItemStack bucket) {
      if (this.hasCustomName()) {
         AMCompat.setHoverName(bucket, this.getCustomName());
      }

      CompoundTag platTag = new CompoundTag();
      AMCompat.saveAdditionalTo(this, platTag);
      CompoundTag compound = AMCompat.getOrCreateTag(bucket);
      AMCompat.put(compound, "MimicOctopusData", platTag);
      AMCompat.setTag(bucket, compound);
   }

   public void loadFromBucketTag(@Nonnull CompoundTag compound) {
      if (AMCompat.contains(compound, "MimicOctopusData")) {
         AMCompat.readAdditionalFrom(this, AMCompat.getCompound(compound, "MimicOctopusData"));
      }

      this.setMoistness(60000);
   }

   protected float getJumpPower() {
      return super.getJumpPower() * (this.isInWaterOrBubble() ? 1.3F : 1.0F);
   }

   @Override
   public boolean shouldFollow() {
      return this.getCommand() == 1;
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

   public boolean isPushedByFluid() {
      return false;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new EntityMimicOctopus.AIAttack());
      this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(2, new TameableAIFollowOwnerWater(this, 1.3, 4.0F, 2.0F, false));
      this.goalSelector.addGoal(3, new AnimalAIFindWater(this));
      this.goalSelector.addGoal(3, new AnimalAILeaveWater(this));
      this.goalSelector
         .addGoal(
            4, new TemptGoal(this, 1.0, AMCompat.ingredientOfTags(AMTagRegistry.MIMIC_OCTOPUS_BREEDABLES, AMTagRegistry.MIMIC_OCTOPUS_TAMEABLES), false) {
               public void tick() {
                  EntityMimicOctopus.this.setMimickedBlock(null);
                  super.tick();
                  EntityMimicOctopus.this.camoCooldown = 40;
                  EntityMimicOctopus.this.stopMimicCooldown = 40;
               }
            }
         );
      this.goalSelector.addGoal(5, new EntityMimicOctopus.AIFlee());
      this.goalSelector.addGoal(7, new BreedGoal(this, 0.8));
      this.goalSelector.addGoal(8, new EntityMimicOctopus.AIMimicNearbyMobs());
      this.goalSelector.addGoal(9, new BreedGoal(this, 0.8));
      this.goalSelector.addGoal(10, new EntityMimicOctopus.AISwim());
      this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
      this.targetSelector.addGoal(3, new HurtByTargetGoal(this) {
         public boolean canUse() {
            return EntityMimicOctopus.this.isTame() && super.canUse();
         }
      });
   }

   public boolean isFood(ItemStack stack) {
      Item item = stack.getItem();
      return this.isTame() && stack.is(AMTagRegistry.MIMIC_OCTOPUS_BREEDABLES);
   }

   public boolean isActiveCamo() {
      return this.getMimicState() == EntityMimicOctopus.MimicState.OVERLAY && this.getMimickedBlock() != null;
   }

   public double getVisibilityPercent(@Nullable Entity lookingEntity) {
      return this.isActiveCamo() ? super.getVisibilityPercent(lookingEntity) * 0.10000000149011612 : super.getVisibilityPercent(lookingEntity);
   }

   @Nonnull
   public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      Item item = itemstack.getItem();
      EntityMimicOctopus.MimicState readState = getStateForItem(itemstack);
      InteractionResult type = super.mobInteract(player, hand);
      if (readState != null && this.isTame()) {
         if (this.mimicCooldown == 0) {
            this.setMimicState(readState);
            this.mimicCooldown = 20;
            this.stopMimicCooldown = this.isUpgraded() ? 120 : 1200;
            this.camoCooldown = this.stopMimicCooldown;
            this.setMimickedBlock(null);
         }

         return InteractionResult.SUCCESS;
      } else {
         boolean tame = this.isTame();
         if (tame && itemstack.is(AMTagRegistry.MIMIC_OCTOPUS_TOGGLES_MIMIC)) {
            this.setStopChange(!this.isStopChange());
            if (this.isStopChange()) {
               this.makeEatingParticles(itemstack);
            } else {
               this.level().broadcastEntityEvent(this, (byte)6);
               this.mimicEnvironment();
            }

            return InteractionResult.SUCCESS;
         } else if (!tame && itemstack.is(AMTagRegistry.MIMIC_OCTOPUS_TAMEABLES)) {
            this.usePlayerItem(player, hand, itemstack);
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.DOLPHIN_EAT, this.getSoundVolume(), this.getVoicePitch());
            this.fishFeedings++;
            if (this.getMimicState() == EntityMimicOctopus.MimicState.OVERLAY && this.getMimickedBlock() == null) {
               if ((this.fishFeedings <= 5 || this.getRandom().nextInt(2) != 0) && this.fishFeedings <= 8) {
                  this.level().broadcastEntityEvent(this, (byte)6);
               } else {
                  this.tame(player);
                  this.level().broadcastEntityEvent(this, (byte)7);
               }
            }

            return InteractionResult.SUCCESS;
         } else if (tame && itemstack.is(AMTagRegistry.MIMIC_OCTOPUS_TAMEABLES)) {
            if (this.getHealth() < this.getMaxHealth()) {
               this.usePlayerItem(player, hand, itemstack);
               this.gameEvent(GameEvent.EAT);
               this.playSound(SoundEvents.DOLPHIN_EAT, this.getSoundVolume(), this.getVoicePitch());
               this.heal(5.0F);
               return InteractionResult.SUCCESS;
            } else {
               return InteractionResult.PASS;
            }
         } else {
            if (tame) {
               Optional<InteractionResult> result = Bucketable.bucketMobPickup(player, hand, this);
               if (result.isPresent()) {
                  return result.get();
               }

               if (itemstack.is(AMTagRegistry.MIMIC_OCTOPUS_MOISTURIZES) && this.getMoistness() < 24000) {
                  this.setMoistness(48000);
                  this.makeEatingParticles(itemstack);
                  this.usePlayerItem(player, hand, itemstack);
                  return InteractionResult.SUCCESS;
               }

               if (!this.isUpgraded() && itemstack.is(AMTagRegistry.MIMIC_OCTOPUS_ATTACK_FOODS)) {
                  this.mimicreamFeedings++;
                  if (this.mimicreamFeedings > 5 || this.mimicreamFeedings > 2 && this.random.nextInt(2) == 0) {
                     this.level().broadcastEntityEvent(this, (byte)46);
                     this.setUpgraded(true);
                     this.setMimicState(EntityMimicOctopus.MimicState.MIMICUBE);
                     this.setStopChange(false);
                     this.setMimickedBlock(null);
                     this.stopMimicCooldown = 40;
                  }

                  this.makeEatingParticles(itemstack);
                  this.usePlayerItem(player, hand, itemstack);
                  return InteractionResult.SUCCESS;
               }
            }

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
   }

   public int getCommand() {
      return (Integer)this.entityData.get(COMMAND);
   }

   public void setCommand(int command) {
      this.entityData.set(COMMAND, command);
   }

   private void makeEatingParticles(ItemStack item) {
      for (int i = 0; i < 6 + this.random.nextInt(3); i++) {
         double d2 = this.random.nextGaussian() * 0.02;
         double d0 = this.random.nextGaussian() * 0.02;
         double d1 = this.random.nextGaussian() * 0.02;
         this.level()
            .addParticle(
               new ItemParticleOption(ParticleTypes.ITEM, item),
               this.getX() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
               this.getY() + this.getBbHeight() * 0.5F + this.random.nextFloat() * this.getBbHeight() * 0.5F,
               this.getZ() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
               d0,
               d1,
               d2
            );
      }
   }

   public void calculateEntityAnimation(boolean flying) {
      float f1 = (float)Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
      float f2 = Math.min(f1 * (this.groundProgress < 2.5F ? 4.0F : 8.0F), 1.0F);
      this.walkAnimation.update(f2, 0.4F);
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigatorWide(this, this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new AnimalSwimMoveControllerSink(this, 1.3F, 1.0F);
         this.navigation = new SemiAquaticPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   public void tick() {
      super.tick();
      if (this.localMimic != this.getPrevMimickedBlock()) {
         this.localMimic = this.getPrevMimickedBlock();
         this.colorShiftProgress = 0.0F;
      }

      if (this.localMimicState != this.getPrevMimicState()) {
         this.localMimicState = this.getPrevMimicState();
         this.transProgress = 0.0F;
      }

      if (this.isInWater()) {
         if (this.isLandNavigator) {
            this.switchNavigator(false);
         }
      } else if (!this.isLandNavigator) {
         this.switchNavigator(true);
      }

      BlockPos pos = AMBlockPos.fromCoords(this.getX(), this.getEyeY() - 1.0, this.getZ());
      boolean ground = this.level().getBlockState(pos).isFaceSturdy(this.level(), pos, Direction.UP)
            && this.getMimicState() != EntityMimicOctopus.MimicState.GUARDIAN
         || !this.isInWaterOrBubble()
         || this.isSitting();
      this.prevTransProgress = this.transProgress;
      this.prevColorShiftProgress = this.colorShiftProgress;
      this.prevGroundProgress = this.groundProgress;
      this.prevSitProgress = this.sitProgress;
      if (this.getPrevMimicState() != this.getMimicState() && this.transProgress < 5.0F) {
         this.transProgress += 0.25F;
      }

      if (this.getPrevMimicState() == this.getMimicState() && this.transProgress > 0.0F) {
         this.transProgress -= 0.25F;
      }

      if (this.getPrevMimickedBlock() != this.getMimickedBlock() && this.colorShiftProgress < 5.0F) {
         this.colorShiftProgress += 0.25F;
      }

      if (this.getPrevMimickedBlock() == this.getMimickedBlock() && this.colorShiftProgress > 0.0F) {
         this.colorShiftProgress -= 0.25F;
      }

      if (ground && this.groundProgress < 5.0F) {
         this.groundProgress += 0.5F;
      }

      if (!ground && this.groundProgress > 0.0F) {
         this.groundProgress -= 0.5F;
      }

      if (this.isSitting() && this.sitProgress < 5.0F) {
         this.sitProgress += 0.5F;
      }

      if (!this.isSitting() && this.sitProgress > 0.0F) {
         this.sitProgress -= 0.5F;
      }

      if (this.isInWaterOrBubble()) {
         float f2 = (float)(-((float)this.getDeltaMovement().y * 3.0F * 57.2957763671875));
         this.setXRot(f2);
      }

      if (this.camoCooldown > 0) {
         this.camoCooldown--;
      }

      if (this.mimicCooldown > 0) {
         this.mimicCooldown--;
      }

      if (this.stopMimicCooldown > 0) {
         this.stopMimicCooldown--;
      }

      if (this.isNoAi()) {
         this.setAirSupply(this.getMaxAirSupply());
      } else if (!this.isInWaterRainOrBubble() && this.getMainHandItem().getItem() != Items.WATER_BUCKET) {
         this.setMoistness(this.getMoistness() - 1);
         if (this.getMoistness() <= 0 && this.moistureAttackTime-- <= 0) {
            this.setOrderedToSit(false);
            this.hurt(this.damageSources().dryOut(), this.random.nextInt(2) == 0 ? 1.0F : 0.0F);
            this.moistureAttackTime = 20;
         }
      } else {
         this.setMoistness(60000);
      }

      if (this.camoCooldown <= 0 && this.random.nextInt(300) == 0) {
         this.mimicEnvironment();
         this.camoCooldown = this.getRandom().nextInt(2200) + 200;
      }

      if ((this.getMimicState() != EntityMimicOctopus.MimicState.OVERLAY || this.getMimickedBlock() != null)
         && this.stopMimicCooldown == 0
         && !this.isStopChange()) {
         this.setMimicState(EntityMimicOctopus.MimicState.OVERLAY);
         this.setMimickedBlock(null);
         this.stopMimicCooldown = -1;
      }

      if (this.level().isClientSide() && this.exclaimTime > 0) {
         this.exclaimTime--;
         if (this.exclaimTime == 0) {
            Entity e = this.level().getEntity((Integer)this.entityData.get(LAST_SCARED_MOB_ID));
            if (e != null && this.transProgress >= 5.0F) {
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
      }

      if (this.hasGuardianLaser()) {
         if (this.guardianLaserTime < 30) {
            this.guardianLaserTime++;
         }

         LivingEntity livingentity = this.getGuardianLaser();
         if (livingentity != null && this.isInWaterOrBubble()) {
            this.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
            this.getLookControl().tick();
            double d5 = this.getLaserAttackAnimationScale(0.0F);
            double d0 = livingentity.getX() - this.getX();
            double d1 = livingentity.getY(0.5) - this.getEyeY();
            double d2 = livingentity.getZ() - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d0 /= d3;
            d1 /= d3;
            d2 /= d3;
            double d4 = this.random.nextDouble();

            while (d4 < d3) {
               d4 += 1.8 - d5 + this.random.nextDouble() * (1.7 - d5);
               this.level().addParticle(ParticleTypes.BUBBLE, this.getX() + d0 * d4, this.getEyeY() + d1 * d4, this.getZ() + d2 * d4, 0.0, 0.0, 0.0);
            }

            if (this.guardianLaserTime == 30) {
               livingentity.hurt(this.damageSources().mobAttack(this), 5.0F);
               this.guardianLaserTime = 0;
               this.entityData.set(UPGRADED_LASER_ENTITY_ID, -1);
            }
         }
      }

      if (!this.level().isClientSide() && this.tickCount % 40 == 0) {
         this.heal(2.0F);
      }
   }

   public float getLaserAttackAnimationScale(float p_175477_1_) {
      return (this.guardianLaserTime + p_175477_1_) / 30.0F;
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      if (id == 68) {
         if (this.exclaimTime == 0) {
            this.exclaimTime = 20;
         }
      } else if (id == 69) {
         this.creeperExplode();
      } else {
         super.handleEntityEvent(id);
      }
   }

   public void mimicEnvironment() {
      if (!this.isStopChange()) {
         BlockPos down = this.getPositionDown();
         if (!this.level().isEmptyBlock(down)) {
            this.setMimicState(EntityMimicOctopus.MimicState.OVERLAY);
            this.setMimickedBlock(this.level().getBlockState(down));
         }

         this.stopMimicCooldown = this.getRandom().nextInt(2200);
      }
   }

   public int getMoistness() {
      return (Integer)this.entityData.get(MOISTNESS);
   }

   public void setMoistness(int p_211137_1_) {
      this.entityData.set(MOISTNESS, p_211137_1_);
   }

   private BlockPos getPositionDown() {
      BlockPos pos = AMBlockPos.fromCoords(this.getX(), this.getEyeY(), this.getZ());

      while (pos.getY() > 1 && (this.level().isEmptyBlock(pos) || this.level().isWaterAt(pos))) {
         pos = pos.below();
      }

      return pos;
   }

   public void travel(Vec3 travelVector) {
      if (this.isSitting()) {
         if (this.getNavigation().getPath() != null) {
            this.getNavigation().stop();
         }

         travelVector = Vec3.ZERO;
         super.travel(travelVector);
      } else {
         if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
         } else {
            super.travel(travelVector);
         }
      }
   }

   public boolean isSitting() {
      return (Boolean)this.entityData.get(SITTING);
   }

   public void setOrderedToSit(boolean sit) {
      this.entityData.set(SITTING, sit);
   }

   public boolean fromBucket() {
      return (Boolean)this.entityData.get(FROM_BUCKET);
   }

   public void setFromBucket(boolean sit) {
      this.entityData.set(FROM_BUCKET, sit);
   }

   @Nonnull
   public SoundEvent getPickupSound() {
      return SoundEvents.BUCKET_FILL_FISH;
   }

   public boolean isUpgraded() {
      return (Boolean)this.entityData.get(FROM_BUCKET);
   }

   public void setUpgraded(boolean sit) {
      this.entityData.set(FROM_BUCKET, sit);
   }

   public boolean isStopChange() {
      return (Boolean)this.entityData.get(STOP_CHANGE);
   }

   public void setStopChange(boolean sit) {
      this.entityData.set(STOP_CHANGE, sit);
   }

   public boolean hasGuardianLaser() {
      return (Integer)this.entityData.get(UPGRADED_LASER_ENTITY_ID) != -1 && this.isUpgraded() && this.isInWaterOrBubble();
   }

   @Nullable
   public LivingEntity getGuardianLaser() {
      if (!this.hasGuardianLaser()) {
         return null;
      } else if (this.level().isClientSide()) {
         if (this.laserTargetEntity != null) {
            return this.laserTargetEntity;
         } else {
            Entity lvt_1_1_ = this.level().getEntity((Integer)this.entityData.get(UPGRADED_LASER_ENTITY_ID));
            if (lvt_1_1_ instanceof LivingEntity) {
               this.laserTargetEntity = (LivingEntity)lvt_1_1_;
               return this.laserTargetEntity;
            } else {
               return null;
            }
         }
      } else {
         return this.getTarget();
      }
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return AMCompat.create(AMEntityRegistry.MIMIC_OCTOPUS.get(), serverWorld);
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.fromBucket() || this.isTame();
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return !this.isTame() && !this.fromBucket();
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(MIMIC_ORDINAL, 0);
      builder.define(PREV_MIMIC_ORDINAL, -1);
      builder.define(MOISTNESS, 60000);
      builder.define(MIMICKED_BLOCK, Optional.empty());
      builder.define(PREV_MIMICKED_BLOCK, Optional.empty());
      builder.define(SITTING, false);
      builder.define(COMMAND, 0);
      builder.define(LAST_SCARED_MOB_ID, -1);
      builder.define(FROM_BUCKET, false);
      builder.define(UPGRADED, false);
      builder.define(STOP_CHANGE, false);
      builder.define(UPGRADED_LASER_ENTITY_ID, -1);
   }

   public EntityMimicOctopus.MimicState getMimicState() {
      return EntityMimicOctopus.MimicState.values()[Mth.clamp((Integer)this.entityData.get(MIMIC_ORDINAL), 0, 4)];
   }

   public void setMimicState(EntityMimicOctopus.MimicState state) {
      if (this.getMimicState() != state) {
         this.entityData.set(PREV_MIMIC_ORDINAL, (Integer)this.entityData.get(MIMIC_ORDINAL));
      }

      this.entityData.set(MIMIC_ORDINAL, state.ordinal());
   }

   public EntityMimicOctopus.MimicState getPrevMimicState() {
      return this.entityData.get(PREV_MIMIC_ORDINAL) == -1
         ? null
         : EntityMimicOctopus.MimicState.values()[Mth.clamp((Integer)this.entityData.get(PREV_MIMIC_ORDINAL), 0, 4)];
   }

   @Nullable
   public BlockState getMimickedBlock() {
      return (BlockState)((Optional)this.entityData.get(MIMICKED_BLOCK)).orElse(null);
   }

   public void setMimickedBlock(@Nullable BlockState state) {
      if (this.getMimickedBlock() != state) {
         this.entityData.set(PREV_MIMICKED_BLOCK, Optional.ofNullable(this.getMimickedBlock()));
      }

      this.entityData.set(MIMICKED_BLOCK, Optional.ofNullable(state));
   }

   @Nullable
   public BlockState getPrevMimickedBlock() {
      return (BlockState)((Optional)this.entityData.get(PREV_MIMICKED_BLOCK)).orElse(null);
   }

   protected void updateAir(int p_209207_1_) {
      if (this.isAlive() && !this.isInWaterOrBubble()) {
         this.setAirSupply(p_209207_1_ - 1);
         if (this.getAirSupply() == -20) {
            this.setAirSupply(0);
            this.hurt(this.damageSources().dryOut(), 2.0F);
         }
      } else {
         this.setAirSupply(1200);
      }
   }

   @Override
   public boolean shouldEnterWater() {
      return !this.isSitting() && (this.getTarget() == null || this.getTarget().isInWaterOrBubble());
   }

   @Override
   public boolean shouldLeaveWater() {
      return this.getTarget() != null && !this.getTarget().isInWaterOrBubble();
   }

   @Override
   public boolean shouldStopMoving() {
      return this.isSitting();
   }

   @Override
   public int getWaterSearchRange() {
      return 16;
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).getType() != Type.MISS;
   }

   public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
      float radius = -9.45F - this.getRandom().nextInt(24) - radiusAdd;
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.x() + extraX), 0, (int)(fleePos.z() + extraZ));
      BlockPos ground = this.getOctopusGround(radialPos);
      return ground != null ? Vec3.atCenterOf(ground) : null;
   }

   private BlockPos getOctopusGround(BlockPos in) {
      BlockPos position = new BlockPos(in.getX(), (int)this.getY(), in.getZ());

      while (position.getY() > 2 && this.level().getFluidState(position).is(FluidTags.WATER)) {
         position = position.below();
      }

      return position;
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
      super.onSyncedDataUpdated(key);
      if (UPGRADED_LASER_ENTITY_ID.equals(key)) {
         this.guardianLaserTime = 0;
         this.laserTargetEntity = null;
      }
   }

   private void creeperExplode() {
      Explosion explosion = new Explosion(
         this.level(),
         this,
         this.damageSources().mobAttack(this),
         (ExplosionDamageCalculator)null,
         this.getX(),
         this.getY(),
         this.getZ(),
         1.0F + this.random.nextFloat(),
         false,
         BlockInteraction.KEEP,
         ParticleTypes.EXPLOSION,
         ParticleTypes.EXPLOSION_EMITTER,
         SoundEvents.GENERIC_EXPLODE
      );
      explosion.explode();
      explosion.finalizeExplosion(true);
   }

   private class AIAttack extends Goal {
      private int executionCooldown = 0;
      private int scareMobTime = 0;
      private Vec3 fleePosition = null;

      public AIAttack() {
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (this.executionCooldown > 0) {
            EntityMimicOctopus.this.entityData.set(EntityMimicOctopus.UPGRADED_LASER_ENTITY_ID, -1);
            this.executionCooldown--;
         }

         return EntityMimicOctopus.this.isStopChange() && EntityMimicOctopus.this.getMimicState() == EntityMimicOctopus.MimicState.OVERLAY
            ? false
            : this.executionCooldown == 0
               && EntityMimicOctopus.this.isTame()
               && EntityMimicOctopus.this.getTarget() != null
               && EntityMimicOctopus.this.getTarget().isAlive();
      }

      public void stop() {
         this.fleePosition = null;
         this.scareMobTime = 0;
         this.executionCooldown = 100 + EntityMimicOctopus.this.random.nextInt(200);
         if (EntityMimicOctopus.this.isUpgraded()) {
            this.executionCooldown = 30;
         } else {
            EntityMimicOctopus.this.setLastHurtByMob(null);
            EntityMimicOctopus.this.setTarget(null);
         }

         if (EntityMimicOctopus.this.stopMimicCooldown <= 0) {
            EntityMimicOctopus.this.mimicEnvironment();
         }

         EntityMimicOctopus.this.entityData.set(EntityMimicOctopus.UPGRADED_LASER_ENTITY_ID, -1);
      }

      public Vec3 generateFleePosition(LivingEntity fleer) {
         for (int i = 0; i < 15; i++) {
            BlockPos pos = fleer.blockPosition()
               .offset(
                  EntityMimicOctopus.this.random.nextInt(32) - 16, EntityMimicOctopus.this.random.nextInt(16), EntityMimicOctopus.this.random.nextInt(32) - 16
               );

            while (fleer.level().isEmptyBlock(pos) && pos.getY() > 1) {
               pos = pos.below();
            }

            if (!(fleer instanceof PathfinderMob)) {
               return Vec3.atCenterOf(pos);
            }

            if (((PathfinderMob)fleer).getWalkTargetValue(pos) >= 0.0F) {
               return Vec3.atCenterOf(pos);
            }
         }

         return null;
      }

      public void tick() {
         LivingEntity target = EntityMimicOctopus.this.getTarget();
         if (target != null) {
            if (this.scareMobTime > 0) {
               if (this.fleePosition == null || target.distanceToSqr(this.fleePosition) < target.getBbWidth() * target.getBbWidth() * 2.0F) {
                  this.fleePosition = this.generateFleePosition(target);
               }

               if (target instanceof Mob && this.fleePosition != null) {
                  ((Mob)target).getNavigation().moveTo(this.fleePosition.x, this.fleePosition.y, this.fleePosition.z, 1.5);
                  ((Mob)target).getMoveControl().setWantedPosition(this.fleePosition.x, this.fleePosition.y, this.fleePosition.z, 1.5);
                  ((Mob)target).setTarget(null);
               }

               EntityMimicOctopus.this.camoCooldown = Math.max(EntityMimicOctopus.this.camoCooldown, 20);
               EntityMimicOctopus.this.stopMimicCooldown = Math.max(EntityMimicOctopus.this.stopMimicCooldown, 20);
               this.scareMobTime--;
               if (this.scareMobTime == 0) {
                  this.stop();
                  return;
               }
            }

            double dist = EntityMimicOctopus.this.distanceTo(target);
            boolean move = true;
            if (dist < 7.0
               && EntityMimicOctopus.this.hasLineOfSight(target)
               && EntityMimicOctopus.this.getMimicState() == EntityMimicOctopus.MimicState.GUARDIAN
               && EntityMimicOctopus.this.isUpgraded()) {
               EntityMimicOctopus.this.entityData.set(EntityMimicOctopus.UPGRADED_LASER_ENTITY_ID, target.getId());
               move = false;
            }

            if (dist < 3.0) {
               EntityMimicOctopus.this.entityData.set(EntityMimicOctopus.LAST_SCARED_MOB_ID, target.getId());
               if (move) {
                  move = EntityMimicOctopus.this.isUpgraded() && dist > 2.0;
               }

               EntityMimicOctopus.this.getNavigation().stop();
               if (!EntityMimicOctopus.this.isStopChange()) {
                  EntityMimicOctopus.this.setMimickedBlock(null);
                  EntityMimicOctopus.MimicState prev = EntityMimicOctopus.this.getMimicState();
                  if (EntityMimicOctopus.this.isInWaterOrBubble()) {
                     if (prev != EntityMimicOctopus.MimicState.GUARDIAN && prev != EntityMimicOctopus.MimicState.PUFFERFISH) {
                        if (EntityMimicOctopus.this.random.nextBoolean()) {
                           EntityMimicOctopus.this.setMimicState(EntityMimicOctopus.MimicState.GUARDIAN);
                        } else {
                           EntityMimicOctopus.this.setMimicState(EntityMimicOctopus.MimicState.PUFFERFISH);
                        }
                     }
                  } else {
                     EntityMimicOctopus.this.setMimicState(EntityMimicOctopus.MimicState.CREEPER);
                  }
               }

               if (EntityMimicOctopus.this.getMimicState() != EntityMimicOctopus.MimicState.OVERLAY) {
                  EntityMimicOctopus.this.mimicCooldown = 40;
                  EntityMimicOctopus.this.stopMimicCooldown = Math.max(EntityMimicOctopus.this.stopMimicCooldown, 60);
               }

               if (EntityMimicOctopus.this.isUpgraded() && EntityMimicOctopus.this.transProgress >= 5.0F) {
                  if (EntityMimicOctopus.this.getMimicState() == EntityMimicOctopus.MimicState.PUFFERFISH
                     && EntityMimicOctopus.this.getBoundingBox().expandTowards(2.0, 1.3, 2.0).intersects(target.getBoundingBox())) {
                     target.hurt(EntityMimicOctopus.this.damageSources().mobAttack(EntityMimicOctopus.this), 4.0F);
                     target.addEffect(new MobEffectInstance(MobEffects.POISON, 400, 2));
                  }

                  if (EntityMimicOctopus.this.getMimicState() == EntityMimicOctopus.MimicState.GUARDIAN) {
                     if (EntityMimicOctopus.this.getBoundingBox().expandTowards(1.0, 1.0, 1.0).intersects(target.getBoundingBox())) {
                        target.hurt(EntityMimicOctopus.this.damageSources().mobAttack(EntityMimicOctopus.this), 1.0F);
                     }

                     EntityMimicOctopus.this.entityData.set(EntityMimicOctopus.UPGRADED_LASER_ENTITY_ID, target.getId());
                  }

                  if (EntityMimicOctopus.this.getMimicState() == EntityMimicOctopus.MimicState.CREEPER) {
                     EntityMimicOctopus.this.creeperExplode();
                     EntityMimicOctopus.this.level().broadcastEntityEvent(EntityMimicOctopus.this, (byte)69);
                     this.executionCooldown = 300;
                  }
               }

               if (this.scareMobTime == 0) {
                  EntityMimicOctopus.this.level().broadcastEntityEvent(EntityMimicOctopus.this, (byte)68);
                  this.scareMobTime = 60 + EntityMimicOctopus.this.random.nextInt(60);
               }
            }

            if (move) {
               EntityMimicOctopus.this.lookAt(target, 30.0F, 30.0F);
               EntityMimicOctopus.this.getNavigation().moveTo(target, 1.2000000476837158);
            }
         }
      }
   }

   private class AIFlee extends Goal {
      protected final EntityMimicOctopus.EntitySorter theNearestAttackableTargetSorter;
      protected final com.google.common.base.Predicate<? super Entity> targetEntitySelector;
      protected int executionChance = 8;
      protected boolean mustUpdate;
      private Entity targetEntity;
      private Vec3 flightTarget = null;
      private int cooldown = 0;

      AIFlee() {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.theNearestAttackableTargetSorter = new EntityMimicOctopus.EntitySorter(EntityMimicOctopus.this);
         this.targetEntitySelector = new com.google.common.base.Predicate<Entity>() {
            public boolean apply(@Nullable Entity e) {
               return e.isAlive() && e.getType().builtInRegistryHolder().is(AMTagRegistry.MIMIC_OCTOPUS_FEARS)
                  || e instanceof Player && !((Player)e).isCreative();
            }
         };
      }

      public boolean canUse() {
         if (!EntityMimicOctopus.this.isPassenger() && !EntityMimicOctopus.this.isVehicle() && !EntityMimicOctopus.this.isTame()) {
            if (!this.mustUpdate) {
               long worldTime = EntityMimicOctopus.this.level().getGameTime() % 10L;
               if (EntityMimicOctopus.this.getNoActionTime() >= 100 && worldTime != 0L) {
                  return false;
               }

               if (EntityMimicOctopus.this.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0L) {
                  return false;
               }
            }

            List<Entity> list = EntityMimicOctopus.this.level()
               .getEntitiesOfClass(Entity.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
            if (list.isEmpty()) {
               return false;
            } else {
               Collections.sort(list, this.theNearestAttackableTargetSorter);
               this.targetEntity = list.get(0);
               this.mustUpdate = false;
               return true;
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.targetEntity != null && !EntityMimicOctopus.this.isTame() && EntityMimicOctopus.this.distanceTo(this.targetEntity) < 20.0F;
      }

      public void stop() {
         this.flightTarget = null;
         this.targetEntity = null;
         EntityMimicOctopus.this.setMimicState(EntityMimicOctopus.MimicState.OVERLAY);
         EntityMimicOctopus.this.setMimickedBlock(null);
      }

      public void tick() {
         if (this.cooldown > 0) {
            this.cooldown--;
         }

         if (!EntityMimicOctopus.this.isActiveCamo()) {
            EntityMimicOctopus.this.mimicEnvironment();
         }

         if (this.flightTarget != null) {
            EntityMimicOctopus.this.getNavigation().moveTo(this.flightTarget.x, this.flightTarget.y, this.flightTarget.z, 1.2000000476837158);
            if (this.cooldown == 0 && EntityMimicOctopus.this.isTargetBlocked(this.flightTarget)) {
               this.cooldown = 30;
               this.flightTarget = null;
            }
         }

         if (this.targetEntity != null) {
            if (this.flightTarget == null || this.flightTarget != null && EntityMimicOctopus.this.distanceToSqr(this.flightTarget) < 6.0) {
               Vec3 vec = DefaultRandomPos.getPosAway(EntityMimicOctopus.this, 16, 7, this.targetEntity.position());
               if (vec != null) {
                  this.flightTarget = vec;
               }
            }

            if (EntityMimicOctopus.this.distanceTo(this.targetEntity) > 20.0F) {
               this.stop();
            }
         }
      }

      protected double getTargetDistance() {
         return 10.0;
      }

      protected AABB getTargetableArea(double targetDistance) {
         Vec3 renderCenter = new Vec3(EntityMimicOctopus.this.getX(), EntityMimicOctopus.this.getY() + 0.5, EntityMimicOctopus.this.getZ());
         AABB aabb = new AABB(-targetDistance, -targetDistance, -targetDistance, targetDistance, targetDistance, targetDistance);
         return aabb.move(renderCenter);
      }
   }

   private class AIMimicNearbyMobs extends Goal {
      protected final EntityMimicOctopus.EntitySorter theNearestAttackableTargetSorter;
      protected final com.google.common.base.Predicate<? super Entity> targetEntitySelector;
      protected int executionChance = 30;
      protected boolean mustUpdate;
      private Entity targetEntity;
      private Vec3 flightTarget = null;
      private int cooldown = 0;

      AIMimicNearbyMobs() {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.theNearestAttackableTargetSorter = new EntityMimicOctopus.EntitySorter(EntityMimicOctopus.this);
         this.targetEntitySelector = new com.google.common.base.Predicate<Entity>() {
            public boolean apply(@Nullable Entity e) {
               return e.isAlive() && (e instanceof Creeper || e instanceof Guardian || e instanceof Pufferfish);
            }
         };
      }

      public boolean canUse() {
         if (!EntityMimicOctopus.this.isPassenger()
            && !EntityMimicOctopus.this.isVehicle()
            && EntityMimicOctopus.this.getMimicState() == EntityMimicOctopus.MimicState.OVERLAY
            && EntityMimicOctopus.this.mimicCooldown <= 0) {
            if (!this.mustUpdate) {
               long worldTime = EntityMimicOctopus.this.level().getGameTime() % 10L;
               if (EntityMimicOctopus.this.getNoActionTime() >= 100 && worldTime != 0L) {
                  return false;
               }

               if (EntityMimicOctopus.this.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0L) {
                  return false;
               }
            }

            List<Entity> list = EntityMimicOctopus.this.level()
               .getEntitiesOfClass(Entity.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
            if (list.isEmpty()) {
               return false;
            } else {
               Collections.sort(list, this.theNearestAttackableTargetSorter);
               this.targetEntity = list.get(0);
               this.mustUpdate = false;
               return true;
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.targetEntity != null
            && EntityMimicOctopus.this.distanceTo(this.targetEntity) < 10.0F
            && EntityMimicOctopus.this.getMimicState() == EntityMimicOctopus.MimicState.OVERLAY;
      }

      public void stop() {
         EntityMimicOctopus.this.getNavigation().stop();
         this.flightTarget = null;
         this.targetEntity = null;
      }

      public void tick() {
         if (this.cooldown > 0) {
            this.cooldown--;
         }

         if (this.targetEntity != null) {
            EntityMimicOctopus.this.getNavigation().moveTo(this.targetEntity, 1.2000000476837158);
            if (EntityMimicOctopus.this.distanceTo(this.targetEntity) > 20.0F) {
               this.stop();
               EntityMimicOctopus.this.setMimicState(EntityMimicOctopus.MimicState.OVERLAY);
               EntityMimicOctopus.this.setMimickedBlock(null);
            } else if (EntityMimicOctopus.this.distanceTo(this.targetEntity) < 5.0F && EntityMimicOctopus.this.hasLineOfSight(this.targetEntity)) {
               int i = 1200;
               EntityMimicOctopus.this.stopMimicCooldown = i;
               EntityMimicOctopus.this.camoCooldown = i + 40;
               EntityMimicOctopus.this.mimicCooldown = 40;
               if (this.targetEntity instanceof Creeper) {
                  EntityMimicOctopus.this.setMimicState(EntityMimicOctopus.MimicState.CREEPER);
               } else if (this.targetEntity instanceof Guardian) {
                  EntityMimicOctopus.this.setMimicState(EntityMimicOctopus.MimicState.GUARDIAN);
               } else if (this.targetEntity instanceof Pufferfish) {
                  EntityMimicOctopus.this.setMimicState(EntityMimicOctopus.MimicState.PUFFERFISH);
               } else {
                  EntityMimicOctopus.this.setMimicState(EntityMimicOctopus.MimicState.OVERLAY);
                  EntityMimicOctopus.this.setMimickedBlock(null);
               }

               this.stop();
            }
         }
      }

      protected double getTargetDistance() {
         return 10.0;
      }

      protected AABB getTargetableArea(double targetDistance) {
         Vec3 renderCenter = new Vec3(EntityMimicOctopus.this.getX(), EntityMimicOctopus.this.getY() + 0.5, EntityMimicOctopus.this.getZ());
         AABB aabb = new AABB(-targetDistance, -targetDistance, -targetDistance, targetDistance, targetDistance, targetDistance);
         return aabb.move(renderCenter);
      }
   }

   private class AISwim extends SemiAquaticAIRandomSwimming {
      public AISwim() {
         super(EntityMimicOctopus.this, 1.0, 35);
      }

      @Override
      protected Vec3 findSurfaceTarget(PathfinderMob creature, int i, int i1) {
         if (creature.getRandom().nextInt(5) == 0) {
            return super.findSurfaceTarget(creature, i, i1);
         } else {
            BlockPos downPos = creature.blockPosition();

            while (creature.level().getFluidState(downPos).is(FluidTags.WATER) || creature.level().getFluidState(downPos).is(FluidTags.LAVA)) {
               downPos = downPos.below();
            }

            return EntityMimicOctopus.this.level().getBlockState(downPos).canOcclude()
                  && EntityMimicOctopus.this.level().getBlockState(downPos).getBlock() != Blocks.MAGMA_BLOCK
               ? new Vec3(downPos.getX() + 0.5F, downPos.getY(), downPos.getZ() + 0.5F)
               : null;
         }
      }
   }

   public record EntitySorter(Entity theEntity) implements Comparator<Entity> {
      public int compare(Entity p_compare_1_, Entity p_compare_2_) {
         double d0 = this.theEntity.distanceToSqr(p_compare_1_);
         double d1 = this.theEntity.distanceToSqr(p_compare_2_);
         return Double.compare(d0, d1);
      }
   }

   public static enum MimicState {
      OVERLAY,
      CREEPER,
      GUARDIAN,
      PUFFERFISH,
      MIMICUBE;
   }
}
