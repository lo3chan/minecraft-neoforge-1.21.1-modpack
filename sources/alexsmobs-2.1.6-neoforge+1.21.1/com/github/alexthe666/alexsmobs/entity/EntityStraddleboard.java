package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.enchantment.AMEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil.FoundRectangle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.MovementEmission;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityStraddleboard extends Entity implements PlayerRideableJumping {
   private static final EntityDataAccessor<ItemStack> ITEMSTACK = SynchedEntityData.defineId(EntityStraddleboard.class, EntityDataSerializers.ITEM_STACK);
   private static final EntityDataAccessor<Integer> TIME_SINCE_HIT = SynchedEntityData.defineId(EntityStraddleboard.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(EntityStraddleboard.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DEFAULT_COLOR = SynchedEntityData.defineId(EntityStraddleboard.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> BOARD_ROT = SynchedEntityData.defineId(EntityStraddleboard.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> REMOVE_SOON = SynchedEntityData.defineId(EntityStraddleboard.class, EntityDataSerializers.BOOLEAN);
   public float prevBoardRot = 0.0F;
   private boolean rocking;
   private float rockingIntensity;
   private float rockingAngle;
   private float prevRockingAngle;
   private int extinguishTimer = 0;
   private int jumpFor = 0;
   private int lSteps;
   private double lx;
   private double ly;
   private double lz;
   private double lyr;
   private double lxr;
   private double lxd;
   private double lyd;
   private double lzd;
   private int rideForTicks = 0;
   private float boardForwards = 0.0F;
   private int removeIn;
   private Player returnToPlayer = null;

   public EntityStraddleboard(EntityType<?> p_i48580_1_, Level p_i48580_2_) {
      super(p_i48580_1_, p_i48580_2_);
      this.blocksBuilding = true;
   }

   public EntityStraddleboard(Level worldIn, double x, double y, double z) {
      this(AMEntityRegistry.STRADDLEBOARD.get(), worldIn);
      this.setPos(x, y, z);
      this.setDeltaMovement(Vec3.ZERO);
      this.xo = x;
      this.yo = y;
      this.zo = z;
   }

   public static boolean canVehicleCollide(Entity p_242378_0_, Entity entity) {
      return (AMCompat.canBeCollidedWith(entity, p_242378_0_) || entity.isPushable()) && !p_242378_0_.isPassengerOfSameVehicle(entity);
   }

   public EntityDimensions getDimensions(Pose pose) {
      EntityDimensions dimensions = this.getType().getDimensions();
      return dimensions.withEyeHeight(dimensions.height());
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(TIME_SINCE_HIT, 0);
      builder.define(ITEMSTACK, new ItemStack((ItemLike)AMItemRegistry.STRADDLEBOARD.get()));
      builder.define(DEFAULT_COLOR, true);
      builder.define(COLOR, 0);
      builder.define(BOARD_ROT, 0.0F);
      builder.define(REMOVE_SOON, false);
   }

   public boolean shouldRiderSit() {
      return false;
   }

   public boolean canCollideWith(Entity entity) {
      return canVehicleCollide(this, entity);
   }

   public Vec3 getRelativePortalPosition(Axis axis, FoundRectangle result) {
      return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(axis, result));
   }

   public Vec3 getPassengerRidingPosition(Entity passenger) {
      return new Vec3(this.getX(), this.getY() + 0.5, this.getZ());
   }

   public float getBoardRot() {
      return (Float)this.entityData.get(BOARD_ROT);
   }

   public void setBoardRot(float f) {
      this.entityData.set(BOARD_ROT, f);
   }

   public boolean hurt(DamageSource source, float amount) {
      if (AMCompat.isInvulnerableTo(this, source)) {
         return false;
      } else if (!this.level().isClientSide() && !this.isRemoved()) {
         this.entityData.set(REMOVE_SOON, true);
         return true;
      } else {
         return true;
      }
   }

   private ItemStack getItemBoard() {
      return this.getItemStack();
   }

   public void push(Entity entityIn) {
      if (entityIn instanceof EntityStraddleboard) {
         if (entityIn.getBoundingBox().minY < this.getBoundingBox().maxY) {
            super.push(entityIn);
         }
      } else if (entityIn.getBoundingBox().minY <= this.getBoundingBox().minY) {
         super.push(entityIn);
      }
   }

   public boolean isRemoveLogic() {
      return (Boolean)this.entityData.get(REMOVE_SOON) || this.isRemoved();
   }

   public boolean canBeCollidedWith() {
      return AMCompat.isFullyConstructed(this) && !this.isRemoveLogic();
   }

   public boolean isPushable() {
      return !this.isRemoveLogic();
   }

   public boolean isPickable() {
      return !this.isRemoveLogic();
   }

   public boolean shouldBeSaved() {
      return !this.isRemoveLogic();
   }

   public boolean isAttackable() {
      return !this.isRemoveLogic();
   }

   public boolean isDefaultColor() {
      return (Boolean)this.entityData.get(DEFAULT_COLOR);
   }

   public void setDefaultColor(boolean bar) {
      this.entityData.set(DEFAULT_COLOR, bar);
   }

   public int getColor() {
      return this.isDefaultColor() ? 11387863 : (Integer)this.entityData.get(COLOR);
   }

   public void setColor(int index) {
      this.entityData.set(COLOR, index);
   }

   public void tick() {
      super.tick();
      float boardRot = this.getBoardRot();
      if (this.jumpFor > 0) {
         this.jumpFor--;
      }

      if (this.getTimeSinceHit() > 0) {
         this.setTimeSinceHit(this.getTimeSinceHit() - 1);
      }

      if (this.extinguishTimer > 0) {
         this.extinguishTimer--;
      }

      if ((Boolean)this.entityData.get(REMOVE_SOON)) {
         this.removeIn--;
         this.setBoardRot((float)Math.sin(this.removeIn * 0.3F * 3.141592653589793) * 50.0F);
         if (this.removeIn <= 0 && !this.level().isClientSide()) {
            this.removeIn = 0;
            boolean drop;
            if (this.getEnchant(AMEnchantmentRegistry.STRADDLE_BOARDRETURN.get()) <= 0) {
               drop = true;
            } else {
               drop = this.returnToPlayer != null && !this.returnToPlayer.addItem(this.getItemBoard());
            }

            if (drop) {
               AMCompat.spawnAtLocation(this, this.getItemStack().copy());
            }

            this.discard();
         }
      }

      Entity controller = this.getControllingPlayer();
      if (this.level().isClientSide()) {
         if (this.lSteps > 0) {
            double d5 = this.getX() + (this.lx - this.getX()) / this.lSteps;
            double d6 = this.getY() + (this.ly - this.getY()) / this.lSteps;
            double d7 = this.getZ() + (this.lz - this.getZ()) / this.lSteps;
            this.setYRot(Mth.wrapDegrees((float)this.lyr));
            this.setXRot(this.getXRot() + (float)(this.lxr - this.getXRot()) / this.lSteps);
            this.lSteps--;
            this.setPos(d5, d6, d7);
            this.setRot(this.getYRot(), this.getXRot());
         } else {
            this.reapplyPosition();
            this.setRot(this.getYRot(), this.getXRot());
         }
      } else {
         this.checkInsideBlocks();
         float slowdown = !this.isInWaterOrBubble() && !this.onGround() ? 0.98F : 0.05F;
         this.tickMovement();
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().multiply(slowdown, slowdown, slowdown));
         float f2 = (float)(-((float)this.getDeltaMovement().y * 0.5F * 57.2957763671875));
         this.setXRot(Mth.approachDegrees(this.getXRot(), f2, 5.0F));
         if (controller instanceof Player player) {
            this.returnToPlayer = player;
            this.rideForTicks++;
            if (this.tickCount % 50 == 0 && this.getEnchant(AMEnchantmentRegistry.STRADDLE_LAVAWAX.get()) > 0) {
               player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0, true, false));
            }

            if (player.getRemainingFireTicks() > 0 && this.extinguishTimer == 0) {
               player.clearFire();
            }

            this.setYRot(Mth.approachDegrees(this.getYRot(), player.getYRot(), 6.0F));
            Vec3 deltaMovement = this.getDeltaMovement();
            if (deltaMovement.y > -0.5) {
               this.fallDistance = 1.0F;
            }

            float riderForward = AMCompat.riderForward(player);
            float slow = riderForward < 0.0F ? 0.0F : riderForward * 0.115F;
            float threshold = 3.0F;
            boolean flag = false;
            float boardRot1 = boardRot;
            if (this.yRotO - this.getYRot() > threshold) {
               boardRot1 = boardRot + 10.0F;
               flag = true;
            }

            if (this.yRotO - this.getYRot() < -threshold) {
               boardRot1 -= 10.0F;
               flag = true;
            }

            if (!flag) {
               if (boardRot1 > 0.0F) {
                  boardRot1 = Math.max(boardRot1 - 5.0F, 0.0F);
               }

               if (boardRot1 < 0.0F) {
                  boardRot1 = Math.min(boardRot1 + 5.0F, 0.0F);
               }
            }

            this.setBoardRot(Mth.approachDegrees(boardRot, Mth.clamp(boardRot1, -25.0F, 25.0F), 5.0F));
            this.boardForwards = slow;
            if (player.isShiftKeyDown() || !this.isAlive() || (Boolean)this.entityData.get(REMOVE_SOON)) {
               this.ejectPassengers();
            }

            if (player.isInWall()) {
               this.ejectPassengers();
               this.hurt(this.damageSources().generic(), 100.0F);
            }
         } else {
            this.rideForTicks = 0;
         }
      }

      this.prevBoardRot = boardRot;
   }

   private void tickMovement() {
      this.hasImpulse = true;
      float moveForwards = Math.min(this.boardForwards, 1.0F);
      float yRot = this.getYRot();
      Vec3 prev = this.getDeltaMovement();
      float gravity = this.isOnLava() ? 0.0F : (this.isInLava() ? 0.1F : -1.0F);
      float f1 = -Mth.sin(yRot * 0.017453292F);
      float f2 = Mth.cos(yRot * 0.017453292F);
      Vec3 moveVec = new Vec3(f1, 0.0, f2).scale(moveForwards);
      Vec3 vec31 = prev.scale(0.9750000238418579).add(moveVec);
      float jumpGravity = gravity;
      if (this.jumpFor > 0) {
         float jumpRunsOutIn = this.jumpFor < 5 ? this.jumpFor / 5.0F : 1.0F;
         jumpGravity = gravity + (jumpRunsOutIn + jumpRunsOutIn * 1.0F);
      }

      this.setDeltaMovement(vec31.x, jumpGravity, vec31.z);
   }

   private boolean isOnLava() {
      BlockPos ourPos = BlockPos.containing(this.getX(), this.getY() + 0.4000000059604645, this.getZ());
      BlockPos underPos = this.getOnPos();
      return this.level().getFluidState(underPos).is(FluidTags.LAVA) && !this.level().getFluidState(ourPos).is(FluidTags.LAVA);
   }

   public void lerpTo(double x, double y, double z, float yr, float xr, int steps) {
      this.lx = x;
      this.ly = y;
      this.lz = z;
      this.lyr = yr;
      this.lxr = xr;
      this.lSteps = steps;
      this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
   }

   public void lerpMotion(double lerpX, double lerpY, double lerpZ) {
      this.lxd = lerpX;
      this.lyd = lerpY;
      this.lzd = lerpZ;
      this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
   }

   public double getEyeY() {
      return this.getY() + 0.30000001192092896;
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      return this.getControllingPlayer();
   }

   public boolean isControlledByLocalInstance() {
      return false;
   }

   @Nullable
   public Player getControllingPlayer() {
      for (Entity passenger : this.getPassengers()) {
         if (passenger instanceof Player) {
            return (Player)passenger;
         }
      }

      return null;
   }

   protected void addPassenger(Entity passenger) {
      super.addPassenger(passenger);
      if (this.isControlledByLocalInstance() && this.lSteps > 0) {
         this.lSteps = 0;
         this.absMoveTo(this.lx, this.ly, this.lz, (float)this.lyr, (float)this.lxr);
      }
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
      super.onSyncedDataUpdated(entityDataAccessor);
      if (REMOVE_SOON.equals(entityDataAccessor)) {
         this.removeIn = 5;
      }
   }

   public InteractionResult interact(Player player, InteractionHand hand) {
      if (player.isSecondaryUseActive()) {
         return InteractionResult.PASS;
      } else if (!this.level().isClientSide()) {
         return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   public int getTimeSinceHit() {
      return (Integer)this.entityData.get(TIME_SINCE_HIT);
   }

   public void setTimeSinceHit(int timeSinceHit) {
      this.entityData.set(TIME_SINCE_HIT, timeSinceHit);
   }

   @OnlyIn(Dist.CLIENT)
   public float getRockingAngle(float partialTicks) {
      return Mth.lerp(partialTicks, this.prevRockingAngle, this.rockingAngle);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity amServerEntity) {
      return AMPlatform.getEntitySpawningPacket(this, amServerEntity);
   }

   protected MovementEmission getMovementEmission() {
      return MovementEmission.EVENTS;
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.setDefaultColor(AMCompat.getBoolean(compound, "IsDefColor"));
      if (AMCompat.contains(compound, "BoardStack")) {
         this.setItemStack(AMCompat.loadItem(this.level().registryAccess(), AMCompat.getCompound(compound, "BoardStack")));
      }

      this.setColor(AMCompat.getInt(compound, "Color"));
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putBoolean("IsDefColor", this.isDefaultColor());
      compound.putInt("Color", this.getColor());
      if (!this.getItemStack().isEmpty()) {
         CompoundTag stackTag = new CompoundTag();
         AMCompat.saveInto(this.level().registryAccess(), this.getItemStack(), stackTag);
         AMCompat.put(compound, "BoardStack", stackTag);
      }
   }

   public void onPlayerJump(int i) {
   }

   public boolean canJump() {
      return this.isOnLava();
   }

   public void handleStartJump(int i) {
      this.hasImpulse = true;
      if (this.canJump()) {
         float f = 0.075F + this.getEnchant(AMEnchantmentRegistry.STRADDLE_JUMP.get()) * 0.05F;
         this.jumpFor = 5 + (int)(i * f);
      }
   }

   private int getEnchant(ResourceKey<Enchantment> enchantment) {
      return AMCompat.enchantLevel(enchantment, this.getItemBoard(), this.level());
   }

   public boolean shouldSerpentFriend() {
      return this.getEnchant(AMEnchantmentRegistry.STRADDLE_SERPENTFRIEND.get()) > 0;
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity entity) {
      return new Vec3(this.getX(), this.getY() + 2.0, this.getZ());
   }

   public void handleStopJump() {
   }

   public ItemStack getItemStack() {
      return (ItemStack)this.entityData.get(ITEMSTACK);
   }

   public void setItemStack(ItemStack item) {
      this.entityData.set(ITEMSTACK, item);
   }
}
