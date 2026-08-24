package com.aetherteam.aether.entity.passive;

import com.aetherteam.aether.entity.MountableMob;
import com.aetherteam.aether.entity.NotGrounded;
import com.google.common.collect.UnmodifiableIterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class MountableAnimal extends AetherAnimal implements MountableMob, Saddleable, NotGrounded {
   private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID = SynchedEntityData.defineId(MountableAnimal.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_PLAYER_JUMPED_ID = SynchedEntityData.defineId(MountableAnimal.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_MOUNT_JUMPING_ID = SynchedEntityData.defineId(MountableAnimal.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_PLAYER_CROUCHED_ID = SynchedEntityData.defineId(MountableAnimal.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_ENTITY_ON_GROUND_ID = SynchedEntityData.defineId(MountableAnimal.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_HAS_PASSENGER_ID = SynchedEntityData.defineId(MountableAnimal.class, EntityDataSerializers.BOOLEAN);

   protected MountableAnimal(EntityType<? extends Animal> type, Level level) {
      super(type, level);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_SADDLE_ID, false);
      builder.define(DATA_PLAYER_JUMPED_ID, false);
      builder.define(DATA_MOUNT_JUMPING_ID, false);
      builder.define(DATA_PLAYER_CROUCHED_ID, false);
      builder.define(DATA_ENTITY_ON_GROUND_ID, true);
      builder.define(DATA_HAS_PASSENGER_ID, false);
   }

   public void tick() {
      this.tick(this);
      this.riderTick();
      super.tick();
      if (this.onGround()) {
         this.setEntityOnGround(true);
      }

      if (this.getPlayerJumped()) {
         this.setEntityOnGround(false);
      }

      if (!this.hasPassenger() && this.getControllingPassenger() != null) {
         this.ejectPassengers();
      }
   }

   public void riderTick() {
      this.riderTick(this);
   }

   public void travel(Vec3 vector3d) {
      this.travel(this, vector3d);
   }

   @Override
   public void travelWithInput(Vec3 travelVec) {
      super.travel(travelVec);
   }

   public void jumpFromGround() {
      super.jumpFromGround();
      this.setEntityOnGround(false);
   }

   public InteractionResult mobInteract(Player playerEntity, InteractionHand hand) {
      boolean flag = this.isFood(playerEntity.getItemInHand(hand));
      if (!flag && this.isSaddled() && !this.isVehicle() && !playerEntity.isSecondaryUseActive()) {
         if (!this.level().isClientSide()) {
            playerEntity.startRiding(this);
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         InteractionResult interactionResult = super.mobInteract(playerEntity, hand);
         if (!interactionResult.consumesAction()) {
            ItemStack itemstack = playerEntity.getItemInHand(hand);
            return itemstack.is(Items.SADDLE) ? itemstack.interactLivingEntity(playerEntity, this, hand) : InteractionResult.PASS;
         } else {
            return interactionResult;
         }
      }
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
      Direction direction = this.getMotionDirection();
      if (direction.getAxis() != Axis.Y) {
         int[][] offsets = DismountHelper.offsetsForDirection(direction);
         BlockPos blockPos = this.blockPosition();
         MutableBlockPos mutableBlockPos = new MutableBlockPos();
         UnmodifiableIterator var6 = livingEntity.getDismountPoses().iterator();

         while (var6.hasNext()) {
            Pose pose = (Pose)var6.next();
            AABB bounds = livingEntity.getLocalBoundsForPose(pose);

            for (int[] offset : offsets) {
               mutableBlockPos.set(blockPos.getX() + offset[0], blockPos.getY(), blockPos.getZ() + offset[1]);
               double d0 = this.level().getBlockFloorHeight(mutableBlockPos);
               if (DismountHelper.isBlockFloorValid(d0)) {
                  Vec3 vector3d = Vec3.upFromBottomCenterOf(mutableBlockPos, d0);
                  if (DismountHelper.canDismountTo(this.level(), livingEntity, bounds.move(vector3d))) {
                     livingEntity.setPose(pose);
                     return vector3d;
                  }
               }
            }
         }
      }

      return super.getDismountLocationForPassenger(livingEntity);
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (this.isSaddled()) {
         this.spawnAtLocation(Items.SADDLE);
      }
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      return this.getFirstPassenger() instanceof LivingEntity livingEntity && this.isSaddled() ? livingEntity : null;
   }

   protected boolean canRide(Entity entityIn) {
      return true;
   }

   public void equipSaddle(ItemStack stack, @Nullable SoundSource soundCategory) {
      this.setSaddled(true);
      if (soundCategory != null && this.getSaddledSound() != null) {
         this.level().playSound(null, this, this.getSaddledSound(), soundCategory, 0.5F, 1.0F);
      }
   }

   public boolean isSaddleable() {
      return this.isAlive() && !this.isBaby();
   }

   public boolean isSaddled() {
      return (Boolean)this.getEntityData().get(DATA_SADDLE_ID);
   }

   public void setSaddled(boolean isSaddled) {
      this.getEntityData().set(DATA_SADDLE_ID, isSaddled);
   }

   @Override
   public boolean getPlayerJumped() {
      return (Boolean)this.getEntityData().get(DATA_PLAYER_JUMPED_ID);
   }

   @Override
   public void setPlayerJumped(boolean playerJumped) {
      this.getEntityData().set(DATA_PLAYER_JUMPED_ID, playerJumped);
   }

   @Override
   public boolean isMountJumping() {
      return (Boolean)this.getEntityData().get(DATA_MOUNT_JUMPING_ID);
   }

   @Override
   public void setMountJumping(boolean isMountJumping) {
      this.getEntityData().set(DATA_MOUNT_JUMPING_ID, isMountJumping);
   }

   public boolean playerTriedToCrouch() {
      return (Boolean)this.getEntityData().get(DATA_PLAYER_CROUCHED_ID);
   }

   public void setPlayerTriedToCrouch(boolean playerTriedToCrouch) {
      this.getEntityData().set(DATA_PLAYER_CROUCHED_ID, playerTriedToCrouch);
   }

   @Override
   public boolean isEntityOnGround() {
      return (Boolean)this.getEntityData().get(DATA_ENTITY_ON_GROUND_ID);
   }

   @Override
   public void setEntityOnGround(boolean onGround) {
      this.getEntityData().set(DATA_ENTITY_ON_GROUND_ID, onGround);
   }

   public boolean hasPassenger() {
      return (Boolean)this.getEntityData().get(DATA_HAS_PASSENGER_ID);
   }

   public void setHasPassenger(boolean hasPassenger) {
      this.getEntityData().set(DATA_HAS_PASSENGER_ID, hasPassenger);
   }

   @Override
   public boolean canJump() {
      return this.isSaddled() && this.onGround();
   }

   @Override
   public double getMountJumpStrength() {
      return 1.8;
   }

   @Override
   public float getSteeringSpeed() {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.625F;
   }

   public float getFlyingSpeed() {
      return this.getControllingPassenger() != null ? this.getSteeringSpeed() * 0.25F : 0.02F;
   }

   @Override
   public double jumpFactor() {
      return this.getBlockJumpFactor();
   }

   @Nullable
   protected SoundEvent getSaddledSound() {
      return null;
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putBoolean("Saddled", this.isSaddled());
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.contains("Saddled")) {
         this.setSaddled(tag.getBoolean("Saddled"));
      }
   }
}
