package com.seibel.distanthorizons.core.generation;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.file.fullDatafile.GeneratedFullDataSourceProvider;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.util.FormatUtil;
import com.seibel.distanthorizons.core.util.objects.RollingAverage;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftSharedWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.Nullable;

public class PregenManager {
   protected static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftSharedWrapper MC_SERVER = SingletonInjector.INSTANCE.get(IMinecraftSharedWrapper.class);
   private final AtomicReference<PregenManager.PregenState> pregenFuture = new AtomicReference<>();

   public CompletableFuture<Void> startPregen(IServerLevelWrapper levelWrapper, DhBlockPos2D origin, int chunkRadius) {
      PregenManager.PregenState pregenState = new PregenManager.PregenState(
         (GeneratedFullDataSourceProvider)SharedApi.tryGetDhServerWorld().getLevel(levelWrapper).getFullDataProvider(),
         DhSectionPos.convertToDetailLevel(DhSectionPos.encode((byte)0, origin.x, origin.z), (byte)6),
         (int)Math.pow(Math.ceil(chunkRadius / 4.0 * 2.0), 2.0)
      );
      if (!this.pregenFuture.compareAndSet(null, pregenState)) {
         pregenState.completeExceptionally(new IllegalStateException("Pregen is already running."));
         return pregenState;
      } else {
         pregenState.whenComplete((result, throwable) -> this.pregenFuture.set(null));
         pregenState.fillPendingQueue();
         return pregenState;
      }
   }

   public CompletableFuture<Void> getRunningPregen() {
      return this.pregenFuture.get();
   }

   @Nullable
   public String getStatusString() {
      PregenManager.PregenState pregenState = this.pregenFuture.get();
      return pregenState != null ? pregenState.getStatusString() : null;
   }

   private static class DynamicNumberFormat {
      private final int maxPrecision;
      private double value = 0.0;
      private int lastPrecision = 0;

      private DynamicNumberFormat(int maxPrecision) {
         this.maxPrecision = maxPrecision;
      }

      public synchronized void update(double newValue) {
         int precision = 0;

         while (precision < this.maxPrecision && (int)(newValue * Math.pow(10.0, precision)) == (int)(this.value * Math.pow(10.0, precision))) {
            precision++;
         }

         if (precision < this.lastPrecision) {
            int tmpPrecision = this.lastPrecision;
            this.lastPrecision = precision;
            precision = tmpPrecision;
         } else {
            this.lastPrecision = precision;
         }

         this.value = Math.round(newValue * Math.pow(10.0, precision)) / Math.pow(10.0, precision);
      }

      public double getValue() {
         return this.value;
      }
   }

   private static class PregenState extends CompletableFuture<Void> {
      private final GeneratedFullDataSourceProvider fullDataSourceProvider;
      private final long originSectionPos;
      private final int sectionsToGenerate;
      private final AtomicInteger nextSectionSpiralIndex = new AtomicInteger(0);
      private final AtomicLong lastTaskFinishTime = new AtomicLong(System.currentTimeMillis());
      private RollingAverage averageTaskCompletionIntervalMs = new RollingAverage(1000);
      private final RollingAverage averageTaskCompletionIntervalMsShort = new RollingAverage(50);
      private final AtomicLong lastLogTime = new AtomicLong();
      private final PregenManager.DynamicNumberFormat generatedRadius = new PregenManager.DynamicNumberFormat(3);
      private final PregenManager.DynamicNumberFormat generatedPercentage = new PregenManager.DynamicNumberFormat(5);
      private final Cache<Long, Long> pendingGenerations = CacheBuilder.newBuilder()
         .expireAfterWrite(2L, TimeUnit.MINUTES)
         .removalListener(removalNotification -> {
            if (removalNotification.getCause() == RemovalCause.EXPIRED) {
               PregenManager.LOGGER.warn("Generation for section " + DhSectionPos.toString((Long)removalNotification.getKey()) + " has expired!");
            }

            long timeSincePreviousTaskFinish = System.currentTimeMillis() - this.lastTaskFinishTime.getAndSet(System.currentTimeMillis());
            this.averageTaskCompletionIntervalMs.add(timeSincePreviousTaskFinish);
            this.averageTaskCompletionIntervalMsShort.add(timeSincePreviousTaskFinish);
            this.fillPendingQueue();
         })
         .build();

      public PregenState(GeneratedFullDataSourceProvider fullDataSourceProvider, long originSectionPos, int sectionsToGenerate) {
         this.fullDataSourceProvider = fullDataSourceProvider;
         this.originSectionPos = originSectionPos;
         this.sectionsToGenerate = sectionsToGenerate;
      }

      private void fillPendingQueue() {
         while (!this.isDone() && this.pendingGenerations.size() < Config.Common.MultiThreading.numberOfThreads.get().intValue()) {
            int nextSpiralIndex = this.nextSectionSpiralIndex.getAndIncrement();
            if (nextSpiralIndex > this.sectionsToGenerate) {
               this.complete(null);
               return;
            }

            long nextSectionPos = this.sectionPosOnSpiral(nextSpiralIndex);
            long lastLogTime = this.lastLogTime.get();
            if (System.currentTimeMillis() - lastLogTime
                  >= TimeUnit.SECONDS.toMillis(Config.Common.WorldGenerator.generationProgressDisplayIntervalInSeconds.get().intValue())
               && this.lastLogTime.compareAndSet(lastLogTime, System.currentTimeMillis())) {
               PregenManager.LOGGER.info(this.getStatusString());
            }

            this.pendingGenerations.put(nextSectionPos, System.currentTimeMillis());
            this.fullDataSourceProvider.getAsync(nextSectionPos).thenAccept(fullDataSource -> {
               if (this.fullDataSourceProvider.generationStepsAreFullyGenerated(fullDataSource.columnGenerationSteps)) {
                  this.pendingGenerations.invalidate(fullDataSource.getPos());
               } else {
                  this.fullDataSourceProvider.queuePositionForRetrieval(fullDataSource.getPos()).whenComplete((result, throwable) -> {
                     if (throwable != null) {
                        PregenManager.LOGGER.warn("Failed to generate section " + DhSectionPos.toString(result.pos));
                     }

                     this.pendingGenerations.invalidate(result.pos);
                  });
               }

               fullDataSource.close();
            });
         }
      }

      public String getStatusString() {
         this.generatedRadius.update(Math.sqrt(this.nextSectionSpiralIndex.get()) / 2.0 * 4.0);
         this.generatedPercentage.update((double)this.nextSectionSpiralIndex.get() / this.sectionsToGenerate);
         double chunksToGenerate = Math.ceil(Math.sqrt(this.sectionsToGenerate) / 2.0 * 4.0 * 10.0) / 10.0;
         int chunkRatePerSecond = (int)(1000.0 / this.averageTaskCompletionIntervalMs.getAverage() * 4.0 * 4.0);
         double etaMs = this.averageTaskCompletionIntervalMs.getAverage() * (this.sectionsToGenerate - this.nextSectionSpiralIndex.get());
         double averageRatio = this.averageTaskCompletionIntervalMsShort.getAverage() / this.averageTaskCompletionIntervalMs.getAverage();
         if (averageRatio < 0.5 || averageRatio > 2.0) {
            this.averageTaskCompletionIntervalMs = new RollingAverage(1000);
         }

         return MessageFormat.format(
            "Generated radius: {0,number,#.###} / {1,number,#.#} chunks ({2} cps, {3,number,#.###%}), ETA: {4}",
            this.generatedRadius.getValue(),
            chunksToGenerate,
            chunkRatePerSecond,
            this.generatedPercentage.getValue(),
            FormatUtil.formatEta(Duration.ofMillis((long)etaMs))
         );
      }

      private long sectionPosOnSpiral(int index) {
         if (index == 0) {
            return this.originSectionPos;
         } else {
            index--;
            int ringNumber = (int)Math.round(Math.sqrt(Math.floor(index / 4.0) + 1.0));
            index -= ringNumber * 8 * (ringNumber - 1) / 2;
            int x = -ringNumber + 1 + Math.min(index % (ringNumber * 4), ringNumber * 2 - 1);
            int z = ringNumber - Math.max(0, index % (ringNumber * 4) - ringNumber * 2 + 1);
            if (index >= ringNumber * 4) {
               x = -x;
               z = -z;
            }

            x += DhSectionPos.getX(this.originSectionPos);
            z += DhSectionPos.getZ(this.originSectionPos);
            return DhSectionPos.encode(DhSectionPos.getDetailLevel(this.originSectionPos), x, z);
         }
      }
   }
}
