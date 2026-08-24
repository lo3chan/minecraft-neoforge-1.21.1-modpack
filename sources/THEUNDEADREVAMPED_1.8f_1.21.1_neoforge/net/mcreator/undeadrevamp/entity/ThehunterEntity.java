package net.mcreator.undeadrevamp.entity;

import javax.annotation.Nullable;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.mcreator.undeadrevamp.procedures.HighgorundedmobsProcedure;
import net.mcreator.undeadrevamp.procedures.HoardingProcedure;
import net.mcreator.undeadrevamp.procedures.HunterfeastProcedure;
import net.mcreator.undeadrevamp.procedures.HunterfleeProcedure;
import net.mcreator.undeadrevamp.procedures.HunthuntinglolProcedure;
import net.mcreator.undeadrevamp.procedures.ThebeartamerDeathTimeIsReachedProcedure;
import net.mcreator.undeadrevamp.procedures.ThehunterEntityDiesProcedure;
import net.mcreator.undeadrevamp.procedures.ThehunterEntityFallsProcedure;
import net.mcreator.undeadrevamp.procedures.ThehunterOnEntityTickUpdateProcedure;
import net.mcreator.undeadrevamp.procedures.ThehunterOnInitialEntitySpawnProcedure;
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
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
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

public class ThehunterEntity extends Monster implements GeoEntity {
   public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(ThehunterEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(ThehunterEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ThehunterEntity.class, EntityDataSerializers.STRING);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean swinging;
   private boolean lastloop;
   private long lastSwing;
   public String animationprocedure = "empty";
   String prevAnim = "empty";

   public ThehunterEntity(EntityType<ThehunterEntity> type, Level world) {
      super(type, world);
      this.xpReward = 5;
      this.setNoAi(false);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOT, false);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "thehunter");
   }

   public void setTexture(String texture) {
      this.entityData.set(TEXTURE, texture);
   }

   public String getTexture() {
      return (String)this.entityData.get(TEXTURE);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this) {
         public boolean canUse() {
            double x = ThehunterEntity.this.getX();
            double y = ThehunterEntity.this.getY();
            double z = ThehunterEntity.this.getZ();
            Entity entity = ThehunterEntity.this;
            Level world = ThehunterEntity.this.level();
            return super.canUse() && HunterfeastProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = ThehunterEntity.this.getX();
            double y = ThehunterEntity.this.getY();
            double z = ThehunterEntity.this.getZ();
            Entity entity = ThehunterEntity.this;
            Level world = ThehunterEntity.this.level();
            return super.canContinueToUse() && HunterfeastProcedure.execute(entity);
         }
      }).setAlertOthers(new Class[0]));
      this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.5F));
      this.goalSelector.addGoal(3, new BreakDoorGoal(this, e -> true));
      this.goalSelector.addGoal(4, new AvoidEntityGoal<LivingEntity>(this, LivingEntity.class, 7.0F, 2.0, 1.0) {
         public boolean canUse() {
            double x = ThehunterEntity.this.getX();
            double y = ThehunterEntity.this.getY();
            double z = ThehunterEntity.this.getZ();
            Entity entity = ThehunterEntity.this;
            Level world = ThehunterEntity.this.level();
            return super.canUse() && HunterfleeProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = ThehunterEntity.this.getX();
            double y = ThehunterEntity.this.getY();
            double z = ThehunterEntity.this.getZ();
            Entity entity = ThehunterEntity.this;
            Level world = ThehunterEntity.this.level();
            return super.canContinueToUse() && HunterfleeProcedure.execute(entity);
         }
      });
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, SlavemanEntity.class, false, false) {
         public boolean canUse() {
            double x = ThehunterEntity.this.getX();
            double y = ThehunterEntity.this.getY();
            double z = ThehunterEntity.this.getZ();
            Entity entity = ThehunterEntity.this;
            Level world = ThehunterEntity.this.level();
            return super.canUse() && HoardingProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = ThehunterEntity.this.getX();
            double y = ThehunterEntity.this.getY();
            double z = ThehunterEntity.this.getZ();
            Entity entity = ThehunterEntity.this;
            Level world = ThehunterEntity.this.level();
            return super.canContinueToUse() && HoardingProcedure.execute(entity);
         }
      });
      this.goalSelector.addGoal(6, new RestrictSunGoal(this));
      this.goalSelector
         .addGoal(
            7,
            new MeleeAttackGoal(this, 1.2, true) {
               protected boolean canPerformAttack(LivingEntity entity) {
                  return this.isTimeToAttack()
                     && this.mob.distanceToSqr(entity) < this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth()
                     && this.mob.getSensing().hasLineOfSight(entity);
               }

               public boolean canUse() {
                  double x = ThehunterEntity.this.getX();
                  double y = ThehunterEntity.this.getY();
                  double z = ThehunterEntity.this.getZ();
                  Entity entity = ThehunterEntity.this;
                  Level world = ThehunterEntity.this.level();
                  return super.canUse() && HunterfeastProcedure.execute(entity);
               }

               public boolean canContinueToUse() {
                  double x = ThehunterEntity.this.getX();
                  double y = ThehunterEntity.this.getY();
                  double z = ThehunterEntity.this.getZ();
                  Entity entity = ThehunterEntity.this;
                  Level world = ThehunterEntity.this.level();
                  return super.canContinueToUse() && HunterfeastProcedure.execute(entity);
               }
            }
         );
      this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, Player.class, false, false));
      this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, Animal.class, false, false) {
         public boolean canUse() {
            double x = ThehunterEntity.this.getX();
            double y = ThehunterEntity.this.getY();
            double z = ThehunterEntity.this.getZ();
            Entity entity = ThehunterEntity.this;
            Level world = ThehunterEntity.this.level();
            return super.canUse() && HunthuntinglolProcedure.execute();
         }

         public boolean canContinueToUse() {
            double x = ThehunterEntity.this.getX();
            double y = ThehunterEntity.this.getY();
            double z = ThehunterEntity.this.getZ();
            Entity entity = ThehunterEntity.this;
            Level world = ThehunterEntity.this.level();
            return super.canContinueToUse() && HunthuntinglolProcedure.execute();
         }
      });
      this.targetSelector.addGoal(10, new NearestAttackableTargetGoal(this, IronGolem.class, false, false));
      this.targetSelector.addGoal(11, new NearestAttackableTargetGoal(this, Villager.class, false, false));
      this.goalSelector.addGoal(12, new FollowMobGoal(this, 1.2, 20.0F, 5.0F));
      this.goalSelector.addGoal(13, new WaterAvoidingRandomStrollGoal(this, 0.8));
      this.goalSelector.addGoal(14, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(15, new RemoveBlockGoal(Blocks.TURTLE_EGG, this, 1.0, 4));
      this.goalSelector.addGoal(16, new RemoveBlockGoal(Blocks.OAK_LEAVES, this, 1.0, 4));
   }

   public SoundEvent getAmbientSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:hunterambt"));
   }

   public void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound((SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:hunterfoots")), 0.15F, 1.0F);
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:hunterhurts"));
   }

   public SoundEvent getDeathSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:hunterdying"));
   }

   public boolean causeFallDamage(float l, float d, DamageSource source) {
      ThehunterEntityFallsProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
      return super.causeFallDamage(l, d, source);
   }

   public boolean hurt(DamageSource source, float amount) {
      return source.is(DamageTypes.FALL) ? false : super.hurt(source, amount);
   }

   public void die(DamageSource source) {
      super.die(source);
      ThehunterEntityDiesProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
      SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
      ThehunterOnInitialEntitySpawnProcedure.execute(this);
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
      ThehunterOnEntityTickUpdateProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
      this.refreshDimensions();
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      return super.getDefaultDimensions(pose).scale(1.15F);
   }

   public static void init(RegisterSpawnPlacementsEvent event) {
      event.register(
         (EntityType)UndeadRevamp2ModEntities.THEHUNTER.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (entityType, world, reason, pos, random) -> {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            return HighgorundedmobsProcedure.execute(world, x, y, z);
         },
         Operation.REPLACE
      );
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
      net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.4);
      builder = builder.add(Attributes.MAX_HEALTH, 16.0);
      builder = builder.add(Attributes.ARMOR, 0.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 5.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 80.0);
      return builder.add(Attributes.STEP_HEIGHT, 0.6);
   }

   private PlayState movementPredicate(AnimationState event) {
      if (this.animationprocedure.equals("empty")) {
         if (event.isMoving() || !(event.getLimbSwingAmount() > -0.15F) || !(event.getLimbSwingAmount() < 0.15F)) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
         } else if (this.isDeadOrDying()) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("die"));
         } else {
            return this.isShiftKeyDown()
               ? event.setAndContinue(RawAnimation.begin().thenLoop("fly"))
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
         return event.setAndContinue(RawAnimation.begin().thenPlay("claw"));
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
      if (this.deathTime == 80) {
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
