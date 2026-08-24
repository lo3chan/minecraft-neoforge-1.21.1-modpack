package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.CosmicCodAIFollowLeader;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.Entity.MovementEmission;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent.EnderEntity;

public class EntityCosmicCod extends Mob implements Bucketable {
   private static final EntityDataAccessor<Float> FISH_PITCH = SynchedEntityData.defineId(EntityCosmicCod.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityCosmicCod.class, EntityDataSerializers.BOOLEAN);
   public float prevFishPitch;
   private int baitballCooldown = 100 + this.random.nextInt(100);
   private int circleTime = 0;
   private int maxCircleTime = 300;
   private BlockPos circlePos;
   private int teleportIn;
   private EntityCosmicCod groupLeader;
   private int groupSize = 1;

   protected EntityCosmicCod(EntityType<? extends Mob> mob, Level level) {
      super(mob, level);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.moveControl = new FlightMoveController(this, 1.0F, false, true);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.cosmicCodSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.COSMIC_COD_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.COSMIC_COD_HURT.get();
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 4.0).add(Attributes.MOVEMENT_SPEED, 0.3499999940395355);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new EntityCosmicCod.AISwimIdle(this));
      this.goalSelector.addGoal(1, new CosmicCodAIFollowLeader(this));
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FISH_PITCH, 0.0F);
      builder.define(FROM_BUCKET, false);
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

   @Nonnull
   public ItemStack getBucketItemStack() {
      ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.COSMIC_COD_BUCKET.get());
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
      AMCompat.put(compound, "CosmicCodData", platTag);
      AMCompat.setTag(bucket, compound);
   }

   public void loadFromBucketTag(@Nonnull CompoundTag compound) {
      if (AMCompat.contains(compound, "CosmicCodData")) {
         AMCompat.readAdditionalFrom(this, AMCompat.getCompound(compound, "CosmicCodData"));
      }
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.hasCustomName() || this.fromBucket();
   }

   public boolean removeWhenFarAway(double p_213397_1_) {
      return !this.fromBucket() && !this.hasCustomName();
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("FromBucket", this.fromBucket());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFromBucket(AMCompat.getBoolean(compound, "FromBucket"));
   }

   public boolean isSensitiveToWater() {
      return true;
   }

   private void doInitialPosing(LevelAccessor world, EntityCosmicCod.GroupData data) {
      BlockPos down = this.blockPosition();

      while (world.isEmptyBlock(down) && down.getY() > -62) {
         down = down.below();
      }

      if (down.getY() <= -60) {
         if (data != null && data.groupLeader != null) {
            this.setPos(down.getX() + 0.5F, data.groupLeader.getY() - 1.0 + this.random.nextInt(1), down.getZ() + 0.5F);
         } else {
            this.setPos(down.getX() + 0.5F, down.getY() + 90 + this.random.nextInt(60), down.getZ() + 0.5F);
         }
      } else {
         this.setPos(down.getX() + 0.5F, down.getY() + 1, down.getZ() + 0.5F);
      }
   }

   protected MovementEmission getMovementEmission() {
      return MovementEmission.EVENTS;
   }

   public void tick() {
      super.tick();
      this.prevFishPitch = this.getFishPitch();
      if (!this.level().isClientSide()) {
         double ydist = this.yo - this.getY();
         float fishDist = (float)((Math.abs(this.getDeltaMovement().x) + Math.abs(this.getDeltaMovement().z)) * 6.0) / this.getPitchSensitivity();
         this.incrementFishPitch((float)ydist * 10.0F * this.getPitchSensitivity());
         this.setFishPitch(Mth.clamp(this.getFishPitch(), -60.0F, 40.0F));
         if (this.getFishPitch() > 2.0F) {
            this.decrementFishPitch(fishDist * Math.abs(this.getFishPitch()) / 90.0F);
         }

         if (this.getFishPitch() < -2.0F) {
            this.incrementFishPitch(fishDist * Math.abs(this.getFishPitch()) / 90.0F);
         }

         if (this.getFishPitch() > 2.0F) {
            this.decrementFishPitch(1.0F);
         } else if (this.getFishPitch() < -2.0F) {
            this.incrementFishPitch(1.0F);
         }

         if (this.baitballCooldown > 0) {
            this.baitballCooldown--;
         }
      }

      if (this.teleportIn > 0) {
         this.teleportIn--;
         if (this.teleportIn == 0 && !this.level().isClientSide()) {
            double range = 8.0;
            AABB bb = new AABB(this.getX() - 8.0, this.getY() - 8.0, this.getZ() - 8.0, this.getX() + 8.0, this.getY() + 8.0, this.getZ() + 8.0);
            List<EntityCosmicCod> list = this.level().getEntitiesOfClass(EntityCosmicCod.class, bb);
            Vec3 vec3 = this.teleport();
            if (vec3 != null) {
               this.baitballCooldown = 5;

               for (EntityCosmicCod cod : list) {
                  if (cod != this) {
                     cod.baitballCooldown = 5;
                     cod.teleport(vec3.x, vec3.y, vec3.z);
                  }
               }
            }
         }
      }
   }

   public void handleEntityEvent(byte msg) {
      if (msg == 46) {
         this.gameEvent(GameEvent.TELEPORT);
         this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
      }

      super.handleEntityEvent(msg);
   }

   public void resetBaitballCooldown() {
      this.baitballCooldown = 120 + this.random.nextInt(100);
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean prev = super.hurt(source, amount);
      if (prev) {
         this.teleportIn = 5;
      }

      return prev;
   }

   private float getPitchSensitivity() {
      return 3.0F;
   }

   public boolean isNoGravity() {
      return true;
   }

   public boolean isPushedByWater() {
      return false;
   }

   public float getFishPitch() {
      return (Float)this.entityData.get(FISH_PITCH);
   }

   public void setFishPitch(float pitch) {
      this.entityData.set(FISH_PITCH, pitch);
   }

   public void incrementFishPitch(float pitch) {
      this.entityData.set(FISH_PITCH, this.getFishPitch() + pitch);
   }

   public void decrementFishPitch(float pitch) {
      this.entityData.set(FISH_PITCH, this.getFishPitch() - pitch);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public boolean canBlockPosBeSeen(BlockPos pos) {
      double x = pos.getX() + 0.5F;
      double y = pos.getY() + 0.5F;
      double z = pos.getZ() + 0.5F;
      HitResult result = this.level().clip(new ClipContext(this.getEyePosition(), new Vec3(x, y, z), Block.COLLIDER, Fluid.NONE, this));
      double dist = result.getLocation().distanceToSqr(x, y, z);
      return dist <= 1.0 || result.getType() == Type.MISS;
   }

   protected Vec3 teleport() {
      if (!this.level().isClientSide() && this.isAlive()) {
         double d0 = this.getX() + (this.random.nextDouble() - 0.5) * 64.0;
         double d1 = this.getY() + (this.random.nextInt(64) - 32);
         double d2 = this.getZ() + (this.random.nextDouble() - 0.5) * 64.0;
         if (this.teleport(d0, d1, d2)) {
            this.circlePos = null;
            return new Vec3(d0, d1, d2);
         }
      }

      return null;
   }

   private boolean teleport(double x, double y, double z) {
      MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos(x, y, z);
      BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos);
      boolean flag = blockstate.isAir();
      if (flag && !blockstate.getFluidState().is(FluidTags.WATER)) {
         this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
         EnderEntity event = EventHooks.onEnderTeleport(this, x, y, z);
         if (event.isCanceled()) {
            return false;
         } else {
            this.level().broadcastEntityEvent(this, (byte)46);
            this.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
            return true;
         }
      } else {
         return false;
      }
   }

   public void leaveGroup() {
      if (this.groupLeader != null) {
         this.groupLeader.decreaseGroupSize();
      }

      this.groupLeader = null;
   }

   protected boolean hasNoLeader() {
      return !this.hasGroupLeader();
   }

   public boolean hasGroupLeader() {
      return this.groupLeader != null && this.groupLeader.isAlive();
   }

   private void increaseGroupSize() {
      this.groupSize++;
   }

   private void decreaseGroupSize() {
      this.groupSize--;
   }

   public boolean canGroupGrow() {
      return this.isGroupLeader() && this.groupSize < this.getMaxGroupSize();
   }

   private int getMaxGroupSize() {
      return 15;
   }

   public int getMaxSpawnClusterSize() {
      return 7;
   }

   public boolean isMaxGroupSizeReached(int sizeIn) {
      return false;
   }

   public boolean isGroupLeader() {
      return this.groupSize > 1;
   }

   public boolean inRangeOfGroupLeader() {
      return this.distanceToSqr(this.groupLeader) <= 121.0;
   }

   public void moveToGroupLeader() {
      if (this.hasGroupLeader()) {
         this.getMoveControl().setWantedPosition(this.groupLeader.getX(), this.groupLeader.getY(), this.groupLeader.getZ(), 1.0);
      }
   }

   public EntityCosmicCod createAndSetLeader(EntityCosmicCod leader) {
      this.groupLeader = leader;
      leader.increaseGroupSize();
      return leader;
   }

   public void createFromStream(Stream<EntityCosmicCod> stream) {
      stream.limit(this.getMaxGroupSize() - this.groupSize).filter(fishe -> fishe != this).forEach(fishe -> fishe.createAndSetLeader(this));
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      if (spawnDataIn == null) {
         spawnDataIn = new EntityCosmicCod.GroupData(this);
      } else {
         this.createAndSetLeader(((EntityCosmicCod.GroupData)spawnDataIn).groupLeader);
      }

      if (reason == MobSpawnType.NATURAL && spawnDataIn instanceof EntityCosmicCod.GroupData) {
         this.doInitialPosing(worldIn, (EntityCosmicCod.GroupData)spawnDataIn);
      }

      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   public boolean isCircling() {
      return this.circlePos != null && this.circleTime < this.maxCircleTime;
   }

   @Nonnull
   protected InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      if (itemstack.getItem() == Items.BUCKET && this.isAlive()) {
         this.gameEvent(GameEvent.ENTITY_INTERACT);
         this.playSound(this.getPickupSound(), 1.0F, 1.0F);
         ItemStack itemstack1 = this.getBucketItemStack();
         this.saveToBucketTag(itemstack1);
         ItemStack itemstack2 = ItemUtils.createFilledResult(itemstack, player, itemstack1, false);
         player.setItemInHand(hand, itemstack2);
         Level level = this.level();
         if (!this.level().isClientSide()) {
            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, itemstack1);
         }

         this.discard();
         return AMCompat.sidedSuccess(this.level().isClientSide());
      } else {
         return super.mobInteract(player, hand);
      }
   }

   private static class AISwimIdle extends Goal {
      private final EntityCosmicCod cod;
      float circleDistance = 5.0F;
      boolean clockwise = false;

      public AISwimIdle(EntityCosmicCod cod) {
         this.cod = cod;
      }

      public boolean canUse() {
         return this.cod.isGroupLeader() || this.cod.hasNoLeader() || this.cod.hasGroupLeader() && this.cod.groupLeader.circlePos != null;
      }

      public void tick() {
         if (this.cod.circleTime > this.cod.maxCircleTime) {
            this.cod.circleTime = 0;
            this.cod.circlePos = null;
         }

         if (this.cod.circlePos != null && this.cod.circleTime <= this.cod.maxCircleTime) {
            this.cod.circleTime++;
            Vec3 movePos = this.getSharkCirclePos(this.cod.circlePos);
            this.cod.getMoveControl().setWantedPosition(movePos.x(), movePos.y(), movePos.z(), 1.0);
         } else if (this.cod.isGroupLeader()) {
            if (this.cod.baitballCooldown == 0) {
               this.cod.resetBaitballCooldown();
               if (this.cod.circlePos == null || this.cod.circleTime >= this.cod.maxCircleTime) {
                  this.cod.circleTime = 0;
                  this.cod.maxCircleTime = 360 + this.cod.random.nextInt(80);
                  this.circleDistance = 1.0F + this.cod.random.nextFloat();
                  this.clockwise = this.cod.random.nextBoolean();
                  this.cod.circlePos = this.cod.blockPosition().above();
               }
            }
         } else if (this.cod.random.nextInt(40) == 0 || this.cod.hasNoLeader()) {
            Vec3 movepos = this.cod
               .position()
               .add(this.cod.random.nextInt(4) - 2, this.cod.getY() < 0.0 ? 1.0 : this.cod.random.nextInt(4) - 2, this.cod.random.nextInt(4) - 2);
            this.cod.getMoveControl().setWantedPosition(movepos.x, movepos.y, movepos.z, 1.0);
         } else if (this.cod.hasGroupLeader() && this.cod.groupLeader.circlePos != null && this.cod.circlePos == null) {
            this.cod.circlePos = this.cod.groupLeader.circlePos;
            this.cod.circleTime = this.cod.groupLeader.circleTime;
            this.cod.maxCircleTime = this.cod.groupLeader.maxCircleTime;
            this.circleDistance = 1.0F + this.cod.random.nextFloat();
            this.clockwise = this.cod.random.nextBoolean();
         }
      }

      public Vec3 getSharkCirclePos(BlockPos target) {
         float prog = 1.0F - (float)this.cod.circleTime / this.cod.maxCircleTime;
         float angle = 0.17453292F * (this.clockwise ? -this.cod.circleTime : this.cod.circleTime);
         float circleDistanceTimesProg = this.circleDistance * prog;
         double extraX = (circleDistanceTimesProg + 0.75F) * Mth.sin(angle);
         double extraZ = (circleDistanceTimesProg + 0.75F) * prog * Mth.cos(angle);
         return new Vec3(target.getX() + 0.5F + extraX, Math.max(target.getY() + this.cod.random.nextInt(4) - 2, -62), target.getZ() + 0.5F + extraZ);
      }
   }

   public static class GroupData extends AgeableMobGroupData {
      public final EntityCosmicCod groupLeader;

      public GroupData(EntityCosmicCod groupLeaderIn) {
         super(0.05F);
         this.groupLeader = groupLeaderIn;
      }
   }
}
