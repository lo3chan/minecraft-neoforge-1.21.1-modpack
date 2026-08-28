/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector2f
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.diebuddies.dualcontouring;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Vertex {
    public Vector3f position = new Vector3f();
    public Vector3f normal = new Vector3f();
    public Vector3f tangent = new Vector3f();
    public Vector2f uv = new Vector2f();
    public int ambient;

    public Vertex set(Vector3f position, int ambient) {
        this.position.set((Vector3fc)position);
        this.ambient = ambient;
        this.uv.set(0.0f);
        this.tangent.set(0.0f);
        this.normal.set(0.0f);
        return this;
    }

    public String toString() {
        return "Vertex [position=" + String.valueOf(this.position) + ", normal=" + String.valueOf(this.normal) + "]";
    }
}

