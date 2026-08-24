package com.seibel.distanthorizons.common.render.openGl.glObject.shader;

import com.seibel.distanthorizons.api.objects.math.DhApiMat4f;
import com.seibel.distanthorizons.api.objects.math.DhApiVec3i;
import com.seibel.distanthorizons.core.util.math.DhMat4f;
import com.seibel.distanthorizons.core.util.math.DhVec3f;
import java.awt.Color;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

public class GlShaderProgram {
   public final int id = GL33.glCreateProgram();

   public GlShaderProgram(String vertResourcePath, String fragResourcePath, String attribute) {
      this(vertResourcePath, fragResourcePath, new String[]{attribute});
   }

   public GlShaderProgram(String vertResourcePath, String fragResourcePath, String[] attributes) {
      String shaderString = GlShader.loadFile(vertResourcePath, false);
      GlShader vertShader = new GlShader(35633, shaderString);
      GL33.glAttachShader(this.id, vertShader.id);
      vertShader.free();
      shaderString = GlShader.loadFile(fragResourcePath, false);
      vertShader = new GlShader(35632, shaderString);
      GL33.glAttachShader(this.id, vertShader.id);
      vertShader.free();

      for (int i = 0; i < attributes.length; i++) {
         GL33.glBindAttribLocation(this.id, i, attributes[i]);
      }

      GL33.glLinkProgram(this.id);
      int status = GL33.glGetProgrami(this.id, 35714);
      if (status != 1) {
         String message = "Shader Link Error. Details: " + GL33.glGetProgramInfoLog(this.id);
         this.free();
         throw new RuntimeException(message);
      } else {
         GL33.glUseProgram(this.id);
      }
   }

   public void bind() {
      GL33.glUseProgram(this.id);
   }

   public void unbind() {
      GL33.glUseProgram(0);
   }

   public void free() {
      GL33.glDeleteProgram(this.id);
   }

   public int getAttributeLocation(CharSequence name) {
      int i = GL33.glGetAttribLocation(this.id, name);
      if (i == -1) {
         throw new RuntimeException("Attribute name not found: " + name);
      } else {
         return i;
      }
   }

   public int tryGetAttributeLocation(CharSequence name) {
      return GL33.glGetAttribLocation(this.id, name);
   }

   public int getUniformLocation(CharSequence name) throws RuntimeException {
      int i = GL33.glGetUniformLocation(this.id, name);
      if (i == -1) {
         throw new RuntimeException("Uniform name not found: " + name);
      } else {
         return i;
      }
   }

   public int tryGetUniformLocation(CharSequence name) {
      return GL33.glGetUniformLocation(this.id, name);
   }

   public void setUniform(int location, boolean value) {
      GL33.glUniform1i(location, value ? 1 : 0);
   }

   public void trySetUniform(int location, boolean value) {
      if (location != -1) {
         this.setUniform(location, value);
      }
   }

   public void setUniform(int location, int value) {
      GL33.glUniform1i(location, value);
   }

   public void trySetUniform(int location, int value) {
      if (location != -1) {
         this.setUniform(location, value);
      }
   }

   public void setUniform(int location, float value) {
      GL33.glUniform1f(location, value);
   }

   public void trySetUniform(int location, float value) {
      if (location != -1) {
         this.setUniform(location, value);
      }
   }

   public void setUniform(int location, DhVec3f value) {
      GL33.glUniform3f(location, value.x, value.y, value.z);
   }

   public void trySetUniform(int location, DhVec3f value) {
      if (location != -1) {
         this.setUniform(location, value);
      }
   }

   public void setUniform(int location, DhApiVec3i value) {
      GL33.glUniform3i(location, value.x, value.y, value.z);
   }

   public void trySetUniform(int location, DhApiVec3i value) {
      if (location != -1) {
         this.setUniform(location, value);
      }
   }

   public void setUniform(int location, DhApiMat4f value) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         FloatBuffer buffer = stack.mallocFloat(16);
         storeMatrixInBuffer(value, buffer);
         GL33.glUniformMatrix4fv(location, false, buffer);
      } catch (Throwable var7) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (stack != null) {
         stack.close();
      }
   }

   private static void storeMatrixInBuffer(DhApiMat4f matrix, FloatBuffer floatBuffer) {
      floatBuffer.put(bufferIndex(0, 0), matrix.m00);
      floatBuffer.put(bufferIndex(0, 1), matrix.m01);
      floatBuffer.put(bufferIndex(0, 2), matrix.m02);
      floatBuffer.put(bufferIndex(0, 3), matrix.m03);
      floatBuffer.put(bufferIndex(1, 0), matrix.m10);
      floatBuffer.put(bufferIndex(1, 1), matrix.m11);
      floatBuffer.put(bufferIndex(1, 2), matrix.m12);
      floatBuffer.put(bufferIndex(1, 3), matrix.m13);
      floatBuffer.put(bufferIndex(2, 0), matrix.m20);
      floatBuffer.put(bufferIndex(2, 1), matrix.m21);
      floatBuffer.put(bufferIndex(2, 2), matrix.m22);
      floatBuffer.put(bufferIndex(2, 3), matrix.m23);
      floatBuffer.put(bufferIndex(3, 0), matrix.m30);
      floatBuffer.put(bufferIndex(3, 1), matrix.m31);
      floatBuffer.put(bufferIndex(3, 2), matrix.m32);
      floatBuffer.put(bufferIndex(3, 3), matrix.m33);
   }

   private static int bufferIndex(int xIndex, int zIndex) {
      return zIndex * 4 + xIndex;
   }

   public void trySetUniform(int location, DhMat4f value) {
      if (location != -1) {
         this.setUniform(location, value);
      }
   }

   public void setUniform(int location, Color value) {
      GL33.glUniform4f(location, value.getRed() / 256.0F, value.getGreen() / 256.0F, value.getBlue() / 256.0F, value.getAlpha() / 256.0F);
   }

   public void trySetUniform(int location, Color value) {
      if (location != -1) {
         this.setUniform(location, value);
      }
   }
}
