/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 */
package net.diebuddies.dualcontouring;

import org.joml.Vector3f;

public class OctreeDrawInfo {
    public Vector3f pos = new Vector3f();
    public int ambient;
    public byte corners;
    public int index;

    public String toString() {
        return "OctreeDrawInfo [pos=" + String.valueOf(this.pos) + ", corners=" + this.corners + ", index=" + this.index + "]";
    }
}

