package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EntitySquidGrapple extends Entity {
   private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(
      EntitySquidGrapple.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Direction> ATTACHED_FACE = SynchedEntityData.defineId(EntitySquidGrapple.class, EntityDataSerializers.DIRECTION);
   private static final EntityDataAccessor<Boolean> WITHDRAWING = SynchedEntityData.defineId(EntitySquidGrapple.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Optional<BlockPos>> ATTACHED_POS = SynchedEntityData.defineId(
      EntitySquidGrapple.class, EntityDataSerializers.OPTIONAL_BLOCK_POS
   );
   private int ticksWithdrawing = 0;

   public EntitySquidGrapple(EntityType type, Level level) {
      super(type, level);
   }

   public EntitySquidGrapple(Level worldIn, LivingEntity player, boolean rightHand) {
      this(AMEntityRegistry.SQUID_GRAPPLE.get(), worldIn);
      this.setOwnerId(player.getUUID());
      float rot = player.yHeadRot + (rightHand ? 60 : -60);
      this.setPos(
         player.getX() - player.getBbWidth() * 0.5 * Mth.sin(rot * 0.017453292F),
         player.getEyeY() - 0.20000000298023224,
         player.getZ() + player.getBbWidth() * 0.5 * Mth.cos(rot * 0.017453292F)
      );
   }

   protected static float lerpRotation(float f2, float f3) {
      while (f3 - f2 < -180.0F) {
         f2 -= 360.0F;
      }

      while (f3 - f2 >= 180.0F) {
         f2 += 360.0F;
      }

      return Mth.lerp(0.2F, f2, f3);
   }

   public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
      Vec3 vector3d = new Vec3(x, y, z)
         .normalize()
         .add(
            this.random.nextGaussian() * 0.007499999832361937 * inaccuracy,
            this.random.nextGaussian() * 0.007499999832361937 * inaccuracy,
            this.random.nextGaussian() * 0.007499999832361937 * inaccuracy
         )
         .scale(velocity);
      this.setDeltaMovement(vector3d);
      float f = Mth.sqrt((float)(vector3d.x * vector3d.x + vector3d.z * vector3d.z));
      this.setYRot(Mth.wrapDegrees((float)(Mth.atan2(vector3d.x, vector3d.z) * 57.2957763671875) + 180.0F));
      this.setXRot((float)(Mth.atan2(vector3d.y, f) * 57.2957763671875));
      this.yRotO = this.getYRot();
      this.xRotO = this.getXRot();
   }

   public Direction getAttachmentFacing() {
      return (Direction)this.entityData.get(ATTACHED_FACE);
   }

   public void setAttachmentFacing(Direction direction) {
      this.entityData.set(ATTACHED_FACE, direction);
   }

   @Nullable
   public UUID getOwnerId() {
      return (UUID)((Optional)this.entityData.get(OWNER_UUID)).orElse(null);
   }

   public void setOwnerId(@Nullable UUID uniqueId) {
      this.entityData.set(OWNER_UUID, Optional.ofNullable(uniqueId));
   }

   public BlockPos getStuckToPos() {
      return (BlockPos)((Optional)this.entityData.get(ATTACHED_POS)).orElse(null);
   }

   public void setStuckToPos(BlockPos harvestedPos) {
      this.entityData.set(ATTACHED_POS, Optional.ofNullable(harvestedPos));
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(OWNER_UUID, Optional.empty());
      builder.define(ATTACHED_FACE, Direction.DOWN);
      builder.define(ATTACHED_POS, Optional.empty());
      builder.define(WITHDRAWING, false);
   }

   public Entity getOwner() {
      UUID id = this.getOwnerId();
      if (id != null && !this.level().isClientSide()) {
         return ((ServerLevel)this.level()).getEntity(id);
      } else {
         return this.getOwnerId() == null ? null : this.level().getPlayerByUUID(this.getOwnerId());
      }
   }

   public boolean isWithdrawing() {
      return (Boolean)this.entityData.get(WITHDRAWING);
   }

   public void setWithdrawing(boolean withdrawing) {
      this.entityData.set(WITHDRAWING, withdrawing);
   }

   public void tick() {
      this.xRotO = this.getXRot();
      this.yRotO = this.getYRot();
      Entity entity = this.getOwner();
      if (!this.level().isClientSide()) {
         if (entity == null || !entity.isAlive()) {
            this.discard();
         } else if (entity.isShiftKeyDown()) {
            this.setWithdrawing(true);
         }
      }

      if (this.isWithdrawing() && entity != null) {
         super.tick();
         this.ticksWithdrawing++;
         this.setStuckToPos(null);
         Vec3 withDrawTo = entity.getEyePosition().add(0.0, -0.20000000298023224, 0.0);
         if (withDrawTo.distanceTo(this.position()) > 1.2000000476837158 && this.ticksWithdrawing < 200) {
            Vec3 move = new Vec3(withDrawTo.x - this.getX(), withDrawTo.y - this.getY(), withDrawTo.z - this.getZ());
            Vec3 vector3d = move.normalize().scale(1.2);
            this.setDeltaMovement(vector3d.scale(0.99));
            double d0 = this.getX() + vector3d.x;
            double d1 = this.getY() + vector3d.y;
            double d2 = this.getZ() + vector3d.z;
            float f = Mth.sqrt((float)(move.x * move.x + move.z * move.z));
            if (!this.level().isClientSide()) {
               this.setYRot(Mth.wrapDegrees((float)(-Mth.atan2(move.x, move.z) * 57.2957763671875)) - 180.0F);
               this.setXRot((float)(Mth.atan2(move.y, f) * 57.2957763671875));
               this.yRotO = this.getYRot();
               this.xRotO = this.getXRot();
            }

            this.setPos(d0, d1, d2);
         } else {
            this.discard();
         }
      } else if (!this.level().isClientSide() && !this.level().hasChunkAt(this.blockPosition())) {
         this.discard();
      } else if (this.getStuckToPos() == null) {
         super.tick();
         Vec3 vector3d = this.getDeltaMovement();
         HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, newentity -> false);
         if (raytraceresult != null && raytraceresult.getType() != Type.MISS) {
            this.onImpact(raytraceresult);
         }

         this.checkInsideBlocks();
         double d0 = this.getX() + vector3d.x;
         double d1 = this.getY() + vector3d.y;
         double d2 = this.getZ() + vector3d.z;
         this.updateRotation();
         this.setDeltaMovement(vector3d.scale(0.99));
         if (this.level().getBlockStates(this.getBoundingBox()).noneMatch(BlockStateBase::isAir) && !this.isInWater()) {
            this.setDeltaMovement(Vec3.ZERO);
         } else {
            this.setPos(d0, d1, d2);
         }

         if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.10000000149011612, 0.0));
         }
      } else {
         BlockState state = this.level().getBlockState(this.getStuckToPos());
         Vec3 vec3 = new Vec3(this.getStuckToPos().getX() + 0.5F, this.getStuckToPos().getY() + 0.5F, this.getStuckToPos().getZ() + 0.5F);
         Vec3 offset = new Vec3(
            this.getAttachmentFacing().getStepX() * 0.55F, this.getAttachmentFacing().getStepY() * 0.55F, this.getAttachmentFacing().getStepZ() * 0.55F
         );
         this.setPos(vec3.add(offset));
         float targetX = this.getXRot();
         float targetY = this.getYRot();
         switch (this.getAttachmentFacing()) {
            case UP:
               targetX = 0.0F;
               break;
            case DOWN:
               targetX = 180.0F;
               break;
            case NORTH:
               targetX = -90.0F;
               targetY = 0.0F;
               break;
            case EAST:
               targetX = -90.0F;
               targetY = 90.0F;
               break;
            case SOUTH:
               targetX = -90.0F;
               targetY = 180.0F;
               break;
            case WEST:
               targetX = -90.0F;
               targetY = -90.0F;
         }

         this.setXRot(targetX);
         this.setYRot(targetY);
         if (entity != null && entity.distanceTo(this) > 2.0F) {
            float entitySwing = 1.0F;
            if (entity instanceof LivingEntity living) {
               float detract = living.xxa * living.xxa + living.yya * living.yya + living.zza * living.zza;
               entitySwing = (float)(entitySwing - Math.min(1.0, Math.sqrt(detract) * 0.3330000042915344));
            }

            Vec3 move = new Vec3(this.getX() - entity.getX(), this.getY() - entity.getEyeHeight() / 2.0 - entity.getY(), this.getZ() - entity.getZ());
            entity.setDeltaMovement(entity.getDeltaMovement().add(move.normalize().scale(0.2 * entitySwing)));
            if (!entity.onGround()) {
               entity.fallDistance = 0.0F;
            }
         }

         if (state.isAir()) {
            this.setWithdrawing(true);
         }
      }
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

   private void updateRotation() {
   }

   protected void onImpact(HitResult result) {
      Type raytraceresult$type = result.getType();
      if (!this.level().isClientSide() && raytraceresult$type == Type.BLOCK && this.getStuckToPos() == null) {
         this.setDeltaMovement(Vec3.ZERO);
         this.setStuckToPos(((BlockHitResult)result).getBlockPos());
         this.setAttachmentFacing(((BlockHitResult)result).getDirection());
      }
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      if (this.getOwnerId() != null) {
         AMCompat.putUUID(compound, "OwnerUUID", this.getOwnerId());
      }
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      if (AMCompat.hasUUID(compound, "OwnerUUID")) {
         this.setOwnerId(AMCompat.getUUID(compound, "OwnerUUID"));
      }
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity amServerEntity) {
      return AMPlatform.getEntitySpawningPacket(this, amServerEntity);
   }
}
