package com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.lang.ref.PhantomReference;

public abstract class AbstractPhantomArrayList implements AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private final PhantomArrayListPool phantomArrayListPool;
   private final PhantomReference<AbstractPhantomArrayList> phantomReference;
   protected final PhantomArrayListCheckout pooledArraysCheckout;

   public AbstractPhantomArrayList(
      PhantomArrayListPool phantomArrayListPool, int byteArrayCount, int shortArrayCount, int longArrayCount, int charArrayCount, int byteBufferCount
   ) {
      if (byteArrayCount >= 0 && shortArrayCount >= 0 && longArrayCount >= 0 && charArrayCount >= 0 && byteBufferCount >= 0) {
         this.phantomArrayListPool = phantomArrayListPool;
         this.phantomReference = new PhantomReference<>(this, this.phantomArrayListPool.phantomRefQueue);
         this.pooledArraysCheckout = this.phantomArrayListPool.checkoutArrays(byteArrayCount, shortArrayCount, longArrayCount, charArrayCount, byteBufferCount);
         this.phantomArrayListPool.phantomRefToCheckout.put(this.phantomReference, this.pooledArraysCheckout);
      } else {
         throw new IllegalArgumentException("Can't get a negative number of pooled arrays.");
      }
   }

   @Override
   public void close() {
      this.phantomArrayListPool.returnParentPhantomRef(this.phantomReference);
   }
}
