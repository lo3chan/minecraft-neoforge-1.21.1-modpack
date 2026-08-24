package com.seibel.distanthorizons.core.render.QuadTree;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiTransparency;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.dataObjects.render.ColumnRenderSource;
import com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding.ColumnRenderBufferBuilder;
import com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding.LodBufferContainer;
import com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding.LodQuadBuilder;
import com.seibel.distanthorizons.core.dataObjects.transformers.FullDataToRenderDataTransformer;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.file.fullDatafile.V2.FullDataSourceProviderV2;
import com.seibel.distanthorizons.core.level.IDhClientLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.render.renderer.AbstractDebugWireframeRenderer;
import com.seibel.distanthorizons.core.render.renderer.IDebugRenderable;
import com.seibel.distanthorizons.core.util.ExceptionUtil;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListCheckout;
import com.seibel.distanthorizons.core.util.threading.PriorityTaskPicker;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.WillNotClose;
import org.jetbrains.annotations.Nullable;

public class LodRenderSection implements IDebugRenderable, AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftClientWrapper MC = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   private static final AbstractDebugWireframeRenderer DEBUG_RENDERER = SingletonInjector.INSTANCE.get(AbstractDebugWireframeRenderer.class);
   public final long pos;
   private final IDhClientLevel clientLevel;
   private final IClientLevelWrapper levelWrapper;
   @WillNotClose
   private final FullDataSourceProviderV2 fullDataSourceProvider;
   private final LodQuadTree quadTree;
   private boolean renderingEnabled = false;
   public boolean renderDataDirty = false;
   public boolean queuedMissingSectionsForRetrieval = false;
   public LodBufferContainer renderBufferContainer;
   private final AtomicReference<CompletableFuture<Void>> getAndBuildRenderDataFutureRef = new AtomicReference<>(null);
   private Runnable getAndBuildRenderDataRunnable = null;

   public LodRenderSection(long pos, LodQuadTree quadTree, IDhClientLevel clientLevel, FullDataSourceProviderV2 fullDataSourceProvider) {
      this.pos = pos;
      this.quadTree = quadTree;
      this.clientLevel = clientLevel;
      this.levelWrapper = clientLevel.getClientLevelWrapper();
      this.fullDataSourceProvider = fullDataSourceProvider;
      DEBUG_RENDERER.register(this, Config.Client.Advanced.Debugging.DebugWireframe.showRenderSectionStatus);
   }

   public synchronized boolean uploadRenderDataToGpuAsync() {
      if (this.getAndBuildRenderDataFutureRef.get() != null) {
         return false;
      } else {
         PriorityTaskPicker.Executor executor = ThreadPoolUtil.getRenderLoadingExecutor();
         if (executor != null && !executor.isTerminated()) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.handle((voidObj, throwable) -> {
               this.getAndBuildRenderDataFutureRef.compareAndSet(future, null);
               this.getAndBuildRenderDataRunnable = null;
               return null;
            });

            try {
               if (!this.getAndBuildRenderDataFutureRef.compareAndSet(null, future)) {
                  CompletableFuture<Void> oldFuture = this.getAndBuildRenderDataFutureRef.get();
                  LodUtil.assertTrue(oldFuture != null, "Concurrency error");
                  return true;
               } else {
                  this.getAndBuildRenderDataRunnable = () -> {
                     try {
                        LodQuadBuilder lodQuadBuilder = this.getAndBuildRenderData();

                        label53: {
                           try {
                              if (lodQuadBuilder == null) {
                                 future.complete(null);
                                 break label53;
                              }

                              PhantomArrayListCheckout opaqueCheckout = LodQuadBuilder.ARRAY_LIST_POOL.checkoutByteBuffers(3);
                              PhantomArrayListCheckout transparentCheckout = LodQuadBuilder.ARRAY_LIST_POOL.checkoutByteBuffers(3);
                              ArrayList<ByteBuffer> opaqueBuffers = lodQuadBuilder.makeOpaqueVertexBuffers(opaqueCheckout);
                              ArrayList<ByteBuffer> transparentBuffers = lodQuadBuilder.makeTransparentVertexBuffers(transparentCheckout);
                              this.uploadToGpuAsync(future, opaqueBuffers, transparentBuffers).join();
                              opaqueCheckout.close();
                              transparentCheckout.close();
                              future.complete(null);
                           } catch (Throwable var8) {
                              if (lodQuadBuilder != null) {
                                 try {
                                    lodQuadBuilder.close();
                                 } catch (Throwable var7) {
                                    var8.addSuppressed(var7);
                                 }
                              }

                              throw var8;
                           }

                           if (lodQuadBuilder != null) {
                              lodQuadBuilder.close();
                           }

                           return;
                        }

                        if (lodQuadBuilder != null) {
                           lodQuadBuilder.close();
                        }
                     } catch (Exception var9) {
                        LOGGER.error(
                           "Unexpected issue creating render data for pos: [" + DhSectionPos.toString(this.pos) + "], error: [" + var9.getMessage() + "].",
                           var9
                        );
                        future.completeExceptionally(var9);
                     }
                  };
                  executor.execute(this.getAndBuildRenderDataRunnable);
                  return true;
               }
            } catch (RejectedExecutionException var4) {
               future.complete(null);
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Nullable
   private synchronized LodQuadBuilder getAndBuildRenderData() {
      try {
         ColumnRenderSource thisRenderSource = this.getRenderSourceForPos(this.pos, null);

         label151: {
            Object var23;
            label152: {
               LodQuadBuilder var10;
               try {
                  if (thisRenderSource == null) {
                     var23 = null;
                     break label152;
                  }

                  boolean enableTransparency = Config.Client.Advanced.Graphics.Quality.transparency.get() == EDhApiTransparency.COMPLETE;
                  LodQuadBuilder lodQuadBuilder = LodQuadBuilder.getBuilder(enableTransparency, this.clientLevel.getClientLevelWrapper());

                  try {
                     ColumnRenderSource northRenderSource = this.getRenderSourceForPos(this.pos, EDhDirection.NORTH);

                     try {
                        ColumnRenderSource southRenderSource = this.getRenderSourceForPos(this.pos, EDhDirection.SOUTH);

                        try {
                           ColumnRenderSource eastRenderSource = this.getRenderSourceForPos(this.pos, EDhDirection.EAST);

                           try {
                              ColumnRenderSource westRenderSource = this.getRenderSourceForPos(this.pos, EDhDirection.WEST);

                              try {
                                 ColumnRenderSource[] adjacentRenderSections = new ColumnRenderSource[EDhDirection.CARDINAL_COMPASS.length];
                                 adjacentRenderSections[EDhDirection.NORTH.compassIndex] = northRenderSource;
                                 adjacentRenderSections[EDhDirection.SOUTH.compassIndex] = southRenderSource;
                                 adjacentRenderSections[EDhDirection.EAST.compassIndex] = eastRenderSource;
                                 adjacentRenderSections[EDhDirection.WEST.compassIndex] = westRenderSource;
                                 boolean[] adjIsSameDetailLevel = new boolean[EDhDirection.CARDINAL_COMPASS.length];
                                 adjIsSameDetailLevel[EDhDirection.NORTH.compassIndex] = this.isAdjacentPosSameDetailLevel(EDhDirection.NORTH);
                                 adjIsSameDetailLevel[EDhDirection.SOUTH.compassIndex] = this.isAdjacentPosSameDetailLevel(EDhDirection.SOUTH);
                                 adjIsSameDetailLevel[EDhDirection.EAST.compassIndex] = this.isAdjacentPosSameDetailLevel(EDhDirection.EAST);
                                 adjIsSameDetailLevel[EDhDirection.WEST.compassIndex] = this.isAdjacentPosSameDetailLevel(EDhDirection.WEST);
                                 ColumnRenderBufferBuilder.makeLodRenderData(
                                    lodQuadBuilder, thisRenderSource, this.clientLevel, adjacentRenderSections, adjIsSameDetailLevel
                                 );
                                 var10 = lodQuadBuilder;
                              } catch (Throwable var16) {
                                 if (westRenderSource != null) {
                                    try {
                                       westRenderSource.close();
                                    } catch (Throwable var15) {
                                       var16.addSuppressed(var15);
                                    }
                                 }

                                 throw var16;
                              }

                              if (westRenderSource != null) {
                                 westRenderSource.close();
                              }
                           } catch (Throwable var17) {
                              if (eastRenderSource != null) {
                                 try {
                                    eastRenderSource.close();
                                 } catch (Throwable var14) {
                                    var17.addSuppressed(var14);
                                 }
                              }

                              throw var17;
                           }

                           if (eastRenderSource != null) {
                              eastRenderSource.close();
                           }
                        } catch (Throwable var18) {
                           if (southRenderSource != null) {
                              try {
                                 southRenderSource.close();
                              } catch (Throwable var13) {
                                 var18.addSuppressed(var13);
                              }
                           }

                           throw var18;
                        }

                        if (southRenderSource != null) {
                           southRenderSource.close();
                        }
                     } catch (Throwable var19) {
                        if (northRenderSource != null) {
                           try {
                              northRenderSource.close();
                           } catch (Throwable var12) {
                              var19.addSuppressed(var12);
                           }
                        }

                        throw var19;
                     }

                     if (northRenderSource != null) {
                        northRenderSource.close();
                     }
                  } catch (Exception var20) {
                     LOGGER.error(
                        "Unexpected error while loading LodRenderSection ["
                           + DhSectionPos.toString(this.pos)
                           + "] adjacent data, Error: ["
                           + var20.getMessage()
                           + "].",
                        var20
                     );
                     break label151;
                  }
               } catch (Throwable var21) {
                  if (thisRenderSource != null) {
                     try {
                        thisRenderSource.close();
                     } catch (Throwable var11) {
                        var21.addSuppressed(var11);
                     }
                  }

                  throw var21;
               }

               if (thisRenderSource != null) {
                  thisRenderSource.close();
               }

               return var10;
            }

            if (thisRenderSource != null) {
               thisRenderSource.close();
            }

            return (LodQuadBuilder)var23;
         }

         if (thisRenderSource != null) {
            thisRenderSource.close();
         }
      } catch (Exception var22) {
         LOGGER.error("Unexpected error while loading LodRenderSection [" + DhSectionPos.toString(this.pos) + "], Error: [" + var22.getMessage() + "].", var22);
      }

      return null;
   }

   private ColumnRenderSource getRenderSourceForPos(long pos, @Nullable EDhDirection direction) {
      if (direction != null) {
         pos = DhSectionPos.getAdjacentPos(pos, direction);
      }

      long finalPos = pos;

      try {
         FullDataSourceV2 fullDataSource = direction == null
            ? this.fullDataSourceProvider.get(finalPos)
            : this.fullDataSourceProvider.getAdjForDirection(finalPos, direction.opposite());

         ColumnRenderSource var8;
         try {
            ColumnRenderSource columnRenderSource = FullDataToRenderDataTransformer.transformFullDataToRenderSource(fullDataSource, this.levelWrapper);
            var8 = columnRenderSource;
         } catch (Throwable var10) {
            if (fullDataSource != null) {
               try {
                  fullDataSource.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (fullDataSource != null) {
            fullDataSource.close();
         }

         return var8;
      } catch (Exception var11) {
         LOGGER.error("Unexpected issue creating render data for pos: [" + DhSectionPos.toString(pos) + "], error: [" + var11.getMessage() + "].", var11);
         return null;
      }
   }

   private boolean isAdjacentPosSameDetailLevel(EDhDirection direction) {
      long adjPos = DhSectionPos.getAdjacentPos(this.pos, direction);
      byte detailLevel = this.quadTree.calcExpectedDetailLevel(new DhBlockPos2D(MC.getPlayerBlockPos()), adjPos);
      detailLevel = (byte)(detailLevel + 6);
      return detailLevel == DhSectionPos.getDetailLevel(this.pos);
   }

   private synchronized CompletableFuture<LodBufferContainer> uploadToGpuAsync(
      CompletableFuture<Void> parentFuture, ArrayList<ByteBuffer> opaqueBuffers, ArrayList<ByteBuffer> transparentBuffers
   ) {
      CompletableFuture<LodBufferContainer> uploadFuture = LodBufferContainer.tryMakeAndUploadBuffersAsync(
         this.pos, this.clientLevel, opaqueBuffers, transparentBuffers
      );
      uploadFuture.whenComplete((bufferContainer, e) -> {
         try {
            if (e != null) {
               if (!ExceptionUtil.isShutdownException(e)) {
                  LOGGER.error("Unexpected issue uploading buffers for pos: [" + DhSectionPos.toString(this.pos) + "], error: [" + e.getMessage() + "].", e);
               }

               if (bufferContainer != null) {
                  bufferContainer.close();
               }

               return;
            }

            LodBufferContainer oldContainer = this.renderBufferContainer;
            this.renderBufferContainer = bufferContainer.buffersUploaded ? bufferContainer : null;
            if (oldContainer != null) {
               oldContainer.close();
            }

            this.renderDataDirty = false;
            if (parentFuture.isCancelled()) {
               bufferContainer.close();
            }
         } catch (Exception var5) {
            LOGGER.error("Unexpected buffer finish exception: [" + var5.getMessage() + "]", var5);
         }
      });
      return uploadFuture;
   }

   public boolean canRender() {
      return this.renderBufferContainer != null;
   }

   public boolean gpuUploadComplete() {
      return this.renderBufferContainer != null && !this.renderDataDirty;
   }

   public boolean getRenderingEnabled() {
      return this.renderingEnabled;
   }

   public void setRenderingEnabled(boolean enabled) {
      this.renderingEnabled = enabled;
   }

   public boolean gpuUploadInProgress() {
      return this.getAndBuildRenderDataFutureRef.get() != null;
   }

   @Override
   public void debugRender(AbstractDebugWireframeRenderer debugRenderer) {
      Color color = Color.red;
      if (!this.renderingEnabled) {
         if (this.getAndBuildRenderDataFutureRef.get() != null) {
            color = Color.yellow;
         } else if (this.canRender()) {
            return;
         }

         int levelMinY = this.clientLevel.getLevelWrapper().getMinHeight();
         int levelMaxY = this.clientLevel.getLevelWrapper().getMaxHeight();
         int levelHeightRange = levelMaxY - levelMinY;
         int maxY = levelMaxY - levelHeightRange / 2;
         debugRenderer.renderBox(new AbstractDebugWireframeRenderer.Box(this.pos, levelMinY, maxY, 0.01F, color));
      }
   }

   @Override
   public String toString() {
      return "pos=["
         + DhSectionPos.toString(this.pos)
         + "] enabled=["
         + this.renderingEnabled
         + "] canRender=["
         + (this.renderBufferContainer != null)
         + "] uploading=["
         + this.gpuUploadInProgress()
         + "] ";
   }

   @Override
   public void close() {
      DEBUG_RENDERER.unregister(this, Config.Client.Advanced.Debugging.DebugWireframe.showRenderSectionStatus);
      if (Config.Client.Advanced.Debugging.DebugWireframe.showRenderSectionStatus.get()) {
         DEBUG_RENDERER.makeParticle(
            new AbstractDebugWireframeRenderer.BoxParticle(
               new AbstractDebugWireframeRenderer.Box(this.pos, 128.0F, 156.0F, 0.09F, Color.RED.darker()), 0.5, 32.0F
            )
         );
      }

      CompletableFuture<Void> buildFuture = this.getAndBuildRenderDataFutureRef.get();
      if (buildFuture != null) {
         PriorityTaskPicker.Executor renderLoaderExecutor = ThreadPoolUtil.getRenderLoadingExecutor();
         if (renderLoaderExecutor != null && !renderLoaderExecutor.isTerminated()) {
            Runnable runnable = this.getAndBuildRenderDataRunnable;
            if (runnable != null) {
               renderLoaderExecutor.remove(runnable);
            }
         }

         buildFuture.cancel(true);
      }

      this.setRenderingEnabled(false);
      if (this.renderBufferContainer != null) {
         this.renderBufferContainer.close();
      }
   }
}
