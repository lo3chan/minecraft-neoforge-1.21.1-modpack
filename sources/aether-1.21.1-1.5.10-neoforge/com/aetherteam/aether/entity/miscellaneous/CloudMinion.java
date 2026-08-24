package com.aetherteam.aether.entity.miscellaneous;

import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.entity.EntityUtil;
import com.aetherteam.aether.entity.projectile.crystal.CloudCrystal;
import javax.annotation.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CloudMinion extends FlyingMob {
   private static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.defineId(CloudMinion.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_IS_RIGHT_ID = SynchedEntityData.defineId(CloudMinion.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> DATA_LIFESPAN_ID = SynchedEntityData.defineId(CloudMinion.class, EntityDataSerializers.INT);
   private boolean shouldShoot;
   private double targetX;
   private double targetY;
   private double targetZ;

   public CloudMinion(EntityType<? extends FlyingMob> type, Level level) {
      super(type, level);
   }

   public CloudMinion(Level level, Player player, HumanoidArm armSide) {
      super((EntityType)AetherEntityTypes.CLOUD_MINION.get(), level);
      this.setOwner(player);
      this.setSide(armSide);
      this.setLifeSpan(3600);
      this.noPhysics = true;
      this.setPositionFromOwner();
      this.setPos(this.targetX, this.targetY, this.targetZ);
      this.setXRot(player.getXRot());
      this.setYRot(player.getYRot());
   }

   public static Builder createMobAttributes() {
      return FlyingMob.createMobAttributes().add(Attributes.MAX_HEALTH, 1.0).add(Attributes.MOVEMENT_SPEED, 10.0);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_OWNER_ID, 0);
      builder.define(DATA_IS_RIGHT_ID, true);
      builder.define(DATA_LIFESPAN_ID, 0);
   }

   public void tick() {
      super.tick();
      this.setLifeSpan(this.getLifeSpan() - 1);
      if (this.getLifeSpan() <= 0) {
         this.spawnExplosionParticles();
         this.remove(RemovalReason.DISCARDED);
      } else if (this.getOwner() != null) {
         if (this.getOwner().isAlive()) {
            this.setPositionFromOwner();
            this.setRotationFromOwner();
            if (this.atShoulder()) {
               Vec3 motion = this.getDeltaMovement();
               this.setDeltaMovement(motion.multiply(0.65, 0.65, 0.65));
               if (this.shouldShoot()) {
                  float offset = this.getSide() == HumanoidArm.RIGHT ? 2.0F : -2.0F;
                  float rotation = Mth.wrapDegrees(this.getYRot() + offset);
                  CloudCrystal crystal = new CloudCrystal(this.level());
                  crystal.setPos(this.getX(), this.getY(), this.getZ());
                  crystal.shootFromRotation(this, this.getXRot(), rotation, 0.0F, 1.0F, 1.0F);
                  crystal.setOwner(this.getOwner());
                  if (!this.level().isClientSide()) {
                     this.level().addFreshEntity(crystal);
                  }

                  this.playSound(
                     (SoundEvent)AetherSoundEvents.ENTITY_CLOUD_MINION_SHOOT.get(),
                     0.75F,
                     (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F + 1.0F
                  );
                  this.setShouldShoot(false);
               }
            } else {
               this.approachOwner();
            }
         } else {
            this.spawnExplosionParticles();
            this.remove(RemovalReason.KILLED);
         }
      } else {
         this.remove(RemovalReason.DISCARDED);
      }
   }

   public void setPositionFromOwner() {
      if (this.getOwner() != null) {
         if (this.distanceTo(this.getOwner()) > 2.0F) {
            this.targetX = this.getOwner().getX();
            this.targetY = this.getOwner().getY() + 1.0;
            this.targetZ = this.getOwner().getZ();
         } else {
            double yaw = this.getOwner().getYRot();
            if (this.getSide() == HumanoidArm.RIGHT) {
               yaw -= 90.0;
            } else {
               yaw += 90.0;
            }

            yaw /= -57.2957763671875;
            this.targetX = this.getOwner().getX() + Math.sin(yaw) * 1.05;
            this.targetY = this.getOwner().getY() + 1.0;
            this.targetZ = this.getOwner().getZ() + Math.cos(yaw) * 1.05;
         }
      }
   }

   public void setRotationFromOwner() {
      if (this.getOwner() != null) {
         this.setYRot(this.getOwner().getYRot() + (this.getSide() == HumanoidArm.RIGHT ? 1.0F : -1.0F));
         this.setXRot(this.getOwner().getXRot());
         this.setYHeadRot(this.getOwner().getYHeadRot());
      }
   }

   public boolean atShoulder() {
      double x = this.getX() - this.targetX;
      double y = this.getY() - this.targetY;
      double z = this.getZ() - this.targetZ;
      return Math.sqrt(x * x + y * y + z * z) < 0.4;
   }

   public void approachOwner() {
      double x = this.targetX - this.getX();
      double y = this.targetY - this.getY();
      double z = this.targetZ - this.getZ();
      double sqrt = Math.min(Math.sqrt(x * x + y * y + z * z), 1.5);
      Vec3 motion = this.getDeltaMovement();
      double motionX = (motion.x() + x / (sqrt * 3.25)) * sqrt / 2.0;
      double motionY = (motion.y() + y / (sqrt * 3.25)) * sqrt / 2.0;
      double motionZ = (motion.z() + z / (sqrt * 3.25)) * sqrt / 2.0;
      this.setDeltaMovement(motionX, motionY, motionZ);
   }

   public void spawnExplosionParticles() {
      if (this.level().isClientSide()) {
         EntityUtil.spawnSummoningExplosionParticles(this);
      } else {
         this.level().broadcastEntityEvent(this, (byte)70);
      }
   }

   protected void pushEntities() {
   }

   protected boolean canRide(Entity vehicle) {
      return false;
   }

   public boolean hurt(DamageSource source, float damage) {
      return false;
   }

   @Nullable
   public Player getOwner() {
      return (Player)this.level().getEntity((Integer)this.getEntityData().get(DATA_OWNER_ID));
   }

   public void setOwner(Player entity) {
      this.getEntityData().set(DATA_OWNER_ID, entity.getId());
   }

   public HumanoidArm getSide() {
      return this.getEntityData().get(DATA_IS_RIGHT_ID) ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
   }

   public void setSide(HumanoidArm armSide) {
      this.getEntityData().set(DATA_IS_RIGHT_ID, armSide == HumanoidArm.RIGHT);
   }

   public int getLifeSpan() {
      return (Integer)this.getEntityData().get(DATA_LIFESPAN_ID);
   }

   public void setLifeSpan(int lifespan) {
      this.getEntityData().set(DATA_LIFESPAN_ID, lifespan);
   }

   public boolean shouldShoot() {
      return this.shouldShoot;
   }

   public void setShouldShoot(boolean shouldShoot) {
      this.shouldShoot = shouldShoot;
   }

   public void handleEntityEvent(byte id) {
      if (id == 70) {
         EntityUtil.spawnSummoningExplosionParticles(this);
      } else {
         super.handleEntityEvent(id);
      }
   }
}
