package net.joefoxe.hexerei.data.coffer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.RequestCofferInventoryPacket;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class ClientCofferData {
   private static final Map<UUID, NonNullList<ItemStack>> CLIENT_CACHE = new HashMap<>();
   private static final Set<UUID> PENDING_REQUESTS = new HashSet<>();
   private static long LAST_REQUEST_TIME = 0L;

   public static void storeInventory(UUID cofferId, NonNullList<ItemStack> items) {
      CLIENT_CACHE.put(cofferId, items);
      PENDING_REQUESTS.remove(cofferId);
   }

   public static void requestInventoryIfNeeded(UUID cofferId) {
      if (!CLIENT_CACHE.containsKey(cofferId)) {
         if (!PENDING_REQUESTS.contains(cofferId)) {
            long now = System.currentTimeMillis();
            if (now - LAST_REQUEST_TIME >= 500L) {
               HexereiPacketHandler.sendToServer(new RequestCofferInventoryPacket(cofferId));
               PENDING_REQUESTS.add(cofferId);
               LAST_REQUEST_TIME = now;
            }
         }
      }
   }

   public static NonNullList<ItemStack> getInventory(UUID cofferId) {
      if (cofferId == null) {
         return NonNullList.withSize(36, ItemStack.EMPTY);
      } else {
         requestInventoryIfNeeded(cofferId);
         return CLIENT_CACHE.getOrDefault(cofferId, NonNullList.withSize(36, ItemStack.EMPTY));
      }
   }
}
