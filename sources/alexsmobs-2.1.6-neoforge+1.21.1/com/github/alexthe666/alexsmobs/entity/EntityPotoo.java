package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.citadel.server.entity.pathfinding.raycoms.AdvancedPathNavigate;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoMountPlayer;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public class EntityPotoo extends Animal implements IFalconry {
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityPotoo.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> PERCHING = SynchedEntityData.defineId(EntityPotoo.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(EntityPotoo.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Optional<BlockPos>> PERCH_POS = SynchedEntityData.defineId(
      EntityPotoo.class, EntityDataSerializers.OPTIONAL_BLOCK_POS
   );
   private static final EntityDataAccessor<Direction> PERCH_DIRECTION = SynchedEntityData.defineId(EntityPotoo.class, EntityDataSerializers.DIRECTION);
   private static final EntityDataAccessor<Integer> MOUTH_TICK = SynchedEntityData.defineId(EntityPotoo.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> TEMP_BRIGHTNESS = SynchedEntityData.defineId(EntityPotoo.class, EntityDataSerializers.INT);
   public final float[] ringBuffer = new float[64];
   public float prevFlyProgress;
   public float flyProgress;
   public float mouthProgress;
   public float prevMouthProgress;
   public float prevPerchProgress;
   public float perchProgress;
   public int ringBufferIndex = -1;
   private int lastScreamTimestamp;
   private int perchCooldown = 100;
   private boolean isLandNavigator;
   private int timeFlying;

   protected EntityPotoo(EntityType type, Level level) {
      super(type, level);
      this.switchNavigator(true);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 8.0)
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new TemptGoal(this, 1.0, AMCompat.ingredientOf(AMTagRegistry.POTOO_BREEDABLES), false));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(3, new PanicGoal(this, 1.0));
      this.goalSelector.addGoal(4, new EntityPotoo.AIPerch());
      this.goalSelector.addGoal(5, new EntityPotoo.AIMelee());
      this.goalSelector.addGoal(6, new EntityPotoo.AIFlyIdle());
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, EntityFly.class, 100, true, true, null));
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = new AdvancedPathNavigateNoTeleport(this, this.level(), false);
         this.isLandNavigator = true;
      } else {
         this.moveControl = new FlightMoveController(this, 0.6F, false, true);
         this.navigation = new AdvancedPathNavigateNoTeleport(this, this.level(), AdvancedPathNavigate.MovementType.FLYING, false, false) {
            public boolean isStableDestination(BlockPos pos) {
               return !this.level.getBlockState(pos.below(2)).isAir();
            }
         };
         this.navigation.setCanFloat(false);
         this.isLandNavigator = false;
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FLYING, false);
      builder.define(PERCHING, false);
      builder.define(PERCH_POS, Optional.empty());
      builder.define(PERCH_DIRECTION, Direction.NORTH);
      builder.define(SLEEPING, false);
      builder.define(MOUTH_TICK, 0);
      builder.define(TEMP_BRIGHTNESS, 0);
   }

   public boolean isSleeping() {
      return (Boolean)this.entityData.get(SLEEPING);
   }

   public void setSleeping(boolean sleeping) {
      this.entityData.set(SLEEPING, sleeping);
   }

   public static boolean canPotooSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      return isBrightEnoughToSpawn(worldIn, pos);
   }

   public boolean checkSpawnObstruction(LevelReader reader) {
      if (reader.isUnobstructed(this) && !reader.containsAnyLiquid(this.getBoundingBox())) {
         BlockPos blockpos = this.blockPosition();
         BlockState blockstate2 = reader.getBlockState(blockpos.below());
         return blockstate2.is(BlockTags.LEAVES) || blockstate2.is(BlockTags.LOGS);
      } else {
         return false;
      }
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.potooSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public void tick() {
      super.tick();
      this.prevPerchProgress = this.perchProgress;
      this.prevMouthProgress = this.mouthProgress;
      this.prevFlyProgress = this.flyProgress;
      if (this.isFlying()) {
         if (this.flyProgress < 5.0F) {
            this.flyProgress++;
         }
      } else if (this.flyProgress > 0.0F) {
         this.flyProgress--;
      }

      if (this.isPerching()) {
         if (this.perchProgress < 5.0F) {
            this.perchProgress++;
         }
      } else if (this.perchProgress > 0.0F) {
         this.perchProgress--;
      }

      if (this.ringBufferIndex < 0) {
         Arrays.fill(this.ringBuffer, 15.0F);
      }

      this.ringBufferIndex++;
      if (this.ringBufferIndex == this.ringBuffer.length) {
         this.ringBufferIndex = 0;
      }

      this.ringBuffer[this.ringBufferIndex] = ((Integer)this.entityData.get(TEMP_BRIGHTNESS)).intValue();
      if (this.perchCooldown > 0) {
         this.perchCooldown--;
      }

      if (!this.level().isClientSide()) {
         this.entityData.set(TEMP_BRIGHTNESS, this.level().getMaxLocalRawBrightness(this.blockPosition()));
         if (this.isFlying()) {
            if (this.isLandNavigator) {
               this.switchNavigator(false);
            }
         } else if (!this.isLandNavigator) {
            this.switchNavigator(true);
         }

         if (this.isFlying()) {
            if (!this.onGround()) {
               if (!this.isInWaterOrBubble()) {
                  this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6000000238418579, 1.0));
               }
            } else if (this.timeFlying > 20) {
               this.setFlying(false);
            }

            this.timeFlying++;
         } else {
            this.timeFlying = 0;
         }

         if (this.isPerching() && !this.isVehicle()) {
            this.setSleeping(this.level().isDay() && (this.getTarget() == null || !this.getTarget().isAlive()));
         } else if (this.isSleeping()) {
            this.setSleeping(false);
         }

         if (this.isPerching() && this.getPerchPos() != null) {
            if (this.level().getBlockState(this.getPerchPos()).is(AMTagRegistry.POTOO_PERCHES)
               && !(this.distanceToSqr(Vec3.atCenterOf(this.getPerchPos())) > 2.25)) {
               this.slideTowardsPerch();
            } else {
               this.setPerching(false);
            }
         }
      }

      if ((Integer)this.entityData.get(MOUTH_TICK) > 0) {
         this.entityData.set(MOUTH_TICK, (Integer)this.entityData.get(MOUTH_TICK) - 1);
         if (this.mouthProgress < 5.0F) {
            this.mouthProgress++;
         }
      } else if (this.mouthProgress > 0.0F) {
         this.mouthProgress--;
      }

      if (!this.isSleeping() && (this.getTarget() == null || !this.getTarget().isAlive())) {
         int j = this.tickCount - this.lastScreamTimestamp;
         if (this.getEyeScale(10, 1.0F) == 0.0F) {
            if (j > 40) {
               this.openMouth(30);
               this.playSound(AMSoundRegistry.POTOO_CALL.get());
               this.gameEvent(AMPlatform.ENTITY_ACTION);
            }
         } else if (this.getEyeScale(10, 1.0F) < 7.0F && j > 300 && j % 300 == 0 && this.random.nextInt(4) == 0) {
            this.openMouth(30);
            this.playSound(AMSoundRegistry.POTOO_CALL.get());
            this.gameEvent(AMPlatform.ENTITY_ACTION);
         }
      }
   }

   @Override
   public float getHandOffset() {
      return 1.0F;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.POTOO_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.POTOO_HURT.get();
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean prev = super.hurt(source, amount);
      if (prev && source.getDirectEntity() instanceof LivingEntity) {
         this.setPerching(false);
      }

      return prev;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
   }

   public void rideTick() {
      Entity entity = this.getVehicle();
      if (!this.isPassenger() || entity.isAlive() && this.isAlive()) {
         if (entity instanceof LivingEntity) {
            this.setDeltaMovement(0.0, 0.0, 0.0);
            this.tick();
            this.setFlying(false);
            this.setSleeping(false);
            this.setPerching(false);
            if (this.isPassenger()) {
               Entity mount = this.getVehicle();
               if (mount instanceof Player) {
                  float yawAdd = 0.0F;
                  if (((Player)mount).getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get()) {
                     yawAdd = ((Player)mount).getMainArm() == HumanoidArm.LEFT ? 135.0F : -135.0F;
                  } else if (((Player)mount).getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get()) {
                     yawAdd = ((Player)mount).getMainArm() == HumanoidArm.LEFT ? -135.0F : 135.0F;
                  } else {
                     this.removeVehicle();
                     this.copyPosition(mount);
                  }

                  float birdYaw = yawAdd * 0.5F;
                  this.yBodyRot = Mth.wrapDegrees(((LivingEntity)mount).yBodyRot + birdYaw);
                  this.setYRot(Mth.wrapDegrees(mount.getYRot() + birdYaw));
                  this.yHeadRot = Mth.wrapDegrees(((LivingEntity)mount).yHeadRot + birdYaw);
                  float radius = 0.6F;
                  float angle = 0.017453292F * (((LivingEntity)mount).yBodyRot - 180.0F + yawAdd);
                  double extraX = radius * Mth.sin(3.1415927F + angle);
                  double extraZ = radius * Mth.cos(angle);
                  this.setPos(mount.getX() + extraX, Math.max(mount.getY() + mount.getBbHeight() * 0.45F, mount.getY()), mount.getZ() + extraZ);
               }

               if (!mount.isAlive()) {
                  this.removeVehicle();
               }
            }
         } else {
            super.rideTick();
         }
      } else {
         this.stopRiding();
      }
   }

   public void openMouth(int duration) {
      this.entityData.set(MOUTH_TICK, duration);
      this.lastScreamTimestamp = this.tickCount;
   }

   public boolean isFlying() {
      return (Boolean)this.entityData.get(FLYING);
   }

   public void setFlying(boolean flying) {
      if (!flying || !this.isBaby()) {
         this.entityData.set(FLYING, flying);
      }
   }

   public BlockPos getPerchPos() {
      return (BlockPos)((Optional)this.entityData.get(PERCH_POS)).orElse(null);
   }

   public void setPerchPos(BlockPos pos) {
      this.entityData.set(PERCH_POS, Optional.ofNullable(pos));
   }

   public Direction getPerchDirection() {
      return (Direction)this.entityData.get(PERCH_DIRECTION);
   }

   public void setPerchDirection(Direction direction) {
      this.entityData.set(PERCH_DIRECTION, direction);
   }

   public boolean isPerching() {
      return (Boolean)this.entityData.get(PERCHING);
   }

   public void setPerching(boolean perching) {
      this.entityData.set(PERCHING, perching);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Flying", this.isFlying());
      compound.putBoolean("Perching", this.isPerching());
      compound.putInt("PerchDir", this.getPerchDirection().ordinal());
      if (this.getPerchPos() != null) {
         compound.putInt("PerchX", this.getPerchPos().getX());
         compound.putInt("PerchY", this.getPerchPos().getY());
         compound.putInt("PerchZ", this.getPerchPos().getZ());
      }
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFlying(AMCompat.getBoolean(compound, "Flying"));
      this.setPerching(AMCompat.getBoolean(compound, "Perching"));
      this.setPerchDirection(Direction.from3DDataValue(AMCompat.getInt(compound, "PerchDir")));
      if (AMCompat.contains(compound, "PerchX") && AMCompat.contains(compound, "PerchY") && AMCompat.contains(compound, "PerchZ")) {
         this.setPerchPos(new BlockPos(AMCompat.getInt(compound, "PerchX"), AMCompat.getInt(compound, "PerchY"), AMCompat.getInt(compound, "PerchZ")));
      }
   }

   public boolean isValidPerchFromSide(BlockPos pos, Direction direction) {
      BlockPos offset = pos.relative(direction);
      BlockState state = this.level().getBlockState(pos);
      return state.is(AMTagRegistry.POTOO_PERCHES)
         && (!this.level().getBlockState(pos.above()).isCollisionShapeFullBlock(this.level(), pos.above()) || this.level().isEmptyBlock(pos.above()))
         && (
            !this.level().getBlockState(offset).isCollisionShapeFullBlock(this.level(), offset)
                  && !this.level().getBlockState(offset).is(AMTagRegistry.POTOO_PERCHES)
               || this.level().isEmptyBlock(offset)
         );
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
      return AMCompat.create(AMEntityRegistry.POTOO.get(), serverLevel);
   }

   public float getEyeScale(int bufferOffset, float partialTicks) {
      int i = this.ringBufferIndex - bufferOffset & 63;
      int j = this.ringBufferIndex - bufferOffset - 1 & 63;
      float prevBuffer = this.ringBuffer[j];
      float buffer = this.ringBuffer[i];
      return prevBuffer + (buffer - prevBuffer) * partialTicks;
   }

   private void slideTowardsPerch() {
      Vec3 block = Vec3.upFromBottomCenterOf(this.getPerchPos(), 1.0);
      Vec3 look = block.subtract(this.position()).normalize();
      Vec3 onBlock = block.add(this.getPerchDirection().getStepX() * 0.35F, 0.0, this.getPerchDirection().getStepZ() * 0.35F);
      Vec3 diff = onBlock.subtract(this.position());
      float f = (float)diff.length();
      float f1 = f > 1.0F ? 0.25F : f * 0.1F;
      Vec3 sub = diff.normalize().scale(f1);
      float f2 = -((float)(Mth.atan2(look.x, look.z) * 57.2957763671875));
      this.setYRot(f2);
      this.yHeadRot = f2;
      this.yBodyRot = f2;
      this.setDeltaMovement(this.getDeltaMovement().add(sub));
   }

   public BlockPos getToucanGround(BlockPos in) {
      BlockPos position = new BlockPos(in.getX(), (int)this.getY(), in.getZ());

      while (position.getY() < 320 && !this.level().getFluidState(position).isEmpty()) {
         position = position.above();
      }

      while (position.getY() > -64 && !this.level().getBlockState(position).isSolid() && this.level().getFluidState(position).isEmpty()) {
         position = position.below();
      }

      return position;
   }

   public Vec3 getBlockGrounding(Vec3 fleePos) {
      float radius = 10 + this.getRandom().nextInt(15);
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = AMBlockPos.fromCoords(fleePos.x() + extraX, this.getY(), fleePos.z() + extraZ);
      BlockPos ground = this.getToucanGround(radialPos);
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

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      Item item = itemstack.getItem();
      InteractionResult type = super.mobInteract(player, hand);
      if (!this.isBaby()
         && this.getRidingFalcons(player) <= 0
         && (
            player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get()
               || player.getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get()
         )) {
         this.boardingCooldown = 30;
         this.ejectPassengers();
         AMCompat.startRiding(this, player, true);
         if (!this.level().isClientSide()) {
            AlexsMobs.sendMSGToAll(new MessageMosquitoMountPlayer(this.getId(), player.getId()));
         }

         return InteractionResult.SUCCESS;
      } else {
         return type;
      }
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).getType() != Type.MISS;
   }

   public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
      float radius = 5.0F + radiusAdd + this.getRandom().nextInt(5);
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.x() + extraX), 0, (int)(fleePos.z() + extraZ));
      BlockPos ground = this.getToucanGround(radialPos);
      int distFromGround = (int)this.getY() - ground.getY();
      int flightHeight = 5 + this.getRandom().nextInt(5);
      int j = this.getRandom().nextInt(5) + 5;
      BlockPos newPos = ground.above(distFromGround > 5 ? flightHeight : j);
      if (this.level().getBlockState(ground).is(BlockTags.LEAVES)) {
         newPos = ground.above(1 + this.getRandom().nextInt(3));
      }

      return !this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1.0 ? Vec3.atCenterOf(newPos) : null;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   private boolean isOverWaterOrVoid() {
      BlockPos position = this.blockPosition();

      while (position.getY() > -65 && this.level().isEmptyBlock(position)) {
         position = position.below();
      }

      return !this.level().getFluidState(position).isEmpty() || this.level().getBlockState(position).is(Blocks.VINE) || position.getY() <= -65;
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.POTOO_BREEDABLES);
   }

   @Override
   public void onLaunch(Player player, Entity pointedEntity) {
   }

   private class AIFlyIdle extends Goal {
      protected double x;
      protected double y;
      protected double z;

      public AIFlyIdle() {
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         if (EntityPotoo.this.isVehicle()
            || EntityPotoo.this.isPerching()
            || EntityPotoo.this.getTarget() != null && EntityPotoo.this.getTarget().isAlive()
            || EntityPotoo.this.isPassenger()) {
            return false;
         } else if (EntityPotoo.this.getRandom().nextInt(45) != 0 && !EntityPotoo.this.isFlying()) {
            return false;
         } else {
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
         EntityPotoo.this.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
         if (EntityPotoo.this.isFlying() && EntityPotoo.this.onGround() && EntityPotoo.this.timeFlying > 10) {
            EntityPotoo.this.setFlying(false);
         }
      }

      @javax.annotation.Nullable
      protected Vec3 getPosition() {
         Vec3 vector3d = EntityPotoo.this.position();
         return EntityPotoo.this.timeFlying >= 200 && !EntityPotoo.this.isOverWaterOrVoid()
            ? EntityPotoo.this.getBlockGrounding(vector3d)
            : EntityPotoo.this.getBlockInViewAway(vector3d, 0.0F);
      }

      public boolean canContinueToUse() {
         return EntityPotoo.this.isFlying() && EntityPotoo.this.distanceToSqr(this.x, this.y, this.z) > 5.0;
      }

      public void start() {
         EntityPotoo.this.setFlying(true);
         EntityPotoo.this.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
      }

      public void stop() {
         EntityPotoo.this.getNavigation().stop();
         this.x = 0.0;
         this.y = 0.0;
         this.z = 0.0;
         super.stop();
      }
   }

   private class AIMelee extends Goal {
      private int biteCooldown = 0;

      public AIMelee() {
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         return !EntityPotoo.this.isSleeping()
            && !EntityPotoo.this.isPassenger()
            && EntityPotoo.this.getTarget() != null
            && EntityPotoo.this.getTarget().isAlive();
      }

      public void tick() {
         if (this.biteCooldown > 0) {
            this.biteCooldown--;
         }

         LivingEntity entity = EntityPotoo.this.getTarget();
         if (entity != null) {
            EntityPotoo.this.setFlying(true);
            EntityPotoo.this.setPerching(false);
            EntityPotoo.this.getMoveControl().setWantedPosition(entity.getX(), entity.getY(0.5), entity.getZ(), 1.5);
            if (EntityPotoo.this.distanceTo(entity) < 1.4F) {
               if (this.biteCooldown == 0) {
                  EntityPotoo.this.openMouth(7);
                  this.biteCooldown = 10;
               }

               if (EntityPotoo.this.mouthProgress >= 4.5F) {
                  entity.hurt(EntityPotoo.this.damageSources().mobAttack(EntityPotoo.this), 2.0F);
                  if (entity.getBbWidth() <= 0.5F) {
                     entity.remove(RemovalReason.KILLED);
                  }
               }
            }
         }
      }
   }

   private class AIPerch extends Goal {
      private BlockPos perch = null;
      private Direction perchDirection = null;
      private int perchingTime = 0;
      private int runCooldown = 0;
      private int pathRecalcTime = 0;

      public AIPerch() {
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (EntityPotoo.this.getTarget() != null && EntityPotoo.this.getTarget().isAlive()) {
            return false;
         } else {
            if (this.runCooldown > 0) {
               this.runCooldown--;
            } else if (!EntityPotoo.this.isPerching() && EntityPotoo.this.perchCooldown == 0 && EntityPotoo.this.random.nextInt(35) == 0) {
               this.perchingTime = 0;
               if (EntityPotoo.this.getPerchPos() != null
                  && EntityPotoo.this.isValidPerchFromSide(EntityPotoo.this.getPerchPos(), EntityPotoo.this.getPerchDirection())) {
                  this.perch = EntityPotoo.this.getPerchPos();
                  this.perchDirection = EntityPotoo.this.getPerchDirection();
               } else {
                  this.findPerch();
               }

               this.runCooldown = 120 + EntityPotoo.this.getRandom().nextInt(140);
               return this.perch != null && this.perchDirection != null;
            }

            return false;
         }
      }

      private void findPerch() {
         RandomSource random = EntityPotoo.this.getRandom();
         Direction[] horiz = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
         if (EntityPotoo.this.isValidPerchFromSide(EntityPotoo.this.getBlockPosBelowThatAffectsMyMovement(), EntityPotoo.this.getDirection())) {
            this.perch = EntityPotoo.this.getBlockPosBelowThatAffectsMyMovement();
            this.perchDirection = EntityPotoo.this.getDirection();
         } else {
            for (Direction dir : horiz) {
               if (EntityPotoo.this.isValidPerchFromSide(EntityPotoo.this.getBlockPosBelowThatAffectsMyMovement(), dir)) {
                  this.perch = EntityPotoo.this.getBlockPosBelowThatAffectsMyMovement();
                  this.perchDirection = dir;
                  return;
               }
            }

            int range = 14;

            for (int i = 0; i < 15; i++) {
               BlockPos blockpos1 = EntityPotoo.this.blockPosition().offset(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);
               if (EntityPotoo.this.level().isLoaded(blockpos1)) {
                  while (EntityPotoo.this.level().isEmptyBlock(blockpos1) && blockpos1.getY() > -64) {
                     blockpos1 = blockpos1.below();
                  }

                  Direction dirx = Direction.from2DDataValue(random.nextInt(3));
                  if (EntityPotoo.this.isValidPerchFromSide(blockpos1, dirx)) {
                     this.perch = blockpos1;
                     this.perchDirection = dirx;
                     break;
                  }
               }
            }
         }
      }

      public void start() {
         this.pathRecalcTime = 0;
      }

      public boolean canContinueToUse() {
         return (this.perchingTime < 300 || EntityPotoo.this.level().isDay())
            && (EntityPotoo.this.getTarget() == null || !EntityPotoo.this.getTarget().isAlive())
            && !EntityPotoo.this.isPassenger();
      }

      public void tick() {
         if (EntityPotoo.this.isPerching()) {
            this.perchingTime++;
            EntityPotoo.this.getNavigation().stop();
            Vec3 block = Vec3.upFromBottomCenterOf(EntityPotoo.this.getPerchPos(), 1.0);
            Vec3 onBlock = block.add(EntityPotoo.this.getPerchDirection().getStepX() * 0.35F, 0.0, EntityPotoo.this.getPerchDirection().getStepZ() * 0.35F);
            double dist = EntityPotoo.this.distanceToSqr(onBlock);
            Vec3 dirVec = block.subtract(EntityPotoo.this.position());
            if (this.perchingTime <= 10
               || !(dist > 2.299999952316284) && EntityPotoo.this.isValidPerchFromSide(EntityPotoo.this.getPerchPos(), EntityPotoo.this.getPerchDirection())) {
               if (dist > 1.0) {
                  EntityPotoo.this.slideTowardsPerch();
                  if (EntityPotoo.this.getPerchPos().getY() + 1.2F > EntityPotoo.this.getBoundingBox().minY) {
                     EntityPotoo.this.setDeltaMovement(EntityPotoo.this.getDeltaMovement().add(0.0, 0.20000000298023224, 0.0));
                  }

                  float f = -((float)(Mth.atan2(dirVec.x, dirVec.z) * 57.2957763671875));
                  EntityPotoo.this.setYRot(f);
                  EntityPotoo.this.yHeadRot = f;
                  EntityPotoo.this.yBodyRot = f;
               }
            } else {
               EntityPotoo.this.setPerching(false);
            }
         } else if (this.perch != null) {
            if (EntityPotoo.this.distanceToSqr(Vec3.atCenterOf(this.perch)) > 100.0) {
               EntityPotoo.this.setFlying(true);
            }

            double distX = this.perch.getX() + 0.5F - EntityPotoo.this.getX();
            double distZ = this.perch.getZ() + 0.5F - EntityPotoo.this.getZ();
            if (!(distX * distX + distZ * distZ < 1.0) && EntityPotoo.this.isFlying()) {
               if (this.pathRecalcTime <= 0) {
                  this.pathRecalcTime = EntityPotoo.this.getRandom().nextInt(30) + 30;
                  EntityPotoo.this.getNavigation().moveTo(this.perch.getX() + 0.5F, this.perch.getY() + 2.5F, this.perch.getZ() + 0.5F, 1.0);
               }
            } else {
               if (this.pathRecalcTime <= 0) {
                  this.pathRecalcTime = EntityPotoo.this.getRandom().nextInt(30) + 30;
                  EntityPotoo.this.getNavigation().moveTo(this.perch.getX() + 0.5F, this.perch.getY() + 1.5F, this.perch.getZ() + 0.5F, 1.0);
               }

               if (EntityPotoo.this.getNavigation().isDone()) {
                  EntityPotoo.this.getMoveControl().setWantedPosition(this.perch.getX() + 0.5F, this.perch.getY() + 1.5F, this.perch.getZ() + 0.5F, 1.0);
               }
            }

            if (EntityPotoo.this.getBlockPosBelowThatAffectsMyMovement().equals(this.perch)) {
               EntityPotoo.this.setDeltaMovement(Vec3.ZERO);
               EntityPotoo.this.setPerching(true);
               EntityPotoo.this.setFlying(false);
               EntityPotoo.this.setPerchPos(this.perch);
               EntityPotoo.this.setPerchDirection(this.perchDirection);
               EntityPotoo.this.getNavigation().stop();
               this.perch = null;
            } else {
               EntityPotoo.this.setPerching(false);
            }
         }

         if (this.pathRecalcTime > 0) {
            this.pathRecalcTime--;
         }
      }

      public void stop() {
         EntityPotoo.this.setPerching(false);
         EntityPotoo.this.perchCooldown = 120 + EntityPotoo.this.random.nextInt(1200);
         this.perch = null;
         this.perchDirection = null;
      }
   }
}
