package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockTerrapinEgg;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalSwimMoveControllerSink;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.entity.util.TerrapinTypes;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityTerrapinEgg;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
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
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EntityTerrapin extends Animal implements ISemiAquatic, Bucketable {
   private static final EntityDataAccessor<Integer> TURTLE_TYPE = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SHELL_TYPE = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SKIN_TYPE = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> TURTLE_COLOR = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SHELL_COLOR = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SKIN_COLOR = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> RETREATED = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SPINNING = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.BOOLEAN);
   public float clientSpin = 0.0F;
   public int spinCounter = 0;
   public float prevSwimProgress;
   public float swimProgress;
   public float prevRetreatProgress;
   public float retreatProgress;
   public float prevSpinProgress;
   public float spinProgress;
   private int maxRollTime = 50;
   private boolean isLandNavigator;
   private int swimTimer = -1000;
   private int hideInShellTimer = 0;
   private Vec3 spinDelta;
   private float spinYRot;
   private int changeSpinAngleCooldown = 0;
   private LivingEntity lastLauncher = null;
   private TileEntityTerrapinEgg.ParentData partnerData;

   protected EntityTerrapin(EntityType animal, Level level) {
      super(animal, level);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
      this.switchNavigator(true);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.ARMOR, 10.0).add(Attributes.MOVEMENT_SPEED, 0.10000000149011612);
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.TERRAPIN_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.TERRAPIN_HURT.get();
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.terrapinSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static boolean canTerrapinSpawn(
      EntityType<EntityTerrapin> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return reason == MobSpawnType.SPAWNER || iServerWorld.getBlockState(pos).getFluidState().is(Fluids.WATER);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new BreathAirGoal(this));
      this.goalSelector.addGoal(1, new EntityTerrapin.MateGoal(this, 1.0));
      this.goalSelector.addGoal(1, new EntityTerrapin.LayEggGoal(this, 1.0));
      this.goalSelector.addGoal(2, new TemptGoal(this, 1.1, AMCompat.ingredientOf(AMTagRegistry.TERRAPIN_BREEDABLES), false));
      this.goalSelector.addGoal(3, new AnimalAIFindWater(this));
      this.goalSelector.addGoal(3, new AnimalAILeaveWater(this));
      this.goalSelector.addGoal(4, new SemiAquaticAIRandomSwimming(this, 1.0, 30));
      this.goalSelector.addGoal(6, new PanicGoal(this, 1.1));
      this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0, 60));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
   }

   public void tick() {
      super.tick();
      this.prevSwimProgress = this.swimProgress;
      this.prevRetreatProgress = this.retreatProgress;
      this.prevSpinProgress = this.spinProgress;
      boolean inWaterOrBubble = this.isInWaterOrBubble();
      boolean spinning = this.isSpinning();
      boolean retreated = this.hasRetreated();
      if (inWaterOrBubble) {
         if (this.swimProgress < 5.0F) {
            this.swimProgress++;
         }
      } else if (this.swimProgress > 0.0F) {
         this.swimProgress--;
      }

      if (spinning) {
         if (this.spinProgress < 5.0F) {
            this.spinProgress++;
         }
      } else if (this.spinProgress > 0.0F) {
         this.spinProgress--;
      }

      if (retreated) {
         if (this.retreatProgress < 5.0F) {
            this.retreatProgress++;
         }
      } else if (this.retreatProgress > 0.0F) {
         this.retreatProgress--;
      }

      if (spinning) {
         this.handleSpin();
         if (this.isAlive() && this.spinCounter > 5 && !this.isBaby()) {
            for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.30000001192092896))) {
               if (!this.isAlliedTo(entity) && !(entity instanceof EntityTerrapin)) {
                  entity.hurt(
                     this.damageSources().mobAttack((LivingEntity)(this.lastLauncher == null ? this : this.lastLauncher)),
                     4.0F + this.random.nextFloat() * 4.0F
                  );
               }
            }
         }

         if (!this.isAlive()) {
            this.setSpinning(false);
         }

         if (this.horizontalCollision) {
            if (this.changeSpinAngleCooldown == 0) {
               this.changeSpinAngleCooldown = 10;
               float f = this.collideDirectionAndSound().getAxis() == Axis.X ? this.spinYRot - 180.0F : 180.0F - this.spinYRot;
               f += this.random.nextInt(40) - 20;
               this.setYRot(f);
               this.copySpinDelta(f, Vec3.ZERO);
            } else {
               this.maxRollTime -= 30;
            }
         }

         if (this.changeSpinAngleCooldown > 0) {
            this.changeSpinAngleCooldown--;
         }
      }

      if (!this.level().isClientSide()) {
         if (this.isInWaterOrBubble() && this.isLandNavigator) {
            this.switchNavigator(false);
         }

         if (!this.isInWaterOrBubble() && !this.isLandNavigator) {
            this.switchNavigator(true);
         }

         if (this.isInWater()) {
            this.swimTimer = Math.max(0, this.swimTimer + 1);
         } else {
            this.swimTimer = Math.min(0, this.swimTimer - 1);

            for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(0.0, 0.15000000596046448, 0.0))) {
               if ((player.jumping || !player.onGround()) && player.getY() > this.getEyeY()) {
                  if (!this.hasRetreated()) {
                     this.hideInShellTimer = this.hideInShellTimer + 40 + this.random.nextInt(40);
                  } else if (!this.isSpinning()) {
                     this.lastLauncher = player;
                     int spin = 100 + this.random.nextInt(100);
                     this.hideInShellTimer = spin;
                     this.setYRot(player.getYHeadRot());
                     this.spinFor(spin);
                  }
               }
            }
         }

         if (this.swimProgress > 0.0F) {
            AMCompat.setMaxUpStep(this, 1.0F);
         } else {
            AMCompat.setMaxUpStep(this, 0.6F);
         }

         if (this.hideInShellTimer > 0) {
            this.hideInShellTimer--;
         }

         this.setRetreated(this.hideInShellTimer > 0 && !this.isSpinning());
      }
   }

   private Direction collideDirectionAndSound() {
      HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, entity -> false);
      if (raytraceresult instanceof BlockHitResult) {
         BlockState state = this.level().getBlockState(((BlockHitResult)raytraceresult).getBlockPos());
         if (state != null && !this.isSilent()) {
         }

         return ((BlockHitResult)raytraceresult).getDirection();
      } else {
         return Direction.DOWN;
      }
   }

   private boolean isMoving() {
      return this.getDeltaMovement().lengthSqr() > 0.02;
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigation(this, this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new AnimalSwimMoveControllerSink(this, 2.5F, 1.15F);
         this.navigation = new SemiAquaticPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(TURTLE_TYPE, 0);
      builder.define(SHELL_TYPE, 0);
      builder.define(SKIN_TYPE, 0);
      builder.define(SHELL_COLOR, 0);
      builder.define(SKIN_COLOR, 0);
      builder.define(TURTLE_COLOR, 0);
      builder.define(RETREATED, false);
      builder.define(SPINNING, false);
      builder.define(HAS_EGG, false);
      builder.define(FROM_BUCKET, false);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("TurtleType", this.getTurtleTypeOrdinal());
      compound.putInt("ShellType", this.getShellType());
      compound.putInt("SkinType", this.getSkinType());
      compound.putInt("TurtleColor", this.getTurtleColor());
      compound.putInt("ShellColor", this.getShellColor());
      compound.putInt("SkinColor", this.getSkinColor());
      compound.putBoolean("HasEgg", this.hasEgg());
      compound.putBoolean("Bucketed", this.fromBucket());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setTurtleTypeOrdinal(AMCompat.getInt(compound, "TurtleType"));
      this.setShellType(AMCompat.getInt(compound, "ShellType"));
      this.setSkinType(AMCompat.getInt(compound, "SkinType"));
      this.setTurtleColor(AMCompat.getInt(compound, "TurtleColor"));
      this.setShellColor(AMCompat.getInt(compound, "ShellColor"));
      this.setSkinColor(AMCompat.getInt(compound, "SkinColor"));
      this.setHasEgg(AMCompat.getBoolean(compound, "HasEgg"));
      this.setFromBucket(AMCompat.getBoolean(compound, "Bucketed"));
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      if (!this.isSpinning()) {
         super.playStepSound(pos, state);
      }
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.TERRAPIN_BREEDABLES);
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

   public boolean removeWhenFarAway(double d) {
      return !this.fromBucket() && !this.hasCustomName();
   }

   private int getTurtleTypeOrdinal() {
      return Mth.clamp((Integer)this.entityData.get(TURTLE_TYPE), 0, TerrapinTypes.values().length - 1);
   }

   private void setTurtleTypeOrdinal(int i) {
      this.entityData.set(TURTLE_TYPE, i);
   }

   public int getShellType() {
      return (Integer)this.entityData.get(SHELL_TYPE);
   }

   public void setShellType(int i) {
      this.entityData.set(SHELL_TYPE, i);
   }

   public int getSkinType() {
      return (Integer)this.entityData.get(SKIN_TYPE);
   }

   public void setSkinType(int i) {
      this.entityData.set(SKIN_TYPE, i);
   }

   public int getShellColor() {
      return (Integer)this.entityData.get(SHELL_COLOR);
   }

   public void setShellColor(int i) {
      this.entityData.set(SHELL_COLOR, i);
   }

   public int getSkinColor() {
      return (Integer)this.entityData.get(SKIN_COLOR);
   }

   public void setSkinColor(int i) {
      this.entityData.set(SKIN_COLOR, i);
   }

   public int getTurtleColor() {
      return (Integer)this.entityData.get(TURTLE_COLOR);
   }

   public void setTurtleColor(int i) {
      this.entityData.set(TURTLE_COLOR, i);
   }

   public TerrapinTypes getTurtleType() {
      return TerrapinTypes.values()[this.getTurtleTypeOrdinal()];
   }

   public void setTurtleType(TerrapinTypes type) {
      this.setTurtleTypeOrdinal(type.ordinal());
   }

   public boolean isSpinning() {
      return (Boolean)this.entityData.get(SPINNING);
   }

   public void setSpinning(boolean b) {
      this.entityData.set(SPINNING, b);
   }

   public boolean hasRetreated() {
      return (Boolean)this.entityData.get(RETREATED);
   }

   public void setRetreated(boolean b) {
      this.entityData.set(RETREATED, b);
   }

   public boolean hasEgg() {
      return (Boolean)this.entityData.get(HAS_EGG);
   }

   private void setHasEgg(boolean hasEgg) {
      this.entityData.set(HAS_EGG, hasEgg);
   }

   public int getMaxAirSupply() {
      return 4800;
   }

   protected int increaseAirSupply(int currentAir) {
      return this.getMaxAirSupply();
   }

   public void push(Entity entity) {
      if (!this.isInWaterOrBubble() && !(entity instanceof EntityTerrapin)) {
         entity.setDeltaMovement(entity.getDeltaMovement().add(this.getDeltaMovement()));
      } else {
         super.push(entity);
      }
   }

   public boolean canBeCollidedWith() {
      return AMCompat.isFullyConstructed(this) && (this.isInWaterOrBubble() ? super.canBeCollidedWith() : this.isAlive());
   }

   private void spinFor(int time) {
      this.maxRollTime = time;
      this.setSpinning(true);
   }

   private void copySpinDelta(float spinRot, Vec3 motionIn) {
      float f = spinRot * 0.017453292F;
      float f1 = this.isBaby() ? 0.3F : 0.5F;
      this.spinYRot = spinRot;
      this.spinDelta = new Vec3(motionIn.x + -Mth.sin(f) * f1, 0.0, motionIn.z + Mth.cos(f) * f1);
      this.setDeltaMovement(this.spinDelta.add(0.0, 0.0, 0.0));
   }

   private void handleSpin() {
      this.setRetreated(true);
      this.spinCounter++;
      if (!this.level().isClientSide()) {
         if (this.spinCounter > this.maxRollTime) {
            this.setSpinning(false);
            this.hideInShellTimer = 10 + this.random.nextInt(30);
            this.spinCounter = 0;
         } else {
            Vec3 vec3 = this.getDeltaMovement();
            if (this.spinCounter == 1) {
               this.copySpinDelta(this.getYRot(), vec3);
            } else {
               this.setYRot(this.spinYRot);
               this.setYHeadRot(this.spinYRot);
               this.setYBodyRot(this.spinYRot);
               this.setDeltaMovement(this.spinDelta.x, vec3.y, this.spinDelta.z);
            }
         }
      }
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      this.setAirSupply(this.getMaxAirSupply());
      this.setTurtleType(TerrapinTypes.getRandomType(this.random));
      this.setShellType(this.random.nextInt(7));
      this.setSkinType(this.random.nextInt(4));
      this.setTurtleColor(TerrapinTypes.generateRandomColor(this.random));
      this.setShellColor(TerrapinTypes.generateRandomColor(this.random));
      this.setSkinColor(TerrapinTypes.generateRandomColor(this.random));
      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
      return AMCompat.create(AMEntityRegistry.TERRAPIN.get(), p_146743_);
   }

   @Override
   public boolean shouldStopMoving() {
      return this.isSpinning() || this.hasRetreated();
   }

   @Override
   public boolean shouldEnterWater() {
      return this.getTarget() == null && !this.shouldLeaveWater() && this.swimTimer <= -1000;
   }

   @Override
   public boolean shouldLeaveWater() {
      return this.swimTimer > 600 || this.hasEgg();
   }

   @Override
   public int getWaterSearchRange() {
      return 10;
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public void travel(Vec3 travelVector) {
      if (this.shouldStopMoving()) {
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

   @Nonnull
   public ItemStack getBucketItemStack() {
      ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.TERRAPIN_BUCKET.get());
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
      AMCompat.put(compound, "TerrapinData", platTag);
      AMCompat.setTag(bucket, compound);
   }

   public void loadFromBucketTag(@Nonnull CompoundTag compound) {
      if (AMCompat.contains(compound, "TerrapinData")) {
         AMCompat.readAdditionalFrom(this, AMCompat.getCompound(compound, "TerrapinData"));
      }
   }

   @Nonnull
   public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      if (itemstack.is(AMTagRegistry.TERRAPIN_BREEDABLES)) {
         this.setPersistenceRequired();
      }

      return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
   }

   public void calculateEntityAnimation(boolean flying) {
      float f1 = (float)Mth.length(this.getX() - this.xo, 0.0, this.getZ() - this.zo);
      float f2 = Math.min(f1 * (this.isSpinning() ? 4.0F : 32.0F), 1.0F);
      this.walkAnimation.update(f2, 0.4F);
   }

   public boolean isKoopa() {
      String s = ChatFormatting.stripFormatting(this.getName().getString());
      return s != null && s.toLowerCase().contains("koopa");
   }

   public boolean checkSpawnObstruction(LevelReader worldIn) {
      return worldIn.isUnobstructed(this);
   }

   static class LayEggGoal extends MoveToBlockGoal {
      private final EntityTerrapin turtle;
      private int digTime;

      LayEggGoal(EntityTerrapin turtle, double speedIn) {
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
         BlockPos blockpos = this.turtle.blockPosition();
         this.turtle.swimTimer = 1000;
         if (!this.turtle.isInWater() && this.isReachedTarget()) {
            Level world = this.turtle.level();
            this.turtle.gameEvent(GameEvent.BLOCK_PLACE);
            world.playSound(null, blockpos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + world.getRandom().nextFloat() * 0.2F);
            world.setBlock(
               this.blockPos.above(),
               (BlockState)AMBlockRegistry.TERRAPIN_EGG.get().defaultBlockState().setValue(BlockTerrapinEgg.EGGS, this.turtle.random.nextInt(1) + 3),
               3
            );
            if (world.getBlockEntity(this.blockPos.above()) instanceof TileEntityTerrapinEgg eggTe) {
               eggTe.parent1 = new TileEntityTerrapinEgg.ParentData(
                  this.turtle.getTurtleType(),
                  this.turtle.getShellType(),
                  this.turtle.getSkinType(),
                  this.turtle.getTurtleColor(),
                  this.turtle.getShellColor(),
                  this.turtle.getSkinColor()
               );
               eggTe.parent2 = this.turtle.partnerData == null ? eggTe.parent1 : this.turtle.partnerData;
            }

            this.turtle.setHasEgg(false);
            this.turtle.setInLoveTime(600);
         }
      }

      protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
         return worldIn.isEmptyBlock(pos.above()) && BlockTerrapinEgg.isProperHabitat(worldIn, pos);
      }
   }

   static class MateGoal extends BreedGoal {
      private final EntityTerrapin turtle;

      MateGoal(EntityTerrapin turtle, double speedIn) {
         super(turtle, speedIn);
         this.turtle = turtle;
      }

      public boolean canUse() {
         return super.canUse() && !this.turtle.hasEgg();
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

         if (this.partner instanceof EntityTerrapin terrapin) {
            this.turtle.partnerData = new TileEntityTerrapinEgg.ParentData(
               terrapin.getTurtleType(),
               terrapin.getShellType(),
               terrapin.getSkinType(),
               terrapin.getTurtleColor(),
               terrapin.getShellColor(),
               terrapin.getSkinColor()
            );
         }

         this.turtle.setHasEgg(true);
         this.animal.resetLove();
         this.partner.resetLove();
         this.animal.setAge(6000);
         this.partner.setAge(6000);
         RandomSource random = this.animal.getRandom();
         if (AMCompat.gameRule(this.level, AMCompat.Rule.MOB_LOOT)) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
         }
      }
   }
}
