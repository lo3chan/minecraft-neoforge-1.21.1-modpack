package dhcomgithubluben.zstd;

import dhcomgithubluben.zstd.util.Native;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Zstd {
   public static long compress(byte[] bs, byte[] cs, int i, boolean bl) {
      ZstdCompressCtx var4 = new ZstdCompressCtx();

      long var5;
      try {
         var4.setLevel(i);
         var4.setChecksum(bl);
         var5 = var4.compress(bs, cs);
      } finally {
         var4.close();
      }

      return var5;
   }

   public static long compress(byte[] bs, byte[] cs, int i) {
      return compress(bs, cs, i, false);
   }

   public static long compressByteArray(byte[] bs, int i, int j, byte[] cs, int k, int l, int m, boolean bl) {
      ZstdCompressCtx var8 = new ZstdCompressCtx();

      long var9;
      try {
         var8.setLevel(m);
         var8.setChecksum(bl);
         var9 = var8.compressByteArray(bs, i, j, cs, k, l);
      } finally {
         var8.close();
      }

      return var9;
   }

   public static long compressByteArray(byte[] bs, int i, int j, byte[] cs, int k, int l, int m) {
      return compressByteArray(bs, i, j, cs, k, l, m, false);
   }

   public static long compressDirectByteBuffer(ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int l, int m, boolean bl) {
      ZstdCompressCtx var8 = new ZstdCompressCtx();

      long var9;
      try {
         var8.setLevel(m);
         var8.setChecksum(bl);
         var9 = var8.compressDirectByteBuffer(byteBuffer, i, j, byteBuffer2, k, l);
      } finally {
         var8.close();
      }

      return var9;
   }

   public static long compressDirectByteBuffer(ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int l, int m) {
      return compressDirectByteBuffer(byteBuffer, i, j, byteBuffer2, k, l, m, false);
   }

   public static native long compressUnsafe(long l, long m, long n, long o, int i, boolean bl);

   public static long compressUnsafe(long l, long m, long n, long o, int i) {
      return compressUnsafe(l, m, n, o, i, false);
   }

   public static long compressUsingDict(byte[] bs, int i, byte[] cs, int j, int k, byte[] ds, int l) {
      ZstdCompressCtx var7 = new ZstdCompressCtx();

      long var8;
      try {
         var7.setLevel(l);
         var7.loadDict(ds);
         var8 = var7.compressByteArray(bs, i, bs.length - i, cs, j, k);
      } finally {
         var7.close();
      }

      return var8;
   }

   public static long compressUsingDict(byte[] bs, int i, byte[] cs, int j, byte[] ds, int k) {
      ZstdCompressCtx var6 = new ZstdCompressCtx();

      long var7;
      try {
         var6.setLevel(k);
         var6.loadDict(ds);
         var7 = var6.compressByteArray(bs, i, bs.length - i, cs, j, cs.length - j);
      } finally {
         var6.close();
      }

      return var7;
   }

   public static long compressDirectByteBufferUsingDict(ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int l, byte[] bs, int m) {
      ZstdCompressCtx var8 = new ZstdCompressCtx();

      long var9;
      try {
         var8.setLevel(m);
         var8.loadDict(bs);
         var9 = var8.compressDirectByteBuffer(byteBuffer, i, j, byteBuffer2, k, l);
      } finally {
         var8.close();
      }

      return var9;
   }

   public static long compressFastDict(byte[] bs, int i, byte[] cs, int j, int k, ZstdDictCompress zstdDictCompress) {
      ZstdCompressCtx var6 = new ZstdCompressCtx();

      long var7;
      try {
         var6.loadDict(zstdDictCompress);
         var6.setLevel(zstdDictCompress.level());
         var7 = var6.compressByteArray(bs, i, bs.length - i, cs, j, k);
      } finally {
         var6.close();
      }

      return var7;
   }

   public static long compressFastDict(byte[] bs, int i, byte[] cs, int j, ZstdDictCompress zstdDictCompress) {
      ZstdCompressCtx var5 = new ZstdCompressCtx();

      long var6;
      try {
         var5.loadDict(zstdDictCompress);
         var5.setLevel(zstdDictCompress.level());
         var6 = var5.compressByteArray(bs, i, bs.length - i, cs, j, cs.length - j);
      } finally {
         var5.close();
      }

      return var6;
   }

   public static long compress(byte[] bs, byte[] cs, ZstdDictCompress zstdDictCompress) {
      ZstdCompressCtx var3 = new ZstdCompressCtx();

      long var4;
      try {
         var3.loadDict(zstdDictCompress);
         var3.setLevel(zstdDictCompress.level());
         var4 = var3.compress(bs, cs);
      } finally {
         var3.close();
      }

      return var4;
   }

   public static long compressDirectByteBufferFastDict(
      ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int l, ZstdDictCompress zstdDictCompress
   ) {
      ZstdCompressCtx var7 = new ZstdCompressCtx();

      long var8;
      try {
         var7.loadDict(zstdDictCompress);
         var7.setLevel(zstdDictCompress.level());
         var8 = var7.compressDirectByteBuffer(byteBuffer, i, j, byteBuffer2, k, l);
      } finally {
         var7.close();
      }

      return var8;
   }

   public static long decompress(byte[] bs, byte[] cs) {
      ZstdDecompressCtx var2 = new ZstdDecompressCtx();

      long var3;
      try {
         var3 = var2.decompress(bs, cs);
      } finally {
         var2.close();
      }

      return var3;
   }

   public static int decompress(byte[] bs, ByteBuffer byteBuffer) {
      ZstdDecompressCtx var2 = new ZstdDecompressCtx();

      int var3;
      try {
         var3 = var2.decompress(bs, byteBuffer);
      } finally {
         var2.close();
      }

      return var3;
   }

   public static long decompressByteArray(byte[] bs, int i, int j, byte[] cs, int k, int l) {
      ZstdDecompressCtx var6 = new ZstdDecompressCtx();

      long var7;
      try {
         var7 = var6.decompressByteArray(bs, i, j, cs, k, l);
      } finally {
         var6.close();
      }

      return var7;
   }

   public static long decompressDirectByteBuffer(ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int l) {
      ZstdDecompressCtx var6 = new ZstdDecompressCtx();

      long var7;
      try {
         var7 = var6.decompressDirectByteBuffer(byteBuffer, i, j, byteBuffer2, k, l);
      } finally {
         var6.close();
      }

      return var7;
   }

   public static native long decompressUnsafe(long l, long m, long n, long o);

   public static long decompressUsingDict(byte[] bs, int i, byte[] cs, int j, int k, byte[] ds) {
      ZstdDecompressCtx var6 = new ZstdDecompressCtx();

      long var7;
      try {
         var6.loadDict(ds);
         var7 = var6.decompressByteArray(bs, i, bs.length - i, cs, j, k);
      } finally {
         var6.close();
      }

      return var7;
   }

   public static long decompressDirectByteBufferUsingDict(ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int l, byte[] bs) {
      ZstdDecompressCtx var7 = new ZstdDecompressCtx();

      long var8;
      try {
         var7.loadDict(bs);
         var8 = var7.decompressDirectByteBuffer(byteBuffer, i, j, byteBuffer2, k, l);
      } finally {
         var7.close();
      }

      return var8;
   }

   public static long decompressFastDict(byte[] bs, int i, byte[] cs, int j, int k, ZstdDictDecompress zstdDictDecompress) {
      ZstdDecompressCtx var6 = new ZstdDecompressCtx();

      long var7;
      try {
         var6.loadDict(zstdDictDecompress);
         var7 = var6.decompressByteArray(bs, i, bs.length - i, cs, j, k);
      } finally {
         var6.close();
      }

      return var7;
   }

   public static long decompressDirectByteBufferFastDict(
      ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int l, ZstdDictDecompress zstdDictDecompress
   ) {
      ZstdDecompressCtx var7 = new ZstdDecompressCtx();

      long var8;
      try {
         var7.loadDict(zstdDictDecompress);
         var8 = var7.decompressDirectByteBuffer(byteBuffer, i, j, byteBuffer2, k, l);
      } finally {
         var7.close();
      }

      return var8;
   }

   public static native int loadDictDecompress(long l, byte[] bs, int i);

   public static native int loadFastDictDecompress(long l, ZstdDictDecompress zstdDictDecompress);

   public static native int loadDictCompress(long l, byte[] bs, int i);

   public static native int loadFastDictCompress(long l, ZstdDictCompress zstdDictCompress);

   public static native void registerSequenceProducer(long l, long m, long n);

   static native long getBuiltinSequenceProducer();

   static native void generateSequences(long l, long m, long n, long o, long p);

   static native long getStubSequenceProducer();

   public static native int setCompressionChecksums(long l, boolean bl);

   public static native int setCompressionMagicless(long l, boolean bl);

   public static native int setCompressionLevel(long l, int i);

   public static native int setCompressionLong(long l, int i);

   public static native int setCompressionWorkers(long l, int i);

   public static native int setCompressionOverlapLog(long l, int i);

   public static native int setCompressionJobSize(long l, int i);

   public static native int setCompressionTargetLength(long l, int i);

   public static native int setCompressionMinMatch(long l, int i);

   public static native int setCompressionSearchLog(long l, int i);

   public static native int setCompressionChainLog(long l, int i);

   public static native int setCompressionHashLog(long l, int i);

   public static native int setCompressionWindowLog(long l, int i);

   public static native int setCompressionStrategy(long l, int i);

   public static native int setDecompressionLongMax(long l, int i);

   public static native int setDecompressionMagicless(long l, boolean bl);

   public static native int setRefMultipleDDicts(long l, boolean bl);

   public static native int setValidateSequences(long l, int i);

   public static native int setSequenceProducerFallback(long l, boolean bl);

   public static native int setSearchForExternalRepcodes(long l, int i);

   public static native int setEnableLongDistanceMatching(long l, int i);

   public static long findFrameCompressedSize(byte[] bs, int i, int j) {
      if (i >= bs.length) {
         throw new ArrayIndexOutOfBoundsException(i);
      } else if (i + j > bs.length) {
         throw new ArrayIndexOutOfBoundsException(i + j);
      } else {
         long var3 = findFrameCompressedSize0(bs, i, j);
         if (isError(var3)) {
            throw new ZstdException(var3);
         } else {
            return var3;
         }
      }
   }

   private static native long findFrameCompressedSize0(byte[] bs, int i, int j);

   public static long findFrameCompressedSize(byte[] bs, int i) {
      return findFrameCompressedSize(bs, i, bs.length - i);
   }

   public static long findFrameCompressedSize(byte[] bs) {
      return findFrameCompressedSize(bs, 0);
   }

   public static long findFrameCompressedSize(ByteBuffer byteBuffer) {
      return findDirectByteBufferFrameCompressedSize(byteBuffer, byteBuffer.position(), byteBuffer.limit() - byteBuffer.position());
   }

   public static native long findDirectByteBufferFrameCompressedSize(ByteBuffer byteBuffer, int i, int j);

   public static long getFrameContentSize(byte[] bs, int i, int j, boolean bl) {
      if (i >= bs.length) {
         throw new ArrayIndexOutOfBoundsException(i);
      } else if (i + j > bs.length) {
         throw new ArrayIndexOutOfBoundsException(i + j);
      } else {
         return getFrameContentSize0(bs, i, j, bl);
      }
   }

   private static native long getFrameContentSize0(byte[] bs, int i, int j, boolean bl);

   @Deprecated
   public static long decompressedSize(byte[] bs, int i, int j, boolean bl) {
      if (i >= bs.length) {
         throw new ArrayIndexOutOfBoundsException(i);
      } else if (i + j > bs.length) {
         throw new ArrayIndexOutOfBoundsException(i + j);
      } else {
         return decompressedSize0(bs, i, j, bl);
      }
   }

   private static native long decompressedSize0(byte[] bs, int i, int j, boolean bl);

   public static long getFrameContentSize(byte[] bs, int i, int j) {
      return getFrameContentSize(bs, i, j, false);
   }

   @Deprecated
   public static long decompressedSize(byte[] bs, int i, int j) {
      return decompressedSize(bs, i, j, false);
   }

   public static long getFrameContentSize(byte[] bs, int i) {
      return getFrameContentSize(bs, i, bs.length - i);
   }

   @Deprecated
   public static long decompressedSize(byte[] bs, int i) {
      return decompressedSize(bs, i, bs.length - i);
   }

   public static long getFrameContentSize(byte[] bs) {
      return getFrameContentSize(bs, 0);
   }

   @Deprecated
   public static long decompressedSize(byte[] bs) {
      return decompressedSize(bs, 0);
   }

   @Deprecated
   public static native long decompressedDirectByteBufferSize(ByteBuffer byteBuffer, int i, int j, boolean bl);

   public static native long getDirectByteBufferFrameContentSize(ByteBuffer byteBuffer, int i, int j, boolean bl);

   @Deprecated
   public static long decompressedDirectByteBufferSize(ByteBuffer byteBuffer, int i, int j) {
      return decompressedDirectByteBufferSize(byteBuffer, i, j, false);
   }

   public static long getDirectByteBufferFrameContentSize(ByteBuffer byteBuffer, int i, int j) {
      return getDirectByteBufferFrameContentSize(byteBuffer, i, j, false);
   }

   public static native long compressBound(long l);

   public static native boolean isError(long l);

   public static native String getErrorName(long l);

   public static native long getErrorCode(long l);

   public static native long errNoError();

   public static native long errGeneric();

   public static native long errPrefixUnknown();

   public static native long errVersionUnsupported();

   public static native long errFrameParameterUnsupported();

   public static native long errFrameParameterWindowTooLarge();

   public static native long errCorruptionDetected();

   public static native long errChecksumWrong();

   public static native long errDictionaryCorrupted();

   public static native long errDictionaryWrong();

   public static native long errDictionaryCreationFailed();

   public static native long errParameterUnsupported();

   public static native long errParameterOutOfBound();

   public static native long errTableLogTooLarge();

   public static native long errMaxSymbolValueTooLarge();

   public static native long errMaxSymbolValueTooSmall();

   public static native long errStageWrong();

   public static native long errInitMissing();

   public static native long errMemoryAllocation();

   public static native long errWorkSpaceTooSmall();

   public static native long errDstSizeTooSmall();

   public static native long errSrcSizeWrong();

   public static native long errDstBufferNull();

   public static long trainFromBuffer(byte[][] bs, byte[] cs, boolean bl) {
      return trainFromBuffer(bs, cs, bl, defaultCompressionLevel());
   }

   public static long trainFromBuffer(byte[][] bs, byte[] cs, boolean bl, int i) {
      if (bs.length <= 10) {
         throw new ZstdException(errGeneric(), "nb of samples too low");
      } else {
         return trainFromBuffer0(bs, cs, bl, i);
      }
   }

   private static native long trainFromBuffer0(byte[][] bs, byte[] cs, boolean bl, int i);

   public static long trainFromBufferDirect(ByteBuffer byteBuffer, int[] is, ByteBuffer byteBuffer2, boolean bl) {
      return trainFromBufferDirect(byteBuffer, is, byteBuffer2, bl, defaultCompressionLevel());
   }

   public static long trainFromBufferDirect(ByteBuffer byteBuffer, int[] is, ByteBuffer byteBuffer2, boolean bl, int i) {
      if (is.length <= 10) {
         throw new ZstdException(errGeneric(), "nb of samples too low");
      } else {
         return trainFromBufferDirect0(byteBuffer, is, byteBuffer2, bl, i);
      }
   }

   private static native long trainFromBufferDirect0(ByteBuffer byteBuffer, int[] is, ByteBuffer byteBuffer2, boolean bl, int i);

   public static native long getDictIdFromFrame(byte[] bs);

   public static native long getDictIdFromFrameBuffer(ByteBuffer byteBuffer);

   public static native long getDictIdFromDict(byte[] bs);

   private static native long getDictIdFromDictDirect(ByteBuffer byteBuffer, int i, int j);

   public static long getDictIdFromDictDirect(ByteBuffer byteBuffer) {
      int var1 = byteBuffer.limit() - byteBuffer.position();
      if (!byteBuffer.isDirect()) {
         throw new IllegalArgumentException("dict must be a direct buffer");
      } else if (var1 < 0) {
         throw new IllegalArgumentException("dict cannot be empty.");
      } else {
         return getDictIdFromDictDirect(byteBuffer, byteBuffer.position(), var1);
      }
   }

   public static long trainFromBuffer(byte[][] bs, byte[] cs) {
      return trainFromBuffer(bs, cs, false);
   }

   public static long trainFromBufferDirect(ByteBuffer byteBuffer, int[] is, ByteBuffer byteBuffer2) {
      return trainFromBufferDirect(byteBuffer, is, byteBuffer2, false);
   }

   public static native int magicNumber();

   public static native int windowLogMin();

   public static native int windowLogMax();

   public static native int chainLogMin();

   public static native int chainLogMax();

   public static native int hashLogMin();

   public static native int hashLogMax();

   public static native int searchLogMin();

   public static native int searchLogMax();

   public static native int searchLengthMin();

   public static native int searchLengthMax();

   public static native int blockSizeMax();

   public static native int defaultCompressionLevel();

   public static native int minCompressionLevel();

   public static native int maxCompressionLevel();

   public static byte[] compress(byte[] bs) {
      return compress(bs, defaultCompressionLevel());
   }

   public static byte[] compress(byte[] bs, int i) {
      ZstdCompressCtx var2 = new ZstdCompressCtx();

      byte[] var3;
      try {
         var2.setLevel(i);
         var3 = var2.compress(bs);
      } finally {
         var2.close();
      }

      return var3;
   }

   public static int compress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
      return compress(byteBuffer, byteBuffer2, defaultCompressionLevel());
   }

   public static int compress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, int i, boolean bl) {
      ZstdCompressCtx var4 = new ZstdCompressCtx();

      int var5;
      try {
         var4.setLevel(i);
         var4.setChecksum(bl);
         var5 = var4.compress(byteBuffer, byteBuffer2);
      } finally {
         var4.close();
      }

      return var5;
   }

   public static int compress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, int i) {
      return compress(byteBuffer, byteBuffer2, i, false);
   }

   public static ByteBuffer compress(ByteBuffer byteBuffer, int i) {
      ZstdCompressCtx var2 = new ZstdCompressCtx();

      ByteBuffer var3;
      try {
         var2.setLevel(i);
         var3 = var2.compress(byteBuffer);
      } finally {
         var2.close();
      }

      return var3;
   }

   public static byte[] compress(byte[] bs, ZstdDictCompress zstdDictCompress) {
      ZstdCompressCtx var2 = new ZstdCompressCtx();

      byte[] var3;
      try {
         var2.loadDict(zstdDictCompress);
         var2.setLevel(zstdDictCompress.level());
         var3 = var2.compress(bs);
      } finally {
         var2.close();
      }

      return var3;
   }

   @Deprecated
   public static long compressUsingDict(byte[] bs, byte[] cs, byte[] ds, int i) {
      return compressUsingDict(bs, 0, cs, 0, cs.length, ds, i);
   }

   public static byte[] compressUsingDict(byte[] bs, byte[] cs, int i) {
      ZstdCompressCtx var3 = new ZstdCompressCtx();

      byte[] var4;
      try {
         var3.loadDict(cs);
         var3.setLevel(i);
         var4 = var3.compress(bs);
      } finally {
         var3.close();
      }

      return var4;
   }

   public static long compress(byte[] bs, byte[] cs, byte[] ds, int i) {
      return compressUsingDict(bs, 0, cs, 0, cs.length, ds, i);
   }

   public static int compress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, byte[] bs, int i) {
      ZstdCompressCtx var4 = new ZstdCompressCtx();

      int var5;
      try {
         var4.loadDict(bs);
         var4.setLevel(i);
         var5 = var4.compress(byteBuffer, byteBuffer2);
      } finally {
         var4.close();
      }

      return var5;
   }

   public static ByteBuffer compress(ByteBuffer byteBuffer, byte[] bs, int i) {
      ZstdCompressCtx var3 = new ZstdCompressCtx();

      ByteBuffer var4;
      try {
         var3.loadDict(bs);
         var3.setLevel(i);
         var4 = var3.compress(byteBuffer);
      } finally {
         var3.close();
      }

      return var4;
   }

   public static int compress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, ZstdDictCompress zstdDictCompress) {
      ZstdCompressCtx var3 = new ZstdCompressCtx();

      int var4;
      try {
         var3.loadDict(zstdDictCompress);
         var3.setLevel(zstdDictCompress.level());
         var4 = var3.compress(byteBuffer, byteBuffer2);
      } finally {
         var3.close();
      }

      return var4;
   }

   public static ByteBuffer compress(ByteBuffer byteBuffer, ZstdDictCompress zstdDictCompress) {
      ZstdCompressCtx var2 = new ZstdCompressCtx();

      ByteBuffer var3;
      try {
         var2.loadDict(zstdDictCompress);
         var2.setLevel(zstdDictCompress.level());
         var3 = var2.compress(byteBuffer);
      } finally {
         var2.close();
      }

      return var3;
   }

   public static byte[] decompress(byte[] bs, int i) {
      ZstdDecompressCtx var2 = new ZstdDecompressCtx();

      byte[] var3;
      try {
         var3 = var2.decompress(bs, i);
      } finally {
         var2.close();
      }

      return var3;
   }

   public static byte[] decompress(byte[] bs) {
      ArrayList var1 = new ArrayList();
      int var2 = calculateContentSizeAndFrames(bs, var1);
      byte[] var3 = new byte[var2];
      int var4 = 0;
      int var5 = 0;

      for (int var6 = 0; var6 < var1.size(); var6++) {
         Zstd.FrameData var7 = (Zstd.FrameData)var1.get(var6);
         long var8 = decompressByteArray(var3, var5, (int)var7.contentSize, bs, var4, (int)var7.compressedSize);
         if (isError(var8)) {
            throw new ZstdException(var8, String.format("error %s while decompressing %d frame", getErrorName(var8), var6));
         }

         if (var8 != var7.contentSize) {
            throw new IllegalStateException("decompressed size mismatch");
         }

         var4 += (int)var7.compressedSize;
         var5 += (int)var7.contentSize;
      }

      return var3;
   }

   private static int calculateContentSizeAndFrames(byte[] bs, List<Zstd.FrameData> list) {
      int var2 = 0;
      int var3 = 0;

      while (var3 < bs.length) {
         Zstd.FrameData var4 = new Zstd.FrameData(bs, var3);
         list.add(var4);
         var3 += (int)var4.compressedSize;
         var2 += (int)var4.contentSize;
      }

      return var2;
   }

   public static byte[] decompressFrame(byte[] bs, int i, int j, int k) {
      ZstdDecompressCtx var4 = new ZstdDecompressCtx();

      byte[] var5;
      try {
         var5 = var4.decompress(bs, i, j, k);
      } finally {
         var4.close();
      }

      return var5;
   }

   public static byte[] decompressFrame(byte[] bs, int i) {
      int var2 = (int)findFrameCompressedSize(bs, i);
      long var3 = getFrameContentSize(bs, i, var2);
      if (isError(var3)) {
         if (var3 == -1L) {
            throw new ZstdException(var3, "Content size is unknown");
         } else {
            throw new ZstdException(var3);
         }
      } else {
         return decompressFrame(bs, i, var2, (int)var3);
      }
   }

   public static byte[] decompressFrame(byte[] bs) {
      return decompressFrame(bs, 0);
   }

   public static int decompress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
      ZstdDecompressCtx var2 = new ZstdDecompressCtx();

      int var3;
      try {
         var3 = var2.decompress(byteBuffer, byteBuffer2);
      } finally {
         var2.close();
      }

      return var3;
   }

   public static int decompress(ByteBuffer byteBuffer, byte[] bs) {
      ZstdDecompressCtx var2 = new ZstdDecompressCtx();

      int var3;
      try {
         var3 = var2.decompress(byteBuffer, bs);
      } finally {
         var2.close();
      }

      return var3;
   }

   public static ByteBuffer decompress(ByteBuffer byteBuffer, int i) {
      ZstdDecompressCtx var2 = new ZstdDecompressCtx();

      ByteBuffer var3;
      try {
         var3 = var2.decompress(byteBuffer, i);
      } finally {
         var2.close();
      }

      return var3;
   }

   public static byte[] decompress(byte[] bs, ZstdDictDecompress zstdDictDecompress, int i) {
      ZstdDecompressCtx var3 = new ZstdDecompressCtx();

      byte[] var4;
      try {
         var3.loadDict(zstdDictDecompress);
         var4 = var3.decompress(bs, i);
      } finally {
         var3.close();
      }

      return var4;
   }

   @Deprecated
   public static long decompressUsingDict(byte[] bs, byte[] cs, byte[] ds) {
      return decompressUsingDict(bs, 0, cs, 0, cs.length, ds);
   }

   public static long decompress(byte[] bs, byte[] cs, byte[] ds) {
      return decompressUsingDict(bs, 0, cs, 0, cs.length, ds);
   }

   public static byte[] decompress(byte[] bs, byte[] cs, int i) {
      ZstdDecompressCtx var3 = new ZstdDecompressCtx();

      byte[] var4;
      try {
         var3.loadDict(cs);
         var4 = var3.decompress(bs, i);
      } finally {
         var3.close();
      }

      return var4;
   }

   @Deprecated
   public static long decompressedSize(ByteBuffer byteBuffer) {
      return decompressedDirectByteBufferSize(byteBuffer, byteBuffer.position(), byteBuffer.limit() - byteBuffer.position());
   }

   public static long getFrameContentSize(ByteBuffer byteBuffer) {
      return getDirectByteBufferFrameContentSize(byteBuffer, byteBuffer.position(), byteBuffer.limit() - byteBuffer.position());
   }

   public static int decompress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, byte[] bs) {
      ZstdDecompressCtx var3 = new ZstdDecompressCtx();

      int var4;
      try {
         var3.loadDict(bs);
         var4 = var3.decompress(byteBuffer, byteBuffer2);
      } finally {
         var3.close();
      }

      return var4;
   }

   public static ByteBuffer decompress(ByteBuffer byteBuffer, byte[] bs, int i) {
      ZstdDecompressCtx var3 = new ZstdDecompressCtx();

      ByteBuffer var4;
      try {
         var3.loadDict(bs);
         var4 = var3.decompress(byteBuffer, i);
      } finally {
         var3.close();
      }

      return var4;
   }

   public static int decompress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, ZstdDictDecompress zstdDictDecompress) {
      ZstdDecompressCtx var3 = new ZstdDecompressCtx();

      int var4;
      try {
         var3.loadDict(zstdDictDecompress);
         var4 = var3.decompress(byteBuffer, byteBuffer2);
      } finally {
         var3.close();
      }

      return var4;
   }

   public static ByteBuffer decompress(ByteBuffer byteBuffer, ZstdDictDecompress zstdDictDecompress, int i) {
      ZstdDecompressCtx var3 = new ZstdDecompressCtx();

      ByteBuffer var4;
      try {
         var3.loadDict(zstdDictDecompress);
         var4 = var3.decompress(byteBuffer, i);
      } finally {
         var3.close();
      }

      return var4;
   }

   static ByteBuffer getArrayBackedBuffer(BufferPool bufferPool, int i) throws ZstdIOException {
      ByteBuffer var2 = bufferPool.get(i);
      if (var2 == null) {
         throw new ZstdIOException(errMemoryAllocation(), "Cannot get ByteBuffer of size " + i + " from the BufferPool");
      } else if (var2.hasArray() && var2.arrayOffset() == 0) {
         return var2;
      } else {
         bufferPool.release(var2);
         throw new IllegalArgumentException("provided ByteBuffer lacks array or has non-zero arrayOffset");
      }
   }

   static {
      Native.load();
   }

   private static class FrameData {
      final long contentSize;
      final long compressedSize;

      FrameData(byte[] bs, int i) {
         this.compressedSize = Zstd.findFrameCompressedSize(bs, i);
         this.contentSize = Zstd.getFrameContentSize(bs, i, (int)this.compressedSize);
         if (Zstd.isError(this.contentSize)) {
            if (this.contentSize == -1L) {
               throw new ZstdException(this.contentSize, "Content size is unknown");
            } else {
               throw new ZstdException(this.contentSize);
            }
         }
      }
   }

   public static enum ParamSwitch {
      AUTO(0),
      ENABLE(1),
      DISABLE(2);

      private int val;

      private ParamSwitch(int j) {
         this.val = j;
      }

      public int getValue() {
         return this.val;
      }
   }
}
