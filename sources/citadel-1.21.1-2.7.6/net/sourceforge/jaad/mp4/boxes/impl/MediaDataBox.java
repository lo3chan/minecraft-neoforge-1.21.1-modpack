package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.BoxImpl;

public class MediaDataBox extends BoxImpl {
   public MediaDataBox() {
      super("Media Data Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
   }
}
