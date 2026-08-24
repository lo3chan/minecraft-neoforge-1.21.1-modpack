package jeresources.profiling;

import java.util.HashMap;
import java.util.Map;
import jeresources.json.WorldGenAdapter;
import jeresources.util.DimensionHelper;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class ProfilingTimer {
   private final CommandSource sender;
   private int totalChunks;
   private final Map<ResourceKey<Level>, ProfilingTimer.DimensionCounters> dimensionsMap = new HashMap<>();

   public ProfilingTimer(CommandSource sender, int chunkCount) {
      this.sender = sender;
      this.totalChunks = chunkCount;
   }

   public void startChunk(ResourceKey<Level> worldRegistryKey) {
      ProfilingTimer.DimensionCounters counters = this.dimensionsMap.get(worldRegistryKey);
      if (counters == null) {
         counters = new ProfilingTimer.DimensionCounters();
         this.dimensionsMap.put(worldRegistryKey, counters);
         this.send("[" + DimensionHelper.getDimensionName(worldRegistryKey) + "] Started profiling");
      }

      counters.threadCounter++;
   }

   public void endChunk(ResourceKey<Level> worldRegistryKey) {
      ProfilingTimer.DimensionCounters counters = this.dimensionsMap.get(worldRegistryKey);
      counters.threadCounter--;
      if (++counters.chunkCounter % 100 == 0) {
         this.sendSpeed(worldRegistryKey);
      }

      if (this.totalChunks == counters.chunkCounter) {
         counters.completed = true;
      }
   }

   public void complete() {
      for (ResourceKey<Level> worldRegistryKey : this.dimensionsMap.keySet()) {
         ProfilingTimer.DimensionCounters counters = this.dimensionsMap.get(worldRegistryKey);
         counters.completed = true;
         this.send(
            "["
               + DimensionHelper.getDimensionName(worldRegistryKey)
               + "] Completed profiling of "
               + this.getBlocksPerLayer(worldRegistryKey) * 256L
               + " blocks in "
               + (System.currentTimeMillis() - counters.start)
               + " ms saved to "
               + WorldGenAdapter.getWorldGenFile()
         );
      }
   }

   public synchronized boolean isCompleted() {
      for (ProfilingTimer.DimensionCounters counters : this.dimensionsMap.values()) {
         if (!counters.completed) {
            return false;
         }
      }

      return true;
   }

   private void send(String s) {
      this.sender.sendSystemMessage(Component.translatable(s));
   }

   private void sendSpeed(ResourceKey<Level> worldRegistryKey) {
      ProfilingTimer.DimensionCounters counters = this.dimensionsMap.get(worldRegistryKey);
      float time = (float)(System.currentTimeMillis() - counters.start) * 1.0F / counters.chunkCounter;
      String message = "["
         + DimensionHelper.getDimensionName(worldRegistryKey)
         + "] Scanned "
         + counters.chunkCounter
         + " chunks at "
         + String.format("%3.2f", time)
         + " ms/chunk";
      this.send(message);
   }

   public long getBlocksPerLayer(ResourceKey<Level> worldRegistryKey) {
      ProfilingTimer.DimensionCounters counters = this.dimensionsMap.get(worldRegistryKey);
      return counters.chunkCounter * 16 * 16;
   }

   private static class DimensionCounters {
      public final long start = System.currentTimeMillis();
      public int chunkCounter;
      public int threadCounter;
      public boolean completed;
   }
}
