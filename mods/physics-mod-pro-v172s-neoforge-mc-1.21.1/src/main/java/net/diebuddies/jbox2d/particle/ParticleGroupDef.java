/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.particle;

import net.diebuddies.jbox2d.collision.shapes.Shape;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.particle.ParticleColor;

public class ParticleGroupDef {
    public int flags = 0;
    public int groupFlags = 0;
    public final Vec2 position = new Vec2();
    public float angle = 0.0f;
    public final Vec2 linearVelocity = new Vec2();
    public float angularVelocity = 0.0f;
    public ParticleColor color;
    public float strength = 1.0f;
    public Shape shape;
    public boolean destroyAutomatically = true;
    public Object userData;
}

