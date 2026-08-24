package com.seibel.distanthorizons.core.level;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.file.fullDatafile.IDataSourceUpdateListenerFunc;
import com.seibel.distanthorizons.core.file.fullDatafile.V2.FullDataSourceProviderV2;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.render.RenderBufferHandler;
import com.seibel.distanthorizons.core.render.QuadTree.LodQuadTree;
import com.seibel.distanthorizons.core.util.math.DhVec3d;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhGenericRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import java.io.Closeable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.WillNotClose;

public class ClientLevelModule implements Closeable, IDataSourceUpdateListenerFunc<FullDataSourceV2> {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftClientWrapper MC_CLIENT = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final IWrapperFactory WRAPPER_FACTORY = SingletonInjector.INSTANCE.get(IWrapperFactory.class);
   private final IDhClientLevel clientLevel;
   @WillNotClose
   public final FullDataSourceProviderV2 fullDataSourceProvider;
   public final AtomicReference<ClientLevelModule.ClientRenderState> ClientRenderStateRef = new AtomicReference<>();
   public final IDhGenericRenderer genericRenderer = WRAPPER_FACTORY.createGenericRenderer();

   public ClientLevelModule(IDhClientLevel clientLevel) {
      this.clientLevel = clientLevel;
      this.fullDataSourceProvider = this.clientLevel.getFullDataProvider();
      this.fullDataSourceProvider.addDataSourceUpdateListener(this);
   }

   public void clientTick() {
      if (MC_CLIENT.playerExists()) {
         ClientLevelModule.ClientRenderState clientRenderState = this.ClientRenderStateRef.get();
         if (clientRenderState != null) {
            if (clientRenderState.viewDiameterInBlocks != ClientLevelModule.ClientRenderState.getViewDiameterInBlocks()) {
               clientRenderState.close();
               this.ClientRenderStateRef.set(null);
               clientRenderState = new ClientLevelModule.ClientRenderState(this.clientLevel, this.clientLevel.getFullDataProvider());
               this.ClientRenderStateRef.set(clientRenderState);
            }

            DhBlockPos2D quadTreeTickBlockPos;
            if (Config.Client.Advanced.Graphics.Quality.useCameraPositionForQualityDropOff.get()) {
               DhVec3d cameraDoublePos = MC_RENDER.getCameraExactPosition();
               quadTreeTickBlockPos = new DhBlockPos2D((int)cameraDoublePos.x, (int)cameraDoublePos.z);
            } else {
               quadTreeTickBlockPos = new DhBlockPos2D(MC_CLIENT.getPlayerBlockPos());
            }

            clientRenderState.quadtree.tryTick(quadTreeTickBlockPos);
         }
      }
   }

   public void startRenderer() {
      ClientLevelModule.ClientRenderState clientRenderState = new ClientLevelModule.ClientRenderState(this.clientLevel, this.clientLevel.getFullDataProvider());
      if (!this.ClientRenderStateRef.compareAndSet(null, clientRenderState)) {
         LOGGER.warn("Renderer already started for [" + this.clientLevel.getClientLevelWrapper() + "].");
         clientRenderState.close();
      }
   }

   public boolean isRendering() {
      return this.ClientRenderStateRef.get() != null;
   }

   public void stopRenderer() {
      ClientLevelModule.ClientRenderState clientRenderState = this.ClientRenderStateRef.get();
      if (clientRenderState == null) {
         LOGGER.warn("Tried to stop the renderer for [" + this + "] when it was not started.");
      } else {
         this.ClientRenderStateRef.compareAndSet(clientRenderState, null);
         clientRenderState.close();
      }
   }

   public CompletableFuture<Void> updateDataSourcesAsync(FullDataSourceV2 data) {
      return this.clientLevel.getFullDataProvider().updateDataSourceAsync(data);
   }

   public void OnDataSourceUpdated(FullDataSourceV2 updatedFullDataSource) {
      ClientLevelModule.ClientRenderState ClientRenderState = this.ClientRenderStateRef.get();
      if (ClientRenderState != null) {
         ClientRenderState.quadtree.queuePosToReload(updatedFullDataSource.getPos());
      }
   }

   @Override
   public void close() {
      ClientLevelModule.ClientRenderState ClientRenderState = this.ClientRenderStateRef.get();
      if (ClientRenderState != null) {
         this.ClientRenderStateRef.compareAndSet(ClientRenderState, null);
         ClientRenderState.close();
      }

      this.fullDataSourceProvider.removeDataSourceUpdateListener(this);
      this.genericRenderer.close();
   }

   public void clearRenderCache() {
      IClientLevelWrapper clientLevelWrapper = this.clientLevel.getClientLevelWrapper();
      if (clientLevelWrapper != null) {
         clientLevelWrapper.clearBlockColorCache();
      }

      ClientLevelModule.ClientRenderState ClientRenderState = this.ClientRenderStateRef.get();
      if (ClientRenderState != null && ClientRenderState.quadtree != null) {
         ClientRenderState.quadtree.clearRenderDataCache();
      }
   }

   public void reloadPos(long pos) {
      ClientLevelModule.ClientRenderState clientRenderState = this.ClientRenderStateRef.get();
      if (clientRenderState != null && clientRenderState.quadtree != null) {
         clientRenderState.quadtree.queuePosToReload(pos);
      }
   }

   public static class ClientRenderState implements Closeable {
      private static final DhLogger LOGGER = new DhLoggerBuilder().build();
      public final LodQuadTree quadtree;
      public final RenderBufferHandler renderBufferHandler;
      public final int viewDiameterInBlocks = getViewDiameterInBlocks();

      public ClientRenderState(IDhClientLevel dhClientLevel, FullDataSourceProviderV2 fullDataSourceProvider) {
         this.quadtree = new LodQuadTree(dhClientLevel, this.viewDiameterInBlocks, 0, 0, fullDataSourceProvider);
         this.renderBufferHandler = new RenderBufferHandler(this.quadtree);
      }

      public static int getViewDiameterInBlocks() {
         return Config.Client.Advanced.Graphics.Quality.lodChunkRenderDistanceRadius.get() * 16 * 2;
      }

      @Override
      public void close() {
         this.quadtree.close();
      }
   }
}
