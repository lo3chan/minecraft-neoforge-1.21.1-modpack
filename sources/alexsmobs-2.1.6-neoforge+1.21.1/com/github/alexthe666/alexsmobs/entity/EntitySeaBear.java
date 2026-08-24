package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAISwimBottom;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntitySeaBear extends WaterAnimal implements IAnimatedEntity {
   public static final Animation ANIMATION_ATTACK = Animation.create(17);
   public static final Animation ANIMATION_POINT = Animation.create(25);
   public float prevOnLandProgress;
   public float onLandProgress;
   public int circleCooldown = 0;
   private int animationTick;
   private Animation currentAnimation;
   private BlockPos lastCircle = null;
   public static final Predicate<LivingEntity> SOMBRERO = player -> player.getItemBySlot(EquipmentSlot.HEAD).is(AMItemRegistry.SOMBRERO.get());

   protected EntitySeaBear(EntityType entityType, Level level) {
      super(entityType, level);
      this.moveControl = new AquaticMoveController(this, 1.0F, 10.0F);
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return !this.requiresCustomPersistence();
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.hasCustomName();
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 200.0)
         .add(Attributes.ATTACK_DAMAGE, 8.0)
         .add(Attributes.MOVEMENT_SPEED, 0.32499998807907104);
   }

   public static boolean isMobSafe(Entity entity) {
      if (entity instanceof Player && ((Player)entity).isCreative()) {
         return true;
      } else {
         BlockState state = entity.level().getBlockState(entity.blockPosition().below());
         return state.is(AMBlockRegistry.SAND_CIRCLE.get()) || state.is(AMBlockRegistry.RED_SAND_CIRCLE.get());
      }
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.GRIZZLY_BEAR_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.GRIZZLY_BEAR_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.GRIZZLY_BEAR_DIE.get();
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
      this.goalSelector.addGoal(2, new EntitySeaBear.AttackAI());
      this.goalSelector.addGoal(3, new EntitySeaBear.AvoidCircleAI());
      this.goalSelector.addGoal(4, new AnimalAISwimBottom(this, 1.0, 7) {
         public boolean canUse() {
            return super.canUse() && EntitySeaBear.this.getAnimation() == IAnimatedEntity.NO_ANIMATION;
         }

         public boolean canContinueToUse() {
            return super.canContinueToUse() && EntitySeaBear.this.getAnimation() == IAnimatedEntity.NO_ANIMATION;
         }
      });
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, LivingEntity.class, false, AMCompat.selector(SOMBRERO)));
   }

   public void tick() {
      super.tick();
      this.prevOnLandProgress = this.onLandProgress;
      if (this.isInWater()) {
         if (this.onLandProgress > 0.0F) {
            this.onLandProgress--;
         }
      } else if (this.onLandProgress < 5.0F) {
         this.onLandProgress++;
      }

      if (this.onGround() && !this.isInWater()) {
         this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
         this.setYRot(this.random.nextFloat() * 360.0F);
         this.setOnGround(false);
         this.hasImpulse = true;
      }

      if (this.circleCooldown > 0) {
         this.circleCooldown--;
         this.setTarget(null);
         this.setLastHurtByMob(null);
      }

      if (this.getAnimation() == ANIMATION_POINT) {
         this.yBodyRot = this.getYHeadRot();
         this.rotOffs = this.getYHeadRot();
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new SemiAquaticPathNavigator(this, worldIn);
   }

   public boolean isPushable() {
      return false;
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean canCollideWith(Entity e) {
      return !isMobSafe(e);
   }

   public void travel(Vec3 travelVector) {
      if (this.getAnimation() == ANIMATION_POINT) {
         super.travel(Vec3.ZERO);
      } else if (this.isEffectiveAi() && this.isInWater()) {
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

   @Override
   public Animation getAnimation() {
      return this.currentAnimation;
   }

   @Override
   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   @Override
   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_POINT, ANIMATION_ATTACK};
   }

   @Override
   public int getAnimationTick() {
      return this.animationTick;
   }

   @Override
   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public void setTarget(@Nullable LivingEntity entity) {
      if (entity == null || !isMobSafe(entity)) {
         super.setTarget(entity);
      }
   }

   public void push(Entity entity) {
      if (!isMobSafe(entity)) {
         super.push(entity);
      }
   }

   private class AttackAI extends Goal {
      public AttackAI() {
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return EntitySeaBear.this.getTarget() != null
            && EntitySeaBear.this.getTarget().isInWaterOrBubble()
            && EntitySeaBear.this.getTarget().isAlive()
            && (EntitySeaBear.this.circleCooldown == 0 || EntitySeaBear.this.getAnimation() == EntitySeaBear.ANIMATION_POINT);
      }

      public void tick() {
         LivingEntity enemy = EntitySeaBear.this.getTarget();
         if (EntitySeaBear.this.getAnimation() == EntitySeaBear.ANIMATION_POINT) {
            EntitySeaBear.this.getNavigation().stop();
            EntitySeaBear.this.setDeltaMovement(EntitySeaBear.this.getDeltaMovement().multiply(0.0, 1.0, 0.0));
            EntitySeaBear.this.lookAt(enemy, 360.0F, 50.0F);
         } else if (EntitySeaBear.isMobSafe(enemy) && EntitySeaBear.this.distanceTo(enemy) < 6.0F) {
            EntitySeaBear.this.circleCooldown = 100 + EntitySeaBear.this.random.nextInt(100);
            EntitySeaBear.this.setAnimation(EntitySeaBear.ANIMATION_POINT);
            EntitySeaBear.this.lookAt(enemy, 360.0F, 50.0F);
            EntitySeaBear.this.lastCircle = enemy.blockPosition();
         } else {
            EntitySeaBear.this.getNavigation().moveTo(enemy.getX(), enemy.getY(0.5), enemy.getZ(), 1.6);
            if (EntitySeaBear.this.hasLineOfSight(enemy) && EntitySeaBear.this.distanceTo(enemy) < 3.5F) {
               EntitySeaBear.this.setAnimation(EntitySeaBear.ANIMATION_ATTACK);
               if (EntitySeaBear.this.getAnimationTick() % 5 == 0) {
                  enemy.hurt(EntitySeaBear.this.damageSources().mobAttack(EntitySeaBear.this), 6.0F);
               }
            }
         }
      }
   }

   private class AvoidCircleAI extends Goal {
      private Vec3 target = null;

      public AvoidCircleAI() {
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return EntitySeaBear.this.circleCooldown > 0
            && EntitySeaBear.this.lastCircle != null
            && EntitySeaBear.this.getAnimation() != EntitySeaBear.ANIMATION_POINT;
      }

      public void tick() {
         BlockPos pos = EntitySeaBear.this.lastCircle;
         if (this.target == null
            || EntitySeaBear.this.distanceToSqr(this.target) < 2.0
            || !EntitySeaBear.this.level().getFluidState(AMBlockPos.fromVec3(this.target).above()).is(FluidTags.WATER)) {
            this.target = DefaultRandomPos.getPosAway(EntitySeaBear.this, 20, 7, Vec3.atCenterOf(pos));
         }

         if (this.target != null && EntitySeaBear.this.level().getFluidState(AMBlockPos.fromVec3(this.target).above()).is(FluidTags.WATER)) {
            EntitySeaBear.this.getNavigation().moveTo(this.target.x, this.target.y, this.target.z, 1.0);
         }
      }
   }
}
