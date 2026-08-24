package pl.skidam.automodpack_core.protocol.netty.message.configuration;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import pl.skidam.automodpack_core.protocol.netty.message.ConfigurationMessage;

public class ConfigurationCompressionMessage extends ConfigurationMessage {
   private final byte compressionType;

   public ConfigurationCompressionMessage(byte version, byte compressionType) {
      super(version, (byte)65);
      this.compressionType = compressionType;
   }

   public byte getCompressionType() {
      return this.compressionType;
   }

   public ByteBuf toByteBuf() {
      ByteBuf buf = Unpooled.buffer(3);
      super.toByteBuf(buf);
      buf.writeByte(this.compressionType);
      return buf;
   }
}
