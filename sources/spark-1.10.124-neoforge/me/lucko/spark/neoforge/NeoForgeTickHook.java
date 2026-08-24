package me.lucko.spark.neoforge;

import me.lucko.spark.common.tick.AbstractTickHook;
import me.lucko.spark.common.tick.TickHook;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.neoforge.common.NeoForge;

public abstract class NeoForgeTickHook extends AbstractTickHook implements TickHook {
   @Override
   public void start() {
      NeoForge.EVENT_BUS.register(this);
   }

   @Override
   public void close() {
      NeoForge.EVENT_BUS.unregister(this);
   }

   public static final class Client extends NeoForgeTickHook {
      @SubscribeEvent
      public void onTickStart(Pre e) {
         this.onTick();
      }
   }

   public static final class Server extends NeoForgeTickHook {
      @SubscribeEvent
      public void onTickStart(net.neoforged.neoforge.event.tick.ServerTickEvent.Pre e) {
         this.onTick();
      }
   }
}
