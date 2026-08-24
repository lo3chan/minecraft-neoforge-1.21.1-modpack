package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.BottomFeederAIWander;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.IShearable;

public class EntityAlligatorSnappingTurtle extends Animal implements ISemiAquatic, Shearable, IShearable {
   public static final Predicate<LivingEntity> TARGET_PRED = animal -> !(animal instanceof EntityAlligatorSnappingTurtle)
      && !(animal instanceof ArmorStand)
      && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(animal)
      && animal.isAlive();
   private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntityAlligatorSnappingTurtle.class, EntityDataSerializers.BYTE);
   private static final EntityDataAccessor<Integer> MOSS = SynchedEntityData.defineId(EntityAlligatorSnappingTurtle.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> WAITING = SynchedEntityData.defineId(EntityAlligatorSnappingTurtle.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> ATTACK_TARGET_FLAG = SynchedEntityData.defineId(
      EntityAlligatorSnappingTurtle.class, EntityDataSerializers.BOOLEAN
   );
   private static final EntityDataAccessor<Boolean> LUNGE_FLAG = SynchedEntityData.defineId(EntityAlligatorSnappingTurtle.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> TURTLE_SCALE = SynchedEntityData.defineId(EntityAlligatorSnappingTurtle.class, EntityDataSerializers.FLOAT);
   public float openMouthProgress;
   public float prevOpenMouthProgress;
   public float attackProgress;
   public float prevAttackProgress;
   public int chaseTime = 0;
   private int biteTick = 0;
   private int waitTime = 0;
   private int timeUntilWait = 0;
   private int mossTime = 0;

   protected EntityAlligatorSnappingTurtle(EntityType<? extends Animal> type, Level worldIn) {
      super(type, worldIn);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
      AMCompat.setMaxUpStep(this, 1.0F);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.ALLIGATOR_SNAPPING_TURTLE_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.ALLIGATOR_SNAPPING_TURTLE_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.ALLIGATOR_SNAPPING_TURTLE_HURT.get();
   }

   public static boolean canTurtleSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      boolean spawnBlock = worldIn.getBlockState(pos.below()).is(AMTagRegistry.ALLIGATOR_SNAPPING_TURTLE_SPAWNS);
      return spawnBlock && pos.getY() < worldIn.getSeaLevel() + 4;
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.alligatorSnappingTurtleSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 18.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.7)
         .add(Attributes.ARMOR, 8.0)
         .add(Attributes.FOLLOW_RANGE, 16.0)
         .add(Attributes.ATTACK_DAMAGE, 4.0)
         .add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   public float getScale() {
      return this.isBaby() ? 0.3F : 1.0F;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.3, false));
      this.goalSelector.addGoal(2, new AnimalAIFindWater(this));
      this.goalSelector.addGoal(2, new AnimalAILeaveWater(this));
      this.goalSelector.addGoal(3, new BottomFeederAIWander(this, 1.0, 120, 150, 10));
      this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
         public boolean canContinueToUse() {
            return EntityAlligatorSnappingTurtle.this.chaseTime >= 0 && super.canContinueToUse();
         }
      });
      this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, LivingEntity.class, 2, false, true, TARGET_PRED) {
         @Override
         protected AABB getTargetSearchArea(double targetDistance) {
            return this.mob.getBoundingBox().inflate(0.5, 2.0, 0.5);
         }
      });
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.ALLIGATOR_SNAPPING_TURTLE_BREEDABLES);
   }

   public boolean onClimbable() {
      return this.isBesideClimbableBlock();
   }

   public boolean doHurtTarget(Entity entityIn) {
      return true;
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CLIMBING, (byte)0);
      builder.define(MOSS, 0);
      builder.define(TURTLE_SCALE, 1.0F);
      builder.define(WAITING, false);
      builder.define(ATTACK_TARGET_FLAG, false);
      builder.define(LUNGE_FLAG, false);
   }

   public void tick() {
      super.tick();
      this.prevOpenMouthProgress = this.openMouthProgress;
      this.prevAttackProgress = this.attackProgress;
      boolean attack = (Boolean)this.entityData.get(LUNGE_FLAG);
      boolean open = this.isWaiting() || (Boolean)this.entityData.get(ATTACK_TARGET_FLAG) && !attack;
      if (attack) {
         if (this.attackProgress < 5.0F) {
            this.attackProgress++;
         }
      } else if (this.attackProgress > 0.0F) {
         this.attackProgress--;
      }

      if (open) {
         if (this.openMouthProgress < 5.0F) {
            this.openMouthProgress++;
         }
      } else if (this.openMouthProgress > 0.0F) {
         this.openMouthProgress--;
      }

      if (this.attackProgress == 4.0F
         && this.getTarget() != null
         && this.isAlive()
         && this.hasLineOfSight(this.getTarget())
         && this.distanceTo(this.getTarget()) < 2.3F) {
         float dmg = this.isBaby() ? 1.0F : (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
         this.getTarget().hurt(this.damageSources().mobAttack(this), dmg);
      }

      if (this.attackProgress > 4.0F) {
         this.biteTick = 5;
      }

      if (this.biteTick > 0) {
         this.biteTick--;
      }

      if (this.chaseTime < 0) {
         this.chaseTime++;
      }

      if (!this.level().isClientSide()) {
         this.setBesideClimbableBlock(this.horizontalCollision && this.isInWater());
         if (this.isWaiting()) {
            this.waitTime++;
            this.timeUntilWait = 1500;
            if (this.waitTime > 1500 || this.getTarget() != null) {
               this.setWaiting(false);
            }
         } else {
            this.timeUntilWait--;
            this.waitTime = 0;
         }

         if ((this.getTarget() == null || !this.getTarget().isAlive()) && this.timeUntilWait <= 0 && this.isInWater()) {
            this.setWaiting(true);
         }

         if (this.getTarget() != null && this.biteTick == 0) {
            this.setWaiting(false);
            this.chaseTime++;
            this.entityData.set(ATTACK_TARGET_FLAG, true);
            this.lookAt(this.getTarget(), 360.0F, 40.0F);
            this.yBodyRot = this.getYRot();
            if (this.openMouthProgress > 4.0F && this.hasLineOfSight(this.getTarget()) && this.distanceTo(this.getTarget()) < 2.3F) {
               this.entityData.set(LUNGE_FLAG, true);
            }

            if (this.chaseTime > 40 && this.distanceTo(this.getTarget()) > (this.getTarget() instanceof Player ? 5 : 10)) {
               this.chaseTime = -50;
               this.setTarget(null);
               this.setLastHurtByMob(null);
               this.setLastHurtMob(null);
               this.lastHurtByPlayer = null;
            }
         } else {
            this.entityData.set(ATTACK_TARGET_FLAG, false);
            this.entityData.set(LUNGE_FLAG, false);
         }

         this.mossTime++;
         if (this.isInWater() && this.mossTime > 12000) {
            this.mossTime = 0;
            this.setMoss(Math.min(10, this.getMoss() + 1));
         }
      }
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.chaseTime < 0 ? null : super.getTarget();
   }

   public void setTarget(@Nullable LivingEntity entitylivingbaseIn) {
      if (this.chaseTime >= 0) {
         super.setTarget(entitylivingbaseIn);
      } else {
         super.setTarget(null);
      }
   }

   @Nullable
   public LivingEntity getLastHurtByMob() {
      return this.chaseTime < 0 ? null : super.getLastHurtByMob();
   }

   public void setLastHurtByMob(@Nullable LivingEntity entitylivingbaseIn) {
      if (this.chaseTime >= 0) {
         super.setLastHurtByMob(entitylivingbaseIn);
      } else {
         super.setLastHurtByMob(null);
      }
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      this.setMoss(this.random.nextInt(6));
      this.setTurtleScale(0.8F + this.random.nextFloat() * 0.2F);
      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   public float getTurtleScale() {
      return (Float)this.entityData.get(TURTLE_SCALE);
   }

   public void setTurtleScale(float scale) {
      this.entityData.set(TURTLE_SCALE, scale);
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new SemiAquaticPathNavigator(this, worldIn) {
         @Override
         public boolean isStableDestination(BlockPos pos) {
            return this.level.getBlockState(pos).getFluidState().isEmpty();
         }
      };
   }

   public boolean isWaiting() {
      return (Boolean)this.entityData.get(WAITING);
   }

   public void setWaiting(boolean sit) {
      this.entityData.set(WAITING, sit);
   }

   public int getMoss() {
      return (Integer)this.entityData.get(MOSS);
   }

   public void setMoss(int moss) {
      this.entityData.set(MOSS, moss);
   }

   protected void updateAir(int air) {
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Waiting", this.isWaiting());
      compound.putInt("MossLevel", this.getMoss());
      compound.putFloat("TurtleScale", this.getTurtleScale());
      compound.putInt("MossTime", this.mossTime);
      compound.putInt("WaitTime", this.waitTime);
      compound.putInt("WaitTime2", this.timeUntilWait);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setWaiting(AMCompat.getBoolean(compound, "Waiting"));
      this.setMoss(AMCompat.getInt(compound, "MossLevel"));
      this.setTurtleScale(AMCompat.getFloat(compound, "TurtleScale"));
      this.mossTime = AMCompat.getInt(compound, "MossTime");
      this.waitTime = AMCompat.getInt(compound, "WaitTime");
      this.timeUntilWait = AMCompat.getInt(compound, "WaitTime2");
   }

   @Override
   public boolean shouldEnterWater() {
      return true;
   }

   @Override
   public boolean shouldLeaveWater() {
      return false;
   }

   @Override
   public boolean shouldStopMoving() {
      return this.isWaiting();
   }

   @Override
   public int getWaterSearchRange() {
      return 10;
   }

   public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
      return worldIn.getFluidState(pos.below()).isEmpty() && worldIn.getFluidState(pos).is(FluidTags.WATER) ? 10.0F : super.getWalkTargetValue(pos, worldIn);
   }

   public boolean isBesideClimbableBlock() {
      return ((Byte)this.entityData.get(CLIMBING) & 1) != 0;
   }

   public void setBesideClimbableBlock(boolean climbing) {
      byte b0 = (Byte)this.entityData.get(CLIMBING);
      if (climbing) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.entityData.set(CLIMBING, b0);
   }

   public boolean checkSpawnObstruction(LevelReader worldIn) {
      return worldIn.isUnobstructed(this);
   }

   public void travel(Vec3 travelVector) {
      if (this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), travelVector);
         this.move(MoverType.SELF, this.getDeltaMovement());
         if (this.jumping) {
            this.setDeltaMovement(this.getDeltaMovement().scale(1.0));
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.72, 0.0));
         } else {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.4));
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.08, 0.0));
         }
      } else {
         super.travel(travelVector);
      }
   }

   public boolean readyForShearing() {
      return this.isAlive() && this.getMoss() > 0;
   }

   public boolean isShearable(@Nullable Player player, @Nonnull ItemStack item, Level world, BlockPos pos) {
      return this.readyForShearing();
   }

   public void shear(SoundSource category) {
      this.level().playSound(null, this, SoundEvents.SHEEP_SHEAR, category, 1.0F, 1.0F);
      this.gameEvent(GameEvent.ENTITY_INTERACT);
      if (!this.level().isClientSide()) {
         if (this.random.nextFloat() < this.getMoss() * 0.05F) {
            AMCompat.spawnAtLocation(this, (ItemLike)AMItemRegistry.SPIKED_SCUTE.get());
         } else {
            AMCompat.spawnAtLocation(this, Items.SEAGRASS);
         }

         this.setMoss(0);
      }
   }

   @Nonnull
   public List<ItemStack> onSheared(@Nullable Player player, @Nonnull ItemStack item, Level world, BlockPos pos) {
      world.playSound(null, this, SoundEvents.SHEEP_SHEAR, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 1.0F, 1.0F);
      this.gameEvent(GameEvent.ENTITY_INTERACT);
      if (!world.isClientSide()) {
         if (this.random.nextFloat() < this.getMoss() * 0.05F) {
            this.setMoss(0);
            return Collections.singletonList(new ItemStack((ItemLike)AMItemRegistry.SPIKED_SCUTE.get()));
         } else {
            this.setMoss(0);
            return Collections.singletonList(new ItemStack(Items.SEAGRASS));
         }
      } else {
         return Collections.emptyList();
      }
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      return AMCompat.create(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE.get(), p_241840_1_);
   }
}
