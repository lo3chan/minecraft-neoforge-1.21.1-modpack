package com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteBufferCheckoutWrapper {
   public ByteBuffer buffer = null;
   public int size = -1;
   public ByteBuffer bufferSlice = null;

   public ByteBufferCheckoutWrapper() {
   }

   public ByteBufferCheckoutWrapper(int byteBufferSize) {
      this.clearAndSetSize(byteBufferSize);
   }

   public void clearAndSetSize(int newSize) {
      if (this.size != newSize) {
         if (this.size < newSize) {
            this.buffer = ByteBuffer.allocateDirect(newSize);
            this.buffer.order(ByteOrder.nativeOrder());
         }

         this.bufferSlice = this.buffer.duplicate();
         ((Buffer)this.bufferSlice).limit(this.buffer.capacity());
         ((Buffer)this.bufferSlice).position(0);
         ((Buffer)this.bufferSlice).limit(newSize);
         this.bufferSlice.order(ByteOrder.nativeOrder());
         this.size = newSize;
      }

      ((Buffer)this.buffer).rewind();
      ((Buffer)this.buffer).limit(this.size);
      ((Buffer)this.bufferSlice).rewind();
      ((Buffer)this.bufferSlice).limit(this.size);
   }
}
