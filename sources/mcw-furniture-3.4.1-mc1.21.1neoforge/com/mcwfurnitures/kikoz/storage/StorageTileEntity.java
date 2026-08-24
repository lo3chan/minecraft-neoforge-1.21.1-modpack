package com.mcwfurnitures.kikoz.storage;

import com.mcwfurnitures.kikoz.init.BlockEntityInit;
import com.mcwfurnitures.kikoz.init.SoundsInit;
import com.mcwfurnitures.kikoz.objects.TallFurniture;
import com.mcwfurnitures.kikoz.objects.bookshelves.BookCabinet;
import com.mcwfurnitures.kikoz.objects.bookshelves.BookCabinetHinge;
import com.mcwfurnitures.kikoz.objects.cabinets.Cabinet;
import com.mcwfurnitures.kikoz.objects.cabinets.CabinetHinge;
import com.mcwfurnitures.kikoz.objects.counters.CupboardCounter;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class StorageTileEntity extends RandomizableContainerBlockEntity {
   private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
   private final IItemHandler itemHandler = new InvWrapper(this);
   private ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
      protected void onOpen(Level level, BlockPos pos, BlockState state) {
         if (!(state.getBlock() instanceof TallFurniture)
            && !(state.getBlock() instanceof BookCabinet)
            && !(state.getBlock() instanceof BookCabinetHinge)
            && !(state.getBlock() instanceof Cabinet)
            && !(state.getBlock() instanceof CabinetHinge)
            && !(state.getBlock() instanceof CupboardCounter)) {
            this.playSound(null, level, pos, true, (SoundEvent)SoundsInit.DRAWER_OPEN.get(), 0.5F);
         } else {
            this.playSound(null, level, pos, true, (SoundEvent)SoundsInit.CABINET_OPEN.get(), 0.5F);
         }
      }

      protected void onClose(Level level, BlockPos pos, BlockState state) {
         if (!(state.getBlock() instanceof TallFurniture)
            && !(state.getBlock() instanceof BookCabinet)
            && !(state.getBlock() instanceof BookCabinetHinge)
            && !(state.getBlock() instanceof Cabinet)
            && !(state.getBlock() instanceof CabinetHinge)
            && !(state.getBlock() instanceof CupboardCounter)) {
            this.playSound(null, level, pos, false, (SoundEvent)SoundsInit.DRAWER_CLOSE.get(), 0.5F);
         } else {
            this.playSound(null, level, pos, false, (SoundEvent)SoundsInit.CABINET_CLOSE.get(), 0.5F);
         }
      }

      private void playSound(@Nullable Entity entity, Level level, BlockPos pos, boolean open, SoundEvent sound, float volume) {
         level.playSound(entity, pos, sound, SoundSource.BLOCKS, volume, level.random.nextFloat() * 0.1F + 0.9F);
      }

      protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int num, int num2) {
      }

      protected boolean isOwnContainer(Player player) {
         if (player.containerMenu instanceof ChestMenu) {
            Container container = ((ChestMenu)player.containerMenu).getContainer();
            return container == StorageTileEntity.this;
         } else {
            return false;
         }
      }
   };
   private final IItemHandler[] sidedHandlers = new IItemHandler[]{new InvWrapper(this)};

   public IItemHandler getItemHandler() {
      return this.itemHandler;
   }

   public StorageTileEntity(BlockPos pos, BlockState state) {
      super(BlockEntityInit.FURNITURE_STORAGE.get(), pos, state);
   }

   protected void loadAdditional(CompoundTag pTag, Provider pRegistries) {
      super.loadAdditional(pTag, pRegistries);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(pTag)) {
         ContainerHelper.loadAllItems(pTag, this.items, pRegistries);
      }
   }

   protected void saveAdditional(CompoundTag pTag, Provider pRegistries) {
      super.saveAdditional(pTag, pRegistries);
      if (!this.trySaveLootTable(pTag)) {
         ContainerHelper.saveAllItems(pTag, this.items, pRegistries);
      }
   }

   public int getContainerSize() {
      return 27;
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> stack) {
      this.items = stack;
   }

   protected Component getDefaultName() {
      return Component.translatable("mcwfurnitures.container.threerows");
   }

   protected AbstractContainerMenu createMenu(int ints, Inventory inventory) {
      return ChestMenu.threeRows(ints, inventory, this);
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

   void updateBlockState(BlockState state, boolean bool) {
      this.level.setBlock(this.getBlockPos(), (BlockState)state.setValue(BarrelBlock.OPEN, bool), 3);
   }

   void playSound(BlockState state, SoundEvent event) {
      Vec3i vec3i = ((Direction)state.getValue(BarrelBlock.FACING)).getNormal();
      double d0 = this.worldPosition.getX() + 0.5 + vec3i.getX() / 2.0;
      double d1 = this.worldPosition.getY() + 0.5 + vec3i.getY() / 2.0;
      double d2 = this.worldPosition.getZ() + 0.5 + vec3i.getZ() / 2.0;
      this.level.playSound((Player)null, d0, d1, d2, event, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
   }

   public IItemHandler getItemHandler(Direction side) {
      return this.sidedHandlers[side == null ? 0 : side.ordinal() % this.sidedHandlers.length];
   }

   private static IItemHandler getItemHandler(StorageTileEntity entity, Direction side) {
      return entity.itemHandler;
   }
}
