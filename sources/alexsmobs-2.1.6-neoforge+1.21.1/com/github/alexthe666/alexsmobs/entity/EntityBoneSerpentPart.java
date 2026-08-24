package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.message.MessageHurtMultipart;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityBoneSerpentPart extends LivingEntity implements IHurtableMultipart {
   private static final EntityDataAccessor<Boolean> TAIL = SynchedEntityData.defineId(EntityBoneSerpentPart.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> BODYINDEX = SynchedEntityData.defineId(EntityBoneSerpentPart.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Optional<UUID>> PARENT_UUID = SynchedEntityData.defineId(
      EntityBoneSerpentPart.class, EntityDataSerializers.OPTIONAL_UUID
   );
   public EntityDimensions multipartSize;
   protected float radius;
   protected float angleYaw;
   protected float offsetY;
   protected float damageMultiplier = 1.0F;

   public EntityBoneSerpentPart(EntityType t, Level world) {
      super(t, world);
      this.multipartSize = t.getDimensions();
   }

   public EntityBoneSerpentPart(EntityType t, LivingEntity parent, float radius, float angleYaw, float offsetY) {
      super(t, parent.level());
      this.setParent(parent);
      this.radius = radius;
      this.angleYaw = (angleYaw + 90.0F) * 0.017453292F;
      this.offsetY = offsetY;
   }

   public boolean startRiding(Entity entityIn) {
      return !(entityIn instanceof AbstractMinecart) && !(entityIn instanceof Boat) ? super.startRiding(entityIn) : false;
   }

   @Nullable
   public ItemStack getPickResult() {
      Entity parent = this.getParent();
      return parent != null ? parent.getPickResult() : ItemStack.EMPTY;
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.15000000596046448);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (this.getParentId() != null) {
         AMCompat.putUUID(compound, "ParentUUID", this.getParentId());
      }

      compound.putBoolean("TailPart", this.isTail());
      compound.putInt("BodyIndex", this.getBodyIndex());
      compound.putFloat("PartAngle", this.angleYaw);
      compound.putFloat("PartRadius", this.radius);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (AMCompat.hasUUID(compound, "ParentUUID")) {
         this.setParentId(AMCompat.getUUID(compound, "ParentUUID"));
      }

      this.setTail(AMCompat.getBoolean(compound, "TailPart"));
      this.setBodyIndex(AMCompat.getInt(compound, "BodyIndex"));
      this.angleYaw = AMCompat.getFloat(compound, "PartAngle");
      this.radius = AMCompat.getFloat(compound, "PartRadius");
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(PARENT_UUID, Optional.empty());
      builder.define(TAIL, false);
      builder.define(BODYINDEX, 0);
   }

   @Nullable
   public UUID getParentId() {
      return (UUID)((Optional)this.entityData.get(PARENT_UUID)).orElse(null);
   }

   public void setParentId(@Nullable UUID uniqueId) {
      this.entityData.set(PARENT_UUID, Optional.ofNullable(uniqueId));
   }

   public void setInitialPartPos(Entity parent) {
      this.setPos(
         parent.xo + this.radius * Math.cos(parent.getYRot() * 0.017453292F + this.angleYaw),
         parent.yo + this.offsetY,
         parent.zo + this.radius * Math.sin(parent.getYRot() * 0.017453292F + this.angleYaw)
      );
   }

   public void tick() {
      this.portalProcess = null;
      if (this.tickCount > 10) {
         Entity parent = this.getParent();
         this.refreshDimensions();
         if (parent != null && !this.level().isClientSide()) {
            this.setNoGravity(true);
            this.setPos(
               parent.xo + this.radius * Math.cos(parent.yRotO * 0.017453292F + this.angleYaw),
               parent.yo + this.offsetY,
               parent.zo + this.radius * Math.sin(parent.yRotO * 0.017453292F + this.angleYaw)
            );
            double d0 = parent.getX() - this.getX();
            double d1 = parent.getY() - this.getY();
            double d2 = parent.getZ() - this.getZ();
            float f2 = -((float)(Mth.atan2(d1, Mth.sqrt((float)(d0 * d0 + d2 * d2))) * 57.2957763671875));
            this.setXRot(this.limitAngle(this.getXRot(), f2, 5.0F));
            this.markHurt();
            this.setYRot(parent.yRotO);
            this.yHeadRot = this.getYRot();
            this.yBodyRot = this.yRotO;
            if (parent instanceof LivingEntity && !this.level().isClientSide() && (((LivingEntity)parent).hurtTime > 0 || ((LivingEntity)parent).deathTime > 0)
               )
             {
               AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.getId(), parent.getId(), 0.0F));
               this.hurtTime = ((LivingEntity)parent).hurtTime;
               this.deathTime = ((LivingEntity)parent).deathTime;
            }

            this.pushEntities();
            if (parent.isRemoved() && !this.level().isClientSide()) {
               this.remove(RemovalReason.DISCARDED);
            }
         } else if (this.tickCount > 20 && !this.level().isClientSide()) {
            this.remove(RemovalReason.DISCARDED);
         }
      }

      super.tick();
   }

   public Entity getParent() {
      UUID id = this.getParentId();
      return id != null && !this.level().isClientSide() ? ((ServerLevel)this.level()).getEntity(id) : null;
   }

   public void setParent(Entity entity) {
      this.setParentId(entity.getUUID());
   }

   public boolean is(Entity entity) {
      return this == entity || this.getParent() == entity;
   }

   public boolean isPickable() {
      return true;
   }

   public HumanoidArm getMainArm() {
      return null;
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity amServerEntity) {
      return AMPlatform.getEntitySpawningPacket(this, amServerEntity);
   }

   public void pushEntities() {
      List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.2, 0.0, 0.2));
      Entity parent = this.getParent();
      if (parent != null) {
         entities.stream()
            .filter(entity -> entity != parent && !(entity instanceof EntityBoneSerpentPart) && entity.isPushable())
            .forEach(entity -> entity.push(parent));
      }
   }

   public InteractionResult interact(Player player, InteractionHand hand) {
      Entity parent = this.getParent();
      return parent != null ? parent.interact(player, hand) : InteractionResult.PASS;
   }

   public boolean hurt(DamageSource source, float damage) {
      Entity parent = this.getParent();
      boolean prev = parent != null && AMCompat.hurt(parent, source, damage * this.damageMultiplier);
      if (prev && !this.level().isClientSide()) {
         AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.getId(), parent.getId(), damage * this.damageMultiplier));
      }

      return prev;
   }

   public Iterable<ItemStack> getArmorSlots() {
      return ImmutableList.of();
   }

   public ItemStack getItemBySlot(EquipmentSlot slotIn) {
      return ItemStack.EMPTY;
   }

   public void setItemSlot(EquipmentSlot slotIn, ItemStack stack) {
   }

   public boolean isTail() {
      return (Boolean)this.entityData.get(TAIL);
   }

   public void setTail(boolean tail) {
      this.entityData.set(TAIL, tail);
   }

   public int getBodyIndex() {
      return (Integer)this.entityData.get(BODYINDEX);
   }

   public void setBodyIndex(int index) {
      this.entityData.set(BODYINDEX, index);
   }

   public boolean shouldNotExist() {
      Entity parent = this.getParent();
      return !parent.isAlive();
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

   public boolean shouldContinuePersisting() {
      return this.isAddedToLevel() || this.isRemoved();
   }
}
