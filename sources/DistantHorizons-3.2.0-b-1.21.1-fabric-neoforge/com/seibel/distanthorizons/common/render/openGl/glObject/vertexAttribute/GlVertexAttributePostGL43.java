package com.seibel.distanthorizons.common.render.openGl.glObject.vertexAttribute;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import org.lwjgl.opengl.GL43;

public final class GlVertexAttributePostGL43 extends GlAbstractVertexAttribute {
   private static final DhLogger LOGGER = new DhLoggerBuilder()
      .fileLevelConfig(Config.Common.Logging.logRendererGLEventToFile)
      .chatLevelConfig(Config.Common.Logging.logRendererGLEventToChat)
      .build();
   int numberOfBindingPoints = 0;
   int strideSize = 0;

   @Override
   public void bindBufferToAllBindingPoints(int buffer) {
      for (int i = 0; i < this.numberOfBindingPoints; i++) {
         GL43.glBindVertexBuffer(i, buffer, 0L, this.strideSize);
      }
   }

   @Override
   public void bindBufferToBindingPoint(int buffer, int bindingPoint) {
      GL43.glBindVertexBuffer(bindingPoint, buffer, 0L, this.strideSize);
   }

   @Override
   public void unbindBuffersFromAllBindingPoint() {
      for (int i = 0; i < this.numberOfBindingPoints; i++) {
         GL43.glBindVertexBuffer(i, 0, 0L, 0);
      }
   }

   @Override
   public void unbindBuffersFromBindingPoint(int bindingPoint) {
      GL43.glBindVertexBuffer(bindingPoint, 0, 0L, 0);
   }

   @Override
   public void setVertexAttribute(int bindingPoint, int attributeIndex, GlVertexPointer attribute) {
      if (attribute.useInteger) {
         GL43.glVertexAttribIFormat(attributeIndex, attribute.elementCount, attribute.glType, this.strideSize);
      } else {
         GL43.glVertexAttribFormat(attributeIndex, attribute.elementCount, attribute.glType, attribute.normalized, this.strideSize);
      }

      this.strideSize = this.strideSize + attribute.byteSize;
      if (this.numberOfBindingPoints <= bindingPoint) {
         this.numberOfBindingPoints = bindingPoint + 1;
      }

      GL43.glVertexAttribBinding(attributeIndex, bindingPoint);
      GL43.glEnableVertexAttribArray(attributeIndex);
   }

   @Override
   public void completeAndCheck(int expectedStrideSize) {
      if (this.strideSize != expectedStrideSize) {
         LOGGER.error(
            "Vertex Attribute calculated stride size " + this.strideSize + " does not match the provided expected stride size " + expectedStrideSize + "!"
         );
         throw new IllegalArgumentException("Vertex Attribute Incorrect Format");
      } else {
         LOGGER.info(
            "Vertex Attribute (GL43+) completed. It contains " + this.numberOfBindingPoints + " binding points and a stride size of " + this.strideSize
         );
      }
   }
}
