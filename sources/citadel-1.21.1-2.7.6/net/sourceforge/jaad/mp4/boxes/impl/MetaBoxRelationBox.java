package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

public class MetaBoxRelationBox extends FullBox {
   private long firstMetaboxHandlerType;
   private long secondMetaboxHandlerType;
   private int metaboxRelation;

   public MetaBoxRelationBox() {
      super("Meta Box Relation Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      this.firstMetaboxHandlerType = in.readBytes(4);
      this.secondMetaboxHandlerType = in.readBytes(4);
      this.metaboxRelation = in.read();
   }

   public long getFirstMetaboxHandlerType() {
      return this.firstMetaboxHandlerType;
   }

   public long getSecondMetaboxHandlerType() {
      return this.secondMetaboxHandlerType;
   }

   public int getMetaboxRelation() {
      return this.metaboxRelation;
   }
}
