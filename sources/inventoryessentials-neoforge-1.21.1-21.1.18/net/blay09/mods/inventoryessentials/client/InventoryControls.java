package net.blay09.mods.inventoryessentials.client;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface InventoryControls {
   boolean singleTransfer(AbstractContainerScreen<?> var1, Slot var2);

   boolean bulkTransferByType(AbstractContainerScreen<?> var1, Slot var2);

   boolean bulkTransferSingle(AbstractContainerScreen<?> var1, Slot var2);

   boolean bulkTransferAll(AbstractContainerScreen<?> var1, Slot var2);

   boolean restockContainer(AbstractContainerScreen<?> var1);

   boolean restockInventory(AbstractContainerScreen<?> var1);

   boolean dumpToContainer(AbstractContainerScreen<?> var1);

   void dragTransfer(AbstractContainerScreen<?> var1, Slot var2);

   boolean dropByType(AbstractContainerScreen<?> var1, Slot var2);

   boolean dropByType(AbstractContainerScreen<?> var1, ItemStack var2);

   boolean sort(AbstractContainerScreen<?> var1, Slot var2);
}
