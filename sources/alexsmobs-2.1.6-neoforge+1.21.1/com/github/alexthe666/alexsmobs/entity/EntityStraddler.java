package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.StraddlerAIShoot;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class EntityStraddler extends Monster implements IAnimatedEntity {
   public static final Animation ANIMATION_LAUNCH = Animation.create(30);
   private static final EntityDataAccessor<Integer> STRADPOLE_COUNT = SynchedEntityData.defineId(EntityStraddler.class, EntityDataSerializers.INT);
   private int animationTick;
   private Animation currentAnimation;

   protected EntityStraddler(EntityType type, Level world) {
      super(type, world);
      this.setPathfindingMalus(PathType.LAVA, 0.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.STRADDLER_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.STRADDLER_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.STRADDLER_HURT.get();
   }

   public static boolean canStraddlerSpawn(EntityType animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
      return worldIn.getBlockState(pos.below()).is(BlockTags.BASE_STONE_NETHER);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 28.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
         .add(Attributes.ARMOR, 5.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.ATTACK_DAMAGE, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 0.30000001192092896);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(STRADPOLE_COUNT, 0);
   }

   public int getStradpoleCount() {
      return (Integer)this.entityData.get(STRADPOLE_COUNT);
   }

   public void setStradpoleCount(int index) {
      this.entityData.set(STRADPOLE_COUNT, index);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.straddlerSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new StraddlerAIShoot(this, 0.5, 30, 16.0F));
      this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0, 60));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Strider.class, 8.0F));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, true));
   }

   protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
      this.checkInsideBlocks();
      if (this.isInLava()) {
         this.fallDistance = 0.0F;
      } else {
         super.checkFallDamage(p_184231_1_, p_184231_3_, p_184231_4_, p_184231_5_);
      }
   }

   public void travel(Vec3 travelVector) {
      this.setSpeed(
         (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (this.getAnimation() == ANIMATION_LAUNCH ? 0.5F : 1.0F) * (this.isInLava() ? 0.2F : 1.0F)
      );
      if (this.isEffectiveAi() && (this.isInWater() || this.isInLava())) {
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

   private void floatStrider() {
      if (this.isInLava()) {
         CollisionContext lvt_1_1_ = CollisionContext.of(this);
         double d1 = AMPlatform.fluidHeightLava(this);
         if (d1 <= 0.5 && d1 > 0.0) {
            if (this.getDeltaMovement().y < 0.0) {
               this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.0, 1.0));
            }

            this.setOnGround(true);
         } else if (lvt_1_1_.isAbove(LiquidBlock.STABLE_SHAPE, this.blockPosition().below(), true)
            && !this.level().getFluidState(this.blockPosition().above()).is(FluidTags.LAVA)) {
            this.setOnGround(true);
         } else {
            this.setDeltaMovement(0.0, Math.min(d1 - 0.5, 1.0) * 0.20000000298023224, 0.0);
         }
      }
   }

   public boolean checkSpawnObstruction(LevelReader worldIn) {
      return worldIn.isUnobstructed(this);
   }

   protected float nextStep() {
      return this.moveDist + 0.6F;
   }

   public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
      if (worldIn.getBlockState(pos).getFluidState().is(FluidTags.LAVA)) {
         return 10.0F;
      } else {
         return this.isInLava() ? -1.0F / 0.0F : 0.0F;
      }
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
      Vec3[] avector3d = new Vec3[]{
         getCollisionHorizontalEscapeVector(this.getBbWidth(), livingEntity.getBbWidth(), livingEntity.getYRot()),
         getCollisionHorizontalEscapeVector(this.getBbWidth(), livingEntity.getBbWidth(), livingEntity.getYRot() - 22.5F),
         getCollisionHorizontalEscapeVector(this.getBbWidth(), livingEntity.getBbWidth(), livingEntity.getYRot() + 22.5F),
         getCollisionHorizontalEscapeVector(this.getBbWidth(), livingEntity.getBbWidth(), livingEntity.getYRot() - 45.0F),
         getCollisionHorizontalEscapeVector(this.getBbWidth(), livingEntity.getBbWidth(), livingEntity.getYRot() + 45.0F)
      };
      Set<BlockPos> set = Sets.newLinkedHashSet();
      double d0 = this.getBoundingBox().maxY;
      double d1 = this.getBoundingBox().minY - 0.5;
      MutableBlockPos blockpos$mutable = new MutableBlockPos();

      for (Vec3 vector3d : avector3d) {
         blockpos$mutable.set(this.getX() + vector3d.x, d0, this.getZ() + vector3d.z);

         for (double d2 = d0; d2 > d1; d2--) {
            set.add(blockpos$mutable.immutable());
            blockpos$mutable.move(Direction.DOWN);
         }
      }

      for (BlockPos blockpos : set) {
         if (!this.level().getFluidState(blockpos).is(FluidTags.LAVA)) {
            double d3 = this.level().getBlockFloorHeight(blockpos);
            if (DismountHelper.isBlockFloorValid(d3)) {
               Vec3 vector3d1 = Vec3.upFromBottomCenterOf(blockpos, d3);
               UnmodifiableIterator var14 = livingEntity.getDismountPoses().iterator();

               while (var14.hasNext()) {
                  Pose pose = (Pose)var14.next();
                  AABB axisalignedbb = livingEntity.getLocalBoundsForPose(pose);
                  if (DismountHelper.canDismountTo(this.level(), livingEntity, axisalignedbb.move(vector3d1))) {
                     livingEntity.setPose(pose);
                     return vector3d1;
                  }
               }
            }
         }
      }

      return new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ());
   }

   public boolean isOnFire() {
      return false;
   }

   public boolean canStandOnFluid(Fluid p_230285_1_) {
      return p_230285_1_.is(FluidTags.LAVA);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("StradpoleCount", this.getStradpoleCount());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setStradpoleCount(AMCompat.getInt(compound, "StradpoleCount"));
   }

   public void tick() {
      super.tick();
      this.floatStrider();
      this.checkInsideBlocks();
      if (this.getAnimation() == ANIMATION_LAUNCH && this.isAlive() && this.getAnimationTick() == 2) {
         this.playSound((SoundEvent)SoundEvents.CROSSBOW_LOADING_MIDDLE.value(), 2.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
      }

      if (this.getAnimation() == ANIMATION_LAUNCH && this.isAlive() && this.getAnimationTick() == 20 && this.getTarget() != null) {
         EntityStradpole pole = AMCompat.create(AMEntityRegistry.STRADPOLE.get(), this.level());
         pole.setParentId(this.getUUID());
         pole.setPos(this.getX(), this.getEyeY(), this.getZ());
         double d0 = this.getTarget().getEyeY() - 1.100000023841858;
         double d1 = this.getTarget().getX() - this.getX();
         double d2 = d0 - pole.getY();
         double d3 = this.getTarget().getZ() - this.getZ();
         float f3 = Mth.sqrt((float)(d1 * d1 + d2 * d2 + d3 * d3)) * 0.2F;
         this.gameEvent(GameEvent.PROJECTILE_SHOOT);
         this.playSound((SoundEvent)SoundEvents.CROSSBOW_LOADING_END.value(), 2.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
         pole.shoot(d1, d2 + f3, d3, 2.0F, 0.0F);
         pole.setYRot(this.getYRot() % 360.0F);
         pole.setXRot(Mth.clamp(this.getYRot(), -90.0F, 90.0F) % 360.0F);
         if (!this.level().isClientSide()) {
            this.level().addFreshEntity(pole);
         }
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
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
   public int getAnimationTick() {
      return this.animationTick;
   }

   @Override
   public void setAnimationTick(int i) {
      this.animationTick = i;
   }

   @Override
   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_LAUNCH};
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new EntityStraddler.LavaPathNavigator(this, worldIn);
   }

   public boolean shouldShoot() {
      return true;
   }

   static class LavaPathNavigator extends GroundPathNavigation {
      LavaPathNavigator(EntityStraddler p_i231565_1_, Level p_i231565_2_) {
         super(p_i231565_1_, p_i231565_2_);
      }

      protected PathFinder createPathFinder(int p_179679_1_) {
         this.nodeEvaluator = new WalkNodeEvaluator();
         return new PathFinder(this.nodeEvaluator, p_179679_1_);
      }

      protected boolean hasValidPathType(PathType p_230287_1_) {
         return p_230287_1_ == PathType.LAVA
            || p_230287_1_ == PathType.DAMAGE_FIRE
            || p_230287_1_ == PathType.DANGER_FIRE
            || super.hasValidPathType(p_230287_1_);
      }

      public boolean isStableDestination(BlockPos pos) {
         return this.level.getBlockState(pos).is(Blocks.LAVA) || super.isStableDestination(pos);
      }
   }
}
