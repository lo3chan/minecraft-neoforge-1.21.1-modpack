package io.github.razordevs.deep_aether.entity.projectile;

import io.github.razordevs.deep_aether.init.DAItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class VenomiteBubble extends ThrowableProjectile {
   private int ticksInAir = 0;

   public VenomiteBubble(EntityType<? extends VenomiteBubble> type, Level level) {
      super(type, level);
   }

   protected void defineSynchedData(Builder builder) {
   }

   public void tick() {
      super.tick();
      if (!this.onGround()) {
         this.ticksInAir++;
      }

      if (this.ticksInAir > 300 && !this.level().isClientSide()) {
         this.discard();
      }
   }

   protected void onHit(HitResult result) {
      super.onHit(result);
      if (!this.level().isClientSide()) {
         this.level().broadcastEntityEvent(this, (byte)3);
         this.discard();
      }
   }

   protected void onHitEntity(EntityHitResult result) {
      if (!this.level().isClientSide()) {
         this.explode();
         this.level().broadcastEntityEvent(this, (byte)70);
      }
   }

   protected void onHitBlock(BlockHitResult result) {
      super.onHitBlock(result);
      if (!this.level().isClientSide) {
         this.explode();
      }
   }

   private void explode() {
      this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1.0F, ExplosionInteraction.NONE);
      this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), new ItemStack(DAItems.BIO_CRYSTAL.asItem())));
   }

   protected double getDefaultGravity() {
      return 0.07000000029802322;
   }

   public void handleEntityEvent(byte id) {
      super.handleEntityEvent(id);
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putInt("TicksInAir", this.ticksInAir);
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.contains("TicksInAir")) {
         this.ticksInAir = tag.getInt("TicksInAir");
      }
   }
}
