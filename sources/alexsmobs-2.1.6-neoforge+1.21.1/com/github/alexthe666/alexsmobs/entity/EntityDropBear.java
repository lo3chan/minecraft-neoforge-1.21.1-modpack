package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityDropBear extends Monster implements IAnimatedEntity {
   public static final Animation ANIMATION_BITE = Animation.create(9);
   public static final Animation ANIMATION_SWIPE_R = Animation.create(15);
   public static final Animation ANIMATION_SWIPE_L = Animation.create(15);
   public static final Animation ANIMATION_JUMPUP = Animation.create(20);
   private static final EntityDataAccessor<Boolean> UPSIDE_DOWN = SynchedEntityData.defineId(EntityDropBear.class, EntityDataSerializers.BOOLEAN);
   public float prevUpsideDownProgress;
   public float upsideDownProgress;
   public boolean fallRotation = this.random.nextBoolean();
   private int animationTick;
   private boolean jumpingUp = false;
   private Animation currentAnimation;
   private int upwardsFallingTicks = 0;
   private boolean isUpsideDownNavigator;
   private boolean prevOnGround = false;

   protected EntityDropBear(EntityType type, Level world) {
      super(type, world);
      this.switchNavigator(true);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 22.0)
         .add(Attributes.FOLLOW_RANGE, 20.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.699999988079071)
         .add(Attributes.ATTACK_DAMAGE, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   public static BlockPos getLowestPos(LevelAccessor world, BlockPos pos) {
      while (!world.getBlockState(pos).isFaceSturdy(world, pos, Direction.DOWN) && pos.getY() < 320) {
         pos = pos.above();
      }

      return pos;
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.dropbearSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.DROPBEAR_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.DROPBEAR_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.DROPBEAR_HURT.get();
   }

   public boolean doHurtTarget(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(this.random.nextBoolean() ? ANIMATION_BITE : (this.random.nextBoolean() ? ANIMATION_SWIPE_L : ANIMATION_SWIPE_R));
      }

      return true;
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new EntityDropBear.AIDropMelee());
      this.goalSelector.addGoal(2, new EntityDropBear.AIUpsideDownWander());
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, LivingEntity.class, 30.0F));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[]{EntityDropBear.class}));
      this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, true) {
         @Override
         protected AABB getTargetSearchArea(double targetDistance) {
            AABB bb = this.mob.getBoundingBox().inflate(targetDistance, targetDistance, targetDistance);
            return new AABB(bb.minX, 0.0, bb.minZ, bb.maxX, 256.0, bb.maxZ);
         }
      });
      this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, AbstractVillager.class, true) {
         @Override
         protected AABB getTargetSearchArea(double targetDistance) {
            AABB bb = this.mob.getBoundingBox().inflate(targetDistance, targetDistance, targetDistance);
            return new AABB(bb.minX, 0.0, bb.minZ, bb.maxX, 256.0, bb.maxZ);
         }
      });
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return super.isInvulnerableTo(source) || source.is(DamageTypeTags.IS_FALL) || source.is(DamageTypes.IN_WALL);
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
      super.checkFallDamage(y, onGroundIn, state, pos);
   }

   protected void playBlockFallSound() {
      this.onLand();
      super.playBlockFallSound();
   }

   private void switchNavigator(boolean rightsideUp) {
      if (rightsideUp) {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigatorWide(this, this.level());
         this.isUpsideDownNavigator = false;
      } else {
         this.moveControl = new FlightMoveController(this, 1.1F, false);
         this.navigation = new DirectPathNavigator(this, this.level());
         this.isUpsideDownNavigator = true;
      }
   }

   public void tick() {
      super.tick();
      AnimationHandler.INSTANCE.updateAnimations(this);
      this.prevUpsideDownProgress = this.upsideDownProgress;
      if (this.isUpsideDown() && this.upsideDownProgress < 5.0F) {
         this.upsideDownProgress++;
      }

      if (!this.isUpsideDown() && this.upsideDownProgress > 0.0F) {
         this.upsideDownProgress--;
      }

      if (!this.level().isClientSide()) {
         BlockPos abovePos = this.getPositionAbove();
         BlockState aboveState = this.level().getBlockState(abovePos);
         BlockState belowState = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement());
         BlockPos worldHeight = this.level().getHeightmapPos(Types.MOTION_BLOCKING, this.blockPosition());
         boolean validAboveState = aboveState.isFaceSturdy(this.level(), abovePos, Direction.DOWN);
         boolean validBelowState = belowState.isFaceSturdy(this.level(), this.getBlockPosBelowThatAffectsMyMovement(), Direction.UP);
         LivingEntity attackTarget = this.getTarget();
         if (attackTarget != null && this.distanceTo(attackTarget) < attackTarget.getBbWidth() + this.getBbWidth() + 1.0F && this.hasLineOfSight(attackTarget)) {
            if (this.getAnimationTick() == 6) {
               if (this.getAnimation() == ANIMATION_BITE) {
                  float yRotRad = this.getYRot() * 0.017453292F;
                  AMCompat.knockback(attackTarget, 0.5, Mth.sin(yRotRad), -Mth.cos(yRotRad));
                  this.getTarget().hurt(this.damageSources().mobAttack(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
               }
            } else if (this.getAnimationTick() == 9) {
               if (this.getAnimation() == ANIMATION_SWIPE_L) {
                  float rot = this.getYRot() + 90.0F;
                  float rotRad = rot * 0.017453292F;
                  AMCompat.knockback(attackTarget, 0.5, Mth.sin(rotRad), -Mth.cos(rotRad));
                  this.getTarget().hurt(this.damageSources().mobAttack(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
               } else if (this.getAnimation() == ANIMATION_SWIPE_R) {
                  float rot = this.getYRot() - 90.0F;
                  float rotRad = rot * 0.017453292F;
                  AMCompat.knockback(attackTarget, 0.5, Mth.sin(rotRad), -Mth.cos(rotRad));
                  this.getTarget().hurt(this.damageSources().mobAttack(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
               }
            }
         }

         if ((attackTarget == null || attackTarget != null && !attackTarget.isAlive())
            && this.random.nextInt(300) == 0
            && this.onGround()
            && !this.isUpsideDown()
            && this.getY() + 2.0 < worldHeight.getY()
            && this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_JUMPUP);
         }

         if (this.jumpingUp && this.getY() > worldHeight.getY()) {
            this.jumpingUp = false;
         }

         if (this.onGround() && this.getAnimation() == ANIMATION_JUMPUP && this.getAnimationTick() > 10
            || this.jumpingUp && this.getAnimation() == NO_ANIMATION) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 2.0, 0.0));
            this.jumpingUp = true;
         }

         if (this.isUpsideDown()) {
            this.jumpingUp = false;
            this.setNoGravity(!this.onGround());
            float f = 0.91F;
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9100000262260437, 1.0, 0.9100000262260437));
            if (!this.verticalCollision) {
               if (!this.onGround() && !validBelowState && this.upwardsFallingTicks <= 5) {
                  if (!validAboveState) {
                     this.upwardsFallingTicks++;
                  }

                  this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.20000000298023224, 0.0));
               } else {
                  this.setUpsideDown(false);
                  this.upwardsFallingTicks = 0;
               }
            } else {
               this.upwardsFallingTicks = 0;
            }

            if (this.horizontalCollision) {
               this.upwardsFallingTicks = 0;
               this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.30000001192092896, 0.0));
            }

            if (this.isInWall() && this.level().isEmptyBlock(this.getBlockPosBelowThatAffectsMyMovement())) {
               this.setPos(this.getX(), this.getY() - 1.0, this.getZ());
            }
         } else {
            this.setNoGravity(false);
            if (validAboveState) {
               this.setUpsideDown(true);
            }
         }

         if (this.isUpsideDown()) {
            if (!this.isUpsideDownNavigator) {
               this.switchNavigator(false);
            }
         } else if (this.isUpsideDownNavigator) {
            this.switchNavigator(true);
         }
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(UPSIDE_DOWN, false);
   }

   public boolean isUpsideDown() {
      return (Boolean)this.entityData.get(UPSIDE_DOWN);
   }

   public void setUpsideDown(boolean upsideDown) {
      this.entityData.set(UPSIDE_DOWN, upsideDown);
   }

   protected BlockPos getPositionAbove() {
      return new BlockPos((int)this.position().x, (int)(this.getBoundingBox().maxY + 0.5000001), (int)this.position().z);
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
   public Animation getAnimation() {
      return this.currentAnimation;
   }

   @Override
   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   @Override
   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_BITE, ANIMATION_SWIPE_L, ANIMATION_SWIPE_R, ANIMATION_JUMPUP};
   }

   private boolean hasLineOfSightBlock(BlockPos destinationBlock) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      Vec3 blockVec = Vec3.atCenterOf(destinationBlock);
      BlockHitResult result = this.level().clip(new ClipContext(Vector3d, blockVec, Block.COLLIDER, Fluid.NONE, this));
      return result.getBlockPos().equals(destinationBlock);
   }

   private void doInitialPosing(LevelAccessor world) {
      BlockPos upperPos = this.getPositionAbove().above();
      BlockPos highest = getLowestPos(world, upperPos);
      this.setPos(highest.getX() + 0.5F, highest.getY(), highest.getZ() + 0.5F);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      if (reason == MobSpawnType.NATURAL) {
         this.doInitialPosing(worldIn);
      }

      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   private void onLand() {
      if (!this.level().isClientSide()) {
         this.level().broadcastEntityEvent(this, (byte)39);

         for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.5))) {
            if (!this.isAlliedTo(entity) && !(entity instanceof EntityDropBear) && entity != this) {
               entity.hurt(this.damageSources().mobAttack(this), 2.0F + this.random.nextFloat() * 5.0F);
               this.launch(entity, true);
            }
         }
      }
   }

   private void launch(Entity e, boolean huge) {
      if (e.onGround()) {
         double d0 = e.getX() - this.getX();
         double d1 = e.getZ() - this.getZ();
         double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
         float f = 0.5F;
         e.push(d0 / d2 * 0.5, huge ? 0.5 : 0.20000000298023224, d1 / d2 * 0.5);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      if (id == 39) {
         this.spawnGroundEffects();
      } else {
         super.handleEntityEvent(id);
      }
   }

   public void spawnGroundEffects() {
      float radius = 2.3F;
      if (this.level().isClientSide()) {
         for (int i1 = 0; i1 < 20 + this.random.nextInt(12); i1++) {
            double motionX = this.getRandom().nextGaussian() * 0.07;
            double motionY = this.getRandom().nextGaussian() * 0.07;
            double motionZ = this.getRandom().nextGaussian() * 0.07;
            float angle = 0.017453292F * this.yBodyRot + i1;
            double extraX = radius * Mth.sin(3.1415927F + angle);
            double extraY = 0.800000011920929;
            double extraZ = radius * Mth.cos(angle);
            BlockPos ground = this.getGroundPosition(new BlockPos(Mth.floor(this.getX() + extraX), (int)this.getY(), Mth.floor(this.getZ() + extraZ)));
            BlockState state = this.level().getBlockState(ground);
            if (!state.isAir()) {
               AMCompat.addParticle(
                  this.level(),
                  new BlockParticleOption(ParticleTypes.BLOCK, state),
                  true,
                  this.getX() + extraX,
                  ground.getY() + extraY,
                  this.getZ() + extraZ,
                  motionX,
                  motionY,
                  motionZ
               );
            }
         }
      }
   }

   private BlockPos getGroundPosition(BlockPos in) {
      BlockPos position = new BlockPos(in.getX(), (int)this.getY(), in.getZ());

      while (position.getY() > 2 && this.level().isEmptyBlock(position) && this.level().getFluidState(position).isEmpty()) {
         position = position.below();
      }

      return position;
   }

   private class AIDropMelee extends Goal {
      private boolean prevOnGround = false;

      public AIDropMelee() {
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         return EntityDropBear.this.getTarget() != null;
      }

      public void tick() {
         LivingEntity target = EntityDropBear.this.getTarget();
         if (target != null) {
            double dist = EntityDropBear.this.distanceTo(target);
            if (EntityDropBear.this.isUpsideDown()) {
               double d0 = EntityDropBear.this.getX() - target.getX();
               double d2 = EntityDropBear.this.getZ() - target.getZ();
               double xzDistSqr = d0 * d0 + d2 * d2;
               BlockPos ceilingPos = new BlockPos(
                  (int)target.getX(), (int)(EntityDropBear.this.getY() - 3.0 - EntityDropBear.this.random.nextInt(3)), (int)target.getZ()
               );
               BlockPos lowestPos = EntityDropBear.getLowestPos(EntityDropBear.this.level(), ceilingPos);
               EntityDropBear.this.getMoveControl().setWantedPosition(lowestPos.getX() + 0.5F, ceilingPos.getY(), lowestPos.getZ() + 0.5F, 1.1);
               if (xzDistSqr < 2.5) {
                  EntityDropBear.this.setUpsideDown(false);
               }
            } else if (EntityDropBear.this.onGround()) {
               EntityDropBear.this.getNavigation().moveTo(target, 1.2);
            }

            if (dist < 3.0) {
               AMCompat.doHurtTarget(EntityDropBear.this, target);
            }
         }
      }
   }

   class AIUpsideDownWander extends RandomStrollGoal {
      public AIUpsideDownWander() {
         super(EntityDropBear.this, 1.0, 50);
      }

      @Nullable
      protected Vec3 getPosition() {
         if (EntityDropBear.this.isUpsideDown()) {
            for (int i = 0; i < 15; i++) {
               Random rand = new Random();
               BlockPos randPos = EntityDropBear.this.blockPosition().offset(rand.nextInt(16) - 8, -2, rand.nextInt(16) - 8);
               BlockPos lowestPos = EntityDropBear.getLowestPos(EntityDropBear.this.level(), randPos);
               if (EntityDropBear.this.level().getBlockState(lowestPos).isFaceSturdy(EntityDropBear.this.level(), lowestPos, Direction.DOWN)) {
                  return Vec3.atCenterOf(lowestPos);
               }
            }

            return null;
         } else {
            return super.getPosition();
         }
      }

      public boolean canUse() {
         return super.canUse();
      }

      public boolean canContinueToUse() {
         if (EntityDropBear.this.isUpsideDown()) {
            double d0 = EntityDropBear.this.getX() - this.wantedX;
            double d2 = EntityDropBear.this.getZ() - this.wantedZ;
            double d4 = d0 * d0 + d2 * d2;
            return d4 > 4.0;
         } else {
            return super.canContinueToUse();
         }
      }

      public void stop() {
         super.stop();
         this.wantedX = 0.0;
         this.wantedY = 0.0;
         this.wantedZ = 0.0;
      }

      public void start() {
         if (EntityDropBear.this.isUpsideDown()) {
            this.mob.getMoveControl().setWantedPosition(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier * 0.699999988079071);
         } else {
            this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
         }
      }
   }
}
