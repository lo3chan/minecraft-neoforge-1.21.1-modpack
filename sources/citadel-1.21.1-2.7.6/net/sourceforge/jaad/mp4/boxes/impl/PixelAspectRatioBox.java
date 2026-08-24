package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.BoxImpl;

public class PixelAspectRatioBox extends BoxImpl {
   private long hSpacing;
   private long vSpacing;

   public PixelAspectRatioBox() {
      super("Pixel Aspect Ratio Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      this.hSpacing = in.readBytes(4);
      this.vSpacing = in.readBytes(4);
   }

   public long getHorizontalSpacing() {
      return this.hSpacing;
   }

   public long getVerticalSpacing() {
      return this.vSpacing;
   }
}
