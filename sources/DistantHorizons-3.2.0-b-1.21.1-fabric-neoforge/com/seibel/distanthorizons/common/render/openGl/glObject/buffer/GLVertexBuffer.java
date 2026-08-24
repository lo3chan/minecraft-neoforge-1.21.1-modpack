package com.seibel.distanthorizons.common.render.openGl.glObject.buffer;

import com.seibel.distanthorizons.api.enums.config.EDhApiGpuUploadMethod;
import com.seibel.distanthorizons.common.render.openGl.glObject.GLProxy;
import com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding.IndexBufferBuilder;
import com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding.LodBufferContainer;
import com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding.LodQuadBuilder;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.render.RenderThreadTaskHandler;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListCheckout;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.AbstractDhRenderApiDefinition;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.objects.IVertexBufferWrapper;
import java.nio.ByteBuffer;

public class GLVertexBuffer extends GLBuffer implements IVertexBufferWrapper {
   private static final AbstractDhRenderApiDefinition RENDER_DEF = SingletonInjector.INSTANCE.get(AbstractDhRenderApiDefinition.class);
   protected int vertexCount = 0;
   private GlQuadIndexBuffer quadIBO = null;
   private static GlQuadIndexBuffer GLOBAL_QUAD_IBO = null;

   public int getVertexCount() {
      return this.vertexCount;
   }

   public GlQuadIndexBuffer getQuadIBO() {
      return RENDER_DEF.useSingleIbo() ? GLOBAL_QUAD_IBO : this.quadIBO;
   }

   public GLVertexBuffer() {
      this(GLProxy.getInstance().getGpuUploadMethod() == EDhApiGpuUploadMethod.BUFFER_STORAGE);
   }

   public GLVertexBuffer(boolean isBufferStorage) {
      super(isBufferStorage);
   }

   @Override
   public int getBufferBindingTarget() {
      return 34962;
   }

   @Override
   public void uploadVertexBuffer(ByteBuffer buffer, int vertexCount) {
      EDhApiGpuUploadMethod uploadMethod = GLProxy.getInstance().getGpuUploadMethod();
      int maxBufferSize = LodQuadBuilder.getMaxBufferByteSize();
      this.uploadBuffer(buffer, vertexCount, uploadMethod, maxBufferSize);
   }

   public void uploadBuffer(ByteBuffer byteBuffer, int vertexCount, EDhApiGpuUploadMethod uploadMethod, int maxExpansionSize) {
      if (vertexCount < 0) {
         throw new IllegalArgumentException("vertexCount is negative!");
      } else {
         if (byteBuffer.limit() - byteBuffer.position() != 0) {
            super.uploadBuffer(byteBuffer, uploadMethod, maxExpansionSize, uploadMethod.useBufferStorage ? 0 : '裤');
         }

         this.vertexCount = vertexCount;
      }
   }

   @Override
   public void uploadIndexBuffer(ByteBuffer buffer, int vertexCount) {
      if (!RENDER_DEF.useSingleIbo()) {
         if (vertexCount != 0) {
            if (this.quadIBO != null) {
               this.quadIBO.close();
            }

            this.quadIBO = new GlQuadIndexBuffer();
            int quadCount = vertexCount / 4;
            this.quadIBO.upload(buffer, quadCount);
         }
      }
   }

   @Override
   public void close() {
      this.destroyAsync();
   }

   @Override
   public void destroyAsync() {
      super.destroyAsync();
      if (this.quadIBO != null) {
         this.quadIBO.destroyAsync();
      }

      this.vertexCount = 0;
   }

   static {
      if (RENDER_DEF.useSingleIbo()) {
         RenderThreadTaskHandler.INSTANCE.queueRunningOnRenderThread("Global IBO Creation", () -> {
            try (PhantomArrayListCheckout checkout = LodBufferContainer.ARRAY_LIST_POOL.checkoutByteBuffers(1)) {
               GLOBAL_QUAD_IBO = new GlQuadIndexBuffer();
               int maxSize = LodQuadBuilder.getMaxBufferByteSize();
               int maxVertexCount = maxSize / 16;
               int maxQuadCount = maxVertexCount / 4;
               ByteBuffer buffer = IndexBufferBuilder.populateBuffer(checkout, 0, maxQuadCount);
               GLOBAL_QUAD_IBO.upload(buffer, maxQuadCount);
            }
         });
      }
   }
}
