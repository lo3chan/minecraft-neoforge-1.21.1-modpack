package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAISwimBottom;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityTriops extends WaterAnimal implements ITargetsDroppedItems, Bucketable {
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityTriops.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> TRIOPS_SCALE = SynchedEntityData.defineId(EntityTriops.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> BABY_AGE = SynchedEntityData.defineId(EntityTriops.class, EntityDataSerializers.INT);
   public float prevOnLandProgress;
   public float onLandProgress;
   public float prevSwimRot;
   public float swimRot;
   public boolean fedCarrot = false;
   public int breedCooldown = 0;
   public float tail1Yaw;
   public float prevTail1Yaw;
   public float tail2Yaw;
   public float prevTail2Yaw;
   public float moveDistance;
   private EntityTriops breedWith;
   private boolean pregnant;

   public EntityTriops(EntityType<? extends WaterAnimal> type, Level level) {
      super(type, level);
      this.moveControl = new AquaticMoveController(this, 1.0F, 15.0F);
      this.tail1Yaw = this.getYRot();
      this.prevTail1Yaw = this.getYRot();
      this.tail2Yaw = this.getYRot();
      this.prevTail2Yaw = this.getYRot();
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FROM_BUCKET, false);
      builder.define(TRIOPS_SCALE, 1.0F);
      builder.define(BABY_AGE, 0);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new EntityTriops.BreedGoal());
      this.goalSelector.addGoal(1, new EntityTriops.LayEggGoal());
      this.goalSelector.addGoal(2, new TryFindWaterGoal(this));
      this.goalSelector.addGoal(3, new PanicGoal(this, 1.0));
      this.goalSelector.addGoal(4, new AnimalAISwimBottom(this, 1.0, 7));
      this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false, 10));
   }

   public int getMaxSpawnClusterSize() {
      return 5;
   }

   public boolean isMaxGroupSizeReached(int sizeIn) {
      return false;
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new WaterBoundPathNavigation(this, worldIn);
   }

   public void travel(Vec3 travelVector) {
      if (this.isEffectiveAi() && this.isInWaterOrBubble()) {
         this.moveRelative(this.getSpeed(), travelVector);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 0.8, 0.9));
         if (this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
         }

         this.moveDistance = (float)(this.moveDistance + travelVector.horizontalDistance());
      } else {
         super.travel(travelVector);
      }
   }

   protected void playSwimSound(float f) {
      if (this.random.nextInt(2) == 0) {
         this.playSound(this.getSwimSound(), 0.2F, 1.3F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
      }
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.FISH_SWIM;
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

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.fromBucket() || this.isBaby() || this.fedCarrot;
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return !this.isBaby() && !this.fromBucket() && !this.fedCarrot;
   }

   protected void handleAirSupply(int i) {
      if (this.isAlive() && !this.isInWaterOrBubble()) {
         this.setAirSupply(i - 1);
         if (this.getAirSupply() == -20) {
            this.setAirSupply(0);
            this.hurt(this.damageSources().dryOut(), this.random.nextInt(2) == 0 ? 1.0F : 0.0F);
         }
      } else {
         this.setAirSupply(2000);
      }
   }

   public int getBabyAge() {
      return (Integer)this.entityData.get(BABY_AGE);
   }

   public void setBabyAge(int babyAge) {
      this.entityData.set(BABY_AGE, babyAge);
   }

   public float getTriopsScale() {
      return (Float)this.entityData.get(TRIOPS_SCALE);
   }

   public void setTriopsScale(float scale) {
      this.entityData.set(TRIOPS_SCALE, scale);
   }

   public boolean isBaby() {
      return this.getBabyAge() < 0;
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("FromBucket", this.fromBucket());
      compound.putBoolean("FedCarrot", this.fedCarrot);
      compound.putBoolean("Pregnant", this.pregnant);
      compound.putInt("BreedCooldown", this.breedCooldown);
      compound.putFloat("TriopsScale", this.getTriopsScale());
      compound.putInt("BabyAge", this.getBabyAge());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFromBucket(AMCompat.getBoolean(compound, "FromBucket"));
      this.fedCarrot = AMCompat.getBoolean(compound, "FedCarrot");
      this.pregnant = AMCompat.getBoolean(compound, "Pregnant");
      this.breedCooldown = AMCompat.getInt(compound, "BreedCooldown");
      this.setTriopsScale(AMCompat.getFloat(compound, "TriopsScale"));
      this.setBabyAge(AMCompat.getInt(compound, "BabyAge"));
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 4.0).add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      this.setTriopsScale(0.9F + this.random.nextFloat() * 0.2F);
      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
   }

   public void tick() {
      super.tick();
      this.prevOnLandProgress = this.onLandProgress;
      this.prevSwimRot = this.swimRot;
      this.prevTail1Yaw = this.tail1Yaw;
      this.prevTail2Yaw = this.tail2Yaw;
      boolean onLand = !this.isInWaterOrBubble() && this.onGround();
      this.setXRot(-((float)this.getDeltaMovement().y * 2.2F * 57.295776F));
      if (onLand && this.onLandProgress < 5.0F) {
         this.onLandProgress++;
      }

      if (!onLand && this.onLandProgress > 0.0F) {
         this.onLandProgress--;
      }

      if (this.breedCooldown > 0) {
         this.breedCooldown--;
      }

      this.tail1Yaw = Mth.approachDegrees(this.tail1Yaw, this.yBodyRot, 7.0F);
      this.tail2Yaw = Mth.approachDegrees(this.tail2Yaw, this.tail1Yaw, 7.0F);
      if (this.onLandProgress == 0.0F) {
         float f = (float)(20.0 * Math.sin(this.walkAnimation.position()) * this.walkAnimation.speed());
         this.swimRot = Mth.approachDegrees(this.swimRot, f, 2.0F);
      }
   }

   public void calculateEntityAnimation(boolean flying) {
      float f1 = (float)Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
      float f2 = Math.min(f1 * 6.0F, 1.0F);
      this.walkAnimation.update(f2, 0.4F);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      if (id == 67) {
         for (int i = 0; i < 5; i++) {
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(0.5), this.getY(0.800000011920929), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
         }
      } else if (id == 68) {
         this.level().addParticle(ParticleTypes.HEART, this.getX(), this.getY(0.800000011920929), this.getZ(), 0.0, 0.0, 0.0);
      } else {
         super.handleEntityEvent(id);
      }
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return (stack.is(AMTagRegistry.TRIOPS_BREEDABLES) || stack.is(AMItemRegistry.MOSQUITO_LARVA.get())) && !this.fedCarrot;
   }

   @Override
   public void onGetItem(ItemEntity e) {
      ItemStack stack = e.getItem();
      if (AMCompat.isEdible(stack.getItem()) && AMCompat.getFoodProperties(stack.getItem()) != null) {
         this.gameEvent(GameEvent.EAT);
         this.playSound(SoundEvents.CAT_EAT, this.getVoicePitch(), this.getSoundVolume());
         this.heal(5.0F);
         if (!this.level().isClientSide() && this.breedCooldown == 0 && !this.fedCarrot) {
            this.fedCarrot = true;
            this.level().broadcastEntityEvent(this, (byte)67);
         }
      }
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      InteractionResult type = super.mobInteract(player, hand);
      if (!type.consumesAction() && this.canTargetItem(itemstack) && !this.fedCarrot) {
         if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
         }

         this.gameEvent(GameEvent.EAT);
         this.playSound(SoundEvents.CAT_EAT, this.getVoicePitch(), this.getSoundVolume());
         this.heal(5.0F);
         if (itemstack.is(AMTagRegistry.TRIOPS_BREEDABLES)) {
            if (!this.level().isClientSide() && this.breedCooldown == 0) {
               this.level().broadcastEntityEvent(this, (byte)67);
            }

            this.fedCarrot = true;
         }

         return InteractionResult.SUCCESS;
      } else {
         return Bucketable.bucketMobPickup(player, hand, this).orElse(type);
      }
   }

   public boolean isSearchingForMate() {
      return this.isAlive() && this.isInWaterOrBubble() && this.fedCarrot && this.breedCooldown <= 0;
   }

   public void saveToBucketTag(@Nonnull ItemStack bucket) {
      if (this.hasCustomName()) {
         AMCompat.setHoverName(bucket, this.getCustomName());
      }

      CompoundTag platTag = new CompoundTag();
      AMCompat.saveAdditionalTo(this, platTag);
      CompoundTag compound = AMCompat.getOrCreateTag(bucket);
      AMCompat.put(compound, "TriopsTag", platTag);
      AMCompat.setTag(bucket, compound);
   }

   public void loadFromBucketTag(@Nonnull CompoundTag compound) {
      if (AMCompat.contains(compound, "TriopsTag")) {
         AMCompat.readAdditionalFrom(this, AMCompat.getCompound(compound, "TriopsTag"));
      }

      this.setAirSupply(2000);
   }

   public ItemStack getBucketItemStack() {
      ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.TRIOPS_BUCKET.get());
      if (this.hasCustomName()) {
         AMCompat.setHoverName(stack, this.getCustomName());
      }

      return stack;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.TRIOPS_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.TRIOPS_HURT.get();
   }

   private class BreedGoal extends Goal {
      private final Predicate<Entity> validBreedPartner;
      private EntityTriops breedPartner;
      private int executionCooldown = 50;

      public BreedGoal() {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.validBreedPartner = shrimp -> shrimp instanceof EntityTriops otherFish
            && otherFish.getId() != EntityTriops.this.getId()
            && otherFish.isSearchingForMate();
      }

      public boolean canUse() {
         if (EntityTriops.this.isInWaterOrBubble()
            && EntityTriops.this.fedCarrot
            && EntityTriops.this.breedCooldown <= 0
            && EntityTriops.this.breedWith == null) {
            if (this.executionCooldown > 0) {
               this.executionCooldown--;
            } else {
               this.executionCooldown = 50 + EntityTriops.this.random.nextInt(50);
               List<EntityTriops> list = EntityTriops.this.level()
                  .getEntitiesOfClass(
                     EntityTriops.class, EntityTriops.this.getBoundingBox().inflate(10.0, 8.0, 10.0), EntitySelector.NO_SPECTATORS.and(this.validBreedPartner)
                  );
               list.sort(Comparator.comparingDouble(EntityTriops.this::distanceToSqr));
               if (!list.isEmpty()) {
                  EntityTriops closestPupfish = list.get(0);
                  if (closestPupfish != null) {
                     this.breedPartner = closestPupfish;
                     this.breedPartner.breedWith = EntityTriops.this;
                     return true;
                  }
               }
            }

            return false;
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.breedPartner != null
            && !EntityTriops.this.pregnant
            && !this.breedPartner.pregnant
            && EntityTriops.this.breedWith == null
            && this.breedPartner.isSearchingForMate()
            && EntityTriops.this.isSearchingForMate();
      }

      public void start() {
      }

      public void stop() {
         EntityTriops.this.fedCarrot = false;
         EntityTriops.this.breedCooldown = 1200 + EntityTriops.this.random.nextInt(3600);
      }

      public void tick() {
         EntityTriops.this.getNavigation().moveTo(this.breedPartner, 1.0);
         this.breedPartner.getNavigation().moveTo(EntityTriops.this, 1.0);
         if (EntityTriops.this.distanceTo(this.breedPartner) < 1.2F) {
            EntityTriops.this.level().broadcastEntityEvent(EntityTriops.this, (byte)68);
            EntityTriops.this.pregnant = true;
         }
      }
   }

   class LayEggGoal extends Goal {
      private BlockPos eggPos;

      LayEggGoal() {
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public void stop() {
         this.eggPos = null;
      }

      public boolean canUse() {
         if (EntityTriops.this.pregnant && EntityTriops.this.getRandom().nextInt(30) == 0) {
            BlockPos egg = this.getEggLayPos();
            if (egg != null) {
               this.eggPos = egg;
               return true;
            }
         }

         return false;
      }

      public boolean canContinueToUse() {
         return this.eggPos != null && EntityTriops.this.pregnant && EntityTriops.this.level().getBlockState(this.eggPos).isAir();
      }

      public boolean isValidPos(BlockPos pos) {
         BlockState state = EntityTriops.this.level().getBlockState(pos);
         FluidState stateBelow = EntityTriops.this.level().getFluidState(pos.below());
         return stateBelow.is(FluidTags.WATER) && state.isAir();
      }

      public BlockPos getEggLayPos() {
         for (int i = 0; i < 10; i++) {
            BlockPos offset = EntityTriops.this.blockPosition()
               .offset(EntityTriops.this.getRandom().nextInt(10) - 5, 10, EntityTriops.this.getRandom().nextInt(10) - 5);

            while (EntityTriops.this.level().getBlockState(offset.below()).isAir() && offset.getY() > AMCompat.minBuildHeight(EntityTriops.this.level())) {
               offset = offset.below();
            }

            if (this.isValidPos(offset)) {
               return offset;
            }
         }

         return null;
      }

      public void tick() {
         super.tick();
         EntityTriops.this.getNavigation().moveTo(this.eggPos.getX(), this.eggPos.getY(), this.eggPos.getZ(), 1.0);
         if (EntityTriops.this.distanceToSqr(Vec3.atBottomCenterOf(this.eggPos)) < 2.0) {
            EntityTriops.this.pregnant = false;
            EntityTriops.this.level().setBlockAndUpdate(this.eggPos, AMBlockRegistry.TRIOPS_EGGS.get().defaultBlockState());
         }
      }
   }
}
