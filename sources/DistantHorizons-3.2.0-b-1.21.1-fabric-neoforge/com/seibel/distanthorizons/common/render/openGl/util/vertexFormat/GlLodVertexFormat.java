package com.seibel.distanthorizons.common.render.openGl.util.vertexFormat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.stream.Collectors;

public class GlLodVertexFormat {
   public static final GlLodVertexFormat DH_VERTEX_FORMAT = GlVertexFormats.POSITION_COLOR_BLOCK_LIGHT_SKY_LIGHT_MATERIAL_ID_NORMAL_INDEX;
   private final ImmutableList<GlLodVertexFormatElement> elements;
   private final IntList offsets = new IntArrayList();
   private final int byteSize;

   public GlLodVertexFormat(ImmutableList<GlLodVertexFormatElement> elementList) {
      this.elements = elementList;
      int i = 0;
      UnmodifiableIterator var3 = elementList.iterator();

      while (var3.hasNext()) {
         GlLodVertexFormatElement LodVertexFormatElement = (GlLodVertexFormatElement)var3.next();
         this.offsets.add(i);
         i += LodVertexFormatElement.getByteSize();
      }

      this.byteSize = i;
   }

   public int getByteSize() {
      return this.byteSize;
   }

   public ImmutableList<GlLodVertexFormatElement> getElements() {
      return this.elements;
   }

   public int getOffset(int index) {
      return this.offsets.getInt(index);
   }

   @Override
   public String toString() {
      return "format: " + this.elements.size() + " elements: " + this.elements.stream().map(Object::toString).collect(Collectors.joining(" "));
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         GlLodVertexFormat vertexFormat = (GlLodVertexFormat)obj;
         return this.byteSize == vertexFormat.byteSize && this.elements.equals(vertexFormat.elements);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.elements.hashCode();
   }
}
