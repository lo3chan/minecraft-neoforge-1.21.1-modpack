package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntityMurmur extends Monster implements ISemiAquatic {
   private static final EntityDataAccessor<Optional<UUID>> HEAD_UUID = SynchedEntityData.defineId(EntityMurmur.class, EntityDataSerializers.OPTIONAL_UUID);
   private static final EntityDataAccessor<Integer> HEAD_ID = SynchedEntityData.defineId(EntityMurmur.class, EntityDataSerializers.INT);
   private boolean renderFakeHead = true;

   protected EntityMurmur(EntityType<? extends Monster> type, Level level) {
      super(type, level);
      this.xpReward = 10;
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 30.0)
         .add(Attributes.FOLLOW_RANGE, 48.0)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.30000001192092896)
         .add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new AnimalAILeaveWater(this));
      this.goalSelector.addGoal(2, new AnimalAIWanderRanged(this, 55, 1.0, 14, 7));
      this.targetSelector.addGoal(0, new HurtByTargetGoal(this, new Class[0]));
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.MURMUR_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.MURMUR_HURT.get();
   }

   protected void playStepSound(BlockPos pos, BlockState blockIn) {
   }

   public static <T extends Mob> boolean checkMurmurSpawnRules(
      EntityType<EntityMurmur> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return reason == MobSpawnType.SPAWNER
         || !iServerWorld.canSeeSky(pos)
            && (pos.getY() <= AMConfig.murmurSpawnHeight || iServerWorld.getBiome(pos).is(AMTagRegistry.SPAWNS_MURMURS_IGNORE_HEIGHT))
            && checkMonsterSpawnRules(entityType, iServerWorld, reason, pos, random);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.murmurSpawnRolls, this.getRandom(), spawnReasonIn) && super.checkSpawnRules(worldIn, spawnReasonIn);
   }

   public boolean isAlliedTo(Entity entity) {
      return this.getHeadUUID() != null && entity.getUUID().equals(this.getHeadUUID()) || super.isAlliedTo(entity);
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      EntityDimensions dimensions = super.getDefaultDimensions(pose);
      return dimensions.withEyeHeight(dimensions.height() * 1.2F);
   }

   protected float getWaterSlowDown() {
      return 0.9F;
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(HEAD_UUID, Optional.empty());
      builder.define(HEAD_ID, -1);
   }

   @Nullable
   public UUID getHeadUUID() {
      return (UUID)((Optional)this.entityData.get(HEAD_UUID)).orElse(null);
   }

   public void setHeadUUID(@Nullable UUID uniqueId) {
      this.entityData.set(HEAD_UUID, Optional.ofNullable(uniqueId));
   }

   public Entity getHead() {
      if (!this.level().isClientSide()) {
         UUID id = this.getHeadUUID();
         return id == null ? null : ((ServerLevel)this.level()).getEntity(id);
      } else {
         int id = (Integer)this.entityData.get(HEAD_ID);
         return id == -1 ? null : this.level().getEntity(id);
      }
   }

   public boolean shouldRenderFakeHead() {
      return this.renderFakeHead;
   }

   public void tick() {
      super.tick();
      if (this.renderFakeHead) {
         this.renderFakeHead = false;
      }

      this.yBodyRot = this.getYRot();
      this.yHeadRot = Mth.clamp(this.yHeadRot, this.yBodyRot - 70.0F, this.yBodyRot + 70.0F);
      if (!this.level().isClientSide()) {
         Entity head = this.getHead();
         if (head == null) {
            LivingEntity created = this.createHead();
            this.setHeadUUID(created.getUUID());
            this.entityData.set(HEAD_ID, created.getId());
         }
      }
   }

   public Vec3 getNeckBottom(float partialTick) {
      double d0 = Mth.lerp(partialTick, this.xo, this.getX());
      double d1 = Mth.lerp(partialTick, this.yo, this.getY());
      double d2 = Mth.lerp(partialTick, this.zo, this.getZ());
      double height = this.getBbHeight() - 0.4F + this.calculateWalkBounce(partialTick);
      Vec3 rotatedOnDeath = new Vec3(0.0, height, 0.0);
      if (this.deathTime > 0) {
         float f = (this.deathTime + partialTick - 1.0F) / 20.0F * 1.6F;
         f = Mth.sqrt(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         rotatedOnDeath = rotatedOnDeath.add(f * 0.1F, f * 0.4F, 0.0).zRot((float)(f * 3.141592653589793 / 2.0)).yRot(-this.yBodyRot * 0.017453292F);
      }

      return new Vec3(d0, d1, d2).add(rotatedOnDeath);
   }

   public double calculateWalkBounce(float partialTick) {
      float limbSwingAmount = this.walkAnimation.speed(partialTick);
      float limbSwing = this.walkAnimation.position() - this.walkAnimation.speed() * (1.0F - partialTick);
      return Math.abs(Math.sin(limbSwing * 0.9F) * limbSwingAmount * 0.25);
   }

   @Override
   public boolean shouldEnterWater() {
      return false;
   }

   @Override
   public boolean shouldLeaveWater() {
      return true;
   }

   @Override
   public boolean shouldStopMoving() {
      return false;
   }

   @Override
   public int getWaterSearchRange() {
      return 5;
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (AMCompat.hasUUID(compound, "HeadUUID")) {
         this.setHeadUUID(AMCompat.getUUID(compound, "HeadUUID"));
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (this.getHeadUUID() != null) {
         AMCompat.putUUID(compound, "HeadUUID", this.getHeadUUID());
      }
   }

   private LivingEntity createHead() {
      EntityMurmurHead head = new EntityMurmurHead(this);
      this.level().addFreshEntity(head);
      return head;
   }

   public boolean isAngry() {
      Entity entity = this.getHead();
      return entity instanceof EntityMurmurHead ? ((EntityMurmurHead)entity).isAngry() : false;
   }
}
