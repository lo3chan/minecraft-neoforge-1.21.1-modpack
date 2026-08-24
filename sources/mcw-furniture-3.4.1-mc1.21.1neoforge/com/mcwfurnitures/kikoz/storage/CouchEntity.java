package com.mcwfurnitures.kikoz.storage;

import com.mcwfurnitures.kikoz.init.EntityInit;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class CouchEntity extends Entity {
   private BlockPos chair;

   public CouchEntity(EntityType<? extends CouchEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public CouchEntity(Level level) {
      this((EntityType<? extends CouchEntity>)EntityInit.COUCH.get(), level);
   }

   private CouchEntity(Level level, BlockPos pos, double yOffset) {
      this(level);
      this.chair = pos;
      this.setPos(this.chair.getX() + 0.5, this.chair.getY() + 0.4, this.chair.getZ() + 0.5);
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
   }

   protected boolean canRide(Entity entity) {
      return true;
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide && (this.getPassengers().isEmpty() || this.level().isEmptyBlock(this.blockPosition()))) {
         this.remove(RemovalReason.DISCARDED);
      }
   }

   public static ItemInteractionResult create(Level level, BlockPos pos, double doub, Player player) {
      if (!level.isClientSide()) {
         List<CouchEntity> seatsInThisBlock = level.getEntitiesOfClass(
            CouchEntity.class, new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0)
         );
         if (seatsInThisBlock.isEmpty()) {
            CouchEntity chair = new CouchEntity(level, pos, doub);
            level.addFreshEntity(chair);
            player.startRiding(chair);
         }
      }

      return ItemInteractionResult.SUCCESS;
   }

   protected void defineSynchedData(Builder build) {
   }
}
