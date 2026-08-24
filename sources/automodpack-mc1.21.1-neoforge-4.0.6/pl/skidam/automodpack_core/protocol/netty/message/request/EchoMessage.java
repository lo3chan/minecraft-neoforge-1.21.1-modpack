package pl.skidam.automodpack_core.protocol.netty.message.request;

import pl.skidam.automodpack_core.protocol.netty.message.ProtocolMessage;

public class EchoMessage extends ProtocolMessage {
   private final int dataLength;
   private final byte[] data;

   public EchoMessage(byte version, byte[] secret, byte[] data) {
      super(version, (byte)0, secret);
      this.dataLength = data.length;
      this.data = data;
   }

   public int getDataLength() {
      return this.dataLength;
   }

   public byte[] getData() {
      return this.data;
   }
}
