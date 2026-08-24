package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

public class MovieFragmentRandomAccessOffsetBox extends FullBox {
   private long byteSize;

   public MovieFragmentRandomAccessOffsetBox() {
      super("Movie Fragment Random Access Offset Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      this.byteSize = in.readBytes(4);
   }

   public long getByteSize() {
      return this.byteSize;
   }
}
