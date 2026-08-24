package net.mehvahdjukaar.amendments.mixins;

import net.mehvahdjukaar.amendments.common.IBellConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({BellBlockEntity.class})
public abstract class BellTileEntityMixin extends BlockEntity implements IBellConnection {
   @Unique
   public IBellConnection.Type amendments$connection = IBellConnection.Type.NONE;

   protected BellTileEntityMixin(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
      super(pType, pWorldPosition, pBlockState);
   }

   @Override
   public IBellConnection.Type amendments$getConnection() {
      return this.amendments$connection;
   }

   @Override
   public void amendments$setConnected(IBellConnection.Type con) {
      this.amendments$connection = con;
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      if (this.amendments$connection != null) {
         tag.putInt("Connection", this.amendments$connection.ordinal());
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      if (tag.contains("Connection")) {
         this.amendments$connection = IBellConnection.Type.values()[tag.getInt("Connection")];
      } else {
         this.amendments$connection = IBellConnection.Type.NONE;
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return this.saveWithoutMetadata(registries);
   }

   public AABB getRenderBoundingBox() {
      return new AABB(this.worldPosition);
   }
}
