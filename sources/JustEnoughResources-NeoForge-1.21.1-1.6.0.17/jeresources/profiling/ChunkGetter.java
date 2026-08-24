package jeresources.profiling;

import java.util.LinkedList;
import java.util.List;
import jeresources.util.LogHelper;
import net.minecraft.ReportedException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class ChunkGetter implements Runnable {
   public static final int CHUNKS_PER_RUN = 25;
   private final int maxRunCount;
   private final Runnable runnable;
   private ChunkGetter.IChunkGetterStrategy strategy;
   private int runCount;

   public ChunkGetter(int chunkCount, ServerLevel level, ProfilingExecutor executor) {
      this.maxRunCount = (int)Math.ceil(chunkCount / 25.0F);
      this.strategy = new ChunkGetter.ChunkGetterRandom(level);
      this.runnable = new Runnable() {
         @Override
         public void run() {
            try {
               if (ChunkGetter.this.getRunCount() < ChunkGetter.this.getMaxRunCount()) {
                  List<ChunkAccess> chunks = ChunkGetter.this.strategy.generateChunks(level);
                  if (ChunkGetter.this.strategy instanceof ChunkGetter.ChunkGetterRandom && ChunkGetter.this.areAllChunksEmpty(chunks)) {
                     ChunkGetter.this.strategy = new ChunkGetter.ChunkGetterOrigin(level, chunkCount);
                     chunks = ChunkGetter.this.strategy.generateChunks(level);
                  }

                  ChunkGetter.this.runCount++;
                  executor.addChunkProfiler(level, chunks);
                  executor.execute(() -> level.getServer().addTickable(ChunkGetter.this.runnable));
               } else {
                  executor.shutdown();
               }
            } catch (ReportedException var2) {
               LogHelper.info("Chunk getting failed: " + var2.getMessage());
               executor.shutdown();
            }
         }
      };
   }

   @Override
   public void run() {
      this.runnable.run();
   }

   private int getMaxRunCount() {
      return this.maxRunCount;
   }

   private int getRunCount() {
      return this.runCount;
   }

   private boolean areAllChunksEmpty(List<ChunkAccess> chunks) {
      for (ChunkAccess chunk : chunks) {
         if (chunk.getHighestSectionPosition() != 0) {
            return false;
         }
      }

      return true;
   }

   private static List<ChunkAccess> centerChunks(ServerLevel level, ChunkGenerator chunkGenerator, int chunkX, int chunkZ, int generate_size) {
      List<ChunkAccess> centerChunks = new LinkedList<>();

      for (int i = 0; i < generate_size; i++) {
         for (int j = 0; j < generate_size; j++) {
            if (i > 0 && i < generate_size - 1 && j > 0 && j < generate_size - 1) {
               centerChunks.add(new EmptyChunkJER(level, chunkX + i, chunkZ + j));
            }
         }
      }

      return centerChunks;
   }

   private static class ChunkGetterOrigin implements ChunkGetter.IChunkGetterStrategy {
      private static final int GENERATE_SIZE = (int)Math.ceil(Math.sqrt(25.0)) + 2;
      private final ChunkGenerator chunkGenerator;
      private final int sideLength;
      private final int minX;
      private final int maxX;
      private int posX;
      private int posZ;

      public ChunkGetterOrigin(ServerLevel level, int chunkCount) {
         this.chunkGenerator = level.getChunkSource().getGenerator();
         this.sideLength = (int)Math.ceil(Math.sqrt(chunkCount));
         WorldBorder worldBorder = level.getWorldBorder();
         this.minX = (int)worldBorder.getCenterX() - this.sideLength / 2;
         this.maxX = (int)worldBorder.getCenterX() + this.sideLength / 2;
         this.posX = this.minX;
         this.posZ = (int)worldBorder.getCenterZ() - this.sideLength / 2;
      }

      @Override
      public List<ChunkAccess> generateChunks(ServerLevel level) {
         int chunkX = this.posX;
         int chunkZ = this.posZ;
         this.posX = this.posX + (GENERATE_SIZE - 1);
         if (this.posX > this.maxX) {
            this.posX = this.minX;
            this.posZ = this.posZ + (GENERATE_SIZE - 1);
         }

         return ChunkGetter.centerChunks(level, this.chunkGenerator, chunkX, chunkZ, GENERATE_SIZE);
      }
   }

   private static class ChunkGetterRandom implements ChunkGetter.IChunkGetterStrategy {
      private static final int GENERATE_SIZE = (int)Math.ceil(Math.sqrt(25.0)) + 2;
      private final ChunkGenerator chunkGenerator;

      public ChunkGetterRandom(ServerLevel level) {
         this.chunkGenerator = level.getChunkSource().getGenerator();
      }

      @Override
      public List<ChunkAccess> generateChunks(ServerLevel level) {
         WorldBorder worldBorder = level.getWorldBorder();
         int maxChunkPos = (int)(worldBorder.getSize() / 16.0) - GENERATE_SIZE;
         int chunkX = level.random.nextInt(2 * maxChunkPos) - maxChunkPos + (int)worldBorder.getCenterX();
         int chunkZ = level.random.nextInt(2 * maxChunkPos) - maxChunkPos + (int)worldBorder.getCenterZ();
         return ChunkGetter.centerChunks(level, this.chunkGenerator, chunkX, chunkZ, GENERATE_SIZE);
      }
   }

   private interface IChunkGetterStrategy {
      List<ChunkAccess> generateChunks(ServerLevel var1);
   }
}
