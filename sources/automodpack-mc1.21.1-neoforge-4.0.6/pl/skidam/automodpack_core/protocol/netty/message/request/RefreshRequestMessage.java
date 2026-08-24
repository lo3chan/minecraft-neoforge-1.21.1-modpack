package pl.skidam.automodpack_core.protocol.netty.message.request;

import pl.skidam.automodpack_core.protocol.netty.message.ProtocolMessage;

public class RefreshRequestMessage extends ProtocolMessage {
   private final int fileHashesCount;
   private final int fileHashesLength;
   private final byte[][] fileHashesList;

   public RefreshRequestMessage(byte version, byte[] secret, byte[][] fileHashesList) {
      super(version, (byte)3, secret);
      this.fileHashesCount = fileHashesList.length;
      this.fileHashesLength = fileHashesList[0].length;
      this.fileHashesList = fileHashesList;
   }

   public int getFileHashesCount() {
      return this.fileHashesCount;
   }

   public int getFileHashesLength() {
      return this.fileHashesLength;
   }

   public byte[][] getFileHashesList() {
      return this.fileHashesList;
   }
}
