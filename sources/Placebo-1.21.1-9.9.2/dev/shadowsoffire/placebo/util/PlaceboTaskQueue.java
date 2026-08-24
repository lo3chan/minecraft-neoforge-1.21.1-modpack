package dev.shadowsoffire.placebo.util;

import dev.shadowsoffire.placebo.Placebo;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;
import org.apache.commons.lang3.tuple.Pair;

public class PlaceboTaskQueue {
   public static void submitTask(ResourceLocation id, PlaceboTaskQueue.Task task) {
      PlaceboTaskQueue.Impl.TASKS.add(Pair.of(id, task));
   }

   public static void submitDelayedTask(ResourceLocation id, int delay, PlaceboTaskQueue.Task task) {
      PlaceboTaskQueue.Impl.TASKS.add(Pair.of(id, new PlaceboTaskQueue.DelayedTask(delay, task)));
   }

   private static class DelayedTask implements PlaceboTaskQueue.Task {
      private int delay;
      private PlaceboTaskQueue.Task task;

      private DelayedTask(int delay, PlaceboTaskQueue.Task task) {
         this.delay = delay;
         this.task = task;
      }

      @Override
      public PlaceboTaskQueue.Status execute() {
         return this.delay-- > 0 ? PlaceboTaskQueue.Status.RUNNING : this.task.execute();
      }
   }

   @EventBusSubscriber(
      modid = "placebo"
   )
   public static class Impl {
      private static final Queue<Pair<ResourceLocation, PlaceboTaskQueue.Task>> TASKS = new ArrayDeque<>();

      @SubscribeEvent
      public static void tick(Post e) {
         Iterator<Pair<ResourceLocation, PlaceboTaskQueue.Task>> it = TASKS.iterator();
         Pair<ResourceLocation, PlaceboTaskQueue.Task> current = null;

         while (it.hasNext()) {
            current = it.next();

            try {
               if (((PlaceboTaskQueue.Task)current.getRight()).execute().isCompleted()) {
                  it.remove();
               }
            } catch (Exception var4) {
               Placebo.LOGGER.error("An exception occurred while running a ticking task with ID {}. It will be terminated.", current.getLeft());
               it.remove();
               var4.printStackTrace();
            }
         }
      }

      @SubscribeEvent
      public static void stopped(ServerStoppedEvent e) {
         TASKS.clear();
      }

      @SubscribeEvent
      public static void started(ServerStartedEvent e) {
         TASKS.clear();
      }
   }

   public static enum Status {
      RUNNING,
      COMPLETED;

      public boolean isCompleted() {
         return this == COMPLETED;
      }
   }

   @FunctionalInterface
   public interface Task {
      PlaceboTaskQueue.Status execute();
   }
}
