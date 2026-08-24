package net.mehvahdjukaar.moonlight.api.block;

import java.util.stream.IntStream;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public abstract class ItemDisplayTile extends RandomizableContainerBlockEntity implements WorldlyContainer {
   private NonNullList<ItemStack> stacks;

   protected ItemDisplayTile(BlockEntityType type, BlockPos pos, BlockState state) {
      this(type, pos, state, 1);
   }

   protected ItemDisplayTile(BlockEntityType type, BlockPos pos, BlockState state, int slots) {
      super(type, pos, state);
      this.stacks = NonNullList.withSize(slots, ItemStack.EMPTY);
   }

   public void setChanged() {
      if (this.level != null && !this.level.isClientSide) {
         this.serverSideUpdateWhenChanged(this.level.registryAccess());
         if (this.needsToUpdateClientWhenChanged()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
         }

         super.setChanged();
      }
   }

   public boolean needsToUpdateClientWhenChanged() {
      return true;
   }

   @Deprecated(
      forRemoval = true
   )
   public void updateTileOnInventoryChanged() {
   }

   @Deprecated(
      forRemoval = true
   )
   public void updateClientVisualsOnLoad() {
   }

   public void clientSideUpdateWhenChanged(Provider registries) {
      this.updateClientVisualsOnLoad();
   }

   public void serverSideUpdateWhenChanged(Provider registries) {
      this.updateTileOnInventoryChanged();
   }

   public ItemStack getDisplayedItem() {
      return this.getItem(0);
   }

   public void setDisplayedItem(ItemStack stack) {
      this.setItem(0, stack);
   }

   public ItemInteractionResult interactWithPlayerItem(Player player, InteractionHand handIn, ItemStack stack) {
      return this.interactWithPlayerItem(player, handIn, stack, 0);
   }

   public ItemInteractionResult interactWithPlayerItem(Player player, InteractionHand handIn, ItemStack handItem, int slot) {
      if (handIn == InteractionHand.MAIN_HAND) {
         if (handItem.isEmpty()) {
            ItemStack it = this.removeItemNoUpdate(slot);
            if (!it.isEmpty()) {
               this.onItemRemoved(player, it, slot);
               if (!this.level.isClientSide()) {
                  Utils.addItemOrDrop(player, it);
                  this.setChanged();
               } else {
                  this.clientSideUpdateWhenChanged(this.level.registryAccess());
               }

               return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
            }
         } else if (this.canPlaceItem(slot, handItem)) {
            ItemStack it = handItem.copy();
            it.setCount(1);
            this.setItem(slot, it);
            handItem.consume(1, player);
            this.onItemAdded(player, it, slot);
            if (!this.level.isClientSide()) {
               this.level.playSound(null, this.worldPosition, this.getAddItemSound(), SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.95F);
            } else {
               this.clientSideUpdateWhenChanged(this.level.registryAccess());
            }

            return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
         }
      }

      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   public void onItemRemoved(Player player, ItemStack stack, int slot) {
      this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.worldPosition, Context.of(player, this.getBlockState()));
   }

   public void onItemAdded(Player player, ItemStack stack, int slot) {
      this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.worldPosition, Context.of(player, this.getBlockState()));
      if (player instanceof ServerPlayer serverPlayer) {
         CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, this.worldPosition, stack);
         player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
      }
   }

   public SoundEvent getAddItemSound() {
      return SoundEvents.ITEM_FRAME_ADD_ITEM;
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      if (!this.tryLoadLootTable(tag)) {
         this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      }

      ContainerHelper.loadAllItems(tag, this.stacks, registries);
      if (this.level != null) {
         if (this.level.isClientSide) {
            this.clientSideUpdateWhenChanged(registries);
         } else {
            this.serverSideUpdateWhenChanged(registries);
         }
      }
   }

   public void saveAdditional(CompoundTag compound, Provider registries) {
      super.saveAdditional(compound, registries);
      if (!this.trySaveLootTable(compound)) {
         ContainerHelper.saveAllItems(compound, this.stacks, registries);
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return this.saveWithoutMetadata(registries);
   }

   public int getContainerSize() {
      return this.stacks.size();
   }

   public int getMaxStackSize() {
      return 1;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int id, Inventory player) {
      return null;
   }

   @Internal
   @Nullable
   public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
      return super.createMenu(i, inventory, player);
   }

   protected Component getDefaultName() {
      return this.getBlockState().getBlock().getName();
   }

   protected NonNullList<ItemStack> getItems() {
      return this.stacks;
   }

   public void setItems(NonNullList<ItemStack> stacks) {
      this.stacks = stacks;
   }

   public boolean canPlaceItem(int index, ItemStack stack) {
      return this.isEmpty();
   }

   public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
      return false;
   }

   public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
      return false;
   }

   public int[] getSlotsForFace(Direction side) {
      return IntStream.range(0, this.getContainerSize()).toArray();
   }
}
