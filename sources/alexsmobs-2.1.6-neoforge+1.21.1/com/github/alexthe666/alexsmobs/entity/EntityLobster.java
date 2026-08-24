package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.BottomFeederAIWander;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class EntityLobster extends WaterAnimal implements ISemiAquatic, Bucketable {
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityLobster.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(EntityLobster.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityLobster.class, EntityDataSerializers.INT);
   public float attackProgress;
   public float prevAttackProgress;
   private int attackCooldown = 0;

   protected EntityLobster(EntityType type, Level p_i48565_2_) {
      super(type, p_i48565_2_);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
   }

   public int getMaxSpawnClusterSize() {
      return 7;
   }

   public boolean isMaxGroupSizeReached(int sizeIn) {
      return false;
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 5.0)
         .add(Attributes.ARMOR, 2.0)
         .add(Attributes.ATTACK_DAMAGE, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 0.15000000596046448);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.lobsterSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.LOBSTER_HURT.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.LOBSTER_HURT.get();
   }

   public boolean checkSpawnObstruction(LevelReader worldIn) {
      return worldIn.isUnobstructed(this);
   }

   public static String getVariantName(int variant) {
      return switch (variant) {
         case 1 -> "blue";
         case 2 -> "yellow";
         case 3 -> "redblue";
         case 4 -> "black";
         case 5 -> "white";
         default -> "red";
      };
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new AnimalAIFindWater(this));
      this.goalSelector.addGoal(1, new AnimalAILeaveWater(this));
      this.goalSelector.addGoal(3, new BottomFeederAIWander(this, 1.0, 10, 50));
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
   }

   public void travel(Vec3 travelVector) {
      if (this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), travelVector);
         this.move(MoverType.SELF, this.getDeltaMovement());
         if (this.jumping) {
            this.setDeltaMovement(this.getDeltaMovement().scale(1.4));
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.72, 0.0));
         } else {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.4));
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.08, 0.0));
         }
      } else {
         super.travel(travelVector);
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(VARIANT, 0);
      builder.define(ATTACK_TICK, 0);
      builder.define(FROM_BUCKET, false);
   }

   @Nonnull
   public ItemStack getBucketItemStack() {
      ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.LOBSTER_BUCKET.get());
      if (this.hasCustomName()) {
         AMCompat.setHoverName(stack, this.getCustomName());
      }

      return stack;
   }

   public void saveToBucketTag(@Nonnull ItemStack bucket) {
      if (this.hasCustomName()) {
         AMCompat.setHoverName(bucket, this.getCustomName());
      }

      Bucketable.saveDefaultDataToBucketTag(this, bucket);
      CompoundTag compoundnbt = AMCompat.getOrCreateTag(bucket);
      compoundnbt.putInt("BucketVariantTag", this.getVariant());
      AMCompat.setTag(bucket, compoundnbt);
   }

   public void loadFromBucketTag(@Nonnull CompoundTag compound) {
      Bucketable.loadDefaultDataFromBucketTag(this, compound);
      if (AMCompat.contains(compound, "BucketVariantTag", 3)) {
         this.setVariant(AMCompat.getInt(compound, "BucketVariantTag"));
      }
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.fromBucket();
   }

   public boolean removeWhenFarAway(double p_27492_) {
      return !this.fromBucket() && !this.hasCustomName();
   }

   @Nonnull
   protected InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
      return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
   }

   public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
      return worldIn.getFluidState(pos.below()).isEmpty() && worldIn.getFluidState(pos).is(FluidTags.WATER) ? 10.0F : super.getWalkTargetValue(pos, worldIn);
   }

   public boolean doHurtTarget(Entity entityIn) {
      this.entityData.set(ATTACK_TICK, 5);
      return super.doHurtTarget(entityIn);
   }

   public void tick() {
      super.tick();
      this.prevAttackProgress = this.attackProgress;
      if ((Integer)this.entityData.get(ATTACK_TICK) > 0) {
         if (this.attackProgress == 3.0F) {
            this.playSound(AMSoundRegistry.LOBSTER_ATTACK.get(), this.getSoundVolume(), this.getVoicePitch());
         }

         if ((Integer)this.entityData.get(ATTACK_TICK) == 2 && this.getTarget() != null && this.distanceTo(this.getTarget()) < 1.3) {
            this.getTarget().hurt(this.damageSources().mobAttack(this), 2.0F);
         }

         this.entityData.set(ATTACK_TICK, (Integer)this.entityData.get(ATTACK_TICK) - 1);
         if (this.attackProgress < 5.0F) {
            this.attackProgress++;
         }
      } else if (this.attackProgress > 0.0F) {
         this.attackProgress--;
      }

      if (this.attackCooldown > 0) {
         this.attackCooldown--;
      }

      if (this.getTarget() != null && this.distanceTo(this.getTarget()) <= 1.0F && this.attackCooldown == 0) {
         this.lookAt(this.getTarget(), 180.0F, 20.0F);
         AMCompat.doHurtTarget(this, this.getTarget());
         this.attackCooldown = 20;
      }
   }

   protected void handleAirSupply(int air) {
   }

   public int getVariant() {
      return (Integer)this.entityData.get(VARIANT);
   }

   public void setVariant(int variant) {
      this.entityData.set(VARIANT, variant);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Variant", this.getVariant());
      compound.putBoolean("FromBucket", this.fromBucket());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setVariant(AMCompat.getInt(compound, "Variant"));
      this.setFromBucket(AMCompat.getBoolean(compound, "FromBucket"));
   }

   public boolean fromBucket() {
      return (Boolean)this.entityData.get(FROM_BUCKET);
   }

   public void setFromBucket(boolean p_203706_1_) {
      this.entityData.set(FROM_BUCKET, p_203706_1_);
   }

   @Nonnull
   public SoundEvent getPickupSound() {
      return SoundEvents.BUCKET_FILL_FISH;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      float variantChange = this.getRandom().nextFloat();
      if (variantChange <= 1.0E-5) {
         this.setVariant(5);
      } else if (variantChange <= 2.0E-5) {
         this.setVariant(4);
      } else if (variantChange <= 0.05F) {
         this.setVariant(3);
      } else if (variantChange <= 0.1F) {
         this.setVariant(2);
      } else if (variantChange <= 0.25F) {
         this.setVariant(1);
      } else {
         this.setVariant(0);
      }

      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new SemiAquaticPathNavigator(this, worldIn) {
         @Override
         public boolean isStableDestination(BlockPos pos) {
            return this.level.getBlockState(pos).getFluidState().isEmpty();
         }
      };
   }

   @Override
   public boolean shouldEnterWater() {
      return true;
   }

   @Override
   public boolean shouldLeaveWater() {
      return false;
   }

   @Override
   public boolean shouldStopMoving() {
      return false;
   }

   @Override
   public int getWaterSearchRange() {
      return 5;
   }

   public static <T extends Mob> boolean canLobsterSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      boolean spawnBlock = worldIn.getBlockState(pos.below()).is(AMTagRegistry.LOBSTER_SPAWNS);
      return spawnBlock || worldIn.getFluidState(pos).is(FluidTags.WATER);
   }
}
