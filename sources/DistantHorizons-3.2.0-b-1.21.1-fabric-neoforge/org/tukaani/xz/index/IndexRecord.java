package org.tukaani.xz.index;

class IndexRecord {
   final long unpadded;
   final long uncompressed;

   IndexRecord(long l, long m) {
      this.unpadded = l;
      this.uncompressed = m;
   }
}
