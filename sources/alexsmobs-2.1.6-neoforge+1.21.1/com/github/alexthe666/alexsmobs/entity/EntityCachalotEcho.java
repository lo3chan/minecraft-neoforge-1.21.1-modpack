package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import java.util.UUID;
import javax.annotation.Nullable;
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

public class EntityCachalotEcho extends Entity {
   private static final EntityDataAccessor<Boolean> RETURNING = SynchedEntityData.defineId(EntityCachalotEcho.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> FASTER_ANIM = SynchedEntityData.defineId(EntityCachalotEcho.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> GREEN = SynchedEntityData.defineId(EntityCachalotEcho.class, EntityDataSerializers.BOOLEAN);
   private UUID ownerUUID;
   private int ownerNetworkId;
   private boolean leftOwner;
   private boolean playerLaunched = false;

   public EntityCachalotEcho(EntityType p_i50162_1_, Level p_i50162_2_) {
      super(p_i50162_1_, p_i50162_2_);
   }

   public EntityCachalotEcho(Level worldIn, EntityCachalotWhale p_i47273_2_) {
      this(AMEntityRegistry.CACHALOT_ECHO.get(), worldIn);
      this.setShooter(p_i47273_2_);
   }

   public EntityCachalotEcho(Level worldIn, LivingEntity p_i47273_2_, boolean right, boolean green) {
      this(AMEntityRegistry.CACHALOT_ECHO.get(), worldIn);
      this.setShooter(p_i47273_2_);
      float rot = p_i47273_2_.yHeadRot + (right ? 90 : -90);
      this.playerLaunched = true;
      this.setGreen(green);
      this.setFasterAnimation(true);
      this.setPos(
         p_i47273_2_.getX() - p_i47273_2_.getBbWidth() * 0.5 * Mth.sin(rot * 0.017453292F),
         p_i47273_2_.getY() + 1.0,
         p_i47273_2_.getZ() + p_i47273_2_.getBbWidth() * 0.5 * Mth.cos(rot * 0.017453292F)
      );
   }

   @OnlyIn(Dist.CLIENT)
   public EntityCachalotEcho(Level worldIn, double x, double y, double z, double p_i47274_8_, double p_i47274_10_, double p_i47274_12_) {
      this(AMEntityRegistry.CACHALOT_ECHO.get(), worldIn);
      this.setPos(x, y, z);
      this.setDeltaMovement(p_i47274_8_, p_i47274_10_, p_i47274_12_);
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

   public boolean isReturning() {
      return (Boolean)this.entityData.get(RETURNING);
   }

   public void setReturning(boolean returning) {
      this.entityData.set(RETURNING, returning);
   }

   public boolean isFasterAnimation() {
      return (Boolean)this.entityData.get(FASTER_ANIM);
   }

   public void setFasterAnimation(boolean anim) {
      this.entityData.set(FASTER_ANIM, anim);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity amServerEntity) {
      return AMPlatform.getEntitySpawningPacket(this, amServerEntity);
   }

   public void tick() {
      double yMot = Mth.sqrt((float)(this.getDeltaMovement().x * this.getDeltaMovement().x + this.getDeltaMovement().z * this.getDeltaMovement().z));
      this.setXRot((float)(Mth.atan2(this.getDeltaMovement().y, yMot) * 57.2957763671875));
      if (!this.leftOwner) {
         this.leftOwner = this.checkLeftOwner();
      }

      super.tick();
      Vec3 vector3d = this.getDeltaMovement();
      HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
      if (raytraceresult.getType() != Type.MISS) {
         this.onImpact(raytraceresult);
      }

      if (this.isReturning() && this.getOwner() instanceof EntityCachalotWhale whale && whale.headPart.distanceTo(this) < whale.headPart.getBbWidth()) {
         this.remove(RemovalReason.DISCARDED);
         whale.recieveEcho();
      }

      if (!this.playerLaunched && !this.level().isClientSide() && !this.isInWaterOrBubble()) {
         this.remove(RemovalReason.DISCARDED);
      }

      if (this.tickCount > 100) {
         this.remove(RemovalReason.DISCARDED);
      }

      double d0 = this.getX() + vector3d.x;
      double d1 = this.getY() + vector3d.y;
      double d2 = this.getZ() + vector3d.z;
      this.updateRotation();
      if (this.playerLaunched) {
         this.noPhysics = true;
      }

      this.setDeltaMovement(vector3d.scale(0.9900000095367432));
      this.setNoGravity(true);
      this.setPos(d0, d1, d2);
      this.setYRot((float)(Mth.atan2(vector3d.x, vector3d.z) * 57.2957763671875) - 90.0F);
   }

   protected void onEntityHit(EntityHitResult result) {
      Entity entity = this.getOwner();
      if (this.isReturning()) {
         EntityCachalotWhale whale = null;
         if (entity instanceof EntityCachalotWhale var11
            && (result.getEntity() instanceof EntityCachalotWhale || result.getEntity() instanceof EntityCachalotPart)) {
            var11.recieveEcho();
            this.remove(RemovalReason.DISCARDED);
         }
      } else if (result.getEntity() != entity && !result.getEntity().is(entity)) {
         this.setReturning(true);
         if (entity instanceof EntityCachalotWhale) {
            Vec3 vec = ((EntityCachalotWhale)entity).getReturnEchoVector();
            double d0 = vec.x() - this.getX();
            double d1 = vec.y() - this.getY();
            double d2 = vec.z() - this.getZ();
            this.setDeltaMovement(Vec3.ZERO);
            EntityCachalotEcho echo = new EntityCachalotEcho(this.level(), (EntityCachalotWhale)entity);
            echo.copyPosition(this);
            this.remove(RemovalReason.DISCARDED);
            echo.setReturning(true);
            echo.shoot(d0, d1, d2, 1.0F, 0.0F);
            if (!this.level().isClientSide()) {
               this.level().addFreshEntity(echo);
            }
         }
      }
   }

   protected void onHitBlock(BlockHitResult p_230299_1_) {
      if (!this.level().isClientSide() && !this.playerLaunched) {
         this.remove(RemovalReason.DISCARDED);
      }
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(RETURNING, false);
      builder.define(FASTER_ANIM, false);
      builder.define(GREEN, false);
   }

   public void setShooter(@Nullable Entity entityIn) {
      if (entityIn != null) {
         this.ownerUUID = entityIn.getUUID();
         this.ownerNetworkId = entityIn.getId();
      }
   }

   @Nullable
   public Entity getOwner() {
      if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
         return ((ServerLevel)this.level()).getEntity(this.ownerUUID);
      } else {
         return this.ownerNetworkId != 0 ? this.level().getEntity(this.ownerNetworkId) : null;
      }
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      if (this.ownerUUID != null) {
         AMCompat.putUUID(compound, "Owner", this.ownerUUID);
      }

      if (this.leftOwner) {
         compound.putBoolean("LeftOwner", true);
      }

      compound.putBoolean("Green", this.isGreen());
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      if (AMCompat.hasUUID(compound, "Owner")) {
         this.ownerUUID = AMCompat.getUUID(compound, "Owner");
      }

      this.setGreen(AMCompat.getBoolean(compound, "Green"));
      this.leftOwner = AMCompat.getBoolean(compound, "LeftOwner");
   }

   private boolean checkLeftOwner() {
      Entity entity = this.getOwner();
      if (entity != null) {
         for (Entity entity1 : this.level()
            .getEntities(
               this,
               this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0),
               p_234613_0_ -> !p_234613_0_.isSpectator() && p_234613_0_.isPickable()
            )) {
            if (entity1.getRootVehicle() == entity.getRootVehicle()) {
               return false;
            }
         }
      }

      return true;
   }

   public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
      Vec3 vector3d = new Vec3(x, y, z)
         .normalize()
         .add(
            this.random.nextGaussian() * 0.0075 * inaccuracy,
            this.random.nextGaussian() * 0.0075 * inaccuracy,
            this.random.nextGaussian() * 0.0075 * inaccuracy
         )
         .scale(velocity);
      this.setDeltaMovement(vector3d);
      float f = Mth.sqrt((float)this.horizontalMag(vector3d));
      this.setYRot((float)(Mth.atan2(vector3d.x, vector3d.z) * 57.2957763671875));
      this.setXRot((float)(Mth.atan2(vector3d.y, f) * 57.2957763671875));
      this.yRotO = this.getYRot();
      this.xRotO = this.getXRot();
   }

   private double horizontalMag(Vec3 vector3d) {
      return vector3d.x * vector3d.x + vector3d.z * vector3d.z;
   }

   public void shootFromRotation(Entity p_234612_1_, float p_234612_2_, float p_234612_3_, float p_234612_4_, float p_234612_5_, float p_234612_6_) {
      float f3 = p_234612_3_ * 0.017453292F;
      float f0 = Mth.cos(p_234612_2_ * 0.017453292F);
      float f = -Mth.sin(f3) * f0;
      float f1 = -Mth.sin((p_234612_2_ + p_234612_4_) * 0.017453292F);
      float f2 = Mth.cos(f3) * f0;
      this.shoot(f, f1, f2, p_234612_5_, p_234612_6_);
      Vec3 vector3d = p_234612_1_.getDeltaMovement();
      this.setDeltaMovement(this.getDeltaMovement().add(vector3d.x, p_234612_1_.onGround() ? 0.0 : vector3d.y, vector3d.z));
   }

   protected void onImpact(HitResult result) {
      Type raytraceresult$type = result.getType();
      if (!this.playerLaunched) {
         if (raytraceresult$type == Type.ENTITY) {
            this.onEntityHit((EntityHitResult)result);
         } else if (raytraceresult$type == Type.BLOCK) {
            this.onHitBlock((BlockHitResult)result);
         }
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

   protected boolean canHitEntity(Entity p_230298_1_) {
      if (this.playerLaunched) {
         return false;
      } else if (this.isReturning()) {
         return p_230298_1_ instanceof EntityCachalotPart || p_230298_1_ instanceof EntityCachalotWhale;
      } else if (p_230298_1_ instanceof EntityCachalotPart) {
         return false;
      } else if (!p_230298_1_.isSpectator() && p_230298_1_.isAlive() && p_230298_1_.isPickable()) {
         Entity entity = this.getOwner();
         return entity == null || this.leftOwner || !entity.isPassengerOfSameVehicle(p_230298_1_);
      } else {
         return false;
      }
   }

   protected void updateRotation() {
      Vec3 vector3d = this.getDeltaMovement();
      float f = Mth.sqrt((float)this.horizontalMag(vector3d));
      this.setXRot(lerpRotation(this.xRotO, (float)(Mth.atan2(vector3d.y, f) * 57.2957763671875)));
      this.setYRot(lerpRotation(this.yRotO, (float)(Mth.atan2(vector3d.x, vector3d.z) * 57.2957763671875)));
   }

   public boolean isGreen() {
      return (Boolean)this.entityData.get(GREEN);
   }

   public void setGreen(boolean bool) {
      this.entityData.set(GREEN, bool);
   }
}
