package net.sourceforge.jaad.mp4.boxes.impl.sampleentries;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

public class XMLMetadataSampleEntry extends MetadataSampleEntry {
   private String namespace;
   private String schemaLocation;

   public XMLMetadataSampleEntry() {
      super("XML Metadata Sample Entry");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      this.namespace = in.readUTFString((int)this.getLeft(in), "UTF-8");
      this.schemaLocation = in.readUTFString((int)this.getLeft(in), "UTF-8");
   }

   public String getNamespace() {
      return this.namespace;
   }

   public String getSchemaLocation() {
      return this.schemaLocation;
   }
}
