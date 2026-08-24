package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.util.VineLassoUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityVineLasso extends Entity {
   private UUID ownerUUID;
   private int ownerNetworkId;
   private boolean leftOwner;

   public EntityVineLasso(EntityType p_i50162_1_, Level p_i50162_2_) {
      super(p_i50162_1_, p_i50162_2_);
   }

   public EntityVineLasso(Level worldIn, LivingEntity entity) {
      this(AMEntityRegistry.VINE_LASSO.get(), worldIn);
      this.setShooter(entity);
      this.setPos(entity.getX(), entity.getEyeY() + 0.15000000596046448, entity.getZ());
   }

   @OnlyIn(Dist.CLIENT)
   public EntityVineLasso(Level worldIn, double x, double y, double z, double p_i47274_8_, double p_i47274_10_, double p_i47274_12_) {
      this(AMEntityRegistry.VINE_LASSO.get(), worldIn);
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

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity amServerEntity) {
      return AMPlatform.getEntitySpawningPacket(this, amServerEntity);
   }

   public void tick() {
      if (!this.leftOwner) {
         this.leftOwner = this.checkLeftOwner();
      }

      super.tick();
      Vec3 vector3d = this.getDeltaMovement();
      HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
      if (raytraceresult != null && raytraceresult.getType() != Type.MISS) {
         this.onImpact(raytraceresult);
      }

      this.updateRotation();
      if (this.getOwner() != null && this.distanceTo(this.getOwner()) > 15.0F) {
         this.removeAndAddToInventory();
      }

      if (this.level().getBlockStates(this.getBoundingBox()).noneMatch(BlockStateBase::isAir) && !this.isInWater() && !this.isInLava()) {
         this.removeAndAddToInventory();
      } else {
         double d0 = this.getX() + vector3d.x;
         double d1 = this.getY() + vector3d.y;
         double d2 = this.getZ() + vector3d.z;
         this.setDeltaMovement(vector3d.scale(0.9900000095367432));
         if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.019999999552965164, 0.0));
         }

         this.setPos(d0, d1, d2);
      }
   }

   protected void onEntityHit(EntityHitResult p_213868_1_) {
      Entity entity = this.getOwner();
      if (entity instanceof LivingEntity
         && p_213868_1_.getEntity() != this.getOwner()
         && p_213868_1_.getEntity() instanceof LivingEntity
         && !VineLassoUtil.hasLassoData((LivingEntity)p_213868_1_.getEntity())) {
         this.remove(RemovalReason.DISCARDED);
         VineLassoUtil.lassoTo((LivingEntity)entity, (LivingEntity)p_213868_1_.getEntity());
      }
   }

   private void removeAndAddToInventory() {
      Entity entity = this.getOwner();
      ItemStack item = new ItemStack((ItemLike)AMItemRegistry.VINE_LASSO.get());
      if (!this.isRemoved() && (!(entity instanceof Player) || !((Player)entity).addItem(item))) {
         AMCompat.spawnAtLocation(this, item);
      }

      this.remove(RemovalReason.DISCARDED);
   }

   protected void onHitBlock(BlockHitResult p_230299_1_) {
      BlockState blockstate = this.level().getBlockState(p_230299_1_.getBlockPos());
      if (!this.level().isClientSide()) {
         this.removeAndAddToInventory();
      }
   }

   protected void defineSynchedData(Builder builder) {
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
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      if (AMCompat.hasUUID(compound, "Owner")) {
         this.ownerUUID = AMCompat.getUUID(compound, "Owner");
      }

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
            this.random.nextGaussian() * 0.007499999832361937 * inaccuracy,
            this.random.nextGaussian() * 0.007499999832361937 * inaccuracy,
            this.random.nextGaussian() * 0.007499999832361937 * inaccuracy
         )
         .scale(velocity);
      this.setDeltaMovement(vector3d);
      float f = Mth.sqrt((float)(vector3d.x * vector3d.x + vector3d.z * vector3d.z));
      this.setYRot((float)(Mth.atan2(vector3d.x, vector3d.z) * 57.2957763671875));
      this.setXRot((float)(Mth.atan2(vector3d.y, f) * 57.2957763671875));
      this.yRotO = this.getYRot();
      this.xRotO = this.getXRot();
   }

   public void shootFromRotation(Entity p_234612_1_, float p_234612_2_, float p_234612_3_, float p_234612_4_, float p_234612_5_, float p_234612_6_) {
      float f = -Mth.sin(p_234612_3_ * 0.017453292F) * Mth.cos(p_234612_2_ * 0.017453292F);
      float f1 = -Mth.sin((p_234612_2_ + p_234612_4_) * 0.017453292F);
      float f2 = Mth.cos(p_234612_3_ * 0.017453292F) * Mth.cos(p_234612_2_ * 0.017453292F);
      this.shoot(f, f1, f2, p_234612_5_, p_234612_6_);
      Vec3 vector3d = p_234612_1_.getDeltaMovement();
      this.setDeltaMovement(this.getDeltaMovement().add(vector3d.x, p_234612_1_.onGround() ? 0.0 : vector3d.y, vector3d.z));
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

   protected boolean canHitEntity(Entity p_230298_1_) {
      if (!p_230298_1_.isSpectator() && p_230298_1_.isAlive() && p_230298_1_.isPickable()) {
         Entity entity = this.getOwner();
         return entity == null || this.leftOwner || !entity.isPassengerOfSameVehicle(p_230298_1_);
      } else {
         return false;
      }
   }

   protected void updateRotation() {
      Vec3 vector3d = this.getDeltaMovement();
      float f = Mth.sqrt((float)(vector3d.x * vector3d.x + vector3d.z * vector3d.z));
      this.setXRot(lerpRotation(this.xRotO, (float)(Mth.atan2(vector3d.y, f) * 57.2957763671875)));
      this.setYRot(this.getYRot() + 20.0F);
   }
}
