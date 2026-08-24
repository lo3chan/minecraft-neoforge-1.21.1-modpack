package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFollowParentRanged;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalSwimMoveControllerSink;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowBoatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.entity.PartEntity;

public class EntityCachalotWhale extends Animal implements IMultipartOwner {
   private static final TargetingConditions REWARD_PLAYER_PREDICATE = TargetingConditions.forNonCombat().range(50.0).ignoreLineOfSight();
   private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(EntityCachalotWhale.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(EntityCachalotWhale.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> BEACHED = SynchedEntityData.defineId(EntityCachalotWhale.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> ALBINO = SynchedEntityData.defineId(EntityCachalotWhale.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DESPAWN_BEACH = SynchedEntityData.defineId(EntityCachalotWhale.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> GRABBING = SynchedEntityData.defineId(EntityCachalotWhale.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> HOLDING_SQUID_LEFT = SynchedEntityData.defineId(EntityCachalotWhale.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> CAUGHT_ID = SynchedEntityData.defineId(EntityCachalotWhale.class, EntityDataSerializers.INT);
   public final double[][] ringBuffer = new double[64][3];
   public final EntityCachalotPart headPart;
   public final EntityCachalotPart bodyFrontPart;
   public final EntityCachalotPart bodyPart;
   public final EntityCachalotPart tail1Part;
   public final EntityCachalotPart tail2Part;
   public final EntityCachalotPart tail3Part;
   public final EntityCachalotPart[] whaleParts;
   private final boolean hasAlbinoAttribute = false;
   public int ringBufferIndex = -1;
   public float prevChargingProgress;
   public float chargeProgress;
   public float prevSleepProgress;
   public float sleepProgress;
   public float prevBeachedProgress;
   public float beachedProgress;
   public float prevGrabProgress;
   public float grabProgress;
   public int grabTime = 0;
   private boolean receivedEcho = false;
   private boolean waitForEchoFlag = true;
   private int echoTimer = 0;
   private boolean prevEyesInWater = false;
   private int spoutTimer = 0;
   private int chargeCooldown = 0;
   private float whaleSpeedMod = 1.0F;
   private int rewardTime = 0;
   private Player rewardPlayer;
   private int blockBreakCounter;
   private int despawnDelay = 47999;
   private int echoSoundCooldown = 0;
   private boolean hasRewardedPlayer = false;

   public boolean isFood(ItemStack stack) {
      return stack.is(Items.WHEAT);
   }

   public EntityCachalotWhale(EntityType type, Level world) {
      super(type, world);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.moveControl = new AnimalSwimMoveControllerSink(this, 1.0F, 1.0F, 6.0F);
      this.lookControl = new SmoothSwimmingLookControl(this, 4);
      this.headPart = new EntityCachalotPart(this, 3.0F, 3.5F);
      this.bodyFrontPart = new EntityCachalotPart(this, 4.0F, 4.0F);
      this.bodyPart = new EntityCachalotPart(this, 5.0F, 4.0F);
      this.tail1Part = new EntityCachalotPart(this, 4.0F, 3.0F);
      this.tail2Part = new EntityCachalotPart(this, 3.0F, 2.0F);
      this.tail3Part = new EntityCachalotPart(this, 3.0F, 0.7F);
      this.whaleParts = new EntityCachalotPart[]{this.headPart, this.bodyFrontPart, this.bodyPart, this.tail1Part, this.tail2Part, this.tail3Part};
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 160.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.MOVEMENT_SPEED, 1.2000000476837158)
         .add(Attributes.ATTACK_DAMAGE, 30.0);
   }

   public static <T extends Mob> boolean canCachalotWhaleSpawn(
      EntityType<T> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      BlockPos up = pos;

      while (up.getY() < AMCompat.maxBuildHeight(iServerWorld) && iServerWorld.getFluidState(up).is(FluidTags.WATER)) {
         up = up.above();
      }

      return iServerWorld.getFluidState(up.below()).is(FluidTags.WATER) && up.getY() < iServerWorld.getSeaLevel() + 15 && iServerWorld.canSeeSky(up);
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return !this.isSleeping() && !this.isCharging() && !this.isDespawnBeach() && !this.isAlbino();
   }

   private boolean canDespawn() {
      return this.isDespawnBeach();
   }

   private void tryDespawn() {
      if (this.canDespawn()) {
         this.despawnDelay--;
         if (this.despawnDelay <= 0) {
            this.dropLeash(true, false);
            this.remove(RemovalReason.DISCARDED);
         }
      }
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.CACHALOT_WHALE_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.CACHALOT_WHALE_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.CACHALOT_WHALE_HURT.get();
   }

   public void scaleParts() {
      for (EntityCachalotPart parts : this.whaleParts) {
         float prev = parts.scale;
         parts.scale = this.isBaby() ? 0.5F : 1.0F;
         if (prev != parts.scale) {
            parts.refreshDimensions();
         }
      }
   }

   public boolean isPickable() {
      return true;
   }

   public void pushEntities() {
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      return super.mobInteract(player, hand);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Albino", this.isAlbino());
      compound.putBoolean("Beached", this.isBeached());
      compound.putBoolean("BeachedDespawnFlag", this.isDespawnBeach());
      compound.putBoolean("GivenReward", this.hasRewardedPlayer);
      compound.putInt("DespawnDelay", this.despawnDelay);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setAlbino(AMCompat.getBoolean(compound, "Albino"));
      this.setBeached(AMCompat.getBoolean(compound, "Beached"));
      this.setDespawnBeach(AMCompat.getBoolean(compound, "BeachedDespawnFlag"));
      if (AMCompat.contains(compound, "DespawnDelay", 99)) {
         this.despawnDelay = AMCompat.getInt(compound, "DespawnDelay");
      }

      this.hasRewardedPlayer = AMCompat.getBoolean(compound, "GivenReward");
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CHARGING, false);
      builder.define(SLEEPING, false);
      builder.define(BEACHED, false);
      builder.define(ALBINO, false);
      builder.define(GRABBING, false);
      builder.define(HOLDING_SQUID_LEFT, false);
      builder.define(DESPAWN_BEACH, false);
      builder.define(CAUGHT_ID, -1);
   }

   public boolean hasCaughtSquid() {
      return (Integer)this.entityData.get(CAUGHT_ID) != -1;
   }

   private void setCaughtSquidId(int i) {
      this.entityData.set(CAUGHT_ID, i);
   }

   @Nullable
   public Entity getCaughtSquid() {
      return !this.hasCaughtSquid() ? null : this.level().getEntity((Integer)this.entityData.get(CAUGHT_ID));
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new EntityCachalotWhale.AIBreathe());
      this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(3, new AnimalAIFollowParentRanged(this, 1.100000023841858, 32.0F, 10.0F));
      this.goalSelector.addGoal(4, new AnimalAIRandomSwimming(this, 0.6, 10, 24, true) {
         @Override
         public boolean canUse() {
            return !EntityCachalotWhale.this.isSleeping() && !EntityCachalotWhale.this.isBeached() && super.canUse();
         }
      });
      this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 20.0F));
      this.goalSelector.addGoal(7, new FollowBoatGoal(this));
      this.targetSelector.addGoal(1, new AnimalAIHurtByTargetNotBaby(this).setAlertOthers(new Class[0]));
      this.targetSelector
         .addGoal(
            2,
            new EntityAINearestTarget3D(this, LivingEntity.class, 30, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.CACHALOT_WHALE_TARGETS)) {
               public boolean canUse() {
                  return !EntityCachalotWhale.this.isSleeping() && !EntityCachalotWhale.this.isBeached() && super.canUse();
               }
            }
         );
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new WaterBoundPathNavigation(this, worldIn);
   }

   public void customServerAiStep() {
      super.customServerAiStep();
      this.breakBlock();
   }

   public void breakBlock() {
      if (this.blockBreakCounter > 0) {
         this.blockBreakCounter--;
      } else {
         boolean flag = false;
         if (!this.level().isClientSide() && this.blockBreakCounter == 0 && AMPlatform.mobGriefing(this.level(), this)) {
            TagKey<Block> breakables = this.isCharging() && this.getTarget() != null && AMConfig.cachalotDestruction
               ? AMTagRegistry.CACHALOT_WHALE_BREAKABLES
               : AMTagRegistry.ORCA_BREAKABLES;

            for (int a = (int)Math.round(this.getBoundingBox().minX); a <= (int)Math.round(this.getBoundingBox().maxX); a++) {
               for (int b = (int)Math.round(this.getBoundingBox().minY) - 1; b <= (int)Math.round(this.getBoundingBox().maxY) + 1 && b <= 127; b++) {
                  for (int c = (int)Math.round(this.getBoundingBox().minZ); c <= (int)Math.round(this.getBoundingBox().maxZ); c++) {
                     BlockPos pos = new BlockPos(a, b, c);
                     BlockState state = this.level().getBlockState(pos);
                     FluidState fluidState = this.level().getFluidState(pos);
                     if (!state.isAir() && !state.getShape(this.level(), pos).isEmpty() && state.is(breakables) && fluidState.isEmpty()) {
                        Block block = state.getBlock();
                        if (block != Blocks.AIR) {
                           this.setDeltaMovement(this.getDeltaMovement().multiply(0.6000000238418579, 1.0, 0.6000000238418579));
                           flag = true;
                           this.level().destroyBlock(pos, true);
                           if (state.is(BlockTags.ICE)) {
                              this.level().setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
                           }
                        }
                     }
                  }
               }
            }
         }

         if (flag) {
            this.blockBreakCounter = this.isCharging() && this.getTarget() != null ? 2 : 20;
         }
      }
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

   private void spawnSpoutParticles() {
      if (this.isAlive()) {
         float radius = this.headPart.getBbWidth() * 0.5F;

         for (int j = 0; j < 5 + this.random.nextInt(4); j++) {
            float angle = 0.017453292F * this.yBodyRot;
            double extraX = radius * (1.0F + this.random.nextFloat() * 0.13F) * Mth.sin(3.1415927F + angle) + (this.random.nextFloat() - 0.5F)
               + this.getDeltaMovement().x * 2.0;
            double extraZ = radius * (1.0F + this.random.nextFloat() * 0.13F) * Mth.cos(angle) + (this.random.nextFloat() - 0.5F)
               + this.getDeltaMovement().z * 2.0;
            double motX = this.random.nextGaussian();
            double motZ = this.random.nextGaussian();
            this.level()
               .addParticle(
                  (ParticleOptions)AMParticleRegistry.WHALE_SPLASH.get(),
                  this.headPart.getX() + extraX,
                  this.headPart.getY() + this.headPart.getBbHeight(),
                  this.headPart.getZ() + extraZ,
                  motX * 0.10000000149011612 + this.getDeltaMovement().x,
                  2.0,
                  motZ * 0.10000000149011612 + this.getDeltaMovement().z
               );
         }
      }
   }

   public boolean isCharging() {
      return (Boolean)this.entityData.get(CHARGING);
   }

   public void setCharging(boolean charging) {
      this.entityData.set(CHARGING, charging);
   }

   public boolean isSleeping() {
      return (Boolean)this.entityData.get(SLEEPING);
   }

   public void setSleeping(boolean charging) {
      this.entityData.set(SLEEPING, charging);
   }

   public boolean isBeached() {
      return (Boolean)this.entityData.get(BEACHED);
   }

   public void setBeached(boolean charging) {
      this.entityData.set(BEACHED, charging);
   }

   public boolean isGrabbing() {
      return (Boolean)this.entityData.get(GRABBING);
   }

   public void setGrabbing(boolean charging) {
      this.entityData.set(GRABBING, charging);
   }

   public boolean isHoldingSquidLeft() {
      return (Boolean)this.entityData.get(HOLDING_SQUID_LEFT);
   }

   public void setHoldingSquidLeft(boolean charging) {
      this.entityData.set(HOLDING_SQUID_LEFT, charging);
   }

   public boolean isAlbino() {
      return (Boolean)this.entityData.get(ALBINO);
   }

   public void setAlbino(boolean albino) {
      boolean prev = this.isAlbino();
      if (!prev && albino) {
         this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(230.0);
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(45.0);
         this.setHealth(230.0F);
      } else {
         this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(160.0);
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(30.0);
      }

      this.entityData.set(ALBINO, albino);
   }

   public boolean isDespawnBeach() {
      return (Boolean)this.entityData.get(DESPAWN_BEACH);
   }

   public void setDespawnBeach(boolean despawn) {
      this.entityData.set(DESPAWN_BEACH, despawn);
   }

   protected float getSoundVolume() {
      return this.isSilent() ? 0.0F : (float)AMConfig.cachalotVolume;
   }

   public void aiStep() {
      super.aiStep();
      this.scaleParts();
      if (this.echoSoundCooldown > 0) {
         this.echoSoundCooldown--;
      }

      if (this.isSleeping()) {
         this.getNavigation().stop();
         this.setXRot(-90.0F);
         this.whaleSpeedMod = 0.0F;
         if (this.isEyeInFluid(FluidTags.WATER) && this.getAirSupply() < 200) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.06, 0.0));
         } else {
            BlockPos waterPos = this.blockPosition();

            while (this.level().getFluidState(waterPos).is(FluidTags.WATER) && waterPos.getY() < 255) {
               waterPos = waterPos.above();
            }

            if (waterPos.getY() - this.getY() < (this.isBaby() ? 7 : 12)) {
               this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.06, 0.0));
            }

            if (this.random.nextInt(100) == 0) {
               this.setDeltaMovement(this.getDeltaMovement().add(0.0, this.random.nextGaussian() * 0.06, 0.0));
            }
         }
      } else if (this.whaleSpeedMod == 0.0F) {
         this.whaleSpeedMod = 1.0F;
      }

      float rPitch = -((float)this.getDeltaMovement().y * 57.295776F);
      if (this.isGrabbing()) {
         this.setXRot(0.0F);
      } else {
         this.setXRot(Mth.clamp(rPitch, -90.0F, 90.0F));
      }

      if (this.onGround() && !this.isInWaterOrBubble()) {
         this.setBeached(true);
         this.setXRot(0.0F);
         this.setSleeping(false);
      }

      if (this.isBeached()) {
         this.whaleSpeedMod = 0.0F;
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 1.0, 0.5));
         if (this.isEyeInFluid(FluidTags.WATER)) {
            Player entity = AMCompat.getNearestPlayer(this.level(), REWARD_PLAYER_PREDICATE, this);
            if (this.getLastHurtByMob() != entity) {
               this.rewardPlayer = entity;
            }

            this.despawnDelay = 47999;
            this.setBeached(false);
         }
      }

      if (this.rewardPlayer != null && !this.hasRewardedPlayer && this.isInWaterOrBubble()) {
         double d0 = this.rewardPlayer.getX() - this.getX();
         double d1 = this.rewardPlayer.getEyeY() - this.getEyeY();
         double d2 = this.rewardPlayer.getZ() - this.getZ();
         double d3 = Mth.sqrt((float)(d0 * d0 + d2 * d2));
         float targetYaw = (float)(Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
         float targetPitch = (float)(-(Mth.atan2(d1, d3) * 57.2957763671875));
         this.setYRot(this.getYRot() + Mth.clamp(targetYaw - this.getYRot(), -2.0F, 2.0F));
         this.setXRot(this.getXRot() + Mth.clamp(targetPitch - this.getXRot(), -2.0F, 2.0F));
         this.yBodyRot = this.getYRot();
         this.whaleSpeedMod = 0.1F;
         this.getMoveControl().setWantedPosition(this.rewardPlayer.getX(), this.rewardPlayer.getY(), this.rewardPlayer.getZ(), 0.5);
         if (this.distanceTo(this.rewardPlayer) < 10.0F) {
            if (!this.level().isClientSide()) {
               Vec3 vec = this.getMouthVec();
               ItemEntity itementity = new ItemEntity(
                  this.level(), vec.x, vec.y, vec.z, new ItemStack((ItemLike)AMItemRegistry.AMBERGRIS.get(), 2 + this.random.nextInt(2))
               );
               itementity.setDefaultPickUpDelay();
               this.level().addFreshEntity(itementity);
            }

            this.hasRewardedPlayer = true;
            this.rewardPlayer = null;
         }
      }

      this.prevChargingProgress = this.chargeProgress;
      this.prevSleepProgress = this.sleepProgress;
      this.prevBeachedProgress = this.beachedProgress;
      this.prevGrabProgress = this.grabProgress;
      if (this.tickCount % 200 == 0) {
         this.heal(2.0F);
      }

      if (this.isCharging()) {
         if (this.chargeProgress < 10.0F) {
            this.chargeProgress++;
         }
      } else if (this.chargeProgress > 0.0F) {
         this.chargeProgress--;
      }

      if (this.isSleeping()) {
         if (this.sleepProgress < 10.0F) {
            this.sleepProgress++;
         }
      } else if (this.sleepProgress > 0.0F) {
         this.sleepProgress--;
      }

      if (this.isBeached()) {
         if (this.beachedProgress < 10.0F) {
            this.beachedProgress++;
         }
      } else if (this.beachedProgress > 0.0F) {
         this.beachedProgress--;
      }

      if (this.isGrabbing()) {
         if (this.grabProgress < 10.0F) {
            this.grabProgress++;
         }

         this.grabTime++;
      } else {
         if (this.grabProgress > 0.0F) {
            this.grabProgress--;
         }

         this.grabTime = 0;
      }

      this.yHeadRot = this.getYRot();
      this.yBodyRot = this.getYRot();
      if (!this.isNoAi()) {
         if (this.ringBufferIndex < 0) {
            for (int i = 0; i < this.ringBuffer.length; i++) {
               this.ringBuffer[i][0] = this.getYRot();
               this.ringBuffer[i][1] = this.getY();
            }
         }

         this.ringBufferIndex++;
         if (this.ringBufferIndex == this.ringBuffer.length) {
            this.ringBufferIndex = 0;
         }

         this.ringBuffer[this.ringBufferIndex][0] = this.getYRot();
         this.ringBuffer[this.ringBufferIndex][1] = this.getY();
         Vec3[] avector3d = new Vec3[this.whaleParts.length];

         for (int j = 0; j < this.whaleParts.length; j++) {
            this.whaleParts[j].collideWithNearbyEntities();
            avector3d[j] = new Vec3(this.whaleParts[j].getX(), this.whaleParts[j].getY(), this.whaleParts[j].getZ());
         }

         float f15 = (float)(this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F * 0.017453292F;
         float f16 = Mth.cos(f15);
         float f17 = this.getYRot() * 0.017453292F;
         float pitch = this.getXRot() * 0.017453292F;
         float xRotDiv90 = Math.abs(this.getXRot() / 90.0F);
         float f3 = Mth.sin(f17) * (1.0F - xRotDiv90);
         float f18 = Mth.cos(f17) * (1.0F - xRotDiv90);
         this.setPartPosition(this.bodyPart, f3 * 0.5F, -pitch * 0.5F, -f18 * 0.5F);
         this.setPartPosition(this.bodyFrontPart, f3 * -3.5F, -pitch * 3.0F, f18 * 3.5F);
         this.setPartPosition(this.headPart, f3 * -7.0F, -pitch * 5.0F, -f18 * -7.0F);
         double[] adouble = this.getMovementOffsets(5, 1.0F);

         for (int k = 0; k < 3; k++) {
            EntityCachalotPart enderdragonpartentity;
            if (k == 0) {
               enderdragonpartentity = this.tail1Part;
            } else if (k == 1) {
               enderdragonpartentity = this.tail2Part;
            } else {
               enderdragonpartentity = this.tail3Part;
            }

            double[] adouble1 = this.getMovementOffsets(15 + k * 5, 1.0F);
            float f7 = this.getYRot() * 0.017453292F + (float)Mth.wrapDegrees(adouble1[0] - adouble[0]) * 0.017453292F;
            float f19 = 1.0F - Math.abs(this.getXRot() / 90.0F);
            float f20 = Mth.sin(f7) * f19;
            float f21 = Mth.cos(f7) * f19;
            float f22 = -3.6F;
            float f23 = (k + 1) * -3.6F - 2.0F;
            this.setPartPosition(enderdragonpartentity, -(f3 * 0.5F + f20 * f23) * f16, pitch * 1.5F * (k + 1), (f18 * 0.5F + f21 * f23) * f16);
         }

         for (int l = 0; l < this.whaleParts.length; l++) {
            this.whaleParts[l].xo = avector3d[l].x;
            this.whaleParts[l].yo = avector3d[l].y;
            this.whaleParts[l].zo = avector3d[l].z;
            this.whaleParts[l].xOld = avector3d[l].x;
            this.whaleParts[l].yOld = avector3d[l].y;
            this.whaleParts[l].zOld = avector3d[l].z;
         }
      }

      if (!this.level().isClientSide()) {
         LivingEntity target = this.getTarget();
         if (target != null && target.isAlive()) {
            if (!this.isBeached() && !this.isSleeping() && this.rewardPlayer == null) {
               if (this.isGrabbing() && this.getTarget().isAlive()) {
                  this.setCaughtSquidId(this.getTarget().getId());
                  this.whaleSpeedMod = 0.1F;
                  float scale = this.isBaby() ? 0.5F : 1.0F;
                  float offsetAngle = -((float)Math.cos(this.grabTime * 0.3F)) * 0.1F * this.grabProgress;
                  float renderYaw = (float)this.getMovementOffsets(0, 1.0F)[0];
                  Vec3 extraVec = new Vec3(0.0, 0.0, -3.0).xRot(-this.getXRot() * 0.017453292F).yRot(-renderYaw * 0.017453292F);
                  Vec3 backOfHead = this.headPart.position().add(extraVec);
                  Vec3 swingVec = new Vec3(this.isHoldingSquidLeft() ? 1.399999976158142 : -1.399999976158142, -0.1, 3.0)
                     .xRot(-this.getXRot() * 0.017453292F)
                     .yRot(-renderYaw * 0.017453292F)
                     .yRot(offsetAngle);
                  Vec3 mouth = backOfHead.add(swingVec).scale(scale);
                  this.getTarget().setPos(mouth.x, mouth.y, mouth.z);
                  if (this.isHoldingSquidLeft()) {
                     this.getTarget().setYRot(this.yBodyRot + 90.0F - (float)Math.toDegrees(offsetAngle));
                  } else {
                     this.getTarget().setYRot(this.yBodyRot - 90.0F - (float)Math.toDegrees(offsetAngle));
                  }

                  if (this.getTarget() instanceof EntityGiantSquid && ((EntityGiantSquid)this.getTarget()).tickCaptured(this)) {
                     this.setGrabbing(false);
                     this.getTarget().setPos(this.getDismountLocationForPassenger(this.getTarget()));
                  }

                  if (this.grabTime % 20 == 0 && this.grabTime > 30) {
                     this.getTarget().hurt(this.damageSources().mobAttack(this), 4 + this.random.nextInt(4));
                  }

                  if (this.grabTime > 300) {
                     this.setGrabbing(false);
                     this.getTarget().setPos(this.getDismountLocationForPassenger(this.getTarget()));
                  }
               } else {
                  this.setCaughtSquidId(-1);
                  this.lookAt(target, 360.0F, 360.0F);
                  this.waitForEchoFlag = this.getLastHurtByMob() == null || !this.getLastHurtByMob().is(target);
                  if (target instanceof Player || !target.isInWaterOrBubble()) {
                     this.waitForEchoFlag = false;
                  }

                  if (this.waitForEchoFlag && !this.receivedEcho) {
                     this.setCharging(false);
                     this.whaleSpeedMod = 0.25F;
                     if (this.echoTimer % 10 == 0) {
                        if (this.echoTimer % 40 == 0) {
                           this.playSound(AMSoundRegistry.CACHALOT_WHALE_CLICK.get(), this.getSoundVolume(), this.getVoicePitch());
                           this.gameEvent(AMPlatform.ENTITY_ACTION);
                        }

                        EntityCachalotEcho echo = new EntityCachalotEcho(this.level(), this);
                        float radius = this.headPart.getBbWidth() * 0.5F;
                        float angle = 0.017453292F * this.yBodyRot;
                        double extraX = radius * (1.0F + this.random.nextFloat() * 0.13F) * Mth.sin(3.1415927F + angle) + (this.random.nextFloat() - 0.5F)
                           + this.getDeltaMovement().x * 2.0;
                        double extraZ = radius * (1.0F + this.random.nextFloat() * 0.13F) * Mth.cos(angle) + (this.random.nextFloat() - 0.5F)
                           + this.getDeltaMovement().z * 2.0;
                        double x = this.headPart.getX() + extraX;
                        double y = this.headPart.getY() + this.headPart.getBbHeight() * 0.5;
                        double z = this.headPart.getZ() + extraZ;
                        echo.setPos(x, y, z);
                        double d0 = target.getX() - x;
                        double d1 = target.getY(0.1) - y;
                        double d2 = target.getZ() - z;
                        echo.shoot(d0, d1, d2, 1.0F, 0.0F);
                        this.level().addFreshEntity(echo);
                     }

                     this.echoTimer++;
                  }

                  if (!this.waitForEchoFlag || this.receivedEcho) {
                     double d0 = target.getX() - this.getX();
                     double d1 = target.getEyeY() - this.getEyeY();
                     double d2 = target.getZ() - this.getZ();
                     double d3 = Mth.sqrt((float)(d0 * d0 + d2 * d2));
                     float targetYaw = (float)(Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
                     float targetPitch = (float)(-(Mth.atan2(d1, d3) * 57.2957763671875));
                     this.setXRot(this.getXRot() + Mth.clamp(targetPitch - this.getXRot(), -2.0F, 2.0F));
                     if (d0 * d0 + d2 * d2 >= 4.0) {
                        this.setYRot(this.getYRot() + Mth.clamp(targetYaw - this.getYRot(), -2.0F, 2.0F));
                        this.yBodyRot = this.getYRot();
                     }

                     if (this.chargeCooldown <= 0 && Math.abs(Mth.wrapDegrees(targetYaw) - Mth.wrapDegrees(this.getYRot())) < 4.0F) {
                        this.setCharging(true);
                        this.whaleSpeedMod = 1.2F;
                        double distSq = d0 * d0 + d2 * d2;
                        if (distSq < 4.0) {
                           this.setYRot(this.yRotO);
                           this.yBodyRot = this.yRotO;
                           this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 1.0, 0.8));
                        } else {
                           if (this.isInWater() && target.isInWater()) {
                              Vec3 vector3d = this.getDeltaMovement();
                              Vec3 vector3d1 = new Vec3(target.getX() - this.getX(), target.getY() - this.getY(), target.getZ() - this.getZ());
                              if (vector3d1.lengthSqr() > 1.0E-7) {
                                 vector3d1 = vector3d1.normalize().scale(0.5).add(vector3d.scale(0.8));
                              }

                              this.setDeltaMovement(vector3d1.x, vector3d1.y, vector3d1.z);
                           }

                           this.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0);
                        }

                        if (this.isCharging() && this.distanceTo(target) < this.getBbWidth() && this.chargeProgress > 4.0F) {
                           if (target instanceof EntityGiantSquid && !this.isBaby()) {
                              this.setGrabbing(true);
                              this.setHoldingSquidLeft(this.random.nextBoolean());
                           } else {
                              target.hurt(this.damageSources().mobAttack(this), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                           }

                           this.setCharging(false);
                           if (target.getVehicle() instanceof Boat boat) {
                              for (int i = 0; i < 3; i++) {
                                 AMCompat.spawnAtLocation(this, boat.getVariant().getPlanks());
                              }

                              for (int j = 0; j < 2; j++) {
                                 AMCompat.spawnAtLocation(this, Items.STICK);
                              }

                              target.removeVehicle();
                              boat.hurt(this.damageSources().mobAttack(this), 1000.0F);
                              boat.remove(RemovalReason.DISCARDED);
                           }

                           this.chargeCooldown = target instanceof Player ? 30 : 100;
                           if (this.random.nextInt(10) == 0) {
                              Vec3 vec = this.getMouthVec();
                              ItemEntity itementity = new ItemEntity(
                                 this.level(), vec.x, vec.y, vec.z, new ItemStack((ItemLike)AMItemRegistry.CACHALOT_WHALE_TOOTH.get())
                              );
                              itementity.setDefaultPickUpDelay();
                              this.level().addFreshEntity(itementity);
                           }
                        }
                     }
                  }
               }
            }
         } else {
            this.setGrabbing(false);
            this.whaleSpeedMod = this.isSleeping() ? 0.0F : 1.0F;
            this.setCharging(false);
            this.setCaughtSquidId(-1);
         }

         if (this.chargeCooldown > 0) {
            this.chargeCooldown--;
         }

         if (this.spoutTimer > 0) {
            this.level().broadcastEntityEvent(this, (byte)67);
            this.spoutTimer--;
            this.setXRot(0.0F);
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.0, 0.0, 0.0));
         }

         if (this.isSleepTime() && !this.isSleeping() && this.isInWaterOrBubble() && this.getTarget() == null) {
            this.setSleeping(true);
         }

         if (this.isSleeping() && (!this.isSleepTime() || this.getTarget() != null)) {
            this.setSleeping(false);
         }

         if (target instanceof Player && ((Player)target).isCreative()) {
            this.setTarget(null);
         }
      }

      if (this.isAlive() && this.isCharging()) {
         for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.headPart.getBoundingBox().inflate(1.0))) {
            if (!this.isAlliedTo(entity) && !(entity instanceof EntityCachalotPart) && entity != this) {
               this.launch(entity, true);
            }
         }
      }

      if (this.isInWater() && !this.isEyeInFluid(FluidTags.WATER) && this.getAirSupply() > 140) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.06, 0.0));
      }

      if (!this.level().isClientSide()) {
         this.tryDespawn();
      }

      this.prevEyesInWater = this.isEyeInFluid(FluidTags.WATER);
   }

   private void launch(Entity e, boolean huge) {
      if ((e.onGround() || e.isInWater()) && !(e instanceof EntityCachalotWhale)) {
         double d0 = e.getX() - this.getX();
         double d1 = e.getZ() - this.getZ();
         double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
         float f = huge ? 2.0F : 0.5F;
         e.push(d0 / d2 * f, huge ? 0.5 : 0.20000000298023224, d1 / d2 * f);
      }
   }

   private boolean isSleepTime() {
      long time = this.level().getDayTime();
      return time > 18000L && time < 22812L && this.isInWaterOrBubble();
   }

   public Vec3 getReturnEchoVector() {
      return this.getVec(0.5);
   }

   public Vec3 getMouthVec() {
      return this.getVec(0.25);
   }

   private Vec3 getVec(double yShift) {
      float radius = this.headPart.getBbWidth() * 0.5F;
      float angle = 0.017453292F * this.yBodyRot;
      double extraX = radius * (1.0F + this.random.nextFloat() * 0.13F) * Mth.sin(3.1415927F + angle) + (this.random.nextFloat() - 0.5F)
         + this.getDeltaMovement().x * 2.0;
      double extraZ = radius * (1.0F + this.random.nextFloat() * 0.13F) * Mth.cos(angle) + (this.random.nextFloat() - 0.5F) + this.getDeltaMovement().z * 2.0;
      double x = this.headPart.getX() + extraX;
      double y = this.headPart.getY() + yShift;
      double z = this.headPart.getZ() + extraZ;
      return new Vec3(x, y, z);
   }

   public void setTarget(@Nullable LivingEntity entitylivingbaseIn) {
      LivingEntity prev = this.getTarget();
      if (prev != entitylivingbaseIn && entitylivingbaseIn != null) {
         this.receivedEcho = false;
      }

      super.setTarget(entitylivingbaseIn);
   }

   public double[] getMovementOffsets(int p_70974_1_, float partialTicks) {
      if (this.isDeadOrDying()) {
         partialTicks = 0.0F;
      }

      partialTicks = 1.0F - partialTicks;
      int i = this.ringBufferIndex - p_70974_1_ & 63;
      int j = this.ringBufferIndex - p_70974_1_ - 1 & 63;
      double[] adouble = new double[3];
      double d0 = this.ringBuffer[i][0];
      double d1 = this.ringBuffer[j][0] - d0;
      adouble[0] = d0 + d1 * partialTicks;
      d0 = this.ringBuffer[i][1];
      d1 = this.ringBuffer[j][1] - d0;
      adouble[1] = d0 + d1 * partialTicks;
      adouble[2] = Mth.lerp(partialTicks, this.ringBuffer[i][2], this.ringBuffer[j][2]);
      return adouble;
   }

   public void push(Entity entityIn) {
   }

   private void setPartPosition(EntityCachalotPart part, double offsetX, double offsetY, double offsetZ) {
      part.setPos(this.getX() + offsetX * part.scale, this.getY() + offsetY * part.scale, this.getZ() + offsetZ * part.scale);
   }

   @Override
   public boolean isMultipartEntity() {
      return true;
   }

   @Override
   public PartEntity<?>[] getParts() {
      return this.whaleParts;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
      EntityCachalotWhale whale = AMCompat.create(AMEntityRegistry.CACHALOT_WHALE.get(), serverWorld);
      whale.setAlbino(this.isAlbino());
      return whale;
   }

   public boolean attackEntityPartFrom(EntityCachalotPart entityCachalotPart, DamageSource source, float amount) {
      return AMCompat.hurt(this, source, amount);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      this.setAirSupply(this.getMaxAirSupply());
      this.setXRot(0.0F);
      if (spawnDataIn == null) {
         spawnDataIn = new AgeableMobGroupData(0.75F);
      }

      this.setAlbino(this.random.nextInt(100) == 0);
      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   public void baseTick() {
      int i = this.getAirSupply();
      super.baseTick();
      this.updateAir(i);
   }

   public boolean isPushedByFluid() {
      return this.isBeached();
   }

   public boolean checkSpawnObstruction(LevelReader worldIn) {
      return worldIn.isUnobstructed(this);
   }

   protected void updateAir(int p_209207_1_) {
   }

   public int getMaxAirSupply() {
      return 4000;
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      if (id == 67) {
         this.spawnSpoutParticles();
      } else {
         super.handleEntityEvent(id);
      }
   }

   protected int increaseAirSupply(int currentAir) {
      if (!this.level().isClientSide()
         && this.prevEyesInWater
         && this.spoutTimer <= 0
         && !this.isEyeInFluid(FluidTags.WATER)
         && currentAir < this.getMaxAirSupply() / 2) {
         this.spoutTimer = 20 + this.random.nextInt(10);
      }

      return this.getMaxAirSupply();
   }

   public int getMaxHeadXRot() {
      return 1;
   }

   public int getMaxHeadYRot() {
      return 3;
   }

   public void recieveEcho() {
      this.receivedEcho = true;
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.cachalotWhaleSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity dismount) {
      Vec3 mouth = this.getMouthVec();
      BlockPos pos = AMBlockPos.fromVec3(mouth);

      while (!this.level().isEmptyBlock(pos) && !this.level().isWaterAt(pos) && pos.getY() < AMCompat.maxBuildHeight(this.level())) {
         pos = pos.above();
      }

      return new Vec3(mouth.x, pos.getY() + 0.5F, mouth.z);
   }

   class AIBreathe extends Goal {
      public AIBreathe() {
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         return EntityCachalotWhale.this.getAirSupply() < 140;
      }

      public boolean canContinueToUse() {
         return this.canUse();
      }

      public boolean isInterruptable() {
         return false;
      }

      public void start() {
         this.navigate();
      }

      private void navigate() {
         Iterable<BlockPos> lvt_1_1_ = BlockPos.betweenClosed(
            Mth.floor(EntityCachalotWhale.this.getX() - 1.0),
            Mth.floor(EntityCachalotWhale.this.getY()),
            Mth.floor(EntityCachalotWhale.this.getZ() - 1.0),
            Mth.floor(EntityCachalotWhale.this.getX() + 1.0),
            Mth.floor(EntityCachalotWhale.this.getY() + 8.0),
            Mth.floor(EntityCachalotWhale.this.getZ() + 1.0)
         );
         BlockPos lvt_2_1_ = null;

         for (BlockPos lvt_4_1_ : lvt_1_1_) {
            if (this.canBreatheAt(EntityCachalotWhale.this.level(), lvt_4_1_)) {
               lvt_2_1_ = lvt_4_1_.below((int)(EntityCachalotWhale.this.getBbHeight() * 0.25));
               break;
            }
         }

         if (lvt_2_1_ == null) {
            lvt_2_1_ = AMBlockPos.fromCoords(EntityCachalotWhale.this.getX(), EntityCachalotWhale.this.getY() + 4.0, EntityCachalotWhale.this.getZ());
         }

         if (EntityCachalotWhale.this.isEyeInFluid(FluidTags.WATER)) {
            EntityCachalotWhale.this.setDeltaMovement(EntityCachalotWhale.this.getDeltaMovement().add(0.0, 0.05000000074505806, 0.0));
         }

         EntityCachalotWhale.this.getNavigation().moveTo(lvt_2_1_.getX(), lvt_2_1_.getY(), lvt_2_1_.getZ(), 0.7);
      }

      public void tick() {
         this.navigate();
      }

      private boolean canBreatheAt(LevelReader p_205140_1_, BlockPos p_205140_2_) {
         BlockState lvt_3_1_ = p_205140_1_.getBlockState(p_205140_2_);
         return (p_205140_1_.getFluidState(p_205140_2_).isEmpty() || lvt_3_1_.is(Blocks.BUBBLE_COLUMN))
            && AMCompat.isPathfindable(lvt_3_1_, p_205140_1_, p_205140_2_, PathComputationType.LAND);
      }
   }
}
