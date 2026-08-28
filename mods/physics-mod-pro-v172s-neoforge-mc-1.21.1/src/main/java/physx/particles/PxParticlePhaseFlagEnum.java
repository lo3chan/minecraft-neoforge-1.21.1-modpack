/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import de.fabmax.physxjni.Loader;
import physx.PlatformChecks;

public enum PxParticlePhaseFlagEnum {
    eParticlePhaseGroupMask(PxParticlePhaseFlagEnum.geteParticlePhaseGroupMask()),
    eParticlePhaseFlagsMask(PxParticlePhaseFlagEnum.geteParticlePhaseFlagsMask()),
    eParticlePhaseSelfCollide(PxParticlePhaseFlagEnum.geteParticlePhaseSelfCollide()),
    eParticlePhaseSelfCollideFilter(PxParticlePhaseFlagEnum.geteParticlePhaseSelfCollideFilter()),
    eParticlePhaseFluid(PxParticlePhaseFlagEnum.geteParticlePhaseFluid());

    public final int value;

    private PxParticlePhaseFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteParticlePhaseGroupMask();

    private static int geteParticlePhaseGroupMask() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticlePhaseFlagEnum");
        return PxParticlePhaseFlagEnum._geteParticlePhaseGroupMask();
    }

    private static native int _geteParticlePhaseFlagsMask();

    private static int geteParticlePhaseFlagsMask() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticlePhaseFlagEnum");
        return PxParticlePhaseFlagEnum._geteParticlePhaseFlagsMask();
    }

    private static native int _geteParticlePhaseSelfCollide();

    private static int geteParticlePhaseSelfCollide() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticlePhaseFlagEnum");
        return PxParticlePhaseFlagEnum._geteParticlePhaseSelfCollide();
    }

    private static native int _geteParticlePhaseSelfCollideFilter();

    private static int geteParticlePhaseSelfCollideFilter() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticlePhaseFlagEnum");
        return PxParticlePhaseFlagEnum._geteParticlePhaseSelfCollideFilter();
    }

    private static native int _geteParticlePhaseFluid();

    private static int geteParticlePhaseFluid() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticlePhaseFlagEnum");
        return PxParticlePhaseFlagEnum._geteParticlePhaseFluid();
    }

    public static PxParticlePhaseFlagEnum forValue(int value) {
        for (int i = 0; i < PxParticlePhaseFlagEnum.values().length; ++i) {
            if (PxParticlePhaseFlagEnum.values()[i].value != value) continue;
            return PxParticlePhaseFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxParticlePhaseFlagEnum: " + value);
    }
}

