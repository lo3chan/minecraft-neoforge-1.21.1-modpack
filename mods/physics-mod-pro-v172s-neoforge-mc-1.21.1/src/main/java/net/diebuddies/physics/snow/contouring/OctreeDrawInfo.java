/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 */
package net.diebuddies.physics.snow.contouring;

import org.joml.Vector3d;
import org.joml.Vector3f;

public class OctreeDrawInfo {
    public Vector3d pos;
    public Vector3f normal;
    public int light;
    public int corners;
    public int index;

    public String toString() {
        return "OctreeDrawInfo [pos=" + String.valueOf(this.pos) + ", normal=" + String.valueOf(this.normal) + ", corners=" + this.corners + ", index=" + this.index + "]";
    }
}

