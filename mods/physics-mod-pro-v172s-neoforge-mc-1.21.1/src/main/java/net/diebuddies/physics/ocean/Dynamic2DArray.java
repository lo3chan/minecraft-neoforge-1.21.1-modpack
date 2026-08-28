/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics.ocean;

public class Dynamic2DArray {
    private static final int GROW_FACTOR = 2;
    private byte[] arr;
    private int width;
    private int height;

    public Dynamic2DArray(int initialWidth, int initialHeight) {
        this.arr = new byte[initialWidth * initialHeight];
        this.width = initialWidth;
        this.height = initialHeight;
    }

    public void request(int width, int height) {
        this.width = width;
        this.height = height;
        int size = width * height;
        if (this.arr.length < size) {
            this.arr = new byte[size * 2];
        }
    }

    public byte get(int x, int y) {
        return this.arr[y * this.width + x];
    }

    public void set(int x, int y, byte value) {
        this.arr[y * this.width + x] = value;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}

