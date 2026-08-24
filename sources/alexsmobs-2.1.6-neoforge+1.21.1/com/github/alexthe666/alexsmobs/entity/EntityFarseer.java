package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.message.MessageSendVisualFlagFromServer;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMDamageTypes;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityFarseer extends Monster implements IAnimatedEntity {
   public static final Animation ANIMATION_EMERGE = Animation.create(50);
   private static final int HANDS = 4;
   private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(EntityFarseer.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> HAS_EMERGED = SynchedEntityData.defineId(EntityFarseer.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> MELEEING = SynchedEntityData.defineId(EntityFarseer.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> LASER_ENTITY_ID = SynchedEntityData.defineId(EntityFarseer.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> LASER_ATTACK_LVL = SynchedEntityData.defineId(EntityFarseer.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> LASER_DISTANCE = SynchedEntityData.defineId(EntityFarseer.class, EntityDataSerializers.FLOAT);
   public static final int LASER_ATTACK_DURATION = 10;
   public final double[][] positions = new double[64][4];
   public final float[] claspProgress = new float[4];
   public final float[] prevClaspProgress = new float[4];
   public final float[] strikeProgress = new float[4];
   public final float[] prevStrikeProgress = new float[4];
   public final boolean[] isStriking = new boolean[4];
   public int posPointer = -1;
   public float angryProgress;
   public float prevAngryProgress;
   public Vec3 angryShakeVec = Vec3.ZERO;
   public float prevLaserLvl;
   private float faceCameraProgress;
   private float prevFaceCameraProgress;
   private LivingEntity laserTargetEntity;
   private int claspingHand = -1;
   private int animationTick;
   private Animation currentAnimation;
   private int meleeCooldown = 0;

   protected EntityFarseer(EntityType<? extends Monster> type, Level level) {
      super(type, level);
      this.moveControl = new EntityFarseer.MoveController();
      this.xpReward = 20;
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 70.0)
         .add(Attributes.ARMOR, 6.0)
         .add(Attributes.FLYING_SPEED, 0.5)
         .add(Attributes.ATTACK_DAMAGE, 4.5)
         .add(Attributes.MOVEMENT_SPEED, 0.3499999940395355);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.farseerSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public boolean isNoGravity() {
      return true;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      EntityDimensions dimensions = super.getDefaultDimensions(pose);
      return dimensions.withEyeHeight(dimensions.height() * 0.7F);
   }

   protected PathNavigation createNavigation(Level level) {
      FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, this.level());
      flyingpathnavigation.setCanOpenDoors(false);
      flyingpathnavigation.setCanFloat(true);
      flyingpathnavigation.getNodeEvaluator().setCanPassDoors(true);
      return flyingpathnavigation;
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new EntityFarseer.AttackGoal());
      this.goalSelector.addGoal(3, new EntityFarseer.RandomFlyGoal(this));
      this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 10.0F));
      this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, 3, false, true, null));
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Emerged", this.hasEmerged());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setHasEmerged(AMCompat.getBoolean(compound, "Emerged"));
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.FARSEER_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.FARSEER_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.FARSEER_HURT.get();
   }

   public static boolean checkFarseerSpawnRules(
      EntityType<? extends Monster> animal, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return worldIn.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(worldIn, pos, random) && isFarseerArea(worldIn, pos);
   }

   private static boolean isFarseerArea(ServerLevelAccessor iServerWorld, BlockPos pos) {
      return !AMConfig.restrictFarseerSpawns || iServerWorld.getWorldBorder().getDistanceToBorder(pos.getX(), pos.getZ()) < AMConfig.farseerBorderSpawnDistance;
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(HAS_EMERGED, false);
      builder.define(MELEEING, false);
      builder.define(ANGRY, false);
      builder.define(LASER_ENTITY_ID, -1);
      builder.define(LASER_ATTACK_LVL, 0);
      builder.define(LASER_DISTANCE, 0.0F);
   }

   public boolean isAngry() {
      return (Boolean)this.entityData.get(ANGRY);
   }

   public void setAngry(boolean angry) {
      this.entityData.set(ANGRY, angry);
   }

   public boolean hasLaser() {
      return (Integer)this.entityData.get(LASER_ENTITY_ID) != -1 && this.getAnimation() != ANIMATION_EMERGE;
   }

   public int getLaserAttackLvl() {
      return (Integer)this.entityData.get(LASER_ATTACK_LVL);
   }

   public float getLaserDistance() {
      return (Float)this.entityData.get(LASER_DISTANCE);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> p_32834_) {
      super.onSyncedDataUpdated(p_32834_);
      if (LASER_ENTITY_ID.equals(p_32834_)) {
         this.laserTargetEntity = null;
      }
   }

   @Nullable
   public LivingEntity getLaserTarget() {
      if (!this.hasLaser()) {
         return null;
      } else if (this.level().isClientSide()) {
         if (this.laserTargetEntity != null) {
            return this.laserTargetEntity;
         } else {
            Entity fromID = this.level().getEntity((Integer)this.entityData.get(LASER_ENTITY_ID));
            if (fromID instanceof LivingEntity) {
               this.laserTargetEntity = (LivingEntity)fromID;
               return this.laserTargetEntity;
            } else {
               return null;
            }
         }
      } else {
         return this.getTarget();
      }
   }

   public boolean hasEmerged() {
      return (Boolean)this.entityData.get(HAS_EMERGED);
   }

   public void setHasEmerged(boolean emerged) {
      this.entityData.set(HAS_EMERGED, emerged);
   }

   public void tick() {
      super.tick();
      this.prevFaceCameraProgress = this.faceCameraProgress;
      this.prevLaserLvl = this.getLaserAttackLvl();
      if (this.getAnimation() == ANIMATION_EMERGE) {
         this.setHasEmerged(true);
         this.faceCameraProgress = 1.0F;
      } else if (this.faceCameraProgress > 0.0F) {
         this.faceCameraProgress = Math.max(0.0F, this.faceCameraProgress - 0.2F);
      }

      this.prevAngryProgress = this.angryProgress;

      for (int i = 0; i < 4; i++) {
         this.prevClaspProgress[i] = this.claspProgress[i];
         this.prevStrikeProgress[i] = this.strikeProgress[i];
      }

      if (this.posPointer < 0) {
         for (int i = 0; i < this.positions.length; i++) {
            this.positions[i][0] = this.getX();
            this.positions[i][1] = this.getY();
            this.positions[i][2] = this.getZ();
            this.positions[i][3] = this.yBodyRot;
         }
      }

      if (++this.posPointer == this.positions.length) {
         this.posPointer = 0;
      }

      this.positions[this.posPointer][0] = this.getX();
      this.positions[this.posPointer][1] = this.getY();
      this.positions[this.posPointer][2] = this.getZ();
      this.positions[this.posPointer][3] = this.yBodyRot;
      if (this.isAngry() && this.angryProgress < 5.0F) {
         this.angryProgress++;
      }

      if (!this.isAngry() && this.angryProgress > 0.0F) {
         this.angryProgress--;
      }

      if (this.isAlive()) {
         if (this.random.nextInt(this.isAngry() ? 12 : 40) == 0 && this.claspingHand == -1) {
            int i = Mth.clamp(this.random.nextInt(4), 0, 3);
            if (this.claspProgress[i] == 0.0F) {
               this.claspingHand = i;
            }
         }

         if (this.claspingHand >= 0) {
            if (this.claspProgress[this.claspingHand] < 5.0F) {
               this.claspProgress[this.claspingHand]++;
            } else {
               this.claspingHand = -1;
            }
         } else {
            for (int i = 0; i < 4; i++) {
               if (this.claspProgress[i] > 0.0F) {
                  this.claspProgress[i]--;
               }
            }
         }

         if (!this.hasEmerged()) {
            this.setInvisible(true);
            if (this.level().hasNearbyAlivePlayer(this.getX(), this.getY(), this.getZ(), 9.0)) {
               this.setAnimation(ANIMATION_EMERGE);
            }
         } else {
            this.setInvisible(this.hasEffect(MobEffects.INVISIBILITY));
         }

         if (this.getAnimation() == ANIMATION_EMERGE) {
            if (this.level().isClientSide()) {
               this.level()
                  .addParticle(
                     (ParticleOptions)AMParticleRegistry.STATIC_SPARK.get(),
                     this.getRandomX(0.75),
                     this.getRandomY(),
                     this.getRandomZ(0.75),
                     (this.getRandom().nextFloat() - 0.5F) * 0.2F,
                     this.getRandom().nextFloat() * 0.2F,
                     (this.getRandom().nextFloat() - 0.5F) * 0.2F
                  );
            }

            if (this.getAnimationTick() == 1) {
               this.playSound(AMSoundRegistry.FARSEER_EMERGE.get(), this.getSoundVolume(), this.getVoicePitch());
            }
         }

         LivingEntity target = this.getTarget();
         if (target != null && (Boolean)this.entityData.get(MELEEING) && this.meleeCooldown == 0) {
            this.meleeCooldown = 5;
            int ix = this.random.nextInt(4);
            this.isStriking[ix] = true;
            this.level().broadcastEntityEvent(this, (byte)(40 + ix));
         }

         if (this.meleeCooldown > 0) {
            this.meleeCooldown--;
         }

         for (int ix = 0; ix < 4; ix++) {
            if (!this.isStriking[ix] || !(Boolean)this.entityData.get(MELEEING)) {
               if (this.strikeProgress[ix] > 0.0F) {
                  this.strikeProgress[ix]--;
               }
            } else if (this.isStriking[ix]) {
               if (this.strikeProgress[ix] < 5.0F) {
                  this.strikeProgress[ix]++;
               }

               if (this.strikeProgress[ix] == 5.0F) {
                  this.isStriking[ix] = false;
                  this.level().broadcastEntityEvent(this, (byte)(44 + ix));
                  if (target != null && this.distanceTo(target) <= 4.0F) {
                     target.hurt(this.damageSources().mobAttack(this), 5 + this.random.nextInt(5));
                  }
               }
            }
         }

         if (this.hasLaser()) {
            LivingEntity livingentity = this.getLaserTarget();
            if (livingentity != null) {
               Vec3 hit = this.calculateLaserHit(livingentity.getEyePosition());
               this.entityData.set(LASER_DISTANCE, (float)hit.distanceTo(this.getEyePosition()));
               this.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
               this.getLookControl().tick();
               double d0 = hit.x - this.getX();
               double d1 = hit.y - this.getEyeY();
               double d2 = hit.z - this.getZ();
               double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
               d0 /= d3;
               d1 /= d3;
               d2 /= d3;
               float progress = this.getLaserAttackLvl() / 10.0F;
               double d4 = this.random.nextDouble();

               while (d4 < d3 * progress) {
                  d4 += 0.5 + 2.0 * this.random.nextDouble();
                  double width = d4 / (d3 * progress);
                  double d5 = (this.random.nextDouble() - 0.5) * width;
                  double d6 = (this.random.nextDouble() - 0.5) * width;
                  this.level()
                     .addParticle(
                        (ParticleOptions)AMParticleRegistry.STATIC_SPARK.get(),
                        this.getX() + d0 * d4 + d5,
                        this.getEyeY() + d1 * d4,
                        this.getZ() + d2 * d4 + d6,
                        (this.getRandom().nextFloat() - 0.5F) * 0.2F,
                        this.getRandom().nextFloat() * 0.2F,
                        (this.getRandom().nextFloat() - 0.5F) * 0.2F
                     );
               }
            }
         }
      }

      if (this.isAngry()) {
         this.angryShakeVec = new Vec3(this.random.nextFloat() - 0.5F, this.random.nextFloat() - 0.5F, this.random.nextFloat() - 0.5F);
      } else {
         this.angryShakeVec = Vec3.ZERO;
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      if (id >= 40 && id <= 43) {
         int i = id - 40;
         this.isStriking[i] = true;
      } else if (id >= 44 && id <= 48) {
         int i = id - 44;
         this.isStriking[i] = false;
      } else {
         super.handleEntityEvent(id);
      }
   }

   public double getLatencyVar(int pointer, int index, float partialTick) {
      if (this.isDeadOrDying()) {
         partialTick = 1.0F;
      }

      int i = this.posPointer - pointer & 63;
      int j = this.posPointer - pointer - 1 & 63;
      double d0 = this.positions[j][index];
      double d1 = Mth.wrapDegrees(this.positions[i][index] - d0);
      return d0 + d1 * partialTick;
   }

   public Vec3 getLatencyOffsetVec(int offset, float partialTick) {
      double d0 = Mth.lerp(partialTick, this.xOld, this.getX());
      double d1 = Mth.lerp(partialTick, this.yOld, this.getY());
      double d2 = Mth.lerp(partialTick, this.zOld, this.getZ());
      float renderYaw = (float)this.getLatencyVar(offset, 3, partialTick);
      return new Vec3(
            this.getLatencyVar(offset, 0, partialTick) - d0, this.getLatencyVar(offset, 1, partialTick) - d1, this.getLatencyVar(offset, 2, partialTick) - d2
         )
         .yRot(renderYaw * 0.017453292F);
   }

   public Vec3 calculateAfterimagePos(float partialTick, boolean flip, float speed) {
      float f = (partialTick + this.tickCount) * speed;
      float f1 = 0.1F;
      Vec3 v = new Vec3((float)Math.sin(f) * f1, (float)Math.cos(f - 1.5707963267948966) * f1, -((float)Math.cos(f)) * f1);
      return flip ? new Vec3(v.z, -v.y, v.x) : v;
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
   public Animation getAnimation() {
      return this.currentAnimation;
   }

   @Override
   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   @Override
   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_EMERGE};
   }

   public int getPortalFrame() {
      if (this.getAnimation() == ANIMATION_EMERGE) {
         if (this.getAnimationTick() < 10) {
            return 0;
         } else if (this.getAnimationTick() < 20) {
            return 1;
         } else if (this.getAnimationTick() < 30) {
            return 2;
         } else if (this.getAnimationTick() > 40) {
            int i = 50 - this.getAnimationTick();
            return i < 6 ? (i < 3 ? 0 : 1) : 2;
         } else {
            return 3;
         }
      } else {
         return 0;
      }
   }

   public float getPortalOpacity(float partialTicks) {
      if (this.getAnimation() == ANIMATION_EMERGE) {
         float tick = this.getAnimationTick() - 1 + partialTicks;
         return tick < 5.0F ? tick / 5.0F : 1.0F;
      } else {
         return 0.0F;
      }
   }

   public float getFarseerOpacity(float partialTicks) {
      if (this.getAnimation() == ANIMATION_EMERGE) {
         float tick = this.getAnimationTick() - 1 + partialTicks;
         float prog = tick / ANIMATION_EMERGE.getDuration();
         return prog > 0.5F ? (prog - 0.5F) / 0.5F : 0.0F;
      } else {
         return 1.0F;
      }
   }

   public float getFacingCameraAmount(float partialTicks) {
      return this.prevFaceCameraProgress + (this.faceCameraProgress - this.prevFaceCameraProgress) * partialTicks;
   }

   public boolean isEffectiveAi() {
      return super.isEffectiveAi() && this.getAnimation() != ANIMATION_EMERGE && this.hasEmerged();
   }

   private Vec3 calculateLaserHit(Vec3 target) {
      Vec3 eyes = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      HitResult hitResult = this.level().clip(new ClipContext(eyes, target, Block.COLLIDER, Fluid.NONE, this));
      return hitResult.getLocation();
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).getType() != Type.MISS;
   }

   public void travel(Vec3 vec3) {
      if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
         if (this.isInWater()) {
            this.moveRelative(0.02F, vec3);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.800000011920929));
         } else if (this.isInLava()) {
            this.moveRelative(0.02F, vec3);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
         } else {
            this.moveRelative(this.getSpeed(), vec3);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9100000262260437));
         }
      }

      this.calculateEntityAnimation(false);
   }

   public boolean isInvulnerableTo(DamageSource dmg) {
      return super.isInvulnerableTo(dmg) || this.getAnimation() == ANIMATION_EMERGE;
   }

   private boolean canUseLaser() {
      return !this.hasEffect(MobEffects.BLINDNESS);
   }

   private class AttackGoal extends Goal {
      private boolean attackDecision = true;
      private int timeSinceLastSuccessfulAttack = 0;
      private int laserCooldown = 0;
      private int laserUseTime = 0;
      private int lasersShot = 0;

      public AttackGoal() {
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         return EntityFarseer.this.getTarget() != null && EntityFarseer.this.getTarget().isAlive();
      }

      public void stop() {
         this.lasersShot = 0;
         this.laserCooldown = 0;
         this.laserUseTime = 0;
         this.attackDecision = EntityFarseer.this.getRandom().nextBoolean();
         EntityFarseer.this.entityData.set(EntityFarseer.LASER_ENTITY_ID, -1);
         this.timeSinceLastSuccessfulAttack = 0;
         EntityFarseer.this.setAngry(false);
      }

      public void tick() {
         super.tick();
         LivingEntity target = EntityFarseer.this.getTarget();
         if (this.laserCooldown > 0) {
            this.laserCooldown--;
         }

         this.timeSinceLastSuccessfulAttack++;
         if (this.timeSinceLastSuccessfulAttack > 100) {
            this.timeSinceLastSuccessfulAttack = 0;
            this.attackDecision = !this.attackDecision;
         }

         if (target != null) {
            double dist = EntityFarseer.this.distanceTo(target);
            boolean canLaserHit = this.willLaserHit(target);
            if (this.laserCooldown == 0 && this.attackDecision && canLaserHit && dist > 2.0) {
               EntityFarseer.this.setAngry(true);
               EntityFarseer.this.entityData.set(EntityFarseer.LASER_ENTITY_ID, target.getId());
               if (this.laserUseTime == 0) {
                  EntityFarseer.this.playSound(AMSoundRegistry.FARSEER_BEAM.get(), EntityFarseer.this.getSoundVolume(), EntityFarseer.this.getVoicePitch());
               }

               this.laserUseTime++;
               if (this.laserUseTime > 10) {
                  this.laserUseTime = 0;
                  if (canLaserHit) {
                     float healthTenth = target.getMaxHealth() * 0.1F;
                     if (AMCompat.hurt(
                           target, AMDamageTypes.causeFarseerDamage(EntityFarseer.this), EntityFarseer.this.random.nextInt(2) + Math.max(6.0F, healthTenth)
                        )
                        && !target.isAlive()) {
                        AlexsMobs.sendMSGToAll(new MessageSendVisualFlagFromServer(target.getId(), 87));
                     }

                     this.timeSinceLastSuccessfulAttack = 0;
                  }

                  if (this.lasersShot++ > 5) {
                     this.lasersShot = 0;
                     this.laserCooldown = 80 + EntityFarseer.this.random.nextInt(40);
                     EntityFarseer.this.entityData.set(EntityFarseer.LASER_ENTITY_ID, -1);
                     this.attackDecision = EntityFarseer.this.getRandom().nextBoolean();
                  }
               }

               EntityFarseer.this.entityData.set(EntityFarseer.LASER_ATTACK_LVL, this.laserUseTime);
               EntityFarseer.this.lookAt(target, 180.0F, 180.0F);
               if (dist < 17.0 && canLaserHit) {
                  EntityFarseer.this.getNavigation().stop();
               } else {
                  EntityFarseer.this.getNavigation().moveTo(target, 1.0);
               }

               EntityFarseer.this.entityData.set(EntityFarseer.MELEEING, false);
            } else {
               if (!canLaserHit && dist > 10.0) {
                  EntityFarseer.this.setAngry(false);
               }

               if (EntityFarseer.this.hasLaser()) {
                  EntityFarseer.this.entityData.set(EntityFarseer.LASER_ENTITY_ID, -1);
               }

               EntityFarseer.this.entityData.set(EntityFarseer.MELEEING, dist < 4.0);
               if (dist < 4.0) {
                  this.timeSinceLastSuccessfulAttack = 0;
               } else {
                  EntityFarseer.this.getNavigation().moveTo(target, 1.0);
                  EntityFarseer.this.moveControl.setWantedPosition(target.getX(), target.getEyeY(), target.getZ(), 1.0);
               }
            }
         }
      }

      private boolean willLaserHit(LivingEntity target) {
         Vec3 vec = EntityFarseer.this.calculateLaserHit(target.getEyePosition());
         return vec.distanceTo(target.getEyePosition()) < 1.0 && EntityFarseer.this.canUseLaser();
      }
   }

   class MoveController extends MoveControl {
      private final Mob parentEntity = EntityFarseer.this;

      public MoveController() {
         super(EntityFarseer.this);
      }

      public void tick() {
         float angle = 0.017453292F * (this.parentEntity.yBodyRot + 90.0F);
         float radius = (float)Math.sin(this.parentEntity.tickCount * 0.2F) * 2.0F;
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraY = radius * -Math.cos(angle - 1.5707963267948966);
         double extraZ = radius * Mth.cos(angle);
         Vec3 strafPlus = new Vec3(extraX, extraY, extraZ);
         if (this.operation == Operation.MOVE_TO) {
            Vec3 vector3d = new Vec3(this.wantedX - this.parentEntity.getX(), this.wantedY - this.parentEntity.getY(), this.wantedZ - this.parentEntity.getZ());
            double d0 = vector3d.length();
            double width = this.parentEntity.getBoundingBox().getSize();
            Vec3 shimmy = Vec3.ZERO;
            LivingEntity attackTarget = this.parentEntity.getTarget();
            if (attackTarget != null && this.parentEntity.horizontalCollision) {
               shimmy = new Vec3(0.0, 0.005, 0.0);
            }

            Vec3 vector3d1 = vector3d.scale(this.speedModifier * 0.05 / d0);
            this.parentEntity
               .setDeltaMovement(this.parentEntity.getDeltaMovement().add(vector3d1.add(strafPlus.scale(0.003 * Math.min(d0, 100.0)).add(shimmy))));
            if (d0 >= width) {
               this.parentEntity.setYRot(-((float)Mth.atan2(vector3d1.x, vector3d1.z)) * 57.295776F);
               if (EntityFarseer.this.hasLaser()) {
                  this.parentEntity.yBodyRot = this.parentEntity.getYRot();
               }
            }
         } else if (this.operation == Operation.WAIT) {
            this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(strafPlus.scale(0.003)));
         }
      }
   }

   private static class RandomFlyGoal extends Goal {
      private final EntityFarseer parentEntity;
      private BlockPos target = null;
      private final float speed = 0.6F;

      public RandomFlyGoal(EntityFarseer mosquito) {
         this.parentEntity = mosquito;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         if (this.parentEntity.getNavigation().isDone() && this.parentEntity.getTarget() == null && this.parentEntity.getRandom().nextInt(4) == 0) {
            this.target = this.getBlockInViewFarseer();
            if (this.target != null) {
               this.parentEntity
                  .getMoveControl()
                  .setWantedPosition(this.target.getX() + 0.5, this.target.getY() + 0.5, this.target.getZ() + 0.5, 0.6000000238418579);
               return true;
            }
         }

         return false;
      }

      public boolean canContinueToUse() {
         return this.target != null && this.parentEntity.getTarget() == null;
      }

      public void stop() {
         this.target = null;
      }

      public void tick() {
         if (this.target != null) {
            this.parentEntity
               .getMoveControl()
               .setWantedPosition(this.target.getX() + 0.5, this.target.getY() + 0.5, this.target.getZ() + 0.5, 0.6000000238418579);
            if (this.parentEntity.distanceToSqr(Vec3.atCenterOf(this.target)) < 4.0 || this.parentEntity.horizontalCollision) {
               this.target = null;
            }
         }
      }

      private BlockPos getFarseerGround(BlockPos in) {
         BlockPos position = new BlockPos(in.getX(), (int)this.parentEntity.getY(), in.getZ());

         while (position.getY() < 256 && !this.parentEntity.level().getFluidState(position).isEmpty()) {
            position = position.above();
         }

         while (position.getY() > 1 && this.parentEntity.level().isEmptyBlock(position)) {
            position = position.below();
         }

         return position;
      }

      public BlockPos getBlockInViewFarseer() {
         float radius = 5 + this.parentEntity.getRandom().nextInt(10);
         float neg = this.parentEntity.getRandom().nextBoolean() ? 1.0F : -1.0F;
         float renderYawOffset = this.parentEntity.getYRot();
         float angle = 0.017453292F * renderYawOffset + 3.15F * (this.parentEntity.getRandom().nextFloat() * neg);
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         BlockPos radialPos = new BlockPos((int)(this.parentEntity.getX() + extraX), (int)this.parentEntity.getY(), (int)(this.parentEntity.getZ() + extraZ));
         BlockPos ground = this.getFarseerGround(radialPos).above(2 + this.parentEntity.random.nextInt(2));
         return !this.parentEntity.isTargetBlocked(Vec3.atCenterOf(ground.above())) ? ground : null;
      }
   }
}
