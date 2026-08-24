package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.BlueJayAIMelee;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.FlyingAITempt;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityBlueJay extends Animal implements ITargetsDroppedItems {
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityBlueJay.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(EntityBlueJay.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> CREST_TARGET = SynchedEntityData.defineId(EntityBlueJay.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Optional<UUID>> LAST_FEEDER_UUID = SynchedEntityData.defineId(
      EntityBlueJay.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Optional<UUID>> RACCOON_UUID = SynchedEntityData.defineId(EntityBlueJay.class, EntityDataSerializers.OPTIONAL_UUID);
   private static final EntityDataAccessor<Integer> FEED_TIME = SynchedEntityData.defineId(EntityBlueJay.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SING_TIME = SynchedEntityData.defineId(EntityBlueJay.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> BLUE_VISUAL_FLAG = SynchedEntityData.defineId(EntityBlueJay.class, EntityDataSerializers.BOOLEAN);
   private static final Predicate<Entity> HIGHLIGHTS_WITH_SONG = entity -> entity instanceof Enemy;
   public float prevFlyProgress;
   public float flyProgress;
   public float prevFlapAmount;
   public float flapAmount;
   public float attackProgress;
   public float prevAttackProgress;
   public float prevCrestAmount;
   public float crestAmount;
   private boolean isLandNavigator;
   private int timeFlying;
   public float birdPitch = 0.0F;
   public float prevBirdPitch = 0.0F;
   public boolean aiItemFlag = false;
   private int prevSingTime = 0;
   private int blueTime = 0;
   private int raiseCrestOverrideTicks;

   protected EntityBlueJay(EntityType<? extends Animal> animal, Level level) {
      super(animal, level);
      this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
      this.setPathfindingMalus(PathType.COCOA, -1.0F);
      this.setPathfindingMalus(PathType.FENCE, -1.0F);
      this.switchNavigator(false);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(2, new BlueJayAIMelee(this));
      this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.0));
      this.goalSelector.addGoal(4, new FlyingAITempt(this, 1.0, AMCompat.ingredientOf(AMTagRegistry.BLUE_JAY_FOODSTUFFS), false));
      this.goalSelector.addGoal(5, new EntityBlueJay.AIFollowFeederOrRaccoon());
      this.goalSelector.addGoal(6, new EntityBlueJay.AIFlyIdle());
      this.goalSelector.addGoal(7, new EntityBlueJay.AIScatter());
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, PathfinderMob.class, 6.0F));
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new EntityBlueJay.AITargetItems(this, false, false, 40, 16));
      this.targetSelector.addGoal(4, new HurtByTargetGoal(this, new Class[]{Player.class}).setAlertOthers(new Class[0]));
   }

   public static boolean checkBlueJaySpawnRules(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      return isBrightEnoughToSpawn(worldIn, pos);
   }

   public boolean checkSpawnObstruction(LevelReader reader) {
      if (reader.isUnobstructed(this) && !reader.containsAnyLiquid(this.getBoundingBox())) {
         BlockPos blockpos = this.blockPosition();
         BlockState blockstate2 = reader.getBlockState(blockpos.below());
         return blockstate2.is(BlockTags.LEAVES) || blockstate2.is(BlockTags.LOGS) || blockstate2.is(Blocks.GRASS_BLOCK);
      } else {
         return false;
      }
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.blueJaySpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.BLUE_JAY_BREEDABLES);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FLYING, false);
      builder.define(ATTACK_TICK, 0);
      builder.define(FEED_TIME, 0);
      builder.define(SING_TIME, 0);
      builder.define(CREST_TARGET, 0.0F);
      builder.define(BLUE_VISUAL_FLAG, false);
      builder.define(RACCOON_UUID, Optional.empty());
      builder.define(LAST_FEEDER_UUID, Optional.empty());
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigation(this, this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new FlightMoveController(this, 1.0F, false);
         this.navigation = new DirectPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   public void tick() {
      super.tick();
      this.prevCrestAmount = this.crestAmount;
      this.prevAttackProgress = this.attackProgress;
      this.prevFlapAmount = this.flapAmount;
      this.prevFlyProgress = this.flyProgress;
      this.prevBirdPitch = this.birdPitch;
      if (this.isFlying()) {
         if (this.flyProgress < 5.0F) {
            this.flyProgress++;
         }
      } else if (this.flyProgress > 0.0F) {
         this.flyProgress--;
      }

      if ((Integer)this.entityData.get(ATTACK_TICK) > 0) {
         this.entityData.set(ATTACK_TICK, (Integer)this.entityData.get(ATTACK_TICK) - 1);
         if (this.attackProgress < 5.0F) {
            this.attackProgress++;
         }
      } else if (this.attackProgress > 0.0F) {
         this.attackProgress--;
      }

      float yMov = (float)this.getDeltaMovement().y;
      this.birdPitch = yMov * 2.0F * -57.295776F;
      if (yMov >= 0.0F) {
         if (this.flapAmount < 1.0F) {
            this.flapAmount += 0.25F;
         }
      } else if (yMov < -0.07F && this.flapAmount > 0.0F) {
         this.flapAmount -= 0.25F;
      }

      if (this.raiseCrestOverrideTicks > 0) {
         this.raiseCrestOverrideTicks--;
         this.crestAmount = 0.75F;
      } else {
         this.crestAmount = Mth.approach(this.crestAmount, this.getTargetCrest(), 0.3F);
      }

      if (!this.level().isClientSide()) {
         if (this.isFlying()) {
            if (this.isLandNavigator) {
               this.switchNavigator(false);
            }
         } else if (!this.isLandNavigator) {
            this.switchNavigator(true);
         }

         if (this.isFlying()) {
            this.timeFlying++;
            this.setNoGravity(true);
            if (this.isPassenger() || this.isInLove()) {
               this.setFlying(false);
            }
         } else {
            this.timeFlying = 0;
            this.setNoGravity(false);
         }

         if (this.getTarget() != null) {
            this.setCrestTarget(1.0F);
         } else if (this.getRaccoonUUID() != null) {
            this.setCrestTarget(0.5F);
         } else {
            this.setCrestTarget(0.0F);
         }
      }

      if (this.getFeedTime() > 0) {
         this.setFeedTime(this.getFeedTime() - 1);
         if (this.getFeedTime() == 0) {
            this.setLastFeeder(null);
         }
      }

      if (this.getVehicle() instanceof EntityRaccoon riddenRaccoon) {
         this.yBodyRot = riddenRaccoon.yBodyRot;
      }

      if (this.getRaccoon() instanceof EntityRaccoon raccoon) {
         LivingEntity jayTarget = this.getTarget();
         LivingEntity raccoonTarget = raccoon.getTarget();
         if (jayTarget != null && jayTarget.isAlive()) {
            if (this.isPassenger()) {
               this.stopRiding();
            }
         } else if (raccoonTarget != null && raccoonTarget.isAlive() && this.canAttack(raccoonTarget)) {
            this.setTarget(raccoonTarget);
         }
      }

      if (this.getSingTime() > 0) {
         this.setSingTime(this.getSingTime() - 1);
         if (this.prevSingTime % 15 == 0) {
            this.playSound(AMSoundRegistry.BLUE_JAY_SONG.get(), this.getSoundVolume(), this.getVoicePitch());
         }

         if (this.level().isClientSide() && this.getSingTime() % 5 == 0 && this.level().isClientSide()) {
            Vec3 modelFront = new Vec3(0.0, 0.20000000298023224, 0.30000001192092896)
               .scale(this.getScale())
               .xRot(-this.getXRot() * 0.017453292F)
               .yRot(-this.getYRot() * 0.017453292F);
            Vec3 particleFrom = this.position().add(modelFront);
            this.level()
               .addParticle(
                  (ParticleOptions)AMParticleRegistry.BIRD_SONG.get(), particleFrom.x, particleFrom.y, particleFrom.z, modelFront.x, modelFront.y, modelFront.z
               );
         }
      }

      if (this.prevSingTime < this.getSingTime() && !this.level().isClientSide()) {
         this.blueTime = 1200;
         this.entityData.set(BLUE_VISUAL_FLAG, true);
         this.highlightMonsters();
      }

      if (this.blueTime > 0) {
         this.blueTime--;
         if (this.blueTime == 0) {
            this.entityData.set(BLUE_VISUAL_FLAG, false);
            this.level().broadcastEntityEvent(this, (byte)68);
         } else {
            this.level().broadcastEntityEvent(this, (byte)67);
         }
      }

      this.prevSingTime = this.getSingTime();
   }

   public void playAmbientSound() {
      super.playAmbientSound();
      this.raiseCrestOverrideTicks = 15;
   }

   private boolean highlightMonsters() {
      AABB allyBox = this.getBoundingBox().inflate(64.0);
      allyBox = allyBox.setMinY(-64.0);
      allyBox = allyBox.setMaxY(320.0);
      boolean any = false;

      for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, allyBox, HIGHLIGHTS_WITH_SONG)) {
         entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, this.blueTime, 0, true, false));
      }

      return any;
   }

   public boolean isMakingMonstersBlue() {
      return (Boolean)this.entityData.get(BLUE_VISUAL_FLAG);
   }

   public void remove(RemovalReason removalReason) {
      if (this.getSingTime() > 0 && !this.level().isClientSide()) {
         this.entityData.set(BLUE_VISUAL_FLAG, false);
         this.level().broadcastEntityEvent(this, (byte)68);
      }

      super.remove(removalReason);
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
   }

   public void travel(Vec3 vec3d) {
      if (this.isInWater() && this.getDeltaMovement().y > 0.0) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.5, 1.0));
      }

      super.travel(vec3d);
   }

   public BlockPos getBlueJayGround(BlockPos in) {
      BlockPos position = new BlockPos(in.getX(), (int)this.getY(), in.getZ());

      while (position.getY() < 320 && !this.level().getFluidState(position).isEmpty()) {
         position = position.above();
      }

      while (position.getY() > -64 && !this.level().getBlockState(position).isSolid() && this.level().getFluidState(position).isEmpty()) {
         position = position.below();
      }

      return position;
   }

   public boolean isAlliedTo(Entity entityIn) {
      if (this.getRaccoonUUID() != null) {
         if (entityIn instanceof EntityRaccoon && this.getRaccoonUUID().equals(entityIn.getUUID())) {
            return true;
         }

         Entity raccoon = this.getRaccoon();
         if (raccoon != null && (raccoon.isAlliedTo(entityIn) || entityIn.isAlliedTo(raccoon))) {
            return true;
         }
      }

      return super.isAlliedTo(entityIn);
   }

   public Vec3 getBlockGrounding(Vec3 fleePos) {
      float radius = 10 + this.getRandom().nextInt(15);
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.x() + extraX), (int)this.getY(), (int)(fleePos.z() + extraZ));
      BlockPos ground = this.getBlueJayGround(radialPos);
      if (ground.getY() < -64) {
         return null;
      } else {
         ground = this.blockPosition();

         while (ground.getY() > -64 && !this.level().getBlockState(ground).isSolid()) {
            ground = ground.below();
         }

         return !this.isTargetBlocked(Vec3.atCenterOf(ground.above())) ? Vec3.atCenterOf(ground.below()) : null;
      }
   }

   public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
      float radius = 5.0F + radiusAdd + this.getRandom().nextInt(5);
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.x() + extraX), 0, (int)(fleePos.z() + extraZ));
      BlockPos ground = this.getBlueJayGround(radialPos);
      int distFromGround = (int)this.getY() - ground.getY();
      int flightHeight = 5 + this.getRandom().nextInt(5);
      int j = this.getRandom().nextInt(5) + 5;
      BlockPos newPos = ground.above(distFromGround > 5 ? flightHeight : j);
      if (this.level().getBlockState(ground).is(BlockTags.LEAVES)) {
         newPos = ground.above(1 + this.getRandom().nextInt(3));
      }

      return !this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1.0 ? Vec3.atCenterOf(newPos) : null;
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.BLUE_JAY_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.BLUE_JAY_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.BLUE_JAY_HURT.get();
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).getType() != Type.MISS;
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.ATTACK_DAMAGE, 1.0).add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   @Override
   public boolean isFlying() {
      return (Boolean)this.entityData.get(FLYING);
   }

   @Override
   public void setFlying(boolean flying) {
      if (flying && this.isBaby()) {
         flying = false;
      }

      this.entityData.set(FLYING, flying);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFlying(AMCompat.getBoolean(compound, "Flying"));
      this.blueTime = AMCompat.getInt(compound, "BlueTime");
      if (AMCompat.hasUUID(compound, "FeederUUID")) {
         this.setLastFeederUUID(AMCompat.getUUID(compound, "FeederUUID"));
      }

      if (AMCompat.hasUUID(compound, "RaccoonUUID")) {
         this.setRaccoonUUID(AMCompat.getUUID(compound, "RaccoonUUID"));
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Flying", this.isFlying());
      compound.putInt("BlueTime", this.blueTime);
      if (this.getLastFeederUUID() != null) {
         AMCompat.putUUID(compound, "FeederUUID", this.getLastFeederUUID());
      }

      if (this.getRaccoonUUID() != null) {
         AMCompat.putUUID(compound, "RaccoonUUID", this.getRaccoonUUID());
      }
   }

   public int getFeedTime() {
      return (Integer)this.entityData.get(FEED_TIME);
   }

   public void setFeedTime(int feedTime) {
      this.entityData.set(FEED_TIME, feedTime);
   }

   public int getSingTime() {
      return (Integer)this.entityData.get(SING_TIME);
   }

   public void setSingTime(int singTime) {
      this.entityData.set(SING_TIME, singTime);
   }

   public float getTargetCrest() {
      return (Float)this.entityData.get(CREST_TARGET);
   }

   public void setCrestTarget(float crestTarget) {
      this.entityData.set(CREST_TARGET, crestTarget);
   }

   @Nullable
   public UUID getLastFeederUUID() {
      return (UUID)((Optional)this.entityData.get(LAST_FEEDER_UUID)).orElse(null);
   }

   public void setLastFeederUUID(@Nullable UUID uniqueId) {
      this.entityData.set(LAST_FEEDER_UUID, Optional.ofNullable(uniqueId));
   }

   @Nullable
   public Entity getLastFeeder() {
      UUID id = this.getLastFeederUUID();
      return id != null && !this.level().isClientSide() ? ((ServerLevel)this.level()).getEntity(id) : null;
   }

   public void setLastFeeder(@Nullable Entity feeder) {
      if (feeder == null) {
         this.setLastFeederUUID(null);
      } else {
         this.setLastFeederUUID(feeder.getUUID());
      }
   }

   @Nullable
   public UUID getRaccoonUUID() {
      return (UUID)((Optional)this.entityData.get(RACCOON_UUID)).orElse(null);
   }

   public void setRaccoonUUID(@Nullable UUID uniqueId) {
      this.entityData.set(RACCOON_UUID, Optional.ofNullable(uniqueId));
   }

   @Nullable
   public Entity getRaccoon() {
      UUID id = this.getRaccoonUUID();
      return id != null && !this.level().isClientSide() ? ((ServerLevel)this.level()).getEntity(id) : null;
   }

   public void setRaccoon(@Nullable Entity feeder) {
      if (feeder == null) {
         this.setRaccoonUUID(null);
      } else {
         this.setRaccoonUUID(feeder.getUUID());
      }
   }

   private boolean isOverWaterOrVoid() {
      BlockPos position = this.blockPosition();

      while (position.getY() > -65 && this.level().isEmptyBlock(position)) {
         position = position.below();
      }

      return !this.level().getFluidState(position).isEmpty() || this.level().getBlockState(position).is(Blocks.VINE) || position.getY() <= -65;
   }

   @org.jetbrains.annotations.Nullable
   public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
      return AMCompat.create(AMEntityRegistry.BLUE_JAY.get(), this.level());
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return AMCompat.isEdible(stack.getItem()) || stack.is(AMTagRegistry.BLUE_JAY_FOODSTUFFS);
   }

   @Override
   public double getMaxDistToItem() {
      return 1.0;
   }

   @Override
   public void onGetItem(ItemEntity e) {
      if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level().isClientSide()) {
         AMCompat.spawnAtLocation(this, this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
      }

      this.heal(3.0F);
      Entity itemThrower = e.getOwner();
      if (itemThrower != null && e.getItem().is(AMTagRegistry.BLUE_JAY_TEAMING_FOODS)) {
         this.setLastFeederUUID(itemThrower.getUUID());
         this.setFeedTime(1200);
         this.stopRiding();
      }

      if (e.getOwner() != null && e.getItem().is(AMTagRegistry.BLUE_JAY_ALERT_FOODS)) {
         this.setSingTime(40);
      }
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      InteractionResult type = super.mobInteract(player, hand);
      if (!type.consumesAction()) {
         if (itemstack.is(AMTagRegistry.BLUE_JAY_TEAMING_FOODS) && this.getFeedTime() <= 0) {
            this.heal(3.0F);
            this.usePlayerItem(player, hand, itemstack);
            this.setRaccoonUUID(null);
            this.stopRiding();
            this.setLastFeeder(player);
            this.setFeedTime(1200);
            return InteractionResult.SUCCESS;
         }

         if (itemstack.is(AMTagRegistry.BLUE_JAY_ALERT_FOODS) && this.getSingTime() <= 0) {
            this.heal(3.0F);
            this.setSingTime(40);
            this.usePlayerItem(player, hand, itemstack);
            return InteractionResult.SUCCESS;
         }
      }

      return type;
   }

   @Override
   public void peck() {
      this.entityData.set(ATTACK_TICK, 7);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      if (id != 67 && id != 68) {
         super.handleEntityEvent(id);
      } else {
         AlexsMobs.PROXY.onEntityStatus(this, id);
      }
   }

   private boolean isTrusting() {
      return this.getFeedTime() > 0 || this.getSingTime() > 0 || this.getRaccoonUUID() != null || this.aiItemFlag;
   }

   private class AIFlyIdle extends Goal {
      protected double x;
      protected double y;
      protected double z;
      private boolean flightTarget;

      public AIFlyIdle() {
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         if (EntityBlueJay.this.isVehicle()
            || EntityBlueJay.this.getTarget() != null && EntityBlueJay.this.getTarget().isAlive()
            || EntityBlueJay.this.isPassenger()
            || EntityBlueJay.this.aiItemFlag
            || EntityBlueJay.this.getSingTime() > 0) {
            return false;
         } else if (EntityBlueJay.this.getRandom().nextInt(45) != 0 && !EntityBlueJay.this.isFlying()) {
            return false;
         } else {
            if (EntityBlueJay.this.onGround()) {
               this.flightTarget = EntityBlueJay.this.random.nextBoolean();
            } else {
               this.flightTarget = EntityBlueJay.this.random.nextInt(5) > 0 && EntityBlueJay.this.timeFlying < 200;
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
            EntityBlueJay.this.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
         } else {
            EntityBlueJay.this.getNavigation().moveTo(this.x, this.y, this.z, 1.0);
         }

         if (!this.flightTarget && EntityBlueJay.this.isFlying() && EntityBlueJay.this.onGround()) {
            EntityBlueJay.this.setFlying(false);
         }

         if (EntityBlueJay.this.isFlying() && EntityBlueJay.this.onGround() && EntityBlueJay.this.timeFlying > 10) {
            EntityBlueJay.this.setFlying(false);
         }
      }

      @Nullable
      protected Vec3 getPosition() {
         Vec3 vector3d = EntityBlueJay.this.position();
         if (EntityBlueJay.this.isOverWaterOrVoid()) {
            this.flightTarget = true;
         }

         if (this.flightTarget) {
            return EntityBlueJay.this.timeFlying >= 200 && !EntityBlueJay.this.isOverWaterOrVoid()
               ? EntityBlueJay.this.getBlockGrounding(vector3d)
               : EntityBlueJay.this.getBlockInViewAway(vector3d, 0.0F);
         } else {
            return LandRandomPos.getPos(EntityBlueJay.this, 10, 7);
         }
      }

      public boolean canContinueToUse() {
         return this.flightTarget
            ? EntityBlueJay.this.isFlying() && EntityBlueJay.this.distanceToSqr(this.x, this.y, this.z) > 5.0
            : !EntityBlueJay.this.getNavigation().isDone() && !EntityBlueJay.this.isVehicle();
      }

      public void start() {
         if (this.flightTarget) {
            EntityBlueJay.this.setFlying(true);
            EntityBlueJay.this.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
         } else {
            EntityBlueJay.this.getNavigation().moveTo(this.x, this.y, this.z, 1.0);
         }
      }

      public void stop() {
         EntityBlueJay.this.getNavigation().stop();
         this.x = 0.0;
         this.y = 0.0;
         this.z = 0.0;
         super.stop();
      }
   }

   private class AIFollowFeederOrRaccoon extends Goal {
      private Entity following;

      AIFollowFeederOrRaccoon() {
         this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
      }

      public boolean canUse() {
         if (!EntityBlueJay.this.isPassenger() && (EntityBlueJay.this.getTarget() == null || !EntityBlueJay.this.getTarget().isAlive())) {
            if (EntityBlueJay.this.getRaccoonUUID() != null) {
               Entity raccoon = EntityBlueJay.this.getRaccoon();
               if (raccoon != null) {
                  this.following = raccoon;
                  return true;
               }
            }

            if (EntityBlueJay.this.getFeedTime() > 0) {
               Entity feeder = EntityBlueJay.this.getLastFeeder();
               if (feeder != null) {
                  this.following = feeder;
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         LivingEntity target = EntityBlueJay.this.getTarget();
         return this.following != null
            && this.following.isAlive()
            && (target == null || !target.isAlive())
            && (this.following instanceof EntityRaccoon || EntityBlueJay.this.getFeedTime() > 0)
            && !EntityBlueJay.this.isPassenger();
      }

      public void tick() {
         double dist = EntityBlueJay.this.distanceTo(this.following);
         if (!(dist > 6.0) && !EntityBlueJay.this.isFlying()) {
            EntityBlueJay.this.getNavigation().moveTo(this.following.getX(), this.following.getY(), this.following.getZ(), 1.0);
         } else {
            EntityBlueJay.this.setFlying(true);
            EntityBlueJay.this.getMoveControl().setWantedPosition(this.following.getX(), this.following.getY(), this.following.getZ(), 1.0);
         }

         if (EntityBlueJay.this.isFlying() && EntityBlueJay.this.onGround() && dist < 3.0) {
            EntityBlueJay.this.setFlying(false);
         }

         if (this.following instanceof EntityRaccoon raccoon) {
            if (dist > 40.0) {
               EntityBlueJay.this.teleportTo(this.following.getX(), this.following.getY(), this.following.getZ());
            }

            if (dist < 2.5) {
               EntityBlueJay.this.getMoveControl().setWantedPosition(this.following.getX(), this.following.getY(), this.following.getZ(), 1.0);
            }

            if (dist < 1.0 && raccoon.getPassengers().isEmpty()) {
               AMCompat.startRiding(EntityBlueJay.this, raccoon, false);
            }
         }
      }
   }

   private class AIScatter extends Goal {
      protected final EntityBlueJay.AIScatter.Sorter theNearestAttackableTargetSorter;
      protected final com.google.common.base.Predicate<? super Entity> targetEntitySelector;
      protected int executionChance = 8;
      protected boolean mustUpdate;
      private Entity targetEntity;
      private Vec3 flightTarget = null;
      private int cooldown = 0;

      AIScatter() {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.theNearestAttackableTargetSorter = new EntityBlueJay.AIScatter.Sorter(EntityBlueJay.this);
         this.targetEntitySelector = new com.google.common.base.Predicate<Entity>() {
            public boolean apply(@Nullable Entity e) {
               return e.isAlive() && e.getType().builtInRegistryHolder().is(AMTagRegistry.SCATTERS_CROWS) || e instanceof Player && !((Player)e).isCreative();
            }
         };
      }

      public boolean canUse() {
         Entity entity = EntityBlueJay.this.getTarget();
         if (!EntityBlueJay.this.isPassenger() && !EntityBlueJay.this.isVehicle() && (entity == null || !entity.isAlive()) && !EntityBlueJay.this.isTrusting()) {
            if (!this.mustUpdate) {
               long worldTime = EntityBlueJay.this.level().getGameTime() % 10L;
               if (EntityBlueJay.this.getNoActionTime() >= 100 && worldTime != 0L) {
                  return false;
               }

               if (EntityBlueJay.this.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0L) {
                  return false;
               }
            }

            List<Entity> list = EntityBlueJay.this.level()
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
         return this.targetEntity != null;
      }

      public void stop() {
         this.flightTarget = null;
         this.targetEntity = null;
      }

      public void tick() {
         if (this.cooldown > 0) {
            this.cooldown--;
         }

         if (this.flightTarget != null) {
            EntityBlueJay.this.setFlying(true);
            EntityBlueJay.this.getMoveControl().setWantedPosition(this.flightTarget.x, this.flightTarget.y, this.flightTarget.z, 1.0);
            if (this.cooldown == 0 && EntityBlueJay.this.isTargetBlocked(this.flightTarget)) {
               this.cooldown = 30;
               this.flightTarget = null;
            }
         }

         if (this.targetEntity != null) {
            if (EntityBlueJay.this.onGround()
               || this.flightTarget == null
               || this.flightTarget != null && EntityBlueJay.this.distanceToSqr(this.flightTarget) < 3.0) {
               Vec3 vec = EntityBlueJay.this.getBlockInViewAway(this.targetEntity.position(), 0.0F);
               if (vec != null && vec.y() > EntityBlueJay.this.getY()) {
                  this.flightTarget = vec;
               }
            }

            if (EntityBlueJay.this.distanceTo(this.targetEntity) > 20.0F) {
               this.stop();
            }
         }
      }

      protected double getTargetDistance() {
         return 4.0;
      }

      protected AABB getTargetableArea(double targetDistance) {
         Vec3 renderCenter = new Vec3(EntityBlueJay.this.getX(), EntityBlueJay.this.getY() + 0.5, EntityBlueJay.this.getZ());
         AABB aabb = new AABB(-targetDistance, -targetDistance, -targetDistance, targetDistance, targetDistance, targetDistance);
         return aabb.move(renderCenter);
      }

      public record Sorter(Entity theEntity) implements Comparator<Entity> {
         public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = this.theEntity.distanceToSqr(p_compare_1_);
            double d1 = this.theEntity.distanceToSqr(p_compare_2_);
            return Double.compare(d0, d1);
         }
      }
   }

   private static class AITargetItems extends CreatureAITargetItems {
      public AITargetItems(PathfinderMob creature, boolean checkSight, boolean onlyNearby, int tickThreshold, int radius) {
         super(creature, checkSight, onlyNearby, tickThreshold, radius);
         this.executionChance = 1;
      }

      @Override
      public void stop() {
         super.stop();
         ((EntityBlueJay)this.mob).aiItemFlag = false;
      }

      @Override
      public boolean canUse() {
         return super.canUse() && (this.mob.getTarget() == null || !this.mob.getTarget().isAlive());
      }

      @Override
      public boolean canContinueToUse() {
         return super.canContinueToUse() && (this.mob.getTarget() == null || !this.mob.getTarget().isAlive());
      }

      @Override
      protected void moveTo() {
         EntityBlueJay jay = (EntityBlueJay)this.mob;
         if (this.targetEntity != null) {
            jay.aiItemFlag = true;
            if (this.mob.distanceTo(this.targetEntity) < 2.0F) {
               jay.getMoveControl().setWantedPosition(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.0);
               jay.peck();
            }

            if (!(this.mob.distanceTo(this.targetEntity) > 8.0F) && !jay.isFlying()) {
               this.mob.getNavigation().moveTo(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.0);
            } else {
               jay.setFlying(true);
               float f = (float)(jay.getX() - this.targetEntity.getX());
               float f1 = 1.8F;
               float f2 = (float)(jay.getZ() - this.targetEntity.getZ());
               float xzDist = Mth.sqrt(f * f + f2 * f2);
               if (!jay.hasLineOfSight(this.targetEntity)) {
                  jay.getMoveControl().setWantedPosition(this.targetEntity.getX(), 1.0 + jay.getY(), this.targetEntity.getZ(), 1.0);
               } else {
                  if (xzDist < 5.0F) {
                     f1 = 0.0F;
                  }

                  jay.getMoveControl().setWantedPosition(this.targetEntity.getX(), f1 + this.targetEntity.getY(), this.targetEntity.getZ(), 1.0);
               }
            }
         }
      }

      @Override
      public void tick() {
         super.tick();
         this.moveTo();
      }
   }
}
