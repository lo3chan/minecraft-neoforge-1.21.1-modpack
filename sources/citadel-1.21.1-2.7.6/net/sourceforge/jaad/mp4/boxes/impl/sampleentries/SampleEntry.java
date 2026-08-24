package net.sourceforge.jaad.mp4.boxes.impl.sampleentries;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.BoxImpl;

public abstract class SampleEntry extends BoxImpl {
   private long dataReferenceIndex;

   protected SampleEntry(String name) {
      super(name);
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      in.skipBytes(6L);
      this.dataReferenceIndex = in.readBytes(2);
   }

   public long getDataReferenceIndex() {
      return this.dataReferenceIndex;
   }
}
