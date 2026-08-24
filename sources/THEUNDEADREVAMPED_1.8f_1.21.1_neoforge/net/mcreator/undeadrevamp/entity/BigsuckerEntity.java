package net.mcreator.undeadrevamp.entity;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.mcreator.undeadrevamp.procedures.BigsuckerEntityIsHurtProcedure;
import net.mcreator.undeadrevamp.procedures.BigsuckerNaturalEntitySpawningConditionProcedure;
import net.mcreator.undeadrevamp.procedures.BigsuckerOnEntityTickUpdateProcedure;
import net.mcreator.undeadrevamp.procedures.BigsuckerspawnProcedure;
import net.mcreator.undeadrevamp.procedures.DeadcloggerSolidBoundingBoxConditionProcedure;
import net.mcreator.undeadrevamp.procedures.SuckerEntityDiesProcedure;
import net.mcreator.undeadrevamp.procedures.ThebeartamerDeathTimeIsReachedProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
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

public class BigsuckerEntity extends Monster implements GeoEntity {
   public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(BigsuckerEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(BigsuckerEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(BigsuckerEntity.class, EntityDataSerializers.STRING);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean swinging;
   private boolean lastloop;
   private long lastSwing;
   public String animationprocedure = "empty";
   String prevAnim = "empty";

   public BigsuckerEntity(EntityType<BigsuckerEntity> type, Level world) {
      super(type, world);
      this.xpReward = 6;
      this.setNoAi(false);
      this.moveControl = new FlyingMoveControl(this, 10, true);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOT, false);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "suckerbig");
   }

   public void setTexture(String texture) {
      this.entityData.set(TEXTURE, texture);
   }

   public String getTexture() {
      return (String)this.entityData.get(TEXTURE);
   }

   public boolean canCollideWith(Entity entity) {
      return true;
   }

   public boolean canBeCollidedWith() {
      Level world = this.level();
      double x = this.getX();
      double y = this.getY();
      double z = this.getZ();
      return DeadcloggerSolidBoundingBoxConditionProcedure.execute(this);
   }

   protected PathNavigation createNavigation(Level world) {
      return new FlyingPathNavigation(this, world);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new Goal() {
         {
            this.setFlags(EnumSet.of(Flag.MOVE));
         }

         public boolean canUse() {
            return BigsuckerEntity.this.getTarget() != null && !BigsuckerEntity.this.getMoveControl().hasWanted();
         }

         public boolean canContinueToUse() {
            return BigsuckerEntity.this.getMoveControl().hasWanted() && BigsuckerEntity.this.getTarget() != null && BigsuckerEntity.this.getTarget().isAlive();
         }

         public void start() {
            LivingEntity livingentity = BigsuckerEntity.this.getTarget();
            Vec3 vec3d = livingentity.getEyePosition(1.0F);
            BigsuckerEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.2);
         }

         public void tick() {
            LivingEntity livingentity = BigsuckerEntity.this.getTarget();
            if (BigsuckerEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
               BigsuckerEntity.this.doHurtTarget(livingentity);
            } else {
               double d0 = BigsuckerEntity.this.distanceToSqr(livingentity);
               if (d0 < 16.0) {
                  Vec3 vec3d = livingentity.getEyePosition(1.0F);
                  BigsuckerEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.2);
               }
            }
         }
      });
      this.goalSelector
         .addGoal(
            2,
            new MeleeAttackGoal(this, 10.0, false) {
               protected boolean canPerformAttack(LivingEntity entity) {
                  return this.isTimeToAttack()
                     && this.mob.distanceToSqr(entity) < this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth()
                     && this.mob.getSensing().hasLineOfSight(entity);
               }
            }
         );
      this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8, 20) {
         protected Vec3 getPosition() {
            RandomSource random = BigsuckerEntity.this.getRandom();
            double dir_x = BigsuckerEntity.this.getX() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
            double dir_y = BigsuckerEntity.this.getY() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
            double dir_z = BigsuckerEntity.this.getZ() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
            return new Vec3(dir_x, dir_y, dir_z);
         }
      });
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, IronGolem.class, false, false));
      this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, Villager.class, false, false));
      this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, Player.class, false, false));
      this.targetSelector.addGoal(8, new HurtByTargetGoal(this, new Class[0]));
   }

   public SoundEvent getAmbientSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:suckambt"));
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:suckerhurts"));
   }

   public SoundEvent getDeathSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:suckerdies"));
   }

   public boolean causeFallDamage(float l, float d, DamageSource source) {
      return false;
   }

   public boolean hurt(DamageSource source, float amount) {
      BigsuckerEntityIsHurtProcedure.execute(this);
      Entity immediatesourceentity = source.getDirectEntity();
      return source.is(DamageTypes.FALL) ? false : super.hurt(source, amount);
   }

   public void die(DamageSource source) {
      super.die(source);
      SuckerEntityDiesProcedure.execute(this.level(), this);
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
      SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
      BigsuckerspawnProcedure.execute(world, this.getX(), this.getY(), this.getZ(), this);
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
      BigsuckerOnEntityTickUpdateProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
      this.refreshDimensions();
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      return super.getDefaultDimensions(pose).scale(2.2F);
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
      event.register(
         (EntityType)UndeadRevamp2ModEntities.BIGSUCKER.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (entityType, world, reason, pos, random) -> {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            return BigsuckerNaturalEntitySpawningConditionProcedure.execute(world, x, y, z);
         },
         Operation.REPLACE
      );
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
      net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.5);
      builder = builder.add(Attributes.MAX_HEALTH, 80.0);
      builder = builder.add(Attributes.ARMOR, 0.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 10.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 65.0);
      builder = builder.add(Attributes.STEP_HEIGHT, 1.0);
      builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 100.0);
      builder = builder.add(Attributes.ATTACK_KNOCKBACK, 0.5);
      return builder.add(Attributes.FLYING_SPEED, 0.5);
   }

   private PlayState movementPredicate(AnimationState event) {
      if (this.animationprocedure.equals("empty")) {
         if (this.isDeadOrDying()) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("death"));
         } else {
            return !this.onGround()
               ? event.setAndContinue(RawAnimation.begin().thenLoop("flying"))
               : event.setAndContinue(RawAnimation.begin().thenLoop("flying"));
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
         return event.setAndContinue(RawAnimation.begin().thenPlay("bite"));
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
      if (this.deathTime == 55) {
         this.remove(RemovalReason.KILLED);
         this.dropExperience(this);
         ThebeartamerDeathTimeIsReachedProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
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
