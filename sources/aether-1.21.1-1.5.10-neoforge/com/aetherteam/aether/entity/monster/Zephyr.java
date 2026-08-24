package com.aetherteam.aether.entity.monster;

import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.entity.EntityUtil;
import com.aetherteam.aether.entity.projectile.ZephyrSnowball;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Zephyr extends FlyingMob implements Enemy {
   private static final EntityDataAccessor<Integer> DATA_CHARGE_TIME_ID = SynchedEntityData.defineId(Zephyr.class, EntityDataSerializers.INT);
   private int cloudScale;
   private int cloudScaleAdd;
   private float tailRot;
   private float tailRotAdd;

   public Zephyr(EntityType<? extends Zephyr> type, Level level) {
      super(type, level);
      this.moveControl = new Zephyr.ZephyrMoveControl(this);
      this.xpReward = 5;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(5, new Zephyr.RandomFloatAroundGoal(this));
      this.goalSelector.addGoal(7, new Zephyr.ZephyrLookGoal(this));
      this.goalSelector.addGoal(7, new Zephyr.ZephyrShootSnowballGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true, false));
   }

   public static Builder createMobAttributes() {
      return FlyingMob.createMobAttributes().add(Attributes.MAX_HEALTH, 5.0).add(Attributes.FOLLOW_RANGE, 50.0);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_CHARGE_TIME_ID, 0);
   }

   public static boolean checkZephyrSpawnRules(EntityType<? extends Zephyr> zephyr, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
      return Mob.checkMobSpawnRules(zephyr, level, reason, pos, random)
         && EntityUtil.wholeHitboxCanSeeSky(level, pos, 2)
         && level.getDifficulty() != Difficulty.PEACEFUL
         && (reason != MobSpawnType.NATURAL || random.nextInt(11) == 0);
   }

   public void aiStep() {
      super.aiStep();
      if (this.getY() < this.level().getMinBuildHeight() - 2 || this.getY() > this.level().getMaxBuildHeight()) {
         this.discard();
      }

      if (this.level().isClientSide()) {
         this.cloudScale = this.cloudScale + this.cloudScaleAdd;
         this.tailRot = this.tailRot + this.tailRotAdd;
         if (this.getChargeTime() < 20 && this.getChargeTime() > 0) {
            this.cloudScaleAdd = 1;
         } else {
            this.cloudScaleAdd = 0;
            this.cloudScale = 0;
         }

         this.tailRotAdd = 0.015F;
         if (this.tailRot >= 6.2831855F) {
            this.tailRot -= 6.2831855F;
         }
      }
   }

   public int getChargeTime() {
      return (Integer)this.getEntityData().get(DATA_CHARGE_TIME_ID);
   }

   public void setChargeTime(int chargeTime) {
      this.getEntityData().set(DATA_CHARGE_TIME_ID, chargeTime);
   }

   public int getCloudScale() {
      return this.cloudScale;
   }

   public int getCloudScaleAdd() {
      return this.cloudScaleAdd;
   }

   public float getTailRot() {
      return this.tailRot;
   }

   public float getTailRotAdd() {
      return this.tailRotAdd;
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_ZEPHYR_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return (SoundEvent)AetherSoundEvents.ENTITY_ZEPHYR_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_ZEPHYR_DEATH.get();
   }

   protected float getSoundVolume() {
      return 3.0F;
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return true;
   }

   protected boolean shouldDespawnInPeaceful() {
      return true;
   }

   protected static class RandomFloatAroundGoal extends Goal {
      private final Zephyr zephyr;

      public RandomFloatAroundGoal(Zephyr zephyr) {
         this.zephyr = zephyr;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         MoveControl moveControl = this.zephyr.getMoveControl();
         if (!moveControl.hasWanted()) {
            return true;
         } else {
            double d0 = moveControl.getWantedX() - this.zephyr.getX();
            double d1 = moveControl.getWantedY() - this.zephyr.getY();
            double d2 = moveControl.getWantedZ() - this.zephyr.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            return d3 < 1.0 || d3 > 3600.0;
         }
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void start() {
         RandomSource random = this.zephyr.getRandom();
         double d0 = this.zephyr.getX() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
         double d1 = this.zephyr.getY() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
         double d2 = this.zephyr.getZ() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
         this.zephyr.getMoveControl().setWantedPosition(d0, d1, d2, 1.0);
      }
   }

   protected static class ZephyrLookGoal extends Goal {
      private final Zephyr zephyr;

      public ZephyrLookGoal(Zephyr zephyr) {
         this.zephyr = zephyr;
         this.setFlags(EnumSet.of(Flag.LOOK));
      }

      public boolean canUse() {
         return true;
      }

      public void tick() {
         if (this.zephyr.getTarget() == null) {
            Vec3 vec3d = this.zephyr.getDeltaMovement();
            this.zephyr.setYRot(-((float)Mth.atan2(vec3d.x(), vec3d.z())) * 57.295776F);
            this.zephyr.yBodyRot = this.zephyr.getYRot();
         } else {
            LivingEntity livingEntity = this.zephyr.getTarget();
            if (livingEntity.distanceToSqr(this.zephyr) < 4096.0) {
               double x = livingEntity.getX() - this.zephyr.getX();
               double z = livingEntity.getZ() - this.zephyr.getZ();
               this.zephyr.setYRot(-((float)Mth.atan2(x, z)) * 57.295776F);
               this.zephyr.setYBodyRot(this.zephyr.getYRot());
            }
         }
      }
   }

   protected static class ZephyrMoveControl extends MoveControl {
      private final Zephyr zephyr;
      private int floatDuration;

      public ZephyrMoveControl(Zephyr zephyr) {
         super(zephyr);
         this.zephyr = zephyr;
      }

      public void tick() {
         if (this.operation == Operation.MOVE_TO && this.floatDuration-- <= 0) {
            this.floatDuration = this.floatDuration + this.zephyr.getRandom().nextInt(5) + 2;
            Vec3 vec3d = new Vec3(this.wantedX - this.zephyr.getX(), this.wantedY - this.zephyr.getY(), this.wantedZ - this.zephyr.getZ());
            double d0 = vec3d.length();
            vec3d = vec3d.normalize();
            if (this.canReach(vec3d, Mth.ceil(d0))) {
               this.zephyr.setDeltaMovement(this.zephyr.getDeltaMovement().add(vec3d.scale(0.1)));
            } else {
               this.operation = Operation.WAIT;
            }
         }
      }

      private boolean canReach(Vec3 pos, int distance) {
         AABB axisalignedbb = this.zephyr.getBoundingBox();

         for (int i = 1; i < distance; i++) {
            axisalignedbb = axisalignedbb.move(pos);
            if (!this.zephyr.level().noCollision(this.zephyr, axisalignedbb)) {
               return false;
            }
         }

         return true;
      }
   }

   protected static class ZephyrShootSnowballGoal extends Goal {
      private final Zephyr zephyr;

      public ZephyrShootSnowballGoal(Zephyr zephyr) {
         this.zephyr = zephyr;
      }

      public boolean canUse() {
         return this.zephyr.getTarget() != null;
      }

      public void start() {
         this.zephyr.setChargeTime(0);
      }

      public void stop() {
         this.zephyr.setChargeTime(0);
      }

      public void tick() {
         LivingEntity livingEntity = this.zephyr.getTarget();
         if (livingEntity != null) {
            if (livingEntity.distanceToSqr(this.zephyr) < 1600.0 && this.zephyr.hasLineOfSight(livingEntity)) {
               Level level = this.zephyr.level();
               this.zephyr.setChargeTime(this.zephyr.getChargeTime() + 1);
               if (this.zephyr.getChargeTime() == 10) {
                  if (this.zephyr.getAmbientSound() != null) {
                     this.zephyr
                        .playSound(
                           this.zephyr.getAmbientSound(),
                           this.zephyr.getSoundVolume(),
                           (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F + 1.0F
                        );
                  }
               } else if (this.zephyr.getChargeTime() == 20) {
                  Vec3 look = this.zephyr.getViewVector(1.0F);
                  double accelX = livingEntity.getX() - (this.zephyr.getX() + look.x() * 4.0);
                  double accelY = livingEntity.getY(0.5) - (0.5 + this.zephyr.getY(0.5));
                  double accelZ = livingEntity.getZ() - (this.zephyr.getZ() + look.z() * 4.0);
                  this.zephyr
                     .playSound(
                        (SoundEvent)AetherSoundEvents.ENTITY_ZEPHYR_SHOOT.get(),
                        this.zephyr.getSoundVolume(),
                        (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F + 1.0F
                     );
                  ZephyrSnowball snowball = new ZephyrSnowball(level, this.zephyr, accelX, accelY, accelZ);
                  snowball.setPos(this.zephyr.getX() + look.x() * 4.0, this.zephyr.getY(0.5) + 0.5, this.zephyr.getZ() + look.z() * 4.0);
                  level.addFreshEntity(snowball);
                  this.zephyr.setChargeTime(-40);
               }
            } else if (this.zephyr.getChargeTime() > 0) {
               this.zephyr.setChargeTime(this.zephyr.getChargeTime() - 1);
            }
         }
      }
   }
}
