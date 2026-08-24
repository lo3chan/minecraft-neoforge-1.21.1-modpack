package net.mcreator.undeadrevamp.entity;

import javax.annotation.Nullable;
import net.mcreator.undeadrevamp.procedures.CloggerEntityDiesProcedure;
import net.mcreator.undeadrevamp.procedures.CloggerEntityIsHurtProcedure;
import net.mcreator.undeadrevamp.procedures.CloggerOnEntityTickUpdateProcedure;
import net.mcreator.undeadrevamp.procedures.CloggerOnInitialEntitySpawnProcedure;
import net.mcreator.undeadrevamp.procedures.ClogrushProcedure;
import net.mcreator.undeadrevamp.procedures.NoatkbidProcedure;
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
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Explosion;
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

public class CloggerEntity extends Monster implements GeoEntity {
   public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(CloggerEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(CloggerEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(CloggerEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<Integer> DATA_smashmode = SynchedEntityData.defineId(CloggerEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Integer> DATA_rushmode = SynchedEntityData.defineId(CloggerEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Integer> DATA_honeyman_a = SynchedEntityData.defineId(CloggerEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Integer> DATA_eating = SynchedEntityData.defineId(CloggerEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Integer> DATA_honeyman_b = SynchedEntityData.defineId(CloggerEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Integer> DATA_honeyman_c = SynchedEntityData.defineId(CloggerEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Integer> DATA_activatehitbox = SynchedEntityData.defineId(CloggerEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Integer> DATA_explo = SynchedEntityData.defineId(CloggerEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Integer> DATA_tt = SynchedEntityData.defineId(CloggerEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Integer> DATA_passorsmash = SynchedEntityData.defineId(CloggerEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Integer> DATA_pastat = SynchedEntityData.defineId(CloggerEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Integer> DATA_inrange = SynchedEntityData.defineId(CloggerEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Integer> DATA_phase = SynchedEntityData.defineId(CloggerEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Integer> DATA_wait = SynchedEntityData.defineId(CloggerEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Boolean> DATA_noatk = SynchedEntityData.defineId(CloggerEntity.class, EntityDataSerializers.BOOLEAN);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean swinging;
   private boolean lastloop;
   private long lastSwing;
   public String animationprocedure = "empty";
   String prevAnim = "empty";

   public CloggerEntity(EntityType<CloggerEntity> type, Level world) {
      super(type, world);
      this.xpReward = 12;
      this.setNoAi(false);
      this.setPersistenceRequired();
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOT, false);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "theclogger");
      builder.define(DATA_smashmode, 0);
      builder.define(DATA_rushmode, 1);
      builder.define(DATA_honeyman_a, 0);
      builder.define(DATA_eating, 0);
      builder.define(DATA_honeyman_b, 0);
      builder.define(DATA_honeyman_c, 0);
      builder.define(DATA_activatehitbox, 0);
      builder.define(DATA_explo, 0);
      builder.define(DATA_tt, 0);
      builder.define(DATA_passorsmash, 0);
      builder.define(DATA_pastat, 1);
      builder.define(DATA_inrange, 3);
      builder.define(DATA_phase, 1);
      builder.define(DATA_wait, 0);
      builder.define(DATA_noatk, false);
   }

   public void setTexture(String texture) {
      this.entityData.set(TEXTURE, texture);
   }

   public String getTexture() {
      return (String)this.entityData.get(TEXTURE);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.0, false) {
         protected boolean canPerformAttack(LivingEntity entity) {
            return this.isTimeToAttack() && this.mob.distanceToSqr(entity) < 0.0 && this.mob.getSensing().hasLineOfSight(entity);
         }

         public boolean canUse() {
            double x = CloggerEntity.this.getX();
            double y = CloggerEntity.this.getY();
            double z = CloggerEntity.this.getZ();
            Entity entity = CloggerEntity.this;
            Level world = CloggerEntity.this.level();
            return super.canUse() && NoatkbidProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = CloggerEntity.this.getX();
            double y = CloggerEntity.this.getY();
            double z = CloggerEntity.this.getZ();
            Entity entity = CloggerEntity.this;
            Level world = CloggerEntity.this.level();
            return super.canContinueToUse() && NoatkbidProcedure.execute(entity);
         }
      });
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false) {
         protected boolean canPerformAttack(LivingEntity entity) {
            return this.isTimeToAttack() && this.mob.distanceToSqr(entity) < 42.25 && this.mob.getSensing().hasLineOfSight(entity);
         }

         public boolean canUse() {
            double x = CloggerEntity.this.getX();
            double y = CloggerEntity.this.getY();
            double z = CloggerEntity.this.getZ();
            Entity entity = CloggerEntity.this;
            Level world = CloggerEntity.this.level();
            return super.canUse() && ClogrushProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = CloggerEntity.this.getX();
            double y = CloggerEntity.this.getY();
            double z = CloggerEntity.this.getZ();
            Entity entity = CloggerEntity.this;
            Level world = CloggerEntity.this.level();
            return super.canContinueToUse() && ClogrushProcedure.execute(entity);
         }
      });
      this.targetSelector.addGoal(3, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, Pillager.class, false, false) {
         public boolean canUse() {
            double x = CloggerEntity.this.getX();
            double y = CloggerEntity.this.getY();
            double z = CloggerEntity.this.getZ();
            Entity entity = CloggerEntity.this;
            Level world = CloggerEntity.this.level();
            return super.canUse() && NoatkbidProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = CloggerEntity.this.getX();
            double y = CloggerEntity.this.getY();
            double z = CloggerEntity.this.getZ();
            Entity entity = CloggerEntity.this;
            Level world = CloggerEntity.this.level();
            return super.canContinueToUse() && NoatkbidProcedure.execute(entity);
         }
      });
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Illusioner.class, false, false) {
         public boolean canUse() {
            double x = CloggerEntity.this.getX();
            double y = CloggerEntity.this.getY();
            double z = CloggerEntity.this.getZ();
            Entity entity = CloggerEntity.this;
            Level world = CloggerEntity.this.level();
            return super.canUse() && NoatkbidProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = CloggerEntity.this.getX();
            double y = CloggerEntity.this.getY();
            double z = CloggerEntity.this.getZ();
            Entity entity = CloggerEntity.this;
            Level world = CloggerEntity.this.level();
            return super.canContinueToUse() && NoatkbidProcedure.execute(entity);
         }
      });
      this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, Vindicator.class, false, false) {
         public boolean canUse() {
            double x = CloggerEntity.this.getX();
            double y = CloggerEntity.this.getY();
            double z = CloggerEntity.this.getZ();
            Entity entity = CloggerEntity.this;
            Level world = CloggerEntity.this.level();
            return super.canUse() && NoatkbidProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = CloggerEntity.this.getX();
            double y = CloggerEntity.this.getY();
            double z = CloggerEntity.this.getZ();
            Entity entity = CloggerEntity.this;
            Level world = CloggerEntity.this.level();
            return super.canContinueToUse() && NoatkbidProcedure.execute(entity);
         }
      });
      this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, SpellcasterIllager.class, false, false) {
         public boolean canUse() {
            double x = CloggerEntity.this.getX();
            double y = CloggerEntity.this.getY();
            double z = CloggerEntity.this.getZ();
            Entity entity = CloggerEntity.this;
            Level world = CloggerEntity.this.level();
            return super.canUse() && NoatkbidProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = CloggerEntity.this.getX();
            double y = CloggerEntity.this.getY();
            double z = CloggerEntity.this.getZ();
            Entity entity = CloggerEntity.this;
            Level world = CloggerEntity.this.level();
            return super.canContinueToUse() && NoatkbidProcedure.execute(entity);
         }
      });
      this.targetSelector.addGoal(8, new NearestAttackableTargetGoal(this, Player.class, false, false) {
         public boolean canUse() {
            double x = CloggerEntity.this.getX();
            double y = CloggerEntity.this.getY();
            double z = CloggerEntity.this.getZ();
            Entity entity = CloggerEntity.this;
            Level world = CloggerEntity.this.level();
            return super.canUse() && NoatkbidProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = CloggerEntity.this.getX();
            double y = CloggerEntity.this.getY();
            double z = CloggerEntity.this.getZ();
            Entity entity = CloggerEntity.this;
            Level world = CloggerEntity.this.level();
            return super.canContinueToUse() && NoatkbidProcedure.execute(entity);
         }
      });
      this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, Villager.class, false, false) {
         public boolean canUse() {
            double x = CloggerEntity.this.getX();
            double y = CloggerEntity.this.getY();
            double z = CloggerEntity.this.getZ();
            Entity entity = CloggerEntity.this;
            Level world = CloggerEntity.this.level();
            return super.canUse() && NoatkbidProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = CloggerEntity.this.getX();
            double y = CloggerEntity.this.getY();
            double z = CloggerEntity.this.getZ();
            Entity entity = CloggerEntity.this;
            Level world = CloggerEntity.this.level();
            return super.canContinueToUse() && NoatkbidProcedure.execute(entity);
         }
      });
      this.targetSelector.addGoal(10, new NearestAttackableTargetGoal(this, IronGolem.class, false, false) {
         public boolean canUse() {
            double x = CloggerEntity.this.getX();
            double y = CloggerEntity.this.getY();
            double z = CloggerEntity.this.getZ();
            Entity entity = CloggerEntity.this;
            Level world = CloggerEntity.this.level();
            return super.canUse() && NoatkbidProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = CloggerEntity.this.getX();
            double y = CloggerEntity.this.getY();
            double z = CloggerEntity.this.getZ();
            Entity entity = CloggerEntity.this;
            Level world = CloggerEntity.this.level();
            return super.canContinueToUse() && NoatkbidProcedure.execute(entity);
         }
      });
      this.goalSelector.addGoal(11, new RandomStrollGoal(this, 0.8));
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return false;
   }

   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float f) {
      return super.getPassengerAttachmentPoint(entity, dimensions, f).add(0.0, -0.800000011920929, 0.0);
   }

   public SoundEvent getAmbientSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:cloggerambt"));
   }

   public void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound((SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:impact")), 0.15F, 1.0F);
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:cloggerbleed"));
   }

   public SoundEvent getDeathSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:cloggerdeath"));
   }

   public boolean hurt(DamageSource source, float amount) {
      CloggerEntityIsHurtProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
      Entity immediatesourceentity = source.getDirectEntity();
      if (source.getDirectEntity() instanceof AbstractArrow) {
         return false;
      } else if (source.is(DamageTypes.FALL)) {
         return false;
      } else {
         return !source.is(DamageTypes.EXPLOSION) && !source.is(DamageTypes.PLAYER_EXPLOSION) ? super.hurt(source, amount) : false;
      }
   }

   public boolean ignoreExplosion(Explosion explosion) {
      return true;
   }

   public void die(DamageSource source) {
      super.die(source);
      CloggerEntityDiesProcedure.execute(this, source.getEntity());
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
      SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
      CloggerOnInitialEntitySpawnProcedure.execute(world, this.getX(), this.getY(), this.getZ(), this);
      return retval;
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putString("Texture", this.getTexture());
      compound.putInt("Datasmashmode", (Integer)this.entityData.get(DATA_smashmode));
      compound.putInt("Datarushmode", (Integer)this.entityData.get(DATA_rushmode));
      compound.putInt("Datahoneyman_a", (Integer)this.entityData.get(DATA_honeyman_a));
      compound.putInt("Dataeating", (Integer)this.entityData.get(DATA_eating));
      compound.putInt("Datahoneyman_b", (Integer)this.entityData.get(DATA_honeyman_b));
      compound.putInt("Datahoneyman_c", (Integer)this.entityData.get(DATA_honeyman_c));
      compound.putInt("Dataactivatehitbox", (Integer)this.entityData.get(DATA_activatehitbox));
      compound.putInt("Dataexplo", (Integer)this.entityData.get(DATA_explo));
      compound.putInt("Datatt", (Integer)this.entityData.get(DATA_tt));
      compound.putInt("Datapassorsmash", (Integer)this.entityData.get(DATA_passorsmash));
      compound.putInt("Datapastat", (Integer)this.entityData.get(DATA_pastat));
      compound.putInt("Datainrange", (Integer)this.entityData.get(DATA_inrange));
      compound.putInt("Dataphase", (Integer)this.entityData.get(DATA_phase));
      compound.putInt("Datawait", (Integer)this.entityData.get(DATA_wait));
      compound.putBoolean("Datanoatk", (Boolean)this.entityData.get(DATA_noatk));
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (compound.contains("Texture")) {
         this.setTexture(compound.getString("Texture"));
      }

      if (compound.contains("Datasmashmode")) {
         this.entityData.set(DATA_smashmode, compound.getInt("Datasmashmode"));
      }

      if (compound.contains("Datarushmode")) {
         this.entityData.set(DATA_rushmode, compound.getInt("Datarushmode"));
      }

      if (compound.contains("Datahoneyman_a")) {
         this.entityData.set(DATA_honeyman_a, compound.getInt("Datahoneyman_a"));
      }

      if (compound.contains("Dataeating")) {
         this.entityData.set(DATA_eating, compound.getInt("Dataeating"));
      }

      if (compound.contains("Datahoneyman_b")) {
         this.entityData.set(DATA_honeyman_b, compound.getInt("Datahoneyman_b"));
      }

      if (compound.contains("Datahoneyman_c")) {
         this.entityData.set(DATA_honeyman_c, compound.getInt("Datahoneyman_c"));
      }

      if (compound.contains("Dataactivatehitbox")) {
         this.entityData.set(DATA_activatehitbox, compound.getInt("Dataactivatehitbox"));
      }

      if (compound.contains("Dataexplo")) {
         this.entityData.set(DATA_explo, compound.getInt("Dataexplo"));
      }

      if (compound.contains("Datatt")) {
         this.entityData.set(DATA_tt, compound.getInt("Datatt"));
      }

      if (compound.contains("Datapassorsmash")) {
         this.entityData.set(DATA_passorsmash, compound.getInt("Datapassorsmash"));
      }

      if (compound.contains("Datapastat")) {
         this.entityData.set(DATA_pastat, compound.getInt("Datapastat"));
      }

      if (compound.contains("Datainrange")) {
         this.entityData.set(DATA_inrange, compound.getInt("Datainrange"));
      }

      if (compound.contains("Dataphase")) {
         this.entityData.set(DATA_phase, compound.getInt("Dataphase"));
      }

      if (compound.contains("Datawait")) {
         this.entityData.set(DATA_wait, compound.getInt("Datawait"));
      }

      if (compound.contains("Datanoatk")) {
         this.entityData.set(DATA_noatk, compound.getBoolean("Datanoatk"));
      }
   }

   public void baseTick() {
      super.baseTick();
      CloggerOnEntityTickUpdateProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
      this.refreshDimensions();
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      return super.getDefaultDimensions(pose).scale(1.2F);
   }

   public static void init(RegisterSpawnPlacementsEvent event) {
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
      net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
      builder = builder.add(Attributes.MAX_HEALTH, 210.0);
      builder = builder.add(Attributes.ARMOR, 0.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 10.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 32.0);
      return builder.add(Attributes.STEP_HEIGHT, 5.0);
   }

   private PlayState movementPredicate(AnimationState event) {
      if (!this.animationprocedure.equals("empty")) {
         return PlayState.STOP;
      } else if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F) || !(event.getLimbSwingAmount() < 0.15F)) && !this.isAggressive()) {
         return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
      } else if (this.isDeadOrDying()) {
         return event.setAndContinue(RawAnimation.begin().thenPlay("dies"));
      } else {
         return this.isAggressive() && event.isMoving()
            ? event.setAndContinue(RawAnimation.begin().thenLoop("run"))
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
         return event.setAndContinue(RawAnimation.begin().thenPlay("eat"));
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
      if (this.deathTime == 90) {
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
