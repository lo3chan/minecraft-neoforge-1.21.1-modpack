package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
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

public class EntityWarpedMosco extends Monster implements IAnimatedEntity {
   public static final Animation ANIMATION_PUNCH_R = Animation.create(25);
   public static final Animation ANIMATION_PUNCH_L = Animation.create(25);
   public static final Animation ANIMATION_SLAM = Animation.create(35);
   public static final Animation ANIMATION_SUCK = Animation.create(60);
   public static final Animation ANIMATION_SPIT = Animation.create(60);
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityWarpedMosco.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> HAND_SIDE = SynchedEntityData.defineId(EntityWarpedMosco.class, EntityDataSerializers.BOOLEAN);
   public float flyLeftProgress;
   public float prevLeftFlyProgress;
   public float flyRightProgress;
   public float prevFlyRightProgress;
   private int animationTick;
   private Animation currentAnimation;
   private boolean isLandNavigator;
   private int timeFlying;
   private int loopSoundTick = 0;

   protected EntityWarpedMosco(EntityType entityType, Level world) {
      super(entityType, world);
      this.xpReward = 30;
      this.switchNavigator(false);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 100.0)
         .add(Attributes.FOLLOW_RANGE, 128.0)
         .add(Attributes.ATTACK_DAMAGE, 10.0)
         .add(Attributes.ARMOR, 10.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.ARMOR_TOUGHNESS, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3);
   }

   private static Animation getRandomAttack(RandomSource rand) {
      return switch (rand.nextInt(4)) {
         case 0 -> ANIMATION_PUNCH_L;
         case 1 -> ANIMATION_PUNCH_R;
         case 2 -> ANIMATION_SLAM;
         case 3 -> ANIMATION_SUCK;
         default -> ANIMATION_SUCK;
      };
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.WARPED_MOSCO_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.WARPED_MOSCO_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.WARPED_MOSCO_HURT.get();
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(0, new EntityWarpedMosco.AttackGoal());
      this.goalSelector.addGoal(4, new EntityWarpedMosco.AIWalkIdle());
      this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 32.0F));
      this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[]{EntityCrimsonMosquito.class, EntityWarpedMosco.class}));
      this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, true));
      this.targetSelector
         .addGoal(
            2,
            new EntityAINearestTarget3D<LivingEntity>(
               this, LivingEntity.class, 50, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.CRIMSON_MOSQUITO_TARGETS)
            )
         );
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigatorWide(this, this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new FlightMoveController(this, 0.7F, false);
         this.navigation = new DirectPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FLYING, false);
      builder.define(HAND_SIDE, true);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public boolean isFlying() {
      return (Boolean)this.entityData.get(FLYING);
   }

   public void setFlying(boolean flying) {
      this.setDashRight(flying != this.isFlying() ? this.random.nextBoolean() : this.isDashRight());
      this.entityData.set(FLYING, flying);
   }

   public boolean isDashRight() {
      return (Boolean)this.entityData.get(HAND_SIDE);
   }

   public void setDashRight(boolean right) {
      this.entityData.set(HAND_SIDE, right);
   }

   public void tick() {
      super.tick();
      this.prevFlyRightProgress = this.flyRightProgress;
      this.prevLeftFlyProgress = this.flyLeftProgress;
      boolean dashRight = this.isDashRight();
      boolean flying = this.isFlying();
      if (flying && dashRight && this.flyRightProgress < 5.0F) {
         this.flyRightProgress++;
      }

      if ((!flying || !dashRight) && this.flyRightProgress > 0.0F) {
         this.flyRightProgress--;
      }

      if (flying && !dashRight && this.flyLeftProgress < 5.0F) {
         this.flyLeftProgress++;
      }

      if ((!flying || dashRight) && this.flyLeftProgress > 0.0F) {
         this.flyLeftProgress--;
      }

      if (!this.level().isClientSide()) {
         if (flying) {
            if (this.isLandNavigator) {
               this.switchNavigator(false);
            }
         } else if (!this.isLandNavigator) {
            this.switchNavigator(true);
         }
      }

      if (flying) {
         if (this.loopSoundTick == 0) {
            this.playSound(AMSoundRegistry.MOSQUITO_LOOP.get(), this.getSoundVolume(), this.getVoicePitch() * 0.3F);
         }

         this.loopSoundTick++;
         if (this.loopSoundTick > 100) {
            this.loopSoundTick = 0;
         }

         this.timeFlying++;
         this.setNoGravity(true);
         if (this.isPassenger() || this.isVehicle()) {
            this.setFlying(false);
         }
      } else {
         this.timeFlying = 0;
         this.setNoGravity(false);
      }

      if (this.horizontalCollision && AMPlatform.mobGriefing(this.level(), this)) {
         boolean flag = false;
         AABB axisalignedbb = this.getBoundingBox().inflate(0.2);

         for (BlockPos blockpos : BlockPos.betweenClosed(
            Mth.floor(axisalignedbb.minX),
            Mth.floor(axisalignedbb.minY),
            Mth.floor(axisalignedbb.minZ),
            Mth.floor(axisalignedbb.maxX),
            Mth.floor(axisalignedbb.maxY),
            Mth.floor(axisalignedbb.maxZ)
         )) {
            BlockState blockstate = this.level().getBlockState(blockpos);
            if (blockstate.is(AMTagRegistry.WARPED_MOSCO_BREAKABLES)) {
               flag = this.level().destroyBlock(blockpos, true, this) || flag;
            }
         }

         if (!flag && this.onGround()) {
            this.jumpFromGround();
         }
      }

      LivingEntity target = this.getTarget();
      if (target != null && this.isAlive()) {
         if (this.getAnimation() == ANIMATION_SUCK && this.getAnimationTick() == 3 && this.distanceTo(target) < 4.7F) {
            AMCompat.startRiding(target, this, true);
         }

         if (this.getAnimation() == ANIMATION_SLAM && this.getAnimationTick() == 19) {
            for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5.0))) {
               if (!this.isAlliedTo(entity) && !(entity instanceof EntityWarpedMosco) && entity != this) {
                  entity.hurt(this.damageSources().mobAttack(this), 10.0F + this.random.nextFloat() * 8.0F);
                  this.launch(entity, true);
               }
            }
         }

         if ((this.getAnimation() == ANIMATION_PUNCH_R || this.getAnimation() == ANIMATION_PUNCH_L)
            && this.getAnimationTick() == 13
            && this.distanceTo(target) < 4.7F) {
            target.hurt(this.damageSources().mobAttack(this), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            this.knockbackRidiculous(target, 0.9F);
         }
      }

      if (this.getAnimation() == ANIMATION_SLAM && this.getAnimationTick() == 19) {
         this.spawnGroundEffects();
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public void spawnGroundEffects() {
      float radius = 2.3F;
      double extraY = 0.800000011920929;

      for (int i = 0; i < 4; i++) {
         for (int i1 = 0; i1 < 20 + this.random.nextInt(12); i1++) {
            double motionX = this.getRandom().nextGaussian() * 0.07;
            double motionY = this.getRandom().nextGaussian() * 0.07;
            double motionZ = this.getRandom().nextGaussian() * 0.07;
            float angle = 0.017453292F * this.yBodyRot + i1;
            double extraX = 2.3F * Mth.sin(3.1415927F + angle);
            double extraZ = 2.3F * Mth.cos(angle);
            BlockPos ground = this.getMoscoGround(
               new BlockPos(Mth.floor(this.getX() + extraX), Mth.floor(this.getY() + 0.800000011920929) - 1, Mth.floor(this.getZ() + extraZ))
            );
            BlockState state = this.level().getBlockState(ground);
            if (state.isSolid() && this.level().isClientSide()) {
               AMCompat.addParticle(
                  this.level(),
                  new BlockParticleOption(ParticleTypes.BLOCK, state),
                  true,
                  this.getX() + extraX,
                  ground.getY() + 0.800000011920929,
                  this.getZ() + extraZ,
                  motionX,
                  motionY,
                  motionZ
               );
            }
         }
      }
   }

   private void launch(Entity e, boolean huge) {
      if (e.onGround()) {
         double d0 = e.getX() - this.getX();
         double d1 = e.getZ() - this.getZ();
         double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
         float f = huge ? 2.0F : 0.5F;
         e.push(d0 / d2 * f, huge ? 0.5 : 0.20000000298023224, d1 / d2 * f);
      }
   }

   @Override
   public Animation getAnimation() {
      return this.currentAnimation;
   }

   @Override
   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   @Override
   public int getAnimationTick() {
      return this.animationTick;
   }

   @Override
   public void setAnimationTick(int i) {
      this.animationTick = i;
   }

   @Override
   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_PUNCH_L, ANIMATION_PUNCH_R, ANIMATION_SLAM, ANIMATION_SUCK, ANIMATION_SPIT};
   }

   private BlockPos getMoscoGround(BlockPos in) {
      BlockPos position = new BlockPos(in.getX(), (int)this.getY(), in.getZ());

      while (position.getY() > -62 && !this.level().getBlockState(position).isSolid() && this.level().getFluidState(position).isEmpty()) {
         position = position.below();
      }

      return position;
   }

   public Vec3 getBlockGrounding(Vec3 fleePos) {
      float radius = -9.45F - this.getRandom().nextInt(24);
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = AMBlockPos.fromCoords(fleePos.x() + extraX, this.getY(), fleePos.z() + extraZ);
      BlockPos ground = this.getMoscoGround(radialPos);
      if (ground.getY() == -62) {
         return this.position();
      } else {
         ground = this.blockPosition();

         while (ground.getY() > -62 && !this.level().getBlockState(ground).isSolid()) {
            ground = ground.below();
         }

         return !this.isTargetBlocked(Vec3.atCenterOf(ground.above())) ? Vec3.atCenterOf(ground) : null;
      }
   }

   public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
      float radius = -9.45F - this.getRandom().nextInt(24) - radiusAdd;
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.x() + extraX), 0, (int)(fleePos.z() + extraZ));
      BlockPos ground = this.getMoscoGround(radialPos);
      int distFromGround = (int)this.getY() - ground.getY();
      int flightHeight = 4 + this.getRandom().nextInt(10);
      BlockPos newPos = ground.above(distFromGround > 8 ? flightHeight : this.getRandom().nextInt(6) + 1);
      return !this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1.0 ? Vec3.atCenterOf(newPos) : null;
   }

   public void knockbackRidiculous(LivingEntity target, float power) {
      AMCompat.knockback(target, power, this.getX() - target.getX(), this.getZ() - target.getZ());
      float knockbackResist = (float)Mth.clamp(1.0 - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 0.0, 1.0);
      target.setDeltaMovement(target.getDeltaMovement().add(0.0, knockbackResist * power * 0.45F, 0.0));
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).getType() != Type.MISS;
   }

   private boolean isOverLiquid() {
      BlockPos position = this.blockPosition();

      while (position.getY() > 2 && this.level().isEmptyBlock(position)) {
         position = position.below();
      }

      return !this.level().getFluidState(position).isEmpty();
   }

   public void travel(Vec3 travelVector) {
      if ((this.getAnimation() == ANIMATION_SUCK || this.getAnimation() == ANIMATION_SLAM) && this.getAnimationTick() > 8) {
         if (this.getNavigation().getPath() != null) {
            this.getNavigation().stop();
         }

         travelVector = Vec3.ZERO;
         super.travel(travelVector);
      } else {
         super.travel(travelVector);
      }
   }

   public void positionRider(Entity passenger, MoveFunction moveFunc) {
      super.positionRider(passenger, moveFunc);
      if (this.hasPassenger(passenger)) {
         int tick = 5;
         if (this.getAnimation() == ANIMATION_SUCK) {
            tick = this.getAnimationTick();
         } else {
            passenger.stopRiding();
         }

         float radius = 2.0F;
         float angle = 0.017453292F * this.yBodyRot;
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         double extraY = tick < 10 ? 0.0 : 0.15F * Mth.clamp(tick - 10, 0, 15);
         passenger.setPos(this.getX() + extraX, this.getY() + extraY + 0.10000000149011612, this.getZ() + extraZ);
         if ((tick - 10) % 4 == 0) {
            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
            passenger.hurt(this.damageSources().mobAttack(this), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
         }
      }
   }

   public boolean canRiderInteract() {
      return true;
   }

   public boolean shouldRiderSit() {
      return false;
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.warpedMoscoSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   private void spit(LivingEntity target) {
      if (this.getAnimation() == ANIMATION_SPIT) {
         this.lookAt(target, 100.0F, 100.0F);
         this.yBodyRot = this.yHeadRot;

         for (int i = 0; i < 2 + this.random.nextInt(2); i++) {
            EntityHemolymph llamaspitentity = new EntityHemolymph(this.level(), this);
            double d0 = target.getX() - this.getX();
            double d1 = target.getY(0.3333333333333333) - llamaspitentity.getY();
            double d2 = target.getZ() - this.getZ();
            float f = Mth.sqrt((float)(d0 * d0 + d2 * d2)) * 0.2F;
            llamaspitentity.shoot(d0, d1 + f, d2, 1.5F, 5.0F);
            if (!this.isSilent()) {
               this.gameEvent(GameEvent.PROJECTILE_SHOOT);
               this.level()
                  .playSound(
                     null,
                     this.getX(),
                     this.getY(),
                     this.getZ(),
                     SoundEvents.LLAMA_SPIT,
                     this.getSoundSource(),
                     1.0F,
                     1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
                  );
            }

            this.level().addFreshEntity(llamaspitentity);
         }
      }
   }

   private boolean shouldRangeAttack(LivingEntity target) {
      return this.getHealth() < Math.floor(this.getMaxHealth() * 0.25F) ? true : this.getHealth() < this.getHealth() * 0.5F && this.distanceTo(target) > 10.0F;
   }

   private class AIWalkIdle extends Goal {
      protected final EntityWarpedMosco mosco;
      protected double x;
      protected double y;
      protected double z;
      private boolean flightTarget = false;

      public AIWalkIdle() {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.mosco = EntityWarpedMosco.this;
      }

      public boolean canUse() {
         if (this.mosco.isVehicle() || this.mosco.getTarget() != null && this.mosco.getTarget().isAlive() || this.mosco.isPassenger()) {
            return false;
         } else if (this.mosco.getRandom().nextInt(30) != 0 && !this.mosco.isFlying()) {
            return false;
         } else {
            if (this.mosco.onGround()) {
               this.flightTarget = EntityWarpedMosco.this.random.nextInt(8) == 0;
            } else {
               this.flightTarget = EntityWarpedMosco.this.random.nextInt(5) > 0 && this.mosco.timeFlying < 200;
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
            this.mosco.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
         } else {
            this.mosco.getNavigation().moveTo(this.x, this.y, this.z, 1.0);
         }

         if (!this.flightTarget && EntityWarpedMosco.this.isFlying() && this.mosco.onGround()) {
            this.mosco.setFlying(false);
         }

         if (EntityWarpedMosco.this.isFlying() && this.mosco.onGround() && this.mosco.timeFlying > 10) {
            this.mosco.setFlying(false);
         }
      }

      @Nullable
      protected Vec3 getPosition() {
         Vec3 vector3d = this.mosco.position();
         if (this.mosco.isOverLiquid()) {
            this.flightTarget = true;
         }

         if (this.flightTarget) {
            return this.mosco.timeFlying >= 50 && !this.mosco.isOverLiquid()
               ? this.mosco.getBlockGrounding(vector3d)
               : this.mosco.getBlockInViewAway(vector3d, 0.0F);
         } else {
            return LandRandomPos.getPos(this.mosco, 20, 7);
         }
      }

      public boolean canContinueToUse() {
         return this.flightTarget
            ? this.mosco.isFlying() && this.mosco.distanceToSqr(this.x, this.y, this.z) > 20.0 && !this.mosco.horizontalCollision
            : !this.mosco.getNavigation().isDone() && !this.mosco.isVehicle();
      }

      public void start() {
         if (this.flightTarget) {
            this.mosco.setFlying(true);
            this.mosco.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
         } else {
            this.mosco.getNavigation().moveTo(this.x, this.y, this.z, 1.0);
         }
      }

      public void stop() {
         this.mosco.getNavigation().stop();
         super.stop();
      }
   }

   private class AttackGoal extends Goal {
      private int upTicks = 0;
      private int dashCooldown = 0;
      private boolean ranged = false;
      private BlockPos farTarget = null;

      public AttackGoal() {
      }

      public boolean canUse() {
         return EntityWarpedMosco.this.getTarget() != null;
      }

      public void tick() {
         if (this.dashCooldown > 0) {
            this.dashCooldown--;
         }

         if (EntityWarpedMosco.this.getTarget() != null) {
            LivingEntity target = EntityWarpedMosco.this.getTarget();
            this.ranged = EntityWarpedMosco.this.shouldRangeAttack(target);
            if (EntityWarpedMosco.this.isFlying()
               || this.ranged
               || EntityWarpedMosco.this.distanceTo(target) > 12.0F
                  && !EntityWarpedMosco.this.isTargetBlocked(target.position().add(0.0, target.getBbHeight() * 0.6F, 0.0))) {
               float speedRush = 5.0F;
               this.upTicks++;
               EntityWarpedMosco.this.setFlying(true);
               if (!this.ranged) {
                  if (this.upTicks <= 20 && !(EntityWarpedMosco.this.distanceTo(target) < 6.0F)) {
                     EntityWarpedMosco.this.getMoveControl()
                        .setWantedPosition(EntityWarpedMosco.this.getX(), EntityWarpedMosco.this.getY() + 3.0, EntityWarpedMosco.this.getZ(), 0.5);
                  } else {
                     EntityWarpedMosco.this.getMoveControl()
                        .setWantedPosition(target.getX(), target.getY() + target.getEyeHeight() * 0.6F, target.getZ(), speedRush);
                  }
               } else {
                  if (this.farTarget == null || EntityWarpedMosco.this.distanceToSqr(Vec3.atCenterOf(this.farTarget)) < 9.0) {
                     this.farTarget = this.getAvoidTarget(target);
                  }

                  if (this.farTarget != null) {
                     EntityWarpedMosco.this.getMoveControl()
                        .setWantedPosition(this.farTarget.getX(), this.farTarget.getY() + target.getEyeHeight() * 0.6F, this.farTarget.getZ(), 3.0);
                  }

                  EntityWarpedMosco.this.setAnimation(EntityWarpedMosco.ANIMATION_SPIT);
                  if (this.upTicks % 30 == 0) {
                     EntityWarpedMosco.this.heal(1.0F);
                  }

                  int tick = EntityWarpedMosco.this.getAnimationTick();
                  switch (tick) {
                     case 10:
                     case 20:
                     case 30:
                     case 40:
                        EntityWarpedMosco.this.spit(target);
                  }
               }
            } else {
               EntityWarpedMosco.this.getNavigation().moveTo(EntityWarpedMosco.this.getTarget(), 1.25);
            }

            if (EntityWarpedMosco.this.isFlying()) {
               if (EntityWarpedMosco.this.distanceTo(target) < 4.3F) {
                  if (this.dashCooldown == 0 || target.onGround() || target.isInLava() || target.isInWater()) {
                     target.hurt(EntityWarpedMosco.this.damageSources().mobAttack(EntityWarpedMosco.this), 5.0F);
                     EntityWarpedMosco.this.knockbackRidiculous(target, 1.0F);
                     this.dashCooldown = 30;
                  }

                  float groundHeight = EntityWarpedMosco.this.getMoscoGround(EntityWarpedMosco.this.blockPosition()).getY();
                  if (Math.abs(EntityWarpedMosco.this.getY() - groundHeight) < 3.0 && !EntityWarpedMosco.this.isOverLiquid()) {
                     EntityWarpedMosco.this.timeFlying += 300;
                     EntityWarpedMosco.this.setFlying(false);
                  }
               }
            } else if (EntityWarpedMosco.this.distanceTo(target) < 4.0F && EntityWarpedMosco.this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
               Animation animation = EntityWarpedMosco.getRandomAttack(EntityWarpedMosco.this.random);
               if (animation == EntityWarpedMosco.ANIMATION_SUCK && target.isPassenger()) {
                  animation = EntityWarpedMosco.ANIMATION_SLAM;
               }

               EntityWarpedMosco.this.setAnimation(animation);
            }
         }
      }

      public BlockPos getAvoidTarget(LivingEntity target) {
         float radius = 10 + EntityWarpedMosco.this.getRandom().nextInt(8);
         float angle = 0.017453292F * (target.yHeadRot + 90.0F + EntityWarpedMosco.this.getRandom().nextInt(180));
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         BlockPos radialPos = AMBlockPos.fromCoords(target.getX() + extraX, target.getY() + 1.0, target.getZ() + extraZ);
         return EntityWarpedMosco.this.distanceToSqr(Vec3.atCenterOf(radialPos)) > 30.0
               && !EntityWarpedMosco.this.isTargetBlocked(Vec3.atCenterOf(radialPos))
               && EntityWarpedMosco.this.distanceToSqr(Vec3.atCenterOf(radialPos)) > 6.0
            ? radialPos
            : EntityWarpedMosco.this.blockPosition();
      }

      public void stop() {
         this.upTicks = 0;
         this.dashCooldown = 0;
         this.ranged = false;
      }
   }
}
