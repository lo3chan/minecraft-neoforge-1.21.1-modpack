package pl.skidam.automodpack_core.protocol.netty.message.request;

import pl.skidam.automodpack_core.protocol.netty.message.ProtocolMessage;

public class FileRequestMessage extends ProtocolMessage {
   private final int fileHashLength;
   private final byte[] fileHash;

   public FileRequestMessage(byte version, byte[] secret, byte[] fileHash) {
      super(version, (byte)1, secret);
      this.fileHashLength = fileHash.length;
      this.fileHash = fileHash;
   }

   public int getFileHashLength() {
      return this.fileHashLength;
   }

   public byte[] getFileHash() {
      return this.fileHash;
   }
}
