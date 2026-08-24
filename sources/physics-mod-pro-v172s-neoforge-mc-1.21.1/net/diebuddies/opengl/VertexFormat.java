package net.diebuddies.opengl;

import org.lwjgl.opengl.GL32C;

public class VertexFormat {
   private Data[] format;
   private int stride;

   public VertexFormat(Data... format) {
      this.format = format;

      for (int i = 0; i < format.length; i++) {
         this.stride = this.stride + format[i].getStride();
      }
   }

   public void bindAttributeFormat() {
      int offset = 0;

      for (int i = 0; i < this.format.length; i++) {
         Data attribute = this.format[i];
         GL32C.glEnableVertexAttribArray(attribute.getAttribute());
         if (attribute.isPureInteger()) {
            GL32C.glVertexAttribIPointer(attribute.getAttribute(), attribute.getSize(), attribute.getDataType(), this.stride, offset);
         } else {
            GL32C.glVertexAttribPointer(attribute.getAttribute(), attribute.getSize(), attribute.getDataType(), attribute.normalize(), this.stride, offset);
         }

         offset += attribute.getStride();
      }
   }

   public int getStride() {
      return this.stride;
   }
}
