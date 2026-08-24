package org.tukaani.xz;

abstract class BCJOptions extends FilterOptions {
   private final int alignment;
   int startOffset = 0;

   BCJOptions(int i) {
      this.alignment = i;
   }

   public void setStartOffset(int i) throws UnsupportedOptionsException {
      if ((i & this.alignment - 1) != 0) {
         throw new UnsupportedOptionsException("Start offset must be a multiple of " + this.alignment);
      } else {
         this.startOffset = i;
      }
   }

   public int getStartOffset() {
      return this.startOffset;
   }

   @Override
   public int getEncoderMemoryUsage() {
      return SimpleOutputStream.getMemoryUsage();
   }

   @Override
   public int getDecoderMemoryUsage() {
      return SimpleInputStream.getMemoryUsage();
   }

   @Override
   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         assert false;

         throw new RuntimeException();
      }
   }
}
