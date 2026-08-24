package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

public class ItemInformationBox extends FullBox {
   public ItemInformationBox() {
      super("Item Information Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      int protectionCount = (int)in.readBytes(2);
      this.readChildren(in, protectionCount);
   }
}
