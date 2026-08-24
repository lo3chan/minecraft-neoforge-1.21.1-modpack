package pl.skidam.automodpack_core.protocol.netty.detectors;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public class AMMHDetector {
   private static final byte[] MAGIC_AMMH_ARRAY = new byte[]{65, 77, 77, 72};

   public static MatchResult check(ByteBuf in) {
      int readable = in.readableBytes();
      int start = in.readerIndex();

      for (int i = 0; i < MAGIC_AMMH_ARRAY.length; i++) {
         if (readable <= i) {
            return MatchResult.PARTIAL;
         }

         if (in.getByte(start + i) != MAGIC_AMMH_ARRAY[i]) {
            return MatchResult.MISMATCH;
         }
      }

      return MatchResult.MATCHED;
   }

   public static AMMHDetector.DecodeResult decode(ByteBuf in) {
      try {
         if (in.readableBytes() < 6) {
            return null;
         } else {
            int len = in.getUnsignedShort(in.readerIndex() + 4);
            int totalLen = 6 + len;
            if (in.readableBytes() < totalLen) {
               return null;
            } else {
               byte[] bytes = new byte[len];
               in.getBytes(in.readerIndex() + 6, bytes);
               String hostname = new String(bytes, StandardCharsets.UTF_8);
               return new AMMHDetector.DecodeResult(hostname, totalLen);
            }
         }
      } catch (Exception var5) {
         return new AMMHDetector.DecodeResult(null, 0);
      }
   }

   public record DecodeResult(String hostname, int consumedBytes) {
   }
}
