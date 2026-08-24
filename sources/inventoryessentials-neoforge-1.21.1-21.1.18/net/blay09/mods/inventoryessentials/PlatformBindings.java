package net.blay09.mods.inventoryessentials;

import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.inventory.Slot;

public abstract class PlatformBindings {
   public static PlatformBindings INSTANCE;

   public abstract boolean isSameInventory(Slot var1, Slot var2);

   public boolean isSortableSlot(Slot slot) {
      return slot.getClass() == Slot.class || slot.getClass() == ShulkerBoxSlot.class;
   }
}
