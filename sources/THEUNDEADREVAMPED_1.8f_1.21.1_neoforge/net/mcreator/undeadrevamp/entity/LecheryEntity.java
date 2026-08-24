package net.mcreator.undeadrevamp.entity;

import javax.annotation.Nullable;
import net.mcreator.undeadrevamp.procedures.LecheryOnEntityTickUpdateProcedure;
import net.mcreator.undeadrevamp.procedures.LecheryOnInitialEntitySpawnProcedure;
import net.mcreator.undeadrevamp.procedures.LecherychaseProcedure;
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
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
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

public class LecheryEntity extends Monster implements GeoEntity {
   public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(LecheryEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(LecheryEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(LecheryEntity.class, EntityDataSerializers.STRING);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean swinging;
   private boolean lastloop;
   private long lastSwing;
   public String animationprocedure = "empty";
   String prevAnim = "empty";

   public LecheryEntity(EntityType<LecheryEntity> type, Level world) {
      super(type, world);
      this.xpReward = 4;
      this.setNoAi(false);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOT, false);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "the_lechery");
   }

   public void setTexture(String texture) {
      this.entityData.set(TEXTURE, texture);
   }

   public String getTexture() {
      return (String)this.entityData.get(TEXTURE);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector
         .addGoal(
            1,
            new MeleeAttackGoal(this, 1.2, false) {
               protected boolean canPerformAttack(LivingEntity entity) {
                  return this.isTimeToAttack()
                     && this.mob.distanceToSqr(entity) < this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth()
                     && this.mob.getSensing().hasLineOfSight(entity);
               }

               public boolean canUse() {
                  double x = LecheryEntity.this.getX();
                  double y = LecheryEntity.this.getY();
                  double z = LecheryEntity.this.getZ();
                  Entity entity = LecheryEntity.this;
                  Level world = LecheryEntity.this.level();
                  return super.canUse() && LecherychaseProcedure.execute(entity);
               }

               public boolean canContinueToUse() {
                  double x = LecheryEntity.this.getX();
                  double y = LecheryEntity.this.getY();
                  double z = LecheryEntity.this.getZ();
                  Entity entity = LecheryEntity.this;
                  Level world = LecheryEntity.this.level();
                  return super.canContinueToUse() && LecherychaseProcedure.execute(entity);
               }
            }
         );
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this) {
         public boolean canUse() {
            double x = LecheryEntity.this.getX();
            double y = LecheryEntity.this.getY();
            double z = LecheryEntity.this.getZ();
            Entity entity = LecheryEntity.this;
            Level world = LecheryEntity.this.level();
            return super.canUse() && LecherychaseProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = LecheryEntity.this.getX();
            double y = LecheryEntity.this.getY();
            double z = LecheryEntity.this.getZ();
            Entity entity = LecheryEntity.this;
            Level world = LecheryEntity.this.level();
            return super.canContinueToUse() && LecherychaseProcedure.execute(entity);
         }
      });
      this.goalSelector.addGoal(3, new PanicGoal(this, 1.2) {
         public boolean canUse() {
            double x = LecheryEntity.this.getX();
            double y = LecheryEntity.this.getY();
            double z = LecheryEntity.this.getZ();
            Entity entity = LecheryEntity.this;
            Level world = LecheryEntity.this.level();
            return super.canUse() && LecherychaseProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = LecheryEntity.this.getX();
            double y = LecheryEntity.this.getY();
            double z = LecheryEntity.this.getZ();
            Entity entity = LecheryEntity.this;
            Level world = LecheryEntity.this.level();
            return super.canContinueToUse() && LecherychaseProcedure.execute(entity);
         }
      });
      this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8) {
         public boolean canUse() {
            double x = LecheryEntity.this.getX();
            double y = LecheryEntity.this.getY();
            double z = LecheryEntity.this.getZ();
            Entity entity = LecheryEntity.this;
            Level world = LecheryEntity.this.level();
            return super.canUse() && LecherychaseProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = LecheryEntity.this.getX();
            double y = LecheryEntity.this.getY();
            double z = LecheryEntity.this.getZ();
            Entity entity = LecheryEntity.this;
            Level world = LecheryEntity.this.level();
            return super.canContinueToUse() && LecherychaseProcedure.execute(entity);
         }
      });
      this.goalSelector.addGoal(5, new FloatGoal(this) {
         public boolean canUse() {
            double x = LecheryEntity.this.getX();
            double y = LecheryEntity.this.getY();
            double z = LecheryEntity.this.getZ();
            Entity entity = LecheryEntity.this;
            Level world = LecheryEntity.this.level();
            return super.canUse() && LecherychaseProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = LecheryEntity.this.getX();
            double y = LecheryEntity.this.getY();
            double z = LecheryEntity.this.getZ();
            Entity entity = LecheryEntity.this;
            Level world = LecheryEntity.this.level();
            return super.canContinueToUse() && LecherychaseProcedure.execute(entity);
         }
      });
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this) {
         public boolean canUse() {
            double x = LecheryEntity.this.getX();
            double y = LecheryEntity.this.getY();
            double z = LecheryEntity.this.getZ();
            Entity entity = LecheryEntity.this;
            Level world = LecheryEntity.this.level();
            return super.canUse() && LecherychaseProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = LecheryEntity.this.getX();
            double y = LecheryEntity.this.getY();
            double z = LecheryEntity.this.getZ();
            Entity entity = LecheryEntity.this;
            Level world = LecheryEntity.this.level();
            return super.canContinueToUse() && LecherychaseProcedure.execute(entity);
         }
      });
      this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, Player.class, false, false));
      this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, Villager.class, false, false));
      this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, IronGolem.class, false, false));
   }

   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float f) {
      return super.getPassengerAttachmentPoint(entity, dimensions, f).add(0.0, 0.800000011920929, 0.0);
   }

   public SoundEvent getAmbientSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:lechery_ambt"));
   }

   public void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound((SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:lecherycrawl")), 0.15F, 1.0F);
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:lecheryhurt"));
   }

   public SoundEvent getDeathSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:lecherydies"));
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
      SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
      LecheryOnInitialEntitySpawnProcedure.execute(world, this.getX(), this.getY(), this.getZ(), this);
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
      LecheryOnEntityTickUpdateProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
      this.refreshDimensions();
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      return super.getDefaultDimensions(pose).scale(1.0F);
   }

   public static void init(RegisterSpawnPlacementsEvent event) {
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
      net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.36);
      builder = builder.add(Attributes.MAX_HEALTH, 35.0);
      builder = builder.add(Attributes.ARMOR, 0.4);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 3.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 32.0);
      return builder.add(Attributes.STEP_HEIGHT, 2.0);
   }

   private PlayState movementPredicate(AnimationState event) {
      if (!this.animationprocedure.equals("empty")) {
         return PlayState.STOP;
      } else if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F) || !(event.getLimbSwingAmount() < 0.15F)) && !this.isAggressive()) {
         return event.setAndContinue(RawAnimation.begin().thenLoop("run"));
      } else if (this.isDeadOrDying()) {
         return event.setAndContinue(RawAnimation.begin().thenPlay("died"));
      } else {
         return this.isAggressive() && event.isMoving()
            ? event.setAndContinue(RawAnimation.begin().thenLoop("krun"))
            : event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
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
      if (this.deathTime == 200) {
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
