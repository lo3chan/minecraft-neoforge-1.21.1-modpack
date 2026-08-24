package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.world.AMWorldData;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class EntityDevilsHolePupfish extends WaterAnimal implements FlyingAnimal, Bucketable {
   public static final ResourceLocation PUPFISH_REWARD = AMCompat.rl("alexsmobs", "gameplay/pupfish_reward");
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityDevilsHolePupfish.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> PUPFISH_SCALE = SynchedEntityData.defineId(EntityDevilsHolePupfish.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> FEEDING_TIME = SynchedEntityData.defineId(EntityDevilsHolePupfish.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> BABY_AGE = SynchedEntityData.defineId(EntityDevilsHolePupfish.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Optional<BlockPos>> FEEDING_POS = SynchedEntityData.defineId(
      EntityDevilsHolePupfish.class, EntityDataSerializers.OPTIONAL_BLOCK_POS
   );
   public float prevOnLandProgress;
   public float onLandProgress;
   public float prevFeedProgress;
   public float feedProgress;
   private EntityDevilsHolePupfish chasePartner;
   private int chaseTime = 0;
   private boolean chaseDriver;
   private boolean breedNextChase;
   private int chaseCooldown = 0;
   private int maxChaseTime = 300;

   protected EntityDevilsHolePupfish(EntityType<? extends WaterAnimal> type, Level level) {
      super(type, level);
      this.moveControl = new AquaticMoveController(this, 1.0F, 15.0F);
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new WaterBoundPathNavigation(this, worldIn);
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.DEVILS_HOLE_PUPFISH_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.DEVILS_HOLE_PUPFISH_HURT.get();
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
      this.goalSelector.addGoal(2, new EntityDevilsHolePupfish.EatMossGoal(this));
      this.goalSelector.addGoal(3, new EntityDevilsHolePupfish.ChaseGoal(this));
      this.goalSelector.addGoal(4, new PanicGoal(this, 1.0));
      this.goalSelector.addGoal(5, new AnimalAIRandomSwimming(this, 1.0, 12, 5));
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 2.0).add(Attributes.MOVEMENT_SPEED, 0.3400000035762787);
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.hasCustomName() || this.fromBucket();
   }

   public static boolean canPupfishSpawn(
      EntityType<EntityDevilsHolePupfish> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return reason == MobSpawnType.SPAWNER
         || isPupfishChunk(iServerWorld, pos) && iServerWorld.getFluidState(pos).is(FluidTags.WATER) && isInCave(iServerWorld, pos);
   }

   private static boolean isPupfishChunk(ServerLevelAccessor iServerWorld, BlockPos pos) {
      AMWorldData data = AMWorldData.get(iServerWorld.getLevel());
      return data != null && data.isInPupfishChunk(pos);
   }

   private static boolean isInCave(ServerLevelAccessor iServerWorld, BlockPos pos) {
      while (iServerWorld.getFluidState(pos).is(FluidTags.WATER)) {
         pos = pos.above();
      }

      return !iServerWorld.canSeeSky(pos) && pos.getY() < iServerWorld.getSeaLevel();
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.devilsHolePupfishSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public int getMaxSpawnClusterSize() {
      return 6;
   }

   public boolean isMaxGroupSizeReached(int sizeIn) {
      return false;
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FROM_BUCKET, false);
      builder.define(PUPFISH_SCALE, 1.0F);
      builder.define(FEEDING_TIME, 0);
      builder.define(BABY_AGE, 0);
      builder.define(FEEDING_POS, Optional.empty());
   }

   public void tick() {
      super.tick();
      this.prevOnLandProgress = this.onLandProgress;
      this.prevFeedProgress = this.feedProgress;
      if (this.chaseCooldown > 0) {
         this.chaseCooldown--;
      }

      boolean inWaterOrBubble = this.isInWaterOrBubble();
      if (!inWaterOrBubble && this.onLandProgress < 5.0F) {
         this.onLandProgress++;
      }

      if (inWaterOrBubble && this.onLandProgress > 0.0F) {
         this.onLandProgress--;
      }

      int feedingTime = this.getFeedingTime();
      if (feedingTime > 0 && this.feedProgress < 5.0F) {
         this.feedProgress++;
      }

      if (feedingTime <= 0 && this.feedProgress > 0.0F) {
         this.feedProgress--;
      }

      if (this.isBaby()) {
         this.setBabyAge(this.getBabyAge() + 1);
      }

      BlockPos feedingPos = (BlockPos)((Optional)this.entityData.get(FEEDING_POS)).orElse(null);
      if (feedingPos == null) {
         float f2 = (float)(-((float)this.getDeltaMovement().y * 2.2F * 57.2957763671875));
         this.setXRot(f2);
      } else if (this.getFeedingTime() > 0) {
         Vec3 face = Vec3.atCenterOf(feedingPos).subtract(this.position());
         double d0 = face.horizontalDistance();
         this.setXRot((float)(-Mth.atan2(face.y, d0) * 57.2957763671875));
         this.setYRot((float)Mth.atan2(face.z, face.x) * 57.295776F - 90.0F);
         this.yBodyRot = this.getYRot();
         this.yHeadRot = this.getYRot();
         BlockState state = this.level().getBlockState(feedingPos);
         if (this.random.nextInt(2) == 0 && !state.isAir()) {
            Vec3 mouth = new Vec3(0.0, this.getBbHeight() * 0.5F, 0.4F * this.getPupfishScale())
               .xRot(this.getXRot() * 0.017453292F)
               .yRot(-this.getYRot() * 0.017453292F);

            for (int i = 0; i < 4 + this.random.nextInt(2); i++) {
               double motX = this.random.nextGaussian() * 0.02;
               double motY = 0.1F + this.random.nextFloat() * 0.2F;
               double motZ = this.random.nextGaussian() * 0.02;
               this.level()
                  .addParticle(
                     new BlockParticleOption(ParticleTypes.BLOCK, state), this.getX() + mouth.x, this.getY() + mouth.y, this.getZ() + mouth.z, motX, motY, motZ
                  );
            }
         }
      }

      if (!this.isInWaterOrBubble() && this.isAlive() && this.onGround() && this.random.nextFloat() < 0.5F) {
         this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
         this.setYRot(this.random.nextFloat() * 360.0F);
         this.playSound(SoundEvents.COD_FLOP, this.getSoundVolume(), this.getVoicePitch());
      }
   }

   public EntityDimensions getDefaultDimensions(Pose poseIn) {
      return super.getDefaultDimensions(poseIn).scale(this.getPupfishScale());
   }

   public boolean fromBucket() {
      return (Boolean)this.entityData.get(FROM_BUCKET);
   }

   public void setFromBucket(boolean bucketed) {
      this.entityData.set(FROM_BUCKET, bucketed);
   }

   public void saveToBucketTag(@Nonnull ItemStack bucket) {
      if (this.hasCustomName()) {
         AMCompat.setHoverName(bucket, this.getCustomName());
      }

      Bucketable.saveDefaultDataToBucketTag(this, bucket);
      CompoundTag compound = AMCompat.getOrCreateTag(bucket);
      compound.putFloat("BucketScale", this.getPupfishScale());
      compound.putFloat("BabyAge", this.getBabyAge());
      AMCompat.setTag(bucket, compound);
   }

   public void loadFromBucketTag(@Nonnull CompoundTag compound) {
      Bucketable.loadDefaultDataFromBucketTag(this, compound);
      if (AMCompat.contains(compound, "BucketScale")) {
         this.setPupfishScale(AMCompat.getFloat(compound, "BucketScale"));
      }

      if (AMCompat.contains(compound, "BabyAge")) {
         this.setBabyAge(AMCompat.getInt(compound, "BabyAge"));
      }
   }

   @Nonnull
   public ItemStack getBucketItemStack() {
      ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.DEVILS_HOLE_PUPFISH_BUCKET.get());
      if (this.hasCustomName()) {
         AMCompat.setHoverName(stack, this.getCustomName());
      }

      return stack;
   }

   public SoundEvent getPickupSound() {
      return SoundEvents.BUCKET_FILL_FISH;
   }

   public float getPupfishScale() {
      return (Float)this.entityData.get(PUPFISH_SCALE);
   }

   public void setPupfishScale(float scale) {
      this.entityData.set(PUPFISH_SCALE, scale);
   }

   public int getFeedingTime() {
      return (Integer)this.entityData.get(FEEDING_TIME);
   }

   public void setFeedingTime(int feedingTime) {
      this.entityData.set(FEEDING_TIME, feedingTime);
   }

   public int getBabyAge() {
      return (Integer)this.entityData.get(BABY_AGE);
   }

   public void setBabyAge(int babyAge) {
      this.entityData.set(BABY_AGE, babyAge);
   }

   public boolean isBaby() {
      return this.getBabyAge() < 0;
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("FromBucket", this.fromBucket());
      compound.putBoolean("BreedNextChase", this.breedNextChase);
      compound.putFloat("PupfishScale", this.getPupfishScale());
      compound.putInt("BabyAge", this.getBabyAge());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFromBucket(AMCompat.getBoolean(compound, "FromBucket"));
      this.breedNextChase = AMCompat.getBoolean(compound, "BreedNextChase");
      this.setPupfishScale(AMCompat.getFloat(compound, "PupfishScale"));
      this.setBabyAge(AMCompat.getInt(compound, "BabyAge"));
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      this.setPupfishScale(0.65F + this.random.nextFloat() * 0.35F);
      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   protected void handleAirSupply(int i) {
      if (this.isAlive() && !this.isInWaterOrBubble()) {
         this.setAirSupply(i - 1);
         if (this.getAirSupply() == -20) {
            this.setAirSupply(0);
            this.hurt(this.damageSources().dryOut(), 2.0F);
         }
      } else {
         this.setAirSupply(this.getMaxAirSupply());
      }
   }

   public int getMaxAirSupply() {
      return 600;
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

   protected void playSwimSound(float f) {
      if (this.random.nextInt(2) == 0) {
         this.playSound(this.getSwimSound(), 0.2F, 1.3F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
      }
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.FISH_SWIM;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
   }

   public boolean isFlying() {
      return false;
   }

   private boolean canSeeBlock(BlockPos destinationBlock) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      Vec3 blockVec = Vec3.atCenterOf(destinationBlock);
      BlockHitResult result = this.level().clip(new ClipContext(Vector3d, blockVec, Block.COLLIDER, Fluid.NONE, this));
      return result.getBlockPos().equals(destinationBlock);
   }

   private static List<ItemStack> getFoodLoot(EntityDevilsHolePupfish pupfish) {
      LootTable loottable = AMCompat.lootTable(pupfish.level().getServer(), PUPFISH_REWARD);
      return loottable.getRandomItems(
         new net.minecraft.world.level.storage.loot.LootParams.Builder((ServerLevel)pupfish.level())
            .withParameter(LootContextParams.THIS_ENTITY, pupfish)
            .create(LootContextParamSets.PIGLIN_BARTER)
      );
   }

   public boolean removeWhenFarAway(double dist) {
      return !this.fromBucket() && !this.hasCustomName() && !this.isBaby();
   }

   private void spawnBabiesWith(EntityDevilsHolePupfish chasePartner) {
      EntityDevilsHolePupfish baby = AMCompat.create(AMEntityRegistry.DEVILS_HOLE_PUPFISH.get(), this.level());
      baby.copyPosition(this);
      baby.setPupfishScale(0.65F + this.random.nextFloat() * 0.35F);
      baby.setBabyAge(-24000);
      this.level().addFreshEntity(baby);
   }

   @Nonnull
   protected InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
      return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
   }

   private class ChaseGoal extends Goal {
      private final EntityDevilsHolePupfish pupfish;
      private final Predicate<Entity> validChasePartner;
      private int executionCooldown = 50;

      public ChaseGoal(EntityDevilsHolePupfish pupfish) {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.pupfish = pupfish;
         this.validChasePartner = pupfish1 -> pupfish1 instanceof EntityDevilsHolePupfish otherFish
            && otherFish.getId() != this.pupfish.getId()
            && otherFish.chasePartner == null
            && otherFish.chaseCooldown <= 0;
      }

      public boolean canUse() {
         if (!this.pupfish.isInWaterOrBubble() || this.pupfish.chaseTime > this.pupfish.maxChaseTime || this.pupfish.chaseCooldown > 0) {
            return false;
         } else if (this.pupfish.chasePartner != null && this.pupfish.chasePartner.isAlive()) {
            return true;
         } else {
            if (this.executionCooldown > 0) {
               this.executionCooldown--;
            } else {
               this.executionCooldown = 50 + EntityDevilsHolePupfish.this.random.nextInt(50);
               if (this.pupfish.chasePartner == null || !this.pupfish.chasePartner.isAlive()) {
                  List<EntityDevilsHolePupfish> list = this.pupfish
                     .level()
                     .getEntitiesOfClass(
                        EntityDevilsHolePupfish.class,
                        this.pupfish.getBoundingBox().inflate(10.0, 8.0, 10.0),
                        EntitySelector.NO_SPECTATORS.and(this.validChasePartner)
                     );
                  list.sort(Comparator.comparingDouble(this.pupfish::distanceToSqr));
                  if (!list.isEmpty()) {
                     EntityDevilsHolePupfish closestPupfish = list.get(0);
                     if (closestPupfish != null) {
                        this.pupfish.chasePartner = closestPupfish;
                        closestPupfish.chasePartner = this.pupfish;
                        this.pupfish.chaseDriver = true;
                        return true;
                     }
                  }

                  return false;
               }
            }

            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.pupfish.chasePartner != null && this.pupfish.chasePartner.isAlive() && this.pupfish.chaseTime < this.pupfish.maxChaseTime;
      }

      public void start() {
         this.pupfish.chaseDriver = !this.pupfish.chasePartner.chaseDriver;
         this.pupfish.chaseTime = 0;
         this.pupfish.maxChaseTime = 600;
      }

      public void stop() {
         this.pupfish.chaseTime = 0;
         this.pupfish.chaseCooldown = 100 + EntityDevilsHolePupfish.this.random.nextInt(100);
         this.executionCooldown = 50 + EntityDevilsHolePupfish.this.random.nextInt(20);
         if (this.pupfish.breedNextChase) {
            this.pupfish.spawnBabiesWith(this.pupfish.chasePartner);
            this.pupfish.chasePartner.breedNextChase = false;
            this.pupfish.breedNextChase = false;
         }

         this.pupfish.chasePartner = null;
      }

      public void tick() {
         this.pupfish.chaseTime++;
         if (this.pupfish.chasePartner != null && this.pupfish.chaseDriver) {
            float chaserSpeed = 1.2F + EntityDevilsHolePupfish.this.random.nextFloat() * 0.45F;
            float chasedSpeed = 0.2F + chaserSpeed * 0.7F;
            EntityDevilsHolePupfish flee = this.pupfish.chaseDriver ? this.pupfish.chasePartner : this.pupfish;
            EntityDevilsHolePupfish driver = this.pupfish.chaseDriver ? this.pupfish : this.pupfish.chasePartner;
            driver.getNavigation().moveTo(flee.getX(), flee.getY(0.5), flee.getZ(), chaserSpeed);
            Vec3 from = flee.position()
               .add(
                  EntityDevilsHolePupfish.this.random.nextFloat() - 0.5F,
                  EntityDevilsHolePupfish.this.random.nextFloat() - 0.5F,
                  EntityDevilsHolePupfish.this.random.nextFloat() - 0.5F
               )
               .subtract(driver.position())
               .normalize()
               .scale(2.0F + EntityDevilsHolePupfish.this.random.nextFloat() * 2.0F);
            Vec3 to = flee.position().add(from);
            flee.getNavigation().moveTo(to.x, to.y, to.z, chasedSpeed);
            if (EntityDevilsHolePupfish.this.random.nextInt(50) == 0) {
               this.pupfish.chaseDriver = !this.pupfish.chaseDriver;
               this.pupfish.chasePartner.chaseDriver = !this.pupfish.chasePartner.chaseDriver;
            }
         }
      }
   }

   private class EatMossGoal extends Goal {
      private final int searchLength;
      private final int verticalSearchRange;
      protected BlockPos destinationBlock;
      private final EntityDevilsHolePupfish pupfish;
      private int runDelay = 70;
      private int maxFeedTime = 200;

      private EatMossGoal(EntityDevilsHolePupfish pupfish) {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.pupfish = pupfish;
         this.searchLength = 16;
         this.verticalSearchRange = 6;
      }

      public boolean canContinueToUse() {
         return this.destinationBlock != null && this.isMossBlock(this.pupfish.level(), this.destinationBlock.mutable()) && this.isCloseToMoss(16.0);
      }

      public boolean isCloseToMoss(double dist) {
         return this.destinationBlock == null || this.pupfish.distanceToSqr(Vec3.atCenterOf(this.destinationBlock)) < dist * dist;
      }

      public boolean canUse() {
         if (!this.pupfish.isInWaterOrBubble()) {
            return false;
         } else if (this.runDelay > 0) {
            this.runDelay--;
            return false;
         } else {
            this.runDelay = 200 + this.pupfish.random.nextInt(150);
            return this.searchForDestination();
         }
      }

      public void start() {
         this.maxFeedTime = 60 + EntityDevilsHolePupfish.this.random.nextInt(60);
      }

      public void tick() {
         Vec3 vec = Vec3.atCenterOf(this.destinationBlock);
         if (vec != null) {
            this.pupfish.getNavigation().moveTo(vec.x, vec.y, vec.z, 1.0);
            if (this.pupfish.distanceToSqr(vec) < 1.149999976158142) {
               this.pupfish.entityData.set(EntityDevilsHolePupfish.FEEDING_POS, Optional.of(this.destinationBlock));
               Vec3 face = vec.subtract(this.pupfish.position());
               this.pupfish.setDeltaMovement(this.pupfish.getDeltaMovement().add(face.normalize().scale(0.10000000149011612)));
               this.pupfish.setFeedingTime(this.pupfish.getFeedingTime() + 1);
               if (this.pupfish.getFeedingTime() > this.maxFeedTime) {
                  this.destinationBlock = null;
                  if (EntityDevilsHolePupfish.this.random.nextInt(3) == 0) {
                     List<ItemStack> lootList = EntityDevilsHolePupfish.getFoodLoot(this.pupfish);
                     if (!lootList.isEmpty()) {
                        for (ItemStack stack : lootList) {
                           ItemEntity e = AMCompat.spawnAtLocation(this.pupfish, stack.copy());
                           e.hasImpulse = true;
                           e.setDeltaMovement(e.getDeltaMovement().multiply(0.2, 0.2, 0.2));
                        }
                     }
                  }

                  if (EntityDevilsHolePupfish.this.random.nextInt(3) == 0 && !this.pupfish.isBaby()) {
                     this.pupfish.breedNextChase = true;
                  }
               }
            } else {
               this.pupfish.entityData.set(EntityDevilsHolePupfish.FEEDING_POS, Optional.empty());
            }
         }
      }

      public void stop() {
         this.pupfish.entityData.set(EntityDevilsHolePupfish.FEEDING_POS, Optional.empty());
         this.destinationBlock = null;
         this.pupfish.setFeedingTime(0);
      }

      protected boolean searchForDestination() {
         int lvt_1_1_ = this.searchLength;
         BlockPos lvt_3_1_ = this.pupfish.blockPosition();
         MutableBlockPos lvt_4_1_ = new MutableBlockPos();

         for (int lvt_5_1_ = -8; lvt_5_1_ <= 2; lvt_5_1_++) {
            for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_1_1_; lvt_6_1_++) {
               for (int lvt_7_1_ = 0; lvt_7_1_ <= lvt_6_1_; lvt_7_1_ = lvt_7_1_ > 0 ? -lvt_7_1_ : 1 - lvt_7_1_) {
                  for (int lvt_8_1_ = lvt_7_1_ < lvt_6_1_ && lvt_7_1_ > -lvt_6_1_ ? lvt_6_1_ : 0;
                     lvt_8_1_ <= lvt_6_1_;
                     lvt_8_1_ = lvt_8_1_ > 0 ? -lvt_8_1_ : 1 - lvt_8_1_
                  ) {
                     lvt_4_1_.setWithOffset(lvt_3_1_, lvt_7_1_, lvt_5_1_ - 1, lvt_8_1_);
                     if (this.isMossBlock(this.pupfish.level(), lvt_4_1_) && this.pupfish.canSeeBlock(lvt_4_1_)) {
                        this.destinationBlock = lvt_4_1_;
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      }

      private boolean isMossBlock(Level world, MutableBlockPos pos) {
         return world.getBlockState(pos).is(AMTagRegistry.PUPFISH_EATABLES);
      }
   }
}
