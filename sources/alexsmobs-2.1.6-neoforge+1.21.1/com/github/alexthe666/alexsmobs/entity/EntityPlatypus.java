package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockReptileEgg;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalSwimMoveControllerSink;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.ai.PlatypusAIDigForItems;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class EntityPlatypus extends Animal implements ISemiAquatic, ITargetsDroppedItems, Bucketable {
   private static final EntityDataAccessor<Boolean> SENSING = SynchedEntityData.defineId(EntityPlatypus.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SENSING_VISUAL = SynchedEntityData.defineId(EntityPlatypus.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DIGGING = SynchedEntityData.defineId(EntityPlatypus.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> FEDORA = SynchedEntityData.defineId(EntityPlatypus.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityPlatypus.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(EntityPlatypus.class, EntityDataSerializers.BOOLEAN);
   public float prevInWaterProgress;
   public float inWaterProgress;
   public float prevDigProgress;
   public float digProgress;
   public boolean superCharged = false;
   private boolean isLandNavigator;
   private int swimTimer = -1000;

   protected EntityPlatypus(EntityType type, Level world) {
      super(type, world);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
      this.switchNavigator(false);
   }

   public static boolean canPlatypusSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      return worldIn.getBlockState(pos.below()).is(AMTagRegistry.PLATYPUS_SPAWNS) && pos.getY() < worldIn.getSeaLevel() + 4;
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.platypusSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 10.0)
         .add(Attributes.FOLLOW_RANGE, 16.0)
         .add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.PLATYPUS_BREEDABLES);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.PLATYPUS_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.PLATYPUS_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.PLATYPUS_HURT.get();
   }

   @Nonnull
   public ItemStack getBucketItemStack() {
      ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.PLATYPUS_BUCKET.get());
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
      AMCompat.put(compound, "PlatypusData", platTag);
      AMCompat.setTag(bucket, compound);
   }

   public void loadFromBucketTag(@Nonnull CompoundTag compound) {
      if (AMCompat.contains(compound, "PlatypusData")) {
         AMCompat.readAdditionalFrom(this, AMCompat.getCompound(compound, "PlatypusData"));
      }
   }

   @Nonnull
   public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      if (itemstack.getItem() == AMItemRegistry.FEDORA.get() && !this.hasFedora()) {
         if (!player.isCreative()) {
            itemstack.shrink(1);
         }

         this.setFedora(true);
         return AMCompat.sidedSuccess(this.level().isClientSide());
      } else if (itemstack.is(AMTagRegistry.PLATYPUS_CHARGEABLES) && !this.isSensing()) {
         this.superCharged = itemstack.is(AMTagRegistry.PLATYPUS_SUPER_CHARGEABLES);
         if (!player.isCreative()) {
            itemstack.shrink(1);
         }

         this.setSensing(true);
         return AMCompat.sidedSuccess(this.level().isClientSide());
      } else {
         return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
      }
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new BreathAirGoal(this));
      this.goalSelector.addGoal(1, new AnimalAIFindWater(this));
      this.goalSelector.addGoal(1, new AnimalAILeaveWater(this));
      this.goalSelector.addGoal(2, new EntityPlatypus.MateGoal(this, 1.0));
      this.goalSelector.addGoal(2, new EntityPlatypus.LayEggGoal(this, 1.0));
      this.goalSelector.addGoal(2, new BreedGoal(this, 0.8));
      this.goalSelector.addGoal(3, new PanicGoal(this, 1.1));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.0, AMCompat.ingredientOf(AMTagRegistry.PLATYPUS_CHARGEABLES), false) {
         public void start() {
            super.start();
            EntityPlatypus.this.setSensingVisual(true);
         }

         public boolean canUse() {
            return super.canUse() && !EntityPlatypus.this.isSensing();
         }

         public void stop() {
            super.stop();
            EntityPlatypus.this.setSensingVisual(false);
         }
      });
      this.goalSelector.addGoal(5, new TemptGoal(this, 1.1, AMCompat.ingredientOf(AMTagRegistry.PLATYPUS_FOODSTUFFS), false) {
         public boolean canUse() {
            return super.canUse() && !EntityPlatypus.this.isSensing();
         }
      });
      this.goalSelector.addGoal(5, new PlatypusAIDigForItems(this));
      this.goalSelector.addGoal(6, new SemiAquaticAIRandomSwimming(this, 1.0, 30));
      this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0, 60));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false, false, 40, 15) {
         @Override
         public boolean canUse() {
            return super.canUse() && !EntityPlatypus.this.isSensing();
         }

         @Override
         public boolean canContinueToUse() {
            return super.canContinueToUse() && !EntityPlatypus.this.isSensing();
         }
      });
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean prev = super.hurt(source, amount);
      if (prev && source.getDirectEntity() instanceof LivingEntity) {
         LivingEntity entity = (LivingEntity)source.getDirectEntity();
         entity.addEffect(new MobEffectInstance(MobEffects.POISON, 100));
      }

      return prev;
   }

   public boolean isPerry() {
      String s = ChatFormatting.stripFormatting(this.getName().getString());
      return s != null && s.toLowerCase().contains("perry");
   }

   public int getMaxAirSupply() {
      return 4800;
   }

   protected int increaseAirSupply(int currentAir) {
      return this.getMaxAirSupply();
   }

   public void spawnGroundEffects() {
      float radius = 0.3F;

      for (int i1 = 0; i1 < 3; i1++) {
         double motionX = this.getRandom().nextGaussian() * 0.07;
         double motionY = this.getRandom().nextGaussian() * 0.07;
         double motionZ = this.getRandom().nextGaussian() * 0.07;
         float angle = 0.017453292F * this.yBodyRot + i1;
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraY = 0.800000011920929;
         double extraZ = radius * Mth.cos(angle);
         BlockPos ground = this.getBlockPosBelowThatAffectsMyMovement();
         BlockState state = this.level().getBlockState(ground);
         if (state.isSolid() && this.level().isClientSide()) {
            AMCompat.addParticle(
               this.level(),
               new BlockParticleOption(ParticleTypes.BLOCK, state),
               true,
               this.getX() + extraX,
               ground.getY() + extraY,
               this.getZ() + extraZ,
               motionX,
               motionY,
               motionZ
            );
         }
      }
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      this.setAirSupply(this.getMaxAirSupply());
      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public void travel(Vec3 travelVector) {
      if (this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), travelVector);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
      } else {
         super.travel(travelVector);
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DIGGING, false);
      builder.define(SENSING, false);
      builder.define(SENSING_VISUAL, false);
      builder.define(FEDORA, false);
      builder.define(FROM_BUCKET, false);
      builder.define(HAS_EGG, false);
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (this.hasFedora()) {
         AMCompat.spawnAtLocation(this, (ItemLike)AMItemRegistry.FEDORA.get());
      }
   }

   public boolean isSensing() {
      return (Boolean)this.entityData.get(SENSING);
   }

   public void setSensing(boolean sensing) {
      this.entityData.set(SENSING, sensing);
   }

   public boolean isSensingVisual() {
      return (Boolean)this.entityData.get(SENSING_VISUAL);
   }

   public void setSensingVisual(boolean sensing) {
      this.entityData.set(SENSING_VISUAL, sensing);
   }

   public boolean hasFedora() {
      return (Boolean)this.entityData.get(FEDORA);
   }

   public void setFedora(boolean sensing) {
      this.entityData.set(FEDORA, sensing);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Fedora", this.hasFedora());
      compound.putBoolean("Sensing", this.isSensing());
      compound.putBoolean("FromBucket", this.fromBucket());
      compound.putBoolean("HasEgg", this.hasEgg());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFedora(AMCompat.getBoolean(compound, "Fedora"));
      this.setSensing(AMCompat.getBoolean(compound, "Sensing"));
      this.setFromBucket(AMCompat.getBoolean(compound, "FromBucket"));
      this.setHasEgg(AMCompat.getBoolean(compound, "HasEgg"));
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

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.fromBucket() || this.hasCustomName();
   }

   public boolean removeWhenFarAway(double dist) {
      return !this.fromBucket() && !this.requiresCustomPersistence();
   }

   public void tick() {
      super.tick();
      this.prevInWaterProgress = this.inWaterProgress;
      this.prevDigProgress = this.digProgress;
      boolean dig = this.isDigging() && this.isInWaterOrBubble();
      if (dig && this.digProgress < 5.0F) {
         this.digProgress++;
      }

      if (!dig && this.digProgress > 0.0F) {
         this.digProgress--;
      }

      if (this.isInWaterOrBubble()) {
         if (this.inWaterProgress < 5.0F) {
            this.inWaterProgress++;
         }

         if (this.isLandNavigator) {
            this.switchNavigator(false);
         }
      } else {
         if (this.inWaterProgress > 0.0F) {
            this.inWaterProgress--;
         }

         if (!this.isLandNavigator) {
            this.switchNavigator(true);
         }
      }

      if (this.onGround() && this.isDigging()) {
         this.spawnGroundEffects();
      }

      if (this.inWaterProgress > 0.0F) {
         AMCompat.setMaxUpStep(this, 1.0F);
      } else {
         AMCompat.setMaxUpStep(this, 0.6F);
      }

      if (!this.level().isClientSide()) {
         if (this.isInWater()) {
            this.swimTimer++;
         } else {
            this.swimTimer--;
         }
      }

      if (this.isAlive() && (this.isSensing() || this.isSensingVisual())) {
         for (int j = 0; j < 2; j++) {
            float radius = this.getBbWidth() * 0.65F;
            float angle = 0.017453292F * this.yBodyRot;
            double extraX = radius * (1.5F + this.random.nextFloat() * 0.3F) * Mth.sin(3.1415927F + angle) + (this.random.nextFloat() - 0.5F)
               + this.getDeltaMovement().x * 2.0;
            double extraZ = radius * (1.5F + this.random.nextFloat() * 0.3F) * Mth.cos(angle) + (this.random.nextFloat() - 0.5F)
               + this.getDeltaMovement().z * 2.0;
            double actualX = radius * Mth.sin(3.1415927F + angle);
            double actualZ = radius * Mth.cos(angle);
            double motX = actualX - extraX;
            double motZ = actualZ - extraZ;
            this.level()
               .addParticle(
                  (ParticleOptions)AMParticleRegistry.PLATYPUS_SENSE.get(),
                  this.getX() + extraX,
                  this.getBbHeight() * 0.3F + this.getY(),
                  this.getZ() + extraZ,
                  motX * 0.10000000149011612,
                  0.0,
                  motZ * 0.10000000149011612
               );
         }
      }
   }

   public boolean isDigging() {
      return (Boolean)this.entityData.get(DIGGING);
   }

   public void setDigging(boolean digging) {
      this.entityData.set(DIGGING, digging);
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigatorWide(this, this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new AnimalSwimMoveControllerSink(this, 1.2F, 1.6F);
         this.navigation = new SemiAquaticPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   @Override
   public boolean shouldEnterWater() {
      return (this.getLastHurtByMob() != null || this.swimTimer <= -1000 || this.isSensing()) && !this.hasEgg();
   }

   @Override
   public boolean shouldLeaveWater() {
      return this.swimTimer > 600 && !this.isSensing() || this.hasEgg();
   }

   @Override
   public boolean shouldStopMoving() {
      return this.isDigging();
   }

   @Override
   public int getWaterSearchRange() {
      return 10;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return AMCompat.create(AMEntityRegistry.PLATYPUS.get(), serverWorld);
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return !this.isSensing() && stack.is(AMTagRegistry.PLATYPUS_FOODSTUFFS);
   }

   @Override
   public void onGetItem(ItemEntity e) {
      this.gameEvent(GameEvent.EAT);
      this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
      if (e.getItem().is(AMTagRegistry.PLATYPUS_CHARGEABLES)) {
         this.superCharged = e.getItem().is(AMTagRegistry.PLATYPUS_SUPER_CHARGEABLES);
         this.setSensing(true);
      }
   }

   public boolean hasEgg() {
      return (Boolean)this.entityData.get(HAS_EGG);
   }

   private void setHasEgg(boolean hasEgg) {
      this.entityData.set(HAS_EGG, hasEgg);
   }

   static class LayEggGoal extends MoveToBlockGoal {
      private final EntityPlatypus turtle;
      private int digTime;

      LayEggGoal(EntityPlatypus turtle, double speedIn) {
         super(turtle, speedIn, 16);
         this.turtle = turtle;
      }

      public void stop() {
         this.digTime = 0;
      }

      public boolean canUse() {
         return this.turtle.hasEgg() && super.canUse();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.turtle.hasEgg();
      }

      public double acceptedDistance() {
         return this.turtle.getBbWidth() + 0.5;
      }

      public void tick() {
         super.tick();
         if (!this.turtle.isInWater() && this.isReachedTarget()) {
            BlockPos blockpos = this.turtle.blockPosition();
            Level world = this.turtle.level();
            this.turtle.gameEvent(GameEvent.BLOCK_PLACE);
            world.playSound(null, blockpos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + world.getRandom().nextFloat() * 0.2F);
            world.setBlock(
               this.blockPos.above(),
               (BlockState)AMBlockRegistry.PLATYPUS_EGG.get().defaultBlockState().setValue(BlockReptileEgg.EGGS, this.turtle.random.nextInt(3) + 1),
               3
            );
            this.turtle.setHasEgg(false);
            this.turtle.setDigging(false);
            this.turtle.setInLoveTime(600);
         }
      }

      protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
         return worldIn.isEmptyBlock(pos.above()) && BlockReptileEgg.isProperHabitat(worldIn, pos);
      }
   }

   static class MateGoal extends BreedGoal {
      private final EntityPlatypus platypus;

      MateGoal(EntityPlatypus platypus, double speedIn) {
         super(platypus, speedIn);
         this.platypus = platypus;
      }

      public boolean canUse() {
         return super.canUse() && !this.platypus.hasEgg();
      }

      protected void breed() {
         ServerPlayer serverplayerentity = this.animal.getLoveCause();
         if (serverplayerentity == null && this.partner.getLoveCause() != null) {
            serverplayerentity = this.partner.getLoveCause();
         }

         if (serverplayerentity != null) {
            serverplayerentity.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.animal, this.partner, this.animal);
         }

         this.platypus.setHasEgg(true);
         this.animal.resetLove();
         this.partner.resetLove();
         this.animal.setAge(6000);
         this.partner.setAge(6000);
         if (AMCompat.gameRule(this.level, AMCompat.Rule.MOB_LOOT)) {
            RandomSource random = this.animal.getRandom();
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
         }
      }
   }
}
