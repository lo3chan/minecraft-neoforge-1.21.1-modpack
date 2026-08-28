/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.dynamics;

import java.util.List;
import net.diebuddies.jbox2d.common.MathUtils;

public class Profile {
    private static final int LONG_AVG_NUMS = 20;
    private static final float LONG_FRACTION = 0.05f;
    private static final int SHORT_AVG_NUMS = 5;
    private static final float SHORT_FRACTION = 0.2f;
    public final ProfileEntry step = new ProfileEntry();
    public final ProfileEntry stepInit = new ProfileEntry();
    public final ProfileEntry collide = new ProfileEntry();
    public final ProfileEntry solveParticleSystem = new ProfileEntry();
    public final ProfileEntry solve = new ProfileEntry();
    public final ProfileEntry solveInit = new ProfileEntry();
    public final ProfileEntry solveVelocity = new ProfileEntry();
    public final ProfileEntry solvePosition = new ProfileEntry();
    public final ProfileEntry broadphase = new ProfileEntry();
    public final ProfileEntry solveTOI = new ProfileEntry();

    public void toDebugStrings(List<String> strings) {
        strings.add("Profile:");
        strings.add(" step: " + String.valueOf(this.step));
        strings.add("  init: " + String.valueOf(this.stepInit));
        strings.add("  collide: " + String.valueOf(this.collide));
        strings.add("  particles: " + String.valueOf(this.solveParticleSystem));
        strings.add("  solve: " + String.valueOf(this.solve));
        strings.add("   solveInit: " + String.valueOf(this.solveInit));
        strings.add("   solveVelocity: " + String.valueOf(this.solveVelocity));
        strings.add("   solvePosition: " + String.valueOf(this.solvePosition));
        strings.add("   broadphase: " + String.valueOf(this.broadphase));
        strings.add("  solveTOI: " + String.valueOf(this.solveTOI));
    }

    public static class ProfileEntry {
        float longAvg;
        float shortAvg;
        float min = Float.MAX_VALUE;
        float max = -3.4028235E38f;
        float accum;

        public void record(float value) {
            this.longAvg = this.longAvg * 0.95f + value * 0.05f;
            this.shortAvg = this.shortAvg * 0.8f + value * 0.2f;
            this.min = MathUtils.min(value, this.min);
            this.max = MathUtils.max(value, this.max);
        }

        public void startAccum() {
            this.accum = 0.0f;
        }

        public void accum(float value) {
            this.accum += value;
        }

        public void endAccum() {
            this.record(this.accum);
        }

        public String toString() {
            return String.format("%.2f (%.2f) [%.2f,%.2f]", Float.valueOf(this.shortAvg), Float.valueOf(this.longAvg), Float.valueOf(this.min), Float.valueOf(this.max));
        }
    }
}

