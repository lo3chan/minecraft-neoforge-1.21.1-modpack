package net.sourceforge.jaad.mp4.od;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

public class DecoderSpecificInfo extends Descriptor {
   private byte[] data;

   @Override
   void decode(MP4InputStream in) throws IOException {
      this.data = new byte[this.size];
      in.readBytes(this.data);
   }

   public byte[] getData() {
      return this.data;
   }
}
