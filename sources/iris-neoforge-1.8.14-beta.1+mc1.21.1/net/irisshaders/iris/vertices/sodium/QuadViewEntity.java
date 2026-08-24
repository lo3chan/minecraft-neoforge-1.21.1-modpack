package net.irisshaders.iris.vertices.sodium;

import net.irisshaders.iris.vertices.MemoryAccess;
import net.irisshaders.iris.vertices.views.QuadView;

public class QuadViewEntity implements QuadView {
   private long writePointer;
   private int stride;

   public void setup(long writePointer, int stride) {
      this.writePointer = writePointer;
      this.stride = stride;
   }

   @Override
   public float x(int index) {
      return MemoryAccess.getFloat(this.writePointer - this.stride * (3L - index));
   }

   @Override
   public float y(int index) {
      return MemoryAccess.getFloat(this.writePointer + 4L - this.stride * (3L - index));
   }

   @Override
   public float z(int index) {
      return MemoryAccess.getFloat(this.writePointer + 8L - this.stride * (3L - index));
   }

   @Override
   public float u(int index) {
      return MemoryAccess.getFloat(this.writePointer + 16L - this.stride * (3L - index));
   }

   @Override
   public float v(int index) {
      return MemoryAccess.getFloat(this.writePointer + 20L - this.stride * (3L - index));
   }
}
