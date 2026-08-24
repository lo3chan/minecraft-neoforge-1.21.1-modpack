package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.SwimmerJumpPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class EntityFlyingFish extends WaterAnimal implements FlyingAnimal, Bucketable {
   private static final EntityDataAccessor<Boolean> GLIDING = SynchedEntityData.defineId(EntityFlyingFish.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityFlyingFish.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityFlyingFish.class, EntityDataSerializers.BOOLEAN);
   public float prevOnLandProgress;
   public float onLandProgress;
   public float prevFlyProgress;
   public float flyProgress;
   private int glideIn = this.random.nextInt(75) + 50;

   protected EntityFlyingFish(EntityType<? extends WaterAnimal> type, Level level) {
      super(type, level);
      this.moveControl = new AquaticMoveController(this, 1.0F, 15.0F);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
      this.goalSelector.addGoal(2, new EntityFlyingFish.GlideGoal(this));
      this.goalSelector.addGoal(3, new PanicGoal(this, 1.0));
      this.goalSelector.addGoal(4, new AnimalAIRandomSwimming(this, 1.0, 12, 5));
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.COD_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.COD_HURT;
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.fromBucket();
   }

   public boolean removeWhenFarAway(double p_27492_) {
      return !this.fromBucket() && !this.hasCustomName();
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new SwimmerJumpPathNavigator(this, worldIn);
   }

   public int getMaxSpawnClusterSize() {
      return 8;
   }

   public boolean isMaxGroupSizeReached(int sizeIn) {
      return false;
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FROM_BUCKET, false);
      builder.define(GLIDING, false);
      builder.define(VARIANT, 0);
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 6.0).add(Attributes.MOVEMENT_SPEED, 0.30000001192092896);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.flyingFishSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean prev = super.hurt(source, amount);
      if (prev && source.getEntity() != null) {
         double range = 15.0;
         this.glideIn = 0;

         for (EntityFlyingFish fsh : this.level().getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(range, range / 2.0, range))) {
            fsh.glideIn = 0;
         }
      }

      return prev;
   }

   public void tick() {
      super.tick();
      this.prevOnLandProgress = this.onLandProgress;
      this.prevFlyProgress = this.flyProgress;
      boolean onLand = !this.isInWaterOrBubble() && this.onGround();
      if (onLand && this.onLandProgress < 5.0F) {
         this.onLandProgress++;
      }

      if (!onLand && this.onLandProgress > 0.0F) {
         this.onLandProgress--;
      }

      if (this.isGliding()) {
         if (this.flyProgress < 5.0F) {
            this.flyProgress++;
         }

         if (!this.isInWaterOrBubble() && this.getDeltaMovement().y < 0.0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.5, 1.0));
         }
      } else if (this.flyProgress > 0.0F) {
         this.flyProgress--;
      }

      if (this.glideIn > 0) {
         this.glideIn--;
      }

      this.yBodyRot = this.getYRot();
      float f2 = (float)(-((float)this.getDeltaMovement().y * 3.0F * 57.2957763671875));
      if (this.isGliding()) {
         f2 = -f2;
      }

      this.setXRot(this.rotlerp(this.getXRot(), f2, 9.0F));
      if (!this.isInWaterOrBubble() && this.isAlive() && this.onGround() && this.random.nextFloat() < 0.05F) {
         this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
         this.setYRot(this.random.nextFloat() * 360.0F);
         this.playSound(SoundEvents.COD_FLOP, this.getSoundVolume(), this.getVoicePitch());
      }
   }

   protected float rotlerp(float current, float target, float maxChange) {
      float f = Mth.wrapDegrees(target - current);
      if (f > maxChange) {
         f = maxChange;
      }

      if (f < -maxChange) {
         f = -maxChange;
      }

      return current + f;
   }

   protected void handleAirSupply(int i) {
      if (this.isAlive() && !this.isInWaterOrBubble()) {
         this.setAirSupply(i - 1);
         if (this.getAirSupply() == -20) {
            this.setAirSupply(0);
            this.hurt(this.damageSources().dryOut(), 2.0F);
         }
      } else {
         this.setAirSupply(1000);
      }
   }

   public void travel(Vec3 travelVector) {
      if (this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), travelVector);
         this.move(MoverType.SELF, this.getDeltaMovement());
         float f = 0.6F;
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, f, 0.9));
         if (this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
         }
      } else {
         super.travel(travelVector);
      }
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.FISH_SWIM;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public int getVariant() {
      return (Integer)this.entityData.get(VARIANT);
   }

   public void setVariant(int variant) {
      this.entityData.set(VARIANT, variant);
   }

   public boolean isGliding() {
      return (Boolean)this.entityData.get(GLIDING);
   }

   public void setGliding(boolean flying) {
      this.entityData.set(GLIDING, flying);
   }

   private boolean canSeeBlock(BlockPos destinationBlock) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      Vec3 blockVec = Vec3.atCenterOf(destinationBlock);
      BlockHitResult result = this.level().clip(new ClipContext(Vector3d, blockVec, Block.COLLIDER, Fluid.NONE, this));
      return result.getBlockPos().equals(destinationBlock);
   }

   public boolean isFlying() {
      return true;
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

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("FromBucket", this.fromBucket());
      compound.putInt("Variant", this.getVariant());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFromBucket(AMCompat.getBoolean(compound, "FromBucket"));
      this.setVariant(AMCompat.getInt(compound, "Variant"));
   }

   @Nonnull
   public ItemStack getBucketItemStack() {
      ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.FLYING_FISH_BUCKET.get());
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
      CompoundTag compound = AMCompat.getOrCreateTag(bucket);
      compound.putInt("Variant", this.getVariant());
      AMCompat.setTag(bucket, compound);
   }

   public void loadFromBucketTag(@Nonnull CompoundTag compound) {
      Bucketable.loadDefaultDataFromBucketTag(this, compound);
      if (AMCompat.contains(compound, "Variant")) {
         this.setVariant(AMCompat.getInt(compound, "Variant"));
      }
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance diff, MobSpawnType spawnType, @Nullable SpawnGroupData data) {
      int i;
      if (data instanceof EntityFlyingFish.FlyingFishGroupData) {
         i = ((EntityFlyingFish.FlyingFishGroupData)data).variant;
      } else {
         i = this.random.nextInt(3);
         data = new EntityFlyingFish.FlyingFishGroupData(i);
      }

      this.setVariant(i);
      return super.finalizeSpawn(world, diff, spawnType, data);
   }

   @Nonnull
   protected InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
      return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
   }

   public static class FlyingFishGroupData extends AgeableMobGroupData {
      public final int variant;

      FlyingFishGroupData(int variant) {
         super(true);
         this.variant = variant;
      }
   }

   private class GlideGoal extends Goal {
      private final EntityFlyingFish fish;
      private final Level level;
      private BlockPos surface;
      private BlockPos glide;

      public GlideGoal(EntityFlyingFish fish) {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.fish = fish;
         this.level = fish.level();
      }

      public boolean canUse() {
         if (!this.fish.isInWaterOrBubble()) {
            return false;
         } else {
            if (this.fish.glideIn == 0 || this.fish.getRandom().nextInt(80) == 0) {
               BlockPos found = this.findSurfacePos();
               if (found != null) {
                  BlockPos glideTo = this.findGlideToPos(this.fish.blockPosition(), found);
                  if (glideTo != null) {
                     this.surface = found;
                     this.glide = glideTo;
                     this.fish.glideIn = 0;
                     return true;
                  }
               }
            }

            return false;
         }
      }

      private BlockPos findSurfacePos() {
         BlockPos fishPos = this.fish.blockPosition();

         for (int i = 0; i < 15; i++) {
            BlockPos offset = fishPos.offset(this.fish.random.nextInt(16) - 8, 0, this.fish.random.nextInt(16) - 8);

            while (this.level.isWaterAt(offset) && offset.getY() < AMCompat.maxBuildHeight(this.level)) {
               offset = offset.above();
            }

            if (!this.level.isWaterAt(offset) && this.level.isWaterAt(offset.below()) && this.fish.canSeeBlock(offset)) {
               return offset;
            }
         }

         return null;
      }

      private BlockPos findGlideToPos(BlockPos fishPos, BlockPos surface) {
         Vec3 sub = Vec3.atLowerCornerOf(surface.subtract(fishPos)).normalize();

         for (double scale = EntityFlyingFish.this.random.nextDouble() * 8.0 + 1.0; scale > 2.0; scale--) {
            Vec3 scaled = sub.scale(scale);
            BlockPos at = surface.offset((int)scaled.x, 0, (int)scaled.z);
            if (!this.level.isWaterAt(at) && this.level.isWaterAt(at.below()) && this.fish.canSeeBlock(at)) {
               return at;
            }
         }

         return null;
      }

      public boolean canContinueToUse() {
         return this.surface != null && this.glide != null && (!this.fish.onGround() || this.fish.isInWaterOrBubble());
      }

      public void start() {
      }

      public void stop() {
         this.surface = null;
         this.glide = null;
         this.fish.glideIn = EntityFlyingFish.this.random.nextInt(75) + 150;
         this.fish.setGliding(false);
      }

      public void tick() {
         if (this.fish.isInWaterOrBubble() && this.fish.distanceToSqr(Vec3.atCenterOf(this.surface)) > 3.0) {
            this.fish.getNavigation().moveTo(this.surface.getX() + 0.5F, this.surface.getY() + 1.0F, this.surface.getZ() + 0.5F, 1.2000000476837158);
            if (this.fish.isGliding()) {
               this.stop();
            }
         } else {
            this.fish.getNavigation().stop();
            Vec3 face = Vec3.atCenterOf(this.glide).subtract(Vec3.atCenterOf(this.surface));
            if (face.length() < 0.20000000298023224) {
               face = this.fish.getLookAngle();
            }

            Vec3 target = face.normalize().scale(0.10000000149011612);
            double y = 0.0;
            if (!this.fish.isGliding()) {
               y = 0.4F + EntityFlyingFish.this.random.nextFloat() * 0.2F;
            } else if (this.fish.isGliding() && this.fish.isInWaterOrBubble()) {
               this.stop();
            }

            Vec3 move = this.fish.getDeltaMovement().add(target.x, y, target.y);
            this.fish.setDeltaMovement(move);
            double d0 = move.horizontalDistance();
            this.fish.setXRot((float)(-Mth.atan2(move.y, d0) * 57.2957763671875));
            this.fish.setYRot((float)Mth.atan2(move.z, move.x) * 57.295776F - 90.0F);
            this.fish.setGliding(true);
         }
      }
   }
}
