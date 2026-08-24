package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

public class MetaBox extends FullBox {
   public MetaBox() {
      super("Meta Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      long possibleType = in.peekBytes(8) & 4294967295L;
      if (possibleType != 1751411826L) {
         super.decode(in);
      }

      this.readChildren(in);
   }
}
