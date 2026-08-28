/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics.ocean;

import net.diebuddies.physics.ocean.RippleParticle;

public class RainParticle
extends RippleParticle {
    public RainParticle(int lifetime, float scale, double x, double y, double z) {
        this.state = 0.1f;
        this.lifetime = lifetime;
        this.startLifetime = lifetime;
        this.x = x + this.vx;
        this.y = y + this.vy;
        this.z = z + this.vz;
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.baseAlpha = 1.0f;
        this.alpha = 0.0f;
        this.scale = scale;
    }

    @Override
    public void update() {
        super.update();
        this.state = 1.0f - (float)this.lifetime / (float)this.startLifetime + 0.1f;
    }
}

