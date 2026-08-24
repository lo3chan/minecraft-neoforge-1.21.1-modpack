package com.seibel.distanthorizons.common.wrappers.worldGeneration;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.common.wrappers.McObjectConverter_neoforge;
import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.params.GlobalWorldGenParams_neoforge;
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
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jetbrains.annotations.Nullable;

public class InternalServerGenerator_neoforge {
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
   private static final TicketType<ChunkPos> DH_SERVER_GEN_TICKET = TicketType.create("dh_server_gen_ticket", Comparator.comparingLong(ChunkPos::toLong));
   private static boolean c2meMissingWarningLogged = false;
   private final GlobalWorldGenParams_neoforge params;
   private final IDhServerLevel dhServerLevel;
   @Nullable
   private final ChunkUpdateQueueManager updateManager;
   private final Timer chunkSaveIgnoreTimer = TimerUtil.CreateTimer("ChunkSaveIgnoreTimer");

   public InternalServerGenerator_neoforge(GlobalWorldGenParams_neoforge params, IDhServerLevel dhServerLevel) {
      this.params = params;
      this.dhServerLevel = dhServerLevel;
      this.updateManager = WorldChunkUpdateManager.INSTANCE.getByLevelWrapper(this.dhServerLevel.getServerLevelWrapper());
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void generateChunksViaInternalServer(GenerationEvent_neoforge genEvent) {
      this.runValidation();
      boolean var14 = false /* VF: Semaphore variable */;

      try {
         var14 = true;
         ArrayList<CompletableFuture<ChunkAccess>> releaseFutures = new ArrayList();
         Iterator chunkPosIterator = ChunkPosGenStream_neoforge.getIterator(genEvent.minPos.getX(), genEvent.minPos.getZ(), genEvent.widthInChunks, 0);

         while (chunkPosIterator.hasNext()) {
            ChunkPos chunkPos = (ChunkPos)chunkPosIterator.next();
            CompletableFuture<ChunkAccess> requestChunkFuture = this.requestChunkFromServerAsync(chunkPos)
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
            CompletableFuture<ChunkAccess> getChunkFuture = (CompletableFuture<ChunkAccess>)releaseFutures.get(i);
            ChunkAccess chunk = getChunkFuture.join();
            if (chunk != null) {
               ChunkWrapper_neoforge chunkWrapper = new ChunkWrapper_neoforge(chunk, this.dhServerLevel.getLevelWrapper());
               chunkWrapper.createDhHeightMaps();
               chunkWrappers.add(chunkWrapper);
            }
         }

         int maxSkyLight = this.dhServerLevel.getServerLevelWrapper().hasSkyLight() ? 15 : 0;

         for (int ix = 0; ix < chunkWrappers.size(); ix++) {
            ChunkWrapper_neoforge chunkWrapper = (ChunkWrapper_neoforge)chunkWrappers.get(ix);
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
            Iterator<ChunkPos> chunkPosIterator = ChunkPosGenStream_neoforge.getIterator(
               genEvent.minPos.getX(), genEvent.minPos.getZ(), genEvent.widthInChunks, 0
            );

            while (chunkPosIterator.hasNext()) {
               ChunkPos chunkPos = chunkPosIterator.next();
               releaseFutures.add(this.releaseChunkFromServerAsync(this.params.mcServerLevel, chunkPos));
            }

            for (int ix = 0; ix < releaseFutures.size(); ix++) {
               CompletableFuture<Void> releaseFuture = releaseFutures.get(ix);
               releaseFuture.join();
            }
         }
      }

      ArrayList<CompletableFuture<Void>> releaseFutures = new ArrayList<>();
      Iterator<ChunkPos> chunkPosIterator = ChunkPosGenStream_neoforge.getIterator(genEvent.minPos.getX(), genEvent.minPos.getZ(), genEvent.widthInChunks, 0);

      while (chunkPosIterator.hasNext()) {
         ChunkPos chunkPos = chunkPosIterator.next();
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

   private CompletableFuture<ChunkAccess> requestChunkFromServerAsync(ChunkPos chunkPos) {
      return CompletableFuture.<CompletableFuture<ChunkAccess>>supplyAsync(
            () -> {
               ServerLevel level = this.params.mcServerLevel;
               if (this.updateManager != null) {
                  this.updateManager.addPosToIgnore(McObjectConverter_neoforge.convert(chunkPos));
               }

               int chunkLevel = 33;
               level.getChunkSource().distanceManager.addTicket(DH_SERVER_GEN_TICKET, chunkPos, chunkLevel, chunkPos);
               level.getChunkSource().distanceManager.runAllUpdates(level.getChunkSource().chunkMap);
               ChunkHolder chunkHolder = level.getChunkSource().chunkMap.getUpdatingChunkIfPresent(chunkPos.toLong());
               if (chunkHolder == null) {
                  throw new IllegalStateException("No chunk chunkHolder for pos [" + chunkPos + "] after ticket has been added.");
               } else {
                  return chunkHolder.scheduleChunkGenerationTask(ChunkStatus.FULL, level.getChunkSource().chunkMap)
                     .thenApply(result -> (ChunkAccess)result.orElseThrow(() -> new RuntimeException(result.getError())));
               }
            },
            this.params.mcServerLevel.getChunkSource().chunkMap.mainThreadExecutor
         )
         .thenCompose(Function.identity());
   }

   private CompletableFuture<Void> releaseChunkFromServerAsync(ServerLevel level, ChunkPos chunkPos) {
      CompletableFuture<Void> removeTicketFuture = new CompletableFuture<>();
      level.getChunkSource().chunkMap.mainThreadExecutor.execute(() -> {
         try {
            int chunkLevel = 33;
            level.getChunkSource().distanceManager.removeTicket(DH_SERVER_GEN_TICKET, chunkPos, chunkLevel, chunkPos);
            level.getChunkSource().chunkMap.tick(() -> false);
            level.entityManager.tick();
            this.chunkSaveIgnoreTimer.schedule(new TimerTask() {
               @Override
               public void run() {
                  if (InternalServerGenerator_neoforge.this.updateManager != null) {
                     InternalServerGenerator_neoforge.this.updateManager.removePosToIgnore(McObjectConverter_neoforge.convert(chunkPos));
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
