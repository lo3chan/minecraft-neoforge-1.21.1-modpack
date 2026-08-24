package mezz.jei.library.ingredients.itemStacks;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

final class FullTypedItemStack extends TypedItemStack {
   private final Holder<Item> itemHolder;
   private final DataComponentPatch dataComponentPatch;
   private final int count;

   public FullTypedItemStack(Holder<Item> itemHolder, DataComponentPatch dataComponentPatch, int count) {
      this.itemHolder = itemHolder;
      this.dataComponentPatch = dataComponentPatch;
      this.count = count;
   }

   @Override
   protected ItemStack createItemStackUncached() {
      return new ItemStack(this.itemHolder, this.count, this.dataComponentPatch);
   }

   @Override
   protected TypedItemStack getNormalized() {
      return NormalizedTypedItemStack.create(this.itemHolder, this.dataComponentPatch);
   }

   @Override
   protected Item getItem() {
      return (Item)this.itemHolder.value();
   }

   @Override
   public String toString() {
      return "TypedItemStack{itemHolder=" + this.itemHolder + ", dataComponentPatch=" + this.dataComponentPatch + ", count=" + this.count + "}";
   }
}
