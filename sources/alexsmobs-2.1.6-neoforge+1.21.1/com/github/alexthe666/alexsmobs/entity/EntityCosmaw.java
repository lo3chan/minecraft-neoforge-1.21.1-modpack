package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAITemptDistance;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.FlyingAIFollowOwner;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.base.Predicates;
import java.util.EnumSet;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EntityCosmaw extends TamableAnimal implements ITargetsDroppedItems, FlyingAnimal, IFollower {
   private static final EntityDataAccessor<Float> COSMAW_PITCH = SynchedEntityData.defineId(EntityCosmaw.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(EntityCosmaw.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityCosmaw.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityCosmaw.class, EntityDataSerializers.INT);
   public float clutchProgress;
   public float prevClutchProgress;
   public float openProgress;
   public float prevOpenProgress;
   public float prevCosmawPitch;
   public float biteProgress;
   public float prevBiteProgress;
   private float stuckRot = (float)(this.random.nextInt(3) * 90);
   private UUID fishThrowerID;
   private int heldItemTime;
   private BlockPos lastSafeTpPosition;

   protected EntityCosmaw(EntityType<? extends TamableAnimal> type, Level lvl) {
      super(type, lvl);
      this.moveControl = new FlightMoveController(this, 1.0F, false, true);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.MOVEMENT_SPEED, 0.30000001192092896);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.cosmawSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static boolean canCosmawSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
      return !worldIn.getBlockState(pos.below()).isAir();
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(COSMAW_PITCH, 0.0F);
      builder.define(ATTACK_TICK, 0);
      builder.define(COMMAND, 0);
      builder.define(SITTING, false);
   }

   protected void onBelowWorld() {
   }

   public boolean doHurtTarget(Entity entityIn) {
      if ((Integer)this.entityData.get(ATTACK_TICK) == 0 && this.biteProgress == 0.0F) {
         this.entityData.set(ATTACK_TICK, 5);
      }

      return true;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new EntityCosmaw.AIAttack());
      this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(3, new FlyingAIFollowOwner(this, 1.3, 8.0F, 4.0F, false));
      this.goalSelector.addGoal(4, new EntityCosmaw.AIPickupOwner());
      this.goalSelector.addGoal(5, new BreedGoal(this, 1.2));
      this.goalSelector.addGoal(6, new AnimalAITemptDistance(this, 1.1, AMCompat.ingredientOf(AMTagRegistry.COSMAW_FOODSTUFFS), false, 25.0) {
         @Override
         public boolean canUse() {
            return super.canUse() && EntityCosmaw.this.getMainHandItem().isEmpty();
         }

         @Override
         public boolean canContinueToUse() {
            return super.canContinueToUse() && EntityCosmaw.this.getMainHandItem().isEmpty();
         }
      });
      this.goalSelector.addGoal(7, new EntityCosmaw.RandomFlyGoal(this));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10.0F));
      this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new CreatureAITargetItems(this, true));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this) {
         public boolean canUse() {
            LivingEntity livingentity = this.mob.getLastHurtByMob();
            return livingentity != null && EntityCosmaw.this.isOwnedBy(livingentity) ? false : super.canUse();
         }
      });
      this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, EntityCosmicCod.class, 80, true, false, Predicates.alwaysTrue()));
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.COSMAW_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.COSMAW_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.COSMAW_HURT.get();
   }

   public boolean isFood(ItemStack stack) {
      return this.isTame() && stack.is(AMTagRegistry.COSMAW_BREEDABLES);
   }

   public boolean isNoGravity() {
      return true;
   }

   public boolean isLeftHanded() {
      return false;
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      return null;
   }

   public float getClampedCosmawPitch(float partialTick) {
      float f = this.prevCosmawPitch + (this.getCosmawPitch() - this.prevCosmawPitch) * partialTick;
      return Mth.clamp(f, -90.0F, 90.0F);
   }

   public float getCosmawPitch() {
      return (Float)this.entityData.get(COSMAW_PITCH);
   }

   public void setCosmawPitch(float pitch) {
      this.entityData.set(COSMAW_PITCH, pitch);
   }

   public int getCommand() {
      return (Integer)this.entityData.get(COMMAND);
   }

   public void setCommand(int command) {
      this.entityData.set(COMMAND, command);
   }

   public boolean isSitting() {
      return (Boolean)this.entityData.get(SITTING);
   }

   public void setOrderedToSit(boolean sit) {
      this.entityData.set(SITTING, sit);
   }

   public void positionRider(Entity passenger, MoveFunction moveFunc) {
      if (this.hasPassenger(passenger)) {
         float f = this.walkAnimation.position();
         float f1 = this.walkAnimation.speed();
         float bob = (float)(Math.sin(f * 0.7F) * f1 * 0.0625 * 1.600000023841858 - f1 * 0.0625F * 1.6F);
         passenger.setPos(this.getX(), this.getY() - bob + 0.30000001192092896 - this.getBbHeight() * 0.75, this.getZ());
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("CosmawSitting", this.isSitting());
      compound.putInt("Command", this.getCommand());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setOrderedToSit(AMCompat.getBoolean(compound, "CosmawSitting"));
      this.setCommand(AMCompat.getInt(compound, "Command"));
   }

   public void tick() {
      super.tick();
      this.prevOpenProgress = this.openProgress;
      this.prevClutchProgress = this.clutchProgress;
      this.prevBiteProgress = this.biteProgress;
      this.prevCosmawPitch = this.getCosmawPitch();
      if (!this.level().isClientSide()) {
         float f2 = -((float)this.getDeltaMovement().y * 57.295776F);
         this.setCosmawPitch(this.getCosmawPitch() + 0.6F * (this.getCosmawPitch() + f2) - this.getCosmawPitch());
      }

      if (this.isMouthOpen()) {
         if (this.openProgress < 5.0F) {
            this.openProgress++;
         }
      } else if (this.openProgress > 0.0F) {
         this.openProgress--;
      }

      if (this.isVehicle()) {
         if (this.clutchProgress < 5.0F) {
            this.clutchProgress++;
         }
      } else if (this.clutchProgress > 0.0F) {
         this.clutchProgress--;
      }

      if ((Integer)this.entityData.get(ATTACK_TICK) > 0) {
         if (this.biteProgress < 5.0F) {
            this.biteProgress = Math.min(5.0F, this.biteProgress + 2.0F);
         } else {
            if (this.getTarget() != null && this.distanceTo(this.getTarget()) < 3.3) {
               if (this.getTarget() instanceof EntityCosmicCod && !this.isTame()) {
                  EntityCosmicCod fish = (EntityCosmicCod)this.getTarget();
                  CompoundTag fishNbt = new CompoundTag();
                  AMCompat.saveAdditionalTo(fish, fishNbt);
                  fishNbt.putString("DeathLootTable", "minecraft:empty");
                  AMCompat.readAdditionalFrom(fish, fishNbt);
               }

               this.getTarget().hurt(this.damageSources().mobAttack(this), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            }

            this.entityData.set(ATTACK_TICK, (Integer)this.entityData.get(ATTACK_TICK) - 1);
         }
      } else if (this.biteProgress > 0.0F) {
         this.biteProgress--;
      }

      if (!this.getMainHandItem().isEmpty()) {
         this.heldItemTime++;
         if (this.heldItemTime > 30 && this.canTargetItem(this.getMainHandItem())) {
            this.heldItemTime = 0;
            this.heal(4.0F);
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.DOLPHIN_EAT, this.getSoundVolume(), this.getVoicePitch());
            if (this.getMainHandItem().is(AMTagRegistry.COSMAW_TAMEABLES) && this.fishThrowerID != null && !this.isTame()) {
               if (this.getRandom().nextFloat() < 0.3F) {
                  AMCompat.setTame(this, true);
                  this.setCommand(1);
                  AMCompat.setOwnerUUID(this, this.fishThrowerID);
                  Player player = this.level().getPlayerByUUID(this.fishThrowerID);
                  if (player instanceof ServerPlayer) {
                     CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)player, this);
                  }

                  this.level().broadcastEntityEvent(this, (byte)7);
               } else {
                  this.level().broadcastEntityEvent(this, (byte)6);
               }
            }

            if (AMCompat.hasCraftingRemainder(this.getMainHandItem())) {
               AMCompat.spawnAtLocation(this, AMCompat.craftingRemainder(this.getMainHandItem()));
            }

            this.getMainHandItem().shrink(1);
         }
      } else {
         this.heldItemTime = 0;
      }

      if (!this.level().isClientSide()) {
         if (this.tickCount % 100 == 0 || this.lastSafeTpPosition == null) {
            BlockPos pos = this.getCosmawGround(this.blockPosition());
            if (pos.getY() > 1) {
               this.lastSafeTpPosition = pos;
            }
         }

         if (this.isVehicle()) {
            if (this.lastSafeTpPosition != null) {
               double dist = this.distanceToSqr(Vec3.atCenterOf(this.lastSafeTpPosition));
               float speed = 0.8F;
               if (this.getY() < -40.0) {
                  speed = 3.0F;
               }

               if (this.verticalCollision && dist > 14.0) {
                  this.setYRot(this.stuckRot);
                  if (this.random.nextInt(50) == 0) {
                     this.stuckRot = Mth.wrapDegrees(this.stuckRot + 90.0F);
                  }

                  float angle = 0.017453292F * this.stuckRot;
                  double extraX = -2.0F * Mth.sin(3.1415927F + angle);
                  double extraZ = -2.0F * Mth.cos(angle);
                  this.getMoveControl().setWantedPosition(this.getX() + extraX, this.getY() + 2.0, this.getZ() + extraZ, speed);
               } else if (this.lastSafeTpPosition.getY() > this.getY() + 2.299999952316284) {
                  this.getMoveControl().setWantedPosition(this.getX(), this.getY() + 2.0, this.getZ(), speed);
               } else {
                  this.getMoveControl()
                     .setWantedPosition(this.lastSafeTpPosition.getX(), this.lastSafeTpPosition.getY() + 2, this.lastSafeTpPosition.getZ(), speed);
               }

               if (dist < 7.0 && this.getCosmawGround(this.blockPosition()).getY() > 1) {
                  this.ejectPassengers();
               }
            } else if (this.getY() < 0.0) {
               this.getDeltaMovement().add(0.0, 0.75, 0.0);
            } else if (this.getY() < 80.0) {
               this.getDeltaMovement().add(0.0, 0.10000000149011612, 0.0);
            }
         }
      }
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      InteractionResult type = super.mobInteract(player, hand);
      InteractionResult interactionresult = stack.interactLivingEntity(player, this, hand);
      if (this.canTargetItem(stack) && this.getMainHandItem().isEmpty()) {
         ItemStack rippedStack = stack.copy();
         rippedStack.setCount(1);
         stack.shrink(1);
         this.setItemInHand(InteractionHand.MAIN_HAND, rippedStack);
         if (rippedStack.is(AMTagRegistry.COSMAW_TAMEABLES)) {
            this.fishThrowerID = player.getUUID();
         }

         return InteractionResult.SUCCESS;
      } else if (this.isTame()
         && this.isOwnedBy(player)
         && !this.isBaby()
         && interactionresult != InteractionResult.SUCCESS
         && type != InteractionResult.SUCCESS) {
         this.setCommand(this.getCommand() + 1);
         if (this.getCommand() == 3) {
            this.setCommand(0);
         }

         player.displayClientMessage(Component.translatable("entity.alexsmobs.all.command_" + this.getCommand(), new Object[]{this.getName()}), true);
         boolean sit = this.getCommand() == 2;
         if (sit) {
            this.setOrderedToSit(true);
            return InteractionResult.SUCCESS;
         } else {
            this.setOrderedToSit(false);
            return InteractionResult.SUCCESS;
         }
      } else {
         return type;
      }
   }

   public boolean isMouthOpen() {
      return !this.getMainHandItem().isEmpty();
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   protected PathNavigation createNavigation(Level level) {
      return new DirectPathNavigator(this, level, 0.5F);
   }

   public boolean isAlliedTo(Entity entityIn) {
      if (this.isTame()) {
         LivingEntity livingentity = this.getOwner();
         if (entityIn == livingentity) {
            return true;
         }

         if (entityIn instanceof TamableAnimal) {
            return ((TamableAnimal)entityIn).isOwnedBy(livingentity);
         }

         if (livingentity != null) {
            return livingentity.isAlliedTo(entityIn);
         }
      }

      return super.isAlliedTo(entityIn);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
      return AMCompat.create(AMEntityRegistry.COSMAW.get(), this.level());
   }

   private BlockPos getCosmawGround(BlockPos in) {
      BlockPos position = new BlockPos(in.getX(), (int)this.getY(), in.getZ());

      while (position.getY() < 256 && !this.level().getFluidState(position).isEmpty()) {
         position = position.above();
      }

      while (position.getY() > 1 && this.level().isEmptyBlock(position)) {
         position = position.below();
      }

      return position;
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return stack.is(AMTagRegistry.COSMAW_FOODSTUFFS);
   }

   @Override
   public void onGetItem(ItemEntity e) {
      ItemStack duplicate = e.getItem().copy();
      duplicate.setCount(1);
      if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level().isClientSide()) {
         AMCompat.spawnAtLocation(this, this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
      }

      this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
      Entity itemThrower = e.getOwner();
      if (e.getItem().is(AMTagRegistry.COSMAW_TAMEABLES) && !this.isTame() && itemThrower != null) {
         this.fishThrowerID = itemThrower.getUUID();
      } else {
         this.fishThrowerID = null;
      }
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).getType() != Type.MISS;
   }

   @Override
   public boolean isFlying() {
      return true;
   }

   @Override
   public boolean shouldFollow() {
      return this.getCommand() == 1 && !this.isVehicle();
   }

   private boolean shouldWander() {
      if (this.isVehicle()) {
         return false;
      } else if (this.isTame()) {
         int command = this.getCommand();
         if (command != 2 && !this.isSitting()) {
            return command == 1 && this.getOwner() != null && this.distanceTo(this.getOwner()) < 10.0F ? true : command == 0;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public void push(Entity entity) {
      if (!this.isTame() || !(entity instanceof LivingEntity) || !this.isOwnedBy((LivingEntity)entity)) {
         super.push(entity);
      }
   }

   public boolean canRiderInteract() {
      return true;
   }

   public boolean shouldRiderSit() {
      return false;
   }

   private class AIAttack extends Goal {
      public AIAttack() {
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return EntityCosmaw.this.getTarget() != null && EntityCosmaw.this.getTarget().isAlive();
      }

      public void tick() {
         if (EntityCosmaw.this.distanceTo(EntityCosmaw.this.getTarget()) < 3.0 * (EntityCosmaw.this.isBaby() ? 0.5F : 1.0F)) {
            AMCompat.doHurtTarget(EntityCosmaw.this, EntityCosmaw.this.getTarget());
         } else {
            EntityCosmaw.this.getNavigation().moveTo(EntityCosmaw.this.getTarget(), 1.0);
         }
      }
   }

   private class AIPickupOwner extends Goal {
      private LivingEntity owner;

      public boolean canUse() {
         if (EntityCosmaw.this.isTame()
            && EntityCosmaw.this.getOwner() != null
            && !EntityCosmaw.this.isSitting()
            && !EntityCosmaw.this.getOwner().isPassenger()
            && !EntityCosmaw.this.getOwner().onGround()
            && EntityCosmaw.this.getOwner().fallDistance > 4.0F) {
            this.owner = EntityCosmaw.this.getOwner();
            return true;
         } else {
            return false;
         }
      }

      public void tick() {
         if (this.owner != null && (!this.owner.isFallFlying() || this.owner.getY() < -30.0)) {
            double dist = EntityCosmaw.this.distanceTo(this.owner);
            if (dist < 3.0 || this.owner.getY() <= -50.0) {
               this.owner.fallDistance = 0.0F;
               this.owner.startRiding(EntityCosmaw.this);
            } else if (!(dist > 100.0) && !(this.owner.getY() <= -20.0)) {
               EntityCosmaw.this.getNavigation().moveTo(this.owner, 1.0 + Math.min(dist * 0.30000001192092896, 3.0));
            } else {
               EntityCosmaw.this.teleportTo(this.owner.getX(), this.owner.getY() - 1.0, this.owner.getZ());
            }
         }
      }
   }

   static class RandomFlyGoal extends Goal {
      private final EntityCosmaw parentEntity;
      private BlockPos target = null;

      public RandomFlyGoal(EntityCosmaw mosquito) {
         this.parentEntity = mosquito;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         if (this.parentEntity.getNavigation().isDone()
            && this.parentEntity.shouldWander()
            && this.parentEntity.getTarget() == null
            && this.parentEntity.getRandom().nextInt(4) == 0) {
            this.target = this.getBlockInViewCosmaw();
            if (this.target != null) {
               this.parentEntity.getMoveControl().setWantedPosition(this.target.getX() + 0.5, this.target.getY() + 0.5, this.target.getZ() + 0.5, 1.0);
               return true;
            }
         }

         return false;
      }

      public boolean canContinueToUse() {
         return this.target != null && this.parentEntity.shouldWander() && this.parentEntity.getTarget() == null;
      }

      public void stop() {
         this.target = null;
      }

      public void tick() {
         if (this.target != null) {
            this.parentEntity.getMoveControl().setWantedPosition(this.target.getX() + 0.5, this.target.getY() + 0.5, this.target.getZ() + 0.5, 1.0);
            if (this.parentEntity.distanceToSqr(Vec3.atCenterOf(this.target)) < 4.0 || this.parentEntity.horizontalCollision) {
               this.target = null;
            }
         }
      }

      public BlockPos getBlockInViewCosmaw() {
         float radius = 5 + this.parentEntity.getRandom().nextInt(10);
         float neg = this.parentEntity.getRandom().nextBoolean() ? 1.0F : -1.0F;
         float renderYawOffset = this.parentEntity.getYRot();
         float angle = 0.017453292F * renderYawOffset + 3.15F * (this.parentEntity.getRandom().nextFloat() * neg);
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         BlockPos radialPos = AMBlockPos.fromCoords(this.parentEntity.getX() + extraX, this.parentEntity.getY(), this.parentEntity.getZ() + extraZ);
         BlockPos ground = this.parentEntity.getCosmawGround(radialPos);
         if (ground.getY() <= 1) {
            ground = ground.above(70 + this.parentEntity.random.nextInt(4));
         } else {
            ground = ground.above(2 + this.parentEntity.random.nextInt(2));
         }

         return !this.parentEntity.isTargetBlocked(Vec3.atCenterOf(ground.above())) ? ground : null;
      }
   }
}
