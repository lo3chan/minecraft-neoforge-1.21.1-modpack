package com.iafenvoy.origins.util;

import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;
import org.jetbrains.annotations.ApiStatus.Internal;

@EventBusSubscriber
public final class Timeout {
   private static final CopyOnWriteArrayList<Timeout> TIMEOUTS = new CopyOnWriteArrayList<>();
   private final int waitTicks;
   private final int maxTimes;
   private final Runnable callback;
   private final Runnable finalize;
   public boolean shouldRemove = false;
   private int ticks = 0;
   private int currentTimes = 0;

   private Timeout(int waitTicks, int maxTimes, Runnable callback, Runnable finalize) {
      this.waitTicks = waitTicks;
      this.maxTimes = maxTimes;
      this.callback = callback;
      this.finalize = finalize;
   }

   public static void create(int waitTicks, Runnable callback) {
      create(waitTicks, 1, callback);
   }

   public static void create(int waitTicks, int maxTimes, Runnable callback) {
      create(waitTicks, maxTimes, callback, () -> {});
   }

   public static void create(int waitTicks, int maxTimes, Runnable callback, Runnable finalize) {
      if (maxTimes > 0) {
         TIMEOUTS.add(new Timeout(waitTicks, maxTimes, callback, finalize));
      }
   }

   @SubscribeEvent
   @Internal
   public static void runTimeout(Post event) {
      TIMEOUTS.forEach(t -> t.tick(event.getServer()));
      TIMEOUTS.removeIf(t -> t.shouldRemove);
   }

   public void tick(MinecraftServer server) {
      this.ticks++;
      if (this.ticks >= this.waitTicks) {
         this.ticks = this.ticks - this.waitTicks;
         server.execute(this.callback);
         this.currentTimes++;
         if (this.currentTimes >= this.maxTimes) {
            this.shouldRemove = true;
            this.finalize.run();
         }
      }
   }
}
