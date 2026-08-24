package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

public class PrimaryItemBox extends FullBox {
   private int itemID;

   public PrimaryItemBox() {
      super("Primary Item Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      this.itemID = (int)in.readBytes(2);
   }

   public int getItemID() {
      return this.itemID;
   }
}
