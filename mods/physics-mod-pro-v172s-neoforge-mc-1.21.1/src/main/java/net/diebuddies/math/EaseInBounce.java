/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.math;

import net.diebuddies.math.Curve;

public class EaseInBounce
implements Curve {
    @Override
    public float get(float time) {
        return 1.0f - EaseInBounce.easeOutBounce(1.0f - time);
    }

    private static float easeOutBounce(float time) {
        float n1 = 7.5625f;
        float d1 = 2.75f;
        if (time < 1.0f / d1) {
            return n1 * time * time;
        }
        if (time < 2.0f / d1) {
            return n1 * (time -= 1.5f / d1) * time + 0.75f;
        }
        if ((double)time < 2.5 / (double)d1) {
            return n1 * (time -= 2.25f / d1) * time + 0.9375f;
        }
        return n1 * (time -= 2.625f / d1) * time + 0.984375f;
    }
}

