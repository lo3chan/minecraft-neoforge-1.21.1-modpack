package net.sourceforge.jaad.mp4.boxes.impl.sampleentries;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

public class RTPHintSampleEntry extends SampleEntry {
   private int hintTrackVersion;
   private int highestCompatibleVersion;
   private long maxPacketSize;

   public RTPHintSampleEntry() {
      super("RTP Hint Sample Entry");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      this.hintTrackVersion = (int)in.readBytes(2);
      this.highestCompatibleVersion = (int)in.readBytes(2);
      this.maxPacketSize = in.readBytes(4);
   }

   public long getMaxPacketSize() {
      return this.maxPacketSize;
   }
}
