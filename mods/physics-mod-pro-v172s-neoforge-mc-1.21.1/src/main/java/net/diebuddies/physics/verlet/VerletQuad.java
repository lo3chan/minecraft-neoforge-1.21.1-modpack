/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector2f
 *  org.joml.Vector3d
 *  org.joml.Vector4f
 */
package net.diebuddies.physics.verlet;

import net.diebuddies.physics.verlet.VerletPoint;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector4f;

public class VerletQuad {
    public VerletReference point1;
    public VerletReference point2;
    public VerletReference point3;
    public VerletReference point4;
    public Vector3d normal;
    public Vector3d bufferNormal;

    public VerletQuad(VerletPoint point1, VerletPoint point2, VerletPoint point3, VerletPoint point4) {
        this.point1 = new VerletReference(point1, point1.uv);
        this.point2 = new VerletReference(point2, point2.uv);
        this.point3 = new VerletReference(point3, point3.uv);
        this.point4 = new VerletReference(point4, point4.uv);
        this.normal = new Vector3d();
        this.bufferNormal = new Vector3d();
    }

    public VerletQuad(VerletPoint point1, VerletPoint point2, VerletPoint point3, VerletPoint point4, Vector2f uv1, Vector2f uv2, Vector2f uv3, Vector2f uv4, boolean flipUV) {
        this.point1 = new VerletReference(point1, uv1);
        this.point2 = new VerletReference(point2, uv2);
        this.point3 = new VerletReference(point3, uv3);
        this.point4 = new VerletReference(point4, uv4);
        this.normal = new Vector3d();
        this.bufferNormal = new Vector3d();
        if (flipUV) {
            uv1.y = 1.0f - uv1.y;
            uv2.y = 1.0f - uv2.y;
            uv3.y = 1.0f - uv3.y;
            uv4.y = 1.0f - uv4.y;
        }
    }

    public static class VerletReference {
        public Vector3d position;
        public Vector3d bufferPosition;
        public Vector3d bufferPrevPosition;
        public Vector3d renderPosition;
        public Vector3d normal;
        public Vector3d bufferNormal;
        public Vector2f uv;
        public Vector4f rgba;

        public VerletReference(VerletPoint position, Vector2f uv) {
            this.position = position.position;
            this.renderPosition = position.renderPosition;
            this.bufferPosition = position.bufferPosition;
            this.bufferPrevPosition = position.bufferPrevPosition;
            this.normal = position.normal;
            this.bufferNormal = position.bufferNormal;
            this.uv = uv;
            this.rgba = position.rgba;
        }
    }
}

