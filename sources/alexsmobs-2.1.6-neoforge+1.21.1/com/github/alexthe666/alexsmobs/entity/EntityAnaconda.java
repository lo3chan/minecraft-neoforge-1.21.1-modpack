package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIPanicBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalSwimMoveControllerSink;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.entity.util.AnacondaPartIndex;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
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
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class EntityAnaconda extends Animal implements ISemiAquatic {
   private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(EntityAnaconda.class, EntityDataSerializers.OPTIONAL_UUID);
   private static final EntityDataAccessor<Integer> CHILD_ID = SynchedEntityData.defineId(EntityAnaconda.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> STRANGLING = SynchedEntityData.defineId(EntityAnaconda.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> YELLOW = SynchedEntityData.defineId(EntityAnaconda.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> SHEDTIME = SynchedEntityData.defineId(EntityAnaconda.class, EntityDataSerializers.INT);
   public final float[] ringBuffer = new float[64];
   public int ringBufferIndex = -1;
   private EntityAnacondaPart[] parts;
   private float prevStrangleProgress = 0.0F;
   private float strangleProgress = 0.0F;
   private int strangleTimer = 0;
   private int shedCooldown = 0;
   private int feedings = 0;
   private boolean isLandNavigator;
   private int swimTimer = -1000;
   private int passiveFor = 0;

   protected EntityAnaconda(EntityType t, Level world) {
      super(t, world);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
      this.switchNavigator(true);
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.ANACONDA_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.ANACONDA_HURT.get();
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      if (!this.isBaby()) {
         this.playSound(AMSoundRegistry.ANACONDA_SLITHER.get(), 1.0F, 1.0F);
      } else {
         super.playStepSound(pos, state);
      }
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0).add(Attributes.MOVEMENT_SPEED, 0.15000000596046448);
   }

   public static boolean canAnacondaSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      boolean spawnBlock = worldIn.getBlockState(pos.below()).is(AMTagRegistry.ANACONDA_SPAWNS);
      return spawnBlock && pos.getY() < worldIn.getSeaLevel() + 4;
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.anacondaSpawnRolls, this.getRandom(), spawnReasonIn);
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

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new AnimalAIPanicBaby(this, 1.25));
      this.goalSelector.addGoal(2, new EntityAnaconda.AIMelee());
      this.goalSelector.addGoal(3, new AnimalAIFindWater(this));
      this.goalSelector.addGoal(3, new AnimalAILeaveWater(this));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.25, AMCompat.ingredientOf(AMTagRegistry.ANACONDA_FOODSTUFFS), false));
      this.goalSelector.addGoal(5, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(7, new AnimalAIWanderRanged(this, 60, 1.0, 14, 7));
      this.goalSelector.addGoal(8, new SemiAquaticAIRandomSwimming(this, 1.5, 7));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 25.0F));
      this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
      this.targetSelector
         .addGoal(
            1,
            new NearestAttackableTargetGoal(
               this, LivingEntity.class, 200, false, false, AMCompat.selector(AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.ANACONDA_TARGETS))
            )
         );
      this.targetSelector
         .addGoal(
            2,
            new EntityAINearestTarget3D(this, Player.class, 110, false, true, null) {
               public boolean canUse() {
                  return !EntityAnaconda.this.isBaby()
                     && EntityAnaconda.this.passiveFor == 0
                     && EntityAnaconda.this.level().getDifficulty() != Difficulty.PEACEFUL
                     && !EntityAnaconda.this.isInLove()
                     && super.canUse();
               }
            }
         );
      this.targetSelector.addGoal(3, new HurtByTargetGoal(this, new Class[0]));
   }

   protected float getStandingEyeHeight(Pose p_33799_, EntityDimensions p_33800_) {
      return this.isBaby() ? 0.15F : 0.3F;
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      if (this.isFood(itemstack)) {
         this.setTarget(null);
         this.passiveFor = 3600 + this.random.nextInt(3600);
      }

      return super.mobInteract(player, hand);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (AMCompat.hasUUID(compound, "ChildUUID")) {
         this.setChildId(AMCompat.getUUID(compound, "ChildUUID"));
      }

      this.feedings = AMCompat.getInt(compound, "Feedings");
      this.setSheddingTime(AMCompat.getInt(compound, "ShedTime"));
      this.setYellow(AMCompat.getBoolean(compound, "Yellow"));
      this.shedCooldown = AMCompat.getInt(compound, "ShedCooldown");
      this.passiveFor = AMCompat.getInt(compound, "PassiveFor");
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (this.getChildId() != null) {
         AMCompat.putUUID(compound, "ChildUUID", this.getChildId());
      }

      compound.putInt("Feedings", this.feedings);
      compound.putInt("ShedTime", this.getSheddingTime());
      compound.putBoolean("Yellow", this.isYellow());
      compound.putInt("ShedCooldown", this.shedCooldown);
      compound.putInt("PassiveFor", this.passiveFor);
   }

   public void pushEntities() {
      List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.2, 0.0, 0.2));
      entities.stream().filter(entity -> !(entity instanceof EntityAnacondaPart) && entity.isPushable()).forEach(entity -> entity.push(this));
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CHILD_UUID, Optional.empty());
      builder.define(CHILD_ID, -1);
      builder.define(STRANGLING, false);
      builder.define(YELLOW, false);
      builder.define(SHEDTIME, 0);
   }

   @Nullable
   public UUID getChildId() {
      return (UUID)((Optional)this.entityData.get(CHILD_UUID)).orElse(null);
   }

   public void setChildId(@Nullable UUID uniqueId) {
      this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
   }

   public int getSheddingTime() {
      return (Integer)this.entityData.get(SHEDTIME);
   }

   public void setSheddingTime(int shedtime) {
      this.entityData.set(SHEDTIME, shedtime);
   }

   public boolean isStrangling() {
      return (Boolean)this.entityData.get(STRANGLING);
   }

   public void setStrangling(boolean running) {
      this.entityData.set(STRANGLING, running);
   }

   public boolean isYellow() {
      return (Boolean)this.entityData.get(YELLOW);
   }

   public void setYellow(boolean yellow) {
      this.entityData.set(YELLOW, yellow);
   }

   public int getMaxHeadXRot() {
      return 1;
   }

   public int getMaxHeadYRot() {
      return 3;
   }

   public Entity getChild() {
      UUID id = this.getChildId();
      return id != null && !this.level().isClientSide() ? ((ServerLevel)this.level()).getEntity(id) : null;
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean checkSpawnObstruction(LevelReader worldIn) {
      return worldIn.isUnobstructed(this);
   }

   public void tick() {
      super.tick();
      if (this.passiveFor > 0) {
         this.passiveFor--;
      }

      if (this.isInWater()) {
         if (this.isLandNavigator) {
            this.switchNavigator(false);
         }
      } else if (!this.isLandNavigator) {
         this.switchNavigator(true);
      }

      this.prevStrangleProgress = this.strangleProgress;
      if (this.isStrangling()) {
         if (this.strangleProgress < 5.0F) {
            this.strangleProgress++;
         }
      } else if (this.strangleProgress > 0.0F) {
         this.strangleProgress--;
      }

      this.yBodyRot = this.getYRot();
      this.yHeadRot = Mth.clamp(this.yHeadRot, this.yBodyRot - 70.0F, this.yBodyRot + 70.0F);
      if (this.isStrangling()) {
         if (!this.level().isClientSide() && this.getTarget() != null && this.getTarget().isAlive()) {
            this.setXRot(0.0F);
            LivingEntity target = this.getTarget();
            float radius = this.getTarget().getBbWidth() * -0.5F;
            float angle = 0.017453292F * (target.yBodyRot - 45.0F);
            double extraX = radius * Mth.sin(3.1415927F + angle);
            double extraZ = radius * Mth.cos(angle);
            Vec3 targetVec = new Vec3(extraX + target.getX(), target.getY(1.0), extraZ + target.getZ());
            Vec3 moveVec = targetVec.subtract(this.position()).scale(1.0);
            this.setDeltaMovement(moveVec);
            if (!target.onGround()) {
               target.setDeltaMovement(new Vec3(0.0, -0.07999999821186066, 0.0));
            } else {
               target.setDeltaMovement(Vec3.ZERO);
            }

            if (this.strangleTimer >= 40 && this.strangleTimer % 20 == 0) {
               double health = Mth.clamp(this.getTarget().getMaxHealth(), 4.0F, 50.0F);
               this.getTarget().hurt(this.damageSources().mobAttack(this), (float)Math.max(4.0, 0.25 * health));
            }

            if (this.getTarget() == null || !this.getTarget().isAlive()) {
               this.strangleTimer = 0;
               this.setStrangling(false);
            }
         }

         this.fallDistance = 0.0F;
         this.strangleTimer++;
         this.setNoGravity(true);
      } else {
         this.setNoGravity(false);
      }

      if (this.ringBufferIndex < 0) {
         for (int i = 0; i < this.ringBuffer.length; i++) {
            this.ringBuffer[i] = this.getYRot();
         }
      }

      this.ringBufferIndex++;
      if (this.ringBufferIndex == this.ringBuffer.length) {
         this.ringBufferIndex = 0;
      }

      this.ringBuffer[this.ringBufferIndex] = this.getYRot();
      if (!this.level().isClientSide()) {
         int segments = 7;
         Entity child = this.getChild();
         if (child == null) {
            LivingEntity partParent = this;
            this.parts = new EntityAnacondaPart[7];
            AnacondaPartIndex partIndex = AnacondaPartIndex.HEAD;
            Vec3 prevPos = this.position();

            for (int i = 0; i < 7; i++) {
               float prevReqRot = this.calcPartRotation(i) + this.getYawForPart(i);
               float reqRot = this.calcPartRotation(i + 1) + this.getYawForPart(i);
               EntityAnacondaPart part = new EntityAnacondaPart(AMEntityRegistry.ANACONDA_PART.get(), this);
               part.setParent(partParent);
               part.copyDataFrom(this);
               part.setBodyIndex(i);
               part.setPartType(AnacondaPartIndex.sizeAt(1 + i));
               if (partParent == this) {
                  this.setChildId(part.getUUID());
                  this.entityData.set(CHILD_ID, part.getId());
               }

               if (partParent instanceof EntityAnacondaPart) {
                  ((EntityAnacondaPart)partParent).setChildId(part.getUUID());
               }

               part.setPos(part.tickMultipartPosition(this.getId(), partIndex, prevPos, this.getXRot(), prevReqRot, reqRot, false));
               partParent = part;
               this.level().addFreshEntity(part);
               this.parts[i] = part;
               partIndex = part.getPartType();
               prevPos = part.position();
            }
         }

         if (this.shouldReplaceParts() && this.getChild() instanceof EntityAnacondaPart) {
            this.parts = new EntityAnacondaPart[7];
            this.parts[0] = (EntityAnacondaPart)this.getChild();
            this.entityData.set(CHILD_ID, this.parts[0].getId());

            for (int i = 1; i < this.parts.length && this.parts[i - 1].getChild() instanceof EntityAnacondaPart; i++) {
               this.parts[i] = (EntityAnacondaPart)this.parts[i - 1].getChild();
            }
         }

         AnacondaPartIndex partIndex = AnacondaPartIndex.HEAD;
         Vec3 prev = this.position();
         float xRot = this.getXRot();

         for (int i = 0; i < 7; i++) {
            if (this.parts[i] != null) {
               float prevReqRotx = this.calcPartRotation(i) + this.getYawForPart(i);
               float reqRotx = this.calcPartRotation(i + 1) + this.getYawForPart(i);
               this.parts[i].setStrangleProgress(this.strangleProgress);
               this.parts[i].copyDataFrom(this);
               prev = this.parts[i].tickMultipartPosition(this.getId(), partIndex, prev, xRot, prevReqRotx, reqRotx, true);
               partIndex = this.parts[i].getPartType();
               xRot = this.parts[i].getXRot();
            }
         }

         if (this.isInWater()) {
            this.swimTimer = Math.max(this.swimTimer + 1, 0);
         } else {
            this.swimTimer = Math.min(this.swimTimer - 1, 0);
         }
      }

      if (this.shedCooldown > 0) {
         this.shedCooldown--;
      }

      if (this.getSheddingTime() > 0) {
         this.setSheddingTime(this.getSheddingTime() - 1);
         if (this.getSheddingTime() == 0) {
            this.spawnItemAtOffset(new ItemStack((ItemLike)AMItemRegistry.SHED_SNAKE_SKIN.get()), 1.0F + this.random.nextFloat(), 0.2F);
            this.shedCooldown = 1000 + this.random.nextInt(2000);
         }
      }
   }

   private boolean shouldReplaceParts() {
      if (this.parts != null && this.parts[0] != null) {
         for (int i = 0; i < 7; i++) {
            if (this.parts[i] == null) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private float getYawForPart(int i) {
      return this.getRingBuffer(4 + i * 2, 1.0F);
   }

   public float getRingBuffer(int bufferOffset, float partialTicks) {
      if (this.isDeadOrDying()) {
         partialTicks = 0.0F;
      }

      partialTicks = 1.0F - partialTicks;
      int i = this.ringBufferIndex - bufferOffset & 63;
      int j = this.ringBufferIndex - bufferOffset - 1 & 63;
      float d0 = this.ringBuffer[i];
      float d1 = this.ringBuffer[j] - d0;
      return Mth.wrapDegrees(d0 + d1 * partialTicks);
   }

   public float getScale() {
      return this.isBaby() ? 0.75F : 1.0F;
   }

   public boolean isPushable() {
      return !this.isStrangling();
   }

   public boolean shouldMove() {
      return !this.isStrangling();
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.ANACONDA_FOODSTUFFS);
   }

   public void travel(Vec3 travelVector) {
      if (!this.shouldMove()) {
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
            if (this.getTarget() == null) {
               this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
            }
         } else {
            super.travel(travelVector);
         }
      }
   }

   public float getStrangleProgress(float partialTick) {
      return this.prevStrangleProgress + (this.strangleProgress - this.prevStrangleProgress) * partialTick;
   }

   private float calcPartRotation(int i) {
      float f = 1.0F - this.strangleProgress * 0.2F;
      float strangleIntensity = (float)(Mth.clamp(this.strangleTimer * 3, 0.0F, 100.0F) * (1.0 + 0.20000000298023224 * Math.sin(0.15F * this.strangleTimer)));
      return (float)(40.0 * -Math.sin(this.walkDist * 3.0F - i)) * f + this.strangleProgress * 0.2F * i * strangleIntensity;
   }

   @Nullable
   public ItemEntity spawnItemAtOffset(ItemStack stack, float f, float f1) {
      if (stack.isEmpty()) {
         return null;
      } else if (this.level().isClientSide()) {
         return null;
      } else {
         Vec3 vec = new Vec3(0.0, 0.0, f).yRot(-f * 0.017453292F);
         ItemEntity itementity = new ItemEntity(this.level(), this.getX() + vec.x, this.getY() + f1, this.getZ() + vec.z, stack);
         itementity.setDefaultPickUpDelay();
         if (this.captureDrops() != null) {
            this.captureDrops().add(itementity);
         } else {
            this.level().addFreshEntity(itementity);
         }

         return itementity;
      }
   }

   @Override
   public boolean shouldEnterWater() {
      return this.getTarget() == null && !this.shouldLeaveWater() && this.swimTimer <= -1000;
   }

   @Override
   public boolean shouldLeaveWater() {
      if (!this.getPassengers().isEmpty()) {
         return false;
      } else {
         return this.getTarget() != null && !this.getTarget().isInWater() ? true : this.swimTimer > 600 || this.isShedding();
      }
   }

   @Override
   public boolean shouldStopMoving() {
      return !this.shouldMove();
   }

   @Override
   public int getWaterSearchRange() {
      return 12;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob mob) {
      EntityAnaconda anaconda = AMCompat.create(AMEntityRegistry.ANACONDA.get(), serverWorld);
      anaconda.setYellow(this.isYellow());
      return anaconda;
   }

   public void awardKillScore(Entity entity, int score, DamageSource src) {
      if (entity instanceof LivingEntity living) {
         CompoundTag emptyNbt = new CompoundTag();
         AMCompat.saveAdditionalTo(living, emptyNbt);
         emptyNbt.putString("DeathLootTable", "minecraft:empty");
         AMCompat.readAdditionalFrom(living, emptyNbt);
         if (this.getChild() instanceof EntityAnacondaPart) {
            ((EntityAnacondaPart)this.getChild()).setSwell(5.0F);
         }
      }

      super.awardKillScore(entity, score, src);
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
   }

   public boolean canAttack(LivingEntity livingEntity) {
      boolean prev = super.canAttack(livingEntity);
      return !prev
            || this.passiveFor <= 0
            || !(livingEntity instanceof Player)
            || this.getLastHurtByMob() != null && this.getLastHurtByMob().getUUID().equals(livingEntity.getUUID())
         ? prev
         : false;
   }

   public void feed() {
      this.heal(10.0F);
      this.feedings++;
      if (this.feedings >= 3 && this.feedings % 3 == 0 && this.shedCooldown <= 0) {
         this.setSheddingTime(this.getRandom().nextInt(500) + 500);
      }
   }

   public boolean isShedding() {
      return this.getSheddingTime() > 0;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      this.setYellow(this.random.nextBoolean());
      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   private class AIMelee extends Goal {
      private final EntityAnaconda snake;
      private int jumpAttemptCooldown = 0;

      public AIMelee() {
         this.snake = EntityAnaconda.this;
      }

      public boolean canUse() {
         return this.snake.getTarget() != null && this.snake.getTarget().isAlive();
      }

      public void tick() {
         if (this.jumpAttemptCooldown > 0) {
            this.jumpAttemptCooldown--;
         }

         LivingEntity target = this.snake.getTarget();
         if (target != null && target.isAlive()) {
            if (this.jumpAttemptCooldown == 0 && this.snake.distanceTo(target) < 1.0F + target.getBbWidth() && !this.snake.isStrangling()) {
               target.hurt(this.snake.damageSources().mobAttack(this.snake), 4.0F);
               this.snake.setStrangling(target.getBbWidth() <= 2.0F && !(target instanceof EntityAnaconda));
               this.snake.playSound(AMSoundRegistry.ANACONDA_ATTACK.get(), this.snake.getSoundVolume(), this.snake.getVoicePitch());
               this.jumpAttemptCooldown = 5 + EntityAnaconda.this.random.nextInt(5);
            }

            if (this.snake.isStrangling()) {
               this.snake.getNavigation().stop();
            } else {
               try {
                  this.snake.getNavigation().moveTo(target, 1.2999999523162842);
               } catch (Exception var3) {
                  var3.printStackTrace();
               }
            }
         }
      }

      public void stop() {
         this.snake.setStrangling(false);
      }
   }
}
