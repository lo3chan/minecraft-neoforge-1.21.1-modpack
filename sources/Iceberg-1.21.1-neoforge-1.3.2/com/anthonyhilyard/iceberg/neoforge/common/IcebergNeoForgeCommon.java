package com.anthonyhilyard.iceberg.neoforge.common;

import com.anthonyhilyard.iceberg.events.common.LevelEvents;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelEvent.Load;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;

public class IcebergNeoForgeCommon {
   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void levelLoadEvent(Load event) {
      LevelEvents.LOAD.invoker().onLoad(event.getLevel());
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void levelUnloadEvent(Unload event) {
      LevelEvents.UNLOAD.invoker().onUnload(event.getLevel());
   }
}
