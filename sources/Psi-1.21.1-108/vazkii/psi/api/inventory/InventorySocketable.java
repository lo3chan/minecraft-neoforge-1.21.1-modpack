package vazkii.psi.api.inventory;

import com.google.common.collect.Iterators;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.common.block.tile.TileCADAssembler;

public class InventorySocketable implements Container, Nameable, ContainerData {
   private final TileCADAssembler assembler;
   @Nullable
   private ISocketable socketable;

   public InventorySocketable(TileCADAssembler assembler, ItemStack stack) {
      this.assembler = assembler;
      if (stack.isEmpty()) {
         this.socketable = null;
      } else {
         this.socketable = ISocketable.socketable(stack);
      }
   }

   public void setStack(ItemStack stack) {
      if (stack.isEmpty()) {
         this.socketable = null;
      } else {
         this.socketable = ISocketable.socketable(stack);
      }
   }

   private Iterator<ItemStack> getSockerator() {
      return (Iterator<ItemStack>)(this.socketable == null ? Collections.emptyIterator() : new IteratorSocketable(this.socketable));
   }

   public int getContainerSize() {
      Iterator<ItemStack> sockerator = this.getSockerator();
      return Iterators.size(sockerator);
   }

   public boolean isEmpty() {
      Iterator<ItemStack> sockerator = this.getSockerator();

      while (sockerator.hasNext()) {
         if (!sockerator.next().isEmpty()) {
            return false;
         }
      }

      return true;
   }

   @NotNull
   public ItemStack getItem(int index) {
      return this.socketable == null ? ItemStack.EMPTY : this.socketable.getBulletInSocket(index);
   }

   @NotNull
   public ItemStack removeItem(int index, int count) {
      if (this.socketable == null) {
         return ItemStack.EMPTY;
      } else {
         ItemStack bullet = this.socketable.getBulletInSocket(index);
         if (!bullet.isEmpty()) {
            this.socketable.setBulletInSocket(index, ItemStack.EMPTY);
            this.assembler.setChanged();
         }

         return bullet;
      }
   }

   @NotNull
   public ItemStack removeItemNoUpdate(int index) {
      return this.removeItem(index, 1);
   }

   public void setItem(int index, @NotNull ItemStack bullet) {
      if (this.socketable != null) {
         this.socketable.setBulletInSocket(index, bullet);
         this.assembler.setChanged();
      }
   }

   public int getMaxStackSize() {
      return 1;
   }

   public void setChanged() {
   }

   public boolean stillValid(@NotNull Player player) {
      return true;
   }

   public void startOpen(@NotNull Player player) {
   }

   public void stopOpen(@NotNull Player player) {
   }

   public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
      return this.socketable != null && this.socketable.isItemValid(index, stack);
   }

   public int get(int id) {
      return 0;
   }

   public void set(int id, int value) {
   }

   public int getCount() {
      return 0;
   }

   public void clearContent() {
      Iterator<ItemStack> sockerator = this.getSockerator();

      while (sockerator.hasNext()) {
         sockerator.next();
         sockerator.remove();
      }
   }

   @NotNull
   public Component getName() {
      return Component.translatable("psi.container.socketable");
   }
}
