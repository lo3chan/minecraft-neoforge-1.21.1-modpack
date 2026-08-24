package dhcomgithubluben.zstd;

import dhcomgithubluben.zstd.util.Native;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ZstdCompressCtx extends AutoCloseBase {
   private long nativePtr = 0L;
   private ZstdDictCompress compression_dict = null;
   private SequenceProducer seqprod = null;
   private long seqprod_state = 0L;

   private static native long init();

   private static native void free(long l);

   public ZstdCompressCtx() {
      this.nativePtr = init();
      if (0L == this.nativePtr) {
         throw new IllegalStateException("ZSTD_createCompressCtx failed");
      } else {
         this.storeFence();
      }
   }

   @Override
   void doClose() {
      if (this.nativePtr != 0L) {
         free(this.nativePtr);
         this.nativePtr = 0L;
         if (this.seqprod != null) {
            this.seqprod.freeState(this.seqprod_state);
            this.seqprod = null;
         }
      }
   }

   private void ensureOpen() {
      if (this.nativePtr == 0L) {
         throw new IllegalStateException("Compression context is closed");
      }
   }

   public ZstdCompressCtx setLevel(int i) {
      this.ensureOpen();
      this.acquireSharedLock();
      setLevel0(this.nativePtr, i);
      this.releaseSharedLock();
      return this;
   }

   private static native void setLevel0(long l, int i);

   public ZstdCompressCtx setMagicless(boolean bl) {
      this.ensureOpen();
      this.acquireSharedLock();
      Zstd.setCompressionMagicless(this.nativePtr, bl);
      this.releaseSharedLock();
      return this;
   }

   public ZstdCompressCtx setChecksum(boolean bl) {
      this.ensureOpen();
      this.acquireSharedLock();
      setChecksum0(this.nativePtr, bl);
      this.releaseSharedLock();
      return this;
   }

   private static native void setChecksum0(long l, boolean bl);

   public ZstdCompressCtx setWorkers(int i) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionWorkers(this.nativePtr, i);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setOverlapLog(int i) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionOverlapLog(this.nativePtr, i);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setJobSize(int i) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionJobSize(this.nativePtr, i);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setTargetLength(int i) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionTargetLength(this.nativePtr, i);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setMinMatch(int i) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionMinMatch(this.nativePtr, i);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setSearchLog(int i) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionSearchLog(this.nativePtr, i);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setChainLog(int i) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionChainLog(this.nativePtr, i);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setHashLog(int i) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionHashLog(this.nativePtr, i);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setWindowLog(int i) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionWindowLog(this.nativePtr, i);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setStrategy(int i) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionStrategy(this.nativePtr, i);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setContentSize(boolean bl) {
      this.ensureOpen();
      this.acquireSharedLock();
      setContentSize0(this.nativePtr, bl);
      this.releaseSharedLock();
      return this;
   }

   private static native void setContentSize0(long l, boolean bl);

   public ZstdCompressCtx setDictID(boolean bl) {
      this.ensureOpen();
      this.acquireSharedLock();
      setDictID0(this.nativePtr, bl);
      this.releaseSharedLock();
      return this;
   }

   private static native void setDictID0(long l, boolean bl);

   public ZstdCompressCtx setLong(int i) {
      this.ensureOpen();
      this.acquireSharedLock();
      Zstd.setCompressionLong(this.nativePtr, i);
      this.releaseSharedLock();
      return this;
   }

   public ZstdCompressCtx registerSequenceProducer(SequenceProducer sequenceProducer) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         if (this.seqprod != null) {
            this.seqprod.freeState(this.seqprod_state);
            this.seqprod = null;
         }

         if (sequenceProducer == null) {
            Zstd.registerSequenceProducer(this.nativePtr, 0L, 0L);
         } else {
            this.seqprod_state = sequenceProducer.createState();
            Zstd.registerSequenceProducer(this.nativePtr, this.seqprod_state, sequenceProducer.getFunctionPointer());
            this.seqprod = sequenceProducer;
         }
      } catch (Exception var6) {
         this.seqprod = null;
         Zstd.registerSequenceProducer(this.nativePtr, 0L, 0L);
         throw var6;
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setSequenceProducerFallback(boolean bl) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setSequenceProducerFallback(this.nativePtr, bl);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setSearchForExternalRepcodes(Zstd.ParamSwitch paramSwitch) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setSearchForExternalRepcodes(this.nativePtr, paramSwitch.getValue());
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setValidateSequences(Zstd.ParamSwitch paramSwitch) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setValidateSequences(this.nativePtr, paramSwitch.getValue());
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setEnableLongDistanceMatching(Zstd.ParamSwitch paramSwitch) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setEnableLongDistanceMatching(this.nativePtr, paramSwitch.getValue());
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   long getNativePtr() {
      return this.nativePtr;
   }

   public ZstdCompressCtx loadDict(ZstdDictCompress zstdDictCompress) {
      this.ensureOpen();
      this.acquireSharedLock();
      zstdDictCompress.acquireSharedLock();

      try {
         long var2 = this.loadCDictFast0(this.nativePtr, zstdDictCompress);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }

         this.compression_dict = zstdDictCompress;
      } finally {
         zstdDictCompress.releaseSharedLock();
         this.releaseSharedLock();
      }

      return this;
   }

   private native long loadCDictFast0(long l, ZstdDictCompress zstdDictCompress);

   public ZstdCompressCtx loadDict(byte[] bs) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = this.loadCDict0(this.nativePtr, bs);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }

         this.compression_dict = null;
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   private native long loadCDict0(long l, byte[] bs);

   public ZstdFrameProgression getFrameProgression() {
      this.ensureOpen();
      this.acquireSharedLock();

      ZstdFrameProgression var1;
      try {
         var1 = getFrameProgression0(this.nativePtr);
      } finally {
         this.releaseSharedLock();
      }

      return var1;
   }

   private static native ZstdFrameProgression getFrameProgression0(long l);

   public void reset() {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var1 = reset0(this.nativePtr);
         if (Zstd.isError(var1)) {
            throw new ZstdException(var1);
         }
      } finally {
         this.releaseSharedLock();
      }
   }

   private static native long reset0(long l);

   public void setPledgedSrcSize(long l) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var3 = setPledgedSrcSize0(this.nativePtr, l);
         if (Zstd.isError(var3)) {
            throw new ZstdException(var3);
         }
      } finally {
         this.releaseSharedLock();
      }
   }

   private static native long setPledgedSrcSize0(long l, long m);

   public boolean compressDirectByteBufferStream(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, EndDirective endDirective) {
      this.ensureOpen();
      this.acquireSharedLock();

      boolean var6;
      try {
         long var4 = compressDirectByteBufferStream0(
            this.nativePtr,
            byteBuffer,
            byteBuffer.position(),
            byteBuffer.limit(),
            byteBuffer2,
            byteBuffer2.position(),
            byteBuffer2.limit(),
            endDirective.value()
         );
         if ((var4 & 2147483648L) != 0L) {
            long var11 = -(var4 & 255L);
            throw new ZstdException(var11, Zstd.getErrorName(var11));
         }

         ((Buffer)byteBuffer2).position((int)(var4 & 2147483647L));
         ((Buffer)byteBuffer).position((int)(var4 >>> 32) & 2147483647);
         var6 = var4 >>> 63 == 1L;
      } finally {
         this.releaseSharedLock();
      }

      return var6;
   }

   private static native long compressDirectByteBufferStream0(long l, ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int m, int n);

   public int compressDirectByteBuffer(ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int l) {
      this.ensureOpen();
      if (!byteBuffer2.isDirect()) {
         throw new IllegalArgumentException("srcBuff must be a direct buffer");
      } else if (!byteBuffer.isDirect()) {
         throw new IllegalArgumentException("dstBuff must be a direct buffer");
      } else {
         Objects.checkFromIndexSize(k, l, byteBuffer2.limit());
         Objects.checkFromIndexSize(i, j, byteBuffer.limit());
         this.acquireSharedLock();

         int var9;
         try {
            long var7 = compressDirectByteBuffer0(this.nativePtr, byteBuffer, i, j, byteBuffer2, k, l);
            if (Zstd.isError(var7)) {
               throw new ZstdException(var7);
            }

            if (var7 > 2147483647L) {
               throw new ZstdException(Zstd.errGeneric(), "Output size is greater than MAX_INT");
            }

            var9 = (int)var7;
         } finally {
            this.releaseSharedLock();
         }

         return var9;
      }
   }

   private static native long compressDirectByteBuffer0(long l, ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int m);

   public int compressByteArray(byte[] bs, int i, int j, byte[] cs, int k, int l) {
      Objects.checkFromIndexSize(k, l, cs.length);
      Objects.checkFromIndexSize(i, j, bs.length);
      this.ensureOpen();
      this.acquireSharedLock();

      int var9;
      try {
         long var7 = compressByteArray0(this.nativePtr, bs, i, j, cs, k, l);
         if (Zstd.isError(var7)) {
            throw new ZstdException(var7);
         }

         if (var7 > 2147483647L) {
            throw new ZstdException(Zstd.errGeneric(), "Output size is greater than MAX_INT");
         }

         var9 = (int)var7;
      } finally {
         this.releaseSharedLock();
      }

      return var9;
   }

   private static native long compressByteArray0(long l, byte[] bs, int i, int j, byte[] cs, int k, int m);

   public int compress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
      int var3 = this.compressDirectByteBuffer(
         byteBuffer,
         byteBuffer.position(),
         byteBuffer.limit() - byteBuffer.position(),
         byteBuffer2,
         byteBuffer2.position(),
         byteBuffer2.limit() - byteBuffer2.position()
      );
      ((Buffer)byteBuffer2).position(byteBuffer2.limit());
      ((Buffer)byteBuffer).position(byteBuffer.position() + var3);
      return var3;
   }

   public ByteBuffer compress(ByteBuffer byteBuffer) throws ZstdException {
      long var2 = Zstd.compressBound(byteBuffer.limit() - byteBuffer.position());
      if (var2 > 2147483647L) {
         throw new ZstdException(Zstd.errGeneric(), "Max output size is greater than MAX_INT");
      } else {
         ByteBuffer var4 = ByteBuffer.allocateDirect((int)var2);
         int var5 = this.compressDirectByteBuffer(var4, 0, (int)var2, byteBuffer, byteBuffer.position(), byteBuffer.limit() - byteBuffer.position());
         ((Buffer)byteBuffer).position(byteBuffer.limit());
         ((Buffer)var4).limit(var5);
         return var4;
      }
   }

   public int compress(byte[] bs, byte[] cs) {
      return this.compressByteArray(bs, 0, bs.length, cs, 0, cs.length);
   }

   public byte[] compress(byte[] bs) {
      long var2 = Zstd.compressBound(bs.length);
      if (var2 > 2147483647L) {
         throw new ZstdException(Zstd.errGeneric(), "Max output size is greater than MAX_INT");
      } else {
         byte[] var4 = new byte[(int)var2];
         int var5 = this.compressByteArray(var4, 0, var4.length, bs, 0, bs.length);
         return Arrays.copyOfRange(var4, 0, var5);
      }
   }

   static {
      Native.load();
   }
}
