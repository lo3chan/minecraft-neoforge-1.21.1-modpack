package net.mcreator.undeadrevamp.entity;

import javax.annotation.Nullable;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.mcreator.undeadrevamp.procedures.BlockingatateProcedure;
import net.mcreator.undeadrevamp.procedures.NormalzombiesspawningProcedure;
import net.mcreator.undeadrevamp.procedures.PixkingstateProcedure;
import net.mcreator.undeadrevamp.procedures.ThebeartamerDeathTimeIsReachedProcedure;
import net.mcreator.undeadrevamp.procedures.TheheavyEntityDiesProcedure;
import net.mcreator.undeadrevamp.procedures.TheheavyEntityIsHurtProcedure;
import net.mcreator.undeadrevamp.procedures.TheheavyOnEntityTickUpdateProcedure;
import net.mcreator.undeadrevamp.procedures.TheheavyOnInitialEntitySpawnProcedure;
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
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
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

public class TheheavyEntity extends Monster implements GeoEntity {
   public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(TheheavyEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(TheheavyEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(TheheavyEntity.class, EntityDataSerializers.STRING);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean swinging;
   private boolean lastloop;
   private long lastSwing;
   public String animationprocedure = "empty";
   String prevAnim = "empty";

   public TheheavyEntity(EntityType<TheheavyEntity> type, Level world) {
      super(type, world);
      this.xpReward = 10;
      this.setNoAi(false);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOT, false);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "heavy");
   }

   public void setTexture(String texture) {
      this.entityData.set(TEXTURE, texture);
   }

   public String getTexture() {
      return (String)this.entityData.get(TEXTURE);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.8, false) {
         protected boolean canPerformAttack(LivingEntity entity) {
            return this.isTimeToAttack() && this.mob.distanceToSqr(entity) < 0.0 && this.mob.getSensing().hasLineOfSight(entity);
         }

         public boolean canUse() {
            double x = TheheavyEntity.this.getX();
            double y = TheheavyEntity.this.getY();
            double z = TheheavyEntity.this.getZ();
            Entity entity = TheheavyEntity.this;
            Level world = TheheavyEntity.this.level();
            return super.canUse() && BlockingatateProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = TheheavyEntity.this.getX();
            double y = TheheavyEntity.this.getY();
            double z = TheheavyEntity.this.getZ();
            Entity entity = TheheavyEntity.this;
            Level world = TheheavyEntity.this.level();
            return super.canContinueToUse() && BlockingatateProcedure.execute(entity);
         }
      });
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, false, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, false, false));
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, Villager.class, false, false));
      this.goalSelector.addGoal(5, new AvoidEntityGoal<LivingEntity>(this, LivingEntity.class, 8.0F, 2.0, 1.5) {
         public boolean canUse() {
            double x = TheheavyEntity.this.getX();
            double y = TheheavyEntity.this.getY();
            double z = TheheavyEntity.this.getZ();
            Entity entity = TheheavyEntity.this;
            Level world = TheheavyEntity.this.level();
            return super.canUse() && PixkingstateProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = TheheavyEntity.this.getX();
            double y = TheheavyEntity.this.getY();
            double z = TheheavyEntity.this.getZ();
            Entity entity = TheheavyEntity.this;
            Level world = TheheavyEntity.this.level();
            return super.canContinueToUse() && PixkingstateProcedure.execute(entity);
         }
      });
      this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1.0));
      this.targetSelector.addGoal(7, new HurtByTargetGoal(this, new Class[0]));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(9, new FloatGoal(this));
   }

   public SoundEvent getAmbientSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:heavyambt"));
   }

   public void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound((SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.ravager.step")), 0.15F, 1.0F);
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:heavyhurt"));
   }

   public SoundEvent getDeathSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:heavydies"));
   }

   public boolean hurt(DamageSource source, float amount) {
      TheheavyEntityIsHurtProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this, source.getEntity());
      Entity immediatesourceentity = source.getDirectEntity();
      return !source.is(DamageTypes.EXPLOSION) && !source.is(DamageTypes.PLAYER_EXPLOSION) ? super.hurt(source, amount) : false;
   }

   public boolean ignoreExplosion(Explosion explosion) {
      return true;
   }

   public void die(DamageSource source) {
      super.die(source);
      TheheavyEntityDiesProcedure.execute(source.getEntity());
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
      SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
      TheheavyOnInitialEntitySpawnProcedure.execute(this);
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
      TheheavyOnEntityTickUpdateProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
      this.refreshDimensions();
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      return super.getDefaultDimensions(pose).scale(1.25F);
   }

   public static void init(RegisterSpawnPlacementsEvent event) {
      event.register(
         (EntityType)UndeadRevamp2ModEntities.THEHEAVY.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (entityType, world, reason, pos, random) -> {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            return NormalzombiesspawningProcedure.execute(world, x, y, z);
         },
         Operation.REPLACE
      );
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
      net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.26);
      builder = builder.add(Attributes.MAX_HEALTH, 120.0);
      builder = builder.add(Attributes.ARMOR, 8.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 4.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 32.0);
      builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
      return builder.add(Attributes.KNOCKBACK_RESISTANCE, 10.0);
   }

   private PlayState movementPredicate(AnimationState event) {
      if (!this.animationprocedure.equals("empty")) {
         return PlayState.STOP;
      } else if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F) || !(event.getLimbSwingAmount() < 0.15F)) && !this.isAggressive()) {
         return event.setAndContinue(RawAnimation.begin().thenLoop("nwalk"));
      } else if (this.isDeadOrDying()) {
         return event.setAndContinue(RawAnimation.begin().thenPlay("death"));
      } else {
         return this.isAggressive() && event.isMoving()
            ? event.setAndContinue(RawAnimation.begin().thenLoop("run"))
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
      if (this.deathTime == 100) {
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
