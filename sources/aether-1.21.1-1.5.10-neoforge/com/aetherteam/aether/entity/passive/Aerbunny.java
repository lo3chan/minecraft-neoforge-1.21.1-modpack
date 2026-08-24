package com.aetherteam.aether.entity.passive;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.entity.EntityUtil;
import com.aetherteam.aether.entity.ai.goal.FallingRandomStrollGoal;
import com.aetherteam.aether.mixin.mixins.common.accessor.ServerGamePacketListenerImplAccessor;
import com.aetherteam.aether.network.packet.serverbound.AerbunnyPuffPacket;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.network.PacketDistributor;

public class Aerbunny extends AetherAnimal {
   private static final EntityDataAccessor<Integer> DATA_PUFFINESS_ID = SynchedEntityData.defineId(Aerbunny.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_AFRAID_TIME_ID = SynchedEntityData.defineId(Aerbunny.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_FAST_FALLING_ID = SynchedEntityData.defineId(Aerbunny.class, EntityDataSerializers.BOOLEAN);
   private static final int MAXIMUM_PUFFS = 11;
   private int puffSubtract;
   @Nullable
   private Vec3 lastPos;

   public Aerbunny(EntityType<? extends Aerbunny> type, Level level) {
      super(type, level);
      this.moveControl = new Aerbunny.AerbunnyMoveControl(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new Aerbunny.RunWhenAfraid(this, 1.3));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.2, Ingredient.of(AetherTags.Items.AERBUNNY_TEMPTATION_ITEMS), false));
      this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(5, new FallingRandomStrollGoal(this, 1.0, 80));
   }

   public static Builder createMobAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0).add(Attributes.MOVEMENT_SPEED, 0.28);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_PUFFINESS_ID, 0);
      builder.define(DATA_AFRAID_TIME_ID, 0);
      builder.define(DATA_FAST_FALLING_ID, false);
   }

   public void tick() {
      super.tick();
      if (!this.isFastFalling()) {
         this.handleFallSpeed();
      } else if (this.onGround()) {
         this.setFastFalling(false);
      }

      this.setPuffiness(this.getPuffiness() - this.puffSubtract);
      if (this.getPuffiness() > 0) {
         this.puffSubtract = 1;
      } else {
         this.puffSubtract = 0;
         this.setPuffiness(0);
      }

      this.handlePlayerInput();
      boolean blockIntersection = false;
      if (this.getVehicle() != null) {
         AABB vehicleBounds = this.getVehicle().getBoundingBox();
         BlockPos minPos = BlockPos.containing(vehicleBounds.minX, vehicleBounds.minY, vehicleBounds.minZ);
         BlockPos maxPos = BlockPos.containing(vehicleBounds.maxX, vehicleBounds.maxY, vehicleBounds.maxZ);

         for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
            for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
               for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                  BlockPos pos = BlockPos.containing(x, y, z);
                  BlockState blockState = this.level().getBlockState(pos);
                  VoxelShape shape = blockState.getShape(this.getVehicle().level(), this.getVehicle().blockPosition());

                  for (AABB aabb : shape.toAabbs()) {
                     AABB offset = aabb.move(pos);
                     if (vehicleBounds.intersects(offset)) {
                        blockIntersection = true;
                     }
                  }
               }
            }
         }
      }

      if (this.getVehicle() != null && (this.getVehicle().onGround() || this.getVehicle().isInFluidType() || blockIntersection)) {
         this.lastPos = null;
      }
   }

   public void aiStep() {
      super.aiStep();
      if (this.getAfraidTime() > 0) {
         this.setAfraidTime(this.getAfraidTime() - 1);
      }
   }

   private void handleFallSpeed() {
      AttributeInstance gravity = this.getAttribute(Attributes.GRAVITY);
      if (gravity != null) {
         double fallSpeed = Math.max(gravity.getValue() * -1.25, -0.1);
         if (this.getDeltaMovement().y() < fallSpeed) {
            this.setDeltaMovement(this.getDeltaMovement().x(), fallSpeed, this.getDeltaMovement().z());
         }
      }
   }

   private void handlePlayerInput() {
      if (this.getVehicle() instanceof Player player) {
         if (player.isSpectator()) {
            this.stopRiding();
         }

         EntityUtil.copyRotations(this, player);
         player.resetFallDistance();
         if (!player.onGround() && !player.isFallFlying()) {
            AttributeInstance playerGravity = player.getAttribute(Attributes.GRAVITY);
            if (playerGravity != null && !player.getAbilities().flying && !player.isInFluidType() && playerGravity.getValue() > 0.02) {
               player.setDeltaMovement(player.getDeltaMovement().add(0.0, 0.05, 0.0));
            }

            if (this.level().isClientSide()) {
               AetherPlayerAttachment data = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
               if (player.getDeltaMovement().y() <= 0.0) {
                  if (this.lastPos == null) {
                     this.lastPos = this.position();
                  }

                  if (!player.onGround() && data.isJumping() && player.getDeltaMovement().y() <= 0.0 && this.position().y() < this.lastPos.y() - 1.1) {
                     player.setDeltaMovement(player.getDeltaMovement().x(), 0.125, player.getDeltaMovement().z());
                     PacketDistributor.sendToServer(new AerbunnyPuffPacket(this.getId()), new CustomPacketPayload[0]);
                     this.spawnExplosionParticle();
                     this.lastPos = null;
                  }
               }
            }
         } else if (player.isFallFlying()) {
            this.stopRiding();
         }

         if (player instanceof ServerPlayer serverPlayer) {
            ServerGamePacketListenerImplAccessor serverGamePacketListenerImplAccessor = (ServerGamePacketListenerImplAccessor)serverPlayer.connection;
            serverGamePacketListenerImplAccessor.aether$setAboveGroundTickCount(0);
         }
      }
   }

   public void baseTick() {
      super.baseTick();
      if (this.isAlive()
         && this.isPassenger()
         && this.getVehicle() != null
         && this.getVehicle().isEyeInFluidType((FluidType)NeoForgeMod.WATER_TYPE.value())
         && !this.level()
            .getBlockState(BlockPos.containing(this.getVehicle().getX(), this.getVehicle().getEyeY(), this.getVehicle().getZ()))
            .is(Blocks.BUBBLE_COLUMN)) {
         this.stopRiding();
      }
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      InteractionResult result = super.mobInteract(player, hand);
      return !(this.getVehicle() instanceof Player vehicle && !vehicle.equals(player))
            && (player.isShiftKeyDown() || result == InteractionResult.PASS || result == InteractionResult.FAIL)
            && !super.isInWall()
         ? this.ridePlayer(player)
         : result;
   }

   private InteractionResult ridePlayer(Player player) {
      if (!this.isBaby()) {
         if (this.isPassenger()) {
            this.stopRiding();
            this.setFastFalling(true);
            Vec3 playerMovement = player.getDeltaMovement();
            this.setDeltaMovement(playerMovement.x() * 5.0, playerMovement.y() * 0.5 + 0.5, playerMovement.z() * 5.0);
         } else if (this.startRiding(player)) {
            ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).setMountedAerbunny(this);
            this.level()
               .playSound(
                  player,
                  this,
                  (SoundEvent)AetherSoundEvents.ENTITY_AERBUNNY_LIFT.get(),
                  SoundSource.NEUTRAL,
                  1.0F,
                  (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F + 1.0F
               );
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public void stopRiding() {
      if (this.getVehicle() instanceof Player player) {
         ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).setMountedAerbunny(null);
      }

      super.stopRiding();
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean flag = super.hurt(source, amount);
      if (flag && source.getEntity() instanceof Player) {
         this.setAfraidTime(100 + this.getRandom().nextInt(50));
      }

      return flag;
   }

   protected void midairJump() {
      Vec3 motion = this.getDeltaMovement();
      if (motion.y() < 0.0) {
         this.puff();
         this.level().broadcastEntityEvent(this, (byte)70);
      }

      this.setDeltaMovement(new Vec3(motion.x(), 0.25, motion.z()));
   }

   public void puff() {
      if (this.level() instanceof ServerLevel) {
         this.setPuffiness(11);
      }
   }

   private void spawnExplosionParticle() {
      for (int i = 0; i < 5; i++) {
         EntityUtil.spawnMovementExplosionParticles(this);
      }
   }

   public int getPuffiness() {
      return (Integer)this.entityData.get(DATA_PUFFINESS_ID);
   }

   public void setPuffiness(int puffiness) {
      this.entityData.set(DATA_PUFFINESS_ID, puffiness);
   }

   public int getAfraidTime() {
      return (Integer)this.entityData.get(DATA_AFRAID_TIME_ID);
   }

   public void setAfraidTime(int afraidTime) {
      this.entityData.set(DATA_AFRAID_TIME_ID, afraidTime);
   }

   public boolean isFastFalling() {
      return (Boolean)this.entityData.get(DATA_FAST_FALLING_ID);
   }

   public void setFastFalling(boolean fastFalling) {
      this.entityData.set(DATA_FAST_FALLING_ID, fastFalling);
   }

   public int getPuffSubtract() {
      return this.puffSubtract;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return (SoundEvent)AetherSoundEvents.ENTITY_AERBUNNY_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_AERBUNNY_DEATH.get();
   }

   protected float getFlyingSpeed() {
      return this.getSpeed() * 0.216F;
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AetherTags.Items.AERBUNNY_TEMPTATION_ITEMS);
   }

   public boolean canRiderInteract() {
      return true;
   }

   public Vec3 getVehicleAttachmentPoint(Entity entity) {
      Vec3 attachmentPoint = super.getVehicleAttachmentPoint(entity);
      Entity vehicle = entity.getVehicle();
      if (entity.getPose() != Pose.CROUCHING) {
         attachmentPoint = attachmentPoint.subtract(0.0, 0.05000000074505806, 0.0);
      } else if (vehicle != null) {
         attachmentPoint = attachmentPoint.subtract(0.0, 0.3499999940395355, 0.0);
      }

      return attachmentPoint;
   }

   public boolean isPickable() {
      return this.getVehicle() instanceof Player player
         ? player.getBoundingBox()
            .expandTowards(player.getViewVector(0.0F))
            .contains(this.getBoundingBox().getCenter().add(0.0, this.getBoundingBox().getSize() / 2.0, 0.0))
         : true;
   }

   public boolean isInvulnerableTo(DamageSource damageSource) {
      return this.getVehicle() != null && this.getVehicle() == damageSource.getEntity() || super.isInvulnerableTo(damageSource);
   }

   public boolean isInWall() {
      return !this.isPassenger() && super.isInWall();
   }

   public void checkDespawn() {
      if (this.getVehicle() == null) {
         super.checkDespawn();
      }
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob entity) {
      return (AgeableMob)((EntityType)AetherEntityTypes.AERBUNNY.get()).create(level);
   }

   public void handleEntityEvent(byte id) {
      if (id == 70) {
         this.spawnExplosionParticle();
      } else {
         super.handleEntityEvent(id);
      }
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putInt("AfraidTime", this.getAfraidTime());
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.contains("AfraidTime")) {
         this.setAfraidTime(tag.getInt("AfraidTime"));
      }
   }

   public static class AerbunnyMoveControl extends MoveControl {
      private final Aerbunny aerbunny;

      public AerbunnyMoveControl(Aerbunny aerbunny) {
         super(aerbunny);
         this.aerbunny = aerbunny;
      }

      public void tick() {
         super.tick();
         if (this.aerbunny.zza != 0.0F) {
            if (this.aerbunny.onGround()) {
               this.aerbunny.getJumpControl().jump();
            } else {
               int x = Mth.floor(this.aerbunny.getX());
               int y = Mth.floor(this.aerbunny.getBoundingBox().minY);
               int z = Mth.floor(this.aerbunny.getZ());
               if (this.checkForSurfaces(this.aerbunny.level(), x, y, z) && !this.aerbunny.horizontalCollision) {
                  this.aerbunny.midairJump();
               }
            }
         }
      }

      private boolean checkForSurfaces(Level level, int x, int y, int z) {
         MutableBlockPos pos = new MutableBlockPos(x, y, z);
         return level.getBlockState(pos.setY(y - 1)).isAir()
            ? false
            : level.getBlockState(pos.setY(y + 2)).isAir() && level.getBlockState(pos.setY(y + 1)).isAir();
      }
   }

   public static class RunWhenAfraid extends Goal {
      private final Aerbunny aerbunny;
      private final double speedModifier;

      public RunWhenAfraid(Aerbunny aerbunny, double speedModifier) {
         this.aerbunny = aerbunny;
         this.speedModifier = speedModifier;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return this.aerbunny.getAfraidTime() > 0;
      }

      public boolean canContinueToUse() {
         return !this.aerbunny.getNavigation().isDone() && this.aerbunny.getRandom().nextInt(20) != 0;
      }

      public void start() {
         LivingEntity attacker = this.aerbunny.level().getNearestPlayer(this.aerbunny, 12.0);
         if (attacker != null) {
            Vec3 position = this.aerbunny.position();
            double angle = Mth.atan2(position.x() - attacker.getX(), position.z() - attacker.getZ());
            float angleOffset = this.aerbunny.getRandom().nextFloat() * 2.0F - 1.0F;
            angle += angleOffset * 0.75;
            double x = position.x() + Math.sin(angle) * 8.0;
            double z = position.z() + Math.cos(angle) * 8.0;
            boolean flag = this.aerbunny.getNavigation().moveTo(x, this.aerbunny.getY(), z, this.speedModifier);
            if (!flag) {
               this.aerbunny.getLookControl().setLookAt(attacker, 30.0F, 30.0F);
            }
         }
      }

      public void tick() {
         if (this.aerbunny.level() instanceof ServerLevel serverLevel && this.aerbunny.getRandom().nextInt(4) == 0) {
            serverLevel.sendParticles(
               ParticleTypes.SPLASH, this.aerbunny.getRandomX(0.5), this.aerbunny.getRandomY(), this.aerbunny.getRandomZ(0.5), 2, 0.0, 0.0, 0.0, 0.0
            );
         }
      }
   }
}
