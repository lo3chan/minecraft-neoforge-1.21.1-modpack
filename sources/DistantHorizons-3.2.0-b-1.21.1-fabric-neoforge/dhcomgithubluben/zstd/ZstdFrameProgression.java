package dhcomgithubluben.zstd;

public class ZstdFrameProgression {
   private long ingested;
   private long consumed;
   private long produced;
   private long flushed;
   private int currentJobID;
   private int nbActiveWorkers;

   public ZstdFrameProgression(long l, long m, long n, long o, int i, int j) {
      this.ingested = l;
      this.consumed = m;
      this.produced = n;
      this.flushed = o;
      this.currentJobID = i;
      this.nbActiveWorkers = j;
   }

   public long getIngested() {
      return this.ingested;
   }

   public long getConsumed() {
      return this.consumed;
   }

   public long getProduced() {
      return this.produced;
   }

   public long getFlushed() {
      return this.flushed;
   }

   public int getCurrentJobID() {
      return this.currentJobID;
   }

   public int getNbActiveWorkers() {
      return this.nbActiveWorkers;
   }
}
