package org.tukaani.xz;

import java.io.IOException;
import java.io.OutputStream;
import org.tukaani.xz.check.Check;
import org.tukaani.xz.common.EncoderUtil;
import org.tukaani.xz.common.StreamFlags;
import org.tukaani.xz.index.IndexEncoder;

public class XZOutputStream extends FinishableOutputStream {
   private final ArrayCache arrayCache;
   private OutputStream out;
   private final StreamFlags streamFlags = new StreamFlags();
   private final Check check;
   private final IndexEncoder index = new IndexEncoder();
   private BlockOutputStream blockEncoder = null;
   private FilterEncoder[] filters;
   private boolean filtersSupportFlushing;
   private IOException exception = null;
   private boolean finished = false;
   private final byte[] tempBuf = new byte[1];

   public XZOutputStream(OutputStream outputStream, FilterOptions filterOptions) throws IOException {
      this(outputStream, filterOptions, 4);
   }

   public XZOutputStream(OutputStream outputStream, FilterOptions filterOptions, ArrayCache arrayCache) throws IOException {
      this(outputStream, filterOptions, 4, arrayCache);
   }

   public XZOutputStream(OutputStream outputStream, FilterOptions filterOptions, int i) throws IOException {
      this(outputStream, new FilterOptions[]{filterOptions}, i);
   }

   public XZOutputStream(OutputStream outputStream, FilterOptions filterOptions, int i, ArrayCache arrayCache) throws IOException {
      this(outputStream, new FilterOptions[]{filterOptions}, i, arrayCache);
   }

   public XZOutputStream(OutputStream outputStream, FilterOptions[] filterOptionss) throws IOException {
      this(outputStream, filterOptionss, 4);
   }

   public XZOutputStream(OutputStream outputStream, FilterOptions[] filterOptionss, ArrayCache arrayCache) throws IOException {
      this(outputStream, filterOptionss, 4, arrayCache);
   }

   public XZOutputStream(OutputStream outputStream, FilterOptions[] filterOptionss, int i) throws IOException {
      this(outputStream, filterOptionss, i, ArrayCache.getDefaultCache());
   }

   public XZOutputStream(OutputStream outputStream, FilterOptions[] filterOptionss, int i, ArrayCache arrayCache) throws IOException {
      this.arrayCache = arrayCache;
      this.out = outputStream;
      this.updateFilters(filterOptionss);
      this.streamFlags.checkType = i;
      this.check = Check.getInstance(i);
      this.encodeStreamHeader();
   }

   public void updateFilters(FilterOptions filterOptions) throws XZIOException {
      FilterOptions[] var2 = new FilterOptions[]{filterOptions};
      this.updateFilters(var2);
   }

   public void updateFilters(FilterOptions[] filterOptionss) throws XZIOException {
      if (this.blockEncoder != null) {
         throw new UnsupportedOptionsException("Changing filter options in the middle of a XZ Block not implemented");
      } else if (filterOptionss.length >= 1 && filterOptionss.length <= 4) {
         this.filtersSupportFlushing = true;
         FilterEncoder[] var2 = new FilterEncoder[filterOptionss.length];

         for (int var3 = 0; var3 < filterOptionss.length; var3++) {
            var2[var3] = filterOptionss[var3].getFilterEncoder();
            this.filtersSupportFlushing = this.filtersSupportFlushing & var2[var3].supportsFlushing();
         }

         RawCoder.validate(var2);
         this.filters = var2;
      } else {
         throw new UnsupportedOptionsException("XZ filter chain must be 1-4 filters");
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
            if (this.blockEncoder == null) {
               this.blockEncoder = new BlockOutputStream(this.out, this.filters, this.check, this.arrayCache);
            }

            this.blockEncoder.write(bs, i, j);
         } catch (IOException var5) {
            this.exception = var5;
            throw var5;
         }
      }
   }

   public void endBlock() throws IOException {
      if (this.exception != null) {
         throw this.exception;
      } else if (this.finished) {
         throw new XZIOException("Stream finished or closed");
      } else {
         if (this.blockEncoder != null) {
            try {
               this.blockEncoder.finish();
               this.index.add(this.blockEncoder.getUnpaddedSize(), this.blockEncoder.getUncompressedSize());
               this.blockEncoder = null;
            } catch (IOException var2) {
               this.exception = var2;
               throw var2;
            }
         }
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
            if (this.blockEncoder != null) {
               if (this.filtersSupportFlushing) {
                  this.blockEncoder.flush();
               } else {
                  this.endBlock();
                  this.out.flush();
               }
            } else {
               this.out.flush();
            }
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }
      }
   }

   @Override
   public void finish() throws IOException {
      if (!this.finished) {
         this.endBlock();

         try {
            this.index.encode(this.out);
            this.encodeStreamFooter();
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }

         this.finished = true;
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

   private void encodeStreamFlags(byte[] bs, int i) {
      bs[i] = 0;
      bs[i + 1] = (byte)this.streamFlags.checkType;
   }

   private void encodeStreamHeader() throws IOException {
      this.out.write(XZ.HEADER_MAGIC);
      byte[] var1 = new byte[2];
      this.encodeStreamFlags(var1, 0);
      this.out.write(var1);
      EncoderUtil.writeCRC32(this.out, var1);
   }

   private void encodeStreamFooter() throws IOException {
      byte[] var1 = new byte[6];
      long var2 = this.index.getIndexSize() / 4L - 1L;

      for (int var4 = 0; var4 < 4; var4++) {
         var1[var4] = (byte)(var2 >>> var4 * 8);
      }

      this.encodeStreamFlags(var1, 4);
      EncoderUtil.writeCRC32(this.out, var1);
      this.out.write(var1);
      this.out.write(XZ.FOOTER_MAGIC);
   }
}
