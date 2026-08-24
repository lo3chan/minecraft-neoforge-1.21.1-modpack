package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntitySpectre extends Animal implements FlyingAnimal {
   private static final EntityDataAccessor<Integer> CARDINAL_ORDINAL = SynchedEntityData.defineId(EntitySpectre.class, EntityDataSerializers.INT);
   public float birdPitch = 0.0F;
   public float prevBirdPitch = 0.0F;
   public Vec3 lurePos = null;

   public boolean isFood(ItemStack stack) {
      return stack.is(Items.WHEAT);
   }

   protected EntitySpectre(EntityType type, Level world) {
      super(type, world);
      this.moveControl = new EntitySpectre.MoveHelperController(this);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.spectreSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static boolean canSpectreSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
      BlockState blockstate = worldIn.getBlockState(pos.below());
      return true;
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.SPECTRE_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.SPECTRE_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.SPECTRE_HURT.get();
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 50.0)
         .add(Attributes.FOLLOW_RANGE, 64.0)
         .add(Attributes.ATTACK_DAMAGE, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 1.0);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CARDINAL_ORDINAL, Direction.NORTH.get3DDataValue());
   }

   public int getCardinalInt() {
      return (Integer)this.entityData.get(CARDINAL_ORDINAL);
   }

   public void setCardinalInt(int command) {
      this.entityData.set(CARDINAL_ORDINAL, command);
   }

   public Direction getCardinalDirection() {
      return Direction.from3DDataValue(this.getCardinalInt());
   }

   public void setCardinalDirection(Direction dir) {
      this.setCardinalInt(dir.get3DDataValue());
   }

   protected void registerGoals() {
      this.goalSelector
         .addGoal(1, new EntitySpectre.TemptHeartGoal(this, 1.0, Ingredient.of(new ItemLike[]{(ItemLike)AMItemRegistry.SOUL_HEART.get()}), false));
      this.goalSelector.addGoal(2, new EntitySpectre.FlyGoal(this));
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return !source.is(DamageTypes.MAGIC)
            && !source.is(DamageTypes.FELL_OUT_OF_WORLD)
            && !source.isCreativePlayer()
            && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
         || super.isInvulnerableTo(source);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      this.setXRot(0.0F);
      this.randomizeDirection();
      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   public float getBrightness() {
      return 1.0F;
   }

   public boolean isNoGravity() {
      return true;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public void tick() {
      super.tick();
      Vec3 vector3d1 = this.getDeltaMovement();
      this.setYRot(-((float)Mth.atan2(vector3d1.x, vector3d1.z)) * 57.295776F);
      this.yBodyRot = this.getYRot();
      this.prevBirdPitch = this.birdPitch;
      this.noPhysics = true;
      this.birdPitch = (float)(-((float)this.getDeltaMovement().y * 0.5F * 57.2957763671875));
      if (this.getLeashHolder() != null && !(this.getLeashHolder() instanceof LeashFenceKnotEntity)) {
         Entity entity = this.getLeashHolder();
         float f = this.distanceTo(entity);
         if (f > 10.0F) {
            double d0 = (this.getX() - entity.getX()) / f;
            double d1 = (this.getY() - entity.getY()) / f;
            double d2 = (this.getZ() - entity.getZ()) / f;
            entity.setDeltaMovement(
               entity.getDeltaMovement().add(Math.copySign(d0 * d0 * 0.4, d0), Math.copySign(d1 * d1 * 0.4, d1), Math.copySign(d2 * d2 * 0.4, d2))
            );
         }

         entity.fallDistance = 0.0F;
         if (entity.getDeltaMovement().y < 0.0) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, 0.699999988079071, 1.0));
         }

         if (entity.isShiftKeyDown()) {
            this.dropLeash(true, true);
         }
      }
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return null;
   }

   public boolean handleLeashAtDistance(Entity holder, float distance) {
      if (!holder.isPassenger() && !(holder instanceof LeashFenceKnotEntity)) {
         this.amTickLeash(distance);
         return false;
      } else {
         return true;
      }
   }

   private void amTickLeash(float f) {
      if (f > 30.0F) {
         double lvt_3_1_ = (this.getLeashHolder().getX() - this.getX()) / f;
         double lvt_5_1_ = (this.getLeashHolder().getY() - this.getY()) / f;
         double lvt_7_1_ = (this.getLeashHolder().getZ() - this.getZ()) / f;
         this.setDeltaMovement(
            this.getDeltaMovement()
               .add(
                  Math.copySign(lvt_3_1_ * lvt_3_1_ * 0.4, lvt_3_1_),
                  Math.copySign(lvt_5_1_ * lvt_5_1_ * 0.4, lvt_5_1_),
                  Math.copySign(lvt_7_1_ * lvt_7_1_ * 0.4, lvt_7_1_)
               )
         );
      }

      if (!this.isAlive() || !this.getLeashHolder().isAlive()) {
         this.dropLeash(true, true);
      }
   }

   private void randomizeDirection() {
      this.setCardinalInt(2 + this.random.nextInt(3));
   }

   public boolean isFlying() {
      return true;
   }

   private class FlyGoal extends Goal {
      private final EntitySpectre parentEntity;
      boolean island = false;
      float circlingTime = 0.0F;
      float circleDistance = 14.0F;
      float maxCirclingTime = 80.0F;
      boolean clockwise = false;
      private BlockPos target = null;
      private int islandCheckTime = 20;

      public FlyGoal(EntitySpectre sunbird) {
         this.parentEntity = sunbird;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         if (this.parentEntity.lurePos != null) {
            return false;
         } else {
            MoveControl movementcontroller = this.parentEntity.getMoveControl();
            this.clockwise = EntitySpectre.this.random.nextBoolean();
            this.circleDistance = 5 + EntitySpectre.this.random.nextInt(10);
            if (movementcontroller.hasWanted() && this.target != null) {
               return false;
            } else {
               this.target = this.island ? this.getIslandPos(this.parentEntity.blockPosition()) : this.getBlockFromDirection();
               if (this.target != null) {
                  this.parentEntity.getMoveControl().setWantedPosition(this.target.getX() + 0.5, this.target.getY() + 0.5, this.target.getZ() + 0.5, 1.0);
               }

               return true;
            }
         }
      }

      public boolean canContinueToUse() {
         return this.parentEntity.lurePos == null;
      }

      public void stop() {
         this.island = false;
         this.islandCheckTime = 0;
         this.circleDistance = 5 + EntitySpectre.this.random.nextInt(10);
         this.circlingTime = 0.0F;
         this.clockwise = EntitySpectre.this.random.nextBoolean();
         this.target = null;
      }

      public void tick() {
         if (this.islandCheckTime-- <= 0) {
            this.islandCheckTime = 20;
            if (this.circlingTime == 0.0F) {
               this.island = this.parentEntity.level().getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, this.parentEntity.blockPosition()).getY() > 2;
               if (this.island) {
                  this.parentEntity.randomizeDirection();
               }
            }
         }

         if (this.island) {
            this.circlingTime++;
            if (this.circlingTime > 100.0F) {
               this.island = false;
               this.islandCheckTime = 1200;
            }
         } else if (this.circlingTime > 0.0F) {
            this.circlingTime--;
         }

         if (this.target == null) {
            this.target = this.island ? this.getIslandPos(this.parentEntity.blockPosition()) : this.getBlockFromDirection();
         }

         if (!this.island) {
            this.parentEntity.setYRot(this.parentEntity.getCardinalDirection().toYRot());
         }

         if (this.target != null) {
            this.parentEntity.getMoveControl().setWantedPosition(this.target.getX() + 0.5, this.target.getY() + 0.5, this.target.getZ() + 0.5, 1.0);
            if (this.parentEntity.distanceToSqr(Vec3.atCenterOf(this.target)) < 5.5) {
               this.target = null;
            }
         }
      }

      public BlockPos getBlockFromDirection() {
         float radius = 15.0F;
         BlockPos forwards = this.parentEntity.blockPosition().relative(this.parentEntity.getCardinalDirection(), (int)Math.ceil(radius));
         int height = 0;
         if (EntitySpectre.this.level().getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, forwards).getY() < 15) {
            height = 70 + EntitySpectre.this.random.nextInt(2);
         } else {
            height = EntitySpectre.this.level().getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, forwards).getY() + 10 + EntitySpectre.this.random.nextInt(10);
         }

         return new BlockPos(forwards.getX(), height, forwards.getZ());
      }

      public BlockPos getIslandPos(BlockPos orbit) {
         float angle = 0.05235988F * (this.clockwise ? -this.circlingTime : this.circlingTime);
         double extraX = this.circleDistance * Mth.sin(angle);
         double extraZ = this.circleDistance * Mth.cos(angle);
         int height = EntitySpectre.this.level().getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, orbit).getY();
         if (height < 3) {
            this.island = false;
            return this.getBlockFromDirection();
         } else {
            return new BlockPos(
               (int)(orbit.getX() + extraX),
               Math.min(height + 10, orbit.getY() + EntitySpectre.this.random.nextInt(3) - EntitySpectre.this.random.nextInt(1)),
               (int)(orbit.getZ() + extraZ)
            );
         }
      }
   }

   static class MoveHelperController extends MoveControl {
      private final EntitySpectre parentEntity;

      public MoveHelperController(EntitySpectre sunbird) {
         super(sunbird);
         this.parentEntity = sunbird;
      }

      public void tick() {
         if (this.operation == Operation.MOVE_TO) {
            Vec3 vector3d = new Vec3(this.wantedX - this.parentEntity.getX(), this.wantedY - this.parentEntity.getY(), this.wantedZ - this.parentEntity.getZ());
            double d5 = vector3d.length();
            if (d5 < 0.3) {
               this.operation = Operation.WAIT;
               this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().scale(0.5));
            } else {
               double d0 = this.wantedX - this.parentEntity.getX();
               double d1 = this.wantedY - this.parentEntity.getY();
               double d2 = this.wantedZ - this.parentEntity.getZ();
               this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05 / d5)));
               Vec3 vector3d1 = this.parentEntity.getDeltaMovement();
               this.parentEntity.setYRot(-((float)Mth.atan2(vector3d1.x, vector3d1.z)) * 57.295776F);
               this.parentEntity.yBodyRot = this.parentEntity.getYRot();
            }
         }
      }

      private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
         AABB axisalignedbb = this.parentEntity.getBoundingBox();

         for (int i = 1; i < p_220673_2_; i++) {
            axisalignedbb = axisalignedbb.move(p_220673_1_);
            if (!this.parentEntity.level().noCollision(this.parentEntity, axisalignedbb)) {
               return false;
            }
         }

         return true;
      }
   }

   static class TemptHeartGoal extends Goal {
      protected final EntitySpectre creature;
      private final TargetingConditions ENTITY_PREDICATE = TargetingConditions.forNonCombat().range(64.0).ignoreInvisibilityTesting().ignoreLineOfSight();
      private final double speed;
      private final Ingredient temptItem;
      protected Player closestPlayer;
      private int delayTemptCounter;

      public TemptHeartGoal(EntitySpectre p_i47822_1_, double p_i47822_2_, Ingredient p_i47822_4_, boolean p_i47822_5_) {
         this(p_i47822_1_, p_i47822_2_, p_i47822_5_, p_i47822_4_);
      }

      public TemptHeartGoal(EntitySpectre p_i47823_1_, double p_i47823_2_, boolean p_i47823_4_, Ingredient p_i47823_5_) {
         this.creature = p_i47823_1_;
         this.speed = p_i47823_2_;
         this.temptItem = p_i47823_5_;
      }

      public boolean canUse() {
         if (this.delayTemptCounter > 0) {
            this.delayTemptCounter--;
            return false;
         } else {
            this.closestPlayer = AMCompat.getNearestPlayer(this.creature.level(), this.ENTITY_PREDICATE, this.creature);
            return this.closestPlayer != null && this.creature.getLeashHolder() != this.closestPlayer
               ? this.isTempting(this.closestPlayer.getMainHandItem()) || this.isTempting(this.closestPlayer.getOffhandItem())
               : false;
         }
      }

      protected boolean isTempting(ItemStack p_188508_1_) {
         return this.temptItem.test(p_188508_1_);
      }

      public boolean canContinueToUse() {
         return this.canUse();
      }

      public void start() {
         this.creature.lurePos = this.closestPlayer.position();
      }

      public void stop() {
         this.closestPlayer = null;
         this.delayTemptCounter = 100;
         this.creature.lurePos = null;
      }

      public void tick() {
         this.creature.getLookControl().setLookAt(this.closestPlayer, this.creature.getMaxHeadYRot() + 20, this.creature.getMaxHeadXRot());
         if (this.creature.distanceToSqr(this.closestPlayer) < 6.25) {
            this.creature.getNavigation().stop();
         } else {
            this.creature
               .getMoveControl()
               .setWantedPosition(
                  this.closestPlayer.getX(), this.closestPlayer.getY() + this.closestPlayer.getEyeHeight(), this.closestPlayer.getZ(), this.speed
               );
         }
      }
   }
}
