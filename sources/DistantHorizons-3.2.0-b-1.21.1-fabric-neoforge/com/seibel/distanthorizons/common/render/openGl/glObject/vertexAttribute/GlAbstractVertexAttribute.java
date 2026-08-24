package com.seibel.distanthorizons.common.render.openGl.glObject.vertexAttribute;

import com.seibel.distanthorizons.common.render.openGl.glObject.GLProxy;
import org.lwjgl.opengl.GL33;

public abstract class GlAbstractVertexAttribute {
   public final int id = GL33.glGenVertexArrays();

   protected GlAbstractVertexAttribute() {
      GL33.glBindVertexArray(this.id);
   }

   public static GlAbstractVertexAttribute create() {
      return (GlAbstractVertexAttribute)(GLProxy.getInstance().vertexAttributeBufferBindingSupported
         ? new GlVertexAttributePostGL43()
         : new GlVertexAttributePreGL43());
   }

   public void bind() {
      GL33.glBindVertexArray(this.id);
   }

   public void unbind() {
      GL33.glBindVertexArray(0);
   }

   public void free() {
      GL33.glDeleteVertexArrays(this.id);
   }

   public abstract void bindBufferToAllBindingPoints(int i);

   public abstract void bindBufferToBindingPoint(int i, int j);

   public abstract void unbindBuffersFromAllBindingPoint();

   public abstract void unbindBuffersFromBindingPoint(int i);

   public abstract void setVertexAttribute(int i, int j, GlVertexPointer glVertexPointer);

   public abstract void completeAndCheck(int i);
}
