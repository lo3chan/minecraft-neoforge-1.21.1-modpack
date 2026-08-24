package net.sourceforge.jaad.mp4.boxes.impl.meta;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

public class ThreeGPPAlbumBox extends ThreeGPPMetadataBox {
   private int trackNumber;

   public ThreeGPPAlbumBox() {
      super("3GPP Album Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      this.trackNumber = this.getLeft(in) > 0L ? in.read() : -1;
   }

   public int getTrackNumber() {
      return this.trackNumber;
   }
}
