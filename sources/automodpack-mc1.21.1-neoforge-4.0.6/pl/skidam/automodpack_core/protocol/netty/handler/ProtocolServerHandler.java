package pl.skidam.automodpack_core.protocol.netty.handler;

import amp_libs.io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.ReferenceCountUtil;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.protocol.netty.NettyServer;
import pl.skidam.automodpack_core.protocol.netty.TrafficShaper;
import pl.skidam.automodpack_core.protocol.netty.detectors.AMMHDetector;
import pl.skidam.automodpack_core.protocol.netty.detectors.HAProxyDetector;
import pl.skidam.automodpack_core.protocol.netty.detectors.MatchResult;

public class ProtocolServerHandler extends ByteToMessageDecoder {
   private final SslContext sslCtx;
   private boolean proxyCheckFinished = false;
   private boolean magicCheckFinished = false;
   private SocketAddress remoteAddress = null;
   private ByteBuf originalBuffer = null;

   public ProtocolServerHandler(SslContext sslCtx) {
      this.sslCtx = sslCtx;
   }

   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      this.remoteAddress = ctx.channel().remoteAddress();
      super.channelActive(ctx);
   }

   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
      if (!this.proxyCheckFinished) {
         MatchResult res = this.handleProxyCheck(ctx, in);
         if (res == MatchResult.PARTIAL) {
            return;
         }

         this.proxyCheckFinished = true;
      }

      if (!this.magicCheckFinished) {
         MatchResult res = this.handleMagicCheck(ctx, in, out);
         if (res == MatchResult.PARTIAL) {
            return;
         }

         this.magicCheckFinished = true;
      }
   }

   private MatchResult handleProxyCheck(ChannelHandlerContext ctx, ByteBuf in) {
      MatchResult result = HAProxyDetector.check(in);
      if (result != MatchResult.MATCHED) {
         return result;
      } else {
         HAProxyDetector.DecodeResult decodeResult = HAProxyDetector.decode(in);
         if (decodeResult == null) {
            return MatchResult.PARTIAL;
         } else if (decodeResult.message() == null) {
            return MatchResult.MISMATCH;
         } else {
            this.onProxyMatch(ctx, in, decodeResult);
            return MatchResult.MATCHED;
         }
      }
   }

   private MatchResult handleMagicCheck(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
      MatchResult result = AMMHDetector.check(in);
      if (result != MatchResult.MATCHED) {
         if (result == MatchResult.MISMATCH) {
            this.onMagicMismatch(ctx, in, out);
         }

         return result;
      } else {
         AMMHDetector.DecodeResult decodeResult = AMMHDetector.decode(in);
         if (decodeResult == null) {
            return MatchResult.PARTIAL;
         } else if (decodeResult.hostname() == null) {
            return MatchResult.MISMATCH;
         } else {
            this.onMagicMatch(ctx, in, decodeResult);
            return MatchResult.MATCHED;
         }
      }
   }

   private void onProxyMatch(ChannelHandlerContext ctx, ByteBuf in, HAProxyDetector.DecodeResult result) {
      HAProxyMessage msg = result.message();

      try {
         this.appendConsumedBytes(ctx, in.readRetainedSlice(result.consumedBytes()));
         if (msg != null && msg.sourceAddress() != null) {
            this.remoteAddress = new InetSocketAddress(msg.sourceAddress(), msg.sourcePort());
            GlobalVariables.LOGGER.debug("PROXY: Remote address set to {}", this.remoteAddress);
         }
      } catch (Exception var9) {
         GlobalVariables.LOGGER.error("Error processing HAProxy message", var9);
      } finally {
         ReferenceCountUtil.release(msg);
      }
   }

   private void onMagicMatch(ChannelHandlerContext ctx, ByteBuf in, AMMHDetector.DecodeResult result) {
      in.skipBytes(result.consumedBytes());
      GlobalVariables.LOGGER.debug("AMMH Handshake: {}", result.hostname());
      ctx.writeAndFlush(ctx.alloc().buffer(4).writeInt(1095585611));
      this.finalizeHandshake(ctx);
   }

   private void onMagicMismatch(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
      boolean isSharedPort = GlobalVariables.serverConfig.bindPort == -1 && GlobalVariables.hostServer.isRunning();
      if (isSharedPort) {
         this.fallbackToOriginalPipeline(ctx, in, out);
      } else {
         this.finalizeHandshake(ctx);
      }
   }

   private void fallbackToOriginalPipeline(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
      ByteBuf payload = this.reconstructPayload(ctx, in);
      out.add(payload);
      ctx.pipeline().remove(this);
   }

   private ByteBuf reconstructPayload(ChannelHandlerContext ctx, ByteBuf in) {
      if (this.originalBuffer == null) {
         return in.readRetainedSlice(in.readableBytes());
      } else {
         CompositeByteBuf composite = ctx.alloc().compositeBuffer();
         composite.addComponent(true, this.originalBuffer);
         this.originalBuffer = null;
         composite.addComponent(true, in.readRetainedSlice(in.readableBytes()));
         return composite;
      }
   }

   private void appendConsumedBytes(ChannelHandlerContext ctx, ByteBuf consumed) {
      if (this.originalBuffer == null) {
         this.originalBuffer = consumed;
      } else {
         if (this.originalBuffer instanceof CompositeByteBuf) {
            ((CompositeByteBuf)this.originalBuffer).addComponent(true, consumed);
         } else {
            CompositeByteBuf composite = ctx.alloc().compositeBuffer();
            composite.addComponent(true, this.originalBuffer);
            composite.addComponent(true, consumed);
            this.originalBuffer = composite;
         }
      }
   }

   private void finalizeHandshake(ChannelHandlerContext ctx) {
      ctx.pipeline().toMap().forEach((k, v) -> {
         if (v != this) {
            ctx.pipeline().remove(v);
         }
      });
      this.safeReleaseOriginalBuffer();
      this.setupPipeline(ctx);
      if (ctx.pipeline().context(this) != null) {
         ctx.pipeline().remove(this);
      }
   }

   private void setupPipeline(ChannelHandlerContext ctx) {
      ctx.pipeline().addLast("error-printer-first", new ErrorPrinter());
      ctx.pipeline().addLast("traffic-shaper", TrafficShaper.trafficShaper.getTrafficShapingHandler());
      if (this.sslCtx != null) {
         ctx.pipeline().addLast("tls", this.sslCtx.newHandler(ctx.alloc()));
         GlobalVariables.LOGGER.debug("Pipeline: TLS Enabled");
      } else {
         GlobalVariables.LOGGER.debug("Pipeline: TLS Disabled");
      }

      ctx.channel().attr(NettyServer.REAL_REMOTE_ADDR).set(this.remoteAddress);
      ctx.channel().attr(NettyServer.PROTOCOL_VERSION).set((byte)1);
      ctx.channel().attr(NettyServer.COMPRESSION_TYPE).set((byte)1);
      ctx.channel().attr(NettyServer.CHUNK_SIZE).set(262144);
      ctx.pipeline()
         .addLast("configuration-handler", new ConfigurationHandler())
         .addLast("compression-encoder", new CompressionEncoder())
         .addLast("compression-decoder", new CompressionDecoder())
         .addLast("chunked-write", new ChunkedWriteHandler())
         .addLast("protocol-msg-decoder", new ProtocolMessageDecoder())
         .addLast("msg-handler", new ServerMessageHandler())
         .addLast("error-printer-last", new ErrorPrinter());
   }

   private void safeReleaseOriginalBuffer() {
      if (this.originalBuffer != null) {
         if (this.originalBuffer.refCnt() > 0) {
            this.originalBuffer.release();
         }

         this.originalBuffer = null;
      }
   }

   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      this.safeReleaseOriginalBuffer();
      super.channelInactive(ctx);
   }
}
