/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import de.fabmax.physxjni.Loader;
import physx.PlatformChecks;

public enum PxParticleSolverTypeEnum {
    ePBD(PxParticleSolverTypeEnum.getePBD()),
    eFLIP(PxParticleSolverTypeEnum.geteFLIP()),
    eMPM(PxParticleSolverTypeEnum.geteMPM());

    public final int value;

    private PxParticleSolverTypeEnum(int value) {
        this.value = value;
    }

    private static native int _getePBD();

    private static int getePBD() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleSolverTypeEnum");
        return PxParticleSolverTypeEnum._getePBD();
    }

    private static native int _geteFLIP();

    private static int geteFLIP() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleSolverTypeEnum");
        return PxParticleSolverTypeEnum._geteFLIP();
    }

    private static native int _geteMPM();

    private static int geteMPM() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleSolverTypeEnum");
        return PxParticleSolverTypeEnum._geteMPM();
    }

    public static PxParticleSolverTypeEnum forValue(int value) {
        for (int i = 0; i < PxParticleSolverTypeEnum.values().length; ++i) {
            if (PxParticleSolverTypeEnum.values()[i].value != value) continue;
            return PxParticleSolverTypeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxParticleSolverTypeEnum: " + value);
    }
}

