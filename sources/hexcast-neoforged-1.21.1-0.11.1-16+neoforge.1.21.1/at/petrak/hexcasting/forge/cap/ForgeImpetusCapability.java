package at.petrak.hexcasting.forge.cap;

import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public record ForgeImpetusCapability(BlockEntityAbstractImpetus impetus) implements IItemHandler {
   public int getSlots() {
      return 1;
   }

   @NotNull
   public ItemStack getStackInSlot(int slot) {
      return ItemStack.EMPTY;
   }

   @NotNull
   public ItemStack insertItem(int slot, @NotNull ItemStack originalStack, boolean simulate) {
      if (!this.isItemValid(slot, originalStack)) {
         return originalStack;
      } else {
         ItemStack stack = originalStack.copy();
         if (!simulate) {
            this.impetus.insertMedia(stack);
         } else {
            this.impetus.extractMediaFromInsertedItem(stack, false);
         }

         return stack;
      }
   }

   @NotNull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      return ItemStack.EMPTY;
   }

   public int getSlotLimit(int slot) {
      return 64;
   }

   public boolean isItemValid(int slot, @NotNull ItemStack stack) {
      return this.impetus.canPlaceItem(slot, stack);
   }
}
