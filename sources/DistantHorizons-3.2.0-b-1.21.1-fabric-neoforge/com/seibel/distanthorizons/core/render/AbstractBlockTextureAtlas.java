package com.seibel.distanthorizons.core.render;

import com.seibel.distanthorizons.core.dataObjects.render.textures.BlockTextureRegistry;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListCheckout;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListPool;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public abstract class AbstractBlockTextureAtlas {
   public static final int TILES_PER_ROW = 256;
   protected static final int ALLOCATION_ROW_COUNT = 16;
   protected static final PhantomArrayListPool ARRAY_LIST_POOL = new PhantomArrayListPool("BlockTextureAtlas");
   protected int allocatedTileCount = 0;
   protected int uploadedTileCount = 0;
   protected boolean atlasCreated = false;

   protected abstract void tryCreateOrResize(int i, int j);

   protected abstract void writeToTexture(ByteBuffer byteBuffer, int i, int j, int k, int l);

   protected abstract void beforeWriteToTexture();

   protected abstract void afterWriteToTexture();

   public void uploadPendingTiles() {
      if (!this.atlasCreated) {
         this.atlasCreated = true;
         this.growAtlas(1);
      }

      int totalTileCount = BlockTextureRegistry.INSTANCE.getTileCount();
      if (totalTileCount != this.uploadedTileCount) {
         if (totalTileCount > this.allocatedTileCount) {
            this.growAtlas(totalTileCount);
         }

         BlockTextureRegistry.PendingTiles pendingTiles = BlockTextureRegistry.INSTANCE.getAndClearPendingUploadTiles();
         if (pendingTiles != null) {
            PhantomArrayListCheckout checkout = ARRAY_LIST_POOL.checkoutByteBuffers(1);

            try {
               this.beforeWriteToTexture();
               ByteBuffer pixelBuffer = checkout.getByteBuffer(0, 1024);

               for (int i = 0; i < pendingTiles.tilePixels.length; i++) {
                  ((Buffer)pixelBuffer).clear();
                  pixelBuffer.put(pendingTiles.tilePixels[i]);
                  ((Buffer)pixelBuffer).flip();
                  int tileId = pendingTiles.firstTileId + i;
                  int destinationX = tileId % 256 * 16;
                  int destinationY = tileId / 256 * 16;
                  this.writeToTexture(pixelBuffer, destinationX, destinationY, 16, 16);
               }

               this.afterWriteToTexture();
            } catch (Throwable var10) {
               if (checkout != null) {
                  try {
                     checkout.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (checkout != null) {
               checkout.close();
            }

            this.uploadedTileCount = pendingTiles.firstTileId + pendingTiles.tilePixels.length;
         }
      }
   }

   private void growAtlas(int minTileCount) {
      int newRowCount = Math.max(this.allocatedTileCount / 256, 16);

      while (newRowCount * 256 < minTileCount) {
         newRowCount *= 2;
      }

      int width = 4096;
      int height = newRowCount * 16;
      this.tryCreateOrResize(width, height);
      this.allocatedTileCount = newRowCount * 256;
      this.uploadedTileCount = 0;
      BlockTextureRegistry.INSTANCE.resetPendingUploads();
   }
}
