package com.seibel.distanthorizons.common.render.openGl.glObject.vertexAttribute;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import org.lwjgl.opengl.GL33;

public final class GlVertexAttributePreGL43 extends GlAbstractVertexAttribute {
   private static final DhLogger LOGGER = new DhLoggerBuilder()
      .fileLevelConfig(Config.Common.Logging.logRendererGLEventToFile)
      .chatLevelConfig(Config.Common.Logging.logRendererGLEventToChat)
      .build();
   int strideSize = 0;
   int[][] bindingPointsToIndex;
   GlVertexPointer[] pointers;
   int[] pointersOffset;
   TreeMap<Integer, TreeSet<Integer>> bindingPointsToIndexBuilder = new TreeMap<>();
   ArrayList<GlVertexPointer> pointersBuilder = new ArrayList<>();

   @Override
   public void bindBufferToAllBindingPoints(int buffer) {
      for (int i = 0; i < this.pointers.length; i++) {
         GL33.glEnableVertexAttribArray(i);
      }

      for (int i = 0; i < this.pointers.length; i++) {
         GlVertexPointer pointer = this.pointers[i];
         if (pointer != null) {
            if (pointer.useInteger) {
               GL33.glVertexAttribIPointer(i, pointer.elementCount, pointer.glType, this.strideSize, this.pointersOffset[i]);
            } else {
               GL33.glVertexAttribPointer(i, pointer.elementCount, pointer.glType, pointer.normalized, this.strideSize, this.pointersOffset[i]);
            }
         }
      }
   }

   @Override
   public void bindBufferToBindingPoint(int buffer, int bindingPoint) {
      int[] bindingPointIndexes = this.bindingPointsToIndex[bindingPoint];

      for (int bindingPointIndex : bindingPointIndexes) {
         GL33.glEnableVertexAttribArray(bindingPointIndex);
      }

      for (int bindingPointIndex : bindingPointIndexes) {
         GlVertexPointer pointer = this.pointers[bindingPointIndex];
         if (pointer != null) {
            if (pointer.useInteger) {
               GL33.glVertexAttribIPointer(bindingPointIndex, pointer.elementCount, pointer.glType, this.strideSize, this.pointersOffset[bindingPointIndex]);
            } else {
               GL33.glVertexAttribPointer(
                  bindingPointIndex, pointer.elementCount, pointer.glType, pointer.normalized, this.strideSize, this.pointersOffset[bindingPointIndex]
               );
            }
         }
      }
   }

   @Override
   public void unbindBuffersFromAllBindingPoint() {
      for (int i = 0; i < this.pointers.length; i++) {
         GL33.glDisableVertexAttribArray(i);
      }
   }

   @Override
   public void unbindBuffersFromBindingPoint(int bindingPoint) {
      int[] bindingPointIndexes = this.bindingPointsToIndex[bindingPoint];

      for (int bindingPointIndex : bindingPointIndexes) {
         GL33.glDisableVertexAttribArray(bindingPointIndex);
      }
   }

   @Override
   public void setVertexAttribute(int bindingPoint, int attributeIndex, GlVertexPointer attribute) {
      TreeSet<Integer> intArray = this.bindingPointsToIndexBuilder.computeIfAbsent(bindingPoint, k -> new TreeSet<>());
      intArray.add(attributeIndex);

      while (this.pointersBuilder.size() <= attributeIndex) {
         this.pointersBuilder.add(null);
      }

      this.pointersBuilder.set(attributeIndex, attribute);
   }

   @Override
   public void completeAndCheck(int expectedStrideSize) {
      int maxBindPointNumber = this.bindingPointsToIndexBuilder.lastKey();
      this.bindingPointsToIndex = new int[maxBindPointNumber + 1][];
      this.bindingPointsToIndexBuilder.forEach((ix, set) -> {
         this.bindingPointsToIndex[ix] = new int[set.size()];
         Iterator<Integer> iter = set.iterator();

         for (int j = 0; j < set.size(); j++) {
            this.bindingPointsToIndex[ix][j] = iter.next();
         }
      });
      this.pointers = this.pointersBuilder.toArray(new GlVertexPointer[this.pointersBuilder.size()]);
      this.pointersOffset = new int[this.pointers.length];
      this.pointersBuilder = null;
      this.bindingPointsToIndexBuilder = null;
      int currentOffset = 0;

      for (int i = 0; i < this.pointers.length; i++) {
         GlVertexPointer pointer = this.pointers[i];
         if (pointer == null) {
            LOGGER.warn("Vertex Attribute index " + i + " is not set! No index should be skipped normally!");
         } else {
            this.pointersOffset[i] = currentOffset;
            currentOffset += pointer.byteSize;
         }
      }

      if (currentOffset != expectedStrideSize) {
         LOGGER.error(
            "Vertex Attribute calculated stride size " + currentOffset + " does not match the provided expected stride size " + expectedStrideSize + "!"
         );
         throw new IllegalArgumentException("Vertex Attribute Incorrect Format");
      } else {
         this.strideSize = currentOffset;
         LOGGER.info("Vertex Attribute (pre GL43) completed.");
         LOGGER.debug("AttributeIndex: ElementCount, glType, normalized, strideSize, offset");

         for (int ix = 0; ix < this.pointers.length; ix++) {
            GlVertexPointer pointer = this.pointers[ix];
            if (pointer == null) {
               LOGGER.debug(ix + ": Null!!!!");
            } else {
               LOGGER.debug(
                  ix
                     + ": "
                     + pointer.elementCount
                     + ", "
                     + pointer.glType
                     + ", "
                     + pointer.normalized
                     + ", "
                     + this.strideSize
                     + ", "
                     + this.pointersOffset[ix]
               );
            }
         }
      }
   }
}
