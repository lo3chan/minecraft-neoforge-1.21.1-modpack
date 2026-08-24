package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

public class EntitySkreecher extends Monster {
   public static final float MAX_DIST_TO_CEILING = 4.0F;
   private static final EntityDataAccessor<Boolean> CLINGING = SynchedEntityData.defineId(EntitySkreecher.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> JUMPING_UP = SynchedEntityData.defineId(EntitySkreecher.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> CLAPPING = SynchedEntityData.defineId(EntitySkreecher.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> DIST_TO_CEILING = SynchedEntityData.defineId(EntitySkreecher.class, EntityDataSerializers.FLOAT);
   protected static final EntityDimensions GROUND_SIZE = EntityDimensions.scalable(0.99F, 1.35F);
   public float prevClingProgress;
   public float clingProgress;
   public float prevClapProgress;
   public float clapProgress;
   public float prevDistanceToCeiling;
   private int clapTick = 0;
   private int clingCooldown = 0;
   private boolean isUpsideDownNavigator;
   private boolean hasAttemptedWardenSpawning;
   private boolean hasGroundSize = false;

   protected EntitySkreecher(EntityType<? extends Monster> type, Level level) {
      super(type, level);
      this.switchNavigator(false);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Warden.class, 6.0F, 1.0, 1.2));
      this.goalSelector.addGoal(2, new EntitySkreecher.FollowTargetGoal());
      this.goalSelector.addGoal(3, new EntitySkreecher.WanderUpsideDownGoal());
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, LivingEntity.class, 30.0F));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, true) {
         @Override
         protected AABB getTargetSearchArea(double targetDistance) {
            AABB bb = this.mob.getBoundingBox().inflate(16.0, 1.0, 16.0);
            return new AABB(bb.minX, -64.0, bb.minZ, bb.maxX, 320.0, bb.maxZ);
         }
      });
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.skreecherSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static boolean checkSkreecherSpawnRules(
      EntityType<? extends Monster> animal, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      boolean isOnSculk = worldIn.getBlockState(pos.below()).is(Blocks.SCULK);
      return worldIn.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(worldIn, pos, random) && isOnSculk;
   }

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   private void switchNavigator(boolean clinging) {
      if (clinging) {
         this.moveControl = new EntitySkreecher.MoveController();
         this.navigation = this.createScreecherNavigation(this.level());
         this.isUpsideDownNavigator = true;
      } else {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigation(this, this.level());
         this.isUpsideDownNavigator = false;
      }
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 2.0)
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.MOVEMENT_SPEED, 0.20000000298023224)
         .add(Attributes.FOLLOW_RANGE, 64.0);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DIST_TO_CEILING, 0.0F);
      builder.define(CLINGING, false);
      builder.define(JUMPING_UP, false);
      builder.define(CLAPPING, false);
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.SKREECHER_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.SKREECHER_HURT.get();
   }

   public boolean hurt(DamageSource source, float value) {
      this.setClinging(false);
      this.setClapping(false);
      this.clingCooldown = 200 + this.random.nextInt(200);
      return super.hurt(source, value);
   }

   public void tick() {
      super.tick();
      this.prevClapProgress = this.clapProgress;
      this.prevClingProgress = this.clingProgress;
      this.prevDistanceToCeiling = this.getDistanceToCeiling();
      boolean clingVisually = this.isClinging() || this.isJumpingUp() || this.jumping;
      if (clingVisually && this.clingProgress < 5.0F) {
         this.clingProgress++;
      }

      if (!clingVisually && this.clingProgress > 0.0F && this.getDistanceToCeiling() == 0.0F) {
         this.clingProgress--;
      }

      boolean clapping = this.isClapping();
      if (clapping) {
         if (this.clapProgress < 5.0F) {
            this.clapProgress++;
         }
      } else if (this.clapProgress > 0.0F) {
         this.clapProgress--;
      }

      if (!this.level().isClientSide()) {
         float technicalDistToCeiling = this.calculateDistanceToCeiling();
         float gap = Math.max(technicalDistToCeiling - this.getDistanceToCeiling(), 0.0F);
         if (this.isClinging()) {
            this.setNoGravity(true);
            if (technicalDistToCeiling > 4.0F || !this.isAlive() || this.clingCooldown > 0 || AMPlatform.isInAnyFluid(this)) {
               this.setClinging(false);
            }

            float goal = Math.min(technicalDistToCeiling, 4.0F);
            if (this.getDistanceToCeiling() < goal) {
               this.setDistanceToCeiling(Math.min(goal, this.prevDistanceToCeiling + 0.15F));
            }

            if (this.getDistanceToCeiling() > goal) {
               this.setDistanceToCeiling(Math.max(goal, this.prevDistanceToCeiling - 0.15F));
            }

            if (this.getDistanceToCeiling() < 1.0F) {
               gap = -0.03F;
            }

            this.setDeltaMovement(this.getDeltaMovement().add(0.0, gap * 0.5F, 0.0));
         } else {
            this.setNoGravity(false);
            if (technicalDistToCeiling < 4.0F && this.clingCooldown <= 0) {
               this.setClinging(true);
            }

            this.setDistanceToCeiling(Math.max(0.0F, this.prevDistanceToCeiling - 0.5F));
            if (this.onGround()
               && this.clingCooldown <= 0
               && !this.isJumpingUp()
               && this.isAlive()
               && this.random.nextFloat() < 0.0085F
               && technicalDistToCeiling > 4.0F
               && !this.level().canSeeSky(this.blockPosition())) {
               this.setJumpingUp(true);
            }
         }
      }

      if (this.isJumpingUp()) {
         if (this.isAlive() && !this.level().canSeeSky(this.blockPosition()) && (!this.verticalCollision || this.onGround())) {
            this.setDistanceToCeiling(1.5F);
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.20000000298023224, 0.0));

            for (int i = 0; i < 3; i++) {
               this.level()
                  .addParticle(
                     ParticleTypes.SCULK_CHARGE_POP,
                     this.getRandomX(0.5),
                     this.getY() - 0.20000000298023224,
                     this.getRandomZ(0.5),
                     0.0,
                     -0.20000000298023224,
                     0.0
                  );
            }
         } else {
            this.setJumpingUp(false);
         }
      }

      if (this.clingCooldown > 0) {
         this.clingCooldown--;
      }

      if (!this.isAlive() || this.clingCooldown > 0 && this.isClinging()) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.25, 0.0));
      }

      if (this.isClinging() && !this.isUpsideDownNavigator) {
         this.switchNavigator(true);
      }

      if (!this.isClinging() && this.isUpsideDownNavigator) {
         this.switchNavigator(false);
      }

      if (this.isClapping() && this.isAlive() && this.clingCooldown <= 0) {
         float dir = this.isClinging() ? -0.5F : 0.1F;
         if (this.clapTick % 8 == 0) {
            this.playSound(AMSoundRegistry.SKREECHER_CLAP.get(), this.getSoundVolume() * 3.0F, this.getVoicePitch());
            this.gameEvent(AMPlatform.ENTITY_ACTION);
            this.angerAllNearbyWardens();
            this.level().addParticle((ParticleOptions)AMParticleRegistry.SKULK_BOOM.get(), this.getX(), this.getEyeY(), this.getZ(), 0.0, dir, 0.0);
         } else if (this.clapTick % 15 == 0) {
            this.playSound(AMSoundRegistry.SKREECHER_CALL.get(), this.getSoundVolume() * 4.0F, this.getVoicePitch());
         }

         if (this.clapTick >= 100 && !this.hasAttemptedWardenSpawning && AMConfig.skreechersSummonWarden) {
            this.hasAttemptedWardenSpawning = true;
            BlockPos spawnAt = this.blockPosition().below();

            while (spawnAt.getY() > -64 && !this.level().getBlockState(spawnAt).isFaceSturdy(this.level(), spawnAt, Direction.UP)) {
               spawnAt = spawnAt.below();
            }

            Holder<Biome> holder = this.level().getBiome(spawnAt);
            if (!this.level().isClientSide() && this.getNearbyWardens().isEmpty() && holder.is(AMTagRegistry.SKREECHERS_CAN_SPAWN_WARDENS)) {
               Warden warden = AMCompat.create(EntityType.WARDEN, this.level());
               warden.moveTo(this.getX(), spawnAt.getY() + 1, this.getZ(), this.getYRot(), 0.0F);
               warden.finalizeSpawn(
                  (ServerLevel)this.level(), AMCompat.difficultyAt(this.level(), this.blockPosition()), MobSpawnType.TRIGGERED, (SpawnGroupData)null
               );
               warden.setAttackTarget(this);
               warden.increaseAngerAt(this, 79, false);
               this.level().addFreshEntity(warden);
            }
         }

         this.clapTick++;
         if (!this.level().isClientSide()) {
            if (this.getTarget() != null
               && this.getTarget().isAlive()
               && this.hasLineOfSight(this.getTarget())
               && !this.getTarget().hasEffect(MobEffects.INVISIBILITY)
               && !this.hasEffect(MobEffects.BLINDNESS)) {
               double horizDist = this.getTarget().position().subtract(this.position()).horizontalDistance();
               if (horizDist > 20.0) {
                  this.setClapping(false);
               }
            } else {
               this.setClapping(false);
            }
         }
      }

      if (!this.isClinging() && !this.hasGroundSize) {
         this.refreshDimensions();
         this.hasGroundSize = true;
      }

      if (this.isClinging() && this.hasGroundSize) {
         this.refreshDimensions();
         this.hasGroundSize = false;
      }
   }

   public boolean dampensVibrations() {
      return true;
   }

   public void angerAllNearbyWardens() {
      for (Warden warden : this.getNearbyWardens()) {
         if (warden.hasLineOfSight(this)) {
            warden.increaseAngerAt(this, 100, false);
         }
      }
   }

   private List<Warden> getNearbyWardens() {
      AABB angerBox = new AABB(
         this.getX() - 35.0, this.getY() + (this.isClinging() ? 5.0F : 25.0F), this.getZ() - 35.0, this.getX() + 35.0, -64.0, this.getZ() + 35.0
      );
      return this.level().getEntitiesOfClass(Warden.class, angerBox);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Clinging", this.isClinging());
      compound.putDouble("CeilDist", this.getDistanceToCeiling());
      compound.putBoolean("SummonedWarden", this.hasAttemptedWardenSpawning);
      compound.putInt("ClingCooldown", this.clingCooldown);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setClinging(AMCompat.getBoolean(compound, "Clinging"));
      this.setDistanceToCeiling((float)AMCompat.getDouble(compound, "CeilDist"));
      this.hasAttemptedWardenSpawning = AMCompat.getBoolean(compound, "SummonedWarden");
      this.clingCooldown = AMCompat.getInt(compound, "ClingCooldown");
   }

   public EntityDimensions getDefaultDimensions(Pose poseIn) {
      return this.isClinging() ? super.getDefaultDimensions(poseIn) : GROUND_SIZE.scale(this.getScale());
   }

   public boolean isClinging() {
      return (Boolean)this.entityData.get(CLINGING);
   }

   public void setClinging(boolean upsideDown) {
      this.entityData.set(CLINGING, upsideDown);
   }

   public boolean isClapping() {
      return (Boolean)this.entityData.get(CLAPPING);
   }

   public void setClapping(boolean clapping) {
      this.entityData.set(CLAPPING, clapping);
      if (!clapping) {
         this.clapTick = 0;
      }
   }

   public boolean isJumpingUp() {
      return (Boolean)this.entityData.get(JUMPING_UP);
   }

   public void setJumpingUp(boolean jumping) {
      this.entityData.set(JUMPING_UP, jumping);
   }

   protected BlockPos getPositionAbove(float height) {
      return AMBlockPos.fromCoords(this.position().x, this.getBoundingBox().maxY + height + 0.5000001, this.position().z);
   }

   protected PathNavigation createScreecherNavigation(Level level) {
      FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level) {
         public boolean isStableDestination(BlockPos pos) {
            int airAbove;
            for (airAbove = 0; EntitySkreecher.this.level().getBlockState(pos).isAir() && airAbove < 6.0F; airAbove++) {
               pos = pos.above();
            }

            return airAbove < Math.min(4.0F, (float)EntitySkreecher.this.random.nextInt(4));
         }
      };
      flyingpathnavigation.setCanOpenDoors(false);
      flyingpathnavigation.setCanFloat(false);
      return flyingpathnavigation;
   }

   private float calculateDistanceToCeiling() {
      BlockPos ceiling = this.getCeilingOf(this.blockPosition());
      return (float)(ceiling.getY() - this.getBoundingBox().maxY);
   }

   private boolean isOpaqueBlockAt(double x, double y, double z) {
      if (this.noPhysics) {
         return false;
      } else {
         double d = 0.30000001192092896;
         Vec3 vec3 = new Vec3(x, y, z);
         AABB axisAlignedBB = AABB.ofSize(vec3, 0.30000001192092896, 1.0E-6, 0.30000001192092896);
         return this.level()
            .getBlockStates(axisAlignedBB)
            .filter(Predicate.not(BlockStateBase::isAir))
            .anyMatch(
               p_185969_ -> {
                  BlockPos blockpos = AMBlockPos.fromVec3(vec3);
                  return p_185969_.isSuffocating(this.level(), blockpos)
                     && Shapes.joinIsNotEmpty(
                        p_185969_.getCollisionShape(this.level(), blockpos).move(vec3.x, vec3.y, vec3.z), Shapes.create(axisAlignedBB), BooleanOp.AND
                     );
               }
            );
      }
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public float getDistanceToCeiling() {
      return (Float)this.entityData.get(DIST_TO_CEILING);
   }

   public void setDistanceToCeiling(float dist) {
      this.entityData.set(DIST_TO_CEILING, dist);
   }

   public void travel(Vec3 travelVector) {
      if (this.isEffectiveAi() && this.isClinging() && !AMPlatform.isInAnyFluid(this)) {
         this.moveRelative(this.getSpeed(), travelVector);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.75));
      } else {
         super.travel(travelVector);
      }
   }

   public BlockPos getCeilingOf(BlockPos usPos) {
      while (!this.level().getBlockState(usPos).isFaceSturdy(this.level(), usPos, Direction.DOWN) && usPos.getY() < AMCompat.maxBuildHeight(this.level())) {
         usPos = usPos.above();
      }

      return usPos;
   }

   private class FollowTargetGoal extends Goal {
      public FollowTargetGoal() {
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         return EntitySkreecher.this.getTarget() != null && EntitySkreecher.this.getTarget().isAlive() && EntitySkreecher.this.clingCooldown <= 0;
      }

      public void start() {
         EntitySkreecher.this.playSound(
            AMSoundRegistry.SKREECHER_DETECT.get(), EntitySkreecher.this.getSoundVolume() * 6.0F, EntitySkreecher.this.getVoicePitch()
         );
      }

      public void tick() {
         LivingEntity target = EntitySkreecher.this.getTarget();
         if (target != null) {
            if (EntitySkreecher.this.isClinging()) {
               BlockPos ceilAbove = EntitySkreecher.this.getCeilingOf(target.blockPosition().above());
               EntitySkreecher.this.getNavigation()
                  .moveTo(target.getX(), ceilAbove.getY() - EntitySkreecher.this.random.nextFloat() * 4.0F, target.getZ(), 1.2000000476837158);
            } else {
               EntitySkreecher.this.getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), 1.0);
            }

            Vec3 vec = target.position().subtract(EntitySkreecher.this.position());
            EntitySkreecher.this.getLookControl().setLookAt(target, 360.0F, 180.0F);
            if (vec.horizontalDistance() < 2.5 && EntitySkreecher.this.clingCooldown == 0) {
               EntitySkreecher.this.setClapping(true);
            }
         }
      }
   }

   class MoveController extends MoveControl {
      private final Mob parentEntity = EntitySkreecher.this;

      public MoveController() {
         super(EntitySkreecher.this);
      }

      public void tick() {
         if (this.operation == Operation.MOVE_TO) {
            Vec3 vector3d = new Vec3(this.wantedX - this.parentEntity.getX(), this.wantedY - this.parentEntity.getY(), this.wantedZ - this.parentEntity.getZ());
            double d0 = vector3d.length();
            double width = this.parentEntity.getBoundingBox().getSize();
            Vec3 vector3d1 = vector3d.scale(this.speedModifier * 0.035 / d0);
            float verticalSpeed = 0.15F;
            this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(vector3d1.multiply(1.0, verticalSpeed, 1.0)));
            if (this.parentEntity.getTarget() != null) {
               double d1 = this.parentEntity.getTarget().getZ() - this.parentEntity.getZ();
               double d3 = this.parentEntity.getTarget().getY() - this.parentEntity.getY();
               double d2 = this.parentEntity.getTarget().getX() - this.parentEntity.getX();
               float f = Mth.sqrt((float)(d2 * d2 + d1 * d1));
               this.parentEntity.setYRot(-((float)Mth.atan2(d2, d1)) * 57.295776F);
               this.parentEntity.setXRot((float)(Mth.atan2(d3, f) * 57.2957763671875));
               this.parentEntity.yBodyRot = this.parentEntity.getYRot();
            } else if (d0 >= width) {
               this.parentEntity.setYRot(-((float)Mth.atan2(vector3d1.x, vector3d1.z)) * 57.295776F);
            }
         }
      }
   }

   class WanderUpsideDownGoal extends RandomStrollGoal {
      private int stillTicks = 0;

      public WanderUpsideDownGoal() {
         super(EntitySkreecher.this, 1.0, 25);
      }

      @Nullable
      protected Vec3 getPosition() {
         if (EntitySkreecher.this.isClinging()) {
            int distance = 16;
            int i = 0;
            if (i < 15) {
               Random rand = new Random();
               BlockPos randPos = EntitySkreecher.this.blockPosition().offset(rand.nextInt(distance * 2) - distance, -4, rand.nextInt(distance * 2) - distance);
               BlockPos lowestPos = EntitySkreecher.this.getCeilingOf(randPos).below(rand.nextInt(4));
               return Vec3.atCenterOf(lowestPos);
            } else {
               return null;
            }
         } else {
            return super.getPosition();
         }
      }

      public boolean canUse() {
         return super.canUse();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse();
      }

      public void stop() {
         super.stop();
         this.wantedX = 0.0;
         this.wantedY = 0.0;
         this.wantedZ = 0.0;
      }

      public void start() {
         this.stillTicks = 0;
         this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
      }
   }
}
