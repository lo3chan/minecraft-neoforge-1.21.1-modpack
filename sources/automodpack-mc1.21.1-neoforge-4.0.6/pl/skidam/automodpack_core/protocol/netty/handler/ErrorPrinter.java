package pl.skidam.automodpack_core.protocol.netty.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.ssl.NotSslRecordException;
import javax.net.ssl.SSLHandshakeException;
import pl.skidam.automodpack_core.GlobalVariables;

public class ErrorPrinter extends ChannelDuplexHandler {
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      if (!(cause instanceof DecoderException)
         || cause.getCause() == null
         || !(cause.getCause() instanceof SSLHandshakeException) && !(cause.getCause() instanceof NotSslRecordException)) {
         GlobalVariables.LOGGER.warn("Error occurred in connection to client at address {}", ctx.channel().remoteAddress(), cause);
      } else {
         GlobalVariables.LOGGER.debug("Error occurred in connection to client at address {}: {}", ctx.channel().remoteAddress(), cause.getMessage());
      }

      ctx.close();
   }
}
