package zank.mods.open_in_inventory.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.client.Minecraft;

public interface ScreenClearedEvent {
   Event<ScreenClearedEvent> EVENT = EventFactory.createLoop(new ScreenClearedEvent[0]);

   void onEvent(Minecraft client);
}
