package net.sourceforge.jaad.mp4.boxes.impl.sampleentries;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

public class MPEGSampleEntry extends SampleEntry {
   public MPEGSampleEntry() {
      super("MPEG Sample Entry");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      this.readChildren(in);
   }
}
