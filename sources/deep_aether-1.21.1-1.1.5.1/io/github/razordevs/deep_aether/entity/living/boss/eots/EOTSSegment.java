package io.github.razordevs.deep_aether.entity.living.boss.eots;

import io.github.razordevs.deep_aether.DeepAether;
import io.github.razordevs.deep_aether.DeepAetherConfig;
import io.github.razordevs.deep_aether.datagen.tags.DATags;
import io.github.razordevs.deep_aether.entity.projectile.WindCrystal;
import io.github.razordevs.deep_aether.init.DAEntities;
import io.github.razordevs.deep_aether.init.DAParticles;
import io.github.razordevs.deep_aether.init.DASounds;
import java.util.EnumSet;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EOTSSegment extends FlyingMob implements Enemy {
   private boolean hasContactedControllerOnLoad = false;
   @Nullable
   protected EOTSSegment parent;
   @Nullable
   private UUID parentUUID;
   private int randomYOffset = 0;
   @Nullable
   protected EOTSController controller;
   @Nullable
   private UUID controllerUUID;
   protected boolean segmentDeathAnimation = false;
   protected boolean finishedDeathAnimation = false;
   private boolean shouldMove = true;
   private boolean isAttacking = false;
   private static final EntityDataAccessor<Boolean> DATA_HEAD_ID = SynchedEntityData.defineId(EOTSSegment.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> PARENT_DATA = SynchedEntityData.defineId(EOTSSegment.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<Boolean> DATA_OPEN_MOUTH = SynchedEntityData.defineId(EOTSSegment.class, EntityDataSerializers.BOOLEAN);

   public EOTSSegment(EntityType<? extends EOTSSegment> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.moveControl = new EOTSSegment.EotsSegmentMoveControl(this);
      this.lookControl = new EOTSSegment.EotsLookControl(this);
      this.noPhysics = true;
      AttributeInstance attackDamage = this.getAttribute(Attributes.ATTACK_DAMAGE);
      if (attackDamage != null) {
         double difference = attackDamage.getBaseValue() * (Double)DeepAetherConfig.COMMON.eots_damage_multiplier.get() - attackDamage.getBaseValue();
         attackDamage.addTransientModifier(new AttributeModifier(DeepAether.getResource("attack_damage_multiplier"), difference, Operation.ADD_VALUE));
      }
   }

   protected EOTSSegment(Level level, EOTSSegment parent, int length) {
      this((EntityType<? extends EOTSSegment>)DAEntities.EOTS_SEGMENT.get(), level);
      this.setPos(parent.getOnPos().getCenter());
      level.addFreshEntity(this);
      this.setParent(parent);
      if (length < 22) {
         new EOTSSegment(level, this, length + 1);
      }

      this.hasContactedControllerOnLoad = true;
   }

   public EOTSSegment(Level level, EOTSSegment parent, EOTSController controller) {
      this((EntityType<? extends EOTSSegment>)DAEntities.EOTS_SEGMENT.get(), level);
      this.setPos(parent.getOnPos().getCenter());
      level.addFreshEntity(this);
      this.setParent(parent);
      this.setController(controller);
      if (this.getController() != null) {
         this.getController().segmentUUIDs.add(this.uuid);
      }

      this.hasContactedControllerOnLoad = true;
   }

   public EOTSSegment(Level level, EOTSController controller) {
      this((EntityType<? extends EOTSSegment>)DAEntities.EOTS_SEGMENT.get(), level);
      this.setPos(controller.getOnPos().getCenter().add(0.0, 6.0, 0.0));
      level.addFreshEntity(this);
      this.setController(controller);
      this.hasContactedControllerOnLoad = true;
   }

   @NotNull
   public static Builder createMobAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.011).add(Attributes.FOLLOW_RANGE, 96.0).add(Attributes.ATTACK_DAMAGE, 9.0);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_HEAD_ID, true);
      builder.define(DATA_OPEN_MOUTH, false);
      builder.define(PARENT_DATA, this.getParent() != null && this.getParentUUID() != null ? this.getParentUUID().toString() : this.getStringUUID());
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(
      @NotNull ServerLevelAccessor pLevel, @NotNull DifficultyInstance pDifficulty, @NotNull MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData
   ) {
      new EOTSSegment(this.level(), this, 0);
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new EOTSSegment.EotsAirChargeGoal(this));
      this.goalSelector.addGoal(1, new EOTSSegment.EotsAttackGoal(this));
      this.goalSelector.addGoal(2, new EOTSSegment.RandomFloatAroundGoal(this));
      this.goalSelector.addGoal(3, new EOTSSegment.DeathGoal(this));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Player.class, false));
   }

   public void checkDespawn() {
   }

   public void knockback(double pStrength, double pX, double pZ) {
   }

   public void tick() {
      if (!this.hasContactedControllerOnLoad) {
         if (this.getControllerUUID() == null) {
            this.hasContactedControllerOnLoad = true;
         } else if (this.getController() != null) {
            this.getController().segmentUUIDs.add(this.uuid);
            if (this.isControllingSegment()) {
               this.getController().controllingSegments.add(this);
               this.getController().setInvisible(true);
            }

            this.hasContactedControllerOnLoad = true;
            this.getController().setHasBeenContactedBySegment();
         }
      }

      super.tick();
      if (!this.isControllingSegment()) {
         EOTSSegment parent = this.getParent();
         if (parent != null) {
            float yRot = this.getYRot();
            float yParentRot = parent.getYRot();
            float newYRot;
            if (Math.abs(yRot - yParentRot) < 0.1F) {
               newYRot = yRot;
            } else {
               newYRot = Mth.lerp(0.15F, yRot, yParentRot);
            }

            float xRot = this.getXRot();
            float xParentRot = parent.getXRot();
            float newXRot;
            if (Math.abs(xRot - xParentRot) < 0.1F) {
               newXRot = xRot;
            } else {
               newXRot = Mth.lerp(0.15F, xRot, xParentRot);
            }

            this.setRot(newYRot, newXRot);
            this.setPos(
               parent.position()
                  .subtract(parent.getLookAngle().multiply(1.7999999523162842, 0.0, 1.7999999523162842))
                  .subtract(parent.getLookAngle().reverse().multiply(0.0, 1.7999999523162842, 0.0))
            );
         } else if (this.level() instanceof ServerLevel) {
            this.setControllingSegment(true);
            if (this.getController() != null) {
               this.getController().controllingSegments.add(this);
            }
         }

         if (this.getTarget() != null && this.getTarget().distanceToSqr(this) < 1.0) {
            this.doHurtTarget(this.getTarget());
         }
      }
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return (SoundEvent)DASounds.EOTS_AMBIENT.get();
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)DASounds.EOTS_HURT.get();
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return (SoundEvent)DASounds.EOTS_DEATH.get();
   }

   public boolean canDisableShield() {
      return true;
   }

   protected void blockedByShield(@NotNull LivingEntity pDefender) {
      super.blockedByShield(pDefender);
      if (pDefender instanceof Player player) {
         player.getCooldowns().addCooldown(player.getUseItem().getItem(), 1000);
         player.invulnerableTime = 20;
      }
   }

   public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
      float health = this.getHealth();
      if (pSource.is(DATags.DamageTypes.EOTS_IMMUNE)) {
         return false;
      } else if (this.getController() != null) {
         if (this.getController().segmentUUIDs.size() == 1 && health - pAmount <= 0.0F) {
            this.segmentDeathAnimation = true;
            this.finishedDeathAnimation = false;
            this.getController().hurt(this.createControllerDamageSource(this), this.getController().getHealth() - 0.5F);
            this.setInvulnerable(true);
            return false;
         } else {
            boolean doHurt = super.hurt(pSource, pAmount);
            if (doHurt) {
               this.getController().hurt(this.createControllerDamageSource(pSource.getEntity()), Math.min(pAmount, health));
            }

            return doHurt;
         }
      } else {
         return super.hurt(pSource, pAmount);
      }
   }

   private DamageSource createControllerDamageSource(@Nullable Entity trueSource) {
      return new DamageSource(this.level().damageSources().generic().typeHolder(), this, trueSource);
   }

   public boolean fireImmune() {
      return true;
   }

   public void die(@NotNull DamageSource pDamageSource) {
      if (this.getController() != null) {
         this.getController().segmentUUIDs.remove(this.uuid);
         if (this.isControllingSegment()) {
            this.getController().controllingSegments.remove(this);
         } else {
            super.die(pDamageSource);
         }
      } else {
         super.die(pDamageSource);
      }
   }

   public boolean canCollideWith(@NotNull Entity pEntity) {
      return pEntity.getType() != DAEntities.EOTS_SEGMENT.get() || pEntity.canBeCollidedWith();
   }

   public boolean canAttackType(@NotNull EntityType<?> pType) {
      return true;
   }

   public boolean isControllingSegment() {
      return (Boolean)this.getEntityData().get(DATA_HEAD_ID);
   }

   public void setControllingSegment(boolean head) {
      this.getEntityData().set(DATA_HEAD_ID, head);
   }

   public boolean isMouthOpen() {
      return (Boolean)this.getEntityData().get(DATA_OPEN_MOUTH);
   }

   public void setMouthOpen(boolean head) {
      this.getEntityData().set(DATA_OPEN_MOUTH, head);
   }

   @Nullable
   private UUID getParentUUID() {
      return this.parentUUID;
   }

   @Nullable
   public EOTSSegment getParent() {
      if (this.parent != null && !this.parent.isRemoved()) {
         return this.parent;
      } else if (this.parentUUID == null) {
         return null;
      } else if (this.parent == null && this.level() instanceof ServerLevel) {
         this.parent = (EOTSSegment)((ServerLevel)this.level()).getEntity(this.parentUUID);
         return this.parent;
      } else {
         return null;
      }
   }

   private void setParentUUID(@Nullable UUID uuid) {
      this.parentUUID = uuid;
   }

   public void setParent(@Nullable EOTSSegment parent) {
      this.setControllingSegment(parent == null);
      this.parent = parent;
      if (parent == null) {
         this.setParentUUID(null);
      } else {
         this.setParentUUID(parent.getUUID());
      }
   }

   private void setControllerUUID(@Nullable UUID uuid) {
      this.controllerUUID = uuid;
   }

   @Nullable
   private UUID getControllerUUID() {
      return this.controllerUUID;
   }

   public void setController(@Nullable EOTSController controller) {
      this.controller = controller;
      if (controller == null) {
         this.setControllerUUID(null);
      } else {
         this.setControllerUUID(controller.getUUID());
      }
   }

   @Nullable
   public EOTSController getController() {
      if (this.controller != null && !this.controller.isRemoved()) {
         return this.controller;
      } else if (this.controllerUUID == null) {
         return null;
      } else if (this.controller == null && this.level() instanceof ServerLevel) {
         this.controller = (EOTSController)((ServerLevel)this.level()).getEntity(this.getControllerUUID());
         return this.controller;
      } else {
         return null;
      }
   }

   private int getIdleYPos() {
      if (this.getController() != null) {
         return this.getController().blockPosition().getY() + 21 + this.randomYOffset;
      } else {
         return this.getTarget() != null ? this.getTarget().blockPosition().getY() + 15 + this.randomYOffset : 255;
      }
   }

   private float getGlobalSpeedModifier() {
      EOTSController controller = this.getController();
      return controller != null ? Mth.lerp(Mth.abs(controller.getHealth() / controller.getMaxHealth()), 1.65F, 1.25F) : 1.25F;
   }

   private float getGlobalAttackModifier() {
      EOTSController controller = this.getController();
      if (controller != null) {
         return controller.controllingSegments.size() == 1
            ? Mth.lerp(Mth.abs(controller.getHealth() / controller.getMaxHealth()), 1.65F, 1.25F) * 10.0F
            : Mth.lerp(Mth.abs(controller.getHealth() / controller.getMaxHealth()), 1.65F, 1.25F);
      } else {
         return 1.5F;
      }
   }

   public boolean isAroundIdlePos() {
      return this.getY() > this.getIdleYPos() - 2;
   }

   private void goToIdlePos() {
      float speed = 1.75F;
      if (this.isAroundIdlePos()) {
         speed = 1.0F;
      }

      Entity target;
      if (this.getController() != null) {
         target = this.getController();
      } else if (this.getTarget() != null) {
         target = this.getTarget();
      } else {
         target = this;
      }

      RandomSource random = this.getRandom();
      int x = random.nextInt(9);
      int y;
      if (x < 8) {
         y = random.nextInt(8 - x);
      } else {
         y = 0;
      }

      double d0 = target.getX() + (random.nextFloat() * 2.0F - 1.0F) * x;
      double d2 = target.getZ() + (random.nextFloat() * 2.0F - 1.0F) * y;
      this.getMoveControl().setWantedPosition(d0, this.getIdleYPos(), d2, speed);
   }

   private void goToIdlePosRelativeToDirection() {
      float speed = 2.5F;
      if (this.isAroundIdlePos()) {
         speed = 1.0F;
      }

      Vec3 lookAngle = this.getLookAngle();
      Vec2 vec2 = new Vec2((float)lookAngle.x(), (float)lookAngle.z());
      vec2 = vec2.normalized();
      double d0 = this.getX() + vec2.x * 5.0F + this.getRandom().nextInt(2);
      double d2 = this.getZ() + vec2.y * 5.0F + this.getRandom().nextInt(2);
      this.getMoveControl().setWantedPosition(d0, this.getIdleYPos(), d2, speed);
   }

   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.hasUUID("Controller")) {
         this.setControllerUUID(tag.getUUID("Controller"));
      }

      if (tag.hasUUID("Parent")) {
         this.setParentUUID(tag.getUUID("Parent"));
         this.setControllingSegment(false);
      }
   }

   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      if (this.getControllerUUID() != null) {
         tag.putUUID("Controller", this.getControllerUUID());
      }

      if (this.getParentUUID() != null) {
         tag.putUUID("Parent", this.getParentUUID());
      }
   }

   protected static class DeathGoal extends Goal {
      private final EOTSSegment segment;
      private boolean hasPositionedAboveController = false;
      private double targetY = 0.0;

      public DeathGoal(EOTSSegment segment) {
         this.segment = segment;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return this.segment.segmentDeathAnimation && this.segment.getController() != null && !this.segment.getController().isRemoved();
      }

      public boolean canContinueToUse() {
         if (this.segment.segmentDeathAnimation) {
            MoveControl moveControl = this.segment.getMoveControl();
            if (!this.hasPositionedAboveController) {
               double d0 = moveControl.getWantedX() - this.segment.getX();
               double d1 = (moveControl.getWantedY() - this.segment.getY()) / 10.0;
               double d2 = moveControl.getWantedZ() - this.segment.getZ();
               double d3 = d0 * d0 + d1 * d1 + d2 * d2;
               this.hasPositionedAboveController = d3 < 2.0;
               if (this.hasPositionedAboveController) {
                  if (this.segment.getController() == null) {
                     return false;
                  }

                  this.targetY = this.segment.getController().getY() + 6.0;
                  this.segment.getMoveControl().setWantedPosition(moveControl.getWantedX(), this.targetY - 12.0, moveControl.getWantedZ(), 1.5);
               }

               return true;
            } else {
               return this.segment.getY() > this.targetY;
            }
         } else {
            return false;
         }
      }

      public void stop() {
         this.segment.setInvulnerable(false);
         this.segment.finishedDeathAnimation = true;
         this.segment.setInvisible(true);
         if (this.segment.getController() != null) {
            this.segment.getController().segmentUUIDs.remove(this.segment.getUUID());
            Vec3 pos = new Vec3(this.segment.getX(), this.segment.getController().position().y() + 6.3, this.segment.getZ());
            ((ServerLevel)this.segment.level())
               .sendParticles((SimpleParticleType)DAParticles.EOTS_EXPLOSION.get(), pos.x(), pos.y(), pos.z(), 1, 0.0, 0.0, 0.0, 0.0);
            this.segment.level().playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 2.0F, 1.0F);
            ((ServerLevel)this.segment.level()).sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.x(), pos.y(), pos.z(), 1, 1.0, 0.0, 0.0, 0.0);
         }

         this.segment.hurt(this.segment.level().damageSources().generic(), 20.0F);
      }

      public void start() {
         if (this.segment.getController() != null && !this.segment.getController().isRemoved()) {
            Vec3 pos = this.segment.getController().position().add(0.0, 6.0, 0.0);
            this.segment.getMoveControl().setWantedPosition(pos.x, this.segment.getIdleYPos() + 20.0F, pos.z, 1.399999976158142);
         } else {
            this.segment.setInvulnerable(false);
            this.segment.finishedDeathAnimation = true;
         }
      }
   }

   protected static class EotsAirChargeGoal extends Goal {
      EOTSSegment segment;
      private int attackTimer = 25;
      private int attackDelay = 9;
      private int numberOfAttacks = 0;

      public EotsAirChargeGoal(EOTSSegment segment) {
         this.segment = segment;
      }

      public boolean canUse() {
         if (this.segment.isAttacking || !this.segment.isAroundIdlePos() || !this.segment.isControllingSegment() || this.segment.segmentDeathAnimation) {
            return false;
         } else if (this.attackTimer > 0) {
            this.attackTimer--;
            return false;
         } else {
            this.attackTimer = (int)(this.segment.random.nextInt(25, 75) / this.segment.getGlobalAttackModifier());
            return true;
         }
      }

      public boolean canContinueToUse() {
         if (this.segment.segmentDeathAnimation) {
            return false;
         } else if (this.attackDelay < -2) {
            return false;
         } else {
            LivingEntity livingentity = this.segment.getTarget();
            return livingentity != null && this.segment.canAttack(livingentity, TargetingConditions.DEFAULT);
         }
      }

      public void start() {
         this.segment.isAttacking = true;
         this.attackDelay = 13;
         this.numberOfAttacks = (int)(this.segment.random.nextInt(0, 2) * this.segment.getGlobalSpeedModifier());
         this.segment.shouldMove = false;
         this.segment.setMouthOpen(true);
         super.start();
      }

      public void stop() {
         this.segment.isAttacking = false;
         this.segment.shouldMove = true;
         this.segment.setMouthOpen(false);
         super.stop();
      }

      public void tick() {
         if (this.segment.getTarget() != null) {
            this.lookAt(this.segment.getTarget());
            if (this.attackDelay <= 0) {
               new WindCrystal(
                  this.segment.level(),
                  this.segment,
                  this.segment.getLookAngle().multiply(0.699999988079071, 0.699999988079071, 0.699999988079071).offsetRandom(this.segment.random, 0.3F)
               );
               this.segment
                  .level()
                  .playSound(null, this.segment.getX(), this.segment.getY(), this.segment.getZ(), DASounds.EOTS_SHOOT, SoundSource.HOSTILE, 2.0F, 1.0F);
               if (this.numberOfAttacks > 0) {
                  this.numberOfAttacks--;
                  this.attackDelay = 9;
               } else {
                  this.attackDelay = -3;
               }
            }
         }

         this.attackDelay--;
      }

      private void lookAt(LivingEntity target) {
         double d0 = target.getX() - this.segment.getX();
         double d1 = target.getEyeY() - this.segment.getEyeY();
         double d2 = target.getZ() - this.segment.getZ();
         double d3 = Math.sqrt(d0 * d0 + d2 * d2);
         this.segment.setXRot(Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * 180.0 / 3.1415927410125732))));
         this.segment.setYRot(Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * 180.0 / 3.1415927410125732) - 90.0F));
         this.segment.setYHeadRot(this.segment.getYRot());
         this.segment.xRotO = this.segment.getXRot();
         this.segment.yRotO = this.segment.getYRot();
      }
   }

   protected static class EotsAttackGoal extends Goal {
      EOTSSegment segment;
      private int nextScanTick = 20;
      private boolean hasAttacked = false;
      int maxFollowingTimer = 150;
      Vec3 targetStartPos;
      private EOTSSegment.EotsAttackGoal.AttackType attackType = EOTSSegment.EotsAttackGoal.AttackType.SWEEPING;

      public EotsAttackGoal(EOTSSegment segment) {
         this.segment = segment;
      }

      public boolean canUse() {
         if (this.segment.isAttacking
            || !this.segment.isAroundIdlePos()
            || !this.segment.isControllingSegment()
            || !this.segment.shouldMove
            || this.segment.getTarget() == null
            || this.segment.segmentDeathAnimation) {
            return false;
         } else if (this.nextScanTick > 0) {
            this.nextScanTick--;
            return false;
         } else {
            this.hasAttacked = false;
            this.nextScanTick = (int)(this.segment.random.nextInt(20, 70) / this.segment.getGlobalAttackModifier());
            return true;
         }
      }

      public void start() {
         this.segment.isAttacking = true;
         if (this.segment.getRandom().nextBoolean()) {
            this.attackType = EOTSSegment.EotsAttackGoal.AttackType.SWEEPING;
         } else {
            this.attackType = EOTSSegment.EotsAttackGoal.AttackType.FOLLOWING;
         }

         this.maxFollowingTimer = 150;
         if (this.segment.getTarget() != null) {
            this.targetStartPos = this.segment.getTarget().position();
         }

         this.segment.setMouthOpen(true);
      }

      public void stop() {
         this.segment.isAttacking = false;
         this.segment.setMouthOpen(false);
         this.updateYAxisRandomness();
      }

      private void updateYAxisRandomness() {
         this.segment.randomYOffset = this.segment.getRandom().nextInt(7);
      }

      public boolean canContinueToUse() {
         if (this.segment.segmentDeathAnimation) {
            return false;
         } else if (this.segment.hurtTime > 0) {
            this.segment.goToIdlePosRelativeToDirection();
            return false;
         } else if (!this.hasAttacked && this.maxFollowingTimer > 0) {
            LivingEntity livingentity = this.segment.getTarget();
            return livingentity != null && this.segment.canAttack(livingentity, TargetingConditions.DEFAULT);
         } else {
            return false;
         }
      }

      public void tick() {
         if (this.segment.getTarget() != null && this.segment.isControllingSegment()) {
            if (this.targetStartPos == null) {
               this.hasAttacked = true;
            } else if (this.attackType == EOTSSegment.EotsAttackGoal.AttackType.SWEEPING) {
               Vec3 pos = this.segment.getTarget().position();
               this.segment.moveControl.setWantedPosition(pos.x(), pos.y(), pos.z(), 1.649999976158142);
               if (this.segment.position().y < this.targetStartPos.y || this.segment.position().y < this.segment.getTarget().position().y) {
                  this.hasAttacked = true;
               }
            } else {
               Vec3 pos = this.segment.getTarget().position().add(0.0, 1.0, 0.0);
               this.segment.moveControl.setWantedPosition(pos.x(), pos.y(), pos.z(), 1.5);
               this.maxFollowingTimer--;
            }

            if (this.segment.getBoundingBox().inflate(0.18000000715255737).intersects(this.segment.getTarget().getBoundingBox())) {
               this.segment.doHurtTarget(this.segment.getTarget());
               this.segment.getTarget().setDeltaMovement(this.segment.getLookAngle().multiply(1.5, 1.5, 1.5));
               this.hasAttacked = true;
            }
         }
      }

      private static enum AttackType {
         FOLLOWING,
         SWEEPING;
      }
   }

   protected static class EotsLookControl extends LookControl {
      EOTSSegment segment;

      public EotsLookControl(EOTSSegment segment) {
         super(segment);
         this.segment = segment;
      }

      public void tick() {
         if (this.segment.isAroundIdlePos() && this.segment.isControllingSegment() && !this.segment.segmentDeathAnimation) {
            super.tick();
         }
      }
   }

   protected static class EotsSegmentMoveControl extends MoveControl {
      private final EOTSSegment segment;
      private float baseSpeed = 0.1F;

      public EotsSegmentMoveControl(EOTSSegment segment) {
         super(segment);
         this.segment = segment;
      }

      public void tick() {
         if (this.segment.isControllingSegment()) {
            double d0 = this.segment.moveControl.getWantedX() - this.segment.getX();
            double d1 = this.segment.moveControl.getWantedY() - this.segment.getY();
            double d2 = this.segment.moveControl.getWantedZ() - this.segment.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            if (Math.abs(d3) > 9.999999747378752E-6) {
               double d4 = 1.0 - Math.abs(d1 * 0.699999988079071) / d3;
               d0 *= d4;
               d2 *= d4;
               d3 = Math.sqrt(d0 * d0 + d2 * d2);
               double d5 = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
               float f = this.segment.getYRot();
               float f1 = (float)Mth.atan2(d2, d0);
               float f2 = Mth.wrapDegrees(this.segment.getYRot() + 90.0F);
               float f3 = Mth.wrapDegrees(f1 * 57.3F);
               this.segment.setYRot(Mth.approachDegrees(f2, f3, this.getTurnSpeed()) - 90.0F);
               this.segment.yBodyRot = this.segment.getYRot();
               if (Mth.degreesDifferenceAbs(f, this.segment.getYRot()) < 3.0F) {
                  this.baseSpeed = Mth.approach(this.baseSpeed, 1.8F, 0.005F * (1.8F / this.baseSpeed));
               } else {
                  this.baseSpeed = Mth.approach(this.baseSpeed, 0.2F, 0.025F);
               }

               double waveFrequency = 0.05;
               double waveAmplitude = 0.8;
               double time = this.segment.tickCount;
               EOTSController controller = this.segment.getController();
               if (controller != null) {
                  time += controller.controllingSegments.indexOf(this.segment);
               }

               double verticalWave = Math.sin(time * waveFrequency) * waveAmplitude;
               float f4 = (float)(-(Mth.atan2(-d1 + verticalWave, d3) * 180.0 / 3.1415927410125732));
               this.segment.setXRot(f4);
               float f5 = this.segment.getYRot() + 90.0F;
               double d6 = this.getSpeed() * Mth.cos(f5 * 0.017453292F) * Math.abs(d0 / d5);
               double d7 = this.getSpeed() * Mth.sin(f5 * 0.017453292F) * Math.abs(d2 / d5);
               double d8 = this.getSpeed() * Mth.sin(f4 * 0.017453292F) * Math.abs((d1 + verticalWave) / d5);
               Vec3 vec3 = this.segment.getDeltaMovement();
               this.segment.setDeltaMovement(vec3.add(new Vec3(d6, d8, d7).subtract(vec3).scale(0.2)));
            }
         }
      }

      private float getTurnSpeed() {
         return !this.segment.isAroundIdlePos() && this.segment.isAttacking ? 10.0F : 5.0F;
      }

      private float getSpeed() {
         return this.baseSpeed * (float)this.segment.moveControl.getSpeedModifier() * this.segment.getGlobalSpeedModifier();
      }
   }

   protected static class RandomFloatAroundGoal extends Goal {
      private final EOTSSegment segment;

      public RandomFloatAroundGoal(EOTSSegment segment) {
         this.segment = segment;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         if (this.segment.isControllingSegment() && this.segment.shouldMove && !this.segment.segmentDeathAnimation) {
            MoveControl moveControl = this.segment.getMoveControl();
            if (!moveControl.hasWanted()) {
               return true;
            } else {
               double d0 = moveControl.getWantedX() - this.segment.getX();
               double d1 = moveControl.getWantedY() - this.segment.getY();
               double d2 = moveControl.getWantedZ() - this.segment.getZ();
               double d3 = d0 * d0 + d1 * d1 + d2 * d2;
               return d3 < 5.0 || d3 > 3600.0;
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void start() {
         this.segment.goToIdlePos();
      }
   }
}
