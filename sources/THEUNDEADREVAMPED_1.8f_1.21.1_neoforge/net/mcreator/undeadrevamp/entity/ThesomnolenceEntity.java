package net.mcreator.undeadrevamp.entity;

import java.util.EnumSet;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.mcreator.undeadrevamp.procedures.SomnolencespawnProcedure;
import net.mcreator.undeadrevamp.procedures.SuckerEntityDiesProcedure;
import net.mcreator.undeadrevamp.procedures.ThebeartamerDeathTimeIsReachedProcedure;
import net.mcreator.undeadrevamp.procedures.ThesomnolenceOnEntityTickUpdateProcedure;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
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

public class ThesomnolenceEntity extends Monster implements GeoEntity {
   public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(ThesomnolenceEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(ThesomnolenceEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ThesomnolenceEntity.class, EntityDataSerializers.STRING);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean swinging;
   private boolean lastloop;
   private long lastSwing;
   public String animationprocedure = "empty";
   String prevAnim = "empty";

   public ThesomnolenceEntity(EntityType<ThesomnolenceEntity> type, Level world) {
      super(type, world);
      this.xpReward = 1;
      this.setNoAi(false);
      this.moveControl = new FlyingMoveControl(this, 10, true);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOT, false);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "somnotest");
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
            new Goal() {
               {
                  this.setFlags(EnumSet.of(Flag.MOVE));
               }

               public boolean canUse() {
                  return ThesomnolenceEntity.this.getTarget() != null && !ThesomnolenceEntity.this.getMoveControl().hasWanted();
               }

               public boolean canContinueToUse() {
                  return ThesomnolenceEntity.this.getMoveControl().hasWanted()
                     && ThesomnolenceEntity.this.getTarget() != null
                     && ThesomnolenceEntity.this.getTarget().isAlive();
               }

               public void start() {
                  LivingEntity livingentity = ThesomnolenceEntity.this.getTarget();
                  Vec3 vec3d = livingentity.getEyePosition(1.0F);
                  ThesomnolenceEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.1);
               }

               public void tick() {
                  LivingEntity livingentity = ThesomnolenceEntity.this.getTarget();
                  if (ThesomnolenceEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
                     ThesomnolenceEntity.this.doHurtTarget(livingentity);
                  } else {
                     double d0 = ThesomnolenceEntity.this.distanceToSqr(livingentity);
                     if (d0 < 16.0) {
                        Vec3 vec3d = livingentity.getEyePosition(1.0F);
                        ThesomnolenceEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.1);
                     }
                  }
               }
            }
         );
      this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8, 20) {
         protected Vec3 getPosition() {
            RandomSource random = ThesomnolenceEntity.this.getRandom();
            double dir_x = ThesomnolenceEntity.this.getX() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
            double dir_y = ThesomnolenceEntity.this.getY() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
            double dir_z = ThesomnolenceEntity.this.getZ() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
            return new Vec3(dir_x, dir_y, dir_z);
         }
      });
      this.goalSelector
         .addGoal(
            3,
            new MeleeAttackGoal(this, 1.5, false) {
               protected boolean canPerformAttack(LivingEntity entity) {
                  return this.isTimeToAttack()
                     && this.mob.distanceToSqr(entity) < this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth()
                     && this.mob.getSensing().hasLineOfSight(entity);
               }
            }
         );
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Player.class, false, false));
      this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, IronGolem.class, false, false));
      this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, Villager.class, false, false));
      this.targetSelector.addGoal(8, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
   }

   public SoundEvent getAmbientSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:somnolenceambt"));
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:somnolencehurts"));
   }

   public SoundEvent getDeathSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:somnolencedies"));
   }

   public boolean causeFallDamage(float l, float d, DamageSource source) {
      return false;
   }

   public boolean hurt(DamageSource source, float amount) {
      return source.is(DamageTypes.FALL) ? false : super.hurt(source, amount);
   }

   public void die(DamageSource source) {
      super.die(source);
      SuckerEntityDiesProcedure.execute(this.level(), this);
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
      ThesomnolenceOnEntityTickUpdateProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
      this.refreshDimensions();
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      return super.getDefaultDimensions(pose).scale(0.4F);
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
         (EntityType)UndeadRevamp2ModEntities.THESOMNOLENCE.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (entityType, world, reason, pos, random) -> {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            return SomnolencespawnProcedure.execute(world, x, y, z);
         },
         Operation.REPLACE
      );
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
      net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 1.2);
      builder = builder.add(Attributes.MAX_HEALTH, 16.0);
      builder = builder.add(Attributes.ARMOR, 0.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 4.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 32.0);
      builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
      return builder.add(Attributes.FLYING_SPEED, 1.2);
   }

   private PlayState movementPredicate(AnimationState event) {
      if (this.animationprocedure.equals("empty")) {
         if (this.isDeadOrDying()) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("dies"));
         } else {
            return !this.onGround() ? event.setAndContinue(RawAnimation.begin().thenLoop("idle")) : event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
         }
      } else {
         return PlayState.STOP;
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
      if (this.deathTime == 110) {
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
      data.add(new AnimationController(this, "procedure", 4, this::procedurePredicate));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
