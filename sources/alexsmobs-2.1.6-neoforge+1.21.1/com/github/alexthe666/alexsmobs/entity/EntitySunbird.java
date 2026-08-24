package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.base.Predicates;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EntitySunbird extends Animal implements FlyingAnimal {
   public static final Predicate<? super Entity> SCORCH_PRED = new com.google.common.base.Predicate<Entity>() {
      public boolean apply(@Nullable Entity e) {
         return e.isAlive() && e.getType().builtInRegistryHolder().is(AMTagRegistry.SUNBIRD_SCORCH_TARGETS);
      }
   };
   private static final EntityDataAccessor<Boolean> SCORCHING = SynchedEntityData.defineId(EntitySunbird.class, EntityDataSerializers.BOOLEAN);
   public float birdPitch = 0.0F;
   public float prevBirdPitch = 0.0F;
   private int beaconSearchCooldown = 50;
   private BlockPos beaconPos = null;
   private boolean orbitClockwise = false;
   private float prevScorchProgress;
   private float scorchProgress;
   private int fullScorchTime;

   public boolean isFood(ItemStack stack) {
      return stack.is(Items.WHEAT);
   }

   protected EntitySunbird(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.moveControl = new EntitySunbird.MoveHelperController(this);
      this.orbitClockwise = new Random().nextBoolean();
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SCORCHING, false);
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.FOLLOW_RANGE, 64.0)
         .add(Attributes.ATTACK_DAMAGE, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 1.0);
   }

   public static boolean canSunbirdSpawn(EntityType<? extends Mob> typeIn, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      return true;
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.sunbirdSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.SUNBIRD_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.SUNBIRD_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.SUNBIRD_HURT.get();
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(3, new EntitySunbird.RandomFlyGoal(this));
      this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 32.0F));
      this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
   }

   public float getBrightness() {
      return 1.0F;
   }

   public boolean isNoGravity() {
      return true;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean prev = super.hurt(source, amount);
      if (prev) {
         if (source.getEntity() != null && source.getEntity() instanceof LivingEntity) {
            LivingEntity hurter = (LivingEntity)source.getEntity();
            if (hurter.hasEffect(AMCompat.effect(AMEffectRegistry.SUNBIRD_BLESSING.get()))) {
               hurter.removeEffect(AMCompat.effect(AMEffectRegistry.SUNBIRD_BLESSING.get()));
            }

            hurter.addEffect(new MobEffectInstance(AMCompat.effect(AMEffectRegistry.SUNBIRD_CURSE.get()), 600, 0));
         }

         return prev;
      } else {
         return prev;
      }
   }

   public void travel(Vec3 travelVector) {
      if (this.isInWater()) {
         this.moveRelative(0.02F, travelVector);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.800000011920929));
      } else if (this.isInLava()) {
         this.moveRelative(0.02F, travelVector);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
      } else {
         BlockPos ground = AMBlockPos.fromCoords(this.getX(), this.getY() - 1.0, this.getZ());
         float f = 0.91F;
         if (this.onGround()) {
            f = this.level().getBlockState(ground).getFriction(this.level(), ground, this) * 0.91F;
         }

         f = 0.91F;
         if (this.onGround()) {
            f = this.level().getBlockState(ground).getFriction(this.level(), ground, this) * 0.91F;
         }

         this.calculateEntityAnimation(true);
         this.moveRelative(0.2F, travelVector);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(f));
      }

      this.calculateEntityAnimation(false);
   }

   public void tick() {
      super.tick();
      this.prevBirdPitch = this.birdPitch;
      this.prevScorchProgress = this.scorchProgress;
      float f2 = (float)(-((float)this.getDeltaMovement().y * 57.2957763671875));
      this.birdPitch = f2;
      if (this.level().isClientSide()) {
         float radius = 0.35F + this.random.nextFloat() * 3.5F;
         float angle = 0.017453292F * ((this.random.nextBoolean() ? -85.0F : 85.0F) + this.yBodyRot);
         float angleMotion = 0.017453292F * this.yBodyRot;
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         double extraXMotion = -0.2F * Mth.sin((float)(3.141592653589793 + angleMotion));
         double extraZMotion = -0.2F * Mth.cos(angleMotion);
         double yRandom = 0.2F + this.random.nextFloat() * 0.3F;
         this.level()
            .addParticle(
               (ParticleOptions)AMParticleRegistry.SUNBIRD_FEATHER.get(),
               this.getX() + extraX,
               this.getY() + yRandom,
               this.getZ() + extraZ,
               extraXMotion,
               0.0,
               extraZMotion
            );
      } else {
         if (this.tickCount % 100 == 0) {
            if (!this.isScorching() && !this.getScorchingMobs().isEmpty()) {
               this.setScorching(true);
            }

            for (Player e : this.level().getEntitiesOfClass(Player.class, this.getScorchArea(), Predicates.alwaysTrue())) {
               if (!e.hasEffect(AMCompat.effect(AMEffectRegistry.SUNBIRD_BLESSING.get()))
                  && !e.hasEffect(AMCompat.effect(AMEffectRegistry.SUNBIRD_CURSE.get()))) {
                  e.addEffect(new MobEffectInstance(AMCompat.effect(AMEffectRegistry.SUNBIRD_BLESSING.get()), 600, 0));
               }
            }
         }

         if (this.beaconSearchCooldown > 0) {
            this.beaconSearchCooldown--;
         }

         if (this.beaconSearchCooldown <= 0) {
            this.beaconSearchCooldown = 100 + this.random.nextInt(200);
            if (this.level() instanceof ServerLevel) {
               List<BlockPos> beacons = this.getNearbyBeacons(this.blockPosition(), (ServerLevel)this.level(), 64);
               BlockPos closest = null;

               for (BlockPos pos : beacons) {
                  if ((
                        closest == null
                           || this.distanceToSqr(closest.getX(), closest.getY(), closest.getZ()) > this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ())
                     )
                     && this.isValidBeacon(pos)) {
                     closest = pos;
                  }
               }

               if (closest != null && this.isValidBeacon(closest)) {
                  this.beaconPos = closest;
               }
            }

            if (this.beaconPos != null && !this.isValidBeacon(this.beaconPos) && this.tickCount > 40) {
               this.beaconPos = null;
            }
         }
      }

      boolean scorching = this.isScorching();
      if (scorching) {
         if (this.scorchProgress < 20.0F) {
            this.scorchProgress++;
         }
      } else if (this.scorchProgress > 0.0F) {
         this.scorchProgress--;
      }

      if (scorching && this.scorchProgress == 20.0F && !this.level().isClientSide()) {
         if (this.fullScorchTime > 30) {
            this.setScorching(false);
         } else if (this.fullScorchTime % 5 == 0) {
            for (Entity ex : this.getScorchingMobs()) {
               ex.igniteForSeconds(4.0F);
               if (ex instanceof Phantom) {
                  ((Phantom)ex).addEffect(new MobEffectInstance(AMCompat.effect(AMEffectRegistry.SUNBIRD_CURSE.get()), 200, 0));
               }
            }
         }

         this.fullScorchTime++;
      } else {
         this.fullScorchTime = 0;
      }
   }

   private List<LivingEntity> getScorchingMobs() {
      return this.level().getEntitiesOfClass(LivingEntity.class, this.getScorchArea(), SCORCH_PRED);
   }

   public boolean isScorching() {
      return (Boolean)this.entityData.get(SCORCHING);
   }

   public void setScorching(boolean scorching) {
      this.entityData.set(SCORCHING, scorching);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (AMCompat.contains(compound, "BeaconPosX")) {
         int i = AMCompat.getInt(compound, "BeaconPosX");
         int j = AMCompat.getInt(compound, "BeaconPosY");
         int k = AMCompat.getInt(compound, "BeaconPosZ");
         this.beaconPos = new BlockPos(i, j, k);
      } else {
         this.beaconPos = null;
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      BlockPos blockpos = this.beaconPos;
      if (blockpos != null) {
         compound.putInt("BeaconPosX", blockpos.getX());
         compound.putInt("BeaconPosY", blockpos.getY());
         compound.putInt("BeaconPosZ", blockpos.getZ());
      }
   }

   private AABB getScorchArea() {
      return this.getBoundingBox().inflate(15.0, 32.0, 15.0);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      return null;
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).getType() != Type.MISS;
   }

   private List<BlockPos> getNearbyBeacons(BlockPos blockpos, ServerLevel world, int range) {
      PoiManager pointofinterestmanager = world.getPoiManager();
      Stream<BlockPos> stream = pointofinterestmanager.findAll(
         poiTypeHolder -> poiTypeHolder.is(AMPointOfInterestRegistry.BEACON_KEY), Predicates.alwaysTrue(), blockpos, range, Occupancy.ANY
      );
      return stream.collect(Collectors.toList());
   }

   private boolean isValidBeacon(BlockPos pos) {
      BlockEntity te = this.level().getBlockEntity(pos);
      return te instanceof BeaconBlockEntity && !((BeaconBlockEntity)te).getBeamSections().isEmpty();
   }

   public boolean isFlying() {
      return true;
   }

   public float getScorchProgress(float partialTick) {
      return (this.prevScorchProgress + (this.scorchProgress - this.prevScorchProgress) * partialTick) / 20.0F;
   }

   static class MoveHelperController extends MoveControl {
      private final EntitySunbird parentEntity;

      public MoveHelperController(EntitySunbird sunbird) {
         super(sunbird);
         this.parentEntity = sunbird;
      }

      public void tick() {
         if (this.operation == Operation.MOVE_TO) {
            Vec3 vector3d = new Vec3(this.wantedX - this.parentEntity.getX(), this.wantedY - this.parentEntity.getY(), this.wantedZ - this.parentEntity.getZ());
            double d0 = vector3d.length();
            if (d0 < this.parentEntity.getBoundingBox().getSize()) {
               this.operation = Operation.WAIT;
               this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().scale(0.5));
            } else {
               this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05 / d0)));
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
      private final EntitySunbird parentEntity;
      private BlockPos target = null;

      public RandomFlyGoal(EntitySunbird sunbird) {
         this.parentEntity = sunbird;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         MoveControl movementcontroller = this.parentEntity.getMoveControl();
         if (movementcontroller.hasWanted() && this.target != null) {
            return false;
         } else {
            if (this.parentEntity.beaconPos != null) {
               this.target = this.getBlockInViewBeacon(this.parentEntity.beaconPos, 5 + this.parentEntity.random.nextInt(1));
            } else {
               this.target = this.getBlockInViewSunbird();
            }

            if (this.target != null) {
               this.parentEntity
                  .getMoveControl()
                  .setWantedPosition(
                     this.target.getX() + 0.5, this.target.getY() + 0.5, this.target.getZ() + 0.5, this.parentEntity.beaconPos != null ? 0.8 : 1.0
                  );
            }

            return true;
         }
      }

      public boolean canContinueToUse() {
         return this.target != null
            && this.parentEntity.distanceToSqr(Vec3.atCenterOf(this.target)) > 2.4
            && this.parentEntity.getMoveControl().hasWanted()
            && !this.parentEntity.horizontalCollision;
      }

      public void stop() {
         this.target = null;
      }

      public void tick() {
         if (this.target == null) {
            if (this.parentEntity.beaconPos != null) {
               this.target = this.getBlockInViewBeacon(this.parentEntity.beaconPos, 5 + this.parentEntity.random.nextInt(1));
            } else {
               this.target = this.getBlockInViewSunbird();
            }
         }

         if (this.parentEntity.beaconPos != null && this.parentEntity.random.nextInt(100) == 0) {
            this.parentEntity.orbitClockwise = this.parentEntity.random.nextBoolean();
         }

         if (this.target != null) {
            this.parentEntity
               .getMoveControl()
               .setWantedPosition(this.target.getX() + 0.5, this.target.getY() + 0.5, this.target.getZ() + 0.5, this.parentEntity.beaconPos != null ? 0.8 : 1.0);
            if (this.parentEntity.distanceToSqr(Vec3.atCenterOf(this.target)) < 2.5) {
               this.target = null;
            }
         }
      }

      private BlockPos getBlockInViewBeacon(BlockPos orbitPos, float gatheringCircleDist) {
         float angle = 0.15707964F * (this.parentEntity.orbitClockwise ? -this.parentEntity.tickCount : this.parentEntity.tickCount);
         double extraX = gatheringCircleDist * Mth.sin(angle);
         double extraZ = gatheringCircleDist * Mth.cos(angle);
         if (orbitPos != null) {
            BlockPos pos = AMBlockPos.fromCoords(orbitPos.getX() + extraX, orbitPos.getY() + this.parentEntity.random.nextInt(2) + 2, orbitPos.getZ() + extraZ);
            if (this.parentEntity.level().isEmptyBlock(new BlockPos(pos))) {
               return pos;
            }
         }

         return null;
      }

      public BlockPos getBlockInViewSunbird() {
         float radius = -9.45F - this.parentEntity.getRandom().nextInt(24);
         float neg = this.parentEntity.getRandom().nextBoolean() ? 1.0F : -1.0F;
         float renderYawOffset = this.parentEntity.yBodyRot;
         float angle = 0.017453292F * renderYawOffset + 3.15F + this.parentEntity.getRandom().nextFloat() * neg;
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         BlockPos radialPos = AMBlockPos.fromCoords(this.parentEntity.getX() + extraX, 0.0, this.parentEntity.getZ() + extraZ);
         BlockPos ground = this.parentEntity.level().getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, radialPos);
         int distFromGround = (int)this.parentEntity.getY() - ground.getY();
         int flightHeight = Math.max(ground.getY(), 230 + this.parentEntity.getRandom().nextInt(40)) - ground.getY();
         BlockPos newPos = radialPos.above(distFromGround > 16 ? flightHeight : (int)this.parentEntity.getY() + this.parentEntity.getRandom().nextInt(16) + 1);
         return !this.parentEntity.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.parentEntity.distanceToSqr(Vec3.atCenterOf(newPos)) > 6.0 ? newPos : null;
      }
   }
}
