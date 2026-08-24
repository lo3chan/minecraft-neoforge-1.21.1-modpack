package net.mcreator.borninchaosv.entity;

import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.procedures.ScarletpersecutorPriObnovlieniiTaktaSushchnostiProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController.State;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ScarletPersecutorEntity extends Monster implements GeoEntity {
   public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(ScarletPersecutorEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(ScarletPersecutorEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ScarletPersecutorEntity.class, EntityDataSerializers.STRING);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean swinging;
   private boolean lastloop;
   private long lastSwing;
   public String animationprocedure = "empty";
   String prevAnim = "empty";

   public ScarletPersecutorEntity(EntityType<ScarletPersecutorEntity> type, Level world) {
      super(type, world);
      this.xpReward = 9;
      this.setNoAi(false);
      this.setPersistenceRequired();
      this.moveControl = new FlyingMoveControl(this, 10, true);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOT, false);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "scarletpersecutor");
   }

   public void setTexture(String texture) {
      this.entityData.set(TEXTURE, texture);
   }

   public String getTexture() {
      return (String)this.entityData.get(TEXTURE);
   }

   protected PathNavigation createNavigation(Level world) {
      return new FlyingPathNavigation(this, world);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector
         .addGoal(
            1,
            new MeleeAttackGoal(this, 2.0, false) {
               protected boolean canPerformAttack(LivingEntity entity) {
                  return this.isTimeToAttack()
                     && this.mob.distanceToSqr(entity) < this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth()
                     && this.mob.getSensing().hasLineOfSight(entity);
               }
            }
         );
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, false, false));
      this.targetSelector.addGoal(3, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
      this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8, 20) {
         protected Vec3 getPosition() {
            RandomSource random = ScarletPersecutorEntity.this.getRandom();
            double dir_x = ScarletPersecutorEntity.this.getX() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
            double dir_y = ScarletPersecutorEntity.this.getY() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
            double dir_z = ScarletPersecutorEntity.this.getZ() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
            return new Vec3(dir_x, dir_y, dir_z);
         }
      });
      this.goalSelector.addGoal(5, new LeapAtTargetGoal(this, 0.5F));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, IronGolem.class, false, false));
      this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, AbstractGolem.class, false, false));
      this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, PumpkinSpiritEntity.class, false, false));
      this.targetSelector.addGoal(10, new NearestAttackableTargetGoal(this, ControlledBabySkeletonEntity.class, false, false));
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return false;
   }

   protected void dropCustomDeathLoot(ServerLevel serverLevel, DamageSource source, boolean recentlyHitIn) {
      super.dropCustomDeathLoot(serverLevel, source, recentlyHitIn);
      this.spawnAtLocation(new ItemStack((ItemLike)BornInChaosV1ModItems.ETHEREAL_SPIRIT.get()));
   }

   public SoundEvent getAmbientSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:persecutor_idle"));
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:persecutor_hurt"));
   }

   public SoundEvent getDeathSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:persecutor_death"));
   }

   public boolean causeFallDamage(float l, float d, DamageSource source) {
      return false;
   }

   public boolean hurt(DamageSource source, float amount) {
      if (source.is(DamageTypes.FALL)) {
         return false;
      } else if (source.is(DamageTypes.CACTUS)) {
         return false;
      } else if (source.is(DamageTypes.DROWN)) {
         return false;
      } else {
         return !source.is(DamageTypes.WITHER) && !source.is(DamageTypes.WITHER_SKULL) ? super.hurt(source, amount) : false;
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putString("Texture", this.getTexture());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (compound.contains("Texture")) {
         this.setTexture(compound.getString("Texture"));
      }
   }

   public void baseTick() {
      super.baseTick();
      ScarletpersecutorPriObnovlieniiTaktaSushchnostiProcedure.execute(this.getX(), this.getY(), this.getZ(), this);
      this.refreshDimensions();
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      return super.getDefaultDimensions(pose).scale(1.0F);
   }

   public boolean isPushable() {
      return false;
   }

   protected void doPush(Entity entityIn) {
   }

   protected void pushEntities() {
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public void setNoGravity(boolean ignored) {
      super.setNoGravity(true);
   }

   public void aiStep() {
      super.aiStep();
      this.setNoGravity(true);
   }

   public static void init(RegisterSpawnPlacementsEvent event) {
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
      net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.6);
      builder = builder.add(Attributes.MAX_HEALTH, 35.0);
      builder = builder.add(Attributes.ARMOR, 0.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 5.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 150.0);
      builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
      builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.9);
      builder = builder.add(Attributes.ATTACK_KNOCKBACK, 0.5);
      return builder.add(Attributes.FLYING_SPEED, 0.6);
   }

   private PlayState movementPredicate(AnimationState event) {
      if (this.animationprocedure.equals("empty")) {
         if (event.isMoving() || !(event.getLimbSwingAmount() > -0.15F) || !(event.getLimbSwingAmount() < 0.15F)) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
         } else {
            return this.isDeadOrDying()
               ? event.setAndContinue(RawAnimation.begin().thenPlay("death"))
               : event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
         }
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState attackingPredicate(AnimationState event) {
      double d1 = this.getX() - this.xOld;
      double d0 = this.getZ() - this.zOld;
      float velocity = (float)Math.sqrt(d1 * d1 + d0 * d0);
      if (this.getAttackAnim(event.getPartialTick()) > 0.0F && !this.swinging) {
         this.swinging = true;
         this.lastSwing = this.level().getGameTime();
      }

      if (this.swinging && this.lastSwing + 7L <= this.level().getGameTime()) {
         this.swinging = false;
      }

      if (this.swinging && event.getController().getAnimationState() == State.STOPPED) {
         event.getController().forceAnimationReset();
         return event.setAndContinue(RawAnimation.begin().thenPlay("attack"));
      } else {
         return PlayState.CONTINUE;
      }
   }

   private PlayState procedurePredicate(AnimationState event) {
      if (!this.animationprocedure.equals("empty") && event.getController().getAnimationState() == State.STOPPED
         || !this.animationprocedure.equals(this.prevAnim) && !this.animationprocedure.equals("empty")) {
         if (!this.animationprocedure.equals(this.prevAnim)) {
            event.getController().forceAnimationReset();
         }

         event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
         if (event.getController().getAnimationState() == State.STOPPED) {
            this.animationprocedure = "empty";
            event.getController().forceAnimationReset();
         }
      } else if (this.animationprocedure.equals("empty")) {
         this.prevAnim = "empty";
         return PlayState.STOP;
      }

      this.prevAnim = this.animationprocedure;
      return PlayState.CONTINUE;
   }

   protected void tickDeath() {
      this.deathTime++;
      if (this.deathTime == 30) {
         this.remove(RemovalReason.KILLED);
         this.dropExperience(this);
      }
   }

   public String getSyncedAnimation() {
      return (String)this.entityData.get(ANIMATION);
   }

   public void setAnimation(String animation) {
      this.entityData.set(ANIMATION, animation);
   }

   public void registerControllers(ControllerRegistrar data) {
      data.add(new AnimationController(this, "movement", 4, this::movementPredicate));
      data.add(new AnimationController(this, "attacking", 4, this::attackingPredicate));
      data.add(new AnimationController(this, "procedure", 4, this::procedurePredicate));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
