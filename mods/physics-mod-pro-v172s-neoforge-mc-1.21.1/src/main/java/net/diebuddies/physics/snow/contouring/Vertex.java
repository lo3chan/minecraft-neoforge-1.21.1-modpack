/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.diebuddies.physics.snow.contouring;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Vertex {
    public Vector3d position;
    public Vector3f normal;
    public int light;

    public Vertex(Vector3d position, Vector3f normal, int light) {
        this.position = position;
        this.normal = normal;
        this.light = light;
    }

    public Vertex() {
        this(new Vector3d(), new Vector3f(), 0);
    }

    public Vertex set(Vector3d position, Vector3f normal, int light) {
        this.position.set((Vector3dc)position);
        this.normal.set((Vector3fc)normal);
        this.light = light;
        return this;
    }
}

