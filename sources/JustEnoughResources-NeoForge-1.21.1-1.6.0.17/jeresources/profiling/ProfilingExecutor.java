package jeresources.profiling;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;

public class ProfilingExecutor {
   private final ExecutorService executor;
   private final Profiler profiler;

   public ProfilingExecutor(Profiler profiler) {
      this.profiler = profiler;
      int processors = Runtime.getRuntime().availableProcessors();
      this.executor = Executors.newFixedThreadPool(processors * 2);
   }

   public void addChunkProfiler(ServerLevel level, List<ChunkAccess> chunks) {
      ResourceKey<Level> dimensionKey = level.dimension();
      ProfiledDimensionData dimensionData = this.profiler.getAllDimensionData().get(dimensionKey);
      this.execute(new ChunkProfiler(level, dimensionKey, chunks, dimensionData, this.profiler.getTimer(), this.profiler.getBlacklist()));
   }

   public void execute(Runnable runnable) {
      try {
         this.executor.execute(runnable);
      } catch (RejectedExecutionException var3) {
      }
   }

   public void shutdown() {
      this.executor.shutdown();
   }

   public void shutdownNow() {
      this.executor.shutdownNow();
   }

   public void awaitTermination() {
      while (true) {
         try {
            if (this.executor.awaitTermination(10L, TimeUnit.SECONDS)) {
               return;
            }
         } catch (InterruptedException var2) {
         }
      }
   }
}
