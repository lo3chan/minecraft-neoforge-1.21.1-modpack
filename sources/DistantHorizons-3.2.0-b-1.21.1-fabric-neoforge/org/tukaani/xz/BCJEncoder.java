package org.tukaani.xz;

class BCJEncoder extends BCJCoder implements FilterEncoder {
   private final BCJOptions options;
   private final long filterID;
   private final byte[] props;

   BCJEncoder(BCJOptions bCJOptions, long l) {
      assert isBCJFilterID(l);

      int var4 = bCJOptions.getStartOffset();
      if (var4 == 0) {
         this.props = new byte[0];
      } else {
         this.props = new byte[4];

         for (int var5 = 0; var5 < 4; var5++) {
            this.props[var5] = (byte)(var4 >>> var5 * 8);
         }
      }

      this.filterID = l;
      this.options = (BCJOptions)bCJOptions.clone();
   }

   @Override
   public long getFilterID() {
      return this.filterID;
   }

   @Override
   public byte[] getFilterProps() {
      return this.props;
   }

   @Override
   public boolean supportsFlushing() {
      return false;
   }

   @Override
   public FinishableOutputStream getOutputStream(FinishableOutputStream finishableOutputStream, ArrayCache arrayCache) {
      return this.options.getOutputStream(finishableOutputStream, arrayCache);
   }
}
