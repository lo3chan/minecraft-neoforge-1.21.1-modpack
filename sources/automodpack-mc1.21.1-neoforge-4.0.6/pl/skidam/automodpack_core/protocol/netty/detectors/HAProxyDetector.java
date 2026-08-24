package pl.skidam.automodpack_core.protocol.netty.detectors;

import amp_libs.io.netty.handler.codec.haproxy.HAProxyMessage;
import amp_libs.io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;

public class HAProxyDetector {
   private static final byte[] V1_SIG = new byte[]{80, 82, 79, 88, 89, 32};
   private static final byte[] V2_SIG = new byte[]{13, 10, 13, 10, 0, 13, 10, 81, 85, 73, 84, 10};

   public static MatchResult check(ByteBuf in) {
      int start = in.readerIndex();
      int readable = in.readableBytes();
      boolean v1Possible = true;
      boolean v2Possible = true;

      for (int i = 0; i < V2_SIG.length && readable > i; i++) {
         if (in.getByte(start + i) != V2_SIG[i]) {
            v2Possible = false;
            break;
         }

         if (i == V2_SIG.length - 1) {
            return MatchResult.MATCHED;
         }
      }

      for (int i = 0; i < V1_SIG.length && readable > i; i++) {
         if (in.getByte(start + i) != V1_SIG[i]) {
            v1Possible = false;
            break;
         }

         if (i == V1_SIG.length - 1) {
            return MatchResult.MATCHED;
         }
      }

      return !v1Possible && !v2Possible ? MatchResult.MISMATCH : MatchResult.PARTIAL;
   }

   public static HAProxyDetector.DecodeResult decode(ByteBuf in) {
      EmbeddedChannel channel = new EmbeddedChannel(new ChannelHandler[]{new HAProxyMessageDecoder()});

      try {
         ByteBuf slice = in.slice(in.readerIndex(), in.readableBytes());
         if (channel.writeInbound(new Object[]{slice.retain()})) {
            HAProxyMessage msg = (HAProxyMessage)channel.readInbound();
            return new HAProxyDetector.DecodeResult(msg, slice.readerIndex());
         }
      } catch (Exception var8) {
         return new HAProxyDetector.DecodeResult(null, 0);
      } finally {
         channel.finishAndReleaseAll();
      }

      return null;
   }

   public record DecodeResult(HAProxyMessage message, int consumedBytes) {
   }
}
