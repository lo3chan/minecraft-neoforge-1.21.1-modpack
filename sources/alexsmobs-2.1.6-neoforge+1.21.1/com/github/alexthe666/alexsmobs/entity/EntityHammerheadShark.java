package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FollowBoatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EntityHammerheadShark extends WaterAnimal {
   private static final Predicate<LivingEntity> INJURED_PREDICATE = mob -> mob.getHealth() <= mob.getMaxHealth() / 2.0;

   protected EntityHammerheadShark(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.moveControl = new AquaticMoveController(this, 1.0F);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.hammerheadSharkSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new SemiAquaticPathNavigator(this, worldIn);
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.COD_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.COD_HURT;
   }

   public void travel(Vec3 travelVector) {
      if (this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), travelVector);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
         if (this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
         }
      } else {
         super.travel(travelVector);
      }
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
      this.goalSelector.addGoal(1, new EntityHammerheadShark.CirclePreyGoal(this, 1.0F));
      this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 0.6000000238418579, 7));
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(8, new FollowBoatGoal(this));
      this.goalSelector.addGoal(9, new AvoidEntityGoal(this, Guardian.class, 8.0F, 1.0, 1.0));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new EntityAINearestTarget3D<LivingEntity>(this, LivingEntity.class, 50, false, true, INJURED_PREDICATE));
      this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Squid.class, 50, false, true, null));
      this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, EntityMimicOctopus.class, 80, false, true, null));
      this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, AbstractSchoolingFish.class, 70, false, true, null));
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).getType() == Type.BLOCK;
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 30.0)
         .add(Attributes.ARMOR, 0.0)
         .add(Attributes.ATTACK_DAMAGE, 5.0)
         .add(Attributes.MOVEMENT_SPEED, 0.5);
   }

   public static <T extends Mob> boolean canHammerheadSharkSpawn(
      EntityType<EntityHammerheadShark> p_223364_0_, LevelAccessor p_223364_1_, MobSpawnType reason, BlockPos p_223364_3_, RandomSource p_223364_4_
   ) {
      return p_223364_3_.getY() > 45 && p_223364_3_.getY() < p_223364_1_.getSeaLevel() ? p_223364_1_.getFluidState(p_223364_3_).is(FluidTags.WATER) : false;
   }

   private static class CirclePreyGoal extends Goal {
      EntityHammerheadShark shark;
      float speed;
      float circlingTime = 0.0F;
      float circleDistance = 5.0F;
      float maxCirclingTime = 80.0F;
      boolean clockwise = false;

      public CirclePreyGoal(EntityHammerheadShark shark, float speed) {
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
         this.shark = shark;
         this.speed = speed;
      }

      public boolean canUse() {
         return this.shark.getTarget() != null;
      }

      public boolean canContinueToUse() {
         return this.shark.getTarget() != null;
      }

      public void start() {
         this.circlingTime = 0.0F;
         this.maxCirclingTime = 360 + this.shark.random.nextInt(80);
         this.circleDistance = 5.0F + this.shark.random.nextFloat() * 5.0F;
         this.clockwise = this.shark.random.nextBoolean();
      }

      public void stop() {
         this.circlingTime = 0.0F;
         this.maxCirclingTime = 360 + this.shark.random.nextInt(80);
         this.circleDistance = 5.0F + this.shark.random.nextFloat() * 5.0F;
         this.clockwise = this.shark.random.nextBoolean();
      }

      public void tick() {
         LivingEntity prey = this.shark.getTarget();
         if (prey != null) {
            double dist = this.shark.distanceTo(prey);
            if (this.circlingTime >= this.maxCirclingTime) {
               this.shark.lookAt(prey, 30.0F, 30.0F);
               this.shark.getNavigation().moveTo(prey, 1.5);
               if (dist < 2.0) {
                  AMCompat.doHurtTarget(this.shark, prey);
                  if (this.shark.random.nextFloat() < 0.3F) {
                     AMCompat.spawnAtLocation(this.shark, new ItemStack((ItemLike)AMItemRegistry.SHARK_TOOTH.get()));
                  }

                  this.stop();
               }
            } else if (dist <= 25.0) {
               this.circlingTime++;
               BlockPos circlePos = this.getSharkCirclePos(prey);
               if (circlePos != null) {
                  this.shark.getNavigation().moveTo(circlePos.getX() + 0.5, circlePos.getY() + 0.5, circlePos.getZ() + 0.5, 0.6);
               }
            } else {
               this.shark.lookAt(prey, 30.0F, 30.0F);
               this.shark.getNavigation().moveTo(prey, 0.8);
            }
         }
      }

      public BlockPos getSharkCirclePos(LivingEntity target) {
         float angle = 0.017453292F * (this.clockwise ? -this.circlingTime : this.circlingTime);
         double extraX = this.circleDistance * Mth.sin(angle);
         double extraZ = this.circleDistance * Mth.cos(angle);
         BlockPos ground = AMBlockPos.fromCoords(target.getX() + 0.5 + extraX, this.shark.getY(), target.getZ() + 0.5 + extraZ);
         return this.shark.level().getFluidState(ground).is(FluidTags.WATER) ? ground : null;
      }
   }
}
