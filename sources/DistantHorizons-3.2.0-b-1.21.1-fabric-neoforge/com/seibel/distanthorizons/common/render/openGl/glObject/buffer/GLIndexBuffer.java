package com.seibel.distanthorizons.common.render.openGl.glObject.buffer;

public class GLIndexBuffer extends GLBuffer {
   protected int indicesCount = 0;
   protected int glType = 5125;

   public int getGlType() {
      return this.glType;
   }

   public GLIndexBuffer(boolean isBufferStorage) {
      super(isBufferStorage);
   }

   @Override
   public void destroyAsync() {
      super.destroyAsync();
      this.indicesCount = 0;
   }

   @Override
   public int getBufferBindingTarget() {
      return 34963;
   }
}
