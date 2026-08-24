package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EndergradeAIBreakFlowers;
import com.github.alexthe666.alexsmobs.entity.ai.EndergradeAITargetItems;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EntityEndergrade extends Animal implements FlyingAnimal {
   private static final EntityDataAccessor<Integer> BITE_TICK = SynchedEntityData.defineId(EntityEndergrade.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(EntityEndergrade.class, EntityDataSerializers.BOOLEAN);
   public float tartigradePitch = 0.0F;
   public float prevTartigradePitch = 0.0F;
   public float biteProgress = 0.0F;
   public float prevBiteProgress = 0.0F;
   public boolean stopWandering = false;
   public boolean hasItemTarget = false;

   protected EntityEndergrade(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.moveControl = new EntityEndergrade.MoveHelperController(this);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.ARMOR, 0.0)
         .add(Attributes.ATTACK_DAMAGE, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 0.15000000596046448);
   }

   public static boolean canEndergradeSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
      return !worldIn.getBlockState(pos.below()).isAir();
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new DirectPathNavigator(this, worldIn);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Saddled", this.isSaddled());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setSaddled(AMCompat.getBoolean(compound, "Saddled"));
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(BITE_TICK, 0);
      builder.define(SADDLED, false);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new EndergradeAIBreakFlowers(this));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.2) {
         public void start() {
            super.start();
            EntityEndergrade.this.stopWandering = true;
         }

         public void stop() {
            super.stop();
            EntityEndergrade.this.stopWandering = false;
         }
      });
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, AMCompat.ingredientOf(AMTagRegistry.ENDERGRADE_BREEDABLES), false) {
         public void start() {
            super.start();
            EntityEndergrade.this.stopWandering = true;
         }

         public void stop() {
            super.stop();
            EntityEndergrade.this.stopWandering = false;
         }
      });
      this.goalSelector.addGoal(4, new EntityEndergrade.RandomFlyGoal(this));
      this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 10.0F));
      this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new EndergradeAITargetItems(this, true));
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      for (Entity passenger : this.getPassengers()) {
         if (passenger instanceof Player player
            && (player.getMainHandItem().is(AMTagRegistry.ENDERGRADE_FOLLOWS) || player.getOffhandItem().is(AMTagRegistry.ENDERGRADE_FOLLOWS))) {
            return player;
         }
      }

      return null;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.ENDERGRADE_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.ENDERGRADE_HURT.get();
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      Item item = itemstack.getItem();
      if (item == Items.SADDLE && !this.isSaddled()) {
         if (!player.isCreative()) {
            itemstack.shrink(1);
         }

         this.setSaddled(true);
         return InteractionResult.SUCCESS;
      } else if (itemstack.is(AMTagRegistry.ENDERGRADE_BREEDABLES) && this.hasEffect(AMCompat.effect(AMEffectRegistry.ENDER_FLU.get()))) {
         if (!player.isCreative()) {
            itemstack.shrink(1);
         }

         this.heal(8.0F);
         this.removeEffect(AMCompat.effect(AMEffectRegistry.ENDER_FLU.get()));
         return InteractionResult.SUCCESS;
      } else {
         InteractionResult type = super.mobInteract(player, hand);
         if (type != InteractionResult.SUCCESS && !this.isFood(itemstack) && !player.isShiftKeyDown() && this.isSaddled()) {
            player.startRiding(this);
            return InteractionResult.SUCCESS;
         } else {
            return type;
         }
      }
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.ENDERGRADE_BREEDABLES);
   }

   public void positionRider(Entity passenger, MoveFunction moveFunc) {
      if (this.hasPassenger(passenger)) {
         float radius = -0.25F;
         float angle = 0.017453292F * this.yBodyRot;
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         passenger.setPos(
            this.getX() + extraX, this.getY() + this.getPassengersRidingOffset() + AMPlatform.myRidingOffset(passenger, this), this.getZ() + extraZ
         );
      }
   }

   public double getPassengersRidingOffset() {
      float f = Math.min(0.25F, this.walkAnimation.speed());
      float f1 = this.walkAnimation.position();
      return this.getBbHeight() - 0.1 + 0.12F * Mth.cos(f1 * 0.7F) * 0.7F * f;
   }

   public boolean isNoGravity() {
      return true;
   }

   public boolean isSaddled() {
      return (Boolean)this.entityData.get(SADDLED);
   }

   public void setSaddled(boolean saddled) {
      this.entityData.set(SADDLED, saddled);
   }

   public void tick() {
      super.tick();
      this.prevTartigradePitch = this.tartigradePitch;
      this.prevBiteProgress = this.biteProgress;
      float f2 = (float)(-((float)this.getDeltaMovement().y * 3.0F * 57.2957763671875));
      this.tartigradePitch = f2;
      if (this.getDeltaMovement().lengthSqr() > 0.004999999888241291) {
         float angleMotion = 0.017453292F * this.yBodyRot;
         double extraXMotion = -0.2F * Mth.sin((float)(3.141592653589793 + angleMotion));
         double extraZMotion = -0.2F * Mth.cos(angleMotion);
         this.level().addParticle(ParticleTypes.END_ROD, this.getRandomX(0.5), this.getY() + 0.3, this.getRandomZ(0.5), extraXMotion, 0.0, extraZMotion);
      }

      int tick = (Integer)this.entityData.get(BITE_TICK);
      if (tick > 0) {
         this.entityData.set(BITE_TICK, tick - 1);
         this.biteProgress++;
      } else if (this.biteProgress > 0.0F) {
         this.biteProgress--;
      }
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   private BlockPos getGroundPosition(BlockPos radialPos) {
      while (radialPos.getY() > 1 && this.level().isEmptyBlock(radialPos)) {
         radialPos = radialPos.below();
      }

      return radialPos.getY() <= 1 ? new BlockPos(radialPos.getX(), this.level().getSeaLevel(), radialPos.getZ()) : radialPos;
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).getType() != Type.MISS;
   }

   public boolean canTargetItem(ItemStack stack) {
      return stack.is(AMTagRegistry.ENDERGRADE_FOODSTUFFS);
   }

   public void onGetItem(ItemEntity targetEntity) {
      this.gameEvent(GameEvent.EAT);
      this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
      this.heal(5.0F);
   }

   public void bite() {
      this.entityData.set(BITE_TICK, 5);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.endergradeSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      return AMCompat.create(AMEntityRegistry.ENDERGRADE.get(), p_241840_1_);
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (this.isSaddled() && !this.level().isClientSide()) {
         AMCompat.spawnAtLocation(this, Items.SADDLE);
      }
   }

   protected Vec3 getRiddenInput(Player player, Vec3 deltaIn) {
      if (player.zza != 0.0F) {
         this.setSprinting(true);
         Vec3 lookVec = player.getLookAngle();
         if (player.zza < 0.0F) {
            lookVec = lookVec.yRot(3.1415927F);
         }

         double y = lookVec.y * 0.3499999940395355;
         return new Vec3(player.xxa, y, player.zza);
      } else {
         this.setSprinting(false);
         return Vec3.ZERO;
      }
   }

   protected void tickRidden(Player player, Vec3 vec3) {
      super.tickRidden(player, vec3);
      if (player.zza != 0.0F || player.xxa != 0.0F) {
         this.setRot(player.getYRot(), player.getXRot() * 0.25F);
         this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
         AMCompat.setMaxUpStep(this, 1.0F);
         this.getNavigation().stop();
         this.setTarget(null);
         this.setSprinting(true);
      }
   }

   protected float getRiddenSpeed(Player rider) {
      return (float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (this.onGround() ? 0.2F : 0.8F));
   }

   public boolean isFlying() {
      return true;
   }

   static class MoveHelperController extends MoveControl {
      private final EntityEndergrade parentEntity;

      public MoveHelperController(EntityEndergrade sunbird) {
         super(sunbird);
         this.parentEntity = sunbird;
      }

      public void tick() {
         if (this.operation == Operation.STRAFE) {
            Vec3 vector3d = new Vec3(this.wantedX - this.parentEntity.getX(), this.wantedY - this.parentEntity.getY(), this.wantedZ - this.parentEntity.getZ());
            double d0 = vector3d.length();
            this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(0.0, vector3d.scale(this.speedModifier * 0.05 / d0).y(), 0.0));
            float f = (float)this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
            float f1 = (float)this.speedModifier * f;
            this.strafeForwards = 1.0F;
            this.strafeRight = 0.0F;
            this.mob.setSpeed(f1);
            this.mob.setZza(this.strafeForwards);
            this.mob.setXxa(this.strafeRight);
            this.operation = Operation.WAIT;
         } else if (this.operation == Operation.MOVE_TO) {
            Vec3 vector3d = new Vec3(this.wantedX - this.parentEntity.getX(), this.wantedY - this.parentEntity.getY(), this.wantedZ - this.parentEntity.getZ());
            double d0 = vector3d.length();
            if (d0 < this.parentEntity.getBoundingBox().getSize()) {
               this.operation = Operation.WAIT;
               this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().scale(0.5));
            } else {
               double localSpeed = this.speedModifier;
               if (this.parentEntity.isVehicle()) {
                  localSpeed *= 1.5;
               }

               this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(vector3d.scale(localSpeed * 0.005 / d0)));
               if (this.parentEntity.getTarget() == null) {
                  Vec3 vector3d1 = this.parentEntity.getDeltaMovement();
                  this.parentEntity.setYRot(-((float)Mth.atan2(vector3d1.x, vector3d1.z)) * 57.295776F);
                  this.parentEntity.yBodyRot = this.parentEntity.getYRot();
               } else {
                  double d2 = this.parentEntity.getTarget().getX() - this.parentEntity.getX();
                  double d1 = this.parentEntity.getTarget().getZ() - this.parentEntity.getZ();
                  this.parentEntity.setYRot(-((float)Mth.atan2(d2, d1)) * 57.295776F);
                  this.parentEntity.yBodyRot = this.parentEntity.getYRot();
               }
            }
         }
      }

      private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
         AABB axisalignedbb = this.parentEntity.getBoundingBox();

         for (int i = 1; i < p_220673_2_; i++) {
            axisalignedbb = axisalignedbb.move(p_220673_1_);
            if (!this.parentEntity.level().noCollision(this.parentEntity, axisalignedbb)) {
               return false;
            }
         }

         return true;
      }
   }

   static class RandomFlyGoal extends Goal {
      private final EntityEndergrade parentEntity;
      private BlockPos target = null;

      public RandomFlyGoal(EntityEndergrade mosquito) {
         this.parentEntity = mosquito;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         MoveControl movementcontroller = this.parentEntity.getMoveControl();
         if (this.parentEntity.stopWandering || this.parentEntity.hasItemTarget) {
            return false;
         } else if (movementcontroller.hasWanted() && this.target != null) {
            return false;
         } else {
            this.target = this.getBlockInViewEndergrade();
            if (this.target != null) {
               this.parentEntity.getMoveControl().setWantedPosition(this.target.getX() + 0.5, this.target.getY() + 0.5, this.target.getZ() + 0.5, 1.0);
            }

            return true;
         }
      }

      public boolean canContinueToUse() {
         return this.target != null
            && !this.parentEntity.stopWandering
            && !this.parentEntity.hasItemTarget
            && this.parentEntity.distanceToSqr(Vec3.atCenterOf(this.target)) > 2.4
            && this.parentEntity.getMoveControl().hasWanted()
            && !this.parentEntity.horizontalCollision;
      }

      public void stop() {
         this.target = null;
      }

      public void tick() {
         if (this.target == null) {
            this.target = this.getBlockInViewEndergrade();
         }

         if (this.target != null) {
            this.parentEntity.getMoveControl().setWantedPosition(this.target.getX() + 0.5, this.target.getY() + 0.5, this.target.getZ() + 0.5, 1.0);
            if (this.parentEntity.distanceToSqr(Vec3.atCenterOf(this.target)) < 2.5) {
               this.target = null;
            }
         }
      }

      public BlockPos getBlockInViewEndergrade() {
         float radius = 1 + this.parentEntity.getRandom().nextInt(5);
         float neg = this.parentEntity.getRandom().nextBoolean() ? 1.0F : -1.0F;
         float renderYawOffset = this.parentEntity.yBodyRot;
         float angle = 0.017453292F * renderYawOffset + 3.15F + this.parentEntity.getRandom().nextFloat() * neg;
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         BlockPos radialPos = AMBlockPos.fromCoords(this.parentEntity.getX() + extraX, this.parentEntity.getY() + 2.0, this.parentEntity.getZ() + extraZ);
         BlockPos ground = this.parentEntity.getGroundPosition(radialPos);
         BlockPos newPos = ground.above(1 + this.parentEntity.getRandom().nextInt(6));
         return !this.parentEntity.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.parentEntity.distanceToSqr(Vec3.atCenterOf(newPos)) > 6.0 ? newPos : null;
      }
   }
}
