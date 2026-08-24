package dev.latvian.mods.kubejs.item;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemStackKey {
   public static ItemStackKey EMPTY = new ItemStackKey(Items.AIR, null);
   public final Item item;
   public final DataComponentPatch patch;
   private int hashCode = 0;

   public static ItemStackKey of(ItemStack stack) {
      if (stack.isEmpty()) {
         return EMPTY;
      } else {
         return stack.getComponents().isEmpty() ? stack.getItem().kjs$getTypeItemStackKey() : new ItemStackKey(stack.getItem(), stack.getComponentsPatch());
      }
   }

   public ItemStackKey(Item item, DataComponentPatch patch) {
      this.item = item;
      this.patch = patch;
   }

   @Override
   public int hashCode() {
      if (this.hashCode == 0) {
         this.hashCode = this.item == Items.AIR ? 0 : this.item.hashCode() * 31 + this.patch.hashCode();
         if (this.hashCode == 0) {
            this.hashCode = 1;
         }
      }

      return this.hashCode;
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof ItemStackKey k) ? false : this.item == k.item && this.hashCode() == k.hashCode() && this.patch.equals(k.patch);
   }
}
