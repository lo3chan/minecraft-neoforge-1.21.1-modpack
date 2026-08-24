package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityGust extends Entity {
   protected static final EntityDataAccessor<Boolean> VERTICAL = SynchedEntityData.defineId(EntityGust.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Float> X_DIR = SynchedEntityData.defineId(EntityGust.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> Y_DIR = SynchedEntityData.defineId(EntityGust.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> Z_DIR = SynchedEntityData.defineId(EntityGust.class, EntityDataSerializers.FLOAT);
   private Entity pushedEntity = null;

   public EntityGust(EntityType p_i50162_1_, Level p_i50162_2_) {
      super(p_i50162_1_, p_i50162_2_);
   }

   public EntityGust(Level worldIn) {
      this(AMEntityRegistry.GUST.get(), worldIn);
   }

   public void push(Entity entityIn) {
   }

   protected static float lerpRotation(float p_234614_0_, float p_234614_1_) {
      while (p_234614_1_ - p_234614_0_ < -180.0F) {
         p_234614_0_ -= 360.0F;
      }

      while (p_234614_1_ - p_234614_0_ >= 180.0F) {
         p_234614_0_ += 360.0F;
      }

      return Mth.lerp(0.2F, p_234614_0_, p_234614_1_);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity amServerEntity) {
      return AMPlatform.getEntitySpawningPacket(this, amServerEntity);
   }

   public void tick() {
      super.tick();
      if (this.tickCount > 300) {
         this.remove(RemovalReason.DISCARDED);
      }

      for (int i = 0; i < 1 + this.random.nextInt(1); i++) {
         this.level()
            .addParticle(
               (ParticleOptions)AMParticleRegistry.GUSTER_SAND_SPIN.get(),
               this.getX() + 0.5F * (this.random.nextFloat() - 0.5F),
               this.getY() + 0.5F * (this.random.nextFloat() - 0.5F),
               this.getZ() + 0.5F * (this.random.nextFloat() - 0.5F),
               this.getX(),
               this.getY() + 0.5,
               this.getZ()
            );
      }

      Vec3 vector3d = new Vec3(
         ((Float)this.entityData.get(X_DIR)).floatValue(), ((Float)this.entityData.get(Y_DIR)).floatValue(), ((Float)this.entityData.get(Z_DIR)).floatValue()
      );
      HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
      if (raytraceresult != null && raytraceresult.getType() != Type.MISS && this.tickCount > 4) {
         this.onImpact(raytraceresult);
      }

      List<Entity> list = this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(0.1));
      if (this.pushedEntity != null && this.distanceTo(this.pushedEntity) > 2.0F) {
         this.pushedEntity = null;
      }

      double d0 = this.getX() + vector3d.x;
      double d1 = this.getY() + vector3d.y;
      double d2 = this.getZ() + vector3d.z;
      if (this.getY() > AMCompat.maxBuildHeight(this.level())) {
         this.remove(RemovalReason.DISCARDED);
      }

      this.updateRotation();
      if (this.isInWaterOrBubble()) {
         this.remove(RemovalReason.DISCARDED);
      } else {
         this.setDeltaMovement(vector3d);
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.05999999865889549, 0.0));
         this.setPos(d0, d1, d2);
         if (this.pushedEntity != null) {
            this.pushedEntity.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.063, 0.0));
         }

         for (Entity e : list) {
            e.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.068, 0.0));
            if (e.getDeltaMovement().y < 0.0) {
               e.setDeltaMovement(e.getDeltaMovement().multiply(1.0, 0.0, 1.0));
            }

            e.fallDistance = 0.0F;
         }
      }
   }

   public void setGustDir(float x, float y, float z) {
      this.entityData.set(X_DIR, x);
      this.entityData.set(Y_DIR, y);
      this.entityData.set(Z_DIR, z);
   }

   public float getGustDir(int xyz) {
      return (Float)this.entityData.get(xyz == 2 ? Z_DIR : (xyz == 1 ? Y_DIR : X_DIR));
   }

   protected void onEntityHit(EntityHitResult result) {
      Entity entity = result.getEntity();
      if (entity instanceof EntityGust other) {
         double avgX = (other.getX() + this.getX()) / 2.0;
         double avgY = (other.getY() + this.getY()) / 2.0;
         double avgZ = (other.getZ() + this.getZ()) / 2.0;
         other.setPos(avgX, avgY, avgZ);
         other.setGustDir(other.getGustDir(0) + this.getGustDir(0), other.getGustDir(1) + this.getGustDir(1), other.getGustDir(2) + this.getGustDir(2));
         if (this.isAlive() && other.isAlive()) {
            this.remove(RemovalReason.DISCARDED);
         }
      } else if (entity != null) {
         this.pushedEntity = entity;
      }
   }

   protected boolean canHitEntity(Entity p_230298_1_) {
      return !p_230298_1_.isSpectator();
   }

   protected void onHitBlock(BlockHitResult p_230299_1_) {
      if (p_230299_1_.getBlockPos() != null) {
         BlockPos pos = p_230299_1_.getBlockPos();
         if (this.level().isWaterAt(pos) && !this.level().isClientSide()) {
            this.remove(RemovalReason.DISCARDED);
         }
      }
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(VERTICAL, false);
      builder.define(X_DIR, 0.0F);
      builder.define(Y_DIR, 0.0F);
      builder.define(Z_DIR, 0.0F);
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putBoolean("VerticalTornado", this.getVertical());
      compound.putFloat("GustDirX", (Float)this.entityData.get(X_DIR));
      compound.putFloat("GustDirY", (Float)this.entityData.get(Y_DIR));
      compound.putFloat("GustDirZ", (Float)this.entityData.get(Z_DIR));
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.entityData.set(X_DIR, AMCompat.getFloat(compound, "GustDirX"));
      this.entityData.set(Y_DIR, AMCompat.getFloat(compound, "GustDirX"));
      this.entityData.set(Z_DIR, AMCompat.getFloat(compound, "GustDirX"));
      this.setVertical(AMCompat.getBoolean(compound, "VerticalTornado"));
   }

   public void setVertical(boolean vertical) {
      this.entityData.set(VERTICAL, vertical);
   }

   public boolean getVertical() {
      return (Boolean)this.entityData.get(VERTICAL);
   }

   protected void onImpact(HitResult result) {
      Type raytraceresult$type = result.getType();
      if (raytraceresult$type == Type.ENTITY) {
         this.onEntityHit((EntityHitResult)result);
      } else if (raytraceresult$type == Type.BLOCK) {
         this.onHitBlock((BlockHitResult)result);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void lerpMotion(double x, double y, double z) {
      this.setDeltaMovement(x, y, z);
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         float f = Mth.sqrt((float)(x * x + z * z));
         this.setXRot((float)(Mth.atan2(y, f) * 57.2957763671875));
         this.setYRot((float)(Mth.atan2(x, z) * 57.2957763671875));
         this.xRotO = this.getXRot();
         this.yRotO = this.getYRot();
         this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
      }
   }

   protected void updateRotation() {
      Vec3 vector3d = this.getDeltaMovement();
      float f = Mth.sqrt((float)(vector3d.x * vector3d.x + vector3d.z * vector3d.z));
      this.setXRot(lerpRotation(this.xRotO, (float)(Mth.atan2(vector3d.y, f) * 57.2957763671875)));
      this.setYRot(lerpRotation(this.yRotO, (float)(Mth.atan2(vector3d.x, vector3d.z) * 57.2957763671875)));
   }
}
