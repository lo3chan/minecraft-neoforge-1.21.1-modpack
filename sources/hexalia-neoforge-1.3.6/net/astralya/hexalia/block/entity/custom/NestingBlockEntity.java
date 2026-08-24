package net.astralya.hexalia.block.entity.custom;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.menu.NestingBlockMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;

public class NestingBlockEntity extends BlockEntity implements Container, MenuProvider, ExtendedMenuProvider {
   public static final int COLUMNS = 9;
   public static final int ROWS = 1;
   public static final int SIZE = 9;
   private final NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
   private float openProgress;
   private float openProgressOld;
   private int openCount;
   private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
      protected void onOpen(Level level, BlockPos pos, BlockState state) {
         level.playSound(null, pos, SoundEvents.GRASS_STEP, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
      }

      protected void onClose(Level level, BlockPos pos, BlockState state) {
         level.playSound(null, pos, SoundEvents.GRASS_STEP, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
      }

      protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int oldCount, int newCount) {
         level.blockEvent(pos, state.getBlock(), 1, newCount);
      }

      protected boolean isOwnContainer(Player player) {
         return player.containerMenu instanceof NestingBlockMenu menu && menu.getContainer() == NestingBlockEntity.this;
      }
   };

   public NestingBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntityTypes.NESTING_BLOCK.get(), pos, state);
   }

   public float getOpenProgress(float partialTick) {
      return Mth.lerp(partialTick, this.openProgressOld, this.openProgress);
   }

   public ItemStack insertAll(ItemStack stack) {
      ItemStack remaining = stack.copy();

      for (int slot = 0; slot < this.items.size(); slot++) {
         if (remaining.isEmpty()) {
            return ItemStack.EMPTY;
         }

         ItemStack current = (ItemStack)this.items.get(slot);
         if (current.isEmpty()) {
            this.items.set(slot, remaining);
            this.setChanged();
            return ItemStack.EMPTY;
         }

         if (ItemStack.isSameItemSameComponents(current, remaining)) {
            int transferable = Math.min(remaining.getCount(), current.getMaxStackSize() - current.getCount());
            if (transferable > 0) {
               current.grow(transferable);
               remaining.shrink(transferable);
               this.setChanged();
            }
         }
      }

      return remaining;
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, NestingBlockEntity be) {
      be.openersCounter.recheckOpeners(level, pos, state);
   }

   public static void clientTick(Level level, BlockPos pos, BlockState state, NestingBlockEntity be) {
      be.openProgressOld = be.openProgress;
      float target = be.openCount > 0 ? 1.0F : 0.0F;
      float speed = 0.2F;
      be.openProgress = be.openProgress + (target - be.openProgress) * speed;
      be.openProgress = Mth.clamp(be.openProgress, 0.0F, 1.0F);
   }

   public void startOpen(Player player) {
      if (this.level != null && !this.isRemoved() && !player.isSpectator()) {
         this.openersCounter.incrementOpeners(player, this.level, this.worldPosition, this.getBlockState());
      }
   }

   public void stopOpen(Player player) {
      if (this.level != null && !this.isRemoved() && !player.isSpectator()) {
         this.openersCounter.decrementOpeners(player, this.level, this.worldPosition, this.getBlockState());
      }
   }

   public boolean triggerEvent(int id, int param) {
      if (id == 1) {
         this.openCount = param;
         return true;
      } else {
         return super.triggerEvent(id, param);
      }
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public boolean isEmpty() {
      for (ItemStack stack : this.items) {
         if (!stack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(int slot) {
      return (ItemStack)this.items.get(slot);
   }

   public ItemStack removeItem(int slot, int amount) {
      ItemStack result = ContainerHelper.removeItem(this.items, slot, amount);
      if (!result.isEmpty()) {
         this.setChanged();
      }

      return result;
   }

   public ItemStack removeItemNoUpdate(int slot) {
      ItemStack result = ContainerHelper.takeItem(this.items, slot);
      if (!result.isEmpty()) {
         this.setChanged();
      }

      return result;
   }

   public void setItem(int slot, ItemStack stack) {
      this.items.set(slot, stack);
      int max = this.getMaxStackSize();
      if (stack.getCount() > max) {
         stack.setCount(max);
      }

      this.setChanged();
   }

   public boolean canPlaceItem(int slot, ItemStack stack) {
      return true;
   }

   public boolean stillValid(Player player) {
      if (this.level == null) {
         return false;
      } else if (this.level.getBlockEntity(this.worldPosition) != this) {
         return false;
      } else {
         double dx = player.getX() - (this.worldPosition.getX() + 0.5);
         double dy = player.getY() - (this.worldPosition.getY() + 0.5);
         double dz = player.getZ() - (this.worldPosition.getZ() + 0.5);
         return dx * dx + dy * dy + dz * dz <= 64.0;
      }
   }

   public void clearContent() {
      this.items.clear();
      this.setChanged();
   }

   public Component getDisplayName() {
      return Component.translatable("container.hexalia.nesting_block");
   }

   public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
      return new NestingBlockMenu(syncId, playerInventory, this);
   }

   public void saveExtraData(FriendlyByteBuf buf) {
      buf.writeBlockPos(this.worldPosition);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      ContainerHelper.loadAllItems(tag, this.items, registries);
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      ContainerHelper.saveAllItems(tag, this.items, registries);
   }
}
