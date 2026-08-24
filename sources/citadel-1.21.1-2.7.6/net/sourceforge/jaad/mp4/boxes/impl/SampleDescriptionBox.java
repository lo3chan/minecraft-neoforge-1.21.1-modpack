package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

public class SampleDescriptionBox extends FullBox {
   public SampleDescriptionBox() {
      super("Sample Description Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      int entryCount = (int)in.readBytes(4);
      this.readChildren(in, entryCount);
   }
}
