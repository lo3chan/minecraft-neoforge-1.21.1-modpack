package pl.skidam.automodpack_core.protocol.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import pl.skidam.automodpack_core.protocol.compression.CompressionCodec;
import pl.skidam.automodpack_core.protocol.compression.CompressionFactory;
import pl.skidam.automodpack_core.protocol.netty.NettyServer;

public class CompressionEncoder extends MessageToByteEncoder<ByteBuf> {
   private CompressionCodec codec;

   protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
      Byte comp = (Byte)ctx.channel().attr(NettyServer.COMPRESSION_TYPE).get();
      this.codec = CompressionFactory.getCodec(comp);
      byte[] input = new byte[msg.readableBytes()];
      msg.readBytes(input);
      byte[] compressed = this.codec.compress(input);
      out.writeInt(compressed.length);
      out.writeInt(input.length);
      out.writeBytes(compressed);
   }

   public CompressionCodec getCodec() {
      return this.codec;
   }
}
