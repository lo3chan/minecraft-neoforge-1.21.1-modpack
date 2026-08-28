/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector2f
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector4f
 */
package net.diebuddies.physics.verlet;

import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector4f;

public class VerletPoint {
    public final Vector3d position;
    public final Vector3d prevPosition;
    public final Vector3d bufferPosition;
    public final Vector3d bufferPrevPosition;
    public final Vector3d renderPosition;
    public final Vector3d force;
    public final Vector3d normal;
    public final Vector3d bufferNormal;
    public final Vector2f uv;
    public final Vector4f rgba;
    public boolean locked;
    public double friction = 1.0;
    public Vector3d softRestriction;
    public double restriction;

    public VerletPoint(Vector3d position, boolean locked) {
        this.position = position;
        this.prevPosition = new Vector3d((Vector3dc)position);
        this.bufferPosition = new Vector3d((Vector3dc)position);
        this.bufferPrevPosition = new Vector3d((Vector3dc)position);
        this.force = new Vector3d();
        this.renderPosition = new Vector3d();
        this.locked = locked;
        this.normal = new Vector3d(0.0, 1.0, 0.0);
        this.bufferNormal = new Vector3d((Vector3dc)this.normal);
        this.rgba = new Vector4f(1.0f);
        this.uv = new Vector2f(0.0f);
    }

    public VerletPoint(Vector3d position) {
        this(position, false);
    }

    public void setRestriction(double restriction) {
        this.softRestriction = new Vector3d((Vector3dc)this.position);
        this.restriction = restriction;
    }

    public void updateRenderPosition(double renderPercent) {
        this.bufferPrevPosition.lerp((Vector3dc)this.bufferPosition, renderPercent, this.renderPosition);
    }
}

