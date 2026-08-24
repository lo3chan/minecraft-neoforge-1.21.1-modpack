package net.mehvahdjukaar.moonlight.api.block;

import java.util.stream.IntStream;
import net.mehvahdjukaar.moonlight.api.misc.IContainerProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class OpenableContainerBlockTile extends RandomizableContainerBlockEntity implements WorldlyContainer {
   private final ContainerOpenersCounter openersCounter = new OpenableContainerBlockTile.ContainerCounter();
   protected NonNullList<ItemStack> items;

   protected OpenableContainerBlockTile(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, int size) {
      super(blockEntityType, pos, state);
      this.items = NonNullList.withSize(size, ItemStack.EMPTY);
   }

   protected Component getDefaultName() {
      return this.getBlockState().getBlock().getName();
   }

   public int getContainerSize() {
      return this.items.size();
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(tag) && tag.contains("Items", 9)) {
         ContainerHelper.loadAllItems(tag, this.items, registries);
      }
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      if (!this.trySaveLootTable(tag)) {
         ContainerHelper.saveAllItems(tag, this.items, false, registries);
      }
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> itemsIn) {
      this.items = itemsIn;
   }

   public int[] getSlotsForFace(Direction side) {
      return IntStream.range(0, this.getContainerSize()).toArray();
   }

   public void startOpen(Player player) {
      if (!this.remove && !player.isSpectator()) {
         this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
      }
   }

   public void stopOpen(Player player) {
      if (!this.remove && !player.isSpectator()) {
         this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
      }
   }

   public void recheckOpen() {
      if (!this.remove) {
         this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
      }
   }

   protected abstract void updateBlockState(BlockState var1, boolean var2);

   protected abstract void playOpenSound(BlockState var1);

   protected abstract void playCloseSound(BlockState var1);

   public boolean isUnused() {
      return this.openersCounter.getOpenerCount() == 0;
   }

   private class ContainerCounter extends ContainerOpenersCounter {
      protected void onOpen(Level level, BlockPos pos, BlockState state) {
         OpenableContainerBlockTile.this.playOpenSound(state);
         OpenableContainerBlockTile.this.updateBlockState(state, true);
      }

      protected void onClose(Level level, BlockPos pos, BlockState state) {
         OpenableContainerBlockTile.this.playCloseSound(state);
         OpenableContainerBlockTile.this.updateBlockState(state, false);
      }

      protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int i, int i1) {
      }

      protected boolean isOwnContainer(Player player) {
         if (player.containerMenu instanceof IContainerProvider chestMenu) {
            Container container = chestMenu.getContainer();
            return container == OpenableContainerBlockTile.this;
         } else {
            return false;
         }
      }
   }
}
