package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

public class H263SpecificBox extends CodecSpecificBox {
   private int level;
   private int profile;

   public H263SpecificBox() {
      super("H.263 Specific Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      this.decodeCommon(in);
      this.level = in.read();
      this.profile = in.read();
   }

   public int getLevel() {
      return this.level;
   }

   public int getProfile() {
      return this.profile;
   }
}
