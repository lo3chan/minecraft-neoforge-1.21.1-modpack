package net.sourceforge.jaad.mp4.boxes.impl.meta;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

public class ThreeGPPKeywordsBox extends ThreeGPPMetadataBox {
   private String[] keywords;

   public ThreeGPPKeywordsBox() {
      super("3GPP Keywords Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      this.decodeCommon(in);
      int count = in.read();
      this.keywords = new String[count];

      for (int i = 0; i < count; i++) {
         int len = in.read();
         this.keywords[i] = in.readUTFString(len);
      }
   }

   public String[] getKeywords() {
      return this.keywords;
   }
}
