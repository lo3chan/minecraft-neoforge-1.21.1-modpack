package pl.skidam.automodpack_core.protocol.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.protocol.netty.NettyServer;
import pl.skidam.automodpack_core.protocol.netty.message.configuration.ConfigurationChunkSizeMessage;
import pl.skidam.automodpack_core.protocol.netty.message.configuration.ConfigurationCompressionMessage;
import pl.skidam.automodpack_core.protocol.netty.message.configuration.UnknownConfigurationMessage;
import pl.skidam.automodpack_core.utils.PlatformUtils;

public class ConfigurationHandler extends ChannelInboundHandlerAdapter {
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      if (!(msg instanceof ByteBuf in)) {
         ctx.fireChannelRead(msg);
      } else {
         in.markReaderIndex();
         GlobalVariables.LOGGER.debug("Received a message (checking for configuration) with {} readable bytes", in.readableBytes());
         if (in.readableBytes() >= 2) {
            byte version = in.readByte();
            byte type = in.readByte();
            GlobalVariables.LOGGER.debug("Message version: {}, type: {}, readable bytes: {}", version, type, in.readableBytes());
            if ((type & 240) == 64) {
               if (type == 64) {
                  ctx.pipeline().remove(this);
                  GlobalVariables.LOGGER.debug("Removed ConfigurationHandler from pipeline after receiving echo configuration message.");
               } else if (type == 65) {
                  if (in.readableBytes() < 1) {
                     in.resetReaderIndex();
                     return;
                  }

                  byte clientCompressionType = in.readByte();
                  byte negotiatedCompressionType;
                  if (clientCompressionType != 2 && clientCompressionType != 0) {
                     negotiatedCompressionType = (byte)(PlatformUtils.canUseZstd() ? 1 : 2);
                  } else {
                     negotiatedCompressionType = clientCompressionType;
                  }

                  ctx.channel().attr(NettyServer.COMPRESSION_TYPE).set(negotiatedCompressionType);
                  ConfigurationCompressionMessage responseMsg = new ConfigurationCompressionMessage((byte)1, negotiatedCompressionType);
                  ctx.writeAndFlush(responseMsg.toByteBuf());
                  GlobalVariables.LOGGER.debug("Negotiated configuration: compression {}", negotiatedCompressionType);
               } else if (type == 66) {
                  if (in.readableBytes() < 4) {
                     in.resetReaderIndex();
                     return;
                  }

                  int clientChunkSize = in.readInt();
                  int negotiatedChunkSize;
                  if (clientChunkSize >= 8192 && clientChunkSize <= 524288) {
                     negotiatedChunkSize = clientChunkSize;
                  } else {
                     negotiatedChunkSize = 262144;
                  }

                  ctx.channel().attr(NettyServer.CHUNK_SIZE).set(negotiatedChunkSize);
                  ConfigurationChunkSizeMessage responseMsg = new ConfigurationChunkSizeMessage((byte)1, negotiatedChunkSize);
                  ctx.writeAndFlush(responseMsg.toByteBuf());
                  GlobalVariables.LOGGER.debug("Negotiated configuration: chunk size {}", negotiatedChunkSize);
               } else {
                  GlobalVariables.LOGGER.debug("Received unknown configuration message type: {} version: {}", type, version);
                  UnknownConfigurationMessage responseMsg = new UnknownConfigurationMessage((byte)1);
                  ctx.writeAndFlush(responseMsg.toByteBuf());
               }

               in.release();
            } else {
               GlobalVariables.LOGGER.debug("Received non-configuration message of type: {} version: {}", type, version);
               in.resetReaderIndex();
               ctx.fireChannelRead(in);
               ctx.pipeline().remove(this);
            }
         }
      }
   }
}
