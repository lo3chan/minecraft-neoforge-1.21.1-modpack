package org.tukaani.xz;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.tukaani.xz.check.Check;
import org.tukaani.xz.common.EncoderUtil;

class BlockOutputStream extends FinishableOutputStream {
   private final OutputStream out;
   private final CountingOutputStream outCounted;
   private FinishableOutputStream filterChain;
   private final Check check;
   private final int headerSize;
   private final long compressedSizeLimit;
   private long uncompressedSize = 0L;
   private final byte[] tempBuf = new byte[1];

   public BlockOutputStream(OutputStream outputStream, FilterEncoder[] filterEncoders, Check check, ArrayCache arrayCache) throws IOException {
      this.out = outputStream;
      this.check = check;
      this.outCounted = new CountingOutputStream(outputStream);
      this.filterChain = this.outCounted;

      for (int var5 = filterEncoders.length - 1; var5 >= 0; var5--) {
         this.filterChain = filterEncoders[var5].getOutputStream(this.filterChain, arrayCache);
      }

      ByteArrayOutputStream var8 = new ByteArrayOutputStream();
      var8.write(0);
      var8.write(filterEncoders.length - 1);

      for (int var6 = 0; var6 < filterEncoders.length; var6++) {
         EncoderUtil.encodeVLI(var8, filterEncoders[var6].getFilterID());
         byte[] var7 = filterEncoders[var6].getFilterProps();
         EncoderUtil.encodeVLI(var8, var7.length);
         var8.write(var7);
      }

      while ((var8.size() & 3) != 0) {
         var8.write(0);
      }

      byte[] var9 = var8.toByteArray();
      this.headerSize = var9.length + 4;
      if (this.headerSize > 1024) {
         throw new UnsupportedOptionsException();
      } else {
         var9[0] = (byte)(var9.length / 4);
         outputStream.write(var9);
         EncoderUtil.writeCRC32(outputStream, var9);
         this.compressedSizeLimit = 9223372036854775804L - this.headerSize - check.getSize();
      }
   }

   @Override
   public void write(int i) throws IOException {
      this.tempBuf[0] = (byte)i;
      this.write(this.tempBuf, 0, 1);
   }

   @Override
   public void write(byte[] bs, int i, int j) throws IOException {
      this.filterChain.write(bs, i, j);
      this.check.update(bs, i, j);
      this.uncompressedSize += j;
      this.validate();
   }

   @Override
   public void flush() throws IOException {
      this.filterChain.flush();
      this.validate();
   }

   @Override
   public void finish() throws IOException {
      this.filterChain.finish();
      this.validate();

      for (long var1 = this.outCounted.getSize(); (var1 & 3L) != 0L; var1++) {
         this.out.write(0);
      }

      this.out.write(this.check.finish());
   }

   private void validate() throws IOException {
      long var1 = this.outCounted.getSize();
      if (var1 < 0L || var1 > this.compressedSizeLimit || this.uncompressedSize < 0L) {
         throw new XZIOException("XZ Stream has grown too big");
      }
   }

   public long getUnpaddedSize() {
      return this.headerSize + this.outCounted.getSize() + this.check.getSize();
   }

   public long getUncompressedSize() {
      return this.uncompressedSize;
   }
}
