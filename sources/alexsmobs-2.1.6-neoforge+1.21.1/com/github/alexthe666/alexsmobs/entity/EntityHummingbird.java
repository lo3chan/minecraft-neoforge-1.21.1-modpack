package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.BlockHummingbirdFeeder;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.HummingbirdAIPollinate;
import com.github.alexthe666.alexsmobs.entity.ai.HummingbirdAIWander;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.base.Predicates;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityHummingbird extends Animal {
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityHummingbird.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityHummingbird.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> CROPS_POLLINATED = SynchedEntityData.defineId(EntityHummingbird.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Optional<BlockPos>> FEEDER_POS = SynchedEntityData.defineId(
      EntityHummingbird.class, EntityDataSerializers.OPTIONAL_BLOCK_POS
   );
   public float flyProgress;
   public float prevFlyProgress;
   public float movingProgress;
   public float prevMovingProgress;
   public int hummingStill = 0;
   public int pollinateCooldown = 0;
   public int sipCooldown = 0;
   private int loopSoundTick = 0;
   private boolean sippy;
   public float sipProgress;
   public float prevSipProgress;

   protected EntityHummingbird(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.moveControl = new FlightMoveController(this, 1.5F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
      this.setPathfindingMalus(PathType.COCOA, -1.0F);
      this.setPathfindingMalus(PathType.FENCE, -1.0F);
      this.setPathfindingMalus(PathType.LEAVES, 0.0F);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.hummingbirdSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.HUMMINGBIRD_IDLE.get();
   }

   public int getAmbientSoundInterval() {
      return 60;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.HUMMINGBIRD_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.HUMMINGBIRD_HURT.get();
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 4.0)
         .add(Attributes.FLYING_SPEED, 7.0)
         .add(Attributes.ATTACK_DAMAGE, 0.0)
         .add(Attributes.MOVEMENT_SPEED, 0.44999998807907104);
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.HUMMINGBIRD_BREEDABLES);
   }

   public int getMaxSpawnClusterSize() {
      return 7;
   }

   public boolean isMaxGroupSizeReached(int sizeIn) {
      return false;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(2, new TemptGoal(this, 1.0, AMCompat.ingredientOf(AMTagRegistry.HUMMINGBIRD_BREEDABLES), false));
      this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.0));
      this.goalSelector.addGoal(4, new EntityHummingbird.AIUseFeeder(this));
      this.goalSelector.addGoal(4, new HummingbirdAIPollinate(this));
      this.goalSelector.addGoal(5, new HummingbirdAIWander(this, 16, 6, 15, 1.0F));
      this.goalSelector.addGoal(6, new FloatGoal(this));
   }

   protected void playStepSound(BlockPos pos, BlockState blockIn) {
   }

   protected PathNavigation createNavigation(Level worldIn) {
      FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn) {
         public boolean isStableDestination(BlockPos pos) {
            return !this.level.getBlockState(pos.below(2)).isAir();
         }
      };
      flyingpathnavigator.setCanOpenDoors(false);
      flyingpathnavigator.setCanFloat(false);
      flyingpathnavigator.getNodeEvaluator().setCanPassDoors(true);
      return flyingpathnavigator;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   protected boolean makeFlySound() {
      return true;
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      EntityDimensions dimensions = super.getDefaultDimensions(pose);
      return dimensions.withEyeHeight(dimensions.height() * 0.5F);
   }

   public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
      return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Variant", this.getVariant());
      compound.putInt("CropsPollinated", this.getCropsPollinated());
      compound.putInt("PollinateCooldown", this.pollinateCooldown);
      BlockPos blockpos = this.getFeederPos();
      if (blockpos != null) {
         compound.putInt("HLPX", blockpos.getX());
         compound.putInt("HLPY", blockpos.getY());
         compound.putInt("HLPZ", blockpos.getZ());
      }
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setVariant(AMCompat.getInt(compound, "Variant"));
      this.setCropsPollinated(AMCompat.getInt(compound, "CropsPollinated"));
      this.pollinateCooldown = AMCompat.getInt(compound, "PollinateCooldown");
      if (AMCompat.contains(compound, "HLPX")) {
         int i = AMCompat.getInt(compound, "HLPX");
         int j = AMCompat.getInt(compound, "HLPY");
         int k = AMCompat.getInt(compound, "HLPZ");
         this.entityData.set(FEEDER_POS, Optional.of(new BlockPos(i, j, k)));
      } else {
         this.entityData.set(FEEDER_POS, Optional.empty());
      }
   }

   public BlockPos getFeederPos() {
      return (BlockPos)((Optional)this.entityData.get(FEEDER_POS)).orElse(null);
   }

   public void setFeederPos(BlockPos pos) {
      this.entityData.set(FEEDER_POS, Optional.ofNullable(pos));
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FLYING, false);
      builder.define(VARIANT, 0);
      builder.define(CROPS_POLLINATED, 0);
      builder.define(FEEDER_POS, Optional.empty());
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      this.setVariant(this.getRandom().nextInt(3));
      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   private List<BlockPos> getNearbyFeeders(BlockPos blockpos, ServerLevel world, int range) {
      PoiManager pointofinterestmanager = world.getPoiManager();
      Stream<BlockPos> stream = pointofinterestmanager.findAll(
         poiTypeHolder -> poiTypeHolder.is(AMPointOfInterestRegistry.HUMMINGBIRD_FEEDER_KEY), Predicates.alwaysTrue(), blockpos, range, Occupancy.ANY
      );
      return stream.collect(Collectors.toList());
   }

   public boolean isFlying() {
      return (Boolean)this.entityData.get(FLYING);
   }

   public void setFlying(boolean flying) {
      this.entityData.set(FLYING, flying);
   }

   public int getVariant() {
      return (Integer)this.entityData.get(VARIANT);
   }

   public void setVariant(int variant) {
      this.entityData.set(VARIANT, variant);
   }

   public int getCropsPollinated() {
      return (Integer)this.entityData.get(CROPS_POLLINATED);
   }

   public void setCropsPollinated(int crops) {
      this.entityData.set(CROPS_POLLINATED, crops);
   }

   public void tick() {
      super.tick();
      Vec3 vector3d = this.getDeltaMovement();
      boolean flag = this.getDeltaMovement().x * this.getDeltaMovement().x + this.getDeltaMovement().z * this.getDeltaMovement().z >= 0.001;
      if (!this.onGround() && vector3d.y < 0.0) {
         this.setDeltaMovement(vector3d.multiply(1.0, 0.4, 1.0));
      }

      this.setFlying(true);
      this.setNoGravity(true);
      if (this.isFlying() && this.flyProgress < 5.0F) {
         this.flyProgress++;
      }

      if (!this.isFlying() && this.flyProgress > 0.0F) {
         this.flyProgress--;
      }

      if (this.sippy && this.sipProgress < 5.0F) {
         this.sipProgress++;
      }

      if (!this.sippy && this.sipProgress > 0.0F) {
         this.sipProgress--;
      }

      if (this.sippy && this.sipProgress == 5.0F) {
         this.sippy = false;
      }

      if (flag && this.movingProgress < 5.0F) {
         this.movingProgress++;
      }

      if (!flag && this.movingProgress > 0.0F) {
         this.movingProgress--;
      }

      if (this.getDeltaMovement().lengthSqr() < 1.0E-7) {
         this.hummingStill++;
      } else {
         this.hummingStill = 0;
      }

      if (this.pollinateCooldown > 0) {
         this.pollinateCooldown--;
      }

      if (this.sipCooldown > 0) {
         this.sipCooldown--;
      }

      if (this.loopSoundTick == 0) {
         this.playSound(AMSoundRegistry.HUMMINGBIRD_LOOP.get(), this.getSoundVolume() * 0.33F, this.getVoicePitch());
      }

      this.loopSoundTick++;
      if (this.loopSoundTick > 27) {
         this.loopSoundTick = 0;
      }

      this.prevFlyProgress = this.flyProgress;
      this.prevMovingProgress = this.movingProgress;
      this.prevSipProgress = this.sipProgress;
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      if (id == 68) {
         if (this.getFeederPos() != null) {
            if (this.random.nextFloat() < 0.2F) {
               double d2 = this.random.nextGaussian() * 0.02;
               double d0 = this.random.nextGaussian() * 0.02;
               double d1 = this.random.nextGaussian() * 0.02;
               this.level()
                  .addParticle(
                     ParticleTypes.FALLING_NECTAR,
                     (double)(this.getFeederPos().getX() + 0.2F) + this.random.nextFloat() * 0.6F,
                     this.getFeederPos().getY() + 0.1F,
                     (double)(this.getFeederPos().getZ() + 0.2F) + this.random.nextFloat() * 0.6F,
                     d0,
                     d1,
                     d2
                  );
            }

            this.sippy = true;
         }
      } else {
         super.handleEntityEvent(id);
      }
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return AMCompat.create(AMEntityRegistry.HUMMINGBIRD.get(), serverWorld);
   }

   public static <T extends Mob> boolean canHummingbirdSpawn(
      EntityType<EntityHummingbird> hummingbird, LevelAccessor worldIn, MobSpawnType reason, BlockPos p_223317_3_, RandomSource random
   ) {
      BlockState blockstate = worldIn.getBlockState(p_223317_3_.below());
      return (blockstate.is(AMTagRegistry.HUMMINGBIRD_SPAWNS) || blockstate.is(Blocks.AIR)) && worldIn.getRawBrightness(p_223317_3_, 0) > 8;
   }

   public boolean canBlockBeSeen(BlockPos pos) {
      double x = pos.getX() + 0.5F;
      double y = pos.getY() + 0.5F;
      double z = pos.getZ() + 0.5F;
      HitResult result = this.level()
         .clip(new ClipContext(new Vec3(this.getX(), this.getY() + this.getEyeHeight(), this.getZ()), new Vec3(x, y, z), Block.COLLIDER, Fluid.NONE, this));
      double dist = result.getLocation().distanceToSqr(x, y, z);
      return dist <= 1.0 || result.getType() == Type.MISS;
   }

   private class AIUseFeeder extends Goal {
      int runCooldown = 0;
      private int idleAtFlowerTime = 0;
      private BlockPos localFeeder;

      public AIUseFeeder(EntityHummingbird entityHummingbird) {
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
      }

      public void stop() {
         this.localFeeder = null;
         this.idleAtFlowerTime = 0;
      }

      public boolean canUse() {
         if (EntityHummingbird.this.sipCooldown > 0) {
            return false;
         } else {
            if (this.runCooldown > 0) {
               this.runCooldown--;
            } else {
               BlockPos feedPos = EntityHummingbird.this.getFeederPos();
               if (feedPos != null && this.isValidFeeder(EntityHummingbird.this.level().getBlockState(feedPos))) {
                  this.localFeeder = feedPos;
                  return true;
               }

               List<BlockPos> beacons = EntityHummingbird.this.getNearbyFeeders(
                  EntityHummingbird.this.blockPosition(), (ServerLevel)EntityHummingbird.this.level(), 64
               );
               BlockPos closest = null;

               for (BlockPos pos : beacons) {
                  if ((
                        closest == null
                           || EntityHummingbird.this.distanceToSqr(closest.getX(), closest.getY(), closest.getZ())
                              > EntityHummingbird.this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ())
                     )
                     && this.isValidFeeder(EntityHummingbird.this.level().getBlockState(pos))) {
                     closest = pos;
                  }
               }

               if (closest != null && this.isValidFeeder(EntityHummingbird.this.level().getBlockState(closest))) {
                  this.localFeeder = closest;
                  return true;
               }
            }

            this.runCooldown = 400 + EntityHummingbird.this.random.nextInt(600);
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.localFeeder != null
            && this.isValidFeeder(EntityHummingbird.this.level().getBlockState(this.localFeeder))
            && EntityHummingbird.this.sipCooldown == 0;
      }

      public void tick() {
         if (this.localFeeder != null && this.isValidFeeder(EntityHummingbird.this.level().getBlockState(this.localFeeder))) {
            if (EntityHummingbird.this.getY() > this.localFeeder.getY() && !EntityHummingbird.this.onGround()) {
               EntityHummingbird.this.getMoveControl()
                  .setWantedPosition(this.localFeeder.getX() + 0.5F, this.localFeeder.getY() + 0.1F, this.localFeeder.getZ() + 0.5F, 1.0);
            } else {
               EntityHummingbird.this.getMoveControl()
                  .setWantedPosition(
                     this.localFeeder.getX() + EntityHummingbird.this.random.nextInt(4) - 2,
                     EntityHummingbird.this.getY() + 1.0,
                     this.localFeeder.getZ() + EntityHummingbird.this.random.nextInt(4) - 2,
                     1.0
                  );
            }

            Vec3 vec = Vec3.upFromBottomCenterOf(this.localFeeder, 0.10000000149011612);
            double dist = Mth.sqrt((float)EntityHummingbird.this.distanceToSqr(vec));
            if (dist < 2.5 && EntityHummingbird.this.getY() > this.localFeeder.getY()) {
               EntityHummingbird.this.lookAt(Anchor.EYES, vec);
               this.idleAtFlowerTime++;
               EntityHummingbird.this.setFeederPos(this.localFeeder);
               EntityHummingbird.this.level().broadcastEntityEvent(EntityHummingbird.this, (byte)68);
               if (this.idleAtFlowerTime > 55) {
                  if (EntityHummingbird.this.getCropsPollinated() > 2
                     && EntityHummingbird.this.random.nextInt(25) == 0
                     && this.isValidFeeder(EntityHummingbird.this.level().getBlockState(this.localFeeder))) {
                     EntityHummingbird.this.level()
                        .setBlockAndUpdate(
                           this.localFeeder,
                           (BlockState)EntityHummingbird.this.level().getBlockState(this.localFeeder).setValue(BlockHummingbirdFeeder.CONTENTS, 0)
                        );
                  }

                  EntityHummingbird.this.setCropsPollinated(EntityHummingbird.this.getCropsPollinated() + 1);
                  EntityHummingbird.this.sipCooldown = 120 + EntityHummingbird.this.random.nextInt(1200);
                  EntityHummingbird.this.pollinateCooldown = Math.max(0, EntityHummingbird.this.pollinateCooldown / 3);
                  this.runCooldown = 400 + EntityHummingbird.this.random.nextInt(600);
                  this.stop();
               }
            }
         }
      }

      public boolean isValidFeeder(BlockState state) {
         return state.getBlock() instanceof BlockHummingbirdFeeder && (Integer)state.getValue(BlockHummingbirdFeeder.CONTENTS) == 3;
      }
   }
}
