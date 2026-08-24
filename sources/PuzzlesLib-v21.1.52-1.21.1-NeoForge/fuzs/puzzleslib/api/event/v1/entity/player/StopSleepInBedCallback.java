package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface StopSleepInBedCallback {
   EventInvoker<StopSleepInBedCallback> EVENT = EventInvoker.lookup(StopSleepInBedCallback.class);

   void onStopSleepInBed(ServerPlayer var1, boolean var2);
}
