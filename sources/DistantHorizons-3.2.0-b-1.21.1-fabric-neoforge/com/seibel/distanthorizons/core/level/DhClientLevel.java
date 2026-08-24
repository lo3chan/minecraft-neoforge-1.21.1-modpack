package com.seibel.distanthorizons.core.level;

import com.google.common.cache.CacheBuilder;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.eventHandlers.IgnoredDimensionCsvHandler;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.file.fullDatafile.RemoteFullDataSourceProvider;
import com.seibel.distanthorizons.core.file.fullDatafile.V2.FullDataSourceProviderV2;
import com.seibel.distanthorizons.core.file.structure.ISaveStructure;
import com.seibel.distanthorizons.core.generation.queues.AbstractLodRequestState;
import com.seibel.distanthorizons.core.generation.queues.LodRequestModule;
import com.seibel.distanthorizons.core.generation.queues.RemoteWorldRetrievalQueue;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.multiplayer.client.ClientNetworkState;
import com.seibel.distanthorizons.core.multiplayer.client.SyncOnLoadRequestQueue;
import com.seibel.distanthorizons.core.network.event.ScopedNetworkEventSource;
import com.seibel.distanthorizons.core.network.messages.fullData.FullDataPartialUpdateMessage;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.render.RenderBufferHandler;
import com.seibel.distanthorizons.core.sql.dto.FullDataSourceV2DTO;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhGenericRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import javax.annotation.CheckForNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DhClientLevel extends AbstractDhLevel implements IDhClientLevel {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final DhLogger NETWORK_LOGGER = new DhLoggerBuilder().fileLevelConfig(Config.Common.Logging.logNetworkEventToFile).build();
   private static final IMinecraftClientWrapper MC_CLIENT = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   public final ClientLevelModule clientside;
   public final IClientLevelWrapper levelWrapper;
   public final ISaveStructure saveStructure;
   public final RemoteFullDataSourceProvider remoteDataSourceProvider;
   @CheckForNull
   private final ClientNetworkState networkState;
   @Nullable
   private final ScopedNetworkEventSource networkEventSource;
   private final Set<DhChunkPos> loadedOnceChunks = Collections.newSetFromMap(CacheBuilder.newBuilder().expireAfterWrite(10L, TimeUnit.MINUTES).build().asMap());
   public final LodRequestModule lodRequestModule;
   @Nullable
   private final SyncOnLoadRequestQueue syncOnLoadRequestQueue;

   public DhClientLevel(ISaveStructure saveStructure, IClientLevelWrapper clientLevelWrapper, @Nullable ClientNetworkState networkState) throws SQLException, IOException {
      this(saveStructure, clientLevelWrapper, null, networkState);
   }

   public DhClientLevel(
      ISaveStructure saveStructure, IClientLevelWrapper clientLevelWrapper, @Nullable File fullDataSaveDirOverride, @Nullable ClientNetworkState networkState
   ) throws SQLException, IOException {
      File saveFolder = saveStructure.getSaveFolder(clientLevelWrapper);
      File pre23Folder = saveStructure.getPre23SaveFolder(clientLevelWrapper);
      saveFolder.mkdirs();
      if (pre23Folder.exists() && !pre23Folder.renameTo(saveFolder)) {
         throw new RuntimeException("Could not move old save data folder: [" + pre23Folder.getAbsolutePath() + "] to [" + saveFolder.getAbsolutePath() + "].");
      } else if (!saveFolder.exists()) {
         throw new IOException("unable to create save folder at [" + saveFolder.getPath() + "]. If you're on Windows you may need to enable long file paths.");
      } else {
         this.levelWrapper = clientLevelWrapper;
         this.levelWrapper.setDhLevel(this);
         this.saveStructure = saveStructure;
         this.networkState = networkState;
         if (this.networkState != null) {
            this.networkEventSource = new ScopedNetworkEventSource(this.networkState.getSession());
            this.syncOnLoadRequestQueue = new SyncOnLoadRequestQueue(this, this.networkState);
            this.registerNetworkHandlers();
         } else {
            this.networkEventSource = null;
            this.syncOnLoadRequestQueue = null;
         }

         this.remoteDataSourceProvider = new RemoteFullDataSourceProvider(this, saveStructure, fullDataSaveDirOverride, this.syncOnLoadRequestQueue);
         this.lodRequestModule = new LodRequestModule(this, this, this.remoteDataSourceProvider, () -> new DhClientLevel.LodRequestState(this, networkState));
         this.clientside = new ClientLevelModule(this);
         this.createAndSetSupportingRepos(this.remoteDataSourceProvider.repo.databaseFile);
         this.runRepoReliantSetup();
         this.clientside.startRenderer();
         LOGGER.info("Started DHLevel for " + this.levelWrapper + " with saves at " + this.saveStructure);
      }
   }

   private void registerNetworkHandlers() {
      assert this.networkEventSource != null;

      assert this.networkState != null;

      this.networkEventSource.registerHandler(FullDataPartialUpdateMessage.class, message -> {
         if (!MC_CLIENT.connectedToReplay()) {
            boolean isSameLevel = message.isSameLevelAs(this.levelWrapper);
            if (isSameLevel) {
               try {
                  FullDataSourceV2DTO dataSourceDto = this.networkState.fullDataPayloadReceiver.decodeDataSource(message.payload);

                  try {
                     Executor executor = ThreadPoolUtil.getFileHandlerExecutor();
                     if (executor != null) {
                        executor.execute(() -> {
                           try {
                              this.updateBeaconBeamsForSectionPos(dataSourceDto.pos, message.payload.beaconBeams);
                           } catch (Exception var4x) {
                              LOGGER.error("Unexpected erorr while updating full data source, error: [" + var4x.getMessage() + "].", var4x);
                           }
                        });
                     }

                     FullDataSourceV2 fullDataSource = dataSourceDto.createDataSource(this.levelWrapper, null);
                     this.updateDataSourcesAsync(fullDataSource).whenComplete((result, ex) -> fullDataSource.close());
                  } catch (Throwable var7) {
                     if (dataSourceDto != null) {
                        try {
                           dataSourceDto.close();
                        } catch (Throwable var6) {
                           var7.addSuppressed(var6);
                        }
                     }

                     throw var7;
                  }

                  if (dataSourceDto != null) {
                     dataSourceDto.close();
                  }
               } catch (Exception var8) {
                  LOGGER.error("Error while updating full data source", var8);
               }
            }
         }
      });
   }

   @Override
   public void clientTick() {
      try {
         IClientLevelWrapper clientLevelWrapper = MC_CLIENT.getWrappedClientLevel();
         if (clientLevelWrapper == null || clientLevelWrapper.getDhLevel() != this) {
            return;
         }

         this.clientside.clientTick();
         if (this.syncOnLoadRequestQueue != null) {
            this.syncOnLoadRequestQueue.tick(new DhBlockPos2D(MC_CLIENT.getPlayerBlockPos()));
         }
      } catch (Exception var2) {
         LOGGER.error("Unexpected clientTick Exception: " + var2.getMessage(), var2);
      }
   }

   @Override
   public boolean shouldDoWorldGen() {
      ClientNetworkState networkState = this.networkState;
      boolean isClientUsable = false;
      boolean isAllowedDimension = false;
      if (networkState != null) {
         isClientUsable = networkState.isReady();
         isAllowedDimension = MC_CLIENT.getWrappedClientLevel() == this.levelWrapper;
      }

      return isClientUsable && networkState.sessionConfig.isDistantGenerationEnabled() && isAllowedDimension && this.clientside.isRendering();
   }

   @Override
   public DhBlockPos2D getTargetPosForGeneration() {
      return new DhBlockPos2D(MC_CLIENT.getPlayerBlockPos());
   }

   @Override
   public void onWorldGenTaskComplete(long pos) {
      this.clientside.reloadPos(pos);
   }

   @Override
   public IClientLevelWrapper getClientLevelWrapper() {
      return this.levelWrapper;
   }

   @Override
   public void clearRenderCache() {
      this.clientside.clearRenderCache();
   }

   @NotNull
   @Override
   public ILevelWrapper getLevelWrapper() {
      return this.levelWrapper;
   }

   @Override
   public CompletableFuture<Void> updateDataSourcesAsync(FullDataSourceV2 data) {
      return this.clientside.updateDataSourcesAsync(data);
   }

   @Override
   public FullDataSourceProviderV2 getFullDataProvider() {
      return this.remoteDataSourceProvider;
   }

   @Override
   public ISaveStructure getSaveStructure() {
      return this.saveStructure;
   }

   @Override
   public IDhGenericRenderer getGenericRenderer() {
      return this.clientside.genericRenderer;
   }

   @Override
   public RenderBufferHandler getRenderBufferHandler() {
      ClientLevelModule.ClientRenderState renderState = this.clientside.ClientRenderStateRef.get();
      return renderState != null ? renderState.renderBufferHandler : null;
   }

   @Override
   public boolean isRendering() {
      return this.clientside.isRendering();
   }

   public boolean shouldProcessChunkUpdate(DhChunkPos chunkPos) {
      return this.networkState != null && this.networkState.isReady()
         ? !this.networkState.sessionConfig.isRealTimeUpdatesEnabled() || this.loadedOnceChunks.add(chunkPos)
         : true;
   }

   @Override
   public void addDebugMenuStringsToList(List<String> messageList) {
      String o = "§6";
      String y = "§e";
      String g = "§a";
      String r = "§c";
      String cf = "§r";
      String dimName = this.levelWrapper.getDhIdentifier();
      boolean rendering = this.clientside.isRendering() && !IgnoredDimensionCsvHandler.INSTANCE.dimensionNameShouldBeIgnored(dimName);
      String renderingString = rendering ? g + "yes" + cf : r + "no" + cf;
      messageList.add("[" + y + dimName + cf + "] rendering: " + renderingString);
      this.remoteDataSourceProvider.addDebugMenuStringsToList(messageList);
      this.lodRequestModule.addDebugMenuStringsToList(messageList);
      if (this.syncOnLoadRequestQueue != null) {
         assert this.networkState != null;

         if (this.networkState.sessionConfig.getSynchronizeOnLoad()) {
            this.syncOnLoadRequestQueue.addDebugMenuStringsToList(messageList);
         }
      }
   }

   @Override
   public String toString() {
      return "DhClientLevel{" + this.getClientLevelWrapper().getDhIdentifier() + "}";
   }

   @Override
   public void close() {
      if (this.lodRequestModule != null) {
         this.lodRequestModule.close();
      }

      if (this.networkEventSource != null) {
         this.networkEventSource.close();
      }

      this.levelWrapper.setDhLevel(null);
      this.clientside.close();
      super.close();
      this.remoteDataSourceProvider.close();
      LOGGER.info("Closed [" + DhClientLevel.class.getSimpleName() + "] for [" + this.levelWrapper + "]");
   }

   private static class LodRequestState extends AbstractLodRequestState {
      LodRequestState(DhClientLevel clientLevel, ClientNetworkState networkState) {
         super(clientLevel, new RemoteWorldRetrievalQueue(networkState, clientLevel));
      }
   }
}
