package dhcomgithubluben.zstd;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ZstdDictTrainer {
   private final int allocatedSize;
   private final ByteBuffer trainingSamples;
   private final List<Integer> sampleSizes;
   private final int dictSize;
   private long filledSize;
   private int level;

   public ZstdDictTrainer(int i, int j) {
      this(i, j, Zstd.defaultCompressionLevel());
   }

   public ZstdDictTrainer(int i, int j, int k) {
      this.trainingSamples = ByteBuffer.allocateDirect(i);
      this.sampleSizes = new ArrayList<>();
      this.allocatedSize = i;
      this.dictSize = j;
      this.level = k;
   }

   public synchronized boolean addSample(byte[] bs) {
      if (this.filledSize + bs.length > this.allocatedSize) {
         return false;
      } else {
         this.trainingSamples.put(bs);
         this.sampleSizes.add(bs.length);
         this.filledSize += bs.length;
         return true;
      }
   }

   public ByteBuffer trainSamplesDirect() throws ZstdException {
      return this.trainSamplesDirect(false);
   }

   public synchronized ByteBuffer trainSamplesDirect(boolean bl) throws ZstdException {
      ByteBuffer var2 = ByteBuffer.allocateDirect(this.dictSize);
      long var3 = Zstd.trainFromBufferDirect(this.trainingSamples, this.copyToIntArray(this.sampleSizes), var2, bl, this.level);
      if (Zstd.isError(var3)) {
         ((Buffer)var2).limit(0);
         throw new ZstdException(var3);
      } else {
         ((Buffer)var2).limit(Long.valueOf(var3).intValue());
         return var2;
      }
   }

   public byte[] trainSamples() throws ZstdException {
      return this.trainSamples(false);
   }

   public byte[] trainSamples(boolean bl) throws ZstdException {
      ByteBuffer var2 = this.trainSamplesDirect(bl);
      byte[] var3 = new byte[var2.remaining()];
      var2.get(var3);
      return var3;
   }

   private int[] copyToIntArray(List<Integer> list) {
      int[] var2 = new int[list.size()];
      int var3 = 0;

      for (Integer var5 : list) {
         var2[var3] = var5;
         var3++;
      }

      return var2;
   }
}
