/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Math
 *  org.joml.Vector2f
 */
package net.diebuddies.math;

import net.diebuddies.math.Bezier;
import net.diebuddies.math.Curve;
import net.diebuddies.math.Math;
import org.joml.Vector2f;

public class Bezier2D
implements Curve {
    public static final Bezier2D EASE_IN_SINE = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.12f, 0.0f), new Vector2f(0.41f, 0.0f));
    public static final Bezier2D EASE_OUT_SINE = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.61f, 1.0f), new Vector2f(0.88f, 1.0f));
    public static final Bezier2D EASE_IN_OUT_SINE = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.37f, 0.0f), new Vector2f(0.63f, 1.0f));
    public static final Bezier2D EASE_IN_CUBIC = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.32f, 0.0f), new Vector2f(0.67f, 0.0f));
    public static final Bezier2D EASE_OUT_CUBIC = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.33f, 1.0f), new Vector2f(0.68f, 1.0f));
    public static final Bezier2D EASE_IN_OUT_CUBIC = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.65f, 0.0f), new Vector2f(0.35f, 1.0f));
    public static final Bezier2D EASE_IN_QUINT = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.64f, 0.0f), new Vector2f(0.78f, 0.0f));
    public static final Bezier2D EASE_OUT_QUINT = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.22f, 1.0f), new Vector2f(0.36f, 1.0f));
    public static final Bezier2D EASE_IN_OUT_QUINT = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.83f, 0.0f), new Vector2f(0.17f, 1.0f));
    public static final Bezier2D EASE_IN_CIRC = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.55f, 0.0f), new Vector2f(1.0f, 0.45f));
    public static final Bezier2D EASE_OUT_CIRC = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.0f, 0.55f), new Vector2f(0.45f, 1.0f));
    public static final Bezier2D EASE_IN_OUT_CIRC = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.85f, 0.0f), new Vector2f(0.15f, 1.0f));
    public static final Bezier2D EASE_IN_QUAD = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.11f, 0.0f), new Vector2f(0.5f, 0.0f));
    public static final Bezier2D EASE_OUT_QUAD = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.5f, 1.0f), new Vector2f(0.89f, 1.0f));
    public static final Bezier2D EASE_IN_OUT_QUAD = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.45f, 0.0f), new Vector2f(0.55f, 1.0f));
    public static final Bezier2D EASE_IN_QUART = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.5f, 0.0f), new Vector2f(0.75f, 0.0f));
    public static final Bezier2D EASE_OUT_QUART = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.25f, 1.0f), new Vector2f(0.5f, 1.0f));
    public static final Bezier2D EASE_IN_OUT_QUART = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.76f, 0.0f), new Vector2f(0.24f, 1.0f));
    public static final Bezier2D EASE_IN_EXPO = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.7f, 0.0f), new Vector2f(0.84f, 0.0f));
    public static final Bezier2D EASE_OUT_EXPO = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.16f, 1.0f), new Vector2f(0.3f, 1.0f));
    public static final Bezier2D EASE_IN_OUT_EXPO = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.87f, 0.0f), new Vector2f(0.13f, 1.0f));
    public static final Bezier2D EASE_IN_BACK = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.36f, 0.0f), new Vector2f(0.66f, -0.56f));
    public static final Bezier2D EASE_OUT_BACK = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.34f, 1.56f), new Vector2f(0.64f, 1.0f));
    public static final Bezier2D EASE_IN_OUT_BACK = new Bezier2D(new Vector2f(0.0f), new Vector2f(1.0f), new Vector2f(0.68f, -0.6f), new Vector2f(0.32f, 1.6f));
    public Bezier x;
    public Bezier y;
    public float[] sampleXForTValues = new float[20];

    public Bezier2D(Vector2f p0, Vector2f p1, Vector2f c0, Vector2f c1) {
        this.x = new Bezier(p0.x, p1.x, c0.x, c1.x);
        this.y = new Bezier(p0.y, p1.y, c0.y, c1.y);
        this.initSampleValues();
    }

    private void initSampleValues() {
        for (int i = 0; i < this.sampleXForTValues.length; ++i) {
            this.sampleXForTValues[i] = this.x.get((float)i / (float)(this.sampleXForTValues.length - 1));
        }
    }

    public void get(float time, Vector2f dst) {
        dst.set(this.x.get(time), this.y.get(time));
    }

    @Override
    public float get(float time) {
        float indexFloat = Math.clamp(time * ((float)this.sampleXForTValues.length - 1.0f), 0.0f, (float)(this.sampleXForTValues.length - 1));
        float fraction = indexFloat - (float)((int)indexFloat);
        int index = 0;
        for (int i = 0; i < this.sampleXForTValues.length; ++i) {
            if (!(time < this.sampleXForTValues[i])) continue;
            index = i;
            break;
        }
        if (index == this.sampleXForTValues.length - 1) {
            return this.y.get(time);
        }
        float ntime = org.joml.Math.lerp((float)this.sampleXForTValues[index], (float)this.sampleXForTValues[index + 1], (float)fraction);
        return this.y.get(ntime);
    }
}

