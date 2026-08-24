package pl.skidam.automodpack_core.protocol.netty.message;

import io.netty.buffer.ByteBuf;

public abstract class ConfigurationMessage {
   private final byte version;
   private final byte type;

   public ConfigurationMessage(byte version, byte type) {
      this.version = version;
      this.type = type;
   }

   public byte getVersion() {
      return this.version;
   }

   public byte getType() {
      return this.type;
   }

   public void toByteBuf(ByteBuf buf) {
      buf.writeByte(this.version);
      buf.writeByte(this.type);
   }
}
