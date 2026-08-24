package org.tukaani.xz.common;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import org.tukaani.xz.CorruptedInputException;
import org.tukaani.xz.UnsupportedOptionsException;
import org.tukaani.xz.XZ;
import org.tukaani.xz.XZFormatException;

public class DecoderUtil extends Util {
   public static boolean isCRC32Valid(byte[] bs, int i, int j, int k) {
      CRC32 var4 = new CRC32();
      var4.update(bs, i, j);
      long var5 = var4.getValue();

      for (int var7 = 0; var7 < 4; var7++) {
         if ((byte)(var5 >>> var7 * 8) != bs[k + var7]) {
            return false;
         }
      }

      return true;
   }

   public static StreamFlags decodeStreamHeader(byte[] bs) throws IOException {
      for (int var1 = 0; var1 < XZ.HEADER_MAGIC.length; var1++) {
         if (bs[var1] != XZ.HEADER_MAGIC[var1]) {
            throw new XZFormatException();
         }
      }

      if (!isCRC32Valid(bs, XZ.HEADER_MAGIC.length, 2, XZ.HEADER_MAGIC.length + 2)) {
         throw new CorruptedInputException("XZ Stream Header is corrupt");
      } else {
         try {
            return decodeStreamFlags(bs, XZ.HEADER_MAGIC.length);
         } catch (UnsupportedOptionsException var2) {
            throw new UnsupportedOptionsException("Unsupported options in XZ Stream Header");
         }
      }
   }

   public static StreamFlags decodeStreamFooter(byte[] bs) throws IOException {
      if (bs[10] != XZ.FOOTER_MAGIC[0] || bs[11] != XZ.FOOTER_MAGIC[1]) {
         throw new CorruptedInputException("XZ Stream Footer is corrupt");
      } else if (!isCRC32Valid(bs, 4, 6, 0)) {
         throw new CorruptedInputException("XZ Stream Footer is corrupt");
      } else {
         StreamFlags var1;
         try {
            var1 = decodeStreamFlags(bs, 8);
         } catch (UnsupportedOptionsException var3) {
            throw new UnsupportedOptionsException("Unsupported options in XZ Stream Footer");
         }

         var1.backwardSize = 0L;

         for (int var2 = 0; var2 < 4; var2++) {
            var1.backwardSize = var1.backwardSize | (bs[var2 + 4] & 255) << var2 * 8;
         }

         var1.backwardSize = (var1.backwardSize + 1L) * 4L;
         return var1;
      }
   }

   private static StreamFlags decodeStreamFlags(byte[] bs, int i) throws UnsupportedOptionsException {
      if (bs[i] == 0 && (bs[i + 1] & 255) < 16) {
         StreamFlags var2 = new StreamFlags();
         var2.checkType = bs[i + 1];
         return var2;
      } else {
         throw new UnsupportedOptionsException();
      }
   }

   public static boolean areStreamFlagsEqual(StreamFlags streamFlags, StreamFlags streamFlags2) {
      return streamFlags.checkType == streamFlags2.checkType;
   }

   public static long decodeVLI(InputStream inputStream) throws IOException {
      int var1 = inputStream.read();
      if (var1 == -1) {
         throw new EOFException();
      } else {
         long var2 = var1 & 127;

         for (int var4 = 0; (var1 & 128) != 0; var2 |= (long)(var1 & 127) << var4 * 7) {
            if (++var4 >= 9) {
               throw new CorruptedInputException();
            }

            var1 = inputStream.read();
            if (var1 == -1) {
               throw new EOFException();
            }

            if (var1 == 0) {
               throw new CorruptedInputException();
            }
         }

         return var2;
      }
   }
}
