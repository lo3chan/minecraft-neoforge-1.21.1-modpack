package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;
import net.sourceforge.jaad.mp4.od.ESDescriptor;
import net.sourceforge.jaad.mp4.od.ObjectDescriptor;

public class ESDBox extends FullBox {
   private ESDescriptor esd;

   public ESDBox() {
      super("ESD Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      this.esd = (ESDescriptor)ObjectDescriptor.createDescriptor(in);
   }

   public ESDescriptor getEntryDescriptor() {
      return this.esd;
   }
}
