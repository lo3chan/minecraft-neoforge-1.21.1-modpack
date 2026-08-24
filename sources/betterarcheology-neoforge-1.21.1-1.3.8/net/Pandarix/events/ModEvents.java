package net.Pandarix.events;

import dev.architectury.event.events.common.BlockEvent;
import net.Pandarix.BACommon;

public class ModEvents {
   public static void register() {
      BACommon.LOGGER.info("Registering {} for {}", "Events", "Better Archeology");
   }

   static {
      BlockEvent.BREAK.register(TunnelingEventHandler::handleTunneling);
   }
}
