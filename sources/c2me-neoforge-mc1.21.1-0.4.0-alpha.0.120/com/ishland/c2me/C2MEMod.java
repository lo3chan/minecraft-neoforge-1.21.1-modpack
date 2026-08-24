package com.ishland.c2me;

import com.ibm.asyncutil.util.Combinators;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.world.level.chunk.storage.RegionFileVersion;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("c2me")
public class C2MEMod {
   public static final Logger LOGGER = LoggerFactory.getLogger("C2ME");

   public C2MEMod(IEventBus eventBus) {
      PreLaunchHandler.onPreLaunch();
      if (Boolean.getBoolean("com.ishland.c2me.runCompressionBenchmark")) {
         LOGGER.info("Benchmarking chunk stream speed");
         LOGGER.info("Warming up");

         for (int i = 0; i < 3; i++) {
            this.runBenchmark("GZIP", RegionFileVersion.VERSION_GZIP, true);
            this.runBenchmark("DEFLATE", RegionFileVersion.VERSION_DEFLATE, true);
            this.runBenchmark("UNCOMPRESSED", RegionFileVersion.VERSION_NONE, true);
         }

         this.runBenchmark("GZIP", RegionFileVersion.VERSION_GZIP, false);
         this.runBenchmark("DEFLATE", RegionFileVersion.VERSION_DEFLATE, false);
         this.runBenchmark("UNCOMPRESSED", RegionFileVersion.VERSION_NONE, false);
      }

      if (Boolean.getBoolean("com.ishland.c2me.runConsistencyTest")) {
         this.consistencyTest();
      }
   }

   private void runBenchmark(String name, RegionFileVersion version, boolean suppressLog) {
      try {
         DecimalFormat decimalFormat = new DecimalFormat("0.###");
         if (!suppressLog) {
            LOGGER.info("Generating 128MB random data");
         }

         byte[] bytes = new byte[134217728];
         new Random().nextBytes(bytes);
         if (!suppressLog) {
            LOGGER.info("Starting benchmark for {}", name);
         }

         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         OutputStream wrappedOutputStream = version.wrap(outputStream);
         long startTime = System.nanoTime();
         wrappedOutputStream.write(bytes);
         wrappedOutputStream.close();
         long endTime = System.nanoTime();
         if (!suppressLog) {
            LOGGER.info(
               "{} write speed: {} MB/s ({} MB/s compressed)",
               new Object[]{
                  name,
                  decimalFormat.format(bytes.length / 1024.0 / 1024.0 / ((endTime - startTime) / 1.0E9)),
                  decimalFormat.format(outputStream.size() / 1024.0 / 1024.0 / ((endTime - startTime) / 1.0E9))
               }
            );
         }

         if (!suppressLog) {
            LOGGER.info("{} compression ratio: {} %", name, decimalFormat.format((double)outputStream.size() / bytes.length * 100.0));
         }

         ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
         InputStream wrappedInputStream = version.wrap(inputStream);
         long startTimex = System.nanoTime();
         byte[] readAllBytes = wrappedInputStream.readAllBytes();
         wrappedInputStream.close();
         long endTimex = System.nanoTime();
         if (!suppressLog) {
            LOGGER.info(
               "{} read speed: {} MB/s ({} MB/s compressed)",
               new Object[]{
                  name,
                  decimalFormat.format(readAllBytes.length / 1024.0 / 1024.0 / ((endTimex - startTimex) / 1.0E9)),
                  decimalFormat.format(outputStream.size() / 1024.0 / 1024.0 / ((endTimex - startTimex) / 1.0E9))
               }
            );
         }
      } catch (Throwable var14) {
         var14.printStackTrace();
      }
   }

   private void consistencyTest() {
      int taskSize = 512;
      AtomicIntegerArray array = new AtomicIntegerArray(taskSize);
      List<CompletableFuture<Integer>> futures = IntStream.range(0, taskSize).mapToObj(value -> CompletableFuture.supplyAsync(() -> {
         WorldgenRandom chunkRandom = new WorldgenRandom(new SingleThreadedRandomSource(System.nanoTime()));
         chunkRandom.consumeCount(4096);
         int ix = chunkRandom.nextInt();
         array.set(value, ix);
         return ix;
      })).toList();
      List<Integer> join = (List<Integer>)Combinators.collect(futures, Collectors.toList()).toCompletableFuture().join();

      for (int i = 0; i < taskSize; i++) {
         if (array.get(i) != join.get(i)) {
            throw new IllegalArgumentException("Mismatch at index " + i);
         }
      }
   }
}
