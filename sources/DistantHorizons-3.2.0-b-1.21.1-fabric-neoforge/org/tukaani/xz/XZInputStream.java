package org.tukaani.xz;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XZInputStream extends InputStream {
   private final ArrayCache arrayCache;
   private final int memoryLimit;
   private InputStream in;
   private SingleXZInputStream xzIn;
   private final boolean verifyCheck;
   private boolean endReached = false;
   private IOException exception = null;
   private final byte[] tempBuf = new byte[1];

   public XZInputStream(InputStream inputStream) throws IOException {
      this(inputStream, -1);
   }

   public XZInputStream(InputStream inputStream, ArrayCache arrayCache) throws IOException {
      this(inputStream, -1, arrayCache);
   }

   public XZInputStream(InputStream inputStream, int i) throws IOException {
      this(inputStream, i, true);
   }

   public XZInputStream(InputStream inputStream, int i, ArrayCache arrayCache) throws IOException {
      this(inputStream, i, true, arrayCache);
   }

   public XZInputStream(InputStream inputStream, int i, boolean bl) throws IOException {
      this(inputStream, i, bl, ArrayCache.getDefaultCache());
   }

   public XZInputStream(InputStream inputStream, int i, boolean bl, ArrayCache arrayCache) throws IOException {
      this.arrayCache = arrayCache;
      this.in = inputStream;
      this.memoryLimit = i;
      this.verifyCheck = bl;
      this.xzIn = new SingleXZInputStream(inputStream, i, bl, arrayCache);
   }

   @Override
   public int read() throws IOException {
      return this.read(this.tempBuf, 0, 1) == -1 ? -1 : this.tempBuf[0] & 0xFF;
   }

   @Override
   public int read(byte[] bs, int i, int j) throws IOException {
      if (i < 0 || j < 0 || i + j < 0 || i + j > bs.length) {
         throw new IndexOutOfBoundsException();
      } else if (j == 0) {
         return 0;
      } else if (this.in == null) {
         throw new XZIOException("Stream closed");
      } else if (this.exception != null) {
         throw this.exception;
      } else if (this.endReached) {
         return -1;
      } else {
         int var4 = 0;

         try {
            while (j > 0) {
               if (this.xzIn == null) {
                  this.prepareNextStream();
                  if (this.endReached) {
                     return var4 == 0 ? -1 : var4;
                  }
               }

               int var5 = this.xzIn.read(bs, i, j);
               if (var5 > 0) {
                  var4 += var5;
                  i += var5;
                  j -= var5;
               } else if (var5 == -1) {
                  this.xzIn = null;
               }
            }
         } catch (IOException var6) {
            this.exception = var6;
            if (var4 == 0) {
               throw var6;
            }
         }

         return var4;
      }
   }

   private void prepareNextStream() throws IOException {
      DataInputStream var1 = new DataInputStream(this.in);
      byte[] var2 = new byte[12];

      do {
         int var3 = var1.read(var2, 0, 1);
         if (var3 == -1) {
            this.endReached = true;
            return;
         }

         var1.readFully(var2, 1, 3);
      } while (var2[0] == 0 && var2[1] == 0 && var2[2] == 0 && var2[3] == 0);

      var1.readFully(var2, 4, 8);

      try {
         this.xzIn = new SingleXZInputStream(this.in, this.memoryLimit, this.verifyCheck, var2, this.arrayCache);
      } catch (XZFormatException var4) {
         throw new CorruptedInputException("Garbage after a valid XZ Stream");
      }
   }

   @Override
   public int available() throws IOException {
      if (this.in == null) {
         throw new XZIOException("Stream closed");
      } else if (this.exception != null) {
         throw this.exception;
      } else {
         return this.xzIn == null ? 0 : this.xzIn.available();
      }
   }

   @Override
   public void close() throws IOException {
      this.close(true);
   }

   public void close(boolean bl) throws IOException {
      if (this.in != null) {
         if (this.xzIn != null) {
            this.xzIn.close(false);
            this.xzIn = null;
         }

         try {
            if (bl) {
               this.in.close();
            }
         } finally {
            this.in = null;
         }
      }
   }
}
