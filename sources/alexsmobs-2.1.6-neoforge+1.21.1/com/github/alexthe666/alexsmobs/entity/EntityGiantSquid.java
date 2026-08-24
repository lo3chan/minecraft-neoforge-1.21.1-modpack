package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.entity.PartEntity;

public class EntityGiantSquid extends WaterAnimal implements IMultipartOwner {
   private static final EntityDataAccessor<Float> SQUID_PITCH = SynchedEntityData.defineId(EntityGiantSquid.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DEPRESSURIZATION = SynchedEntityData.defineId(EntityGiantSquid.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> OVERRIDE_BODYROT = SynchedEntityData.defineId(EntityGiantSquid.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> GRABBING = SynchedEntityData.defineId(EntityGiantSquid.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> CAPTURED = SynchedEntityData.defineId(EntityGiantSquid.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> BLUE = SynchedEntityData.defineId(EntityGiantSquid.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> GRAB_ENTITY = SynchedEntityData.defineId(EntityGiantSquid.class, EntityDataSerializers.INT);
   public final EntityGiantSquidPart mantlePart1;
   public final EntityGiantSquidPart mantlePart2;
   public final EntityGiantSquidPart mantlePart3;
   public final EntityGiantSquidPart tentaclesPart1;
   public final EntityGiantSquidPart tentaclesPart2;
   public final EntityGiantSquidPart tentaclesPart3;
   public final EntityGiantSquidPart tentaclesPart4;
   public final EntityGiantSquidPart tentaclesPart5;
   public final EntityGiantSquidPart tentaclesPart6;
   public final EntityGiantSquidPart mantleCollisionPart;
   public final EntityGiantSquidPart[] allParts;
   public final float[][] ringBuffer = new float[64][2];
   public int ringBufferIndex = -1;
   public float prevSquidPitch;
   public float prevDepressurization;
   public float grabProgress;
   public float prevGrabProgress;
   public float dryProgress;
   public float prevDryProgress;
   public float capturedProgress;
   public float prevCapturedProgress;
   public int humTick = 0;
   private int holdTime;
   private int resetCapturedStateIn;

   protected EntityGiantSquid(EntityType type, Level level) {
      super(type, level);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.mantlePart1 = new EntityGiantSquidPart(this, 0.9F, 0.9F);
      this.mantlePart2 = new EntityGiantSquidPart(this, 1.2F, 1.2F);
      this.mantlePart3 = new EntityGiantSquidPart(this, 0.45F, 0.45F);
      this.tentaclesPart1 = new EntityGiantSquidPart(this, 0.9F, 0.9F);
      this.tentaclesPart2 = new EntityGiantSquidPart(this, 1.0F, 1.0F);
      this.tentaclesPart3 = new EntityGiantSquidPart(this, 1.2F, 1.2F);
      this.tentaclesPart4 = new EntityGiantSquidPart(this, 1.2F, 1.2F);
      this.tentaclesPart5 = new EntityGiantSquidPart(this, 1.2F, 1.2F);
      this.tentaclesPart6 = new EntityGiantSquidPart(this, 1.2F, 1.2F);
      this.mantleCollisionPart = new EntityGiantSquidPart(this, 2.9F, 2.9F, true);
      this.allParts = new EntityGiantSquidPart[]{
         this.mantlePart1,
         this.mantlePart2,
         this.mantlePart3,
         this.mantleCollisionPart,
         this.tentaclesPart1,
         this.tentaclesPart2,
         this.tentaclesPart3,
         this.tentaclesPart4,
         this.tentaclesPart5,
         this.tentaclesPart6
      };
      this.lookControl = new SmoothSwimmingLookControl(this, 4);
      this.moveControl = new AquaticMoveController(this, 1.2F, 5.0F);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.giantSquidSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static boolean canGiantSquidSpawn(
      EntityType<EntityGiantSquid> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return reason == MobSpawnType.SPAWNER || iServerWorld.isWaterAt(pos) && iServerWorld.isWaterAt(pos.above());
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      if (reason == MobSpawnType.NATURAL) {
         this.doInitialPosing(worldIn);
      }

      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   private void doInitialPosing(LevelAccessor world) {
      BlockPos down = this.blockPosition();

      while (!world.getFluidState(down).isEmpty() && down.getY() > 1) {
         down = down.below();
      }

      this.setPos(down.getX() + 0.5F, down.getY() + 3 + this.random.nextInt(3), down.getZ() + 0.5F);
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.GIANT_SQUID_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.GIANT_SQUID_HURT.get();
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 38.0).add(Attributes.ATTACK_DAMAGE, 8.0).add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SQUID_PITCH, 0.0F);
      builder.define(OVERRIDE_BODYROT, false);
      builder.define(DEPRESSURIZATION, 0.0F);
      builder.define(GRABBING, false);
      builder.define(CAPTURED, false);
      builder.define(BLUE, false);
      builder.define(GRAB_ENTITY, -1);
   }

   @Nullable
   public Entity getGrabbedEntity() {
      return (Entity)(this.level().isClientSide() && this.entityData.get(GRAB_ENTITY) != -1
         ? this.level().getEntity((Integer)this.entityData.get(GRAB_ENTITY))
         : this.getTarget());
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      Item item = itemstack.getItem();
      return super.mobInteract(player, hand);
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new WaterBoundPathNavigation(this, worldIn);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
      this.goalSelector.addGoal(1, new EntityGiantSquid.AIAvoidWhales());
      this.goalSelector.addGoal(2, new EntityGiantSquid.AIMelee());
      this.goalSelector.addGoal(3, new EntityGiantSquid.AIDeepwaterSwimming());
      this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[]{EntityCachalotWhale.class}));
      this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Guardian.class, 20, true, true, null) {
         public boolean canUse() {
            return super.canUse();
         }
      });
      this.targetSelector
         .addGoal(
            3,
            new EntityAINearestTarget3D(this, LivingEntity.class, 70, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.GIANT_SQUID_TARGETS)) {
               public boolean canUse() {
                  return !EntityGiantSquid.this.isInWaterOrBubble() && !EntityGiantSquid.this.isCaptured() && super.canUse();
               }
            }
         );
   }

   public void aiStep() {
      super.aiStep();
      if (!this.isNoAi()) {
         if (this.ringBufferIndex < 0) {
            for (int i = 0; i < this.ringBuffer.length; i++) {
               this.ringBuffer[i][0] = 180.0F + this.getYRot();
               this.ringBuffer[i][1] = this.getSquidPitch();
            }
         }

         this.ringBufferIndex++;
         if (this.ringBufferIndex == this.ringBuffer.length) {
            this.ringBufferIndex = 0;
         }

         this.ringBuffer[this.ringBufferIndex][0] = this.yBodyRot;
         this.ringBuffer[this.ringBufferIndex][1] = this.getSquidPitch();
      }
   }

   public void tick() {
      super.tick();
      if (this.tickCount % 100 == 0) {
         this.heal(2.0F);
      }

      float f = Mth.wrapDegrees(180.0F + this.getYRot());
      this.yBodyRot = this.rotlerp(this.yBodyRot, f, 180.0F);
      this.prevSquidPitch = this.getSquidPitch();
      this.prevDepressurization = this.getDepressurization();
      this.prevDryProgress = this.dryProgress;
      this.prevGrabProgress = this.grabProgress;
      this.prevCapturedProgress = this.capturedProgress;
      if (!this.isInWater() && this.dryProgress < 5.0F) {
         this.dryProgress++;
      }

      if (this.isInWater() && this.dryProgress > 0.0F) {
         this.dryProgress--;
      }

      if (this.isGrabbing()) {
         if (this.grabProgress < 5.0F) {
            this.grabProgress += 0.25F;
         }
      } else if (this.grabProgress > 0.0F) {
         this.grabProgress -= 0.25F;
      }

      if (this.isCaptured()) {
         if (this.capturedProgress < 5.0F) {
            this.capturedProgress += 0.5F;
         }
      } else if (this.capturedProgress > 0.0F) {
         this.capturedProgress -= 0.5F;
      }

      if (this.isGrabbing()) {
         Entity target = this.getGrabbedEntity();
         if (!this.level().isClientSide() && target != null) {
            this.entityData.set(GRAB_ENTITY, target.getId());
            if (this.holdTime % 20 == 0 && this.holdTime > 30) {
               target.hurt(this.damageSources().mobAttack(this), 3 + this.random.nextInt(5));
            }
         }

         if (target != null && target.isAlive()) {
            this.setXRot(0.0F);
            float invert = 1.0F - this.grabProgress * 0.2F;
            Vec3 extraVec = new Vec3(0.0, 0.0, 2.0F + invert * 7.0F).xRot(-this.getXRot() * 0.017453292F).yRot(-this.yBodyRot * 0.017453292F);
            Vec3 minus = new Vec3(this.getX() + extraVec.x - target.getX(), this.getY() + extraVec.y - target.getY(), this.getZ() + extraVec.z - target.getZ());
            target.setDeltaMovement(minus);
         }

         this.holdTime++;
         if (this.holdTime > 1000) {
            this.holdTime = 0;
            this.setGrabbing(false);
         }
      } else {
         this.holdTime = 0;
      }

      if (!this.isNoAi()) {
         Vec3[] avector3d = new Vec3[this.allParts.length];

         for (int j = 0; j < this.allParts.length; j++) {
            this.allParts[j].collideWithNearbyEntities();
            avector3d[j] = new Vec3(this.allParts[j].getX(), this.allParts[j].getY(), this.allParts[j].getZ());
         }

         float pitch = this.getXRot() * 0.017453292F * 0.8F;
         this.mantleCollisionPart
            .setPos(
               this.getX(), this.getY() - (this.mantleCollisionPart.getBbHeight() - this.getEyeHeight()) * 0.5F * (1.0F - this.dryProgress * 0.2F), this.getZ()
            );
         this.setPartPositionFromBuffer(this.mantlePart1, pitch, 0.9F, 0);
         this.setPartPositionFromBuffer(this.mantlePart2, pitch, 1.6F, 0);
         this.setPartPositionFromBuffer(this.mantlePart3, pitch, 2.45F, 0);
         this.setPartPositionFromBuffer(this.tentaclesPart1, pitch, -0.8F, 0);
         this.setPartPositionFromBuffer(this.tentaclesPart2, pitch, -1.5F, 0);
         this.setPartPositionFromBuffer(this.tentaclesPart3, pitch, -2.3F, 5);
         this.setPartPositionFromBuffer(this.tentaclesPart4, pitch, -3.4F, 10);
         this.setPartPositionFromBuffer(this.tentaclesPart5, pitch, -5.4F, 15);
         this.setPartPositionFromBuffer(this.tentaclesPart6, pitch, -7.4F, 20);
         if (this.isInWaterOrBubble()) {
            if (this.mantleCollisionPart.scale != 1.0F) {
               this.mantleCollisionPart.scale = 1.0F;
               this.mantleCollisionPart.refreshDimensions();
            }
         } else if (this.mantleCollisionPart.scale != 0.25F) {
            this.mantleCollisionPart.scale = 0.25F;
            this.mantleCollisionPart.refreshDimensions();
         }

         for (int l = 0; l < this.allParts.length; l++) {
            this.allParts[l].xo = avector3d[l].x;
            this.allParts[l].yo = avector3d[l].y;
            this.allParts[l].zo = avector3d[l].z;
            this.allParts[l].xOld = avector3d[l].x;
            this.allParts[l].yOld = avector3d[l].y;
            this.allParts[l].zOld = avector3d[l].z;
         }

         this.setNoGravity(this.isInWater());
      }

      if (!this.level().isClientSide()) {
         if (this.getSquidPitch() > 0.0F) {
            float decrease = Math.min(2.0F, this.getSquidPitch());
            this.decrementSquidPitch(decrease);
         }

         if (this.getSquidPitch() < 0.0F) {
            float decrease = Math.min(2.0F, -this.getSquidPitch());
            this.incrementSquidPitch(decrease);
         }

         if (this.isInWaterOrBubble()) {
            float dist = (float)this.getDeltaMovement().y() * 45.0F;
            if ((Boolean)this.entityData.get(OVERRIDE_BODYROT)) {
               this.decrementSquidPitch(dist);
            } else {
               this.incrementSquidPitch(dist);
            }
         }

         if (!this.onGround() && this.getFluidHeight(FluidTags.WATER) < this.getBbHeight()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.10000000149011612, 0.0));
         }

         float pressure = this.getDepressureLevel();
         if (this.getDepressurization() < pressure) {
            this.setDepressurization(this.getDepressurization() + 0.1F);
         }

         if (this.getDepressurization() > pressure) {
            this.setDepressurization(this.getDepressurization() - 0.1F);
         }
      }

      if (this.isHumming()) {
         if (this.humTick % 20 == 0) {
            this.playSound(AMSoundRegistry.GIANT_SQUID_GAMES.get(), this.getSoundVolume(), 1.0F);
            this.humTick = 0;
         }

         this.humTick++;
      }

      if (!this.level().isClientSide()) {
         if (this.resetCapturedStateIn > 0) {
            this.resetCapturedStateIn--;
         } else {
            this.setCaptured(false);
         }
      }
   }

   private boolean isHumming() {
      String s = ChatFormatting.stripFormatting(this.getName().getString());
      return s != null && s.toLowerCase().contains("squid games!!") || AlexsMobs.isAprilFools();
   }

   public float getRingBuffer(int bufferOffset, float partialTicks, boolean pitch) {
      int i = this.ringBufferIndex - bufferOffset & 63;
      int j = this.ringBufferIndex - bufferOffset - 1 & 63;
      int k = pitch ? 1 : 0;
      float prevBuffer = this.ringBuffer[j][k];
      float buffer = this.ringBuffer[i][k];
      float end = prevBuffer + (buffer - prevBuffer) * partialTicks;
      return this.rotlerp(prevBuffer, end, 10.0F);
   }

   private void setPartPosition(EntityGiantSquidPart part, double offsetX, double offsetY, double offsetZ, float offsetScale) {
      part.setPos(
         this.getX() + offsetX * offsetScale * part.scale, this.getY() + offsetY * offsetScale * part.scale, this.getZ() + offsetZ * offsetScale * part.scale
      );
   }

   private void setPartPositionFromBuffer(EntityGiantSquidPart part, float pitch, float offsetScale, int ringBufferOffset) {
      float f2 = Mth.sin(this.getRingBuffer(ringBufferOffset, 1.0F, false) * 0.017453292F) * (1.0F - Math.abs(this.getXRot() / 90.0F));
      float f3 = Mth.cos(this.getRingBuffer(ringBufferOffset, 1.0F, false) * 0.017453292F) * (1.0F - Math.abs(this.getXRot() / 90.0F));
      this.setPartPosition(part, f2, pitch, -f3, offsetScale);
   }

   public int getMaxHeadXRot() {
      return 1;
   }

   public int getMaxHeadYRot() {
      return 3;
   }

   public void travel(Vec3 travelVector) {
      if (this.isEffectiveAi() && this.isInWater()) {
         if ((Boolean)this.entityData.get(OVERRIDE_BODYROT)) {
            travelVector = new Vec3(travelVector.x, travelVector.y, -travelVector.z);
         }

         this.moveRelative(this.getSpeed(), travelVector);
         double d = this.getTarget() == null ? 0.6 : 0.9;
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, d, 0.9));
         this.move(MoverType.SELF, this.getDeltaMovement());
      } else {
         super.travel(travelVector);
      }
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setBlue(AMCompat.getBoolean(compound, "Blue"));
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Blue", this.isBlue());
   }

   public boolean checkSpawnObstruction(LevelReader worldIn) {
      return worldIn.isUnobstructed(this);
   }

   public float getDepressurization() {
      return Mth.clamp((Float)this.entityData.get(DEPRESSURIZATION), 0.0F, 1.0F);
   }

   public void setDepressurization(float depressurization) {
      this.entityData.set(DEPRESSURIZATION, depressurization);
   }

   public float getSquidPitch() {
      return Mth.clamp((Float)this.entityData.get(SQUID_PITCH), -90.0F, 90.0F);
   }

   public void setSquidPitch(float pitch) {
      this.entityData.set(SQUID_PITCH, pitch);
   }

   public void incrementSquidPitch(float pitch) {
      this.entityData.set(SQUID_PITCH, this.getSquidPitch() + pitch);
   }

   public void decrementSquidPitch(float pitch) {
      this.entityData.set(SQUID_PITCH, this.getSquidPitch() - pitch);
   }

   public boolean isGrabbing() {
      return (Boolean)this.entityData.get(GRABBING);
   }

   public void setGrabbing(boolean running) {
      this.entityData.set(GRABBING, running);
   }

   public boolean isCaptured() {
      return (Boolean)this.entityData.get(CAPTURED);
   }

   public void setCaptured(boolean running) {
      this.entityData.set(CAPTURED, running);
   }

   public boolean isBlue() {
      return (Boolean)this.entityData.get(BLUE);
   }

   public void setBlue(boolean t) {
      this.entityData.set(BLUE, t);
   }

   public void push(Entity entity) {
      if (!this.isCaptured()) {
         super.push(entity);
      }
   }

   public void calculateEntityAnimation(boolean flying) {
      float f1 = (float)Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
      float f2 = Math.min(f1 * 8.0F, 1.0F);
      this.walkAnimation.update(f2, 0.4F);
   }

   public boolean canBeCollidedWith() {
      return AMCompat.isFullyConstructed(this) && this.isAlive();
   }

   public Vec3 collide(Vec3 movement) {
      if (!this.touchingUnloadedChunk() && this.isInWaterOrBubble()) {
         AABB aabb = this.mantleCollisionPart.getBoundingBox();
         List<VoxelShape> list = this.level().getEntityCollisions(this, aabb.expandTowards(movement));
         Vec3 vec3 = movement.lengthSqr() == 0.0 ? movement : collideBoundingBox(this, movement, aabb, this.level(), list);
         boolean flag = movement.x != vec3.x;
         boolean flag1 = movement.y != vec3.y;
         boolean flag2 = movement.z != vec3.z;
         boolean flag3 = this.onGround() || flag1 && movement.y < 0.0;
         if (this.maxUpStep() > 0.0F && flag3 && (flag || flag2)) {
            Vec3 vec31 = collideBoundingBox(this, new Vec3(movement.x, this.maxUpStep(), movement.z), aabb, this.level(), list);
            Vec3 vec32 = collideBoundingBox(this, new Vec3(0.0, this.maxUpStep(), 0.0), aabb.expandTowards(movement.x, 0.0, movement.z), this.level(), list);
            if (vec32.y < this.maxUpStep()) {
               Vec3 vec33 = collideBoundingBox(this, new Vec3(movement.x, 0.0, movement.z), aabb.move(vec32), this.level(), list).add(vec32);
               if (vec33.horizontalDistanceSqr() > vec31.horizontalDistanceSqr()) {
                  vec31 = vec33;
               }
            }

            if (vec31.horizontalDistanceSqr() > vec3.horizontalDistanceSqr()) {
               return vec31.add(collideBoundingBox(this, new Vec3(0.0, -vec31.y + movement.y, 0.0), aabb.move(vec31), this.level(), list));
            }
         }

         return vec3;
      } else {
         return super.collide(movement);
      }
   }

   public float getXRot() {
      return this.getSquidPitch();
   }

   @Override
   public boolean isMultipartEntity() {
      return true;
   }

   @Override
   public PartEntity<?>[] getParts() {
      return this.allParts;
   }

   public boolean attackEntityPartFrom(EntityGiantSquidPart part, DamageSource source, float amount) {
      return AMCompat.hurt(this, source, amount);
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
   }

   public void directPitch(double d0, double d1, double d2, double d3) {
      boolean shift = (Boolean)this.entityData.get(OVERRIDE_BODYROT);
      float add = shift ? 90.0F : -90.0F;
      float f = (float)(Mth.atan2(d2, d0) * 57.2957763671875) + add;
      this.setYRot(this.rotlerp(this.getYRot(), f, shift ? 10.0F : 5.0F));
   }

   public float getViewXRot(float partialTick) {
      return this.prevSquidPitch + (this.getSquidPitch() - this.prevSquidPitch) * partialTick;
   }

   public float getViewYRot(float partialTick) {
      return partialTick == 1.0F ? this.yBodyRot : Mth.lerp(partialTick, this.yBodyRotO, this.yBodyRot);
   }

   protected float rotlerp(float in, float target, float maxShift) {
      float f = Mth.wrapDegrees(target - in);
      if (f > maxShift) {
         f = maxShift;
      }

      if (f < -maxShift) {
         f = -maxShift;
      }

      float f1 = in + f;
      if (f1 < 0.0F) {
         f1 += 360.0F;
      } else if (f1 > 360.0F) {
         f1 -= 360.0F;
      }

      return f1;
   }

   private float getDepressureLevel() {
      MutableBlockPos blockpos$mutable = new MutableBlockPos();

      int waterLevelAbove;
      for (waterLevelAbove = 0; waterLevelAbove < 10; waterLevelAbove++) {
         BlockState blockstate = this.level().getBlockState(blockpos$mutable.set(this.getX(), this.getY() + waterLevelAbove, this.getZ()));
         if (!blockstate.getFluidState().is(FluidTags.WATER) && !blockstate.isSolid()) {
            break;
         }
      }

      return 1.0F - waterLevelAbove / 10.0F;
   }

   private boolean canFitAt(BlockPos pos) {
      return true;
   }

   public boolean tickCaptured(EntityCachalotWhale whale) {
      this.resetCapturedStateIn = 25;
      if (this.random.nextInt(13) == 0) {
         this.spawnInk();
         whale.hurt(this.damageSources().mobAttack(this), 4 + this.random.nextInt(4));
         if (this.random.nextFloat() <= 0.3F) {
            this.setCaptured(false);
            if (this.random.nextFloat() < 0.2F) {
               AMCompat.spawnAtLocation(this, (ItemLike)AMItemRegistry.LOST_TENTACLE.get());
            }

            return true;
         }
      }

      this.setCaptured(true);
      this.setSquidPitch(0.0F);
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      super.handleEntityEvent(id);
   }

   public boolean hurt(DamageSource src, float f) {
      if (super.hurt(src, f) && this.getLastHurtByMob() != null && !this.isCaptured() && this.random.nextBoolean()) {
         this.spawnInk();
         return true;
      } else {
         return false;
      }
   }

   private void spawnInk() {
      this.gameEvent(GameEvent.ENTITY_INTERACT);
      this.playSound(SoundEvents.SQUID_SQUIRT, this.getSoundVolume(), 0.5F * this.getVoicePitch());
      if (!this.level().isClientSide()) {
         Vec3 inkDirection = new Vec3(0.0, 0.0, 1.2000000476837158).xRot(-this.getXRot() * 0.017453292F).yRot(-this.yBodyRot * 0.017453292F);
         Vec3 vec3 = this.position().add(inkDirection);

         for (int i = 0; i < 30; i++) {
            Vec3 vec32 = inkDirection.add(this.random.nextFloat() - 0.5F, this.random.nextFloat() - 0.5F, this.random.nextFloat() - 0.5F)
               .scale(0.8 + this.random.nextFloat() * 2.0F);
            ((ServerLevel)this.level()).sendParticles(ParticleTypes.SQUID_INK, vec3.x, vec3.y + 0.5, vec3.z, 0, vec32.x, vec32.y, vec32.z, 0.10000000149011612);
         }
      }
   }

   private class AIAvoidWhales extends Goal {
      private EntityCachalotWhale whale;
      private Vec3 moveTo;
      private int runDelay;

      public AIAvoidWhales() {
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         if (EntityGiantSquid.this.isInWaterOrBubble()
            && !EntityGiantSquid.this.horizontalCollision
            && !EntityGiantSquid.this.isCaptured()
            && this.runDelay-- <= 0) {
            EntityCachalotWhale closest = null;
            float dist = 50.0F;

            for (EntityCachalotWhale dude : EntityGiantSquid.this.level()
               .getEntitiesOfClass(EntityCachalotWhale.class, EntityGiantSquid.this.getBoundingBox().inflate(dist))) {
               if (closest == null || dude.distanceTo(EntityGiantSquid.this) < closest.distanceTo(EntityGiantSquid.this)) {
                  closest = dude;
               }
            }

            if (closest != null) {
               this.whale = closest;
               return true;
            }

            this.runDelay = 50 + EntityGiantSquid.this.random.nextInt(50);
         }

         return false;
      }

      public boolean canContinueToUse() {
         return this.whale != null
            && this.whale.isAlive()
            && !EntityGiantSquid.this.horizontalCollision
            && EntityGiantSquid.this.distanceTo(this.whale) < 60.0F;
      }

      public void tick() {
         if (this.whale != null && this.whale.isAlive()) {
            double dist = EntityGiantSquid.this.distanceTo(this.whale);
            Vec3 vec = EntityGiantSquid.this.position().subtract(this.whale.position()).normalize();
            Vec3 vec2 = EntityGiantSquid.this.position().add(vec.scale(12 + EntityGiantSquid.this.random.nextInt(5)));
            EntityGiantSquid.this.getNavigation().moveTo(vec2.x, vec2.y, vec2.z, dist < 20.0 ? 1.899999976158142 : 1.2999999523162842);
         }
      }

      public void stop() {
         this.whale = null;
         this.moveTo = null;
      }
   }

   private class AIDeepwaterSwimming extends Goal {
      private BlockPos moveTo;

      public AIDeepwaterSwimming() {
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         if (!EntityGiantSquid.this.isVehicle()
            && (EntityGiantSquid.this.getTarget() == null || EntityGiantSquid.this.isGrabbing())
            && (EntityGiantSquid.this.isInWater() || EntityGiantSquid.this.isInLava())) {
            if (EntityGiantSquid.this.getNavigation().isDone() || EntityGiantSquid.this.getRandom().nextInt(30) == 0) {
               BlockPos found = this.findTargetPos();
               if (found != null) {
                  this.moveTo = found;
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }

      private BlockPos findTargetPos() {
         RandomSource r = EntityGiantSquid.this.getRandom();

         for (int i = 0; i < 15; i++) {
            BlockPos pos = EntityGiantSquid.this.blockPosition().offset(r.nextInt(16) - 8, r.nextInt(32) - 16, r.nextInt(16) - 8);
            if (EntityGiantSquid.this.level().isWaterAt(pos) && EntityGiantSquid.this.canFitAt(pos)) {
               return this.getDeeperTarget(pos);
            }
         }

         return null;
      }

      private BlockPos getDeeperTarget(BlockPos waterAtPos) {
         BlockPos surface = new BlockPos(waterAtPos);
         BlockPos seafloor = new BlockPos(waterAtPos);

         while (EntityGiantSquid.this.level().isWaterAt(surface) && surface.getY() < 320) {
            surface = surface.above();
         }

         while (EntityGiantSquid.this.level().isWaterAt(seafloor) && seafloor.getY() > -64) {
            seafloor = seafloor.below();
         }

         int distance = surface.getY() - seafloor.getY();
         if (distance < 10) {
            return waterAtPos;
         } else {
            int i = (int)(distance * 0.4);
            return seafloor.above(1 + EntityGiantSquid.this.getRandom().nextInt(i));
         }
      }

      public void start() {
         EntityGiantSquid.this.getNavigation().moveTo(this.moveTo.getX() + 0.5F, this.moveTo.getY() + 0.5F, this.moveTo.getZ() + 0.5F, 1.0);
      }

      public boolean canContinueToUse() {
         return false;
      }
   }

   private class AIMelee extends Goal {
      public boolean canUse() {
         return EntityGiantSquid.this.isInWaterOrBubble() && EntityGiantSquid.this.getTarget() != null && EntityGiantSquid.this.getTarget().isAlive();
      }

      public void tick() {
         EntityGiantSquid squid = EntityGiantSquid.this;
         LivingEntity target = EntityGiantSquid.this.getTarget();
         double dist = squid.distanceTo(target);
         if (squid.hasLineOfSight(target) && dist < 7.0) {
            squid.setGrabbing(true);
         } else {
            Vec3 moveBodyTo = target.position();
            squid.getNavigation().moveTo(moveBodyTo.x, moveBodyTo.y, moveBodyTo.z, 1.0);
         }

         if (dist < 14.0) {
            squid.entityData.set(EntityGiantSquid.OVERRIDE_BODYROT, true);
         } else {
            squid.entityData.set(EntityGiantSquid.OVERRIDE_BODYROT, false);
         }
      }

      public void stop() {
         EntityGiantSquid.this.entityData.set(EntityGiantSquid.OVERRIDE_BODYROT, false);
         EntityGiantSquid.this.setGrabbing(false);
      }
   }
}
