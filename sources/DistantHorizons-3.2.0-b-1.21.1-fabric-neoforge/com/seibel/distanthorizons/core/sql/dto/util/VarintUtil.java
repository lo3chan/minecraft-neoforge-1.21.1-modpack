package com.seibel.distanthorizons.core.sql.dto.util;

import com.seibel.distanthorizons.core.util.objects.dataStreams.DhDataInputStream;
import com.seibel.distanthorizons.core.util.objects.dataStreams.DhDataOutputStream;
import java.io.IOException;

public class VarintUtil {
   public static int zigzagEncode(int n) {
      return n << 1 ^ n >> 31;
   }

   public static int zigzagDecode(int n) {
      return n >>> 1 ^ -(n & 1);
   }

   public static void writeVarint(DhDataOutputStream out, int value) throws IOException {
      if (value < 0) {
         throw new IllegalArgumentException("varint given [" + value + "], varint only accepts positive values.");
      } else {
         while (value >= 128) {
            out.writeByte(value | 128);
            value >>>= 7;
         }

         out.writeByte(value);
      }
   }

   public static int readVarint(DhDataInputStream in) throws IOException {
      int value = 0;
      int shift = 0;

      while (shift < 32) {
         byte b = in.readByte();
         value |= (b & 127) << shift;
         shift += 7;
         if ((b & 128) == 0) {
            return value;
         }
      }

      throw new IOException("invalid varint");
   }
}
