package net.blay09.mods.inventoryessentials.client;

import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(
   value = "inventoryessentials",
   dist = {Dist.CLIENT}
)
public class NeoForgeInventoryEssentialsClient {
   public NeoForgeInventoryEssentialsClient(IEventBus modEventBus) {
      NeoForgeLoadContext context = new NeoForgeLoadContext(modEventBus);
      BalmClient.initialize("inventoryessentials", context, InventoryEssentialsClient::initialize);
   }
}
