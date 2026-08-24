package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

public class SoundMediaHeaderBox extends FullBox {
   private double balance;

   public SoundMediaHeaderBox() {
      super("Sound Media Header Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      this.balance = in.readFixedPoint(8, 8);
      in.skipBytes(2L);
   }

   public double getBalance() {
      return this.balance;
   }
}
