package org.tukaani.xz.common;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;

public class EncoderUtil extends Util {
   public static void writeCRC32(OutputStream outputStream, byte[] bs) throws IOException {
      CRC32 var2 = new CRC32();
      var2.update(bs);
      long var3 = var2.getValue();

      for (int var5 = 0; var5 < 4; var5++) {
         outputStream.write((byte)(var3 >>> var5 * 8));
      }
   }

   public static void encodeVLI(OutputStream outputStream, long l) throws IOException {
      while (l >= 128L) {
         outputStream.write((byte)(l | 128L));
         l >>>= 7;
      }

      outputStream.write((byte)l);
   }
}
