package com.seibel.distanthorizons.core.api.internal;

import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiWorldLoadEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiWorldUnloadEvent;
import com.seibel.distanthorizons.core.api.internal.chunkUpdating.ChunkUpdateData;
import com.seibel.distanthorizons.core.api.internal.chunkUpdating.ChunkUpdateQueueManager;
import com.seibel.distanthorizons.core.api.internal.chunkUpdating.WorldChunkUpdateManager;
import com.seibel.distanthorizons.core.config.eventHandlers.IgnoredDimensionCsvHandler;
import com.seibel.distanthorizons.core.dataObjects.render.textures.BlockTextureRegistry;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.level.DhClientLevel;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.render.RenderThreadTaskHandler;
import com.seibel.distanthorizons.core.render.renderer.AbstractDebugWireframeRenderer;
import com.seibel.distanthorizons.core.sql.repo.AbstractDhRepo;
import com.seibel.distanthorizons.core.util.objects.Pair;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import com.seibel.distanthorizons.core.world.AbstractDhWorld;
import com.seibel.distanthorizons.core.world.DhApiWorldProxy;
import com.seibel.distanthorizons.core.world.EWorldEnvironment;
import com.seibel.distanthorizons.core.world.IDhClientWorld;
import com.seibel.distanthorizons.core.world.IDhServerWorld;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.coreapi.DependencyInjection.ApiEventInjector;
import java.util.ArrayList;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.RejectedExecutionException;
import org.jetbrains.annotations.Nullable;

public class SharedApi {
   public static final SharedApi INSTANCE = new SharedApi();
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   @Nullable
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   public static final WorldChunkUpdateManager WORLD_CHUNK_UPDATE_MANAGER = WorldChunkUpdateManager.INSTANCE;
   @Nullable
   private static AbstractDhWorld currentWorld;
   private static final Object worldLockObject = new Object();

   private SharedApi() {
   }

   public static EWorldEnvironment getEnvironment() {
      return currentWorld == null ? null : currentWorld.environment;
   }

   public static void setDhWorld(AbstractDhWorld newWorld) {
      synchronized (worldLockObject) {
         AbstractDhWorld oldWorld = currentWorld;
         if (oldWorld != null) {
            oldWorld.close();
         }

         currentWorld = newWorld;
         if (currentWorld != null) {
            ThreadPoolUtil.setupThreadPools();
            ApiEventInjector.INSTANCE.fireAllEvents(DhApiWorldLoadEvent.class, new DhApiWorldLoadEvent.EventParam());
         } else {
            ThreadPoolUtil.shutdownThreadPools();
            AbstractDebugWireframeRenderer debugWireframeRenderer = SingletonInjector.INSTANCE.get(AbstractDebugWireframeRenderer.class);
            debugWireframeRenderer.clearRenderables();
            if (MC_RENDER != null) {
               MC_RENDER.clearTargetFrameBuffer();
            }

            AbstractDhRepo.closeAllConnections();
            WORLD_CHUNK_UPDATE_MANAGER.clear();
            RenderThreadTaskHandler.INSTANCE.clearDebugStats();
            BlockTextureRegistry.INSTANCE.clear();
            System.gc();
            ApiEventInjector.INSTANCE.fireAllEvents(DhApiWorldUnloadEvent.class, new DhApiWorldUnloadEvent.EventParam());
            DhApiWorldProxy.INSTANCE.setReadOnly(false, false);
         }
      }
   }

   @Nullable
   public static AbstractDhWorld getAbstractDhWorld() {
      return currentWorld;
   }

   @Nullable
   public static IDhClientWorld tryGetDhClientWorld() {
      return currentWorld instanceof IDhClientWorld ? (IDhClientWorld)currentWorld : null;
   }

   @Nullable
   public static IDhServerWorld tryGetDhServerWorld() {
      return currentWorld instanceof IDhServerWorld ? (IDhServerWorld)currentWorld : null;
   }

   public static boolean isChunkAtBlockPosAlreadyUpdating(ILevelWrapper levelWrapper, int blockPosX, int blockPosZ) {
      ChunkUpdateQueueManager manager = WORLD_CHUNK_UPDATE_MANAGER.getByLevelWrapper(levelWrapper);
      return manager == null ? true : manager.contains(new DhChunkPos(new DhBlockPos2D(blockPosX, blockPosZ)));
   }

   public static boolean isChunkAtChunkPosAlreadyUpdating(ILevelWrapper levelWrapper, int chunkPosX, int chunkPosZ) {
      ChunkUpdateQueueManager manager = WORLD_CHUNK_UPDATE_MANAGER.getByLevelWrapper(levelWrapper);
      return manager == null ? true : manager.contains(new DhChunkPos(chunkPosX, chunkPosZ));
   }

   public void applyChunkUpdate(IChunkWrapper chunkWrapper, ILevelWrapper levelWrapper, boolean waitForLoadedWorld) {
      if (chunkWrapper != null) {
         AbstractDhWorld dhWorld = getAbstractDhWorld();
         if (dhWorld == null) {
            if (levelWrapper instanceof IClientLevelWrapper) {
               IClientLevelWrapper clientLevel = (IClientLevelWrapper)levelWrapper;
               ClientApi.INSTANCE.waitingChunkByClientLevelAndPos.put(new Pair<>(clientLevel, chunkWrapper.getChunkPos()), chunkWrapper);
            }
         } else if (!DhApiWorldProxy.INSTANCE.tryGetReadOnly()) {
            IDhLevel dhLevel;
            if (waitForLoadedWorld) {
               dhLevel = dhWorld.getLevel(levelWrapper);
               if (dhLevel == null) {
                  if (levelWrapper instanceof IClientLevelWrapper) {
                     IClientLevelWrapper clientLevel = (IClientLevelWrapper)levelWrapper;
                     ClientApi.INSTANCE.waitingChunkByClientLevelAndPos.put(new Pair<>(clientLevel, chunkWrapper.getChunkPos()), chunkWrapper);
                  }

                  return;
               }
            } else {
               dhLevel = levelWrapper.getDhLevel();
               if (dhLevel == null) {
                  LOGGER.warn("No DH level provided by the [" + levelWrapper.getClass() + "], some chunks may not update properly.");
                  return;
               }
            }

            if (!(dhLevel instanceof DhClientLevel) || ((DhClientLevel)dhLevel).shouldProcessChunkUpdate(chunkWrapper.getChunkPos())) {
               String dimName = dhLevel.getLevelWrapper().getDimensionName();
               if (!IgnoredDimensionCsvHandler.INSTANCE.dimensionNameShouldBeIgnored(dimName)) {
                  ChunkUpdateQueueManager chunkManager = WORLD_CHUNK_UPDATE_MANAGER.getByLevelWrapper(levelWrapper);
                  if (chunkManager != null && !chunkManager.contains(chunkWrapper.getChunkPos())) {
                     queueChunkUpdate(chunkManager, chunkWrapper, dhLevel);
                  }
               }
            }
         }
      }
   }

   private static void queueChunkUpdate(ChunkUpdateQueueManager chunkManager, IChunkWrapper chunkWrapper, IDhLevel dhLevel) {
      if (!chunkManager.contains(chunkWrapper.getChunkPos())) {
         ChunkUpdateData updateData = new ChunkUpdateData(chunkWrapper, dhLevel);
         chunkManager.addItemToPreUpdateQueue(chunkWrapper.getChunkPos(), updateData);
         AbstractExecutorService executor = ThreadPoolUtil.getChunkToLodBuilderExecutor();
         if (executor != null) {
            try {
               executor.execute(WORLD_CHUNK_UPDATE_MANAGER::processEachQueue);
            } catch (RejectedExecutionException var6) {
            }
         }
      }
   }

   public ArrayList<String> getDebugMenuString() {
      return WORLD_CHUNK_UPDATE_MANAGER.getDebugMenuString();
   }
}
