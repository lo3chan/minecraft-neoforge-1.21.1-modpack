package net.joefoxe.hexerei.event;

import java.util.ArrayList;
import java.util.List;
import net.joefoxe.hexerei.command.ToggleLightCommand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(
   modid = "hexerei"
)
public class EventQueue {
   List<ITimedEvent> events = new ArrayList<>();
   private static EventQueue serverQueue;
   private static EventQueue clientQueue;

   public void tick(Post tickEvent) {
      if (this.events != null && !this.events.isEmpty()) {
         List<ITimedEvent> stale = new ArrayList<>();

         for (int i = 0; i < this.events.size(); i++) {
            ITimedEvent event = this.events.get(i);
            if (event.isExpired()) {
               stale.add(event);
            } else {
               event.tickEvent(tickEvent);
            }
         }

         this.events.removeAll(stale);
      }
   }

   public void tick(net.neoforged.neoforge.event.tick.ServerTickEvent.Post tickEvent) {
      if (this.events != null && !this.events.isEmpty()) {
         List<ITimedEvent> stale = new ArrayList<>();

         for (int i = 0; i < this.events.size(); i++) {
            ITimedEvent event = this.events.get(i);
            if (event.isExpired()) {
               stale.add(event);
            } else {
               event.tickEvent(tickEvent);
            }
         }

         this.events.removeAll(stale);
      }
   }

   public void addEvent(ITimedEvent event) {
      if (this.events == null) {
         this.events = new ArrayList<>();
      }

      this.events.add(event);
   }

   public static EventQueue getServerInstance() {
      if (serverQueue == null) {
         serverQueue = new EventQueue();
      }

      return serverQueue;
   }

   public static EventQueue getClientQueue() {
      if (clientQueue == null) {
         clientQueue = new EventQueue();
      }

      return clientQueue;
   }

   public void clear() {
      this.events = null;
   }

   private EventQueue() {
   }

   @SubscribeEvent
   public static void serverTick(net.neoforged.neoforge.event.tick.ServerTickEvent.Post e) {
      getServerInstance().tick(e);
   }

   @SubscribeEvent
   public static void clientTickEvent(Post e) {
      getClientQueue().tick(e);
   }

   @SubscribeEvent
   public static void commandRegister(RegisterCommandsEvent event) {
      ToggleLightCommand.register(event.getDispatcher());
   }
}
