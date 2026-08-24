package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoDismount;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoMountPlayer;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EntityEnderiophage extends Animal implements Enemy, FlyingAnimal {
   private static final EntityDataAccessor<Float> PHAGE_PITCH = SynchedEntityData.defineId(EntityEnderiophage.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityEnderiophage.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> MISSING_EYE = SynchedEntityData.defineId(EntityEnderiophage.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> PHAGE_SCALE = SynchedEntityData.defineId(EntityEnderiophage.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityEnderiophage.class, EntityDataSerializers.INT);
   private static final Predicate<LivingEntity> ENDERGRADE_OR_INFECTED = entity -> entity instanceof EntityEndergrade
      || entity.hasEffect(AMCompat.effect(AMEffectRegistry.ENDER_FLU.get()));
   public float prevPhagePitch;
   public float tentacleAngle;
   public float lastTentacleAngle;
   public float phageRotation;
   public float prevFlyProgress;
   public float flyProgress;
   public int passengerIndex = 0;
   public float prevEnderiophageScale = 1.0F;
   private float rotationVelocity;
   private int slowDownTicks = 0;
   private float randomMotionSpeed;
   private boolean isLandNavigator;
   private int timeFlying = 0;
   private int fleeAfterStealTime = 0;
   private int attachTime = 0;
   private int dismountCooldown = 0;
   private int squishCooldown = 0;
   private PathfinderMob angryEnderman = null;

   public boolean isFood(ItemStack stack) {
      return stack.is(Items.WHEAT);
   }

   protected EntityEnderiophage(EntityType type, Level world) {
      super(type, world);
      this.rotationVelocity = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
      this.switchNavigator(false);
      this.xpReward = 5;
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.FOLLOW_RANGE, 16.0)
         .add(Attributes.MOVEMENT_SPEED, 0.15000000596046448)
         .add(Attributes.ATTACK_DAMAGE, 2.0);
   }

   public static boolean canEnderiophageSpawn(
      EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return true;
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.enderiophageSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   private void doInitialPosing(LevelAccessor world) {
      BlockPos down = this.getPhageGround(this.blockPosition());
      this.setPos(down.getX() + 0.5F, down.getY() + 1, down.getZ() + 0.5F);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      if (reason == MobSpawnType.NATURAL) {
         this.doInitialPosing(worldIn);
      }

      this.setSkinForDimension();
      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   public int getMaxSpawnClusterSize() {
      return 2;
   }

   public float getPhageScale() {
      return (Float)this.entityData.get(PHAGE_SCALE);
   }

   public void setPhageScale(float scale) {
      this.entityData.set(PHAGE_SCALE, scale);
   }

   public int getVariant() {
      return (Integer)this.entityData.get(VARIANT);
   }

   public void setVariant(int variant) {
      this.entityData.set(VARIANT, variant);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new EntityEnderiophage.FlyTowardsTarget(this));
      this.goalSelector.addGoal(2, new EntityEnderiophage.AIWalkIdle());
      this.targetSelector.addGoal(1, new EntityAINearestTarget3D(this, EnderMan.class, 15, true, true, null) {
         public boolean canUse() {
            return EntityEnderiophage.this.isMissingEye() && super.canUse();
         }

         public boolean canContinueToUse() {
            return EntityEnderiophage.this.isMissingEye() && super.canContinueToUse();
         }
      });
      this.targetSelector.addGoal(1, new EntityAINearestTarget3D(this, LivingEntity.class, 15, true, true, ENDERGRADE_OR_INFECTED) {
         public boolean canUse() {
            return !EntityEnderiophage.this.isMissingEye() && EntityEnderiophage.this.fleeAfterStealTime == 0 && super.canUse();
         }

         public boolean canContinueToUse() {
            return !EntityEnderiophage.this.isMissingEye() && super.canContinueToUse();
         }
      });
      this.targetSelector.addGoal(3, new HurtByTargetGoal(this, new Class[]{EnderMan.class}));
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigatorWide(this, this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new FlightMoveController(this, 1.0F, false, true);
         this.navigation = new DirectPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(VARIANT, 0);
      builder.define(PHAGE_PITCH, 0.0F);
      builder.define(PHAGE_SCALE, 1.0F);
      builder.define(FLYING, false);
      builder.define(MISSING_EYE, false);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public boolean isInOverworld() {
      return this.level().dimension() == Level.OVERWORLD && !this.isNoAi();
   }

   public boolean isInNether() {
      return this.level().dimension() == Level.NETHER && !this.isNoAi();
   }

   public void setStandardFleeTime() {
      this.fleeAfterStealTime = 20;
   }

   public void rideTick() {
      Entity entity = this.getVehicle();
      if (this.isPassenger() && !entity.isAlive()) {
         this.stopRiding();
      } else {
         this.setDeltaMovement(0.0, 0.0, 0.0);
         this.tick();
         if (this.isPassenger()) {
            this.attachTime++;
            Entity mount = this.getVehicle();
            if (mount instanceof LivingEntity) {
               this.passengerIndex = mount.getPassengers().indexOf(this);
               this.yBodyRot = ((LivingEntity)mount).yBodyRot;
               this.setYRot(((LivingEntity)mount).getYRot());
               this.yHeadRot = ((LivingEntity)mount).yHeadRot;
               this.yRotO = ((LivingEntity)mount).yHeadRot;
               float radius = mount.getBbWidth();
               float angle = 0.017453292F * (((LivingEntity)mount).yBodyRot + this.passengerIndex * 90.0F);
               double extraX = radius * Mth.sin(3.1415927F + angle);
               double extraZ = radius * Mth.cos(angle);
               this.setPos(mount.getX() + extraX, Math.max(mount.getY() + mount.getEyeHeight() * 0.25F, mount.getY()), mount.getZ() + extraZ);
               if (!mount.isAlive() || mount instanceof Player && ((Player)mount).isCreative()) {
                  this.removeVehicle();
               }

               this.setPhagePitch(0.0F);
               if (!this.level().isClientSide() && this.attachTime > 15) {
                  LivingEntity target = (LivingEntity)mount;
                  float dmg = 1.0F;
                  if (target.getHealth() > target.getMaxHealth() * 0.2F) {
                     dmg = 6.0F;
                  }

                  if ((target.getHealth() < 1.5 || AMCompat.hurt(mount, this.damageSources().mobAttack(this), dmg)) && mount instanceof LivingEntity) {
                     this.dismountCooldown = 100;
                     if (mount instanceof EnderMan) {
                        this.setMissingEye(false);
                        this.gameEvent(GameEvent.EAT);
                        this.playSound(SoundEvents.ENDER_EYE_DEATH, this.getSoundVolume(), this.getVoicePitch());
                        this.heal(5.0F);
                        ((EnderMan)mount).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400));
                        this.fleeAfterStealTime = 400;
                        this.setFlying(true);
                        this.angryEnderman = (PathfinderMob)mount;
                     } else if (this.random.nextInt(3) == 0) {
                        if (!this.isMissingEye()) {
                           if (target.getEffect(AMCompat.effect(AMEffectRegistry.ENDER_FLU.get())) == null) {
                              target.addEffect(new MobEffectInstance(AMCompat.effect(AMEffectRegistry.ENDER_FLU.get()), 12000));
                           } else {
                              MobEffectInstance inst = target.getEffect(AMCompat.effect(AMEffectRegistry.ENDER_FLU.get()));
                              int duration = 12000;
                              int level = 0;
                              if (inst != null) {
                                 duration = inst.getDuration();
                                 level = inst.getAmplifier();
                              }

                              target.removeEffect(AMCompat.effect(AMEffectRegistry.ENDER_FLU.get()));
                              target.addEffect(new MobEffectInstance(AMCompat.effect(AMEffectRegistry.ENDER_FLU.get()), duration, Math.min(level + 1, 4)));
                           }

                           this.heal(5.0F);
                           this.gameEvent(AMPlatform.ENTITY_ACTION);
                           this.playSound(SoundEvents.ITEM_BREAK, this.getSoundVolume(), this.getVoicePitch());
                           this.setMissingEye(true);
                        }

                        if (!this.level().isClientSide()) {
                           this.setTarget(null);
                           this.setLastHurtMob(null);
                           this.setLastHurtByMob(null);
                           AMCompat.stopRunningGoals(this.goalSelector);
                           AMCompat.stopRunningGoals(this.targetSelector);
                        }
                     }
                  }

                  if (((LivingEntity)mount).getHealth() <= 0.0F
                     || this.fleeAfterStealTime > 0
                     || this.isMissingEye() && !(mount instanceof EnderMan)
                     || !this.isMissingEye() && mount instanceof EnderMan) {
                     this.removeVehicle();
                     this.setTarget(null);
                     this.dismountCooldown = 100;
                     AlexsMobs.sendMSGToAll(new MessageMosquitoDismount(this.getId(), mount.getId()));
                     this.setFlying(true);
                  }
               }
            }
         }
      }
   }

   public boolean canRiderInteract() {
      return true;
   }

   public void onSpawnFromEffect() {
      this.prevEnderiophageScale = 0.2F;
      this.setPhageScale(0.2F);
   }

   public void setSkinForDimension() {
      if (this.isInNether()) {
         this.setVariant(2);
      } else if (this.isInOverworld()) {
         this.setVariant(1);
      } else {
         this.setVariant(0);
      }
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.ENDERIOPHAGE_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.ENDERIOPHAGE_HURT.get();
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound(AMSoundRegistry.ENDERIOPHAGE_WALK.get(), 0.4F, 1.0F);
   }

   protected float nextStep() {
      return this.moveDist + 0.3F;
   }

   public void tick() {
      super.tick();
      this.prevEnderiophageScale = this.getPhageScale();
      float extraMotionSlow = 1.0F;
      float extraMotionSlowY = 1.0F;
      if (this.slowDownTicks > 0) {
         this.slowDownTicks--;
         extraMotionSlow = 0.33F;
         extraMotionSlowY = 0.1F;
      }

      if (this.dismountCooldown > 0) {
         this.dismountCooldown--;
      }

      if (this.squishCooldown > 0) {
         this.squishCooldown--;
      }

      if (!this.level().isClientSide()) {
         if (!this.isPassenger() && this.attachTime != 0) {
            this.attachTime = 0;
         }

         if (this.fleeAfterStealTime > 0) {
            if (this.angryEnderman != null) {
               Vec3 vec = this.getBlockInViewAway(this.angryEnderman.position(), 10.0F);
               if (this.fleeAfterStealTime < 5) {
                  if (this.angryEnderman instanceof NeutralMob) {
                     ((NeutralMob)this.angryEnderman).stopBeingAngry();
                  }

                  try {
                     AMCompat.stopRunningGoals(this.angryEnderman.goalSelector);
                     AMCompat.stopRunningGoals(this.angryEnderman.targetSelector);
                  } catch (Exception var18) {
                     var18.printStackTrace();
                  }

                  this.angryEnderman = null;
               }

               if (vec != null) {
                  this.setFlying(true);
                  this.getMoveControl().setWantedPosition(vec.x, vec.y, vec.z, 1.2999999523162842);
               }
            }

            this.fleeAfterStealTime--;
         }
      }

      this.yBodyRot = this.getYRot();
      this.yHeadRot = this.getYRot();
      this.setPhagePitch(-90.0F);
      if (this.isAlive() && this.isFlying() && this.randomMotionSpeed > 0.75F && this.getDeltaMovement().lengthSqr() > 0.02 && this.level().isClientSide()) {
         float pitch = -this.getPhagePitch() / 90.0F;
         float radius = this.getBbWidth() * 0.2F * -pitch;
         float angle = 0.017453292F * this.getYRot();
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraY = 0.2F - (1.0F - pitch) * 0.15F;
         double extraZ = radius * Mth.cos(angle);
         double motX = extraX * 8.0 + this.random.nextGaussian() * 0.05000000074505806;
         double motY = -0.10000000149011612;
         double motZ = extraZ + this.random.nextGaussian() * 0.05000000074505806;
         this.level()
            .addParticle((ParticleOptions)AMParticleRegistry.DNA.get(), this.getX() + extraX, this.getY() + extraY, this.getZ() + extraZ, motX, motY, motZ);
      }

      this.prevPhagePitch = this.getPhagePitch();
      this.prevFlyProgress = this.flyProgress;
      if (this.isFlying()) {
         if (this.flyProgress < 5.0F) {
            this.flyProgress++;
         }
      } else if (this.flyProgress > 0.0F) {
         this.flyProgress--;
      }

      this.lastTentacleAngle = this.tentacleAngle;
      this.phageRotation = this.phageRotation + this.rotationVelocity;
      if (this.phageRotation > 6.283185307179586) {
         if (this.level().isClientSide()) {
            this.phageRotation = 6.2831855F;
         } else {
            this.phageRotation = (float)(this.phageRotation - 6.283185307179586);
            if (this.random.nextInt(10) == 0) {
               this.rotationVelocity = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
            }

            this.level().broadcastEntityEvent(this, (byte)19);
         }
      }

      if (this.phageRotation < 3.1415927F) {
         float f = this.phageRotation / 3.1415927F;
         this.tentacleAngle = Mth.sin(f * f * 3.1415927F) * 4.275F;
         if (f > 0.75) {
            if (this.squishCooldown == 0 && this.isFlying()) {
               this.squishCooldown = 20;
               this.playSound(AMSoundRegistry.ENDERIOPHAGE_SQUISH.get(), 3.0F, this.getVoicePitch());
            }

            this.randomMotionSpeed = 1.0F;
         } else {
            this.randomMotionSpeed = 0.01F;
         }
      }

      if (!this.level().isClientSide()) {
         if (this.isFlying() && this.isLandNavigator) {
            this.switchNavigator(false);
         }

         if (!this.isFlying() && !this.isLandNavigator) {
            this.switchNavigator(true);
         }

         if (this.isFlying()) {
            this.setDeltaMovement(
               this.getDeltaMovement().x * this.randomMotionSpeed * extraMotionSlow,
               this.getDeltaMovement().y * this.randomMotionSpeed * extraMotionSlowY,
               this.getDeltaMovement().z * this.randomMotionSpeed * extraMotionSlow
            );
            this.timeFlying++;
            if (this.onGround() && this.timeFlying > 100) {
               this.setFlying(false);
            }
         } else {
            this.timeFlying = 0;
         }

         if (this.isMissingEye() && this.getTarget() != null && !(this.getTarget() instanceof EnderMan)) {
            this.setTarget(null);
         }
      }

      if (!this.onGround() && this.getDeltaMovement().y < 0.0) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
      }

      if (this.isFlying()) {
         float phageDist = -((float)((Math.abs(this.getDeltaMovement().x()) + Math.abs(this.getDeltaMovement().z())) * 6.0));
         this.incrementPhagePitch(phageDist * 1.0F);
         this.setPhagePitch(Mth.clamp(this.getPhagePitch(), -90.0F, 10.0F));
         float plateau = 2.0F;
         if (this.getPhagePitch() > plateau) {
            this.decrementPhagePitch(phageDist * Math.abs(this.getPhagePitch()) / 90.0F);
         }

         if (this.getPhagePitch() < -plateau) {
            this.incrementPhagePitch(phageDist * Math.abs(this.getPhagePitch()) / 90.0F);
         }

         if (this.getPhagePitch() > 2.0F) {
            this.decrementPhagePitch(1.0F);
         } else if (this.getPhagePitch() < -2.0F) {
            this.incrementPhagePitch(1.0F);
         }

         if (this.horizontalCollision) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.20000000298023224, 0.0));
         }
      } else {
         if (this.getPhagePitch() > 0.0F) {
            float decrease = Math.min(2.0F, this.getPhagePitch());
            this.decrementPhagePitch(decrease);
         }

         if (this.getPhagePitch() < 0.0F) {
            float decrease = Math.min(2.0F, -this.getPhagePitch());
            this.incrementPhagePitch(decrease);
         }
      }

      if (this.getPhageScale() < 1.0F) {
         this.setPhageScale(this.getPhageScale() + 0.05F);
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Flying", this.isFlying());
      compound.putBoolean("MissingEye", this.isMissingEye());
      compound.putInt("Variant", this.getVariant());
      compound.putInt("SlowDownTicks", this.slowDownTicks);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFlying(AMCompat.getBoolean(compound, "Flying"));
      this.setMissingEye(AMCompat.getBoolean(compound, "MissingEye"));
      this.setVariant(AMCompat.getInt(compound, "Variant"));
      this.slowDownTicks = AMCompat.getInt(compound, "SlowDownTicks");
   }

   public boolean isMissingEye() {
      return (Boolean)this.entityData.get(MISSING_EYE);
   }

   public void setMissingEye(boolean missingEye) {
      this.entityData.set(MISSING_EYE, missingEye);
   }

   public boolean isFlying() {
      return (Boolean)this.entityData.get(FLYING);
   }

   public void setFlying(boolean flying) {
      this.entityData.set(FLYING, flying);
   }

   public float getPhagePitch() {
      return (Float)this.entityData.get(PHAGE_PITCH);
   }

   public void setPhagePitch(float pitch) {
      this.entityData.set(PHAGE_PITCH, pitch);
   }

   public void incrementPhagePitch(float pitch) {
      this.entityData.set(PHAGE_PITCH, this.getPhagePitch() + pitch);
   }

   public void decrementPhagePitch(float pitch) {
      this.entityData.set(PHAGE_PITCH, this.getPhagePitch() - pitch);
   }

   protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
      return 1.8F;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return null;
   }

   private boolean isOverWaterOrVoid() {
      BlockPos position = this.blockPosition();

      while (position.getY() > -63 && !this.level().getBlockState(position).isSolid()) {
         position = position.below();
      }

      return !this.level().getFluidState(position).isEmpty() || position.getY() < -63;
   }

   public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
      float radius = -9.45F - this.getRandom().nextInt(24) - radiusAdd;
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.x() + extraX), 0, (int)(fleePos.z() + extraZ));
      BlockPos ground = this.getPhageGround(radialPos);
      int distFromGround = (int)this.getY() - ground.getY();
      int flightHeight = 6 + this.getRandom().nextInt(10);
      BlockPos newPos = ground.above(distFromGround <= 8 && this.fleeAfterStealTime <= 0 ? this.getRandom().nextInt(6) + 5 : flightHeight);
      return !this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1.0 ? Vec3.atCenterOf(newPos) : null;
   }

   private BlockPos getPhageGround(BlockPos in) {
      BlockPos position = new BlockPos(in.getX(), (int)this.getY(), in.getZ());

      while (position.getY() > -63 && !this.level().getBlockState(position).isSolid()) {
         position = position.below();
      }

      return position.getY() < -62 ? position.above(120 + this.random.nextInt(5)) : position;
   }

   public Vec3 getBlockGrounding(Vec3 fleePos) {
      float radius = -9.45F - this.getRandom().nextInt(24);
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = AMBlockPos.fromCoords(fleePos.x() + extraX, this.getY(), fleePos.z() + extraZ);
      BlockPos ground = this.getPhageGround(radialPos);
      if (ground.getY() <= -63) {
         return Vec3.upFromBottomCenterOf(ground, 110 + this.random.nextInt(20));
      } else {
         ground = this.blockPosition();

         while (ground.getY() > -63 && !this.level().getBlockState(ground).isSolid()) {
            ground = ground.below();
         }

         return !this.isTargetBlocked(Vec3.atCenterOf(ground.above())) ? Vec3.atCenterOf(ground) : null;
      }
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).getType() != Type.MISS;
   }

   public boolean hurt(DamageSource source, float amount) {
      if (AMCompat.isInvulnerableTo(this, source)) {
         return false;
      } else {
         Entity entity = source.getEntity();
         if (entity instanceof EnderMan) {
            amount = (amount + 1.0F) * 0.35F;
            this.angryEnderman = (EnderMan)entity;
         }

         return super.hurt(source, amount);
      }
   }

   private class AIWalkIdle extends Goal {
      protected final EntityEnderiophage phage;
      protected double x;
      protected double y;
      protected double z;
      private boolean flightTarget = false;

      public AIWalkIdle() {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.phage = EntityEnderiophage.this;
      }

      public boolean canUse() {
         if (this.phage.isVehicle() || this.phage.getTarget() != null && this.phage.getTarget().isAlive() || this.phage.isPassenger()) {
            return false;
         } else if (this.phage.getRandom().nextInt(30) != 0 && !this.phage.isFlying() && this.phage.fleeAfterStealTime == 0) {
            return false;
         } else {
            if (this.phage.onGround()) {
               this.flightTarget = EntityEnderiophage.this.random.nextInt(12) == 0;
            } else {
               this.flightTarget = EntityEnderiophage.this.random.nextInt(5) > 0 && this.phage.timeFlying < 100;
            }

            if (this.phage.fleeAfterStealTime > 0) {
               this.flightTarget = true;
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
            this.phage.getMoveControl().setWantedPosition(this.x, this.y, this.z, EntityEnderiophage.this.fleeAfterStealTime == 0 ? 1.2999999523162842 : 1.0);
         } else {
            this.phage.getNavigation().moveTo(this.x, this.y, this.z, EntityEnderiophage.this.fleeAfterStealTime == 0 ? 1.2999999523162842 : 1.0);
         }

         if (!this.flightTarget && EntityEnderiophage.this.isFlying() && this.phage.onGround()) {
            this.phage.setFlying(false);
         }

         if (EntityEnderiophage.this.isFlying() && this.phage.onGround() && this.phage.timeFlying > 100 && this.phage.fleeAfterStealTime == 0) {
            this.phage.setFlying(false);
         }
      }

      @Nullable
      protected Vec3 getPosition() {
         Vec3 vector3d = this.phage.position();
         if (this.phage.isOverWaterOrVoid()) {
            this.flightTarget = true;
         }

         if (this.flightTarget) {
            return this.phage.timeFlying >= 50 && EntityEnderiophage.this.fleeAfterStealTime <= 0 && !this.phage.isOverWaterOrVoid()
               ? this.phage.getBlockGrounding(vector3d)
               : this.phage.getBlockInViewAway(vector3d, 0.0F);
         } else {
            return LandRandomPos.getPos(this.phage, 10, 7);
         }
      }

      public boolean canContinueToUse() {
         return this.flightTarget
            ? this.phage.isFlying() && this.phage.distanceToSqr(this.x, this.y, this.z) > 2.0
            : !this.phage.getNavigation().isDone() && !this.phage.isVehicle();
      }

      public void start() {
         if (this.flightTarget) {
            this.phage.setFlying(true);
            this.phage.getMoveControl().setWantedPosition(this.x, this.y, this.z, EntityEnderiophage.this.fleeAfterStealTime == 0 ? 1.2999999523162842 : 1.0);
         } else {
            this.phage.getNavigation().moveTo(this.x, this.y, this.z, 1.0);
         }
      }

      public void stop() {
         this.phage.getNavigation().stop();
         super.stop();
      }
   }

   public static class FlyTowardsTarget extends Goal {
      private final EntityEnderiophage parentEntity;

      public FlyTowardsTarget(EntityEnderiophage phage) {
         this.parentEntity = phage;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return !this.parentEntity.isPassenger()
            && this.parentEntity.getTarget() != null
            && !this.isBittenByPhage(this.parentEntity.getTarget())
            && this.parentEntity.fleeAfterStealTime == 0;
      }

      public boolean canContinueToUse() {
         return this.parentEntity.getTarget() != null
            && !this.isBittenByPhage(this.parentEntity.getTarget())
            && !this.parentEntity.horizontalCollision
            && !this.parentEntity.isPassenger()
            && this.parentEntity.isFlying()
            && this.parentEntity.getMoveControl().hasWanted()
            && this.parentEntity.fleeAfterStealTime == 0
            && (this.parentEntity.getTarget() instanceof EnderMan || !this.parentEntity.isMissingEye());
      }

      public boolean isBittenByPhage(Entity entity) {
         int phageCount = 0;

         for (Entity e : entity.getPassengers()) {
            if (e instanceof EntityEnderiophage) {
               phageCount++;
            }
         }

         return phageCount > 3;
      }

      public void stop() {
      }

      public void tick() {
         if (this.parentEntity.getTarget() != null) {
            float width = this.parentEntity.getTarget().getBbWidth() + this.parentEntity.getBbWidth() + 2.0F;
            boolean isWithinReach = this.parentEntity.distanceToSqr(this.parentEntity.getTarget()) < width * width;
            if (!this.parentEntity.isFlying() && !isWithinReach) {
               this.parentEntity
                  .getNavigation()
                  .moveTo(this.parentEntity.getTarget().getX(), this.parentEntity.getTarget().getY(), this.parentEntity.getTarget().getZ(), 1.2);
            } else {
               this.parentEntity
                  .getMoveControl()
                  .setWantedPosition(
                     this.parentEntity.getTarget().getX(),
                     this.parentEntity.getTarget().getY(),
                     this.parentEntity.getTarget().getZ(),
                     isWithinReach ? 1.6 : 1.0
                  );
            }

            if (this.parentEntity.getTarget().getY() > this.parentEntity.getY() + 1.2000000476837158) {
               this.parentEntity.setFlying(true);
            }

            if (this.parentEntity.dismountCooldown == 0
               && this.parentEntity.getBoundingBox().inflate(0.3, 0.3, 0.3).intersects(this.parentEntity.getTarget().getBoundingBox())
               && !this.isBittenByPhage(this.parentEntity.getTarget())) {
               AMCompat.startRiding(this.parentEntity, this.parentEntity.getTarget(), true);
               if (!this.parentEntity.level().isClientSide()) {
                  AlexsMobs.sendMSGToAll(new MessageMosquitoMountPlayer(this.parentEntity.getId(), this.parentEntity.getTarget().getId()));
               }
            }
         }
      }
   }
}
