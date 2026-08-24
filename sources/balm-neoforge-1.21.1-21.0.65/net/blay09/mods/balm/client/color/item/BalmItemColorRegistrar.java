package net.blay09.mods.balm.client.color.item;

import java.util.ArrayList;
import java.util.function.Supplier;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.ItemLike;

public interface BalmItemColorRegistrar {
   default void register(ItemColor color, ItemLike... items) {
      this.register(color, () -> items);
   }

   default void register(ItemColor color, Iterable<? extends ItemLike> items) {
      this.register(color, () -> {
         ArrayList<ItemLike> resolvedItems = new ArrayList<>();

         for (ItemLike itemLike : items) {
            resolvedItems.add(itemLike);
         }

         return resolvedItems.toArray(ItemLike[]::new);
      });
   }

   void register(ItemColor var1, Supplier<ItemLike[]> var2);
}
