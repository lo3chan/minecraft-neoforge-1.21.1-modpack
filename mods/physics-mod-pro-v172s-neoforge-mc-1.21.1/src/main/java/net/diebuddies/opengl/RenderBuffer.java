/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL32C
 */
package net.diebuddies.opengl;

import net.diebuddies.opengl.DrawBuffer;
import org.lwjgl.opengl.GL32C;

public class RenderBuffer
extends DrawBuffer {
    private int width;
    private int height;

    public RenderBuffer(int id, int width, int height) {
        super(id, null);
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public void destroy() {
        GL32C.glDeleteRenderbuffers((int)this.id);
    }
}

