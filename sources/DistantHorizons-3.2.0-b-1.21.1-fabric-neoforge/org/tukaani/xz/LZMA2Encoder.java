package org.tukaani.xz;

import org.tukaani.xz.lzma.LZMAEncoder;

class LZMA2Encoder extends LZMA2Coder implements FilterEncoder {
   private final LZMA2Options options;
   private final byte[] props = new byte[1];

   LZMA2Encoder(LZMA2Options lZMA2Options) {
      if (lZMA2Options.getPresetDict() != null) {
         throw new IllegalArgumentException("XZ doesn't support a preset dictionary for now");
      } else {
         if (lZMA2Options.getMode() == 0) {
            this.props[0] = 0;
         } else {
            int var2 = Math.max(lZMA2Options.getDictSize(), 4096);
            this.props[0] = (byte)(LZMAEncoder.getDistSlot(var2 - 1) - 23);
         }

         this.options = (LZMA2Options)lZMA2Options.clone();
      }
   }

   @Override
   public long getFilterID() {
      return 33L;
   }

   @Override
   public byte[] getFilterProps() {
      return this.props;
   }

   @Override
   public boolean supportsFlushing() {
      return true;
   }

   @Override
   public FinishableOutputStream getOutputStream(FinishableOutputStream finishableOutputStream, ArrayCache arrayCache) {
      return this.options.getOutputStream(finishableOutputStream, arrayCache);
   }
}
