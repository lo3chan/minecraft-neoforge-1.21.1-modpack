package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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

public class EntityFart extends Entity {
   private UUID ownerUUID;
   private int ownerNetworkId;
   private boolean leftOwner;

   public EntityFart(EntityType p_i50162_1_, Level p_i50162_2_) {
      super(p_i50162_1_, p_i50162_2_);
   }

   public EntityFart(Level worldIn, LivingEntity p_i47273_2_, boolean right) {
      this(AMEntityRegistry.FART.get(), worldIn);
      this.setShooter(p_i47273_2_);
      float rot = p_i47273_2_.yHeadRot + (right ? 60 : -60);
      this.setPos(
         p_i47273_2_.getX() - p_i47273_2_.getBbWidth() * 0.5 * Mth.sin(rot * 0.017453292F),
         p_i47273_2_.getEyeY() - 0.20000000298023224,
         p_i47273_2_.getZ() + p_i47273_2_.getBbWidth() * 0.5 * Mth.cos(rot * 0.017453292F)
      );
   }

   public void tick() {
      super.tick();
      Vec3 vector3d = this.getDeltaMovement();
      HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
      if (raytraceresult != null && raytraceresult.getType() != Type.MISS) {
         this.onImpact(raytraceresult);
      }

      this.updateRotation();
      double d0 = this.getX() + vector3d.x;
      double d1 = this.getY() + vector3d.y;
      double d2 = this.getZ() + vector3d.z;
      this.setDeltaMovement(vector3d.scale(0.949999988079071));
      this.setPos(d0, d1, d2);
      if (this.tickCount > 30) {
         this.remove(RemovalReason.DISCARDED);
      }
   }

   public boolean isNoGravity() {
      return true;
   }

   protected void onImpact(HitResult result) {
      Type raytraceresult$type = result.getType();
      if (raytraceresult$type == Type.ENTITY) {
         this.onEntityHit((EntityHitResult)result);
      } else if (raytraceresult$type == Type.BLOCK) {
         this.onHitBlock((BlockHitResult)result);
      }
   }

   protected void onEntityHit(EntityHitResult result) {
      if (result.getEntity() instanceof LivingEntity living) {
         living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 300, 0));

         for (int i = 0; i < 10 + this.random.nextInt(6); i++) {
            this.level()
               .addParticle(
                  (ParticleOptions)AMParticleRegistry.SMELLY.get(), living.getRandomX(1.0), living.getRandomY(), living.getRandomZ(1.0), 0.0, 0.0, 0.0
               );
         }

         for (Mob nearby : this.level().getEntitiesOfClass(Mob.class, living.getBoundingBox().inflate(15.0))) {
            if (nearby != living
               && nearby.getId() != living.getId()
               && !nearby.getUUID().equals(living.getUUID())
               && !nearby.isAlliedTo(living)
               && !living.isAlliedTo(nearby)
               && !(living instanceof IHurtableMultipart)) {
               nearby.setLastHurtByMob(living);
               nearby.setTarget(living);
            }
         }
      }
   }

   protected void onHitBlock(BlockHitResult result) {
      this.remove(RemovalReason.DISCARDED);
   }

   protected boolean canHitEntity(Entity hit) {
      if (!hit.isSpectator() && hit.isAlive() && hit.isPickable()) {
         Entity entity = this.getShooter();
         return entity == null || this.leftOwner || !entity.isPassengerOfSameVehicle(hit);
      } else {
         return false;
      }
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

   protected void updateRotation() {
      Vec3 vector3d = this.getDeltaMovement().normalize();
      float f = Mth.sqrt((float)vector3d.horizontalDistanceSqr());
      this.setXRot(lerpRotation(this.xRotO, (float)(Mth.atan2(vector3d.y, f) * 57.2957763671875)));
      this.setYRot(lerpRotation(this.yRotO, (float)(Mth.atan2(vector3d.x, vector3d.z) * 57.2957763671875)));
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

   protected void addAdditionalSaveData(CompoundTag compound) {
      if (this.ownerUUID != null) {
         AMCompat.putUUID(compound, "Owner", this.ownerUUID);
      }

      if (this.leftOwner) {
         compound.putBoolean("LeftOwner", true);
      }
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity amServerEntity) {
      return AMPlatform.getEntitySpawningPacket(this, amServerEntity);
   }

   protected void defineSynchedData(Builder builder) {
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      if (AMCompat.hasUUID(compound, "Owner")) {
         this.ownerUUID = AMCompat.getUUID(compound, "Owner");
      }

      this.leftOwner = AMCompat.getBoolean(compound, "LeftOwner");
   }

   @Nullable
   public Entity getShooter() {
      if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
         return ((ServerLevel)this.level()).getEntity(this.ownerUUID);
      } else {
         return this.ownerNetworkId != 0 ? this.level().getEntity(this.ownerNetworkId) : null;
      }
   }

   public void setShooter(@Nullable Entity entityIn) {
      if (entityIn != null) {
         this.ownerUUID = entityIn.getUUID();
         this.ownerNetworkId = entityIn.getId();
      }
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
      float f = Mth.sqrt((float)vector3d.horizontalDistanceSqr());
      this.setYRot((float)(Mth.atan2(vector3d.x, vector3d.z) * 57.2957763671875));
      this.setXRot((float)(Mth.atan2(vector3d.y, f) * 57.2957763671875));
      this.yRotO = this.getYRot();
      this.xRotO = this.getXRot();
   }

   private boolean checkLeftOwner() {
      Entity entity = this.getShooter();
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
}
