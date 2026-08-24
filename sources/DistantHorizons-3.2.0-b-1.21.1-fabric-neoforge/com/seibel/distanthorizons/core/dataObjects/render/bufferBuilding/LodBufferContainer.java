package com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding;

import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.level.IDhClientLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.render.RenderThreadTaskHandler;
import com.seibel.distanthorizons.core.util.ExceptionUtil;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListCheckout;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListPool;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.AbstractDhRenderApiDefinition;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.objects.ILodContainerUniformBufferWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.objects.IVertexBufferWrapper;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;

public class LodBufferContainer implements AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IWrapperFactory WRAPPER_FACTORY = SingletonInjector.INSTANCE.get(IWrapperFactory.class);
   private static final AbstractDhRenderApiDefinition RENDER_DEF = SingletonInjector.INSTANCE.get(AbstractDhRenderApiDefinition.class);
   public static final PhantomArrayListPool ARRAY_LIST_POOL = new PhantomArrayListPool("LodBufferContainer");
   public final DhBlockPos minCornerBlockPos;
   public final long pos;
   public boolean buffersUploaded = false;
   public IVertexBufferWrapper[] vboOpaqueWrappers;
   public IVertexBufferWrapper[] vboTransparentWrappers;
   public ILodContainerUniformBufferWrapper uniformContainer = WRAPPER_FACTORY.createLodContainerUniformWrapper();

   private LodBufferContainer(long pos, DhBlockPos minCornerBlockPos) {
      this.pos = pos;
      this.minCornerBlockPos = minCornerBlockPos;
      this.vboOpaqueWrappers = new IVertexBufferWrapper[0];
      this.vboTransparentWrappers = new IVertexBufferWrapper[0];
   }

   public static CompletableFuture<LodBufferContainer> tryMakeAndUploadBuffersAsync(
      long pos, IDhClientLevel clientLevel, ArrayList<ByteBuffer> opaqueBuffers, ArrayList<ByteBuffer> transparentBuffers
   ) {
      CompletableFuture<LodBufferContainer> future = new CompletableFuture<>();
      DhBlockPos minCornerBlockPos = new DhBlockPos(
         DhSectionPos.getMinCornerBlockX(pos), clientLevel.getLevelWrapper().getMinHeight(), DhSectionPos.getMinCornerBlockZ(pos)
      );
      LodBufferContainer bufferContainer = new LodBufferContainer(pos, minCornerBlockPos);
      bufferContainer.vboOpaqueWrappers = resizeWrapperArray(bufferContainer.vboOpaqueWrappers, opaqueBuffers.size());
      bufferContainer.vboTransparentWrappers = resizeWrapperArray(bufferContainer.vboTransparentWrappers, transparentBuffers.size());
      PhantomArrayListCheckout opaqueIndexCheckout = ARRAY_LIST_POOL.checkoutByteBuffers(opaqueBuffers.size());
      PhantomArrayListCheckout transparentIndexCheckout = ARRAY_LIST_POOL.checkoutByteBuffers(transparentBuffers.size());
      future.thenRun(() -> {
         opaqueIndexCheckout.close();
         transparentIndexCheckout.close();
      });
      boolean useSingleIbo = RENDER_DEF.useSingleIbo();
      ArrayList<ByteBuffer> opaqueIndexBuffers = useSingleIbo ? null : bufferContainer.createIndexBuffers(opaqueIndexCheckout, opaqueBuffers);
      ArrayList<ByteBuffer> transparentIndexBuffers = useSingleIbo ? null : bufferContainer.createIndexBuffers(transparentIndexCheckout, transparentBuffers);
      CompletableFuture<Void> createFuture = new CompletableFuture<>();
      RenderThreadTaskHandler.INSTANCE
         .queueRunningOnRenderThread(
            "LodBufferContainer Setup",
            () -> {
               try {
                  if (Thread.interrupted() || future.isCancelled()) {
                     throw new InterruptedException();
                  }

                  createBufferWrappers(bufferContainer.vboOpaqueWrappers, opaqueBuffers);
                  createBufferWrappers(bufferContainer.vboTransparentWrappers, transparentBuffers);
                  createFuture.complete(null);
               } catch (Exception var6x) {
                  if (!ExceptionUtil.isShutdownException(var6x)) {
                     LOGGER.error(
                        "Unexpected issue creating buffers for pos: [" + DhSectionPos.toString(bufferContainer.pos) + "], error: [" + var6x.getMessage() + "].",
                        var6x
                     );
                  }

                  bufferContainer.close();
                  createFuture.completeExceptionally(var6x);
               }
            }
         );
      createFuture.exceptionally(e -> {
         if (!ExceptionUtil.isShutdownException(e)) {
            LOGGER.error("Unexpected issue creating buffer [" + bufferContainer.minCornerBlockPos + "], error: [" + e.getMessage() + "].", e);
         }

         bufferContainer.close();
         future.completeExceptionally(e);
         return null;
      });
      createFuture.thenRun(
         () -> {
            CompletableFuture<Void> opaqueFuture = uploadBuffersAsync(future, bufferContainer.vboOpaqueWrappers, opaqueBuffers, opaqueIndexBuffers);
            CompletableFuture<Void> transparentFuture = uploadBuffersAsync(
               future, bufferContainer.vboTransparentWrappers, transparentBuffers, transparentIndexBuffers
            );
            CompletableFuture<Void> uploadFuture = CompletableFuture.allOf(opaqueFuture, transparentFuture);
            uploadFuture.exceptionally(e -> {
               if (!ExceptionUtil.isShutdownException(e)) {
                  LOGGER.error("Unexpected issue uploading buffer [" + bufferContainer.minCornerBlockPos + "], error: [" + e.getMessage() + "].", e);
               }

               bufferContainer.close();
               future.completeExceptionally(e);
               return null;
            });
            uploadFuture.thenRun(() -> {
               bufferContainer.buffersUploaded = true;
               future.complete(bufferContainer);
            });
         }
      );
      return future;
   }

   private ArrayList<ByteBuffer> createIndexBuffers(PhantomArrayListCheckout checkout, ArrayList<ByteBuffer> vertexBuffers) {
      ArrayList<ByteBuffer> indexBuffers = new ArrayList<>();

      for (int i = 0; i < vertexBuffers.size(); i++) {
         ByteBuffer vertexBuffer = vertexBuffers.get(i);
         int size = vertexBuffer.limit() - vertexBuffer.position();
         int maxVertexCount = size / 16;
         int quadCount = maxVertexCount / 4;
         ByteBuffer indexBuffer = IndexBufferBuilder.populateBuffer(checkout, i, quadCount);
         indexBuffers.add(indexBuffer);
      }

      return indexBuffers;
   }

   private static IVertexBufferWrapper[] resizeWrapperArray(IVertexBufferWrapper[] vbos, int newSize) {
      if (vbos.length == newSize) {
         return vbos;
      } else {
         IVertexBufferWrapper[] newVbos = new IVertexBufferWrapper[newSize];
         System.arraycopy(vbos, 0, newVbos, 0, Math.min(vbos.length, newSize));
         if (newSize < vbos.length) {
            for (int i = newSize; i < vbos.length; i++) {
               if (vbos[i] != null) {
                  vbos[i].close();
               }
            }
         }

         return newVbos;
      }
   }

   private static void createBufferWrappers(IVertexBufferWrapper[] vboWrappers, ArrayList<ByteBuffer> vertexBuffers) {
      for (int i = 0; i < vertexBuffers.size(); i++) {
         if (i >= vboWrappers.length) {
            throw new RuntimeException("Too many vertex buffers!!");
         }

         if (vboWrappers[i] == null) {
            vboWrappers[i] = WRAPPER_FACTORY.createVboWrapper("distantHorizons:TerrainRenderer");
         }
      }
   }

   private static CompletableFuture<Void> uploadBuffersAsync(
      CompletableFuture<LodBufferContainer> parentFuture,
      IVertexBufferWrapper[] vboWrappers,
      ArrayList<ByteBuffer> vertexBuffers,
      @Nullable ArrayList<ByteBuffer> indexBuffers
   ) {
      ArrayList<CompletableFuture<Void>> uploadFutureList = new ArrayList<>();
      int vboIndex = 0;

      for (int i = 0; i < vertexBuffers.size(); i++) {
         if (vboIndex >= vboWrappers.length) {
            throw new RuntimeException("Too many vertex buffers!!");
         }

         IVertexBufferWrapper finalVboWrapper = vboWrappers[vboIndex];
         ByteBuffer finalVertexBuffer = vertexBuffers.get(vboIndex);
         ByteBuffer finalIndexBuffer = indexBuffers != null ? indexBuffers.get(vboIndex) : null;
         int finalVertexCount = vertexByteBufferToVertexCount(finalVertexBuffer);
         CompletableFuture<Void> vertexUploadFuture = new CompletableFuture<>();
         uploadFutureList.add(vertexUploadFuture);
         RenderThreadTaskHandler.INSTANCE.queueRunningOnRenderThread("LodBufferContainer VBO Upload", () -> {
            try {
               if (Thread.interrupted() || parentFuture.isCancelled()) {
                  throw new InterruptedException();
               }

               finalVboWrapper.uploadVertexBuffer(finalVertexBuffer, finalVertexCount);
               vertexUploadFuture.complete(null);
            } catch (Exception var6x) {
               LOGGER.error("Failed to upload buffer. Error: [" + var6x.getMessage() + "].", var6x);
               vertexUploadFuture.completeExceptionally(var6x);
            }
         });
         if (finalIndexBuffer != null) {
            CompletableFuture<Void> indexUploadFuture = new CompletableFuture<>();
            uploadFutureList.add(indexUploadFuture);
            RenderThreadTaskHandler.INSTANCE.queueRunningOnRenderThread("LodBufferContainer IBO Upload", () -> {
               try {
                  if (Thread.interrupted() || parentFuture.isCancelled()) {
                     throw new InterruptedException();
                  }

                  finalVboWrapper.uploadIndexBuffer(finalIndexBuffer, finalVertexCount);
                  indexUploadFuture.complete(null);
               } catch (Exception var6x) {
                  finalVboWrapper.close();
                  indexUploadFuture.completeExceptionally(var6x);
               }
            });
         }

         vboIndex++;
      }

      if (vboIndex < vboWrappers.length) {
         throw new RuntimeException("Too few vertex buffers!!");
      } else {
         CompletableFuture<?>[] futureArray = new CompletableFuture[uploadFutureList.size()];

         for (int i = 0; i < uploadFutureList.size(); i++) {
            futureArray[i] = uploadFutureList.get(i);
         }

         return CompletableFuture.allOf(futureArray);
      }
   }

   private static int vertexByteBufferToVertexCount(ByteBuffer buffer) {
      int size = buffer.limit() - buffer.position();
      return size / 16;
   }

   public boolean hasNonNullVbos() {
      return this.vboOpaqueWrappers != null || this.vboTransparentWrappers != null;
   }

   public int vboBufferCount() {
      int count = 0;
      if (this.vboOpaqueWrappers != null) {
         count += this.vboOpaqueWrappers.length;
      }

      if (this.vboTransparentWrappers != null) {
         count += this.vboTransparentWrappers.length;
      }

      return count;
   }

   @Override
   public void close() {
      this.buffersUploaded = false;
      RenderThreadTaskHandler.INSTANCE.queueRunningOnRenderThread("LodBufferContainer Close", () -> {
         tryCloseBufferWrapperArray(this.vboOpaqueWrappers);
         tryCloseBufferWrapperArray(this.vboTransparentWrappers);
         this.uniformContainer.close();
      });
   }

   private static void tryCloseBufferWrapperArray(@Nullable IVertexBufferWrapper[] bufferWrappers) {
      if (bufferWrappers != null) {
         for (int i = 0; i < bufferWrappers.length; i++) {
            IVertexBufferWrapper buffer = bufferWrappers[i];
            bufferWrappers[i] = null;
            if (buffer != null) {
               buffer.close();
            }
         }
      }
   }
}
