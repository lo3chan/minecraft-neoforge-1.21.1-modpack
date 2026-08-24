package net.blay09.mods.inventoryessentials;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.minecraft.world.inventory.Slot;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.items.SlotItemHandler;

@Mod("inventoryessentials")
public class NeoForgeInventoryEssentials {
   public NeoForgeInventoryEssentials(IEventBus modEventBus) {
      PlatformBindings.INSTANCE = new PlatformBindings() {
         @Override
         public boolean isSameInventory(Slot targetSlot, Slot slot) {
            return targetSlot instanceof SlotItemHandler && slot instanceof SlotItemHandler
               ? ((SlotItemHandler)targetSlot).getItemHandler() == ((SlotItemHandler)slot).getItemHandler()
               : slot.isSameInventory(targetSlot);
         }

         @Override
         public boolean isSortableSlot(Slot slot) {
            return super.isSortableSlot(slot) || slot instanceof SlotItemHandler;
         }
      };
      NeoForgeLoadContext context = new NeoForgeLoadContext(modEventBus);
      Balm.initialize("inventoryessentials", context, InventoryEssentials::initialize);
   }
}
