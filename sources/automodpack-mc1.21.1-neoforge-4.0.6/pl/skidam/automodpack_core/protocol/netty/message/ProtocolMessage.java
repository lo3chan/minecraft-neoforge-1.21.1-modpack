package pl.skidam.automodpack_core.protocol.netty.message;

public abstract class ProtocolMessage {
   private final byte version;
   private final byte type;
   private final byte[] secret;

   public ProtocolMessage(byte version, byte type, byte[] secret) {
      if (secret.length != 32) {
         throw new IllegalArgumentException("Secret must be 32 bytes");
      } else {
         this.version = version;
         this.type = type;
         this.secret = secret;
      }
   }

   public byte getVersion() {
      return this.version;
   }

   public byte getType() {
      return this.type;
   }

   public byte[] getSecret() {
      return this.secret;
   }
}
