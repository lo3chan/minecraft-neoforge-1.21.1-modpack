package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.BoxImpl;

public abstract class CodecSpecificBox extends BoxImpl {
   private long vendor;
   private int decoderVersion;

   public CodecSpecificBox(String name) {
      super(name);
   }

   protected void decodeCommon(MP4InputStream in) throws IOException {
      this.vendor = in.readBytes(4);
      this.decoderVersion = in.read();
   }

   public long getVendor() {
      return this.vendor;
   }

   public int getDecoderVersion() {
      return this.decoderVersion;
   }
}
