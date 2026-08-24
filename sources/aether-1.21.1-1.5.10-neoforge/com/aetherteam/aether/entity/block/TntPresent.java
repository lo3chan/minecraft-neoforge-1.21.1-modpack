package com.aetherteam.aether.entity.block;

import com.aetherteam.aether.entity.AetherEntityTypes;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.Entity.MovementEmission;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;

public class TntPresent extends Entity implements TraceableEntity {
   private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(TntPresent.class, EntityDataSerializers.INT);
   @Nullable
   private LivingEntity owner;

   public TntPresent(EntityType<? extends TntPresent> type, Level level) {
      super(type, level);
      this.blocksBuilding = true;
   }

   public TntPresent(Level level, double x, double y, double z, @Nullable LivingEntity owner) {
      this((EntityType<? extends TntPresent>)AetherEntityTypes.TNT_PRESENT.get(), level);
      this.setPos(x, y, z);
      double d0 = level.getRandom().nextDouble() * 6.2831854820251465;
      this.setDeltaMovement(-Math.sin(d0) * 0.02, 0.2, -Math.cos(d0) * 0.02);
      this.setFuse(10);
      this.xo = x;
      this.yo = y;
      this.zo = z;
      this.owner = owner;
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_FUSE_ID, 80);
   }

   public void tick() {
      this.applyGravity();
      this.move(MoverType.SELF, this.getDeltaMovement());
      this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
      if (this.onGround()) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
      }

      int i = this.getFuse() - 1;
      this.setFuse(i);
      if (i <= 0) {
         this.discard();
         if (!this.level().isClientSide()) {
            this.level()
               .explode(
                  this,
                  Explosion.getDefaultDamageSource(this.level(), this),
                  null,
                  this.getX(),
                  this.getY(0.0625),
                  this.getZ(),
                  1.0F,
                  false,
                  ExplosionInteraction.TNT
               );
         }
      } else {
         this.updateInWaterStateAndDoFluidPushing();
         if (this.level().isClientSide()) {
            this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
         }
      }
   }

   public int getFuse() {
      return (Integer)this.getEntityData().get(DATA_FUSE_ID);
   }

   public void setFuse(int fuse) {
      this.getEntityData().set(DATA_FUSE_ID, fuse);
   }

   @Nullable
   public Entity getOwner() {
      return this.owner;
   }

   protected MovementEmission getMovementEmission() {
      return MovementEmission.NONE;
   }

   public boolean isPickable() {
      return !this.isRemoved();
   }

   protected double getDefaultGravity() {
      return 0.04;
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putShort("Fuse", (short)this.getFuse());
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      if (tag.contains("Fuse")) {
         this.setFuse(tag.getShort("Fuse"));
      }
   }
}
