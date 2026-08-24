package mezz.jei.library.ingredients.itemStacks;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

final class NormalizedTypedItemStack extends TypedItemStack {
   private final Holder<Item> itemHolder;
   private final DataComponentPatch dataComponentPatch;

   public NormalizedTypedItemStack(Holder<Item> itemHolder, DataComponentPatch dataComponentPatch) {
      this.itemHolder = itemHolder;
      this.dataComponentPatch = dataComponentPatch;
   }

   static TypedItemStack create(Holder<Item> itemHolder, DataComponentPatch dataComponentPatch) {
      return (TypedItemStack)(dataComponentPatch.isEmpty() ? new NormalizedTypedItem(itemHolder) : new NormalizedTypedItemStack(itemHolder, dataComponentPatch));
   }

   @Override
   protected ItemStack createItemStackUncached() {
      return new ItemStack(this.itemHolder, 1, this.dataComponentPatch);
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
      return "NormalizedTypedItemStack{itemHolder=" + this.itemHolder + ", dataComponentPatch=" + this.dataComponentPatch + "}";
   }
}
