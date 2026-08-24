package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.BoxImpl;

public class OriginalFormatBox extends BoxImpl {
   private long originalFormat;

   public OriginalFormatBox() {
      super("Original Format Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      this.originalFormat = in.readBytes(4);
   }

   public long getOriginalFormat() {
      return this.originalFormat;
   }
}
