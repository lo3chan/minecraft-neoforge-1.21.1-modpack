package net.sourceforge.jaad.mp4.boxes;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

class UnknownBox extends BoxImpl {
   UnknownBox() {
      super("unknown");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
   }
}
