package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

public class XMLBox extends FullBox {
   private String content;

   public XMLBox() {
      super("XML Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      this.content = in.readUTFString((int)this.getLeft(in));
   }

   public String getContent() {
      return this.content;
   }
}
