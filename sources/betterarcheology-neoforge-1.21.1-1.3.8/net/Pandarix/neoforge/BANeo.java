package net.Pandarix.neoforge;

import net.Pandarix.BACommon;
import net.Pandarix.villager.ModTrades;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@Mod("betterarcheology")
public final class BANeo {
   public BANeo() {
      BACommon.init();
      NeoForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent
   public void onServerAboutToStartEvent(ServerAboutToStartEvent event) {
      ModTrades.register();
   }
}
