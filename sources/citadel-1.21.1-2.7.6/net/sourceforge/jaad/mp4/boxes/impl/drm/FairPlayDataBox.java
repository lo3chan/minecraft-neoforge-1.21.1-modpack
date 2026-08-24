package net.sourceforge.jaad.mp4.boxes.impl.drm;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.BoxImpl;

public class FairPlayDataBox extends BoxImpl {
   private byte[] data;

   public FairPlayDataBox() {
      super("iTunes FairPlay Data Box");
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
