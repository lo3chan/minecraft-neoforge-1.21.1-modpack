package org.jcodec.containers.mkv.boxes;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.common.io.SeekableByteChannel;

public class EbmlVoid extends EbmlBase {
   public EbmlVoid(byte[] id) {
      super(id);
   }

   @Override
   public ByteBuffer getData() {
      return null;
   }

   public void skip(SeekableByteChannel is) throws IOException {
      is.setPosition(this.dataOffset + this.dataLen);
   }
}
