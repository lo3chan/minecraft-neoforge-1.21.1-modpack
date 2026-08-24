package dev.architectury.registry.fuel.forge;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;

public class FuelRegistryImpl {
   private static final Object2IntMap<ItemLike> ITEMS = new Object2IntLinkedOpenHashMap();

   public static void register(int time, ItemLike... items) {
      for (ItemLike item : items) {
         ITEMS.put(item, time);
      }
   }

   public static int get(ItemStack stack) {
      return stack.getBurnTime(null);
   }

   @SubscribeEvent
   public static void event(FurnaceFuelBurnTimeEvent event) {
      if (!event.getItemStack().isEmpty()) {
         int time = ITEMS.getOrDefault(event.getItemStack().getItem(), -2147483648);
         if (time != -2147483648) {
            event.setBurnTime(time);
         }
      }
   }

   static {
      NeoForge.EVENT_BUS.register(FuelRegistryImpl.class);
   }
}
