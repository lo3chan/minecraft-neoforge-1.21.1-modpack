package at.petrak.hexcasting.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class HexBlockEntity extends BlockEntity {
   public HexBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
      super(pType, pWorldPosition, pBlockState);
   }

   protected abstract void saveModData(CompoundTag var1);

   protected abstract void loadModData(CompoundTag var1);

   protected void saveAdditional(CompoundTag pTag, Provider provider) {
      this.saveModData(pTag);
   }

   protected void loadAdditional(CompoundTag pTag, Provider provider) {
      super.loadAdditional(pTag, provider);
      this.loadModData(pTag);
   }

   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag tag = new CompoundTag();
      this.saveModData(tag);
      return tag;
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void sync() {
      this.setChanged();
      this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }
}
