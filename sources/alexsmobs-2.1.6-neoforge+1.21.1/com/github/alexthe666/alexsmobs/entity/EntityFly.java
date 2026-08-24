package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityFly extends Animal implements FlyingAnimal {
   private int conversionTime = 0;
   private static final EntityDataAccessor<Boolean> NO_DESPAWN = SynchedEntityData.defineId(EntityFly.class, EntityDataSerializers.BOOLEAN);

   protected EntityFly(EntityType<? extends Animal> type, Level worldIn) {
      super(type, worldIn);
      this.moveControl = new FlyingMoveControl(this, 20, true);
      this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
      this.setPathfindingMalus(PathType.COCOA, -1.0F);
      this.setPathfindingMalus(PathType.FENCE, -1.0F);
   }

   protected void playStepSound(BlockPos pos, BlockState blockIn) {
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("NoFlyDespawn", this.isNoDespawn());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setNoDespawn(AMCompat.getBoolean(compound, "NoFlyDespawn"));
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(NO_DESPAWN, false);
   }

   public boolean isNoDespawn() {
      return (Boolean)this.entityData.get(NO_DESPAWN);
   }

   public void setNoDespawn(boolean despawn) {
      this.entityData.set(NO_DESPAWN, despawn);
   }

   public static boolean canFlySpawn(EntityType<EntityFly> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
      return reason == MobSpawnType.SPAWNER
         || pos.getY() > 63
            && random.nextInt(4) == 0
            && worldIn.getRawBrightness(pos, 0) > 8
            && worldIn.getBrightness(LightLayer.BLOCK, pos) == 0
            && worldIn.getBlockState(pos.below()).is(AMTagRegistry.FLY_SPAWNS);
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return !this.requiresCustomPersistence();
   }

   public boolean requiresCustomPersistence() {
      return this.isNoDespawn() || this.hasCustomName() || this.isLeashed() || super.requiresCustomPersistence();
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.flySpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public boolean isInNether() {
      return this.level().dimension() == Level.NETHER && !this.isNoAi();
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.FLY_IDLE.get();
   }

   public int getAmbientSoundInterval() {
      return 30;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.FLY_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.FLY_HURT.get();
   }

   public int getMaxSpawnClusterSize() {
      return 2;
   }

   public boolean isMaxGroupSizeReached(int sizeIn) {
      return false;
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 2.0)
         .add(Attributes.FLYING_SPEED, 0.800000011920929)
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
      return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(2, new TemptGoal(this, 1.25, AMCompat.ingredientOfTags(AMTagRegistry.FLY_BREEDABLES, AMTagRegistry.FLY_FOODSTUFFS), false));
      this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.25));
      this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Spider.class, 6.0F, 1.0, 1.2));
      this.goalSelector.addGoal(4, new EntityFly.AnnoyZombieGoal());
      this.goalSelector.addGoal(5, new EntityFly.WanderGoal());
      this.goalSelector.addGoal(6, new FloatGoal(this));
   }

   protected PathNavigation createNavigation(Level worldIn) {
      FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn) {
         public boolean isStableDestination(BlockPos pos) {
            return !this.level.getBlockState(pos.below()).isAir();
         }
      };
      flyingpathnavigator.setCanOpenDoors(false);
      flyingpathnavigator.setCanFloat(false);
      flyingpathnavigator.getNodeEvaluator().setCanPassDoors(true);
      return flyingpathnavigator;
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      EntityDimensions dimensions = super.getDefaultDimensions(pose);
      return dimensions.withEyeHeight(dimensions.height() * 0.5F);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
      this.fallDistance = 0.0F;
   }

   public void tick() {
      super.tick();
      if (this.isBaby() && this.getEyeHeight() > this.getBbHeight()) {
         this.refreshDimensions();
      }

      if (this.isInLove() && !this.isNoDespawn()) {
         this.setNoDespawn(true);
      }

      if (this.isInNether()) {
         this.setNoDespawn(true);
         this.conversionTime++;
         if (this.conversionTime > 300) {
            EntityCrimsonMosquito mosquito = AMCompat.create(AMEntityRegistry.CRIMSON_MOSQUITO.get(), this.level());
            mosquito.copyPosition(this);
            if (!this.level().isClientSide()) {
               mosquito.finalizeSpawn(
                  (ServerLevelAccessor)this.level(), AMCompat.difficultyAt(this.level(), this.blockPosition()), MobSpawnType.CONVERSION, null
               );
            }

            this.level().addFreshEntity(mosquito);
            mosquito.onSpawnFromFly();
            this.remove(RemovalReason.DISCARDED);
         }
      }
   }

   public InteractionResult mobInteract(Player p_230254_1_, InteractionHand p_230254_2_) {
      ItemStack lvt_3_1_ = p_230254_1_.getItemInHand(p_230254_2_);
      if (lvt_3_1_.is(AMTagRegistry.FLY_FOODSTUFFS)) {
         if (!p_230254_1_.isCreative()) {
            lvt_3_1_.shrink(1);
         }

         this.setNoDespawn(true);
         this.heal(2.0F);
         return InteractionResult.SUCCESS;
      } else {
         return super.mobInteract(p_230254_1_, p_230254_2_);
      }
   }

   protected boolean makeFlySound() {
      return true;
   }

   protected void jumpInLiquid(TagKey<Fluid> fluidTag) {
      this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.01, 0.0));
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3 getLeashOffset() {
      return new Vec3(0.0, 0.5F * this.getEyeHeight(), this.getBbWidth() * 0.2F);
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.FLY_BREEDABLES);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
      EntityFly fly = AMCompat.create(AMEntityRegistry.FLY.get(), this.level());
      fly.setNoDespawn(true);
      return fly;
   }

   public boolean isFlying() {
      return true;
   }

   private class AnnoyZombieGoal extends Goal {
      protected final EntityFly.AnnoyZombieGoal.Sorter theNearestAttackableTargetSorter;
      protected final Predicate<? super Entity> targetEntitySelector;
      protected int executionChance = 8;
      protected boolean mustUpdate;
      private Entity targetEntity;
      private int cooldown = 0;

      AnnoyZombieGoal() {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.theNearestAttackableTargetSorter = new EntityFly.AnnoyZombieGoal.Sorter(EntityFly.this);
         this.targetEntitySelector = new Predicate<Entity>() {
            public boolean apply(@Nullable Entity e) {
               return e.isAlive()
                  && e.getType().builtInRegistryHolder().is(AMTagRegistry.FLY_TARGETS)
                  && (!(e instanceof LivingEntity) || ((LivingEntity)e).getHealth() >= 2.0);
            }
         };
      }

      public boolean canUse() {
         if (!EntityFly.this.isPassenger() && !EntityFly.this.isVehicle()) {
            if (!this.mustUpdate) {
               long worldTime = EntityFly.this.level().getGameTime() % 10L;
               if (EntityFly.this.getNoActionTime() >= 100 && worldTime != 0L) {
                  return false;
               }

               if (EntityFly.this.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0L) {
                  return false;
               }
            }

            List<Entity> list = EntityFly.this.level()
               .getEntitiesOfClass(Entity.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
            if (list.isEmpty()) {
               return false;
            } else {
               Collections.sort(list, this.theNearestAttackableTargetSorter);
               this.targetEntity = list.get(0);
               this.mustUpdate = false;
               return true;
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.targetEntity != null;
      }

      public void stop() {
         this.targetEntity = null;
      }

      public void tick() {
         if (this.cooldown > 0) {
            this.cooldown--;
         }

         if (this.targetEntity != null) {
            if (EntityFly.this.getNavigation().isDone()) {
               int i = EntityFly.this.getRandom().nextInt(3) - 1;
               int k = EntityFly.this.getRandom().nextInt(3) - 1;
               int l = (int)((EntityFly.this.getRandom().nextInt(3) - 1) * Math.ceil(this.targetEntity.getBbHeight()));
               EntityFly.this.getNavigation().moveTo(this.targetEntity.getX() + i, this.targetEntity.getY() + l, this.targetEntity.getZ() + k, 1.0);
            }

            if (EntityFly.this.distanceToSqr(this.targetEntity) < 3.0) {
               if (!(this.targetEntity instanceof LivingEntity) || !(((LivingEntity)this.targetEntity).getHealth() > 2.0)) {
                  this.stop();
               } else if (this.cooldown == 0) {
                  this.targetEntity.hurt(EntityFly.this.damageSources().generic(), 1.0F);
                  this.cooldown = 100;
               }
            }
         }
      }

      protected double getTargetDistance() {
         return 16.0;
      }

      protected AABB getTargetableArea(double targetDistance) {
         Vec3 renderCenter = new Vec3(EntityFly.this.getX() + 0.5, EntityFly.this.getY() + 0.5, EntityFly.this.getZ() + 0.5);
         double renderRadius = 5.0;
         AABB aabb = new AABB(-renderRadius, -renderRadius, -renderRadius, renderRadius, renderRadius, renderRadius);
         return aabb.move(renderCenter);
      }

      public record Sorter(Entity theEntity) implements Comparator<Entity> {
         public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = this.theEntity.distanceToSqr(p_compare_1_);
            double d1 = this.theEntity.distanceToSqr(p_compare_2_);
            return Double.compare(d0, d1);
         }
      }
   }

   class WanderGoal extends Goal {
      WanderGoal() {
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return EntityFly.this.navigation.isDone() && EntityFly.this.random.nextInt(3) == 0;
      }

      public boolean canContinueToUse() {
         return EntityFly.this.navigation.isInProgress();
      }

      public void start() {
         Vec3 vector3d = this.getRandomLocation();
         if (vector3d != null) {
            EntityFly.this.navigation.moveTo(EntityFly.this.navigation.createPath(AMBlockPos.fromVec3(vector3d), 1), 1.0);
         }
      }

      @Nullable
      private Vec3 getRandomLocation() {
         Vec3 vec3 = EntityFly.this.getViewVector(0.0F);
         int i = 8;
         Vec3 vec32 = HoverRandomPos.getPos(EntityFly.this, 8, 7, vec3.x, vec3.z, 1.5707964F, 3, 1);
         return vec32 != null ? vec32 : AirAndWaterRandomPos.getPos(EntityFly.this, 8, 4, -2, vec3.x, vec3.z, 1.5707963705062866);
      }
   }
}
