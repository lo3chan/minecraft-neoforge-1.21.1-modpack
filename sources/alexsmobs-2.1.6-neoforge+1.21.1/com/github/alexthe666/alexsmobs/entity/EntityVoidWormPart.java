package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.message.MessageHurtMultipart;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
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
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityVoidWormPart extends LivingEntity implements IHurtableMultipart {
   protected static final EntityDimensions SIZE_BASE = EntityDimensions.scalable(1.2F, 1.95F);
   protected static final EntityDimensions TAIL_SIZE = EntityDimensions.scalable(1.6F, 2.0F);
   private static final EntityDataAccessor<Boolean> TAIL = SynchedEntityData.defineId(EntityVoidWormPart.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> BODYINDEX = SynchedEntityData.defineId(EntityVoidWormPart.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> WORM_SCALE = SynchedEntityData.defineId(EntityVoidWormPart.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> WORM_YAW = SynchedEntityData.defineId(EntityVoidWormPart.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> WORM_ANGLE = SynchedEntityData.defineId(EntityVoidWormPart.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Optional<UUID>> PARENT_UUID = SynchedEntityData.defineId(
      EntityVoidWormPart.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(
      EntityVoidWormPart.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Integer> PORTAL_TICKS = SynchedEntityData.defineId(EntityVoidWormPart.class, EntityDataSerializers.INT);
   public EntityDimensions multipartSize;
   public float prevWormAngle;
   protected float radius;
   protected float angleYaw;
   protected float offsetY;
   protected float damageMultiplier = 1.0F;
   private float prevWormYaw = 0.0F;
   private Vec3 teleportPos = null;
   private Vec3 enterPos = null;
   private boolean doesParentControlPos = false;

   public EntityVoidWormPart(EntityType t, Level world) {
      super(t, world);
      this.multipartSize = t.getDimensions();
   }

   public EntityVoidWormPart(EntityType t, LivingEntity parent, float radius, float angleYaw, float offsetY) {
      super(t, parent.level());
      this.setParent(parent);
      this.radius = radius;
      this.angleYaw = (angleYaw + 90.0F) * 0.017453292F;
      this.offsetY = offsetY;
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30.0).add(Attributes.MOVEMENT_SPEED, 0.15000000596046448);
   }

   public void push(Entity entityIn) {
   }

   public void kill() {
      this.remove(RemovalReason.DISCARDED);
   }

   public EntityDimensions getDefaultDimensions(Pose poseIn) {
      return this.isTail() ? TAIL_SIZE.scale(this.getScale()) : super.getDefaultDimensions(poseIn);
   }

   public float getWormScale() {
      return (Float)this.entityData.get(WORM_SCALE);
   }

   public void setWormScale(float scale) {
      this.entityData.set(WORM_SCALE, scale);
   }

   public float getScale() {
      return this.getWormScale() + 0.5F;
   }

   public boolean startRiding(Entity entityIn) {
      return !(entityIn instanceof AbstractMinecart) && !(entityIn instanceof Boat) ? super.startRiding(entityIn) : false;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.FALL)
         || source.is(DamageTypes.DROWN)
         || source.is(DamageTypes.FELL_OUT_OF_WORLD)
         || source.is(DamageTypes.IN_WALL)
         || source.is(DamageTypes.LAVA)
         || source.is(DamageTypeTags.IS_FIRE)
         || super.isInvulnerableTo(source);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (this.getParentId() != null) {
         AMCompat.putUUID(compound, "ParentUUID", this.getParentId());
      }

      if (this.getChildId() != null) {
         AMCompat.putUUID(compound, "ChildUUID", this.getChildId());
      }

      compound.putBoolean("TailPart", this.isTail());
      compound.putInt("BodyIndex", this.getBodyIndex());
      compound.putInt("PortalTicks", this.getPortalTicks());
      compound.putFloat("PartAngle", this.angleYaw);
      compound.putFloat("WormScale", this.getWormScale());
      compound.putFloat("PartRadius", this.radius);
      compound.putFloat("PartYOffset", this.offsetY);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (AMCompat.hasUUID(compound, "ParentUUID")) {
         this.setParentId(AMCompat.getUUID(compound, "ParentUUID"));
      }

      if (AMCompat.hasUUID(compound, "ChildUUID")) {
         this.setChildId(AMCompat.getUUID(compound, "ChildUUID"));
      }

      this.setTail(AMCompat.getBoolean(compound, "TailPart"));
      this.setBodyIndex(AMCompat.getInt(compound, "BodyIndex"));
      this.setPortalTicks(AMCompat.getInt(compound, "PortalTicks"));
      this.angleYaw = AMCompat.getFloat(compound, "PartAngle");
      this.setWormScale(AMCompat.getFloat(compound, "WormScale"));
      this.radius = AMCompat.getFloat(compound, "PartRadius");
      this.offsetY = AMCompat.getFloat(compound, "PartYOffset");
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(PARENT_UUID, Optional.empty());
      builder.define(CHILD_UUID, Optional.empty());
      builder.define(TAIL, false);
      builder.define(BODYINDEX, 0);
      builder.define(WORM_SCALE, 1.0F);
      builder.define(WORM_YAW, 0.0F);
      builder.define(WORM_ANGLE, 0.0F);
      builder.define(PORTAL_TICKS, 0);
   }

   @Nullable
   public UUID getParentId() {
      return (UUID)((Optional)this.entityData.get(PARENT_UUID)).orElse(null);
   }

   public void setParentId(@Nullable UUID uniqueId) {
      this.entityData.set(PARENT_UUID, Optional.ofNullable(uniqueId));
   }

   @Nullable
   public UUID getChildId() {
      return (UUID)((Optional)this.entityData.get(CHILD_UUID)).orElse(null);
   }

   public void setChildId(@Nullable UUID uniqueId) {
      this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
   }

   public void setInitialPartPos(Entity parent) {
      this.setPos(
         parent.xo + this.radius * Math.cos(parent.getYRot() * 0.017453292519943295 + this.angleYaw),
         parent.yo + this.offsetY,
         parent.zo + this.radius * Math.sin(parent.getYRot() * 0.017453292519943295 + this.angleYaw)
      );
   }

   public float getWormAngle() {
      return (Float)this.entityData.get(WORM_ANGLE);
   }

   public void setWormAngle(float progress) {
      this.entityData.set(WORM_ANGLE, progress);
   }

   public int getPortalTicks() {
      return (Integer)this.entityData.get(PORTAL_TICKS);
   }

   public void setPortalTicks(int ticks) {
      this.entityData.set(PORTAL_TICKS, ticks);
   }

   public void tick() {
      this.portalProcess = null;
      this.prevWormAngle = this.getWormAngle();
      this.prevWormYaw = (Float)this.entityData.get(WORM_YAW);
      this.setDeltaMovement(Vec3.ZERO);
      this.radius = 1.0F + this.getWormScale() * (this.isTail() ? 0.65F : 0.3F) + (this.getBodyIndex() == 0 ? 0.8F : 0.0F);
      if (this.tickCount > 3) {
         Entity parent = this.getParent();
         this.refreshDimensions();
         if (parent != null && !this.level().isClientSide()) {
            this.setNoGravity(true);
            Vec3 parentVec = parent.position().subtract(parent.xo, parent.yo, parent.zo);
            double restrictRadius = Mth.clamp(this.radius - parentVec.lengthSqr() * 0.25, this.radius * 0.5F, this.radius);
            if (parent instanceof EntityVoidWorm) {
               restrictRadius *= this.isTail() ? 0.8F : 0.4F;
            }

            double x = parent.getX() + restrictRadius * Math.cos(parent.getYRot() * 0.017453292519943295 + this.angleYaw);
            double yStretch = Math.abs(parent.getY() - parent.yo) > this.getBbWidth() ? parent.getY() : parent.yo;
            double y = yStretch + this.offsetY * this.getWormScale();
            double z = parent.getZ() + restrictRadius * Math.sin(parent.getYRot() * 0.017453292519943295 + this.angleYaw);
            double d0 = parent.xo - this.getX();
            double d1 = parent.yo - this.getY();
            double d2 = parent.zo - this.getZ();
            float yaw = (float)(Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
            float pitch = parent.getXRot();
            if (this.getPortalTicks() <= 1 && !this.doesParentControlPos) {
               float f2 = -((float)(Mth.atan2(d1, Mth.sqrt((float)(d0 * d0 + d2 * d2))) * 57.2957763671875));
               this.setPos(x, y, z);
               this.setXRot(this.limitAngle(this.getXRot(), f2, 5.0F));
               this.setYRot(yaw);
               this.entityData.set(WORM_YAW, this.getYRot());
            }

            this.markHurt();
            this.yHeadRot = this.getYRot();
            this.yBodyRot = pitch;
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

            if (parent instanceof EntityVoidWorm) {
               this.setWormAngle(((EntityVoidWorm)parent).prevWormAngle);
            } else if (parent instanceof EntityVoidWormPart) {
               this.setWormAngle(((EntityVoidWormPart)parent).prevWormAngle);
            }
         } else if (this.tickCount > 20 && !this.level().isClientSide()) {
            this.remove(RemovalReason.DISCARDED);
         }
      }

      if (this.tickCount % 400 == 0) {
         this.heal(1.0F);
      }

      super.tick();
      if (this.doesParentControlPos && this.enterPos != null) {
         this.teleportTo(this.enterPos.x, this.enterPos.y, this.enterPos.z);
      }

      if (this.getPortalTicks() > 0) {
         this.setPortalTicks(this.getPortalTicks() - 1);
         if (this.getPortalTicks() <= 5 && this.teleportPos != null) {
            Vec3 vec = this.teleportPos;
            this.teleportTo(vec.x, vec.y, vec.z);
            this.xOld = vec.x;
            this.yOld = vec.y;
            this.zOld = vec.z;
            if (this.getPortalTicks() == 5 && this.getChild() instanceof EntityVoidWormPart) {
               ((EntityVoidWormPart)this.getChild()).teleportTo(this.enterPos, this.teleportPos);
            }

            this.teleportPos = null;
         } else if (this.getPortalTicks() > 5 && this.enterPos != null) {
            this.teleportTo(this.enterPos.x, this.enterPos.y, this.enterPos.z);
         }

         if (this.getPortalTicks() == 0) {
            this.doesParentControlPos = false;
         }
      }
   }

   protected void tickDeath() {
      this.deathTime++;
      if (this.deathTime == 20) {
         this.remove(RemovalReason.DISCARDED);

         for (int i = 0; i < 30; i++) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level()
               .addParticle((ParticleOptions)AMParticleRegistry.WORM_PORTAL.get(), this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), d0, d1, d2);
         }
      }
   }

   public void die(DamageSource cause) {
      EntityVoidWorm worm = this.getWorm();
      if (worm != null) {
         int segments = Math.max(worm.getSegmentCount() / 2 - 1, 1);
         worm.setSegmentCount(segments);
         if (this.getChild() instanceof EntityVoidWormPart) {
            EntityVoidWormPart segment = (EntityVoidWormPart)this.getChild();
            EntityVoidWorm worm2 = AMCompat.create(AMEntityRegistry.VOID_WORM.get(), this.level());
            worm2.setNoAi(worm.isNoAi());
            worm2.setInvulnerable(worm.isInvulnerable());
            worm2.copyPosition(this);
            segment.copyPosition(this);
            worm2.setChildId(segment.getUUID());
            worm2.setSegmentCount(segments);
            segment.setParent(worm2);
            if (!this.level().isClientSide()) {
               this.level().addFreshEntity(worm2);
            }

            worm2.setSplitter(true);
            worm2.setBaseMaxHealth(worm.getBaseMaxHealth() / 2.0, true);
            worm2.setSplitFromUuid(worm.getUUID());
            worm2.setWormSpeed((float)Mth.clamp(worm.getWormSpeed() * 0.8, 0.4000000059604645, 1.0));
            worm2.resetWormScales();
            if (!this.level().isClientSide() && cause != null && cause.getEntity() instanceof ServerPlayer) {
               AMAdvancementTriggerRegistry.VOID_WORM_SPLIT.trigger((ServerPlayer)cause.getEntity());
            }
         }

         worm.resetWormScales();
      }
   }

   public boolean isAlliedTo(Entity entityIn) {
      EntityVoidWorm worm = this.getWorm();
      return super.isAlliedTo(entityIn) || worm != null && worm.isAlliedTo(entityIn);
   }

   public EntityVoidWorm getWorm() {
      Entity parent = this.getParent();

      while (parent instanceof EntityVoidWormPart) {
         parent = ((EntityVoidWormPart)parent).getParent();
      }

      return parent instanceof EntityVoidWorm ? (EntityVoidWorm)parent : null;
   }

   public Entity getChild() {
      UUID id = this.getChildId();
      return id != null && !this.level().isClientSide() ? ((ServerLevel)this.level()).getEntity(id) : null;
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

   @Nullable
   public ItemStack getPickResult() {
      Entity parent = this.getParent();
      return parent != null ? parent.getPickResult() : ItemStack.EMPTY;
   }

   public HumanoidArm getMainArm() {
      return null;
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity amServerEntity) {
      return AMPlatform.getEntitySpawningPacket(this, amServerEntity);
   }

   public void pushEntities() {
      List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.20000000298023224, 0.0, 0.20000000298023224));
      Entity parent = this.getParent();
      if (parent != null) {
         entities.stream()
            .filter(entity -> !entity.is(parent) && !(entity instanceof EntityVoidWormPart) && entity.isPushable())
            .forEach(entity -> entity.push(parent));
      }
   }

   public InteractionResult interact(Player player, InteractionHand hand) {
      Entity parent = this.getParent();
      return parent != null ? parent.interact(player, hand) : InteractionResult.PASS;
   }

   public boolean isHurt() {
      return this.getHealth() <= this.getHealthThreshold();
   }

   public double getHealthThreshold() {
      return 5.0;
   }

   public boolean hurt(DamageSource source, float damage) {
      if (super.hurt(source, damage)) {
         EntityVoidWorm worm = this.getWorm();
         if (worm != null) {
            worm.playHurtSoundWorm(source);
         }

         return true;
      } else {
         return false;
      }
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

   public float getWormYaw(float partialTicks) {
      return partialTicks == 0.0F
         ? (Float)this.entityData.get(WORM_YAW)
         : this.prevWormYaw + ((Float)this.entityData.get(WORM_YAW) - this.prevWormYaw) * partialTicks;
   }

   public void teleportTo(Vec3 enterPos, Vec3 to) {
      this.setPortalTicks(10);
      this.teleportPos = to;
      this.enterPos = enterPos;
      EntityVoidWorm worm = this.getWorm();
      if (worm != null && this.getChild() == null) {
         worm.fullyThrough = true;
      }
   }
}
