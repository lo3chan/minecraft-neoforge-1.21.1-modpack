package org.tukaani.xz;

import java.io.IOException;
import java.io.OutputStream;
import org.tukaani.xz.lz.LZEncoder;
import org.tukaani.xz.lzma.LZMAEncoder;
import org.tukaani.xz.rangecoder.RangeEncoderToStream;

public class LZMAOutputStream extends FinishableOutputStream {
   private OutputStream out;
   private final ArrayCache arrayCache;
   private LZEncoder lz;
   private final RangeEncoderToStream rc;
   private LZMAEncoder lzma;
   private final int props;
   private final boolean useEndMarker;
   private final long expectedUncompressedSize;
   private long currentUncompressedSize = 0L;
   private boolean finished = false;
   private IOException exception = null;
   private final byte[] tempBuf = new byte[1];

   private LZMAOutputStream(OutputStream outputStream, LZMA2Options lZMA2Options, boolean bl, boolean bl2, long l, ArrayCache arrayCache) throws IOException {
      if (outputStream == null) {
         throw new NullPointerException();
      } else if (l < -1L) {
         throw new IllegalArgumentException("Invalid expected input size (less than -1)");
      } else {
         this.useEndMarker = bl2;
         this.expectedUncompressedSize = l;
         this.arrayCache = arrayCache;
         this.out = outputStream;
         this.rc = new RangeEncoderToStream(outputStream);
         int var8 = lZMA2Options.getDictSize();
         this.lzma = LZMAEncoder.getInstance(
            this.rc,
            lZMA2Options.getLc(),
            lZMA2Options.getLp(),
            lZMA2Options.getPb(),
            lZMA2Options.getMode(),
            var8,
            0,
            lZMA2Options.getNiceLen(),
            lZMA2Options.getMatchFinder(),
            lZMA2Options.getDepthLimit(),
            arrayCache
         );
         this.lz = this.lzma.getLZEncoder();
         byte[] var9 = lZMA2Options.getPresetDict();
         if (var9 != null && var9.length > 0) {
            if (bl) {
               throw new UnsupportedOptionsException("Preset dictionary cannot be used in .lzma files (try a raw LZMA stream instead)");
            }

            this.lz.setPresetDict(var8, var9);
         }

         this.props = (lZMA2Options.getPb() * 5 + lZMA2Options.getLp()) * 9 + lZMA2Options.getLc();
         if (bl) {
            outputStream.write(this.props);

            for (int var10 = 0; var10 < 4; var10++) {
               outputStream.write(var8 & 0xFF);
               var8 >>>= 8;
            }

            for (int var11 = 0; var11 < 8; var11++) {
               outputStream.write((int)(l >>> 8 * var11) & 0xFF);
            }
         }
      }
   }

   public LZMAOutputStream(OutputStream outputStream, LZMA2Options lZMA2Options, long l) throws IOException {
      this(outputStream, lZMA2Options, l, ArrayCache.getDefaultCache());
   }

   public LZMAOutputStream(OutputStream outputStream, LZMA2Options lZMA2Options, long l, ArrayCache arrayCache) throws IOException {
      this(outputStream, lZMA2Options, true, l == -1L, l, arrayCache);
   }

   public LZMAOutputStream(OutputStream outputStream, LZMA2Options lZMA2Options, boolean bl) throws IOException {
      this(outputStream, lZMA2Options, bl, ArrayCache.getDefaultCache());
   }

   public LZMAOutputStream(OutputStream outputStream, LZMA2Options lZMA2Options, boolean bl, ArrayCache arrayCache) throws IOException {
      this(outputStream, lZMA2Options, false, bl, -1L, arrayCache);
   }

   public int getProps() {
      return this.props;
   }

   public long getUncompressedSize() {
      return this.currentUncompressedSize;
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
      } else if (this.expectedUncompressedSize != -1L && this.expectedUncompressedSize - this.currentUncompressedSize < j) {
         throw new XZIOException("Expected uncompressed input size (" + this.expectedUncompressedSize + " bytes) was exceeded");
      } else {
         this.currentUncompressedSize += j;

         try {
            while (j > 0) {
               int var4 = this.lz.fillWindow(bs, i, j);
               i += var4;
               j -= var4;
               this.lzma.encodeForLZMA1();
            }
         } catch (IOException var5) {
            this.exception = var5;
            throw var5;
         }
      }
   }

   @Override
   public void flush() throws IOException {
      throw new XZIOException("LZMAOutputStream does not support flushing");
   }

   @Override
   public void finish() throws IOException {
      if (!this.finished) {
         if (this.exception != null) {
            throw this.exception;
         }

         try {
            if (this.expectedUncompressedSize != -1L && this.expectedUncompressedSize != this.currentUncompressedSize) {
               throw new XZIOException(
                  "Expected uncompressed size ("
                     + this.expectedUncompressedSize
                     + ") doesn't equal the number of bytes written to the stream ("
                     + this.currentUncompressedSize
                     + ")"
               );
            }

            this.lz.setFinishing();
            this.lzma.encodeForLZMA1();
            if (this.useEndMarker) {
               this.lzma.encodeLZMA1EndMarker();
            }

            this.rc.finish();
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }

         this.finished = true;
         this.lzma.putArraysToCache(this.arrayCache);
         this.lzma = null;
         this.lz = null;
      }
   }

   @Override
   public void close() throws IOException {
      if (this.out != null) {
         try {
            this.finish();
         } catch (IOException var2) {
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
