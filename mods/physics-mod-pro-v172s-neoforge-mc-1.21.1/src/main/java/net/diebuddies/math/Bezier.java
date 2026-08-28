/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.math;

import net.diebuddies.math.Curve;
import net.diebuddies.math.Math;

public class Bezier
implements Curve {
    public static final Bezier EASE_IN_EXPO = new Bezier(0.0f, 1.0f, 0.9f, 1.0f);
    public static final Bezier EASE_OUT_EXPO = new Bezier(0.0f, 1.0f, 0.06f, 0.1f);
    public static final Bezier BOUNCE = new Bezier(0.0f, 1.0f, 0.0f, 2.0f);
    public static final Bezier EASE_IN_OUT_EXPO = new Bezier(0.0f, 1.0f, 0.0f, 1.0f);
    public float p0;
    public float p1;
    public float c0;
    public float c1;

    public Bezier(float p0, float p1, float c0, float c1) {
        this.p0 = p0;
        this.p1 = p1;
        this.c0 = c0;
        this.c1 = c1;
    }

    @Override
    public float get(float time) {
        return this.p0 * (float)java.lang.Math.pow(1.0f - time, 3.0) + this.c0 * (float)java.lang.Math.pow(1.0f - time, 2.0) * 3.0f * time + this.c1 * (float)java.lang.Math.pow(time, 2.0) * 3.0f * (1.0f - time) + this.p1 * (float)java.lang.Math.pow(time, 3.0);
    }

    public static Bezier[] getCubicBSpline(float[] dataPoint) {
        int i;
        Bezier[] beziers = new Bezier[dataPoint.length - 1];
        float[] controlPoints = new float[(dataPoint.length - 1) * 2];
        float[] points = new float[dataPoint.length];
        for (i = 0; i < dataPoint.length; ++i) {
            int i1 = Math.clamp(i - 1, 0, dataPoint.length - 1);
            int i2 = Math.clamp(i, 0, dataPoint.length - 1);
            int i3 = Math.clamp(i + 1, 0, dataPoint.length - 1);
            float[] cp = Bezier.getControlPoints(dataPoint[i1], dataPoint[i2], dataPoint[i3], 1.5f);
            int cpi1 = Math.clamp(i * 2 - 1, 0, controlPoints.length - 1);
            int cpi2 = Math.clamp(i * 2, 0, controlPoints.length - 1);
            controlPoints[cpi1] = cp[0];
            controlPoints[cpi2] = cp[1];
        }
        points[0] = dataPoint[0];
        points[points.length - 1] = dataPoint[dataPoint.length - 1];
        for (i = 1; i < points.length - 1; ++i) {
            points[i] = (controlPoints[i * 2 - 1] + controlPoints[i * 2]) * 0.5f;
        }
        for (i = 0; i < beziers.length; ++i) {
            beziers[i] = new Bezier(dataPoint[i], dataPoint[i + 1], controlPoints[i * 2], controlPoints[i * 2 + 1]);
        }
        return beziers;
    }

    private static float[] getControlPoints(float x0, float x1, float x2, float t) {
        float d01 = x1 - x0;
        float d12 = x2 - x1;
        float fa = t * d01 / (d01 + d12);
        float fb = t * d12 / (d01 + d12);
        float p1x = x1 - fa * (x2 - x0);
        float p2x = x1 + fb * (x2 - x0);
        return new float[]{p1x, p2x};
    }
}

