package pl.skidam.automodpack_core.protocol.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import pl.skidam.automodpack_core.protocol.compression.CompressionCodec;
import pl.skidam.automodpack_core.protocol.compression.CompressionFactory;
import pl.skidam.automodpack_core.protocol.netty.NettyServer;

public class CompressionDecoder extends ByteToMessageDecoder {
   private CompressionCodec codec;

   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
      if (in.readableBytes() >= 8) {
         Byte comp = (Byte)ctx.channel().attr(NettyServer.COMPRESSION_TYPE).get();
         this.codec = CompressionFactory.getCodec(comp);
         in.markReaderIndex();
         int compressedLength = in.readInt();
         int originalLength = in.readInt();
         Integer chunkSize = (Integer)ctx.channel().attr(NettyServer.CHUNK_SIZE).get();
         int maxAllowedSize = chunkSize + 8192;
         if (compressedLength < 0 || compressedLength > maxAllowedSize) {
            throw new IllegalArgumentException("Frame compressed length (" + compressedLength + ") exceeds limit (" + maxAllowedSize + ")");
         } else if (originalLength >= 0 && originalLength <= chunkSize) {
            if (in.readableBytes() < compressedLength) {
               in.resetReaderIndex();
            } else {
               byte[] compressed = new byte[compressedLength];
               in.readBytes(compressed);
               byte[] decompressed = this.codec.decompress(compressed, originalLength);
               ByteBuf decompressedBuf = ctx.alloc().buffer(originalLength);
               decompressedBuf.writeBytes(decompressed);
               out.add(decompressedBuf);
            }
         } else {
            throw new IllegalArgumentException("Frame original length (" + originalLength + ") exceeds chunk size (" + chunkSize + ")");
         }
      }
   }

   public CompressionCodec getCodec() {
      return this.codec;
   }
}
