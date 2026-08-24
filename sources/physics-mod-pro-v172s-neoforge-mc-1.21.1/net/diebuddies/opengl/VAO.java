package net.diebuddies.opengl;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.List;
import net.diebuddies.physics.StarterClient;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.ARBInstancedArrays;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.opengl.GL43C;
import org.lwjgl.opengl.GL44C;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class VAO {
   private static boolean VERTEX_ATTRIB_BINDING_SUPPORT;
   private static boolean MULTI_BIND_SUPPORT;
   private static VAO.VertexAttribBinding vertexAttribBinding;
   private static VAO.MultiBind multiBind;
   private static boolean init = false;
   private static Int2ObjectMap<VAOHeader> vaoHeaders = new Int2ObjectOpenHashMap();
   protected static int bound = 0;
   protected static int previouslyBound = 0;
   protected static int previouslyBoundArray = 0;
   protected static int previouslyBoundElement = 0;
   protected boolean destroyed;
   private int referenceCounter = 1;
   public int id;
   public Usage usage;
   public VAOHeader header;
   private IntBuffer vbuffers;
   private PointerBuffer voffsets;
   private IntBuffer vstrides;
   public List<VAO.BufferObjectData> dataBuffer = new ObjectArrayList();
   public List<Data> dataLayout = new ObjectArrayList();
   public int dataLayoutHashCode;
   public BufferObject indexBuffer;
   public int numberIndices;
   public int indexBufferType;

   public VAO(Usage usage) {
      this.usage = usage;
      this.numberIndices = 0;
      if (!init) {
         MULTI_BIND_SUPPORT = GL.getCapabilities().GL_ARB_multi_bind || GL.getCapabilities().OpenGL44;
         if (GL.getCapabilities().OpenGL44) {
            multiBind = new VAO.GL44MultiBind();
         } else if (GL.getCapabilities().GL_ARB_multi_bind) {
            multiBind = new VAO.ARBMultiBind();
         }

         if (MULTI_BIND_SUPPORT) {
            StarterClient.logger.info("enabled vertex attrib rendering path");
            VERTEX_ATTRIB_BINDING_SUPPORT = GL.getCapabilities().GL_ARB_vertex_attrib_binding || GL.getCapabilities().OpenGL43;
            if (GL.getCapabilities().OpenGL43) {
               vertexAttribBinding = new VAO.GL43VertexAttribBinding();
            } else if (GL.getCapabilities().GL_ARB_vertex_attrib_binding) {
               vertexAttribBinding = new VAO.ARBVertexAttribBinding();
            }
         }

         init = true;
      }
   }

   public VAO() {
      this(Usage.STATIC);
      this.id = this.createBuffer();
   }

   protected int createBuffer() {
      return GL32C.glGenVertexArrays();
   }

   public void bind() {
      StateTracker.bindVertexArray(this.id);
      if (VERTEX_ATTRIB_BINDING_SUPPORT) {
         this.bindVertexBuffers();
      }
   }

   private void bindVertexBuffers() {
      VAO.BufferObjectData bod = this.dataBuffer.get(0);
      if (this.header.bindings[0] != bod.buffer.id) {
         this.header.bindings[0] = bod.buffer.id;
         multiBind.glBindVertexBuffers(0, this.vbuffers, this.voffsets, this.vstrides);
      }

      if (this.indexBuffer != null && this.header.boundElementBuffer != this.indexBuffer.id) {
         this.indexBuffer.bind();
         this.header.boundElementBuffer = this.indexBuffer.id;
      }
   }

   public static void unbind() {
      StateTracker.unbindVertexArray();
   }

   public void render() {
      this.render(4);
   }

   public void renderNoBind() {
      this.renderNoBind(4);
   }

   public void render(int type) {
      if (this.numberIndices != 0) {
         this.bind();
         if (this.indexBuffer == null) {
            GL32C.glDrawArrays(type, 0, this.numberIndices);
         } else {
            GL32C.glDrawElements(type, this.numberIndices, this.indexBufferType, 0L);
         }
      }
   }

   public void renderNoBind(int type) {
      if (this.numberIndices != 0) {
         if (this.indexBuffer == null) {
            GL32C.glDrawArrays(type, 0, this.numberIndices);
         } else {
            GL32C.glDrawElements(type, this.numberIndices, this.indexBufferType, 0L);
         }
      }
   }

   public void renderArrays(int type, int first, int count) {
      if (count != 0) {
         this.bind();
         GL32C.glDrawArrays(type, first, count);
      }
   }

   public void renderEmptyTriangle() {
      StateTracker.bindVertexArray(this.id);
      GL32C.glDrawArrays(4, 0, 3);
   }

   public void renderInstanced(int count) {
      this.renderInstanced(4, count);
   }

   public void renderInstanced(int type, int count) {
      this.bind();
      if (this.indexBuffer == null) {
         GL32C.glDrawArraysInstanced(type, 0, this.numberIndices, count);
      } else {
         GL32C.glDrawElementsInstanced(type, this.numberIndices, this.indexBufferType, 0L, count);
      }
   }

   protected void bindAttributes() {
      StateTracker.bindVertexArray(this.id);

      for (int i = 0; i < this.dataBuffer.size(); i++) {
         VAO.BufferObjectData bod = this.dataBuffer.get(i);
         if (bod.buffer != null) {
            bod.buffer.bind();
         }

         GL32C.glEnableVertexAttribArray(bod.data.getAttribute());
         if (bod.data.isPureInteger()) {
            if (VERTEX_ATTRIB_BINDING_SUPPORT) {
               vertexAttribBinding.glVertexAttribIFormat(bod.data.getAttribute(), bod.data.getSize(), bod.data.getDataType(), 0);
            } else {
               GL32C.glVertexAttribIPointer(bod.data.getAttribute(), bod.data.getSize(), bod.data.getDataType(), bod.data.getStride(), 0L);
            }
         } else if (VERTEX_ATTRIB_BINDING_SUPPORT) {
            vertexAttribBinding.glVertexAttribFormat(bod.data.getAttribute(), bod.data.getSize(), bod.data.getDataType(), bod.data.normalize(), 0);
         } else {
            GL32C.glVertexAttribPointer(bod.data.getAttribute(), bod.data.getSize(), bod.data.getDataType(), bod.data.normalize(), bod.data.getStride(), 0L);
         }

         if (VERTEX_ATTRIB_BINDING_SUPPORT) {
            vertexAttribBinding.glVertexAttribBinding(bod.data.getAttribute(), i);
            if (bod.buffer != null) {
               vertexAttribBinding.glBindVertexBuffer(i, bod.buffer.id, 0L, bod.data.getStride());
            }
         }

         if (bod.data.isInstanced()) {
            if (VERTEX_ATTRIB_BINDING_SUPPORT) {
               vertexAttribBinding.glVertexBindingDivisor(i, 1);
            } else if (GL.getCapabilities().OpenGL33) {
               GL33C.glVertexAttribDivisor(bod.data.getAttribute(), 1);
            } else if (GL.getCapabilities().GL_ARB_instanced_arrays) {
               ARBInstancedArrays.glVertexAttribDivisorARB(bod.data.getAttribute(), 1);
            }
         }
      }
   }

   public void finish(int[] indices, int length) {
      if (VERTEX_ATTRIB_BINDING_SUPPORT) {
         int hashCode = this.dataLayout.hashCode();
         this.header = (VAOHeader)vaoHeaders.get(hashCode);
         if (this.header == null) {
            this.id = this.createBuffer();
            StateTracker.bindVertexArray(this.id);
            if (indices != null) {
               this.attachIndices(indices, length);
            } else {
               this.numberIndices = length;
            }

            this.bindAttributes();
            this.header = new VAOHeader(this.id, this.dataLayout);
            vaoHeaders.put(hashCode, this.header);
            if (this.indexBuffer != null) {
               this.header.boundElementBuffer = 0;
            }
         } else {
            this.id = this.header.vaoID;
            StateTracker.bindVertexArray(this.id);
            if (indices != null) {
               this.attachIndices(indices, length);
            } else {
               this.numberIndices = length;
            }

            if (this.indexBuffer != null) {
               this.header.boundElementBuffer = 0;
            }
         }

         int size = this.dataBuffer.size();
         this.vbuffers = MemoryUtil.memAllocInt(size);
         this.voffsets = MemoryUtil.memCallocPointer(size);
         this.vstrides = MemoryUtil.memAllocInt(size);
         long buffersPointer = MemoryUtil.memAddress(this.vbuffers);
         long stridesPointer = MemoryUtil.memAddress(this.vstrides);

         for (int i = 0; i < this.dataBuffer.size(); i++) {
            VAO.BufferObjectData bod = this.dataBuffer.get(i);
            long offset = i * 4L;
            MemoryUtil.memPutInt(buffersPointer + offset, bod.buffer.id);
            MemoryUtil.memPutInt(stridesPointer + offset, bod.data.getStride());
         }
      } else {
         this.id = this.createBuffer();
         StateTracker.bindVertexArray(this.id);
         if (indices != null) {
            this.attachIndices(indices, length);
         } else {
            this.numberIndices = length;
         }

         this.bindAttributes();
      }
   }

   public void finish(IntBuffer indices, int length) {
      if (VERTEX_ATTRIB_BINDING_SUPPORT) {
         int hashCode = this.dataLayout.hashCode();
         this.header = (VAOHeader)vaoHeaders.get(hashCode);
         if (this.header == null) {
            this.id = this.createBuffer();
            StateTracker.bindVertexArray(this.id);
            if (indices != null) {
               this.attachIndices(indices);
            } else {
               this.numberIndices = length;
            }

            this.bindAttributes();
            this.header = new VAOHeader(this.id, this.dataLayout);
            vaoHeaders.put(hashCode, this.header);
            if (this.indexBuffer != null) {
               this.header.boundElementBuffer = 0;
            }
         } else {
            this.id = this.header.vaoID;
            StateTracker.bindVertexArray(this.id);
            if (indices != null) {
               this.attachIndices(indices);
            } else {
               this.numberIndices = length;
            }

            if (this.indexBuffer != null) {
               this.header.boundElementBuffer = 0;
            }
         }

         int size = this.dataBuffer.size();
         this.vbuffers = MemoryUtil.memAllocInt(size);
         this.voffsets = MemoryUtil.memCallocPointer(size);
         this.vstrides = MemoryUtil.memAllocInt(size);
         long buffersPointer = MemoryUtil.memAddress(this.vbuffers);
         long stridesPointer = MemoryUtil.memAddress(this.vstrides);

         for (int i = 0; i < this.dataBuffer.size(); i++) {
            VAO.BufferObjectData bod = this.dataBuffer.get(i);
            long offset = i * 4L;
            MemoryUtil.memPutInt(buffersPointer + offset, bod.buffer.id);
            MemoryUtil.memPutInt(stridesPointer + offset, bod.data.getStride());
         }
      } else {
         this.id = this.createBuffer();
         StateTracker.bindVertexArray(this.id);
         if (indices != null) {
            this.attachIndices(indices);
         } else {
            this.numberIndices = length;
         }

         this.bindAttributes();
      }
   }

   public void updateAttribute(Data attribute, int[] data, long offset) {
      this.updateAttribute(attribute, data, data.length, offset);
   }

   public void updateAttribute(Data attribute, int[] data, int length, long offset) {
      if (length * 4 <= StarterClient.memoryStack.getSize()) {
         MemoryStack stack = StarterClient.memoryStack.push();

         try {
            IntBuffer fb = stack.mallocInt(length);
            fb.put(0, data, 0, length);
            this.updateAttribute(attribute, fb, offset);
         } catch (Throwable var10) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (stack != null) {
            stack.close();
         }
      } else {
         IntBuffer fb = MemoryUtil.memAllocInt(length);
         fb.put(0, data, 0, length);
         this.updateAttribute(attribute, fb, offset);
         MemoryUtil.memFree(fb);
      }
   }

   public void updateAttribute(Data attribute, float[] data) {
      this.updateAttribute(attribute, data, data.length);
   }

   public void updateAttribute(Data attribute, float[] data, int length) {
      if (length * 4 <= StarterClient.memoryStack.getSize()) {
         MemoryStack stack = StarterClient.memoryStack.push();

         try {
            FloatBuffer fb = stack.mallocFloat(length);
            fb.put(0, data, 0, length);
            this.updateAttribute(attribute, fb);
         } catch (Throwable var8) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (stack != null) {
            stack.close();
         }
      } else {
         FloatBuffer fb = MemoryUtil.memAllocFloat(length);
         fb.put(0, data, 0, length);
         this.updateAttribute(attribute, fb);
         MemoryUtil.memFree(fb);
      }
   }

   public void updateAttribute(Data attribute, byte[] data) {
      this.updateAttribute(attribute, data, data.length);
   }

   public void updateAttribute(Data attribute, byte[] data, int length) {
      if (length * 1 <= StarterClient.memoryStack.getSize()) {
         MemoryStack stack = StarterClient.memoryStack.push();

         try {
            ByteBuffer fb = stack.malloc(length);
            fb.put(0, data, 0, length);
            this.updateAttribute(attribute, fb);
         } catch (Throwable var8) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (stack != null) {
            stack.close();
         }
      } else {
         ByteBuffer fb = MemoryUtil.memAlloc(length);
         fb.put(0, data, 0, length);
         this.updateAttribute(attribute, fb);
         MemoryUtil.memFree(fb);
      }
   }

   public void updateAttribute(Data attribute, short[] data) {
      this.updateAttribute(attribute, data, data.length);
   }

   public void updateAttribute(Data attribute, short[] data, int length) {
      if (length * 2 <= StarterClient.memoryStack.getSize()) {
         MemoryStack stack = StarterClient.memoryStack.push();

         try {
            ShortBuffer fb = stack.mallocShort(length);
            fb.put(0, data, 0, length);
            this.updateAttribute(attribute, fb);
         } catch (Throwable var8) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (stack != null) {
            stack.close();
         }
      } else {
         ShortBuffer fb = MemoryUtil.memAllocShort(length);
         fb.put(0, data, 0, length);
         this.updateAttribute(attribute, fb);
         MemoryUtil.memFree(fb);
      }
   }

   public void updateAttribute(Data attribute, int[] data) {
      if (data.length * 4 <= StarterClient.memoryStack.getSize()) {
         MemoryStack stack = StarterClient.memoryStack.push();

         try {
            IntBuffer fb = stack.mallocInt(data.length);
            fb.put(0, data, 0, data.length);
            this.updateAttribute(attribute, fb);
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
      } else {
         IntBuffer fb = MemoryUtil.memAllocInt(data.length);
         fb.put(0, data, 0, data.length);
         this.updateAttribute(attribute, fb);
         MemoryUtil.memFree(fb);
      }
   }

   public void updateAttribute(Data attribute, Buffer data) {
      if (data instanceof FloatBuffer) {
         this.updateAttribute(attribute, (FloatBuffer)data);
      } else if (data instanceof IntBuffer) {
         this.updateAttribute(attribute, (IntBuffer)data);
      } else if (data instanceof ByteBuffer) {
         this.updateAttribute(attribute, (ByteBuffer)data);
      } else if (data instanceof ShortBuffer) {
         this.updateAttribute(attribute, (ShortBuffer)data);
      }
   }

   public void updateAttribute(Data attr, FloatBuffer data) {
      this.dataBufferGet(attr).bufferDataFast(data);
   }

   public void updateAttribute(Data attr, IntBuffer data) {
      this.dataBufferGet(attr).bufferDataFast(data);
   }

   public void updateAttribute(Data attr, ByteBuffer data) {
      this.dataBufferGet(attr).bufferDataFast(data);
   }

   public void updateAttribute(Data attr, ShortBuffer data) {
      this.dataBufferGet(attr).bufferDataFast(data);
   }

   public void updateAttribute(Data attr, FloatBuffer data, long offset) {
      this.dataBufferGet(attr).bufferSubData(data, offset);
   }

   public void updateAttribute(Data attr, IntBuffer data, long offset) {
      this.dataBufferGet(attr).bufferSubData(data, offset);
   }

   public void updateAttribute(Data attr, ByteBuffer data, long offset) {
      this.dataBufferGet(attr).bufferSubData(data, offset);
   }

   public void updateAttribute(Data attr, ShortBuffer data, long offset) {
      this.dataBufferGet(attr).bufferSubData(data, offset);
   }

   public void updateAttributeSubData(Data attr, FloatBuffer data) {
      this.dataBufferGet(attr).bufferSubData(data, 0L);
   }

   public void updateAttributeSubData(Data attr, IntBuffer data) {
      this.dataBufferGet(attr).bufferSubData(data, 0L);
   }

   public void updateAttributeSubData(Data attr, ByteBuffer data) {
      this.dataBufferGet(attr).bufferSubData(data, 0L);
   }

   public void updateAttributeSubData(Data attr, ShortBuffer data) {
      this.dataBufferGet(attr).bufferSubData(data, 0L);
   }

   public void updateAttributeSubData(Data attr, FloatBuffer data, long offset) {
      this.dataBufferGet(attr).bufferSubData(data, offset);
   }

   public void updateAttributeSubData(Data attr, IntBuffer data, long offset) {
      this.dataBufferGet(attr).bufferSubData(data, offset);
   }

   public void updateAttributeSubData(Data attr, ByteBuffer data, long offset) {
      this.dataBufferGet(attr).bufferSubData(data, offset);
   }

   public void updateAttributeSubData(Data attr, ShortBuffer data, long offset) {
      this.dataBufferGet(attr).bufferSubData(data, offset);
   }

   public BufferObject dataBufferGet(Data data) {
      for (int i = 0; i < this.dataBuffer.size(); i++) {
         VAO.BufferObjectData bod = this.dataBuffer.get(i);
         if (bod.data == data) {
            return bod.buffer;
         }
      }

      return null;
   }

   public void dataBufferPut(Data data, BufferObject obj) {
      this.dataLayout.add(data);
      this.dataBuffer.add(new VAO.BufferObjectData(obj, data));
   }

   public void attachAttribute(Data attribute, Buffer data) {
      if (data instanceof FloatBuffer) {
         this.attachAttribute(attribute, (FloatBuffer)data);
      } else if (data instanceof IntBuffer) {
         this.attachAttribute(attribute, (IntBuffer)data);
      } else if (data instanceof ByteBuffer) {
         this.attachAttribute(attribute, (ByteBuffer)data);
      } else if (data instanceof ShortBuffer) {
         this.attachAttribute(attribute, (ShortBuffer)data);
      }
   }

   public void attachAttribute(Data attr, ByteBuffer data) {
      BufferObject vbo = new BufferObject(Type.DATA, this.usage);
      vbo.bufferData(data);
      this.dataBufferPut(attr, vbo);
   }

   public void attachAttribute(Data attr, ShortBuffer data) {
      BufferObject vbo = new BufferObject(Type.DATA, this.usage);
      vbo.bufferData(data);
      this.dataBufferPut(attr, vbo);
   }

   public void attachAttribute(Data attr, FloatBuffer data) {
      BufferObject vbo = new BufferObject(Type.DATA, this.usage);
      vbo.bufferData(data);
      this.dataBufferPut(attr, vbo);
   }

   public void attachAttribute(Data attr, IntBuffer data) {
      BufferObject vbo = new BufferObject(Type.DATA, this.usage);
      vbo.bufferData(data);
      this.dataBufferPut(attr, vbo);
   }

   public void attachInstancedAttribute(Data attr, Usage usage, long size) {
      BufferObject vbo = new BufferObject(Type.DATA, usage);
      vbo.bufferData(size);
      this.dataBufferPut(attr, vbo);
   }

   public void updateInstancedAttribute(Data attr, FloatBuffer data) {
      this.dataBufferGet(attr).bufferDataFast(data);
   }

   public void attachAttribute(Data attr, float[] data, int length) {
      if (length * 4 <= StarterClient.memoryStack.getSize()) {
         MemoryStack stack = StarterClient.memoryStack.push();

         try {
            FloatBuffer fb = stack.mallocFloat(length);
            fb.put(0, data, 0, length);
            this.attachAttribute(attr, fb);
         } catch (Throwable var8) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (stack != null) {
            stack.close();
         }
      } else {
         FloatBuffer fb = MemoryUtil.memAllocFloat(length);
         fb.put(0, data, 0, length);
         this.attachAttribute(attr, fb);
         MemoryUtil.memFree(fb);
      }
   }

   public void attachAttribute(Data attr, float[] data) {
      this.attachAttribute(attr, data, data.length);
   }

   public void attachAttribute(Data attr, short[] data, int length) {
      if (length * 2 <= StarterClient.memoryStack.getSize()) {
         MemoryStack stack = StarterClient.memoryStack.push();

         try {
            ShortBuffer fb = stack.mallocShort(length);
            fb.put(0, data, 0, length);
            this.attachAttribute(attr, fb);
         } catch (Throwable var8) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (stack != null) {
            stack.close();
         }
      } else {
         ShortBuffer fb = MemoryUtil.memAllocShort(length);
         fb.put(0, data, 0, length);
         this.attachAttribute(attr, fb);
         MemoryUtil.memFree(fb);
      }
   }

   public void attachAttribute(Data attr, short[] data) {
      this.attachAttribute(attr, data, data.length);
   }

   public void attachAttribute(Data attr, byte[] data, int length) {
      if (length * 1 <= StarterClient.memoryStack.getSize()) {
         MemoryStack stack = StarterClient.memoryStack.push();

         try {
            ByteBuffer fb = stack.malloc(length);
            fb.put(0, data, 0, length);
            this.attachAttribute(attr, fb);
         } catch (Throwable var8) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (stack != null) {
            stack.close();
         }
      } else {
         ByteBuffer fb = MemoryUtil.memAlloc(length);
         fb.put(0, data, 0, length);
         this.attachAttribute(attr, fb);
         MemoryUtil.memFree(fb);
      }
   }

   public void attachAttribute(Data attr, byte[] data) {
      this.attachAttribute(attr, data, data.length);
   }

   public void attachAttribute(Data attr, int[] data, int length) {
      if (length * 4 <= StarterClient.memoryStack.getSize()) {
         MemoryStack stack = StarterClient.memoryStack.push();

         try {
            IntBuffer fb = stack.mallocInt(length);
            fb.put(0, data, 0, length);
            this.attachAttribute(attr, fb);
         } catch (Throwable var8) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (stack != null) {
            stack.close();
         }
      } else {
         IntBuffer fb = MemoryUtil.memAllocInt(length);
         fb.put(0, data, 0, length);
         this.attachAttribute(attr, fb);
         MemoryUtil.memFree(fb);
      }
   }

   public void attachAttribute(Data attr, int[] data) {
      this.attachAttribute(attr, data, data.length);
   }

   public void attachIndices(Buffer indices) {
      if (indices instanceof IntBuffer) {
         this.attachIndices((IntBuffer)indices);
      } else if (indices instanceof ByteBuffer) {
         this.attachIndices((ByteBuffer)indices);
      } else if (indices instanceof ShortBuffer) {
         this.attachIndices((ShortBuffer)indices);
      }
   }

   public void attachIndices(IntBuffer indices) {
      this.numberIndices = indices.capacity();
      this.indexBuffer = new BufferObject(Type.INDEX, this.usage);
      this.indexBuffer.bufferData(indices);
      this.indexBufferType = 5125;
   }

   public void attachIndices(ShortBuffer indices) {
      this.numberIndices = indices.capacity();
      this.indexBuffer = new BufferObject(Type.INDEX, this.usage);
      this.indexBuffer.bufferData(indices);
      this.indexBufferType = 5123;
   }

   public void attachIndices(int[] data, int length) {
      if (length * 4 <= StarterClient.memoryStack.getSize()) {
         MemoryStack stack = StarterClient.memoryStack.push();

         try {
            IntBuffer fb = stack.mallocInt(length);
            fb.put(0, data, 0, length);
            this.attachIndices(fb);
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
      } else {
         IntBuffer fb = MemoryUtil.memAllocInt(length);
         fb.put(0, data, 0, length);
         this.attachIndices(fb);
         MemoryUtil.memFree(fb);
      }
   }

   public void attachIndices(short[] data, int length) {
      if (length * 2 <= StarterClient.memoryStack.getSize()) {
         MemoryStack stack = StarterClient.memoryStack.push();

         try {
            ShortBuffer fb = stack.mallocShort(length);
            fb.put(0, data, 0, length);
            this.attachIndices(fb);
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
      } else {
         ShortBuffer fb = MemoryUtil.memAllocShort(length);
         fb.put(0, data, 0, length);
         this.attachIndices(fb);
         MemoryUtil.memFree(fb);
      }
   }

   public void attachIndices(int[] indices) {
      this.attachIndices(indices, indices.length);
   }

   public void updateIndices(Buffer indices) {
      if (indices instanceof IntBuffer) {
         this.updateIndices((IntBuffer)indices);
      } else if (indices instanceof ByteBuffer) {
         this.updateIndices((ByteBuffer)indices);
      } else if (indices instanceof ByteBuffer) {
         this.updateIndices((ShortBuffer)indices);
      }
   }

   public void updateIndices(int[] indices, long offset) {
      MemoryStack stack = StarterClient.memoryStack.push();

      try {
         IntBuffer fb = stack.mallocInt(indices.length);
         fb.put(0, indices, 0, indices.length);
         this.indexBuffer.bufferSubData(fb, offset);
      } catch (Throwable var8) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void updateIndices(int[] indices) {
      this.updateIndices(indices, indices.length);
   }

   public void updateIndices(IntBuffer indices) {
      this.numberIndices = indices.capacity();
      this.indexBuffer.bufferDataFast(indices);
   }

   public void updateIndices(ShortBuffer indices) {
      this.numberIndices = indices.capacity();
      this.indexBuffer.bufferDataFast(indices);
   }

   public void updateIndicesSubData(IntBuffer indices) {
      this.numberIndices = indices.capacity();
      this.indexBuffer.bufferSubData(indices, 0L);
   }

   public void updateIndicesSubData(ShortBuffer indices) {
      this.numberIndices = indices.capacity();
      this.indexBuffer.bufferSubData(indices, 0L);
   }

   public BufferObject getBufferObject(Data data) {
      return data == Data.INDEX ? this.indexBuffer : this.dataBufferGet(data);
   }

   public void increaseReferenceCounter() {
      this.referenceCounter++;
   }

   public static void storePreviouslyBoundState() {
      previouslyBound = GL32C.glGetInteger(34229);
      previouslyBoundArray = GL32C.glGetInteger(34964);
      previouslyBoundElement = GL32C.glGetInteger(34965);
      bound = previouslyBound;
   }

   public static void restorePreviouslyBoundState() {
      GL32C.glBindVertexArray(previouslyBound);
      GL32C.glBindBuffer(34962, previouslyBoundArray);
      GL32C.glBindBuffer(34963, previouslyBoundElement);
      bound = previouslyBound;
   }

   public int getReferenceCounter() {
      return this.referenceCounter;
   }

   public void destroy() {
      this.referenceCounter--;
      if (this.referenceCounter == 0) {
         if (!this.destroyed) {
            if (!VERTEX_ATTRIB_BINDING_SUPPORT) {
               if (bound == this.id) {
                  bound = 0;
               }

               if (previouslyBound == this.id) {
                  previouslyBound = 0;
               }

               GL32C.glDeleteVertexArrays(this.id);
            } else {
               MemoryUtil.memFree(this.vbuffers);
               MemoryUtil.memFree(this.vstrides);
               MemoryUtil.memFree(this.voffsets);
            }

            for (int i = 0; i < this.dataBuffer.size(); i++) {
               VAO.BufferObjectData bod = this.dataBuffer.get(i);
               if (bod.buffer != null) {
                  if (this.header != null && this.header.bindings[0] == bod.buffer.id) {
                     this.header.bindings[0] = 0;
                  }

                  if (previouslyBoundArray == bod.buffer.id) {
                     previouslyBoundArray = 0;
                  }

                  bod.buffer.destroy();
               }
            }

            if (this.indexBuffer != null) {
               if (this.header != null && this.header.boundElementBuffer == this.indexBuffer.id) {
                  this.header.boundElementBuffer = 0;
               }

               if (previouslyBoundElement == this.indexBuffer.id) {
                  previouslyBoundElement = 0;
               }

               this.indexBuffer.destroy();
            }

            this.destroyed = true;
         }
      }
   }

   public boolean isDestroyed() {
      return this.destroyed;
   }

   public static void destroyHeaders() {
      ObjectIterator var0 = vaoHeaders.values().iterator();

      while (var0.hasNext()) {
         VAOHeader header = (VAOHeader)var0.next();
         GL32C.glDeleteVertexArrays(header.vaoID);
      }
   }

   public class ARBMultiBind implements VAO.MultiBind {
      @Override
      public void glBindVertexBuffers(int first, IntBuffer buffers, PointerBuffer offsets, IntBuffer strides) {
         org.lwjgl.opengl.ARBMultiBind.glBindVertexBuffers(first, buffers, offsets, strides);
      }
   }

   public class ARBVertexAttribBinding implements VAO.VertexAttribBinding {
      @Override
      public void glVertexAttribIFormat(int attribindex, int size, int type, int relativeoffset) {
         org.lwjgl.opengl.ARBVertexAttribBinding.glVertexAttribIFormat(attribindex, size, type, relativeoffset);
      }

      @Override
      public void glVertexAttribFormat(int attribindex, int size, int type, boolean normalize, int relativeoffset) {
         org.lwjgl.opengl.ARBVertexAttribBinding.glVertexAttribFormat(attribindex, size, type, normalize, relativeoffset);
      }

      @Override
      public void glVertexAttribBinding(int attribindex, int bindingindex) {
         org.lwjgl.opengl.ARBVertexAttribBinding.glVertexAttribBinding(attribindex, bindingindex);
      }

      @Override
      public void glBindVertexBuffer(int bindingindex, int buffer, long offset, int stride) {
         org.lwjgl.opengl.ARBVertexAttribBinding.glBindVertexBuffer(bindingindex, buffer, offset, stride);
      }

      @Override
      public void glVertexBindingDivisor(int bindingindex, int divisor) {
         org.lwjgl.opengl.ARBVertexAttribBinding.glVertexBindingDivisor(bindingindex, divisor);
      }
   }

   public class Attribute {
      int id;
      int size;

      public Attribute(int id, int size) {
         this.id = id;
         this.size = size;
      }
   }

   public class BufferObjectData {
      public BufferObject buffer;
      public Data data;

      public BufferObjectData(BufferObject buffer, Data data) {
         this.buffer = buffer;
         this.data = data;
      }
   }

   public class GL43VertexAttribBinding implements VAO.VertexAttribBinding {
      @Override
      public void glVertexAttribIFormat(int attribindex, int size, int type, int relativeoffset) {
         GL43C.glVertexAttribIFormat(attribindex, size, type, relativeoffset);
      }

      @Override
      public void glVertexAttribFormat(int attribindex, int size, int type, boolean normalize, int relativeoffset) {
         GL43C.glVertexAttribFormat(attribindex, size, type, normalize, relativeoffset);
      }

      @Override
      public void glVertexAttribBinding(int attribindex, int bindingindex) {
         GL43C.glVertexAttribBinding(attribindex, bindingindex);
      }

      @Override
      public void glBindVertexBuffer(int bindingindex, int buffer, long offset, int stride) {
         GL43C.glBindVertexBuffer(bindingindex, buffer, offset, stride);
      }

      @Override
      public void glVertexBindingDivisor(int bindingindex, int divisor) {
         GL43C.glVertexBindingDivisor(bindingindex, divisor);
      }
   }

   public class GL44MultiBind implements VAO.MultiBind {
      @Override
      public void glBindVertexBuffers(int first, IntBuffer buffers, PointerBuffer offsets, IntBuffer strides) {
         GL44C.glBindVertexBuffers(first, buffers, offsets, strides);
      }
   }

   public interface MultiBind {
      void glBindVertexBuffers(int var1, IntBuffer var2, PointerBuffer var3, IntBuffer var4);
   }

   public interface VertexAttribBinding {
      void glVertexAttribIFormat(int var1, int var2, int var3, int var4);

      void glVertexAttribFormat(int var1, int var2, int var3, boolean var4, int var5);

      void glVertexAttribBinding(int var1, int var2);

      void glBindVertexBuffer(int var1, int var2, long var3, int var5);

      void glVertexBindingDivisor(int var1, int var2);
   }
}
