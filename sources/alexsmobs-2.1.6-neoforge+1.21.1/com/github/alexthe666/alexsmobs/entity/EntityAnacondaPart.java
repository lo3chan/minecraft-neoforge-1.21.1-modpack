package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.util.AnacondaPartIndex;
import com.github.alexthe666.alexsmobs.message.MessageHurtMultipart;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EntityAnacondaPart extends LivingEntity implements IHurtableMultipart {
   private static final EntityDataAccessor<Integer> BODYINDEX = SynchedEntityData.defineId(EntityAnacondaPart.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> BODY_TYPE = SynchedEntityData.defineId(EntityAnacondaPart.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> TARGET_YAW = SynchedEntityData.defineId(EntityAnacondaPart.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(
      EntityAnacondaPart.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Optional<UUID>> PARENT_UUID = SynchedEntityData.defineId(
      EntityAnacondaPart.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Float> SWELL = SynchedEntityData.defineId(EntityAnacondaPart.class, EntityDataSerializers.FLOAT);
   public EntityDimensions multipartSize;
   private float strangleProgess;
   private float prevSwell;
   private float prevStrangleProgess;
   private int headEntityId = -1;
   private double prevHeight = 0.0;
   private static final EntityDataAccessor<Boolean> YELLOW = SynchedEntityData.defineId(EntityAnacondaPart.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SHEDDING = SynchedEntityData.defineId(EntityAnacondaPart.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> BABY = SynchedEntityData.defineId(EntityAnacondaPart.class, EntityDataSerializers.BOOLEAN);

   public EntityAnacondaPart(EntityType t, Level world) {
      super(t, world);
      this.multipartSize = t.getDimensions();
   }

   public EntityAnacondaPart(EntityType t, LivingEntity parent) {
      super(t, parent.level());
      this.setParent(parent);
   }

   public InteractionResult interact(Player p_19978_, InteractionHand p_19979_) {
      return this.getParent() == null ? super.interact(p_19978_, p_19979_) : this.getParent().interact(p_19978_, p_19979_);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.15000000596046448);
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
   }

   public boolean isNoGravity() {
      return false;
   }

   public void tick() {
      super.tick();
      this.prevStrangleProgess = this.strangleProgess;
      this.prevSwell = this.getSwell();
      this.portalProcess = null;
      this.setDeltaMovement(Vec3.ZERO);
      if (this.tickCount > 1) {
         Entity parent = this.getParent();
         this.refreshDimensions();
         if (!this.level().isClientSide()) {
            if (parent == null) {
               this.remove(RemovalReason.DISCARDED);
            }

            if (parent == null) {
               if (this.tickCount > 20) {
                  this.remove(RemovalReason.DISCARDED);
               }
            } else {
               if (parent instanceof LivingEntity livingEntityParent && (livingEntityParent.hurtTime > 0 || livingEntityParent.deathTime > 0)) {
                  AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.getId(), parent.getId(), 0.0F));
                  this.hurtTime = livingEntityParent.hurtTime;
                  this.deathTime = livingEntityParent.deathTime;
               }

               if (parent.isRemoved()) {
                  this.remove(RemovalReason.DISCARDED);
               }
            }

            if (this.getSwell() > 0.0F) {
               float swellInc = 0.25F;
               if (parent instanceof EntityAnaconda || parent instanceof EntityAnacondaPart && ((EntityAnacondaPart)parent).getSwell() == 0.0F) {
                  if (this.getChild() != null) {
                     EntityAnacondaPart child = (EntityAnacondaPart)this.getChild();
                     if (child.getPartType() == AnacondaPartIndex.TAIL) {
                        if (this.getSwell() == 0.25F) {
                           this.feedAnaconda();
                        }
                     } else {
                        child.setSwell(child.getSwell() + 0.25F);
                     }
                  }

                  this.setSwell(this.getSwell() - 0.25F);
               }
            }
         }
      }
   }

   private void feedAnaconda() {
      Entity e = this.getParent();

      while (e instanceof EntityAnacondaPart) {
         e = ((EntityAnacondaPart)e).getParent();
      }

      if (e instanceof EntityAnaconda) {
         ((EntityAnaconda)e).feed();
      }
   }

   public Vec3 tickMultipartPosition(
      int headId, AnacondaPartIndex parentIndex, Vec3 parentPosition, float parentXRot, float parentYRot, float ourYRot, boolean doHeight
   ) {
      Vec3 parentButt = parentPosition.add(this.calcOffsetVec(-parentIndex.getBackOffset() * this.getScale(), parentXRot, parentYRot));
      Vec3 ourButt = parentButt.add(
         this.calcOffsetVec((-this.getPartType().getBackOffset() - 0.5F * this.getBbWidth()) * this.getScale(), this.getXRot(), ourYRot)
      );
      Vec3 avg = new Vec3((parentButt.x + ourButt.x) / 2.0, (parentButt.y + ourButt.y) / 2.0, (parentButt.z + ourButt.z) / 2.0);
      double d0 = parentButt.x - ourButt.x;
      double d2 = parentButt.z - ourButt.z;
      double d3 = Math.sqrt(d0 * d0 + d2 * d2);
      double hgt = doHeight ? this.getLowPartHeight(parentButt.x, parentButt.y, parentButt.z) + this.getHighPartHeight(ourButt.x, ourButt.y, ourButt.z) : 0.0;
      if (Math.abs(hgt - this.prevHeight) > 0.20000000298023224) {
         this.prevHeight = hgt;
      }

      double partYDest = Mth.clamp(this.getScale() * this.prevHeight, -0.6000000238418579, 0.6000000238418579);
      float f = (float)(Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
      float rawAngle = Mth.wrapDegrees((float)(-(Mth.atan2(partYDest, d3) * 57.2957763671875)));
      float f2 = this.limitAngle(this.getXRot(), rawAngle, 10.0F);
      this.setXRot(f2);
      this.setYRot(f);
      this.yHeadRot = f;
      Vec3 grounded = new Vec3(avg.x, this.liftOutOfGround(avg.x, avg.y, avg.z), avg.z);
      this.moveTo(grounded.x, grounded.y, grounded.z, f, f2);
      this.headEntityId = headId;
      return grounded;
   }

   private double liftOutOfGround(double x, double y, double z) {
      if (!this.noPhysics && !this.isFluidAt(x, y, z)) {
         double probe = y + 1.0E-4;
         BlockPos pos = AMBlockPos.fromCoords(x, probe, z);
         BlockState state = this.level().getBlockState(pos);
         if (!state.isAir() && state.isSuffocating(this.level(), pos)) {
            VoxelShape shape = state.getCollisionShape(this.level(), pos);
            if (shape.isEmpty()) {
               return y;
            } else {
               AABB point = AABB.ofSize(new Vec3(x, probe, z), 1.0E-6, 1.0E-6, 1.0E-6);
               return !Shapes.joinIsNotEmpty(shape.move(pos.getX(), pos.getY(), pos.getZ()), Shapes.create(point), BooleanOp.AND)
                  ? y
                  : Math.max(y, pos.getY() + shape.max(Axis.Y));
            }
         } else {
            return y;
         }
      } else {
         return y;
      }
   }

   public double getLowPartHeight(double x, double yIn, double z) {
      if (this.isFluidAt(x, yIn, z)) {
         return 0.0;
      } else {
         double checkAt = 0.0;

         while (checkAt > -3.0 && !this.isOpaqueBlockAt(x, yIn + checkAt, z)) {
            checkAt -= 0.2;
         }

         return checkAt;
      }
   }

   public double getHighPartHeight(double x, double yIn, double z) {
      if (this.isFluidAt(x, yIn, z)) {
         return 0.0;
      } else {
         double checkAt = 0.0;

         while (checkAt <= 3.0 && this.isOpaqueBlockAt(x, yIn + checkAt, z)) {
            checkAt += 0.2;
         }

         return checkAt;
      }
   }

   public boolean isOpaqueBlockAt(double x, double y, double z) {
      if (this.noPhysics) {
         return false;
      } else {
         double d = 1.0;
         Vec3 vec3 = new Vec3(x, y, z);
         AABB axisAlignedBB = AABB.ofSize(vec3, 1.0, 1.0E-6, 1.0);
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

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean isFluidAt(double x, double y, double z) {
      return this.noPhysics ? false : !this.level().getFluidState(AMBlockPos.fromCoords(x, y, z)).isEmpty();
   }

   public boolean hurtHeadId(DamageSource source, float f) {
      if (this.headEntityId != -1) {
         Entity e = this.level().getEntity(this.headEntityId);
         if (e instanceof EntityAnaconda) {
            return AMCompat.hurt(e, source, f);
         }
      }

      return false;
   }

   public boolean hurt(DamageSource source, float damage) {
      return this.hurtHeadId(source, damage);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CHILD_UUID, Optional.empty());
      builder.define(PARENT_UUID, Optional.empty());
      builder.define(BODYINDEX, 0);
      builder.define(BODY_TYPE, AnacondaPartIndex.NECK.ordinal());
      builder.define(TARGET_YAW, 0.0F);
      builder.define(SWELL, 0.0F);
      builder.define(YELLOW, false);
      builder.define(SHEDDING, false);
      builder.define(BABY, false);
   }

   public void pushEntities() {
      List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.2, 0.0, 0.2));
      Entity parent = this.getParent();
      if (parent != null) {
         entities.stream()
            .filter(entity -> !entity.is(parent) && !(entity instanceof EntityAnacondaPart) && !(entity instanceof EntityAnaconda) && entity.isPushable())
            .forEach(entity -> entity.push(parent));
      }
   }

   public Iterable<ItemStack> getArmorSlots() {
      return ImmutableList.of();
   }

   public ItemStack getItemBySlot(EquipmentSlot slotIn) {
      return ItemStack.EMPTY;
   }

   public void setItemSlot(EquipmentSlot p_21036_, ItemStack p_21037_) {
   }

   public HumanoidArm getMainArm() {
      return HumanoidArm.RIGHT;
   }

   @Override
   public void onAttackedFromServer(LivingEntity parent, float damage, DamageSource damageSource) {
      if (parent.deathTime > 0) {
         this.deathTime = parent.deathTime;
      }

      if (parent.hurtTime > 0) {
         this.hurtTime = parent.hurtTime;
      }
   }

   public Entity getParent() {
      if (!this.level().isClientSide()) {
         UUID id = this.getParentId();
         if (id != null) {
            return ((ServerLevel)this.level()).getEntity(id);
         }
      }

      return null;
   }

   public void setParent(Entity entity) {
      this.setParentId(entity.getUUID());
   }

   @Nullable
   public UUID getParentId() {
      return (UUID)((Optional)this.entityData.get(PARENT_UUID)).orElse(null);
   }

   public void setParentId(@Nullable UUID uniqueId) {
      this.entityData.set(PARENT_UUID, Optional.ofNullable(uniqueId));
   }

   public Entity getChild() {
      if (!this.level().isClientSide()) {
         UUID id = this.getChildId();
         if (id != null) {
            return ((ServerLevel)this.level()).getEntity(id);
         }
      }

      return null;
   }

   @Nullable
   public UUID getChildId() {
      return (UUID)((Optional)this.entityData.get(CHILD_UUID)).orElse(null);
   }

   public void setChildId(@Nullable UUID uniqueId) {
      this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (this.getParentId() != null) {
         AMCompat.putUUID(compound, "ParentUUID", this.getParentId());
      }

      if (this.getChildId() != null) {
         AMCompat.putUUID(compound, "ChildUUID", this.getChildId());
      }

      compound.putInt("BodyModel", this.getPartType().ordinal());
      compound.putInt("BodyIndex", this.getBodyIndex());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (AMCompat.hasUUID(compound, "ParentUUID")) {
         this.setParentId(AMCompat.getUUID(compound, "ParentUUID"));
      }

      if (AMCompat.hasUUID(compound, "ChildUUID")) {
         this.setChildId(AMCompat.getUUID(compound, "ChildUUID"));
      }

      this.setPartType(AnacondaPartIndex.fromOrdinal(AMCompat.getInt(compound, "BodyModel")));
      this.setBodyIndex(AMCompat.getInt(compound, "BodyIndex"));
   }

   public boolean is(Entity entity) {
      return this == entity || this.getParent() == entity;
   }

   public boolean isPickable() {
      return true;
   }

   @Nullable
   public ItemStack getPickResult() {
      Entity parent = this.getParent();
      return parent != null ? parent.getPickResult() : ItemStack.EMPTY;
   }

   public int getBodyIndex() {
      return (Integer)this.entityData.get(BODYINDEX);
   }

   public void setBodyIndex(int index) {
      this.entityData.set(BODYINDEX, index);
   }

   public AnacondaPartIndex getPartType() {
      return AnacondaPartIndex.fromOrdinal((Integer)this.entityData.get(BODY_TYPE));
   }

   public void setPartType(AnacondaPartIndex index) {
      this.entityData.set(BODY_TYPE, index.ordinal());
   }

   public void setTargetYaw(float f) {
      this.entityData.set(TARGET_YAW, f);
   }

   public void setSwell(float f) {
      this.entityData.set(SWELL, f);
   }

   public float getSwell() {
      return Math.min((Float)this.entityData.get(SWELL), 5.0F);
   }

   public float getSwellLerp(float partialTick) {
      return this.prevSwell + (Math.max(this.getSwell(), 0.0F) - this.prevSwell) * partialTick;
   }

   public float getYRot() {
      return super.getYRot();
   }

   public void setStrangleProgress(float f) {
      this.strangleProgess = f;
   }

   public float getStrangleProgress(float partialTick) {
      return this.prevStrangleProgess + (this.strangleProgess - this.prevStrangleProgess) * partialTick;
   }

   public void copyDataFrom(EntityAnaconda anaconda) {
      this.entityData.set(YELLOW, anaconda.isYellow());
      this.entityData.set(SHEDDING, anaconda.isShedding());
      this.entityData.set(BABY, anaconda.isBaby());
   }

   public boolean isYellow() {
      return (Boolean)this.entityData.get(YELLOW);
   }

   public boolean isShedding() {
      return (Boolean)this.entityData.get(SHEDDING);
   }

   public boolean isBaby() {
      return (Boolean)this.entityData.get(BABY);
   }
}
