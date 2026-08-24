package pl.skidam.automodpack_core.protocol.netty.message.configuration;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import pl.skidam.automodpack_core.protocol.netty.message.ConfigurationMessage;

public class ConfigurationChunkSizeMessage extends ConfigurationMessage {
   private final int chunkSize;

   public ConfigurationChunkSizeMessage(byte version, int chunkSize) {
      super(version, (byte)66);
      this.chunkSize = chunkSize;
   }

   public int getChunkSize() {
      return this.chunkSize;
   }

   public ByteBuf toByteBuf() {
      ByteBuf buf = Unpooled.buffer(6);
      super.toByteBuf(buf);
      buf.writeInt(this.chunkSize);
      return buf;
   }
}
