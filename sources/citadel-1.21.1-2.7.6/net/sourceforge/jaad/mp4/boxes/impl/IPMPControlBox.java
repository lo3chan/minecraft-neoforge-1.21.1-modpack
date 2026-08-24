package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;
import net.sourceforge.jaad.mp4.od.Descriptor;

public class IPMPControlBox extends FullBox {
   private Descriptor toolList;
   private Descriptor[] ipmpDescriptors;

   public IPMPControlBox() {
      super("IPMP Control Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      this.toolList = Descriptor.createDescriptor(in);
      int count = in.read();
      this.ipmpDescriptors = new Descriptor[count];

      for (int i = 0; i < count; i++) {
         this.ipmpDescriptors[i] = Descriptor.createDescriptor(in);
      }
   }

   public Descriptor getToolList() {
      return this.toolList;
   }

   public Descriptor[] getIPMPDescriptors() {
      return this.ipmpDescriptors;
   }
}
