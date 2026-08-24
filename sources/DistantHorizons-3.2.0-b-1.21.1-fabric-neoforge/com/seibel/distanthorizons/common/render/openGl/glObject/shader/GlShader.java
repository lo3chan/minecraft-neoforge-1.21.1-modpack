package com.seibel.distanthorizons.common.render.openGl.glObject.shader;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

public class GlShader {
   private static final DhLogger LOGGER = new DhLoggerBuilder()
      .fileLevelConfig(Config.Common.Logging.logRendererGLEventToFile)
      .chatLevelConfig(Config.Common.Logging.logRendererGLEventToChat)
      .build();
   public final int id;

   public GlShader(int type, String sourceString) {
      LOGGER.info("Loading shader with type: [" + type + "]");
      LOGGER.debug("Source: \n[" + sourceString + "]");
      if (sourceString != null && !sourceString.isEmpty()) {
         this.id = GL33.glCreateShader(type);
         if (this.id == 0) {
            throw new IllegalArgumentException("Failed to create shader with type [" + type + "] and Source: \n[" + sourceString + "].");
         } else {
            safeShaderSource(this.id, sourceString);
            GL33.glCompileShader(this.id);
            int status = GL33.glGetShaderi(this.id, 35713);
            if (status != 1) {
               String message = "Shader compiler error. Details: [" + GL33.glGetShaderInfoLog(this.id) + "]\n";
               message = message + "Source: \n[" + sourceString + "]";
               this.free();
               throw new RuntimeException(message);
            } else {
               LOGGER.info("Shader loaded sucessfully.");
            }
         }
      } else {
         throw new IllegalArgumentException("No shader source given.");
      }
   }

   private static void safeShaderSource(@NativeType("GLuint") int glId, @NativeType("GLchar const **") CharSequence source) {
      MemoryStack stack = MemoryStack.stackGet();
      int stackPointer = stack.getPointer();

      try {
         ByteBuffer sourceBuffer = MemoryUtil.memUTF8(source, true);
         PointerBuffer pointers = stack.mallocPointer(1);
         pointers.put(sourceBuffer);
         GL33.nglShaderSource(glId, 1, pointers.address0(), 0L);
         APIUtil.apiArrayFree(pointers.address0(), 1);
      } finally {
         stack.setPointer(stackPointer);
      }
   }

   public void free() {
      GL33.glDeleteShader(this.id);
   }

   public static String loadFile(String path, boolean absoluteFilePath) {
      StringBuilder stringBuilder = new StringBuilder();

      try {
         InputStream in;
         if (absoluteFilePath) {
            in = new FileInputStream(path);
         } else {
            in = GlShader.class.getClassLoader().getResourceAsStream(path);
            if (in == null) {
               throw new FileNotFoundException("Shader file not found in resource: " + path);
            }
         }

         BufferedReader reader = new BufferedReader(new InputStreamReader(in));

         String line;
         while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
         }
      } catch (IOException var6) {
         throw new RuntimeException("Unable to load shader from file [" + path + "]. Error: " + var6.getMessage());
      }

      return stringBuilder.toString();
   }
}
