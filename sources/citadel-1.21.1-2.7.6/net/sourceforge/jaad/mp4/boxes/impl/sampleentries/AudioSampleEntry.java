package net.sourceforge.jaad.mp4.boxes.impl.sampleentries;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

public class AudioSampleEntry extends SampleEntry {
   private int channelCount;
   private int sampleSize;
   private int sampleRate;

   public AudioSampleEntry(String name) {
      super(name);
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      in.skipBytes(8L);
      this.channelCount = (int)in.readBytes(2);
      this.sampleSize = (int)in.readBytes(2);
      in.skipBytes(2L);
      in.skipBytes(2L);
      this.sampleRate = (int)in.readBytes(2);
      in.skipBytes(2L);
      this.readChildren(in);
   }

   public int getChannelCount() {
      return this.channelCount;
   }

   public int getSampleRate() {
      return this.sampleRate;
   }

   public int getSampleSize() {
      return this.sampleSize;
   }
}
