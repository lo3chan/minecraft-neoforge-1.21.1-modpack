package org.tukaani.xz;

public class MemoryLimitException extends XZIOException {
   private static final long serialVersionUID = 3L;
   private final int memoryNeeded;
   private final int memoryLimit;

   public MemoryLimitException(int i, int j) {
      super("" + i + " KiB of memory would be needed; limit was " + j + " KiB");
      this.memoryNeeded = i;
      this.memoryLimit = j;
   }

   public int getMemoryNeeded() {
      return this.memoryNeeded;
   }

   public int getMemoryLimit() {
      return this.memoryLimit;
   }
}
