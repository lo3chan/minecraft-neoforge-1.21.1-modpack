package net.mcreator.borninchaosv.entity;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.procedures.DireHoundLeaderPriGibieliSushchnostiProcedure;
import net.mcreator.borninchaosv.procedures.DireHoundLeadercallProcedure;
import net.mcreator.borninchaosv.procedures.DirehoundleaderspawnProcedure;
import net.mcreator.borninchaosv.procedures.DreadHoundMirProcedure;
import net.mcreator.borninchaosv.procedures.DreadHoundSpProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController.State;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DireHoundLeaderEntity extends Monster implements GeoEntity {
   public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(DireHoundLeaderEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(DireHoundLeaderEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(DireHoundLeaderEntity.class, EntityDataSerializers.STRING);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean swinging;
   private boolean lastloop;
   private long lastSwing;
   public String animationprocedure = "empty";
   String prevAnim = "empty";

   public DireHoundLeaderEntity(EntityType<DireHoundLeaderEntity> type, Level world) {
      super(type, world);
      this.xpReward = 40;
      this.setNoAi(false);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOT, false);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "direhoundleader");
   }

   public void setTexture(String texture) {
      this.entityData.set(TEXTURE, texture);
   }

   public String getTexture() {
      return (String)this.entityData.get(TEXTURE);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, false) {
         protected boolean canPerformAttack(LivingEntity entity) {
            return this.isTimeToAttack() && this.mob.distanceToSqr(entity) < 2.25 && this.mob.getSensing().hasLineOfSight(entity);
         }
      });
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, false, false) {
         public boolean canUse() {
            double x = DireHoundLeaderEntity.this.getX();
            double y = DireHoundLeaderEntity.this.getY();
            double z = DireHoundLeaderEntity.this.getZ();
            Entity entity = DireHoundLeaderEntity.this;
            Level world = DireHoundLeaderEntity.this.level();
            return super.canUse() && DreadHoundMirProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = DireHoundLeaderEntity.this.getX();
            double y = DireHoundLeaderEntity.this.getY();
            double z = DireHoundLeaderEntity.this.getZ();
            Entity entity = DireHoundLeaderEntity.this;
            Level world = DireHoundLeaderEntity.this.level();
            return super.canContinueToUse() && DreadHoundMirProcedure.execute(entity);
         }
      });
      this.targetSelector.addGoal(3, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
      this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.7));
      this.goalSelector.addGoal(5, new FloatGoal(this));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, BabySkeletonEntity.class, false, false));
      this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, BabySkeletonMinionEntity.class, false, false));
      this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, BoneImpEntity.class, false, false));
      this.targetSelector.addGoal(10, new NearestAttackableTargetGoal(this, BoneImpMinionEntity.class, false, false));
      this.targetSelector.addGoal(11, new NearestAttackableTargetGoal(this, BonescallerEntity.class, false, false));
      this.targetSelector.addGoal(12, new NearestAttackableTargetGoal(this, ControlledBabySkeletonEntity.class, false, false));
      this.targetSelector.addGoal(13, new NearestAttackableTargetGoal(this, DecrepitSkeletonEntity.class, false, false));
      this.targetSelector.addGoal(14, new NearestAttackableTargetGoal(this, SkeletonThrasherEntity.class, false, false));
      this.targetSelector.addGoal(15, new NearestAttackableTargetGoal(this, SupremeBonescallerEntity.class, false, false));
      this.targetSelector.addGoal(16, new NearestAttackableTargetGoal(this, SupremeBonescallerStage2Entity.class, false, false));
      this.targetSelector.addGoal(17, new NearestAttackableTargetGoal(this, Skeleton.class, false, false));
      this.targetSelector.addGoal(18, new NearestAttackableTargetGoal(this, WitherSkeleton.class, false, false));
      this.targetSelector.addGoal(19, new NearestAttackableTargetGoal(this, SpiritGuideEntity.class, false, false));
      this.targetSelector.addGoal(20, new NearestAttackableTargetGoal(this, SpiritGuideAssistantEntity.class, false, false));
      this.targetSelector.addGoal(21, new NearestAttackableTargetGoal(this, BonescallerNotDespawnEntity.class, false, false));
      this.targetSelector.addGoal(22, new NearestAttackableTargetGoal(this, SupremeBonescallerNotDespawnEntity.class, false, false));
      this.targetSelector.addGoal(23, new NearestAttackableTargetGoal(this, SkeletonThrasherNotDespawnEntity.class, false, false));
      this.targetSelector.addGoal(24, new NearestAttackableTargetGoal(this, SiameseSkeletonsEntity.class, false, false));
      this.targetSelector.addGoal(25, new NearestAttackableTargetGoal(this, SiameseSkeletonsleftEntity.class, false, false));
      this.targetSelector.addGoal(26, new NearestAttackableTargetGoal(this, SiameseSkeletonsrightEntity.class, false, false));
   }

   public SoundEvent getAmbientSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:hound_ambient"));
   }

   public void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound((SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.polar_bear.step")), 0.15F, 1.0F);
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:hound_hit"));
   }

   public SoundEvent getDeathSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:hound_death"));
   }

   public boolean hurt(DamageSource source, float amount) {
      DireHoundLeadercallProcedure.execute(this.level(), this.getY(), this);
      Entity immediatesourceentity = source.getDirectEntity();
      return super.hurt(source, amount);
   }

   public void die(DamageSource source) {
      super.die(source);
      DireHoundLeaderPriGibieliSushchnostiProcedure.execute(source.getEntity());
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
      SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
      DirehoundleaderspawnProcedure.execute(world, this.getY(), this);
      return retval;
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
      this.refreshDimensions();
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      return super.getDefaultDimensions(pose).scale(1.0F);
   }

   public static void init(RegisterSpawnPlacementsEvent event) {
      event.register(
         (EntityType)BornInChaosV1ModEntities.DIRE_HOUND_LEADER.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (entityType, world, reason, pos, random) -> {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            return DreadHoundSpProcedure.execute(world, x, y, z);
         },
         Operation.REPLACE
      );
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
      net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.34);
      builder = builder.add(Attributes.MAX_HEALTH, 100.0);
      builder = builder.add(Attributes.ARMOR, 0.5);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 10.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 18.0);
      builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
      builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.7);
      return builder.add(Attributes.ATTACK_KNOCKBACK, 1.4);
   }

   private PlayState movementPredicate(AnimationState event) {
      if (!this.animationprocedure.equals("empty")) {
         return PlayState.STOP;
      } else if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F) || !(event.getLimbSwingAmount() < 0.15F)) && !this.isAggressive()) {
         return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
      } else if (this.isDeadOrDying()) {
         return event.setAndContinue(RawAnimation.begin().thenPlay("death"));
      } else if (this.isInWaterOrBubble()) {
         return event.setAndContinue(RawAnimation.begin().thenLoop("swim"));
      } else {
         return this.isAggressive() && event.isMoving()
            ? event.setAndContinue(RawAnimation.begin().thenLoop("aggression"))
            : event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
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
