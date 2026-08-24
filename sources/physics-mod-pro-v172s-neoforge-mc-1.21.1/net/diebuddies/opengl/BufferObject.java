package net.diebuddies.opengl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.opengl.GL32C;

public class BufferObject {
   protected int id = this.createBuffer();
   protected int type;
   protected int usage;
   protected long size;

   public BufferObject(Type type, Usage usage) {
      this.type = type.getType();
      this.usage = usage.getUsage();
      this.size = -1L;
   }

   public BufferObject(Type type) {
      this.type = type.getType();
      this.usage = Usage.STATIC.getUsage();
      this.size = -1L;
   }

   private int createBuffer() {
      return GL32C.glGenBuffers();
   }

   public void bufferData(long size) {
      this.bind();
      GL32C.glBufferData(this.type, size, this.usage);
      this.size = size;
   }

   public void bufferData(ByteBuffer buffer) {
      this.bind();
      if (buffer.capacity() <= this.size) {
         GL32C.glBufferSubData(this.type, 0L, buffer);
      } else {
         GL32C.glBufferData(this.type, buffer, this.usage);
         this.size = buffer.capacity();
      }
   }

   public void bufferData(FloatBuffer buffer) {
      this.bind();
      if (buffer.capacity() * 4 <= this.size) {
         GL32C.glBufferSubData(this.type, 0L, buffer);
      } else {
         GL32C.glBufferData(this.type, buffer, this.usage);
         this.size = buffer.capacity() * 4;
      }
   }

   public void bufferData(ShortBuffer buffer) {
      this.bind();
      if (buffer.capacity() * 2 <= this.size) {
         GL32C.glBufferSubData(this.type, 0L, buffer);
      } else {
         GL32C.glBufferData(this.type, buffer, this.usage);
         this.size = buffer.capacity() * 2;
      }
   }

   public void bufferData(IntBuffer buffer) {
      this.bind();
      if (buffer.capacity() * 4 <= this.size) {
         GL32C.glBufferSubData(this.type, 0L, buffer);
      } else {
         GL32C.glBufferData(this.type, buffer, this.usage);
         this.size = buffer.capacity() * 4;
      }
   }

   public void createSize(long size) {
      this.bind();
      GL32C.glBufferData(this.type, size, this.usage);
      this.size = size;
   }

   public void bufferSubData(ByteBuffer buffer, long offset) {
      this.bind();
      GL32C.glBufferSubData(this.type, offset, buffer);
   }

   public void bufferSubData(FloatBuffer buffer, long offset) {
      this.bind();
      GL32C.glBufferSubData(this.type, offset, buffer);
   }

   public void bufferSubData(ShortBuffer buffer, long offset) {
      this.bind();
      GL32C.glBufferSubData(this.type, offset, buffer);
   }

   public void bufferSubData(IntBuffer buffer, long offset) {
      this.bind();
      GL32C.glBufferSubData(this.type, offset, buffer);
   }

   public void bufferDataFast(FloatBuffer buffer) {
      this.bind();
      GL32C.glBufferData(this.type, buffer.capacity() * 4, this.usage);
      GL32C.glBufferSubData(this.type, 0L, buffer);
      this.size = buffer.capacity() * 4;
   }

   public void bufferDataFast(ShortBuffer buffer) {
      this.bind();
      GL32C.glBufferData(this.type, buffer.capacity() * 2, this.usage);
      GL32C.glBufferSubData(this.type, 0L, buffer);
      this.size = buffer.capacity() * 2;
   }

   public void bufferDataFast(ByteBuffer buffer) {
      this.bind();
      GL32C.glBufferData(this.type, buffer.capacity(), this.usage);
      GL32C.glBufferSubData(this.type, 0L, buffer);
      this.size = buffer.capacity();
   }

   public void bufferDataFast(IntBuffer buffer) {
      this.bind();
      GL32C.glBufferData(this.type, buffer.capacity() * 4, this.usage);
      GL32C.glBufferSubData(this.type, 0L, buffer);
      this.size = buffer.capacity() * 4;
   }

   public ByteBuffer mapBuffer() {
      this.bind();
      return GL32C.glMapBuffer(this.type, 35001);
   }

   public void unmapBuffer() {
      this.bind();
      GL32C.glUnmapBuffer(this.type);
   }

   public void bind(int type) {
      GL32C.glBindBuffer(type, this.id);
   }

   public void bind() {
      this.bind(this.type);
   }

   public void unbind() {
      unbind(this.type);
   }

   public static void unbind(int type) {
      GL32C.glBindBuffer(type, 0);
   }

   public void destroy() {
      GL32C.glDeleteBuffers(this.id);
   }

   public long getSize() {
      return this.size;
   }

   public int getID() {
      return this.id;
   }
}
