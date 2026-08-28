/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.common;

import net.diebuddies.jbox2d.common.Vec2;

public class RaycastResult {
    public float lambda = 0.0f;
    public final Vec2 normal = new Vec2();

    public RaycastResult set(RaycastResult argOther) {
        this.lambda = argOther.lambda;
        this.normal.set(argOther.normal);
        return this;
    }
}

