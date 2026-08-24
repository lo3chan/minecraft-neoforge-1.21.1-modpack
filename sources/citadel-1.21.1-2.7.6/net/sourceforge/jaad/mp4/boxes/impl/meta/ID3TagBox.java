package net.sourceforge.jaad.mp4.boxes.impl.meta;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;
import net.sourceforge.jaad.mp4.boxes.Utils;

public class ID3TagBox extends FullBox {
   private String language;
   private byte[] id3Data;

   public ID3TagBox() {
      super("ID3 Tag Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      this.language = Utils.getLanguageCode(in.readBytes(2));
      this.id3Data = new byte[(int)this.getLeft(in)];
      in.readBytes(this.id3Data);
   }

   public byte[] getID3Data() {
      return this.id3Data;
   }

   public String getLanguage() {
      return this.language;
   }
}
