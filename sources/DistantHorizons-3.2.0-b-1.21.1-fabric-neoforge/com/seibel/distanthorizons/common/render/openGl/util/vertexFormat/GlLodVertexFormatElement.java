package com.seibel.distanthorizons.common.render.openGl.util.vertexFormat;

public class GlLodVertexFormatElement {
   private final GlLodVertexFormatElement.DataType dataType;
   private final int index;
   private final int count;
   private final int byteSize;
   private final boolean isPadding;

   public GlLodVertexFormatElement(int newIndex, GlLodVertexFormatElement.DataType newType, int newCount, boolean isPadding) {
      this.dataType = newType;
      this.index = newIndex;
      this.count = newCount;
      this.byteSize = newType.getSize() * this.count;
      this.isPadding = isPadding;
   }

   public final boolean getIsPadding() {
      return this.isPadding;
   }

   public final GlLodVertexFormatElement.DataType getType() {
      return this.dataType;
   }

   public final int getIndex() {
      return this.index;
   }

   public final int getByteSize() {
      return this.byteSize;
   }

   public int getElementCount() {
      return this.count;
   }

   @Override
   public int hashCode() {
      int i = this.dataType.hashCode();
      i = 31 * i + this.index;
      return 31 * i + this.count;
   }

   @Override
   public String toString() {
      return this.count + "," + this.dataType.getName();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         GlLodVertexFormatElement LodVertexFormatElement = (GlLodVertexFormatElement)obj;
         if (this.count != LodVertexFormatElement.count) {
            return false;
         } else if (this.index != LodVertexFormatElement.index) {
            return false;
         } else {
            return this.dataType != LodVertexFormatElement.dataType ? false : false;
         }
      } else {
         return false;
      }
   }

   public static enum DataType {
      FLOAT(4, "Float", 5126),
      UBYTE(1, "Unsigned Byte", 5121),
      BYTE(1, "Byte", 5120),
      USHORT(2, "Unsigned Short", 5123),
      SHORT(2, "Short", 5122),
      UINT(4, "Unsigned Int", 5125),
      INT(4, "Int", 5124);

      private final int size;
      private final String name;
      private final int glType;

      private DataType(int sizeInBytes, String newName, int openGlDataType) {
         this.size = sizeInBytes;
         this.name = newName;
         this.glType = openGlDataType;
      }

      public int getSize() {
         return this.size;
      }

      public String getName() {
         return this.name;
      }

      public int getGlType() {
         return this.glType;
      }
   }
}
