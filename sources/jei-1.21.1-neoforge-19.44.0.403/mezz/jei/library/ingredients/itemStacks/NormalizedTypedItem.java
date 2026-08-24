package mezz.jei.library.ingredients.itemStacks;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

final class NormalizedTypedItem extends TypedItemStack {
   private final Holder<Item> itemHolder;

   NormalizedTypedItem(Holder<Item> itemHolder) {
      this.itemHolder = itemHolder;
   }

   @Override
   protected ItemStack createItemStackUncached() {
      return new ItemStack(this.itemHolder);
   }

   @Override
   public TypedItemStack getNormalized() {
      return this;
   }

   @Override
   protected Item getItem() {
      return (Item)this.itemHolder.value();
   }

   @Override
   public String toString() {
      return "NormalizedTypedItem{itemHolder=" + this.itemHolder + "}";
   }
}
