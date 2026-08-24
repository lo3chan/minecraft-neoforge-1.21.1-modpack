package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntitySoulVulture extends Monster implements FlyingAnimal {
   public static final ResourceLocation SOUL_LOOT = AMCompat.rl("alexsmobs", "entities/soul_vulture_heart");
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntitySoulVulture.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> TACKLING = SynchedEntityData.defineId(EntitySoulVulture.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Optional<BlockPos>> PERCH_POS = SynchedEntityData.defineId(
      EntitySoulVulture.class, EntityDataSerializers.OPTIONAL_BLOCK_POS
   );
   private static final EntityDataAccessor<Integer> SOUL_LEVEL = SynchedEntityData.defineId(EntitySoulVulture.class, EntityDataSerializers.INT);
   public float prevFlyProgress;
   public float flyProgress;
   public float prevTackleProgress;
   public float tackleProgress;
   private boolean isLandNavigator;
   private int perchSearchCooldown = 0;
   private int landingCooldown = 0;
   private int tackleCooldown = 0;

   protected EntitySoulVulture(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.switchNavigator(true);
   }

   @Nullable
   protected ResourceKey<LootTable> getDefaultLootTable() {
      return this.hasSoulHeart() ? AMCompat.lootKey(SOUL_LOOT) : super.getDefaultLootTable();
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.soulVultureSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static boolean canVultureSpawn(
      EntityType<? extends Mob> typeIn, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn
   ) {
      BlockPos blockpos = pos.below();
      boolean spawnBlock = worldIn.getBlockState(blockpos).is(AMTagRegistry.SOUL_VULTURE_SPAWNS);
      return reason == MobSpawnType.SPAWNER || spawnBlock && checkMobSpawnRules(AMEntityRegistry.SOUL_VULTURE.get(), worldIn, reason, pos, randomIn);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.SOUL_VULTURE_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.SOUL_VULTURE_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.SOUL_VULTURE_HURT.get();
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 12.0)
         .add(Attributes.FOLLOW_RANGE, 18.0)
         .add(Attributes.ATTACK_DAMAGE, 4.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   public boolean isPerchBlock(BlockPos pos, BlockState state) {
      return this.level().isEmptyBlock(pos.above()) && this.level().isEmptyBlock(pos.above(2)) && state.is(AMTagRegistry.SOUL_VULTURE_PERCHES);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new EntitySoulVulture.AICirclePerch(this));
      this.goalSelector.addGoal(2, new EntitySoulVulture.AIFlyRandom(this));
      this.goalSelector.addGoal(3, new EntitySoulVulture.AITackleMelee(this));
      this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 20.0F));
      this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[]{EntitySoulVulture.class}));
      this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, true));
      this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, AbstractPiglin.class, true));
      this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, AbstractVillager.class, true));
   }

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   public boolean isMaxGroupSizeReached(int sizeIn) {
      return true;
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigatorWide(this, this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new EntitySoulVulture.MoveHelper(this);
         this.navigation = new DirectPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FLYING, false);
      builder.define(TACKLING, false);
      builder.define(PERCH_POS, Optional.empty());
      builder.define(SOUL_LEVEL, 0);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Flying", this.isFlying());
      if (this.getPerchPos() != null) {
         compound.putInt("PerchX", this.getPerchPos().getX());
         compound.putInt("PerchY", this.getPerchPos().getY());
         compound.putInt("PerchZ", this.getPerchPos().getZ());
      }

      compound.putInt("SoulLevel", this.getSoulLevel());
      compound.putInt("LandingCooldown", this.landingCooldown);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFlying(AMCompat.getBoolean(compound, "Flying"));
      this.setSoulLevel(AMCompat.getInt(compound, "SoulLevel"));
      this.landingCooldown = AMCompat.getInt(compound, "LandingCooldown");
      if (AMCompat.contains(compound, "PerchX") && AMCompat.contains(compound, "PerchY") && AMCompat.contains(compound, "PerchZ")) {
         this.setPerchPos(new BlockPos(AMCompat.getInt(compound, "PerchX"), AMCompat.getInt(compound, "PerchY"), AMCompat.getInt(compound, "PerchZ")));
      }
   }

   public boolean isFlying() {
      return (Boolean)this.entityData.get(FLYING);
   }

   public void setFlying(boolean flying) {
      this.entityData.set(FLYING, flying);
   }

   public boolean isTackling() {
      return (Boolean)this.entityData.get(TACKLING);
   }

   public void setTackling(boolean tackling) {
      this.entityData.set(TACKLING, tackling);
   }

   public BlockPos getPerchPos() {
      return (BlockPos)((Optional)this.entityData.get(PERCH_POS)).orElse(null);
   }

   public void setPerchPos(BlockPos pos) {
      this.entityData.set(PERCH_POS, Optional.ofNullable(pos));
   }

   public int getSoulLevel() {
      return (Integer)this.entityData.get(SOUL_LEVEL);
   }

   public void setSoulLevel(int tackling) {
      this.entityData.set(SOUL_LEVEL, tackling);
   }

   public void tick() {
      super.tick();
      this.prevTackleProgress = this.tackleProgress;
      this.prevFlyProgress = this.flyProgress;
      if (!this.level().isClientSide()) {
         if (this.perchSearchCooldown > 0) {
            this.perchSearchCooldown--;
         }

         if (this.getTarget() != null && this.getTarget().isAlive()) {
            this.setPerchPos(this.getTarget().blockPosition().above(7));
         } else if (this.getPerchPos() != null && !this.isPerchBlock(this.getPerchPos(), this.level().getBlockState(this.getPerchPos()))) {
            this.setPerchPos(null);
         }

         if (this.getPerchPos() == null && this.perchSearchCooldown == 0) {
            this.perchSearchCooldown = 20 + this.random.nextInt(20);
            this.setPerchPos(this.findNewPerchPos());
         }

         if (!this.isFlying() && this.landingCooldown == 0 && (this.getPerchPos() == null || this.shouldLeavePerch(this.getPerchPos()))) {
            this.setFlying(true);
         }

         if (!this.isFlying() && this.getTarget() != null) {
            this.setFlying(true);
         }

         if (this.landingCooldown > 0 && this.isFlying() && this.onGround() && this.getTarget() == null) {
            this.setFlying(false);
         }
      }

      boolean flying = this.isFlying();
      if (flying) {
         if (this.isLandNavigator) {
            this.switchNavigator(false);
         }

         if (this.flyProgress < 5.0F) {
            this.flyProgress++;
         }
      } else {
         if (!this.isLandNavigator) {
            this.switchNavigator(true);
         }

         if (this.flyProgress > 0.0F) {
            this.flyProgress--;
         }
      }

      if (this.isTackling()) {
         if (this.tackleProgress < 5.0F) {
            this.tackleProgress++;
         }
      } else if (this.tackleProgress > 0.0F) {
         this.tackleProgress--;
      }

      if (this.landingCooldown > 0) {
         this.landingCooldown--;
      }

      if (this.tackleCooldown > 0) {
         this.tackleCooldown--;
      }

      if (this.isFlying()) {
         this.setNoGravity(true);
      } else {
         this.setNoGravity(false);
      }

      if (this.level().isClientSide() && this.hasSoulHeart()) {
         float radius = 0.25F + this.random.nextFloat() * 1.0F;
         float fly = this.flyProgress * 0.2F;
         float wingSpread = 15.0F + 65.0F * fly + this.random.nextInt(5);
         float angle = 0.017453292F * ((this.random.nextBoolean() ? -1 : 1) * (wingSpread + 180.0F) + this.yBodyRot);
         float angleMotion = 0.017453292F * this.yBodyRot;
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         double mov = this.getDeltaMovement().length();
         double extraXMotion = -mov * Mth.sin((float)(3.141592653589793 + angleMotion));
         double extraZMotion = -mov * Mth.cos(angleMotion);
         double yRandom = 0.2F + this.random.nextFloat() * 0.3F;
         this.level()
            .addParticle(
               ParticleTypes.SOUL_FIRE_FLAME,
               this.getX() + extraX,
               this.getY() + yRandom,
               this.getZ() + extraZ,
               extraXMotion,
               this.random.nextFloat() * 0.1F,
               extraZMotion
            );
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      if (id == 68) {
         for (int i = 0; i < 6 + this.random.nextInt(3); i++) {
            double d2 = this.random.nextGaussian() * 0.02;
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            this.level()
               .addParticle(
                  ParticleTypes.SOUL,
                  this.getX() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
                  this.getY() + this.getBbHeight() * 0.5F + this.random.nextFloat() * this.getBbHeight() * 0.5F,
                  this.getZ() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
                  d0,
                  d1,
                  d2
               );
         }
      } else {
         super.handleEntityEvent(id);
      }
   }

   public BlockPos findNewPerchPos() {
      BlockState beneathState = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement());
      if (this.isPerchBlock(this.getBlockPosBelowThatAffectsMyMovement(), beneathState)) {
         return this.getBlockPosBelowThatAffectsMyMovement();
      } else {
         BlockPos blockpos = null;
         Random random = new Random();
         int range = 14;

         for (int i = 0; i < 15; i++) {
            BlockPos blockpos1 = this.blockPosition().offset(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);

            while (this.level().isEmptyBlock(blockpos1) && blockpos1.getY() > 1) {
               blockpos1 = blockpos1.below();
            }

            if (this.isPerchBlock(blockpos1, this.level().getBlockState(blockpos1))) {
               blockpos = blockpos1;
            }
         }

         return blockpos;
      }
   }

   private boolean shouldLeavePerch(BlockPos perchPos) {
      return this.distanceToSqr(Vec3.atCenterOf(perchPos)) > 13.0 || this.landingCooldown == 0;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public boolean shouldSwoop() {
      return this.getTarget() != null && this.tackleCooldown == 0;
   }

   public boolean hasSoulHeart() {
      return this.getSoulLevel() > 2;
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).getType() != Type.MISS;
   }

   class AICirclePerch extends Goal {
      private final EntitySoulVulture vulture;
      float speed = 1.0F;
      float circlingTime = 0.0F;
      float circleDistance = 5.0F;
      float maxCirclingTime = 80.0F;
      boolean clockwise = false;
      private BlockPos targetPos;
      private int yLevel = 1;

      public AICirclePerch(EntitySoulVulture vulture) {
         this.vulture = vulture;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         return !this.vulture.shouldSwoop() && this.vulture.isFlying() && this.vulture.getPerchPos() != null;
      }

      public void start() {
         this.circlingTime = 0.0F;
         this.speed = 0.8F + EntitySoulVulture.this.random.nextFloat() * 0.4F;
         this.yLevel = this.vulture.random.nextInt(3);
         this.maxCirclingTime = 360 + this.vulture.random.nextInt(80);
         this.circleDistance = 5.0F + this.vulture.random.nextFloat() * 5.0F;
         this.clockwise = this.vulture.random.nextBoolean();
      }

      public void stop() {
         this.circlingTime = 0.0F;
         this.speed = 0.8F + EntitySoulVulture.this.random.nextFloat() * 0.4F;
         this.yLevel = this.vulture.random.nextInt(3);
         this.maxCirclingTime = 360 + this.vulture.random.nextInt(80);
         this.circleDistance = 5.0F + this.vulture.random.nextFloat() * 5.0F;
         this.clockwise = this.vulture.random.nextBoolean();
         this.vulture.tackleCooldown = 0;
      }

      public void tick() {
         BlockPos encircle = this.vulture.getPerchPos();
         double localSpeed = this.speed;
         if (this.vulture.getTarget() != null) {
            localSpeed *= 1.55;
         }

         if (encircle != null) {
            this.circlingTime++;
            if (this.circlingTime > 360.0F) {
               this.vulture.getMoveControl().setWantedPosition(encircle.getX() + 0.5, encircle.getY() + 1.1, encircle.getZ() + 0.5, localSpeed);
               if (this.vulture.verticalCollision || this.vulture.distanceToSqr(encircle.getX() + 0.5, encircle.getY() + 1.1, encircle.getZ() + 0.5) < 1.0) {
                  this.vulture.setFlying(false);
                  this.vulture.setDeltaMovement(Vec3.ZERO);
                  this.vulture.landingCooldown = 400 + EntitySoulVulture.this.random.nextInt(1200);
                  this.stop();
               }
            } else {
               BlockPos circlePos = this.getVultureCirclePos(encircle);
               if (circlePos != null) {
                  this.vulture.getMoveControl().setWantedPosition(circlePos.getX() + 0.5, circlePos.getY() + 0.5, circlePos.getZ() + 0.5, localSpeed);
               }
            }
         }
      }

      public boolean canContinueToUse() {
         return this.canUse();
      }

      public BlockPos getVultureCirclePos(BlockPos target) {
         float angle = 0.05235988F * (this.clockwise ? -this.circlingTime : this.circlingTime);
         double extraX = this.circleDistance * Mth.sin(angle);
         double extraZ = this.circleDistance * Mth.cos(angle);
         BlockPos pos = new BlockPos((int)(target.getX() + extraX), target.getY() + 1 + this.yLevel, (int)(target.getZ() + extraZ));
         return this.vulture.level().isEmptyBlock(pos) ? pos : null;
      }
   }

   private class AIFlyRandom extends Goal {
      private final EntitySoulVulture vulture;
      private BlockPos target = null;

      public AIFlyRandom(EntitySoulVulture vulture) {
         this.vulture = vulture;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (this.vulture.getPerchPos() == null && !this.vulture.shouldSwoop()) {
            MoveControl movementcontroller = this.vulture.getMoveControl();
            if (movementcontroller.hasWanted() && this.target != null) {
               return false;
            } else {
               this.target = this.getBlockInViewVulture();
               if (this.target != null) {
                  this.vulture.getMoveControl().setWantedPosition(this.target.getX() + 0.5, this.target.getY() + 0.5, this.target.getZ() + 0.5, 1.0);
               }

               return true;
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.vulture.getPerchPos() == null && !this.vulture.shouldSwoop()
            ? this.target != null
               && this.vulture.distanceToSqr(Vec3.atCenterOf(this.target)) > 2.4
               && this.vulture.getMoveControl().hasWanted()
               && !this.vulture.horizontalCollision
            : false;
      }

      public void stop() {
         this.target = null;
      }

      public void tick() {
         if (this.target == null) {
            this.target = this.getBlockInViewVulture();
         }

         if (this.target != null) {
            this.vulture.getMoveControl().setWantedPosition(this.target.getX() + 0.5, this.target.getY() + 0.5, this.target.getZ() + 0.5, 1.0);
            if (this.vulture.distanceToSqr(Vec3.atCenterOf(this.target)) < 2.5) {
               this.target = null;
            }
         }
      }

      public BlockPos getBlockInViewVulture() {
         float radius = -9.45F - this.vulture.getRandom().nextInt(10);
         float neg = this.vulture.getRandom().nextBoolean() ? 1.0F : -1.0F;
         float renderYawOffset = this.vulture.yBodyRot;
         float angle = 0.017453292F * renderYawOffset + 3.15F + this.vulture.getRandom().nextFloat() * neg;
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         BlockPos radialPos = new BlockPos((int)(this.vulture.getX() + extraX), (int)this.vulture.getY(), (int)(this.vulture.getZ() + extraZ));

         while (EntitySoulVulture.this.level().isEmptyBlock(radialPos) && radialPos.getY() > 2) {
            radialPos = radialPos.below();
         }

         BlockPos newPos = radialPos.above(this.vulture.getY() - radialPos.getY() > 16.0 ? 4 : this.vulture.getRandom().nextInt(5) + 5);
         return !this.vulture.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.vulture.distanceToSqr(Vec3.atCenterOf(newPos)) > 6.0 ? newPos : null;
      }
   }

   private class AITackleMelee extends Goal {
      private final EntitySoulVulture vulture;

      public AITackleMelee(EntitySoulVulture vulture) {
         this.vulture = vulture;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (this.vulture.getTarget() != null && this.vulture.shouldSwoop()) {
            this.vulture.setFlying(true);
            return true;
         } else {
            return false;
         }
      }

      public void stop() {
         this.vulture.setTackling(false);
      }

      public void tick() {
         if (this.vulture.isFlying()) {
            this.vulture.setTackling(true);
         } else {
            this.vulture.setTackling(false);
         }

         if (this.vulture.getTarget() != null) {
            this.vulture
               .getMoveControl()
               .setWantedPosition(
                  this.vulture.getTarget().getX(),
                  this.vulture.getTarget().getY() + this.vulture.getTarget().getEyeHeight(),
                  this.vulture.getTarget().getZ(),
                  2.0
               );
            double d0 = this.vulture.getX() - this.vulture.getTarget().getX();
            double d2 = this.vulture.getZ() - this.vulture.getTarget().getZ();
            float f = (float)(Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
            this.vulture.setYRot(f);
            this.vulture.yBodyRot = this.vulture.getYRot();
            if (this.vulture
                  .getBoundingBox()
                  .inflate(0.30000001192092896, 0.30000001192092896, 0.30000001192092896)
                  .intersects(this.vulture.getTarget().getBoundingBox())
               && this.vulture.tackleCooldown == 0) {
               EntitySoulVulture.this.tackleCooldown = 100 + EntitySoulVulture.this.random.nextInt(200);
               float dmg = (float)this.vulture.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
               if (AMCompat.hurt(this.vulture.getTarget(), this.vulture.damageSources().mobAttack(this.vulture), dmg)
                  && this.vulture.getHealth() < this.vulture.getMaxHealth() - dmg
                  && this.vulture.getSoulLevel() < 5) {
                  this.vulture.setSoulLevel(this.vulture.getSoulLevel() + 1);
                  this.vulture.heal(dmg);
                  this.vulture.level().broadcastEntityEvent(this.vulture, (byte)68);
               }

               this.stop();
            }
         }
      }
   }

   static class MoveHelper extends MoveControl {
      private final EntitySoulVulture parentEntity;

      public MoveHelper(EntitySoulVulture bird) {
         super(bird);
         this.parentEntity = bird;
      }

      public void tick() {
         if (this.operation == Operation.MOVE_TO) {
            Vec3 vector3d = new Vec3(this.wantedX - this.parentEntity.getX(), this.wantedY - this.parentEntity.getY(), this.wantedZ - this.parentEntity.getZ());
            double d5 = vector3d.length();
            if (d5 < 0.3) {
               this.operation = Operation.WAIT;
               this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().scale(0.5));
            } else {
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
}
