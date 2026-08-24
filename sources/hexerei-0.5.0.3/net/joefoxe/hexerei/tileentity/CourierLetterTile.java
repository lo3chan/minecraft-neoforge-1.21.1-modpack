package net.joefoxe.hexerei.tileentity;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class CourierLetterTile extends BlockEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private OwlEntity.MessageText text = new OwlEntity.MessageText();
   private boolean sealed = false;

   public CourierLetterTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
      super(tileEntityTypeIn, blockPos, blockState);
   }

   public CourierLetterTile(BlockPos blockPos, BlockState blockState) {
      this((BlockEntityType<?>)ModTileEntities.COURIER_LETTER_TILE.get(), blockPos, blockState);
   }

   private OwlEntity.MessageText loadLines(OwlEntity.MessageText pText) {
      for (int i = 0; i < 12; i++) {
         Component component = pText.getMessage(i);
         pText = pText.setMessage(i, component);
      }

      return pText;
   }

   public boolean interact() {
      return false;
   }

   public BlockEntityType<?> getType() {
      return super.getType();
   }

   public void requestModelDataUpdate() {
      super.requestModelDataUpdate();
   }

   @NotNull
   public ModelData getModelData() {
      return super.getModelData();
   }

   public void onLoad() {
      super.onLoad();
   }

   public CompoundTag save(CompoundTag pTag, Provider registries) {
      this.saveAdditional(pTag, registries);
      return pTag;
   }

   public CompoundTag saveData(CompoundTag pTag, Provider registries) {
      return this.save(pTag, registries);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.loadFromTag(tag);
   }

   protected void saveAdditional(CompoundTag pTag, Provider registries) {
      super.saveAdditional(pTag, registries);
      if (this.sealed) {
         pTag.putBoolean("Sealed", this.sealed);
      }

      OwlEntity.MessageText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, this.text).resultOrPartial(LOGGER::error).ifPresent(tag -> pTag.put("Message", tag));
   }

   public void loadFromTag(CompoundTag pTag) {
      if (pTag.contains("Message")) {
         OwlEntity.MessageText.DIRECT_CODEC
            .parse(NbtOps.INSTANCE, pTag.getCompound("Message"))
            .resultOrPartial(LOGGER::error)
            .ifPresent(message -> this.text = this.loadLines(message));
      }

      if (pTag.contains("Sealed")) {
         this.sealed = pTag.getBoolean("Sealed");
      }
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return this.save(new CompoundTag(), registries);
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this, (tag, registryAccess) -> this.getUpdateTag(registryAccess));
   }

   public void sync() {
      this.setChanged();
      if (this.level != null) {
         if (!this.level.isClientSide) {
            CompoundTag tag = new CompoundTag();
            this.saveAdditional(tag, this.level.registryAccess());
            HexereiPacketHandler.sendToNearbyClient(this.level, this.worldPosition, new TESyncPacket(this.worldPosition, tag));
         }

         if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition), 2);
         }
      }
   }

   public void tick() {
   }
}
