package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.util.TendonWhipUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EntityTendonSegment extends Entity {
   private static final EntityDataAccessor<Optional<UUID>> CREATOR_ID = SynchedEntityData.defineId(
      EntityTendonSegment.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Integer> FROM_ID = SynchedEntityData.defineId(EntityTendonSegment.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> TARGET_COUNT = SynchedEntityData.defineId(EntityTendonSegment.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> CURRENT_TARGET_ID = SynchedEntityData.defineId(EntityTendonSegment.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> PROGRESS = SynchedEntityData.defineId(EntityTendonSegment.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(EntityTendonSegment.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> RETRACTING = SynchedEntityData.defineId(EntityTendonSegment.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> HAS_CLAW = SynchedEntityData.defineId(EntityTendonSegment.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> HAS_GLINT = SynchedEntityData.defineId(EntityTendonSegment.class, EntityDataSerializers.BOOLEAN);
   private List<Entity> previouslyTouched = new ArrayList<>();
   private boolean hasTouched = false;
   private boolean hasChained = false;
   public float prevProgress = 0.0F;
   public static final float MAX_EXTEND_TIME = 3.0F;

   public EntityTendonSegment(EntityType<?> type, Level level) {
      super(type, level);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity amServerEntity) {
      return AMPlatform.getEntitySpawningPacket(this, amServerEntity);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(CREATOR_ID, Optional.empty());
      builder.define(FROM_ID, -1);
      builder.define(TARGET_COUNT, 0);
      builder.define(CURRENT_TARGET_ID, -1);
      builder.define(PROGRESS, 0.0F);
      builder.define(DAMAGE, 5.0F);
      builder.define(RETRACTING, false);
      builder.define(HAS_CLAW, true);
      builder.define(HAS_GLINT, false);
   }

   public void tick() {
      float progress = this.getProgress();
      this.prevProgress = progress;
      if (this.tickCount < 1) {
         this.onJoinWorld();
      } else if (this.tickCount == 1 && !this.level().isClientSide()) {
         this.playSound(AMSoundRegistry.TENDON_WHIP.get(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
      }

      super.tick();
      Entity creator = this.getCreatorEntity();
      Entity current = this.getToEntity();
      if (progress < 3.0F && !this.isRetracting()) {
         this.setProgress(progress + 1.0F);
      }

      if (progress > 0.0F && this.isRetracting()) {
         this.setProgress(progress - 1.0F);
      }

      if (progress == 0.0F && this.isRetracting()) {
         if (this.getFromEntity() instanceof EntityTendonSegment tendonSegment) {
            tendonSegment.setRetracting(true);
            this.updateLastTendon(tendonSegment);
         } else {
            this.updateLastTendon(null);
         }

         this.remove(RemovalReason.DISCARDED);
      }

      if (creator instanceof LivingEntity && current != null) {
         Vec3 target = new Vec3(current.getX(), current.getY(0.4000000059604645), current.getZ());
         Vec3 lerp = target.subtract(this.position());
         this.setDeltaMovement(lerp.scale(0.5));
         if (!this.level().isClientSide() && !this.hasTouched && progress >= 3.0F) {
            this.hasTouched = true;
            Entity entity = this.getCreatorEntity();
            if (entity instanceof LivingEntity
               && current != creator
               && AMCompat.hurt(
                  current,
                  this.damageSources().mobProjectile(this, (LivingEntity)entity),
                  (float)this.getDamageFor((LivingEntity)creator, (LivingEntity)entity)
               )) {
               AMCompat.enchantDamageEffects((LivingEntity)creator, entity);
            }
         }
      }

      Vec3 vector3d = this.getDeltaMovement();
      if (!this.level().isClientSide() && !this.hasChained) {
         if (this.getTargetsHit() > 3) {
            this.setRetracting(true);
         } else if (creator instanceof LivingEntity && this.getProgress() >= 3.0F) {
            Entity closestValid = null;

            for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(8.0))) {
               if (!entity.equals(creator)
                  && !this.previouslyTouched.contains(entity)
                  && this.isValidTarget((LivingEntity)creator, entity)
                  && this.hasLineOfSight(entity)
                  && (closestValid == null || this.distanceTo(entity) < this.distanceTo(closestValid))) {
                  closestValid = entity;
               }
            }

            if (closestValid != null) {
               this.createChain(closestValid);
               this.hasChained = true;
            } else {
               this.setRetracting(true);
            }
         }
      }

      double d0 = this.getX() + vector3d.x;
      double d1 = this.getY() + vector3d.y;
      double d2 = this.getZ() + vector3d.z;
      this.setDeltaMovement(vector3d.scale(0.9900000095367432));
      this.setPos(d0, d1, d2);
   }

   private boolean isValidTarget(LivingEntity creator, Entity entity) {
      return !creator.isAlliedTo(entity) && !entity.isAlliedTo(creator) && entity instanceof Mob
         ? true
         : creator.getLastHurtMob() != null && creator.getLastHurtMob().getUUID().equals(entity.getUUID())
            || creator.getLastHurtByMob() != null && creator.getLastHurtByMob().getUUID().equals(entity.getUUID());
   }

   private double getDamageFor(LivingEntity creator, LivingEntity entity) {
      ItemStack stack = creator.getItemInHand(InteractionHand.MAIN_HAND).is(AMItemRegistry.TENDON_WHIP.get())
         ? creator.getItemInHand(InteractionHand.MAIN_HAND)
         : creator.getItemInHand(InteractionHand.OFF_HAND);
      double dmg = this.getBaseDamage();
      if (stack.is(AMItemRegistry.TENDON_WHIP.get())) {
         dmg += AMCompat.getDamageBonus(stack, entity);
      }

      return dmg;
   }

   private double getDamageForItem(ItemStack itemStack) {
      return AMCompat.attackDamageOf(itemStack, EquipmentSlot.MAINHAND);
   }

   private boolean hasLineOfSight(Entity entity) {
      if (entity.level() != this.level()) {
         return false;
      } else {
         Vec3 vec3 = new Vec3(this.getX(), this.getEyeY(), this.getZ());
         Vec3 vec31 = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
         return vec31.distanceTo(vec3) > 128.0
            ? false
            : this.level().clip(new ClipContext(vec3, vec31, Block.COLLIDER, Fluid.NONE, this)).getType() == Type.MISS;
      }
   }

   private void updateLastTendon(EntityTendonSegment lastTendon) {
      Entity creator = this.getCreatorEntity();
      if (creator == null) {
         creator = this.level().getPlayerByUUID(this.getCreatorEntityUUID());
      }

      if (creator instanceof LivingEntity) {
         TendonWhipUtil.setLastTendon((LivingEntity)creator, lastTendon);
      }
   }

   private void createChain(Entity closestValid) {
      this.entityData.set(HAS_CLAW, false);
      EntityTendonSegment child = AMCompat.create(AMEntityRegistry.TENDON_SEGMENT.get(), this.level());
      child.previouslyTouched = new ArrayList<>(this.previouslyTouched);
      child.previouslyTouched.add(closestValid);
      child.setCreatorEntityUUID(this.getCreatorEntityUUID());
      child.setFromEntityID(this.getId());
      child.setToEntityID(closestValid.getId());
      child.setPos(closestValid.getX(), closestValid.getY(0.4000000059604645), closestValid.getZ());
      child.setTargetsHit(this.getTargetsHit() + 1);
      this.updateLastTendon(child);
      child.setHasGlint(this.hasGlint());
      this.level().addFreshEntity(child);
   }

   private void onJoinWorld() {
      Entity creator = this.getCreatorEntity();
      if (creator == null) {
         creator = this.level().getPlayerByUUID(this.getCreatorEntityUUID());
      }

      Entity prior = this.getFromEntity();
      if (creator instanceof Player player) {
         ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND).is(AMItemRegistry.TENDON_WHIP.get())
            ? player.getItemInHand(InteractionHand.MAIN_HAND)
            : player.getItemInHand(InteractionHand.OFF_HAND);
         if (stack.is(AMItemRegistry.TENDON_WHIP.get())) {
            this.setHasGlint(stack.hasFoil());
         }

         float dmg = 2.0F;
         if (prior instanceof EntityTendonSegment) {
            dmg = Math.max(((EntityTendonSegment)prior).getBaseDamage() - 1.0F, 2.0F);
         } else {
            dmg = (float)this.getDamageForItem(stack);
         }

         this.entityData.set(DAMAGE, dmg);
      }
   }

   private float getBaseDamage() {
      return (Float)this.entityData.get(DAMAGE);
   }

   public UUID getCreatorEntityUUID() {
      return (UUID)((Optional)this.entityData.get(CREATOR_ID)).orElse(null);
   }

   public void setCreatorEntityUUID(UUID id) {
      this.entityData.set(CREATOR_ID, Optional.ofNullable(id));
   }

   public Entity getCreatorEntity() {
      UUID uuid = this.getCreatorEntityUUID();
      return uuid != null && !this.level().isClientSide() ? ((ServerLevel)this.level()).getEntity(uuid) : null;
   }

   public int getFromEntityID() {
      return (Integer)this.entityData.get(FROM_ID);
   }

   public void setFromEntityID(int id) {
      this.entityData.set(FROM_ID, id);
   }

   public Entity getFromEntity() {
      return this.getFromEntityID() == -1 ? null : this.level().getEntity(this.getFromEntityID());
   }

   public int getToEntityID() {
      return (Integer)this.entityData.get(CURRENT_TARGET_ID);
   }

   public void setToEntityID(int id) {
      this.entityData.set(CURRENT_TARGET_ID, id);
   }

   public Entity getToEntity() {
      return this.getToEntityID() == -1 ? null : this.level().getEntity(this.getToEntityID());
   }

   public int getTargetsHit() {
      return (Integer)this.entityData.get(TARGET_COUNT);
   }

   public void setTargetsHit(int i) {
      this.entityData.set(TARGET_COUNT, i);
   }

   public float getProgress() {
      return (Float)this.entityData.get(PROGRESS);
   }

   public void setProgress(float progress) {
      this.entityData.set(PROGRESS, progress);
   }

   public boolean isRetracting() {
      return (Boolean)this.entityData.get(RETRACTING);
   }

   public void setRetracting(boolean retract) {
      this.entityData.set(RETRACTING, retract);
   }

   public boolean hasGlint() {
      return (Boolean)this.entityData.get(HAS_GLINT);
   }

   public void setHasGlint(boolean glint) {
      this.entityData.set(HAS_GLINT, glint);
   }

   public boolean hasClaw() {
      return (Boolean)this.entityData.get(HAS_CLAW);
   }

   protected void readAdditionalSaveData(CompoundTag p_20052_) {
   }

   protected void addAdditionalSaveData(CompoundTag p_20139_) {
   }

   public boolean isCreator(Entity mob) {
      return this.getCreatorEntityUUID() != null && mob.getUUID().equals(this.getCreatorEntityUUID());
   }
}
