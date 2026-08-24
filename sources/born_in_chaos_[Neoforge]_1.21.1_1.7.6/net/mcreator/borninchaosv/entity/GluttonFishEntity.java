package net.mcreator.borninchaosv.entity;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.procedures.GluttonFishLandProcedure;
import net.mcreator.borninchaosv.procedures.GluttonFishPriGibieliOtEtoiSushchnostiDrughoiProcedure;
import net.mcreator.borninchaosv.procedures.GluttonFishPriGibieliSushchnostiProcedure;
import net.mcreator.borninchaosv.procedures.GluttonFishPriProcedure;
import net.mcreator.borninchaosv.procedures.GluttonFishfullProcedure;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController.State;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GluttonFishEntity extends Monster implements GeoEntity {
   public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(GluttonFishEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(GluttonFishEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(GluttonFishEntity.class, EntityDataSerializers.STRING);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean swinging;
   private boolean lastloop;
   private long lastSwing;
   public String animationprocedure = "empty";
   String prevAnim = "empty";

   public GluttonFishEntity(EntityType<GluttonFishEntity> type, Level world) {
      super(type, world);
      this.xpReward = 30;
      this.setNoAi(false);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.moveControl = new MoveControl(this) {
         public void tick() {
            if (GluttonFishEntity.this.isInWater()) {
               GluttonFishEntity.this.setDeltaMovement(GluttonFishEntity.this.getDeltaMovement().add(0.0, 0.005, 0.0));
            }

            if (this.operation == Operation.MOVE_TO && !GluttonFishEntity.this.getNavigation().isDone()) {
               double dx = this.wantedX - GluttonFishEntity.this.getX();
               double dy = this.wantedY - GluttonFishEntity.this.getY();
               double dz = this.wantedZ - GluttonFishEntity.this.getZ();
               float f = (float)(Mth.atan2(dz, dx) * 57.29577951308232) - 90.0F;
               float f1 = (float)(this.speedModifier * GluttonFishEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
               GluttonFishEntity.this.setYRot(this.rotlerp(GluttonFishEntity.this.getYRot(), f, 10.0F));
               GluttonFishEntity.this.yBodyRot = GluttonFishEntity.this.getYRot();
               GluttonFishEntity.this.yHeadRot = GluttonFishEntity.this.getYRot();
               if (GluttonFishEntity.this.isInWater()) {
                  GluttonFishEntity.this.setSpeed((float)GluttonFishEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
                  float f2 = -((float)(Mth.atan2(dy, (float)Math.sqrt(dx * dx + dz * dz)) * 57.29577951308232));
                  f2 = Mth.clamp(Mth.wrapDegrees(f2), -85.0F, 85.0F);
                  GluttonFishEntity.this.setXRot(this.rotlerp(GluttonFishEntity.this.getXRot(), f2, 5.0F));
                  float f3 = Mth.cos(GluttonFishEntity.this.getXRot() * 0.017453292F);
                  GluttonFishEntity.this.setZza(f3 * f1);
                  GluttonFishEntity.this.setYya((float)(f1 * dy));
               } else {
                  GluttonFishEntity.this.setSpeed(f1 * 0.05F);
               }
            } else {
               GluttonFishEntity.this.setSpeed(0.0F);
               GluttonFishEntity.this.setYya(0.0F);
               GluttonFishEntity.this.setZza(0.0F);
            }
         }
      };
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOT, false);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "gluttonfish");
   }

   public void setTexture(String texture) {
      this.entityData.set(TEXTURE, texture);
   }

   public String getTexture() {
      return (String)this.entityData.get(TEXTURE);
   }

   protected PathNavigation createNavigation(Level world) {
      return new WaterBoundPathNavigation(this, world);
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
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
      this.goalSelector.addGoal(3, new RandomSwimmingGoal(this, 1.0, 40));
      this.goalSelector.addGoal(4, new TryFindWaterGoal(this));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Player.class, false, false));
      this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, Drowned.class, false, false));
      this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, Cod.class, false, true) {
         public boolean canUse() {
            double x = GluttonFishEntity.this.getX();
            double y = GluttonFishEntity.this.getY();
            double z = GluttonFishEntity.this.getZ();
            Entity entity = GluttonFishEntity.this;
            Level world = GluttonFishEntity.this.level();
            return super.canUse() && GluttonFishfullProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = GluttonFishEntity.this.getX();
            double y = GluttonFishEntity.this.getY();
            double z = GluttonFishEntity.this.getZ();
            Entity entity = GluttonFishEntity.this;
            Level world = GluttonFishEntity.this.level();
            return super.canContinueToUse() && GluttonFishfullProcedure.execute(entity);
         }
      });
      this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, Salmon.class, false, true) {
         public boolean canUse() {
            double x = GluttonFishEntity.this.getX();
            double y = GluttonFishEntity.this.getY();
            double z = GluttonFishEntity.this.getZ();
            Entity entity = GluttonFishEntity.this;
            Level world = GluttonFishEntity.this.level();
            return super.canUse() && GluttonFishfullProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = GluttonFishEntity.this.getX();
            double y = GluttonFishEntity.this.getY();
            double z = GluttonFishEntity.this.getZ();
            Entity entity = GluttonFishEntity.this;
            Level world = GluttonFishEntity.this.level();
            return super.canContinueToUse() && GluttonFishfullProcedure.execute(entity);
         }
      });
      this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, Squid.class, false, true) {
         public boolean canUse() {
            double x = GluttonFishEntity.this.getX();
            double y = GluttonFishEntity.this.getY();
            double z = GluttonFishEntity.this.getZ();
            Entity entity = GluttonFishEntity.this;
            Level world = GluttonFishEntity.this.level();
            return super.canUse() && GluttonFishfullProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = GluttonFishEntity.this.getX();
            double y = GluttonFishEntity.this.getY();
            double z = GluttonFishEntity.this.getZ();
            Entity entity = GluttonFishEntity.this;
            Level world = GluttonFishEntity.this.level();
            return super.canContinueToUse() && GluttonFishfullProcedure.execute(entity);
         }
      });
      this.targetSelector.addGoal(10, new NearestAttackableTargetGoal(this, TropicalFish.class, false, true) {
         public boolean canUse() {
            double x = GluttonFishEntity.this.getX();
            double y = GluttonFishEntity.this.getY();
            double z = GluttonFishEntity.this.getZ();
            Entity entity = GluttonFishEntity.this;
            Level world = GluttonFishEntity.this.level();
            return super.canUse() && GluttonFishfullProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = GluttonFishEntity.this.getX();
            double y = GluttonFishEntity.this.getY();
            double z = GluttonFishEntity.this.getZ();
            Entity entity = GluttonFishEntity.this;
            Level world = GluttonFishEntity.this.level();
            return super.canContinueToUse() && GluttonFishfullProcedure.execute(entity);
         }
      });
      this.targetSelector.addGoal(11, new NearestAttackableTargetGoal(this, GlowSquid.class, false, true) {
         public boolean canUse() {
            double x = GluttonFishEntity.this.getX();
            double y = GluttonFishEntity.this.getY();
            double z = GluttonFishEntity.this.getZ();
            Entity entity = GluttonFishEntity.this;
            Level world = GluttonFishEntity.this.level();
            return super.canUse() && GluttonFishfullProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = GluttonFishEntity.this.getX();
            double y = GluttonFishEntity.this.getY();
            double z = GluttonFishEntity.this.getZ();
            Entity entity = GluttonFishEntity.this;
            Level world = GluttonFishEntity.this.level();
            return super.canContinueToUse() && GluttonFishfullProcedure.execute(entity);
         }
      });
      this.targetSelector.addGoal(12, new NearestAttackableTargetGoal(this, Pufferfish.class, false, true) {
         public boolean canUse() {
            double x = GluttonFishEntity.this.getX();
            double y = GluttonFishEntity.this.getY();
            double z = GluttonFishEntity.this.getZ();
            Entity entity = GluttonFishEntity.this;
            Level world = GluttonFishEntity.this.level();
            return super.canUse() && GluttonFishfullProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = GluttonFishEntity.this.getX();
            double y = GluttonFishEntity.this.getY();
            double z = GluttonFishEntity.this.getZ();
            Entity entity = GluttonFishEntity.this;
            Level world = GluttonFishEntity.this.level();
            return super.canContinueToUse() && GluttonFishfullProcedure.execute(entity);
         }
      });
   }

   public SoundEvent getAmbientSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:glutton_fish_ambient"));
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:glutton_fish_hurt"));
   }

   public SoundEvent getDeathSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:glutton_fish_death"));
   }

   public boolean hurt(DamageSource source, float amount) {
      return source.is(DamageTypes.DROWN) ? false : super.hurt(source, amount);
   }

   public void die(DamageSource source) {
      super.die(source);
      GluttonFishPriGibieliSushchnostiProcedure.execute(source.getEntity());
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

   public void awardKillScore(Entity entity, int score, DamageSource damageSource) {
      super.awardKillScore(entity, score, damageSource);
      GluttonFishPriGibieliOtEtoiSushchnostiDrughoiProcedure.execute(this);
   }

   public void baseTick() {
      super.baseTick();
      GluttonFishLandProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
      this.refreshDimensions();
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      return super.getDefaultDimensions(pose).scale(1.0F);
   }

   public boolean canDrownInFluidType(FluidType type) {
      return false;
   }

   public boolean checkSpawnObstruction(LevelReader world) {
      return world.isUnobstructed(this);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public static void init(RegisterSpawnPlacementsEvent event) {
      event.register(
         (EntityType)BornInChaosV1ModEntities.GLUTTON_FISH.get(),
         SpawnPlacementTypes.IN_WATER,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (entityType, world, reason, pos, random) -> {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            return GluttonFishPriProcedure.execute(world, x, y, z);
         },
         net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation.REPLACE
      );
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
      net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.7);
      builder = builder.add(Attributes.MAX_HEALTH, 90.0);
      builder = builder.add(Attributes.ARMOR, 3.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 12.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 30.0);
      builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
      builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
      builder = builder.add(Attributes.ATTACK_KNOCKBACK, 0.9);
      return builder.add(NeoForgeMod.SWIM_SPEED, 0.7);
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
      if (this.deathTime == 20) {
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
