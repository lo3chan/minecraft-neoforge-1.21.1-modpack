package net.joefoxe.hexerei.client.renderer.entity.custom.ai;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public interface ITargetsDroppedItems {
   boolean canTargetItem(ItemStack var1);

   void onGetItem(ItemEntity var1);

   default void onFindTarget(ItemEntity e) {
   }

   default double getMaxDistToItem() {
      return 2.0;
   }

   default void peck() {
   }
}
