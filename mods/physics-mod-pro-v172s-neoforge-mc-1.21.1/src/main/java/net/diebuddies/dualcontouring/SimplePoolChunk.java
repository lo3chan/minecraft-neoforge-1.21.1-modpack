/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.dualcontouring;

import net.diebuddies.dualcontouring.Chunk;

public class SimplePoolChunk {
    private Chunk[] objects;
    private int index;

    public SimplePoolChunk(int size) {
        this.objects = new Chunk[size];
        for (int i = 0; i < size; ++i) {
            this.objects[i] = new Chunk(40, 40, 40, 0, 0, 0);
        }
    }

    public Chunk get(int width, int height, int depth, byte density, int ambient, int color) {
        if (this.index < this.objects.length) {
            return this.objects[this.index++].reset(width, height, depth, density, ambient, color);
        }
        this.resize();
        return this.objects[this.index++].reset(width, height, depth, density, ambient, color);
    }

    private void resize() {
        Chunk[] newArray = new Chunk[this.objects.length * 2];
        System.arraycopy(this.objects, 0, newArray, 0, this.objects.length);
        for (int i = this.objects.length; i < newArray.length; ++i) {
            newArray[i] = new Chunk(40, 40, 40, 0, 0, 0);
        }
        this.objects = newArray;
    }

    public void reset() {
        this.index = 0;
    }
}

