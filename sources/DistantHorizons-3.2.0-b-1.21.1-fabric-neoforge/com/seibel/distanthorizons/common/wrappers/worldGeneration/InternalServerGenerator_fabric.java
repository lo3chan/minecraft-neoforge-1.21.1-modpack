package com.seibel.distanthorizons.common.wrappers.worldGeneration;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.common.wrappers.McObjectConverter_fabric;
import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.params.GlobalWorldGenParams_fabric;
import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.api.internal.chunkUpdating.ChunkUpdateQueueManager;
import com.seibel.distanthorizons.core.api.internal.chunkUpdating.WorldChunkUpdateManager;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.ModAccessorInjector;
import com.seibel.distanthorizons.core.generation.DhLightingEngine;
import com.seibel.distanthorizons.core.level.IDhServerLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.ExceptionUtil;
import com.seibel.distanthorizons.core.util.TimerUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IC2meAccessor;
import com.seibel.distanthorizons.coreapi.ModInfo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import net.minecraft.class_1923;
import net.minecraft.class_2791;
import net.minecraft.class_2806;
import net.minecraft.class_3193;
import net.minecraft.class_3218;
import net.minecraft.class_3230;
import org.jetbrains.annotations.Nullable;

public class InternalServerGenerator_fabric {
   public static final DhLogger LOGGER = new DhLoggerBuilder()
      .name("LOD World Gen - Internal Server")
      .fileLevelConfig(Config.Common.Logging.logWorldGenEventToFile)
      .build();
   public static final DhLogger CHUNK_LOAD_LOGGER = new DhLoggerBuilder()
      .name("LOD Chunk Loading")
      .fileLevelConfig(Config.Common.Logging.logWorldGenChunkLoadEventToFile)
      .build();
   private static final IC2meAccessor C2ME_ACCESSOR = ModAccessorInjector.INSTANCE.get(IC2meAccessor.class);
   private static final int MS_TO_IGNORE_CHUNK_AFTER_COMPLETION = 5000;
   private static final class_3230<class_1923> DH_SERVER_GEN_TICKET = class_3230.method_14291(
      "dh_server_gen_ticket", Comparator.comparingLong(class_1923::method_8324)
   );
   private static boolean c2meMissingWarningLogged = false;
   private final GlobalWorldGenParams_fabric params;
   private final IDhServerLevel dhServerLevel;
   @Nullable
   private final ChunkUpdateQueueManager updateManager;
   private final Timer chunkSaveIgnoreTimer = TimerUtil.CreateTimer("ChunkSaveIgnoreTimer");

   public InternalServerGenerator_fabric(GlobalWorldGenParams_fabric params, IDhServerLevel dhServerLevel) {
      this.params = params;
      this.dhServerLevel = dhServerLevel;
      this.updateManager = WorldChunkUpdateManager.INSTANCE.getByLevelWrapper(this.dhServerLevel.getServerLevelWrapper());
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void generateChunksViaInternalServer(GenerationEvent_fabric genEvent) {
      this.runValidation();
      boolean var14 = false /* VF: Semaphore variable */;

      try {
         var14 = true;
         ArrayList<CompletableFuture<class_2791>> releaseFutures = new ArrayList();
         Iterator chunkPosIterator = ChunkPosGenStream_fabric.getIterator(genEvent.minPos.getX(), genEvent.minPos.getZ(), genEvent.widthInChunks, 0);

         while (chunkPosIterator.hasNext()) {
            class_1923 chunkPos = (class_1923)chunkPosIterator.next();
            CompletableFuture<class_2791> requestChunkFuture = this.requestChunkFromServerAsync(chunkPos)
               .whenCompleteAsync(
                  (chunk, throwable) -> {
                     Throwable actualThrowable = throwable;

                     while (actualThrowable instanceof CompletionException) {
                        actualThrowable = actualThrowable.getCause();
                     }

                     if (actualThrowable != null) {
                        boolean isShutdownException = ExceptionUtil.isShutdownException(actualThrowable)
                           || actualThrowable.getMessage().contains("Unloaded chunk");
                        if (!isShutdownException) {
                           CHUNK_LOAD_LOGGER.warn(
                              "DistantHorizons: Couldn't load chunk [" + chunkPos + "] from server, error: [" + actualThrowable.getMessage() + "].",
                              actualThrowable
                           );
                        }
                     }
                  }
               );
            releaseFutures.add(requestChunkFuture);
         }

         ArrayList<IChunkWrapper> chunkWrappers = new ArrayList<>();

         for (int i = 0; i < releaseFutures.size(); i++) {
            CompletableFuture<class_2791> getChunkFuture = (CompletableFuture<class_2791>)releaseFutures.get(i);
            class_2791 chunk = getChunkFuture.join();
            if (chunk != null) {
               ChunkWrapper_fabric chunkWrapper = new ChunkWrapper_fabric(chunk, this.dhServerLevel.getLevelWrapper());
               chunkWrapper.createDhHeightMaps();
               chunkWrappers.add(chunkWrapper);
            }
         }

         int maxSkyLight = this.dhServerLevel.getServerLevelWrapper().hasSkyLight() ? 15 : 0;

         for (int ix = 0; ix < chunkWrappers.size(); ix++) {
            ChunkWrapper_fabric chunkWrapper = (ChunkWrapper_fabric)chunkWrappers.get(ix);
            if (!chunkWrapper.isDhBlockLightingCorrect()) {
               DhLightingEngine.INSTANCE.bakeChunkBlockLighting(chunkWrapper, chunkWrappers, maxSkyLight);
            }

            this.dhServerLevel.updateBeaconBeamsForChunk(chunkWrapper, chunkWrappers);
            genEvent.resultConsumer.accept(chunkWrapper);
         }

         var14 = false;
      } finally {
         if (var14) {
            ArrayList<CompletableFuture<Void>> releaseFutures = new ArrayList<>();
            Iterator<class_1923> chunkPosIterator = ChunkPosGenStream_fabric.getIterator(
               genEvent.minPos.getX(), genEvent.minPos.getZ(), genEvent.widthInChunks, 0
            );

            while (chunkPosIterator.hasNext()) {
               class_1923 chunkPos = chunkPosIterator.next();
               releaseFutures.add(this.releaseChunkFromServerAsync(this.params.mcServerLevel, chunkPos));
            }

            for (int ix = 0; ix < releaseFutures.size(); ix++) {
               CompletableFuture<Void> releaseFuture = releaseFutures.get(ix);
               releaseFuture.join();
            }
         }
      }

      ArrayList<CompletableFuture<Void>> releaseFutures = new ArrayList<>();
      Iterator<class_1923> chunkPosIterator = ChunkPosGenStream_fabric.getIterator(genEvent.minPos.getX(), genEvent.minPos.getZ(), genEvent.widthInChunks, 0);

      while (chunkPosIterator.hasNext()) {
         class_1923 chunkPos = chunkPosIterator.next();
         releaseFutures.add(this.releaseChunkFromServerAsync(this.params.mcServerLevel, chunkPos));
      }

      for (int ix = 0; ix < releaseFutures.size(); ix++) {
         CompletableFuture<Void> releaseFuture = releaseFutures.get(ix);
         releaseFuture.join();
      }
   }

   private void runValidation() {
      if (!DhApi.isDhThread() && ModInfo.IS_DEV_BUILD) {
         throw new IllegalStateException(
            "Internal server generation should be called from one of DH's world gen thread. Current thread: [" + Thread.currentThread().getName() + "]"
         );
      } else {
         if (C2ME_ACCESSOR == null && !c2meMissingWarningLogged) {
            c2meMissingWarningLogged = true;
            String c2meWarning = "C2ME missing, \nlow CPU usage and slow world gen speeds expected. \nDH is set to use MC's internal server for world gen \nthis mode is less efficient unless a mod like C2ME is present.";
            if (Config.Common.Logging.Warning.showSlowWorldGenSettingWarnings.get()) {
               String message = "§6Distant Horizons: slow world gen.§r\n" + c2meWarning;
               ClientApi.INSTANCE.showChatMessageNextFrame(message);
            }

            LOGGER.warn(c2meWarning);
         }
      }
   }

   private CompletableFuture<class_2791> requestChunkFromServerAsync(class_1923 chunkPos) {
      return CompletableFuture.<CompletableFuture<class_2791>>supplyAsync(
            () -> {
               class_3218 level = this.params.mcServerLevel;
               if (this.updateManager != null) {
                  this.updateManager.addPosToIgnore(McObjectConverter_fabric.convert(chunkPos));
               }

               int chunkLevel = 33;
               level.method_14178().field_17252.method_17290(DH_SERVER_GEN_TICKET, chunkPos, chunkLevel, chunkPos);
               level.method_14178().field_17252.method_15892(level.method_14178().field_17254);
               class_3193 chunkHolder = level.method_14178().field_17254.method_17255(chunkPos.method_8324());
               if (chunkHolder == null) {
                  throw new IllegalStateException("No chunk chunkHolder for pos [" + chunkPos + "] after ticket has been added.");
               } else {
                  return chunkHolder.method_60458(class_2806.field_12803, level.method_14178().field_17254)
                     .thenApply(result -> (class_2791)result.method_57132(() -> new RuntimeException(result.method_57129())));
               }
            },
            this.params.mcServerLevel.method_14178().field_17254.field_17216
         )
         .thenCompose(Function.identity());
   }

   private CompletableFuture<Void> releaseChunkFromServerAsync(class_3218 level, class_1923 chunkPos) {
      CompletableFuture<Void> removeTicketFuture = new CompletableFuture<>();
      level.method_14178().field_17254.field_17216.execute(() -> {
         try {
            int chunkLevel = 33;
            level.method_14178().field_17252.method_20444(DH_SERVER_GEN_TICKET, chunkPos, chunkLevel, chunkPos);
            level.method_14178().field_17254.method_17233(() -> false);
            level.field_26935.method_31809();
            this.chunkSaveIgnoreTimer.schedule(new TimerTask() {
               @Override
               public void run() {
                  if (InternalServerGenerator_fabric.this.updateManager != null) {
                     InternalServerGenerator_fabric.this.updateManager.removePosToIgnore(McObjectConverter_fabric.convert(chunkPos));
                  }
               }
            }, 5000L);
         } catch (Exception var8) {
            LOGGER.warn("Failed to release chunk [" + chunkPos + "] back to internal server. Error: [" + var8.getMessage() + "]", var8);
         } finally {
            removeTicketFuture.complete(null);
         }
      });
      return removeTicketFuture;
   }
}
