package net.cibernet.alchemancy.blocks.blockentities;

import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public class ItemStackHolderBlockEntity extends BaseContainerBlockEntity {
   NonNullList<ItemStack> slot = NonNullList.withSize(1, ItemStack.EMPTY);
   public final InvWrapper wrapper = new InvWrapper(this);

   public ItemStackHolderBlockEntity(BlockPos pos, BlockState blockState) {
      super((BlockEntityType)AlchemancyBlockEntities.ITEMSTACK_HOLDER.get(), pos, blockState);
   }

   public ItemStackHolderBlockEntity(BlockEntityType<? extends ItemStackHolderBlockEntity> blockEntityType, BlockPos pos, BlockState state) {
      super(blockEntityType, pos, state);
   }

   protected Component getDefaultName() {
      return null;
   }

   protected NonNullList<ItemStack> getItems() {
      return this.slot;
   }

   protected void setItems(NonNullList<ItemStack> items) {
      this.slot = items;
   }

   protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
      return null;
   }

   public int getContainerSize() {
      return 1;
   }

   public ItemStack getItem() {
      return super.getItem(0);
   }

   public ItemStack removeItem(int amount) {
      return this.removeItem(0, amount);
   }

   public void setItem(int slot, ItemStack stack) {
      super.setItem(slot, stack);
      this.notifyInventoryUpdate();
   }

   public ItemStack removeItem(int slot, int amount) {
      ItemStack result = super.removeItem(slot, amount);
      this.notifyInventoryUpdate();
      return result;
   }

   public void clearContent() {
      super.clearContent();
      this.notifyInventoryUpdate();
   }

   public void setItem(ItemStack stack) {
      this.setItem(0, stack);
   }

   public void notifyInventoryUpdate() {
      if (this.level != null) {
         this.level.markAndNotifyBlock(this.getBlockPos(), this.level.getChunkAt(this.getBlockPos()), this.getBlockState(), this.getBlockState(), 2, 1);
      }
   }

   @Nullable
   public static ItemEntity dropItem(Level pLevel, BlockPos pPos, ItemStack itemstack) {
      if (!pLevel.isClientSide && !itemstack.isEmpty()) {
         ItemStack itemstack1 = itemstack.copy();
         ItemEntity itementity = new ItemEntity(pLevel, pPos.getX() + 0.5, pPos.getY() + 1, pPos.getZ() + 0.5, itemstack1);
         itementity.setDeltaMovement(0.0, 0.15, 0.0);
         itementity.setDefaultPickUpDelay();
         itementity.getPersistentData().putBoolean("alchemancy:from_pedestal", true);
         pLevel.addFreshEntity(itementity);
         return itementity;
      } else {
         return null;
      }
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      ContainerHelper.saveAllItems(tag, this.slot, registries);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.slot.clear();
      ContainerHelper.loadAllItems(tag, this.slot, registries);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = new CompoundTag();
      this.saveAdditional(tag, registries);
      return tag;
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
