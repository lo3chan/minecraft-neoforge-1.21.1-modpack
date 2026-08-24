package com.anthonyhilyard.prism.events.client;

import com.anthonyhilyard.prism.events.Event;
import com.anthonyhilyard.prism.events.EventFactory;
import net.minecraft.client.DeltaTracker;

public class RenderTickEvent {
   public static final Event<RenderTickEvent.Start> START = EventFactory.create(RenderTickEvent.Start.class, callbacks -> tracker -> {
      for (RenderTickEvent.Start callback : callbacks) {
         callback.onStart(tracker);
      }
   });

   @FunctionalInterface
   public interface Start {
      void onStart(DeltaTracker var1);
   }
}
