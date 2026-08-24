package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
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
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EntityCombJelly extends WaterAnimal implements Bucketable {
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityCombJelly.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> JELLYPITCH = SynchedEntityData.defineId(EntityCombJelly.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityCombJelly.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> JELLY_SCALE = SynchedEntityData.defineId(EntityCombJelly.class, EntityDataSerializers.FLOAT);
   public float prevOnLandProgress;
   public float onLandProgress;
   private BlockPos moveTarget;
   public float prevjellyPitch = 0.0F;
   public float spin;
   public float prevSpin;

   protected EntityCombJelly(EntityType<? extends WaterAnimal> animal, Level level) {
      super(animal, level);
   }

   public int getMaxSpawnClusterSize() {
      return 4;
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.hasCustomName() || this.fromBucket();
   }

   public boolean removeWhenFarAway(double p_213397_1_) {
      return !this.fromBucket() && !this.hasCustomName();
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.terrapinSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static boolean canCombJellySpawn(
      EntityType<EntityCombJelly> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return reason == MobSpawnType.SPAWNER || iServerWorld.isWaterAt(pos) && iServerWorld.isWaterAt(pos.above()) && isLightLevelOk(pos, iServerWorld);
   }

   private static boolean isLightLevelOk(BlockPos pos, ServerLevelAccessor iServerWorld) {
      float time = AMCompat.timeOfDay(iServerWorld, pos);
      int light = iServerWorld.getMaxLocalRawBrightness(pos);
      return light <= 4 && time > 0.27F && time <= 0.8F;
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(VARIANT, 0);
      builder.define(JELLYPITCH, 0.0F);
      builder.define(FROM_BUCKET, false);
      builder.define(JELLY_SCALE, 1.0F);
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.COMB_JELLY_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.COMB_JELLY_HURT.get();
   }

   public int getVariant() {
      return (Integer)this.entityData.get(VARIANT);
   }

   public void setVariant(int variant) {
      this.entityData.set(VARIANT, variant);
   }

   public float getJellyPitch() {
      return Mth.clamp((Float)this.entityData.get(JELLYPITCH), -90.0F, 90.0F);
   }

   public void setJellyPitch(float pitch) {
      this.entityData.set(JELLYPITCH, Mth.clamp(pitch, -90.0F, 90.0F));
   }

   public float getJellyScale() {
      return (Float)this.entityData.get(JELLY_SCALE);
   }

   public void setJellyScale(float scale) {
      this.entityData.set(JELLY_SCALE, scale);
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

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 6.0).add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   @Nonnull
   public ItemStack getBucketItemStack() {
      ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.COMB_JELLY_BUCKET.get());
      if (this.hasCustomName()) {
         AMCompat.setHoverName(stack, this.getCustomName());
      }

      return stack;
   }

   public void tick() {
      super.tick();
      this.prevOnLandProgress = this.onLandProgress;
      this.prevjellyPitch = this.getJellyPitch();
      this.prevSpin = this.spin;
      if (!this.isInWater() && this.onLandProgress < 5.0F) {
         this.onLandProgress++;
      }

      if (this.isInWater() && this.onLandProgress > 0.0F) {
         this.onLandProgress--;
      }

      if (!this.level().isClientSide()) {
         if (this.isInWater()) {
            this.setNoGravity(true);
            if (this.moveTarget == null
               || this.random.nextInt(120) == 0
               || this.distanceToSqr(this.moveTarget.getX() + 0.5F, this.moveTarget.getY() + 0.5F, this.moveTarget.getZ() + 0.5F) < 5.0
               || this.tickCount % 10 == 0 && !this.canBlockPosBeSeen(this.moveTarget)) {
               BlockPos randPos = this.blockPosition().offset(this.random.nextInt(10) - 5, this.random.nextInt(6) - 3, this.random.nextInt(10) - 5);
               if (this.level().getFluidState(randPos).is(Fluids.WATER) && this.level().getFluidState(randPos.above()).is(Fluids.WATER)) {
                  this.moveTarget = randPos;
               }
            }

            if (this.getFluidHeight(FluidTags.WATER) < this.getBbHeight()) {
               this.moveTarget = null;
               this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.02, 0.0));
            }

            if (this.moveTarget != null) {
               double d0 = this.moveTarget.getX() + 0.5F - this.getX();
               double d1 = this.moveTarget.getY() + 0.5F - this.getY();
               double d2 = this.moveTarget.getZ() + 0.5F - this.getZ();
               double d3 = Mth.sqrt((float)(d0 * d0 + d1 * d1 + d2 * d2));
               float f = (float)(Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
               this.setYRot(this.rotlerp(this.getYRot(), f, 1.0F));
               this.yBodyRot = this.getYRot();
               float movSpeed = 0.004F;
               Vec3 movingVec = new Vec3(d0 / d3, d1 / d3, d2 / d3).normalize();
               this.setDeltaMovement(this.getDeltaMovement().add(movingVec.scale(0.004000000189989805)));
            }

            float dist = (float)((Math.abs(this.getDeltaMovement().x()) + Math.abs(this.getDeltaMovement().z())) * 30.0);
            this.incrementJellyPitch(dist);
            if (this.horizontalCollision) {
               this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.20000000298023224, 0.0));
            }

            if (this.getJellyPitch() > 0.0F) {
               float decrease = Math.min(0.5F, this.getJellyPitch());
               this.decrementJellyPitch(decrease);
            }

            if (this.getJellyPitch() < 0.0F) {
               float decrease = Math.min(0.5F, -this.getJellyPitch());
               this.incrementJellyPitch(decrease);
            }
         } else {
            this.setNoGravity(false);
         }
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("FromBucket", this.fromBucket());
      compound.putFloat("JellyScale", this.getJellyScale());
      compound.putInt("Variant", this.getVariant());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFromBucket(AMCompat.getBoolean(compound, "FromBucket"));
      this.setJellyScale(AMCompat.getFloat(compound, "JellyScale"));
      this.setVariant(AMCompat.getInt(compound, "Variant"));
   }

   public boolean canBlockPosBeSeen(BlockPos pos) {
      double x = pos.getX() + 0.5F;
      double y = pos.getY() + 0.5F;
      double z = pos.getZ() + 0.5F;
      HitResult result = this.level().clip(new ClipContext(this.getEyePosition(), new Vec3(x, y, z), Block.COLLIDER, Fluid.NONE, this));
      double dist = result.getLocation().distanceToSqr(x, y, z);
      return dist <= 1.0 || result.getType() == Type.MISS;
   }

   public void travel(Vec3 travelVector) {
      if (this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), travelVector);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 0.6, 0.9));
      } else {
         super.travel(travelVector);
      }
   }

   @Nonnull
   protected InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
      return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
   }

   public void saveToBucketTag(@Nonnull ItemStack bucket) {
      if (this.hasCustomName()) {
         AMCompat.setHoverName(bucket, this.getCustomName());
      }

      Bucketable.saveDefaultDataToBucketTag(this, bucket);
      CompoundTag compoundnbt = AMCompat.getOrCreateTag(bucket);
      compoundnbt.putFloat("BucketScale", this.getJellyScale());
      compoundnbt.putInt("BucketVariantTag", this.getVariant());
      AMCompat.setTag(bucket, compoundnbt);
   }

   public void loadFromBucketTag(@Nonnull CompoundTag compound) {
      Bucketable.loadDefaultDataFromBucketTag(this, compound);
      if (AMCompat.contains(compound, "BucketScale")) {
         this.setJellyScale(AMCompat.getFloat(compound, "BucketScale"));
      }

      if (AMCompat.contains(compound, "BucketVariantTag")) {
         this.setVariant(AMCompat.getInt(compound, "BucketVariantTag"));
      }
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      this.setVariant(this.random.nextInt(3));
      this.setJellyScale(0.8F + this.random.nextFloat() * 0.4F);
      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   public void incrementJellyPitch(float pitch) {
      this.entityData.set(JELLYPITCH, this.getJellyPitch() + pitch);
   }

   public void decrementJellyPitch(float pitch) {
      this.entityData.set(JELLYPITCH, this.getJellyPitch() - pitch);
   }

   protected float rotlerp(float p_24992_, float p_24993_, float p_24994_) {
      float f = Mth.wrapDegrees(p_24993_ - p_24992_);
      if (f > p_24994_) {
         f = p_24994_;
      }

      if (f < -p_24994_) {
         f = -p_24994_;
      }

      float f1 = p_24992_ + f;
      if (f1 < 0.0F) {
         f1 += 360.0F;
      } else if (f1 > 360.0F) {
         f1 -= 360.0F;
      }

      return f1;
   }

   public boolean checkSpawnObstruction(LevelReader worldIn) {
      return worldIn.isUnobstructed(this);
   }
}
