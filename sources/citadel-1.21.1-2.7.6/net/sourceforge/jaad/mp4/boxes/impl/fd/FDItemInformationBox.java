package net.sourceforge.jaad.mp4.boxes.impl.fd;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

public class FDItemInformationBox extends FullBox {
   public FDItemInformationBox() {
      super("FD Item Information Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      int entryCount = (int)in.readBytes(2);
      this.readChildren(in, entryCount);
      this.readChildren(in);
   }
}
