package com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public final class PhantomArrayListPoolStatTracker<T> {
   public final String typeName;
   public final long elementSizeInBytes;
   private final Supplier<T> emptyListCreatorFunc;
   public final AtomicInteger totalArrayCountRef = new AtomicInteger(0);
   public long lastPoolSizeInBytes = -1L;
   public int lastPoolCount = 0;
   private long pendingPoolByteSize = 0L;
   private int pendingPoolCount = 0;

   public PhantomArrayListPoolStatTracker(String typeName, long elementSizeInBytes, Supplier<T> emptyListCreatorFunc) {
      this.typeName = typeName;
      this.elementSizeInBytes = elementSizeInBytes;
      this.emptyListCreatorFunc = emptyListCreatorFunc;
   }

   public void fillCheckout(int requestedArrayCount, IntSupplier getCheckoutExistingArrayCountFunc, Consumer<T> addArrayToCheckoutFunc) {
      int alreadyCreatedArrayCount = getCheckoutExistingArrayCountFunc.getAsInt();

      for (int i = alreadyCreatedArrayCount; i < requestedArrayCount; i++) {
         T newList = this.emptyListCreatorFunc.get();
         addArrayToCheckoutFunc.accept(newList);
      }
   }

   public void debugAddPoolByteSize(Iterable<T> lists) {
      for (T list : lists) {
         long elementCount = this.getBackingElementCount(list);
         this.pendingPoolByteSize = this.pendingPoolByteSize + elementCount * this.elementSizeInBytes;
         this.pendingPoolCount++;
      }
   }

   private long getBackingElementCount(@NotNull T list) {
      if (list instanceof ByteArrayList) {
         return ((ByteArrayList)list).elements().length;
      } else if (list instanceof ShortArrayList) {
         return ((ShortArrayList)list).elements().length;
      } else if (list instanceof LongArrayList) {
         return ((LongArrayList)list).elements().length;
      } else if (list instanceof CharArrayList) {
         return ((CharArrayList)list).elements().length;
      } else if (list instanceof ByteBufferCheckoutWrapper) {
         return ((ByteBufferCheckoutWrapper)list).size;
      } else {
         throw new UnsupportedOperationException("getBackingElementCount not implemented for type [" + list.getClass().getSimpleName() + "].");
      }
   }

   public void updateDebugValues(boolean clearLastPoolSize) {
      if (clearLastPoolSize) {
         this.lastPoolSizeInBytes = 0L;
      }

      this.lastPoolSizeInBytes = Math.max(this.pendingPoolByteSize, this.lastPoolSizeInBytes);
      this.lastPoolCount = this.pendingPoolCount;
      this.pendingPoolByteSize = 0L;
      this.pendingPoolCount = 0;
   }
}
