package org.tukaani.xz;

import java.io.IOException;
import org.tukaani.xz.lz.LZEncoder;
import org.tukaani.xz.lzma.LZMAEncoder;
import org.tukaani.xz.rangecoder.RangeEncoderToBuffer;

class LZMA2OutputStream extends FinishableOutputStream {
   static final int COMPRESSED_SIZE_MAX = 65536;
   private final ArrayCache arrayCache;
   private FinishableOutputStream out;
   private LZEncoder lz;
   private RangeEncoderToBuffer rc;
   private LZMAEncoder lzma;
   private final int props;
   private boolean dictResetNeeded = true;
   private boolean stateResetNeeded = true;
   private boolean propsNeeded = true;
   private int pendingSize = 0;
   private boolean finished = false;
   private IOException exception = null;
   private final byte[] chunkHeader = new byte[6];
   private final byte[] tempBuf = new byte[1];

   private static int getExtraSizeBefore(int i) {
      return 65536 > i ? 65536 - i : 0;
   }

   static int getMemoryUsage(LZMA2Options lZMA2Options) {
      int var1 = lZMA2Options.getDictSize();
      int var2 = getExtraSizeBefore(var1);
      return 70 + LZMAEncoder.getMemoryUsage(lZMA2Options.getMode(), var1, var2, lZMA2Options.getMatchFinder());
   }

   LZMA2OutputStream(FinishableOutputStream finishableOutputStream, LZMA2Options lZMA2Options, ArrayCache arrayCache) {
      if (finishableOutputStream == null) {
         throw new NullPointerException();
      } else {
         this.arrayCache = arrayCache;
         this.out = finishableOutputStream;
         this.rc = new RangeEncoderToBuffer(65536, arrayCache);
         int var4 = lZMA2Options.getDictSize();
         int var5 = getExtraSizeBefore(var4);
         this.lzma = LZMAEncoder.getInstance(
            this.rc,
            lZMA2Options.getLc(),
            lZMA2Options.getLp(),
            lZMA2Options.getPb(),
            lZMA2Options.getMode(),
            var4,
            var5,
            lZMA2Options.getNiceLen(),
            lZMA2Options.getMatchFinder(),
            lZMA2Options.getDepthLimit(),
            this.arrayCache
         );
         this.lz = this.lzma.getLZEncoder();
         byte[] var6 = lZMA2Options.getPresetDict();
         if (var6 != null && var6.length > 0) {
            this.lz.setPresetDict(var4, var6);
            this.dictResetNeeded = false;
         }

         this.props = (lZMA2Options.getPb() * 5 + lZMA2Options.getLp()) * 9 + lZMA2Options.getLc();
      }
   }

   @Override
   public void write(int i) throws IOException {
      this.tempBuf[0] = (byte)i;
      this.write(this.tempBuf, 0, 1);
   }

   @Override
   public void write(byte[] bs, int i, int j) throws IOException {
      if (i < 0 || j < 0 || i + j < 0 || i + j > bs.length) {
         throw new IndexOutOfBoundsException();
      } else if (this.exception != null) {
         throw this.exception;
      } else if (this.finished) {
         throw new XZIOException("Stream finished or closed");
      } else {
         try {
            while (j > 0) {
               int var4 = this.lz.fillWindow(bs, i, j);
               i += var4;
               j -= var4;
               this.pendingSize += var4;
               if (this.lzma.encodeForLZMA2()) {
                  this.writeChunk();
               }
            }
         } catch (IOException var5) {
            this.exception = var5;
            throw var5;
         }
      }
   }

   private void writeChunk() throws IOException {
      int var1 = this.rc.finish();
      int var2 = this.lzma.getUncompressedSize();

      assert var1 > 0 : var1;

      assert var2 > 0 : var2;

      if (var1 + 2 < var2) {
         this.writeLZMA(var2, var1);
      } else {
         this.lzma.reset();
         var2 = this.lzma.getUncompressedSize();

         assert var2 > 0 : var2;

         this.writeUncompressed(var2);
      }

      this.pendingSize -= var2;
      this.lzma.resetUncompressedSize();
      this.rc.reset();
   }

   private void writeLZMA(int i, int j) throws IOException {
      short var3;
      if (this.propsNeeded) {
         if (this.dictResetNeeded) {
            var3 = 224;
         } else {
            var3 = 192;
         }
      } else if (this.stateResetNeeded) {
         var3 = 160;
      } else {
         var3 = 128;
      }

      var3 |= i - 1 >>> 16;
      this.chunkHeader[0] = (byte)var3;
      this.chunkHeader[1] = (byte)(i - 1 >>> 8);
      this.chunkHeader[2] = (byte)(i - 1);
      this.chunkHeader[3] = (byte)(j - 1 >>> 8);
      this.chunkHeader[4] = (byte)(j - 1);
      if (this.propsNeeded) {
         this.chunkHeader[5] = (byte)this.props;
         this.out.write(this.chunkHeader, 0, 6);
      } else {
         this.out.write(this.chunkHeader, 0, 5);
      }

      this.rc.write(this.out);
      this.propsNeeded = false;
      this.stateResetNeeded = false;
      this.dictResetNeeded = false;
   }

   private void writeUncompressed(int i) throws IOException {
      while (i > 0) {
         int var2 = Math.min(i, 65536);
         this.chunkHeader[0] = (byte)(this.dictResetNeeded ? 1 : 2);
         this.chunkHeader[1] = (byte)(var2 - 1 >>> 8);
         this.chunkHeader[2] = (byte)(var2 - 1);
         this.out.write(this.chunkHeader, 0, 3);
         this.lz.copyUncompressed(this.out, i, var2);
         i -= var2;
         this.dictResetNeeded = false;
      }

      this.stateResetNeeded = true;
   }

   private void writeEndMarker() throws IOException {
      assert !this.finished;

      if (this.exception != null) {
         throw this.exception;
      } else {
         this.lz.setFinishing();

         try {
            while (this.pendingSize > 0) {
               this.lzma.encodeForLZMA2();
               this.writeChunk();
            }

            this.out.write(0);
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }

         this.finished = true;
         this.lzma.putArraysToCache(this.arrayCache);
         this.lzma = null;
         this.lz = null;
         this.rc.putArraysToCache(this.arrayCache);
         this.rc = null;
      }
   }

   @Override
   public void flush() throws IOException {
      if (this.exception != null) {
         throw this.exception;
      } else if (this.finished) {
         throw new XZIOException("Stream finished or closed");
      } else {
         try {
            this.lz.setFlushing();

            while (this.pendingSize > 0) {
               this.lzma.encodeForLZMA2();
               this.writeChunk();
            }

            this.out.flush();
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }
      }
   }

   @Override
   public void finish() throws IOException {
      if (!this.finished) {
         this.writeEndMarker();

         try {
            this.out.finish();
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }
      }
   }

   @Override
   public void close() throws IOException {
      if (this.out != null) {
         if (!this.finished) {
            try {
               this.writeEndMarker();
            } catch (IOException var2) {
            }
         }

         try {
            this.out.close();
         } catch (IOException var3) {
            if (this.exception == null) {
               this.exception = var3;
            }
         }

         this.out = null;
      }

      if (this.exception != null) {
         throw this.exception;
      }
   }
}
