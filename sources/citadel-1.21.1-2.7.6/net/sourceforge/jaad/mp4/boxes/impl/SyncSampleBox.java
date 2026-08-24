package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

public class SyncSampleBox extends FullBox {
   private long[] sampleNumbers;

   public SyncSampleBox() {
      super("Sync Sample Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      int entryCount = (int)in.readBytes(4);
      this.sampleNumbers = new long[entryCount];

      for (int i = 0; i < entryCount; i++) {
         this.sampleNumbers[i] = in.readBytes(4);
      }
   }

   public long[] getSampleNumbers() {
      return this.sampleNumbers;
   }
}
