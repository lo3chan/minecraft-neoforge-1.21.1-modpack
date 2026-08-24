package net.bobophones.bobolib.util;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Pre;

public class DelayedAction {
   private int ticks;
   private final Runnable action;

   public DelayedAction(int ticks, Runnable action) {
      this.action = action;
      this.ticks = ticks;
      NeoForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent
   public void onServerTick(Pre event) {
      if (this.ticks-- <= 0) {
         this.action.run();
         NeoForge.EVENT_BUS.unregister(this);
      }
   }
}
