package zank.mods.open_in_inventory.impl.handler;

import net.minecraft.client.multiplayer.ClientLevel;
import zank.mods.open_in_inventory.OpenInInventory;

public class ClientEventHandler {
   public static void clientStarted(ClientLevel world) {
      OpenInInventory.refreshConfig();
   }
}
