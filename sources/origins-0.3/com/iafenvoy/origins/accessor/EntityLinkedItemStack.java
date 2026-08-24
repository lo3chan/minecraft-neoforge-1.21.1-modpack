package com.iafenvoy.origins.accessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface EntityLinkedItemStack {
   Entity origins$getEntity();

   Entity origins$getEntity(boolean var1);

   void origins$setEntity(Entity var1);

   @Nullable
   static Entity getEntity(ItemStack stack) {
      return ((EntityLinkedItemStack)stack).origins$getEntity();
   }
}
