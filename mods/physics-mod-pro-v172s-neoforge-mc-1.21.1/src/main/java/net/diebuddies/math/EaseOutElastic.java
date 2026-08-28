/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.math;

import net.diebuddies.math.Curve;

public class EaseOutElastic
implements Curve {
    @Override
    public float get(float time) {
        float c4 = 2.0943952f;
        return (float)Math.pow(2.0, -10.0f * time) * (float)Math.sin((time * 10.0f - 0.75f) * c4) + 1.0f;
    }
}

