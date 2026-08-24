package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.message.MessageHurtMultipart;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

public class EntityCentipedeBody extends Mob implements IHurtableMultipart {
   private static final EntityDataAccessor<Integer> BODYINDEX = SynchedEntityData.defineId(EntityCentipedeBody.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> BODY_XROT = SynchedEntityData.defineId(EntityCentipedeBody.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Optional<UUID>> PARENT_UUID = SynchedEntityData.defineId(
      EntityCentipedeBody.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(
      EntityCentipedeBody.class, EntityDataSerializers.OPTIONAL_UUID
   );
   public EntityDimensions multipartSize;
   protected float radius;
   protected float angleYaw;
   protected float damageMultiplier = 1.0F;
   private double prevHeight = 0.0;

   protected EntityCentipedeBody(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.multipartSize = type.getDimensions();
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.getParent() != null;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
   }

   public boolean isNoGravity() {
      return false;
   }

   public void tick() {
      super.tick();
      this.portalProcess = null;
      this.setDeltaMovement(Vec3.ZERO);
      if (this.tickCount > 1) {
         Entity parent = this.getParent();
         this.refreshDimensions();
         if (parent != null && !this.level().isClientSide()) {
            if (parent instanceof LivingEntity parentEntity && (parentEntity.hurtTime > 0 || parentEntity.deathTime > 0)) {
               AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.getId(), parent.getId(), 0.0F));
               this.hurtTime = parentEntity.hurtTime;
               this.deathTime = parentEntity.deathTime;
            }

            if (parent.isRemoved()) {
               this.remove(RemovalReason.DISCARDED);
            }
         } else if (!this.level().isClientSide() && this.tickCount > 20) {
            this.remove(RemovalReason.DISCARDED);
         }
      }
   }

   public EntityCentipedeBody(EntityType t, LivingEntity parent, float radius, float angleYaw, float offsetY) {
      super(t, parent.level());
      this.setParent(parent);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (this.getParentId() != null) {
         AMCompat.putUUID(compound, "ParentUUID", this.getParentId());
      }

      if (this.getChildId() != null) {
         AMCompat.putUUID(compound, "ChildUUID", this.getChildId());
      }

      compound.putInt("BodyIndex", this.getBodyIndex());
      compound.putFloat("PartAngle", this.angleYaw);
      compound.putFloat("PartRadius", this.radius);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (AMCompat.hasUUID(compound, "ParentUUID")) {
         this.setParentId(AMCompat.getUUID(compound, "ParentUUID"));
      }

      if (AMCompat.hasUUID(compound, "ChildUUID")) {
         this.setChildId(AMCompat.getUUID(compound, "ChildUUID"));
      }

      this.setBodyIndex(AMCompat.getInt(compound, "BodyIndex"));
      this.angleYaw = AMCompat.getFloat(compound, "PartAngle");
      this.radius = AMCompat.getFloat(compound, "PartRadius");
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(PARENT_UUID, Optional.empty());
      builder.define(CHILD_UUID, Optional.empty());
      builder.define(BODYINDEX, 0);
      builder.define(BODY_XROT, 0.0F);
   }

   public Entity getParent() {
      UUID id = this.getParentId();
      return id != null && !this.level().isClientSide() ? ((ServerLevel)this.level()).getEntity(id) : null;
   }

   public void setParent(Entity entity) {
      this.setParentId(entity.getUUID());
   }

   public Entity getChild() {
      UUID id = this.getChildId();
      return id != null && !this.level().isClientSide() ? ((ServerLevel)this.level()).getEntity(id) : null;
   }

   @Nullable
   public UUID getChildId() {
      return (UUID)((Optional)this.entityData.get(CHILD_UUID)).orElse(null);
   }

   public void setChildId(@Nullable UUID uniqueId) {
      this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
   }

   public boolean is(Entity entity) {
      return this == entity || this.getParent() == entity;
   }

   public boolean hurt(DamageSource source, float damage) {
      Entity parent = this.getParent();
      boolean prev = parent != null && AMCompat.hurt(parent, source, damage * this.damageMultiplier);
      if (prev && !this.level().isClientSide()) {
         AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.getId(), parent.getId(), damage * this.damageMultiplier));
      }

      return prev;
   }

   public boolean isPickable() {
      return true;
   }

   public void pushEntities() {
      List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.2, 0.0, 0.2));
      Entity parent = this.getParent();
      if (parent != null) {
         entities.stream()
            .filter(entity -> entity != parent && !(entity instanceof EntityCentipedeBody) && entity.isPushable())
            .forEach(entity -> entity.push(parent));
      }
   }

   public boolean startRiding(Entity entityIn) {
      return !(entityIn instanceof AbstractMinecart) && !(entityIn instanceof Boat) ? super.startRiding(entityIn) : false;
   }

   public int getBodyIndex() {
      return (Integer)this.entityData.get(BODYINDEX);
   }

   public void setBodyIndex(int index) {
      this.entityData.set(BODYINDEX, index);
   }

   @Nullable
   public UUID getParentId() {
      return (UUID)((Optional)this.entityData.get(PARENT_UUID)).orElse(null);
   }

   public void setParentId(@Nullable UUID uniqueId) {
      this.entityData.set(PARENT_UUID, Optional.ofNullable(uniqueId));
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 10.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.ARMOR, 6.0)
         .add(Attributes.ATTACK_DAMAGE, 8.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
         .add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   public Vec3 tickMultipartPosition(int headId, float parentOffset, Vec3 parentPosition, float parentXRot, float ourYRot, boolean doHeight) {
      float yDif = doHeight ? 1.0F - 0.95F * (float)Math.min(Math.abs(parentPosition.y - this.getY()), 1.0) : 1.0F;
      Vec3 parentFront = parentPosition.add(this.calcOffsetVec(yDif * parentOffset * this.getScale(), parentXRot, ourYRot));
      Vec3 parentButt = parentPosition.add(this.calcOffsetVec(yDif * -parentOffset * this.getScale(), parentXRot, ourYRot));
      Vec3 ourButt = parentButt.add(this.calcOffsetVec((yDif * -this.getBackOffset() - 0.5F * this.getBbWidth()) * this.getScale(), this.getXRot(), ourYRot));
      Vec3 avg = new Vec3((parentButt.x + ourButt.x) / 2.0, (parentButt.y + ourButt.y) / 2.0, (parentButt.z + ourButt.z) / 2.0);
      double d0 = parentButt.x - ourButt.x;
      double d2 = parentButt.z - ourButt.z;
      double d3 = Math.sqrt(d0 * d0 + d2 * d2);
      double hgt = doHeight ? this.getLowPartHeight(parentButt.x, parentButt.y, parentButt.z) + this.getHighPartHeight(ourButt.x, ourButt.y, ourButt.z) : 0.0;
      if (Math.abs(this.prevHeight - hgt) > 0.2) {
         this.prevHeight = hgt;
      }

      if (!this.isOpaqueBlockAt(parentFront.x, parentFront.y + 0.4000000059604645, parentFront.z) && Math.abs(this.prevHeight) > 1.0) {
         this.prevHeight = 0.0;
      }

      double partYDest = Mth.clamp(this.prevHeight, -0.4000000059604645, 0.4000000059604645);
      float f = (float)(Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
      float rawAngle = Mth.wrapDegrees((float)(-(Mth.atan2(partYDest, d3) * 57.2957763671875)));
      float f2 = this.limitAngle(this.getXRot(), rawAngle, 10.0F);
      this.setXRot(f2);
      this.entityData.set(BODY_XROT, f2);
      this.setYRot(f);
      this.yHeadRot = f;
      this.moveTo(avg.x, avg.y, avg.z, f, f2);
      return avg;
   }

   public float getXRot() {
      return (Float)this.entityData.get(BODY_XROT);
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

   public boolean isFluidAt(double x, double y, double z) {
      return this.noPhysics ? false : !this.level().getFluidState(AMBlockPos.fromCoords(x, y, z)).isEmpty();
   }

   public boolean isOpaqueBlockAt(double x, double y, double z) {
      if (this.noPhysics) {
         return false;
      } else {
         float f = 1.0F;
         Vec3 vec3 = new Vec3(x, y, z);
         AABB axisalignedbb = AABB.ofSize(vec3, 1.0, 1.0E-6, 1.0);
         return this.level()
            .getBlockStates(axisalignedbb)
            .filter(Predicate.not(BlockStateBase::isAir))
            .anyMatch(
               p_185969_ -> {
                  BlockPos blockpos = AMBlockPos.fromVec3(vec3);
                  return p_185969_.isSuffocating(this.level(), blockpos)
                     && Shapes.joinIsNotEmpty(
                        p_185969_.getCollisionShape(this.level(), blockpos).move(vec3.x, vec3.y, vec3.z), Shapes.create(axisalignedbb), BooleanOp.AND
                     );
               }
            );
      }
   }

   public float getBackOffset() {
      return 0.5F;
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
}
