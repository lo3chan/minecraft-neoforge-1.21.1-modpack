package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.server.entity.collision.ICustomCollisions;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexthe666.alexsmobs.entity.ai.MovementControllerCustomCollisions;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EntityRockyRoller extends Monster implements ICustomCollisions {
   private static final EntityDataAccessor<Boolean> ROLLING = SynchedEntityData.defineId(EntityRockyRoller.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(EntityRockyRoller.class, EntityDataSerializers.BOOLEAN);
   public float rollProgress;
   public float prevRollProgress;
   public int rollCounter = 0;
   public float clientRoll = 0.0F;
   private int maxRollTime = 50;
   private Vec3 rollDelta;
   private float rollYRot;
   private int rollCooldown = 0;
   private int earthquakeCooldown = 0;
   private static final double MAX_PUSH_IMPULSE = 2.0;

   protected EntityRockyRoller(EntityType<? extends Monster> monster, Level level) {
      super(monster, level);
      this.xpReward = 8;
      this.moveControl = new MovementControllerCustomCollisions(this);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.rockyRollerSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static boolean checkRockyRollerSpawnRules(
      EntityType<? extends Monster> animal, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return worldIn.getDifficulty() != Difficulty.PEACEFUL
         && isDarkEnoughToSpawn(worldIn, pos, random)
         && (worldIn.getBlockState(pos.below()).is(AMTagRegistry.ROCKY_ROLLER_SPAWNS) || worldIn.getBlockState(pos.below()).isSolid());
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 10.0)
         .add(Attributes.ARMOR, 20.0)
         .add(Attributes.FOLLOW_RANGE, 20.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.699999988079071)
         .add(Attributes.ATTACK_DAMAGE, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new EntityRockyRoller.Navigator(this, worldIn);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new EntityRockyRoller.AIMelee());
      this.goalSelector.addGoal(2, new EntityRockyRoller.AIRollIdle(this));
      this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8));
      this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, AbstractVillager.class, false, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, false, true));
      this.targetSelector.addGoal(3, new HurtByTargetGoal(this, new Class[0]));
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(ANGRY, false);
      builder.define(ROLLING, false);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.ROCKY_ROLLER_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.ROCKY_ROLLER_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.ROCKY_ROLLER_HURT.get();
   }

   public void tick() {
      super.tick();
      this.prevRollProgress = this.rollProgress;
      if (this.isRolling()) {
         if (this.rollProgress < 5.0F) {
            this.rollProgress++;
         }
      } else if (this.rollProgress > 0.0F) {
         this.rollProgress--;
      }

      if (!this.level().isClientSide()) {
         this.setAngry(this.getTarget() != null && this.getTarget().isAlive() && this.distanceToSqr(this.getTarget()) < 400.0);
      }

      if (this.isRolling() && this.rollCooldown <= 0) {
         this.handleRoll();
         if (this.isAngry() && this.isAlive()) {
            for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.30000001192092896))) {
               if (!this.isAlliedTo(entity) && entity != this) {
                  entity.hurt(this.damageSources().mobAttack(this), (this.isTarget(entity) ? 5.0F : 2.0F) + this.random.nextFloat() * 2.0F);
                  this.launch(entity, this.isTarget(entity));
                  if (this.isTarget(entity)) {
                     this.maxRollTime = this.rollCounter + 10;
                  }
               }
            }
         }

         if (this.rollCounter > 2 && !this.isMoving() || !this.isAlive()) {
            this.setRolling(false);
         }

         AMCompat.setMaxUpStep(this, 1.0F);
      } else {
         AMCompat.setMaxUpStep(this, 0.66F);
         this.rollCounter = 0;
      }

      if (this.rollCooldown > 0) {
         this.rollCooldown--;
      }

      if (this.earthquakeCooldown > 0) {
         this.earthquakeCooldown--;
      }
   }

   private boolean isMoving() {
      return this.getDeltaMovement().lengthSqr() > 0.02;
   }

   private void earthquake() {
      boolean flag = false;

      for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(6.0, 8.0, 6.0))) {
         if (!(e instanceof EntityRockyRoller) && e.isAlive()) {
            e.addEffect(new MobEffectInstance(AMCompat.effect(AMEffectRegistry.EARTHQUAKE.get()), 20, 0, false, false, true));
            flag = true;
         }
      }

      if (!this.level().canSeeSky(this.blockPosition()) && AMCompat.gameRule(this.level(), AMCompat.Rule.MOB_GRIEFING)) {
         BlockPos ceil = this.blockPosition().offset(0, 2, 0);

         while (
            (!this.level().getBlockState(ceil).isSolid() || this.level().getBlockState(ceil).getBlock() == Blocks.POINTED_DRIPSTONE)
               && ceil.getY() < AMCompat.maxBuildHeight(this.level())
         ) {
            ceil = ceil.above();
         }

         int i = 2 + this.random.nextInt(2);
         int j = 2 + this.random.nextInt(2);
         int k = 2 + this.random.nextInt(2);
         float f = (i + j + k) * 0.333F + 0.5F;
         double fTimesF = f * f;

         for (BlockPos blockpos1 : BlockPos.betweenClosed(ceil.offset(-i, -j, -k), ceil.offset(i, j, k))) {
            if (blockpos1.distSqr(ceil) <= fTimesF && this.level().getBlockState(blockpos1).getBlock() instanceof Fallable) {
               if (!this.isHangingDripstone(blockpos1)) {
                  this.level().scheduleTick(blockpos1, this.level().getBlockState(blockpos1).getBlock(), 2);
               } else {
                  while (this.isHangingDripstone(blockpos1.above()) && blockpos1.getY() < AMCompat.maxBuildHeight(this.level())) {
                     blockpos1 = blockpos1.above();
                  }

                  if (this.isHangingDripstone(blockpos1)) {
                     Vec3 vec3 = Vec3.atBottomCenterOf(blockpos1);
                     FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(
                        this.level(), new BlockPos((int)vec3.x, (int)vec3.y, (int)vec3.z), this.level().getBlockState(blockpos1)
                     );
                     this.level().destroyBlock(blockpos1, false);
                     this.level().addFreshEntity(fallingblockentity);
                  }
               }

               flag = true;
            }
         }
      }

      if (flag) {
         this.gameEvent(AMPlatform.ENTITY_ACTION);
         this.playSound(AMSoundRegistry.ROCKY_ROLLER_EARTHQUAKE.get(), this.getSoundVolume(), this.getVoicePitch());
      }
   }

   private boolean isHangingDripstone(BlockPos pos) {
      return this.level().getBlockState(pos).getBlock() instanceof PointedDripstoneBlock
         && this.level().getBlockState(pos).getValue(PointedDripstoneBlock.TIP_DIRECTION) == Direction.DOWN;
   }

   private boolean isTarget(Entity entity) {
      return this.getTarget() != null && this.getTarget().is(entity);
   }

   public boolean isRolling() {
      return (Boolean)this.entityData.get(ROLLING);
   }

   public void setRolling(boolean rolling) {
      this.entityData.set(ROLLING, rolling);
   }

   public boolean isAngry() {
      return (Boolean)this.entityData.get(ANGRY);
   }

   public void setAngry(boolean angry) {
      this.entityData.set(ANGRY, angry);
   }

   private void handleRoll() {
      this.rollCounter++;
      if (!this.level().isClientSide()) {
         if (this.horizontalCollision && this.earthquakeCooldown == 0 & this.isAngry()) {
            this.earthquakeCooldown = this.maxRollTime;
            this.earthquake();
         }

         if (this.rollCounter > this.maxRollTime) {
            this.setRolling(false);
            this.rollCooldown = 10 + this.random.nextInt(10);
            this.rollCounter = 0;
            this.setDeltaMovement(Vec3.ZERO);
         } else {
            Vec3 vec3 = this.getDeltaMovement();
            if (this.rollCounter == 1) {
               float f = this.getYRot() * 0.017453292F;
               float f1 = this.isBaby() ? 0.2F : 0.35F;
               this.rollYRot = this.getYRot();
               this.rollDelta = new Vec3(vec3.x + -Mth.sin(f) * f1, 0.0, vec3.z + Mth.cos(f) * f1);
               this.setDeltaMovement(this.rollDelta.add(0.0, 0.27, 0.0));
            } else {
               this.setYRot(this.rollYRot);
               this.setYHeadRot(this.rollYRot);
               this.setYBodyRot(this.rollYRot);
               this.setDeltaMovement(this.rollDelta.x, vec3.y, this.rollDelta.z);
            }
         }
      }
   }

   private void rollFor(int time) {
      if (this.rollCooldown == 0) {
         this.maxRollTime = time;
         this.earthquakeCooldown = 0;
         this.setRolling(true);
      }
   }

   private void launch(Entity e, boolean huge) {
      if (e.onGround()) {
         double d0 = e.getX() - this.getX();
         double d1 = e.getZ() - this.getZ();
         double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
         float f = huge ? 1.0F : 0.35F;
         e.push(d0 / d2 * f, huge ? 0.5 : 0.20000000298023224, d1 / d2 * f);
      }
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.equals("fallingStalactite") || super.isInvulnerableTo(source);
   }

   public int getMaxFallDistance() {
      return super.getMaxFallDistance() * 2;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public boolean canBeCollidedWith() {
      return AMCompat.isFullyConstructed(this) && this.isAlive();
   }

   public void push(Entity entity) {
      if (entity instanceof EntityRockyRoller) {
         super.push(entity);
      } else {
         Vec3 impulse = this.getDeltaMovement();
         double length = impulse.length();
         if (length > 2.0) {
            impulse = impulse.scale(2.0 / length);
         }

         entity.setDeltaMovement(entity.getDeltaMovement().add(impulse));
      }
   }

   @Override
   public boolean canPassThrough(BlockPos blockPos, BlockState blockstate, VoxelShape voxelShape) {
      return blockstate.getBlock() instanceof PointedDripstoneBlock;
   }

   public boolean isColliding(BlockPos pos, BlockState blockstate) {
      return !(blockstate.getBlock() instanceof PointedDripstoneBlock) && super.isColliding(pos, blockstate);
   }

   public Vec3 collide(Vec3 vec3) {
      return ICustomCollisions.getAllowedMovementForEntity(this, vec3);
   }

   public boolean hurt(DamageSource dmg, float amount) {
      if (!this.isMoving()
         && !dmg.is(DamageTypes.MAGIC)
         && dmg.getDirectEntity() instanceof LivingEntity livingentity
         && !(livingentity instanceof EntityRockyRoller)
         && !dmg.is(DamageTypes.EXPLOSION)
         && !livingentity.hurtMarked) {
         livingentity.hurt(this.damageSources().thorns(this), 2.0F);
      }

      return super.hurt(dmg, amount);
   }

   private class AIMelee extends Goal {
      private BlockPos rollFromPos = null;
      private int rollTimeout = 0;

      public AIMelee() {
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         return EntityRockyRoller.this.getTarget() != null && EntityRockyRoller.this.getTarget().isAlive() && !EntityRockyRoller.this.isRolling();
      }

      public void tick() {
         LivingEntity enemy = EntityRockyRoller.this.getTarget();
         double d0 = this.validRollDistance(enemy);
         double distToEnemySqr = EntityRockyRoller.this.distanceTo(enemy);
         if (this.rollFromPos == null
            || enemy.distanceToSqr(this.rollFromPos.getX() + 0.5F, this.rollFromPos.getY() + 0.5F, this.rollFromPos.getZ() + 0.5) > 60.0
            || !this.canEntitySeePosition(enemy, this.rollFromPos)) {
            this.rollFromPos = this.getRollAtPosition(enemy);
         }

         EntityRockyRoller.this.lookAt(enemy, 100.0F, 5.0F);
         if (this.rollTimeout < 40
            && this.rollFromPos != null
            && distToEnemySqr <= d0
            && EntityRockyRoller.this.distanceToSqr(this.rollFromPos.getX() + 0.5F, this.rollFromPos.getY() + 0.5F, this.rollFromPos.getZ() + 0.5) > 2.25) {
            EntityRockyRoller.this.getNavigation().moveTo(this.rollFromPos.getX() + 0.5F, this.rollFromPos.getY() + 0.5F, this.rollFromPos.getZ() + 0.5F, 1.6);
            this.rollTimeout++;
         } else {
            double d1 = enemy.getX() - EntityRockyRoller.this.getX();
            double d2 = enemy.getZ() - EntityRockyRoller.this.getZ();
            float f = (float)(Mth.atan2(d2, d1) * 57.2957763671875) - 90.0F;
            EntityRockyRoller.this.setYRot(f);
            EntityRockyRoller.this.yBodyRot = f;
            EntityRockyRoller.this.rollFor(30 + EntityRockyRoller.this.random.nextInt(40));
         }
      }

      public void stop() {
         super.stop();
         this.rollTimeout = 0;
      }

      protected double validRollDistance(LivingEntity attackTarget) {
         return 3.0F + attackTarget.getBbWidth();
      }

      private boolean canEntitySeePosition(LivingEntity entity, BlockPos destinationBlock) {
         Vec3 Vector3d = new Vec3(entity.getX(), entity.getY() + 0.5, entity.getZ());
         Vec3 blockVec = Vec3.atCenterOf(destinationBlock);
         BlockHitResult result = entity.level().clip(new ClipContext(Vector3d, blockVec, Block.COLLIDER, Fluid.NONE, entity));
         return result != null
            && (result.getBlockPos().equals(destinationBlock) || entity.level().getBlockState(result.getBlockPos()).getBlock() == Blocks.POINTED_DRIPSTONE);
      }

      public BlockPos getRollAtPosition(Entity target) {
         float radius = EntityRockyRoller.this.getRandom().nextInt(2) + 6 + target.getBbWidth();
         int orbit = EntityRockyRoller.this.getRandom().nextInt(360);
         float angle = 0.017453292F * orbit;
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         BlockPos circlePos = new BlockPos((int)(target.getX() + extraX), (int)target.getEyeY(), (int)(target.getZ() + extraZ));

         while (!EntityRockyRoller.this.level().getBlockState(circlePos).isAir() && circlePos.getY() < AMCompat.maxBuildHeight(EntityRockyRoller.this.level())) {
            circlePos = circlePos.above();
         }

         while (
            !EntityRockyRoller.this.level()
                  .getBlockState(circlePos.below())
                  .entityCanStandOn(EntityRockyRoller.this.level(), circlePos.below(), EntityRockyRoller.this)
               && circlePos.getY() > 1
         ) {
            circlePos = circlePos.below();
         }

         return EntityRockyRoller.this.getWalkTargetValue(circlePos) > -1.0F ? circlePos : null;
      }
   }

   class AIRollIdle extends Goal {
      EntityRockyRoller rockyRoller;

      public AIRollIdle(EntityRockyRoller p_29328_) {
         this.rockyRoller = p_29328_;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
      }

      public boolean canUse() {
         if (!this.rockyRoller.onGround()) {
            return false;
         } else if (!this.rockyRoller.isRolling()
            && this.rockyRoller.rollCooldown <= 0
            && (this.rockyRoller.getTarget() == null || !this.rockyRoller.getTarget().isAlive())) {
            float f = this.rockyRoller.getYRot() * 0.017453292F;
            int i = 0;
            int j = 0;
            float f1 = -Mth.sin(f);
            float f2 = Mth.cos(f);
            if (Math.abs(f1) > 0.5) {
               i = (int)(i + f1 / Math.abs(f1));
            }

            if (Math.abs(f2) > 0.5) {
               j = (int)(j + f2 / Math.abs(f2));
            }

            return this.rockyRoller.level().getBlockState(this.rockyRoller.blockPosition().offset(i, -1, j)).isAir();
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void start() {
         this.rockyRoller.rollFor(30 + EntityRockyRoller.this.random.nextInt(30));
      }

      public boolean isInterruptable() {
         return false;
      }
   }

   static class Navigator extends AdvancedPathNavigateNoTeleport {
      public Navigator(Mob mob, Level world) {
         super(mob, world, true);
      }

      @Override
      protected PathFinder createPathFinder(int i) {
         this.nodeEvaluator = new EntityRockyRoller.RockyRollerNodeEvaluator();
         return new PathFinder(this.nodeEvaluator, i);
      }
   }

   static class RockyRollerNodeEvaluator extends WalkNodeEvaluator {
      public PathType getPathType(PathfindingContext context, int x, int y, int z) {
         BlockPos pos = new BlockPos(x, y, z);
         return context.getBlockState(pos).getBlock() instanceof PointedDripstoneBlock ? PathType.OPEN : super.getPathType(context, x, y, z);
      }
   }
}
