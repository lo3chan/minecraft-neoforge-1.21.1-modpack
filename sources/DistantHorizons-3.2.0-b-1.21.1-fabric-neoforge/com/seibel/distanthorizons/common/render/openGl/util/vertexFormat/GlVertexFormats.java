package com.seibel.distanthorizons.common.render.openGl.util.vertexFormat;

import com.google.common.collect.ImmutableList;

public class GlVertexFormats {
   public static final GlLodVertexFormatElement ELEMENT_POSITION = new GlLodVertexFormatElement(3, GlLodVertexFormatElement.DataType.USHORT, 3, false);
   public static final GlLodVertexFormatElement ELEMENT_COLOR = new GlLodVertexFormatElement(0, GlLodVertexFormatElement.DataType.UBYTE, 4, false);
   public static final GlLodVertexFormatElement ELEMENT_BYTE_PADDING = new GlLodVertexFormatElement(0, GlLodVertexFormatElement.DataType.BYTE, 1, true);
   public static final GlLodVertexFormatElement ELEMENT_LIGHT = new GlLodVertexFormatElement(0, GlLodVertexFormatElement.DataType.UBYTE, 1, false);
   public static final GlLodVertexFormatElement ELEMENT_IRIS_MATERIAL_INDEX = new GlLodVertexFormatElement(0, GlLodVertexFormatElement.DataType.BYTE, 1, false);
   public static final GlLodVertexFormatElement ELEMENT_IRIS_NORMAL_INDEX = new GlLodVertexFormatElement(0, GlLodVertexFormatElement.DataType.BYTE, 1, false);
   public static final GlLodVertexFormat POSITION_COLOR_BLOCK_LIGHT_SKY_LIGHT_MATERIAL_ID_NORMAL_INDEX = new GlLodVertexFormat(
      ImmutableList.builder()
         .add(ELEMENT_POSITION)
         .add(ELEMENT_BYTE_PADDING)
         .add(ELEMENT_LIGHT)
         .add(ELEMENT_COLOR)
         .add(ELEMENT_IRIS_MATERIAL_INDEX)
         .add(ELEMENT_IRIS_NORMAL_INDEX)
         .add(ELEMENT_BYTE_PADDING)
         .add(ELEMENT_BYTE_PADDING)
         .build()
   );
}
