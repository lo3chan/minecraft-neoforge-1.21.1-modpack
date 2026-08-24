package net.joefoxe.hexerei.items;

import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class JarHandler extends ItemStackHandler {
   public final int stacklimit;

   public JarHandler(int size, int stacklimit) {
      super(size);
      this.stacklimit = stacklimit;
   }

   public boolean isEmpty() {
      return IntStream.range(0, this.getSlots()).allMatch(i -> this.getStackInSlot(i).isEmpty());
   }

   public boolean noValidSlots() {
      return IntStream.range(0, this.getSlots()).mapToObj(this::getStackInSlot).allMatch(ItemStack::isEmpty);
   }

   public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
      this.stacks.set(slot, stack);
   }

   public int getSlotLimit(int slot) {
      return this.stacklimit;
   }

   public int getStackLimit(int slot, @Nonnull ItemStack stack) {
      return this.stacklimit;
   }

   public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
      return true;
   }

   @Nonnull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      if (amount == 0) {
         return ItemStack.EMPTY;
      } else {
         this.validateSlotIndex(slot);
         ItemStack existing = (ItemStack)this.stacks.get(slot);
         if (existing.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            int toExtract = Math.min(amount, this.stacklimit);
            if (existing.getCount() <= toExtract) {
               if (!simulate) {
                  this.stacks.set(slot, ItemStack.EMPTY);
                  this.onContentsChanged(slot);
               }

               return existing;
            } else {
               if (!simulate) {
                  this.stacks.set(slot, existing.copyWithCount(existing.getCount() - toExtract));
                  this.onContentsChanged(slot);
               }

               return existing.copyWithCount(toExtract);
            }
         }
      }
   }

   public NonNullList<ItemStack> getContents() {
      return this.stacks;
   }

   public CompoundTag serializeNBT(Provider provider) {
      ListTag nbtTagList = new ListTag();

      for (int i = 0; i < this.getContents().size(); i++) {
         if (!((ItemStack)this.getContents().get(i)).isEmpty()) {
            int realCount = Math.min(this.stacklimit, ((ItemStack)this.getContents().get(i)).getCount());
            ItemStack stack = ((ItemStack)this.getContents().get(i)).copyWithCount(1);
            CompoundTag itemTag = new CompoundTag();
            itemTag.putInt("Slot", i);
            itemTag = (CompoundTag)stack.save(provider, itemTag);
            itemTag.putInt("ExtendedCount", realCount);
            nbtTagList.add(itemTag);
         }
      }

      CompoundTag nbt = new CompoundTag();
      nbt.put("Items", nbtTagList);
      nbt.putInt("Size", this.getContents().size());
      return nbt;
   }

   public void deserializeNBT(Provider provider, CompoundTag nbt) {
      this.setSize(nbt.contains("Size", 3) ? nbt.getInt("Size") : this.getContents().size());
      ListTag tagList = nbt.getList("Items", 10);

      for (int i = 0; i < tagList.size(); i++) {
         CompoundTag itemTags = tagList.getCompound(i);
         int slot = itemTags.getInt("Slot");
         if (slot >= 0 && slot < this.stacks.size()) {
            ItemStack stack = ItemStack.parseOptional(provider, itemTags);
            if (itemTags.contains("ExtendedCount", 3)) {
               stack.setCount(itemTags.getInt("ExtendedCount"));
            }

            this.stacks.set(slot, stack);
         }
      }

      this.onLoad();
   }
}
