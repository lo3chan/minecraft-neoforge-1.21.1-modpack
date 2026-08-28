/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.common;

import java.io.Serializable;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.common.Vec2;

public class Sweep
implements Serializable {
    private static final long serialVersionUID = 1L;
    public final Vec2 localCenter = new Vec2();
    public final Vec2 c0 = new Vec2();
    public final Vec2 c = new Vec2();
    public float a0;
    public float a;
    public float alpha0;

    public String toString() {
        String s = "Sweep:\nlocalCenter: " + String.valueOf(this.localCenter) + "\n";
        s = s + "c0: " + String.valueOf(this.c0) + ", c: " + String.valueOf(this.c) + "\n";
        s = s + "a0: " + this.a0 + ", a: " + this.a + "\n";
        s = s + "alpha0: " + this.alpha0;
        return s;
    }

    public final void normalize() {
        float d = (float)Math.PI * 2 * (float)MathUtils.floor(this.a0 / ((float)Math.PI * 2));
        this.a0 -= d;
        this.a -= d;
    }

    public final Sweep set(Sweep other) {
        this.localCenter.set(other.localCenter);
        this.c0.set(other.c0);
        this.c.set(other.c);
        this.a0 = other.a0;
        this.a = other.a;
        this.alpha0 = other.alpha0;
        return this;
    }

    public final void getTransform(Transform xf, float beta) {
        assert (xf != null);
        xf.p.x = (1.0f - beta) * this.c0.x + beta * this.c.x;
        xf.p.y = (1.0f - beta) * this.c0.y + beta * this.c.y;
        float angle = (1.0f - beta) * this.a0 + beta * this.a;
        xf.q.set(angle);
        Rot q = xf.q;
        xf.p.x -= q.c * this.localCenter.x - q.s * this.localCenter.y;
        xf.p.y -= q.s * this.localCenter.x + q.c * this.localCenter.y;
    }

    public final void advance(float alpha) {
        assert (this.alpha0 < 1.0f);
        float beta = (alpha - this.alpha0) / (1.0f - this.alpha0);
        this.c0.x += beta * (this.c.x - this.c0.x);
        this.c0.y += beta * (this.c.y - this.c0.y);
        this.a0 += beta * (this.a - this.a0);
        this.alpha0 = alpha;
    }
}

