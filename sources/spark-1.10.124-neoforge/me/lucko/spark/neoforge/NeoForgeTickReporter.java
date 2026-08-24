package me.lucko.spark.neoforge;

import me.lucko.spark.common.tick.SimpleTickReporter;
import me.lucko.spark.common.tick.TickReporter;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.neoforge.common.NeoForge;

public abstract class NeoForgeTickReporter extends SimpleTickReporter implements TickReporter {
   @Override
   public void start() {
      NeoForge.EVENT_BUS.register(this);
   }

   @Override
   public void close() {
      NeoForge.EVENT_BUS.unregister(this);
      super.close();
   }

   public static final class Client extends NeoForgeTickReporter {
      @SubscribeEvent
      public void onTickStart(Pre e) {
         this.onStart();
      }

      @SubscribeEvent
      public void onTickEnd(Post e) {
         this.onEnd();
      }
   }

   public static final class Server extends NeoForgeTickReporter {
      @SubscribeEvent
      public void onTickStart(net.neoforged.neoforge.event.tick.ServerTickEvent.Pre e) {
         this.onStart();
      }

      @SubscribeEvent
      public void onTickEnd(net.neoforged.neoforge.event.tick.ServerTickEvent.Post e) {
         this.onEnd();
      }
   }
}
