package com.aetherteam.aether.entity.monster;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.entity.EntityUtil;
import com.aetherteam.aether.entity.MountableMob;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.EquipmentUtil;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Swet extends Slime implements MountableMob {
   private static final EntityDataAccessor<Boolean> DATA_PLAYER_JUMPED_ID = SynchedEntityData.defineId(Swet.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_MOUNT_JUMPING_ID = SynchedEntityData.defineId(Swet.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_MID_JUMP_ID = SynchedEntityData.defineId(Swet.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> DATA_WATER_DAMAGE_SCALE_ID = SynchedEntityData.defineId(Swet.class, EntityDataSerializers.FLOAT);
   private int ascendTimer;
   private boolean wasOnGround;
   private int jumpTimer;
   private float swetHeight = 1.0F;
   private float swetHeightO = 1.0F;
   private float swetWidth = 1.0F;
   private float swetWidthO = 1.0F;

   public Swet(EntityType<? extends Swet> type, Level level) {
      super(type, level);
      this.moveControl = new Swet.SwetMoveControl(this);
      this.xpReward = 5;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new Swet.ConsumeGoal(this));
      this.goalSelector.addGoal(1, new Swet.HuntGoal(this));
      this.goalSelector.addGoal(2, new Swet.SwetRandomDirectionGoal(this));
      this.goalSelector.addGoal(4, new Swet.SwetKeepOnJumpingGoal(this));
      this.targetSelector
         .addGoal(
            1,
            new NearestAttackableTargetGoal(
               this, Player.class, true, target -> !this.isFriendlyTowardEntity(target) && !(target.getRootVehicle() instanceof Swet)
            )
         );
   }

   public static Builder createMobAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 12.0)
         .add(Attributes.MOVEMENT_SPEED, 0.4)
         .add(Attributes.FOLLOW_RANGE, 14.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_PLAYER_JUMPED_ID, false);
      builder.define(DATA_MOUNT_JUMPING_ID, false);
      builder.define(DATA_MID_JUMP_ID, false);
      builder.define(DATA_WATER_DAMAGE_SCALE_ID, 0.0F);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
      if (DATA_WATER_DAMAGE_SCALE_ID.equals(dataAccessor)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(dataAccessor);
   }

   public static boolean checkSwetSpawnRules(EntityType<? extends Swet> swet, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
      return level.getBlockState(pos.below()).is(AetherTags.Blocks.SWET_SPAWNABLE_ON)
         && level.getRawBrightness(pos, 0) > 8
         && level.getDifficulty() != Difficulty.PEACEFUL
         && (reason != MobSpawnType.NATURAL || !inRadiusOfBanner(level, pos, 40) && !inRadiusOfSwetCape(level, pos, 40));
   }

   private static boolean inRadiusOfBanner(LevelAccessor level, BlockPos pos, int radius) {
      for (ChunkPos chunk : ChunkPos.rangeClosed(new ChunkPos(pos), radius).toList()) {
         ChunkAccess chunkAccess = level.getChunk(chunk.x, chunk.z, ChunkStatus.FULL, false);
         if (chunkAccess != null) {
            for (BlockPos blockEntityPos : chunkAccess.getBlockEntitiesPos()) {
               if (blockEntityPos.distSqr(pos) <= radius * radius) {
                  BlockEntity blockEntity = level.getBlockEntity(blockEntityPos);
                  if (blockEntity instanceof BannerBlockEntity bannerBlockEntity
                     && blockEntity.getBlockState().is(Blocks.BLACK_BANNER)
                     && ItemStack.matches(bannerBlockEntity.getItem(), AetherItems.createSwetBannerItemStack(level.holderLookup(Registries.BANNER_PATTERN)))) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private static boolean inRadiusOfSwetCape(LevelAccessor level, BlockPos pos, int radius) {
      return !level.getEntities(
            EntityTypeTest.forClass(ArmorStand.class), AABB.ofSize(pos.getCenter(), radius * 2, radius * 2, radius * 2), EquipmentUtil::hasSwetCape
         )
         .isEmpty();
   }

   public void tick() {
      if (this.isInWater()) {
         this.spawnDissolveParticles();
         if (this.getWaterDamageScale() < 0.9F) {
            this.setWaterDamageScale(this.getWaterDamageScale() + 0.02F);
         }
      }

      if (this.getWaterDamageScale() >= 0.9F && !this.level().isClientSide()) {
         this.level().broadcastEntityEvent(this, (byte)60);
         this.remove(RemovalReason.KILLED);
      }

      this.tick(this);
      this.riderTick(this);
      super.tick();
      if (!this.onGround() && this.getDeltaMovement().y() < 0.05 && this.ascendTimer > 0) {
         this.setDeltaMovement(this.getDeltaMovement().x() * 1.2, 0.07, this.getDeltaMovement().z() * 1.2);
         this.ascendTimer--;
      }

      if (this.onGround()) {
         this.ascendTimer = 10;
      }

      if (!this.hasPrey() && this.canSpawnSplashParticles() && this.level().isClientSide()) {
         double d = (float)this.getX() + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.3F;
         double d1 = (float)this.getY() + this.getBbHeight();
         double d2 = (float)this.getZ() + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.3F;
         this.level().addParticle(ParticleTypes.SPLASH, d, d1 - 0.25, d2, 0.0, 0.0, 0.0);
      }

      if (!this.isNoAi()) {
         this.setMidJump(!this.onGround());
         if (this.level().isClientSide()) {
            this.swetHeightO = this.swetHeight;
            this.swetWidthO = this.swetWidth;
            if (this.getMidJump()) {
               this.jumpTimer++;
            } else {
               this.jumpTimer = 0;
            }

            if (this.getJumpTimer() > 1) {
               this.swetHeight = 1.425F;
               this.swetWidth = 0.875F;
               float scale = Math.min(this.getJumpTimer(), 10);
               if (this.getJumpTimer() > 2) {
                  this.swetHeight -= 0.04F * scale;
                  this.swetWidth += 0.04F * scale;
               }

               if (this.getJumpTimer() > 3) {
                  this.swetHeight -= 0.02F * scale;
                  this.swetWidth += 0.02F * scale;
               }
            } else {
               this.swetHeight = this.swetHeight < 1.0F ? this.swetHeight + 0.25F : 1.0F;
               this.swetWidth = this.swetWidth > 1.0F ? this.swetWidth - 0.25F : 1.0F;
            }
         }

         this.wasOnGround = this.onGround();
      }

      if (this.isFriendly()) {
         this.resetFallDistance();
      }
   }

   public void aiStep() {
      super.aiStep();
      if (this.getTarget() != null && (this.hasPrey() || this.isFriendlyTowardEntity(this.getTarget()) || this.getTarget().getRootVehicle() instanceof Swet)) {
         this.setTarget(null);
      }
   }

   public void travel(Vec3 vector) {
      this.travel(this, vector);
      if (this.isAlive()) {
         LivingEntity entity = this.getControllingPassenger();
         if (this.isVehicle() && entity != null) {
            if (this.onGround() && !this.getPlayerJumped() && (this.getDeltaMovement().x() != 0.0 || this.getDeltaMovement().z() != 0.0)) {
               this.setDeltaMovement(this.getDeltaMovement().x(), 0.41999998688697815, this.getDeltaMovement().z());
            }

            this.resetFallDistance();
         }
      }
   }

   @Override
   public void travelWithInput(Vec3 motion) {
      super.travel(motion);
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      if (!this.level().isClientSide() && !this.hasPrey() && this.isFriendlyTowardEntity(player) && this.getScale() >= super.getScale()) {
         this.consumePassenger(player);
      }

      return InteractionResult.PASS;
   }

   public void consumePassenger(LivingEntity livingEntity) {
      this.playSound((SoundEvent)AetherSoundEvents.ENTITY_SWET_ATTACK.get(), 0.5F, (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F + 1.0F);
      EntityUtil.copyRotations(livingEntity, this);
      this.setDeltaMovement(livingEntity.getDeltaMovement());
      livingEntity.startRiding(this, true);
   }

   public void spawnDissolveParticles() {
      if (this.level() instanceof ServerLevel level) {
         level.broadcastEntityEvent(this, (byte)70);
      }
   }

   public boolean getMidJump() {
      return (Boolean)this.getEntityData().get(DATA_MID_JUMP_ID);
   }

   public void setMidJump(boolean midJump) {
      this.getEntityData().set(DATA_MID_JUMP_ID, midJump);
   }

   public float getWaterDamageScale() {
      return (Float)this.getEntityData().get(DATA_WATER_DAMAGE_SCALE_ID);
   }

   public void setWaterDamageScale(float scale) {
      this.getEntityData().set(DATA_WATER_DAMAGE_SCALE_ID, scale);
   }

   @Override
   public boolean getPlayerJumped() {
      return (Boolean)this.getEntityData().get(DATA_PLAYER_JUMPED_ID);
   }

   @Override
   public void setPlayerJumped(boolean playerJumped) {
      this.getEntityData().set(DATA_PLAYER_JUMPED_ID, playerJumped);
   }

   @Override
   public boolean isMountJumping() {
      return (Boolean)this.getEntityData().get(DATA_MOUNT_JUMPING_ID);
   }

   @Override
   public void setMountJumping(boolean isMountJumping) {
      this.getEntityData().set(DATA_MOUNT_JUMPING_ID, isMountJumping);
   }

   public float getSwetHeight() {
      return this.swetHeight;
   }

   public float getSwetHeightO() {
      return this.swetHeightO;
   }

   public float getSwetWidth() {
      return this.swetWidth;
   }

   public float getSwetWidthO() {
      return this.swetWidthO;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return (SoundEvent)AetherSoundEvents.ENTITY_SWET_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_SWET_DEATH.get();
   }

   protected SoundEvent getSquishSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_SWET_SQUISH.get();
   }

   public boolean hasPrey() {
      return this.getFirstPassenger() != null;
   }

   public boolean canSpawnSplashParticles() {
      return true;
   }

   public boolean isFriendly() {
      return this.getControllingPassenger() != null;
   }

   public boolean isFriendlyTowardEntity(LivingEntity entity) {
      return EquipmentUtil.hasSwetPacifyingAccessory(entity);
   }

   public int getJumpTimer() {
      return this.jumpTimer;
   }

   public int getJumpDelay() {
      return this.getRandom().nextInt(20) + 10;
   }

   public float getJumpPower() {
      LivingEntity entity = this.getControllingPassenger();
      return this.isVehicle() && entity != null ? 0.5F : 0.325F;
   }

   @Override
   public double getMountJumpStrength() {
      return 1.2;
   }

   @Override
   public double jumpFactor() {
      return this.getBlockJumpFactor();
   }

   @Override
   public boolean canJump() {
      return this.onGround() && this.isFriendly();
   }

   @Override
   public float getSteeringSpeed() {
      return (float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.20999999344348907);
   }

   public float getFlyingSpeed() {
      return this.getControllingPassenger() != null ? this.getSteeringSpeed() * 0.25F : 0.02F;
   }

   public boolean canRiderInteract() {
      return true;
   }

   public Vec3 getPassengerRidingPosition(Entity entity) {
      return super.getPassengerRidingPosition(entity).add(0.0, 0.725, 0.0);
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      return this.getFirstPassenger() instanceof LivingEntity livingEntity && this.isFriendlyTowardEntity(livingEntity) ? livingEntity : null;
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
      return this.isFriendlyTowardEntity(livingEntity) ? super.getDismountLocationForPassenger(livingEntity) : this.position();
   }

   public int getSize() {
      return this.isVehicle() ? 2 : 1;
   }

   public void setSize(int size, boolean resetHealth) {
   }

   public float getScale() {
      return super.getScale() - super.getScale() * this.getWaterDamageScale();
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      return super.getDefaultDimensions(pose).scale(this.getScale());
   }

   protected boolean isDealsDamage() {
      return false;
   }

   protected boolean shouldDespawnInPeaceful() {
      return true;
   }

   protected boolean spawnCustomParticles() {
      return true;
   }

   public void handleEntityEvent(byte id) {
      if (id == 70) {
         for (int i = 0; i < 10; i++) {
            double f = this.getRandom().nextFloat() * 6.2831855F;
            double f1 = this.getRandom().nextFloat() * this.swetWidth + 0.25F;
            double f2 = this.getRandom().nextFloat() * this.swetHeight - this.getRandom().nextGaussian() * 0.02 * 10.0;
            double f3 = Mth.sin((float)f) * f1;
            double f4 = Mth.cos((float)f) * f1;
            this.level()
               .addParticle(
                  ParticleTypes.SPLASH,
                  this.getX() + f3,
                  this.getY() + f2,
                  this.getZ() + f4,
                  f3 * 1.5 + this.getDeltaMovement().x(),
                  4.0,
                  f4 * 1.5 + this.getDeltaMovement().z()
               );
         }
      } else if (id == 71) {
         this.absMoveTo(this.getX(), this.getY(), this.getZ());
      } else {
         super.handleEntityEvent(id);
      }
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putFloat("WaterDamageScale", this.getWaterDamageScale());
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.contains("WaterDamageScale")) {
         this.setWaterDamageScale(tag.getFloat("WaterDamageScale"));
      }
   }

   public static class ConsumeGoal extends Goal {
      private final Swet swet;
      private int jumps = 0;
      private float chosenDegrees = 0.0F;

      public ConsumeGoal(Swet swet) {
         this.swet = swet;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return this.swet.hasPrey() && this.swet.getPassengers().getFirst() instanceof LivingEntity passenger && !this.swet.isFriendlyTowardEntity(passenger);
      }

      public void tick() {
         if (this.jumps <= 3) {
            if (this.swet.onGround()) {
               this.swet.level().broadcastEntityEvent(this.swet, (byte)71);
               this.swet
                  .playSound(
                     (SoundEvent)AetherSoundEvents.ENTITY_SWET_JUMP.get(),
                     1.0F,
                     ((this.swet.getRandom().nextFloat() - this.swet.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.8F
                  );
               this.chosenDegrees = this.swet.getRandom().nextInt(360);
               if (this.jumps == 0) {
                  this.swet.setDeltaMovement(this.swet.getDeltaMovement().add(0.0, 0.65, 0.0));
               } else if (this.jumps == 1) {
                  this.swet.setDeltaMovement(this.swet.getDeltaMovement().add(0.0, 0.75, 0.0));
               } else if (this.jumps == 2) {
                  this.swet.setDeltaMovement(this.swet.getDeltaMovement().add(0.0, 1.55, 0.0));
               } else {
                  ((Entity)this.swet.getPassengers().getFirst()).stopRiding();
                  this.swet.spawnDissolveParticles();
                  this.swet.discard();
               }

               if (!this.swet.getMidJump()) {
                  this.jumps++;
               }
            }

            if (!this.swet.wasOnGround && this.swet.getJumpTimer() < 6) {
               if (this.jumps == 1) {
                  this.moveHorizontal(0.0F, 0.1F, this.chosenDegrees);
               } else if (this.jumps == 2) {
                  this.moveHorizontal(0.0F, 0.15F, this.chosenDegrees);
               } else if (this.jumps == 3) {
                  this.moveHorizontal(0.0F, 0.3F, this.chosenDegrees);
               }
            }
         }
      }

      public void moveHorizontal(float strafe, float forward, float rotation) {
         float f = Mth.square(strafe) + Mth.square(forward);
         f = Mth.sqrt(f);
         if (f < 1.0F) {
            f = 1.0F;
         }

         strafe *= f;
         forward *= f;
         float f1 = Mth.sin(rotation * 0.017453292F);
         float f2 = Mth.cos(rotation * 0.017453292F);
         this.swet.setDeltaMovement(strafe * f2 - forward * f1, this.swet.getDeltaMovement().y(), forward * f2 + strafe * f1);
         if (this.swet.getMoveControl() instanceof Swet.SwetMoveControl swetMoveControl) {
            swetMoveControl.yRot = rotation % 360.0F;
         }
      }
   }

   public static class HuntGoal extends Goal {
      private final Swet swet;

      public HuntGoal(Swet swet) {
         this.swet = swet;
         this.setFlags(EnumSet.of(Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity target = this.swet.getTarget();
         return !this.swet.hasPrey()
               && target != null
               && target.isAlive()
               && !this.swet.isFriendlyTowardEntity(target)
               && !(target instanceof Player player && player.getAbilities().invulnerable)
            ? this.swet.getMoveControl() instanceof Swet.SwetMoveControl
            : false;
      }

      public boolean canContinueToUse() {
         LivingEntity target = this.swet.getTarget();
         if (!this.swet.hasPrey() && target != null && target.isAlive()) {
            return target instanceof Player player && player.getAbilities().invulnerable ? false : !this.swet.isFriendlyTowardEntity(target);
         } else {
            return false;
         }
      }

      public void tick() {
         if (this.swet.getMoveControl() instanceof Swet.SwetMoveControl swetMoveControl) {
            LivingEntity target = this.swet.getTarget();
            if (target != null) {
               this.swet.lookAt(target, 10.0F, 10.0F);
               swetMoveControl.setDirection(this.swet.getYRot(), true);
               swetMoveControl.setWantedMovement(1.0);
               if (this.swet.getBoundingBox().intersects(target.getBoundingBox())) {
                  this.swet.consumePassenger(target);
               }
            }
         }
      }
   }

   public static class SwetKeepOnJumpingGoal extends Goal {
      private final Swet swet;

      public SwetKeepOnJumpingGoal(Swet swetEntity) {
         this.swet = swetEntity;
         this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
      }

      public boolean canUse() {
         return !this.swet.isPassenger() && this.swet.getMoveControl() instanceof Swet.SwetMoveControl moveHelperController && moveHelperController.canJump;
      }

      public void tick() {
         if (this.swet.getMoveControl() instanceof Swet.SwetMoveControl swetMoveControl) {
            swetMoveControl.setWantedMovement(1.0);
         }
      }
   }

   public static class SwetMoveControl extends MoveControl {
      private float yRot;
      private int jumpDelay;
      private final Swet swet;
      private boolean isAggressive;
      private boolean canJump;

      public SwetMoveControl(Swet swet) {
         super(swet);
         this.swet = swet;
         this.yRot = 180.0F * swet.getYRot() / 3.1415927F;
      }

      public void setDirection(float yRot, boolean isAggressive) {
         this.yRot = yRot;
         this.isAggressive = isAggressive;
      }

      public void setWantedMovement(double speed) {
         this.speedModifier = speed;
         this.operation = Operation.MOVE_TO;
      }

      public void setCanJump(boolean canJump) {
         this.canJump = canJump;
      }

      public void tick() {
         if (!this.swet.isFriendly()) {
            this.swet.setYRot(this.rotlerp(this.swet.getYRot(), this.yRot, 90.0F));
            this.swet.setYHeadRot(this.swet.getYRot());
            this.swet.setYBodyRot(this.swet.getYRot());
            if (this.operation != Operation.MOVE_TO) {
               this.swet.setZza(0.0F);
            } else {
               this.operation = Operation.WAIT;
               if (this.swet.onGround()) {
                  this.swet.setSpeed((float)(this.speedModifier * this.swet.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                  if (this.jumpDelay-- <= 0) {
                     this.jumpDelay = this.swet.getJumpDelay();
                     if (this.isAggressive) {
                        this.jumpDelay /= 6;
                     }

                     this.swet.getJumpControl().jump();
                     this.swet
                        .playSound(
                           (SoundEvent)AetherSoundEvents.ENTITY_SWET_JUMP.get(),
                           1.0F,
                           ((this.swet.getRandom().nextFloat() - this.swet.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.8F
                        );
                  } else {
                     this.swet.xxa = 0.0F;
                     this.swet.zza = 0.0F;
                     this.swet.setSpeed(0.0F);
                  }
               } else {
                  this.swet.setSpeed((float)(this.speedModifier * this.swet.getAttributeValue(Attributes.MOVEMENT_SPEED)));
               }
            }
         }
      }
   }

   public static class SwetRandomDirectionGoal extends Goal {
      private final Swet swet;
      private float chosenDegrees;
      private int nextRandomizeTime;

      public SwetRandomDirectionGoal(Swet swet) {
         this.swet = swet;
         this.setFlags(EnumSet.of(Flag.LOOK));
      }

      public boolean canUse() {
         return this.swet.getTarget() == null
            && (this.swet.onGround() || this.swet.isInFluidType() || this.swet.hasEffect(MobEffects.LEVITATION))
            && this.swet.getMoveControl() instanceof Swet.SwetMoveControl;
      }

      public void tick() {
         Swet.SwetMoveControl moveHelperController = (Swet.SwetMoveControl)this.swet.getMoveControl();
         float rot = moveHelperController.yRot;
         Vec3 offset = new Vec3(-Math.sin(rot * 0.017453292F) * 2.0, 0.0, Math.cos(rot * 0.017453292F) * 2.0);
         BlockPos offsetPos = BlockPos.containing(this.swet.position().add(offset));
         if (this.swet.level().getHeight(Types.WORLD_SURFACE, offsetPos.getX(), offsetPos.getZ()) < offsetPos.getY() - this.swet.getMaxFallDistance()) {
            this.nextRandomizeTime = this.adjustedTickDelay(40 + this.swet.getRandom().nextInt(60));
            this.chosenDegrees += 180.0F;
            moveHelperController.setCanJump(false);
         } else {
            if (--this.nextRandomizeTime <= 0) {
               this.nextRandomizeTime = this.adjustedTickDelay(40 + this.swet.getRandom().nextInt(60));
               this.chosenDegrees = this.swet.getRandom().nextInt(360);
            }

            moveHelperController.setCanJump(true);
         }

         moveHelperController.setDirection(this.chosenDegrees, false);
      }
   }
}
