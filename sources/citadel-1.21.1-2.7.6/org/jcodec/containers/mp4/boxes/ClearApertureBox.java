package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

public class ClearApertureBox extends FullBox {
   public static final String CLEF = "clef";
   protected float width;
   protected float height;

   public static ClearApertureBox createClearApertureBox(int width, int height) {
      ClearApertureBox clef = new ClearApertureBox(new Header("clef"));
      clef.width = width;
      clef.height = height;
      return clef;
   }

   public ClearApertureBox(Header atom) {
      super(atom);
   }

   @Override
   public void parse(ByteBuffer input) {
      super.parse(input);
      this.width = input.getInt() / 65536.0F;
      this.height = input.getInt() / 65536.0F;
   }

   @Override
   protected void doWrite(ByteBuffer out) {
      super.doWrite(out);
      out.putInt((int)(this.width * 65536.0F));
      out.putInt((int)(this.height * 65536.0F));
   }

   @Override
   public int estimateSize() {
      return 20;
   }

   public float getWidth() {
      return this.width;
   }

   public float getHeight() {
      return this.height;
   }
}
