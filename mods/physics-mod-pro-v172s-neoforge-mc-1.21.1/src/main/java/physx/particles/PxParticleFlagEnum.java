/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import de.fabmax.physxjni.Loader;
import physx.PlatformChecks;

public enum PxParticleFlagEnum {
    eDISABLE_SELF_COLLISION(PxParticleFlagEnum.geteDISABLE_SELF_COLLISION()),
    eDISABLE_RIGID_COLLISION(PxParticleFlagEnum.geteDISABLE_RIGID_COLLISION()),
    eFULL_DIFFUSE_ADVECTION(PxParticleFlagEnum.geteFULL_DIFFUSE_ADVECTION());

    public final int value;

    private PxParticleFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteDISABLE_SELF_COLLISION();

    private static int geteDISABLE_SELF_COLLISION() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleFlagEnum");
        return PxParticleFlagEnum._geteDISABLE_SELF_COLLISION();
    }

    private static native int _geteDISABLE_RIGID_COLLISION();

    private static int geteDISABLE_RIGID_COLLISION() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleFlagEnum");
        return PxParticleFlagEnum._geteDISABLE_RIGID_COLLISION();
    }

    private static native int _geteFULL_DIFFUSE_ADVECTION();

    private static int geteFULL_DIFFUSE_ADVECTION() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleFlagEnum");
        return PxParticleFlagEnum._geteFULL_DIFFUSE_ADVECTION();
    }

    public static PxParticleFlagEnum forValue(int value) {
        for (int i = 0; i < PxParticleFlagEnum.values().length; ++i) {
            if (PxParticleFlagEnum.values()[i].value != value) continue;
            return PxParticleFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxParticleFlagEnum: " + value);
    }
}

