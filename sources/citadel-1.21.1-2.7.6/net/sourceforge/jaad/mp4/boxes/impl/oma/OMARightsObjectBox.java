package net.sourceforge.jaad.mp4.boxes.impl.oma;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

public class OMARightsObjectBox extends FullBox {
   private byte[] data;

   public OMARightsObjectBox() {
      super("OMA DRM Rights Object Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      this.data = new byte[(int)this.getLeft(in)];
      in.readBytes(this.data);
   }

   public byte[] getData() {
      return this.data;
   }
}
