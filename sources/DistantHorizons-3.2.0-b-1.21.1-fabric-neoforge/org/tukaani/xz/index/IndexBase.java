package org.tukaani.xz.index;

import org.tukaani.xz.XZIOException;
import org.tukaani.xz.common.Util;

abstract class IndexBase {
   private final XZIOException invalidIndexException;
   long blocksSum = 0L;
   long uncompressedSum = 0L;
   long indexListSize = 0L;
   long recordCount = 0L;

   IndexBase(XZIOException xZIOException) {
      this.invalidIndexException = xZIOException;
   }

   private long getUnpaddedIndexSize() {
      return 1 + Util.getVLISize(this.recordCount) + this.indexListSize + 4L;
   }

   public long getIndexSize() {
      return this.getUnpaddedIndexSize() + 3L & -4L;
   }

   public long getStreamSize() {
      return 12L + this.blocksSum + this.getIndexSize() + 12L;
   }

   int getIndexPaddingSize() {
      return (int)(4L - this.getUnpaddedIndexSize() & 3L);
   }

   void add(long l, long m) throws XZIOException {
      this.blocksSum += l + 3L & -4L;
      this.uncompressedSum += m;
      this.indexListSize = this.indexListSize + (Util.getVLISize(l) + Util.getVLISize(m));
      this.recordCount++;
      if (this.blocksSum < 0L || this.uncompressedSum < 0L || this.getIndexSize() > 17179869184L || this.getStreamSize() < 0L) {
         throw this.invalidIndexException;
      }
   }
}
