/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL32C
 */
package net.diebuddies.opengl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import net.diebuddies.opengl.Type;
import net.diebuddies.opengl.Usage;
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
        GL32C.glBufferData((int)this.type, (long)size, (int)this.usage);
        this.size = size;
    }

    public void bufferData(ByteBuffer buffer) {
        this.bind();
        if ((long)buffer.capacity() <= this.size) {
            GL32C.glBufferSubData((int)this.type, (long)0L, (ByteBuffer)buffer);
        } else {
            GL32C.glBufferData((int)this.type, (ByteBuffer)buffer, (int)this.usage);
            this.size = buffer.capacity();
        }
    }

    public void bufferData(FloatBuffer buffer) {
        this.bind();
        if ((long)(buffer.capacity() * 4) <= this.size) {
            GL32C.glBufferSubData((int)this.type, (long)0L, (FloatBuffer)buffer);
        } else {
            GL32C.glBufferData((int)this.type, (FloatBuffer)buffer, (int)this.usage);
            this.size = buffer.capacity() * 4;
        }
    }

    public void bufferData(ShortBuffer buffer) {
        this.bind();
        if ((long)(buffer.capacity() * 2) <= this.size) {
            GL32C.glBufferSubData((int)this.type, (long)0L, (ShortBuffer)buffer);
        } else {
            GL32C.glBufferData((int)this.type, (ShortBuffer)buffer, (int)this.usage);
            this.size = buffer.capacity() * 2;
        }
    }

    public void bufferData(IntBuffer buffer) {
        this.bind();
        if ((long)(buffer.capacity() * 4) <= this.size) {
            GL32C.glBufferSubData((int)this.type, (long)0L, (IntBuffer)buffer);
        } else {
            GL32C.glBufferData((int)this.type, (IntBuffer)buffer, (int)this.usage);
            this.size = buffer.capacity() * 4;
        }
    }

    public void createSize(long size) {
        this.bind();
        GL32C.glBufferData((int)this.type, (long)size, (int)this.usage);
        this.size = size;
    }

    public void bufferSubData(ByteBuffer buffer, long offset) {
        this.bind();
        GL32C.glBufferSubData((int)this.type, (long)offset, (ByteBuffer)buffer);
    }

    public void bufferSubData(FloatBuffer buffer, long offset) {
        this.bind();
        GL32C.glBufferSubData((int)this.type, (long)offset, (FloatBuffer)buffer);
    }

    public void bufferSubData(ShortBuffer buffer, long offset) {
        this.bind();
        GL32C.glBufferSubData((int)this.type, (long)offset, (ShortBuffer)buffer);
    }

    public void bufferSubData(IntBuffer buffer, long offset) {
        this.bind();
        GL32C.glBufferSubData((int)this.type, (long)offset, (IntBuffer)buffer);
    }

    public void bufferDataFast(FloatBuffer buffer) {
        this.bind();
        GL32C.glBufferData((int)this.type, (long)(buffer.capacity() * 4), (int)this.usage);
        GL32C.glBufferSubData((int)this.type, (long)0L, (FloatBuffer)buffer);
        this.size = buffer.capacity() * 4;
    }

    public void bufferDataFast(ShortBuffer buffer) {
        this.bind();
        GL32C.glBufferData((int)this.type, (long)(buffer.capacity() * 2), (int)this.usage);
        GL32C.glBufferSubData((int)this.type, (long)0L, (ShortBuffer)buffer);
        this.size = buffer.capacity() * 2;
    }

    public void bufferDataFast(ByteBuffer buffer) {
        this.bind();
        GL32C.glBufferData((int)this.type, (long)buffer.capacity(), (int)this.usage);
        GL32C.glBufferSubData((int)this.type, (long)0L, (ByteBuffer)buffer);
        this.size = buffer.capacity();
    }

    public void bufferDataFast(IntBuffer buffer) {
        this.bind();
        GL32C.glBufferData((int)this.type, (long)(buffer.capacity() * 4), (int)this.usage);
        GL32C.glBufferSubData((int)this.type, (long)0L, (IntBuffer)buffer);
        this.size = buffer.capacity() * 4;
    }

    public ByteBuffer mapBuffer() {
        this.bind();
        return GL32C.glMapBuffer((int)this.type, (int)35001);
    }

    public void unmapBuffer() {
        this.bind();
        GL32C.glUnmapBuffer((int)this.type);
    }

    public void bind(int type) {
        GL32C.glBindBuffer((int)type, (int)this.id);
    }

    public void bind() {
        this.bind(this.type);
    }

    public void unbind() {
        BufferObject.unbind(this.type);
    }

    public static void unbind(int type) {
        GL32C.glBindBuffer((int)type, (int)0);
    }

    public void destroy() {
        GL32C.glDeleteBuffers((int)this.id);
    }

    public long getSize() {
        return this.size;
    }

    public int getID() {
        return this.id;
    }
}

