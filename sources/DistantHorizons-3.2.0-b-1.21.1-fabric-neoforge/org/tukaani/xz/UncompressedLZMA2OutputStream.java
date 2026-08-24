package org.tukaani.xz;

import java.io.DataOutputStream;
import java.io.IOException;

class UncompressedLZMA2OutputStream extends FinishableOutputStream {
   private final ArrayCache arrayCache;
   private FinishableOutputStream out;
   private final DataOutputStream outData;
   private final byte[] uncompBuf;
   private int uncompPos = 0;
   private boolean dictResetNeeded = true;
   private boolean finished = false;
   private IOException exception = null;
   private final byte[] tempBuf = new byte[1];

   static int getMemoryUsage() {
      return 70;
   }

   UncompressedLZMA2OutputStream(FinishableOutputStream finishableOutputStream, ArrayCache arrayCache) {
      if (finishableOutputStream == null) {
         throw new NullPointerException();
      } else {
         this.out = finishableOutputStream;
         this.outData = new DataOutputStream(finishableOutputStream);
         this.arrayCache = arrayCache;
         this.uncompBuf = arrayCache.getByteArray(65536, false);
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
               int var4 = Math.min(65536 - this.uncompPos, j);
               System.arraycopy(bs, i, this.uncompBuf, this.uncompPos, var4);
               j -= var4;
               this.uncompPos += var4;
               if (this.uncompPos == 65536) {
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
      this.outData.writeByte(this.dictResetNeeded ? 1 : 2);
      this.outData.writeShort(this.uncompPos - 1);
      this.outData.write(this.uncompBuf, 0, this.uncompPos);
      this.uncompPos = 0;
      this.dictResetNeeded = false;
   }

   private void writeEndMarker() throws IOException {
      if (this.exception != null) {
         throw this.exception;
      } else if (this.finished) {
         throw new XZIOException("Stream finished or closed");
      } else {
         try {
            if (this.uncompPos > 0) {
               this.writeChunk();
            }

            this.out.write(0);
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }

         this.finished = true;
         this.arrayCache.putArray(this.uncompBuf);
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
            if (this.uncompPos > 0) {
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
