package net.mcreator.borninchaosv.entity;

import java.util.List;
import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.procedures.BabySpiderAtackCondssionProcedure;
import net.mcreator.borninchaosv.procedures.ControlledBabySkeletonNaNachalnomPoiavlieniiSushchnostiProcedure;
import net.mcreator.borninchaosv.procedures.ControlledBabySkeletonPriObnovlieniiTaktaSushchnostiProcedure;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
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

public class ControlledBabySkeletonEntity extends TamableAnimal implements GeoEntity {
   public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(ControlledBabySkeletonEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(ControlledBabySkeletonEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ControlledBabySkeletonEntity.class, EntityDataSerializers.STRING);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean swinging;
   private boolean lastloop;
   private long lastSwing;
   public String animationprocedure = "empty";
   String prevAnim = "empty";

   public ControlledBabySkeletonEntity(EntityType<ControlledBabySkeletonEntity> type, Level world) {
      super(type, world);
      this.xpReward = 0;
      this.setNoAi(false);
      this.setPersistenceRequired();
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOT, false);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "baby_skeleton_controlled");
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
            }
         );
      this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this) {
         public boolean canUse() {
            double x = ControlledBabySkeletonEntity.this.getX();
            double y = ControlledBabySkeletonEntity.this.getY();
            double z = ControlledBabySkeletonEntity.this.getZ();
            Entity entity = ControlledBabySkeletonEntity.this;
            Level world = ControlledBabySkeletonEntity.this.level();
            return super.canUse() && BabySpiderAtackCondssionProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = ControlledBabySkeletonEntity.this.getX();
            double y = ControlledBabySkeletonEntity.this.getY();
            double z = ControlledBabySkeletonEntity.this.getZ();
            Entity entity = ControlledBabySkeletonEntity.this;
            Level world = ControlledBabySkeletonEntity.this.level();
            return super.canContinueToUse() && BabySpiderAtackCondssionProcedure.execute(entity);
         }
      });
      this.goalSelector.addGoal(3, new OwnerHurtByTargetGoal(this) {
         public boolean canUse() {
            double x = ControlledBabySkeletonEntity.this.getX();
            double y = ControlledBabySkeletonEntity.this.getY();
            double z = ControlledBabySkeletonEntity.this.getZ();
            Entity entity = ControlledBabySkeletonEntity.this;
            Level world = ControlledBabySkeletonEntity.this.level();
            return super.canUse() && BabySpiderAtackCondssionProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = ControlledBabySkeletonEntity.this.getX();
            double y = ControlledBabySkeletonEntity.this.getY();
            double z = ControlledBabySkeletonEntity.this.getZ();
            Entity entity = ControlledBabySkeletonEntity.this;
            Level world = ControlledBabySkeletonEntity.this.level();
            return super.canContinueToUse() && BabySpiderAtackCondssionProcedure.execute(entity);
         }
      });
      this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
      this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, Monster.class, false, false));
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return false;
   }

   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float f) {
      return super.getPassengerAttachmentPoint(entity, dimensions, f).add(0.0, -1.0, 0.0);
   }

   public SoundEvent getAmbientSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.ambient"));
   }

   public void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound((SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.step")), 0.15F, 1.0F);
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.hurt"));
   }

   public SoundEvent getDeathSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.death"));
   }

   public boolean hurt(DamageSource source, float amount) {
      if (source.is(DamageTypes.IN_FIRE)) {
         return false;
      } else if (source.is(DamageTypes.FALL)) {
         return false;
      } else if (source.is(DamageTypes.CACTUS)) {
         return false;
      } else {
         return source.is(DamageTypes.DROWN) ? false : super.hurt(source, amount);
      }
   }

   public boolean fireImmune() {
      return true;
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
      SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
      ControlledBabySkeletonNaNachalnomPoiavlieniiSushchnostiProcedure.execute(this);
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

   public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
      ItemStack itemstack = sourceentity.getItemInHand(hand);
      InteractionResult retval = InteractionResult.sidedSuccess(this.level().isClientSide());
      Item item = itemstack.getItem();
      if (itemstack.getItem() instanceof SpawnEggItem) {
         retval = super.mobInteract(sourceentity, hand);
      } else if (this.level().isClientSide()) {
         retval = (!this.isTame() || !this.isOwnedBy(sourceentity)) && !this.isFood(itemstack)
            ? InteractionResult.PASS
            : InteractionResult.sidedSuccess(this.level().isClientSide());
      } else if (this.isTame()) {
         if (this.isOwnedBy(sourceentity)) {
            if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
               this.usePlayerItem(sourceentity, hand, itemstack);
               FoodProperties foodproperties = itemstack.getFoodProperties(this);
               float nutrition = foodproperties != null ? foodproperties.nutrition() : 1.0F;
               this.heal(nutrition);
               retval = InteractionResult.sidedSuccess(this.level().isClientSide());
            } else if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
               this.usePlayerItem(sourceentity, hand, itemstack);
               this.heal(4.0F);
               retval = InteractionResult.sidedSuccess(this.level().isClientSide());
            } else {
               retval = super.mobInteract(sourceentity, hand);
            }
         }
      } else if (this.isFood(itemstack)) {
         this.usePlayerItem(sourceentity, hand, itemstack);
         if (this.random.nextInt(3) == 0 && !EventHooks.onAnimalTame(this, sourceentity)) {
            this.tame(sourceentity);
            this.level().broadcastEntityEvent(this, (byte)7);
         } else {
            this.level().broadcastEntityEvent(this, (byte)6);
         }

         this.setPersistenceRequired();
         retval = InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         retval = super.mobInteract(sourceentity, hand);
         if (retval == InteractionResult.SUCCESS || retval == InteractionResult.CONSUME) {
            this.setPersistenceRequired();
         }
      }

      return retval;
   }

   public void baseTick() {
      super.baseTick();
      ControlledBabySkeletonPriObnovlieniiTaktaSushchnostiProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
      this.refreshDimensions();
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      return super.getDefaultDimensions(pose).scale(1.0F);
   }

   public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
      ControlledBabySkeletonEntity retval = (ControlledBabySkeletonEntity)((EntityType)BornInChaosV1ModEntities.CONTROLLED_BABY_SKELETON.get())
         .create(serverWorld);
      retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null);
      return retval;
   }

   public boolean isFood(ItemStack stack) {
      return List.of().contains(stack.getItem());
   }

   public void aiStep() {
      super.aiStep();
      this.updateSwingTime();
   }

   public static void init(RegisterSpawnPlacementsEvent event) {
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
      net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
      builder = builder.add(Attributes.MAX_HEALTH, 20.0);
      builder = builder.add(Attributes.ARMOR, 1.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 4.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 20.0);
      builder = builder.add(Attributes.STEP_HEIGHT, 0.8);
      return builder.add(Attributes.ATTACK_KNOCKBACK, 0.1);
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
