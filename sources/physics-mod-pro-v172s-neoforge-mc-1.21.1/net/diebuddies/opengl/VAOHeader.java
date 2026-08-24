package net.diebuddies.opengl;

import java.util.List;

public class VAOHeader {
   public int vaoID;
   public int[] bindings;
   public int boundElementBuffer;

   public VAOHeader(int vaoID, List<Data> layout) {
      this.vaoID = vaoID;
      this.bindings = new int[layout.size()];
   }
}
