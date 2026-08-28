/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import de.fabmax.physxjni.Loader;

public enum PxBaseFlagEnum {
    eOWNS_MEMORY(PxBaseFlagEnum.geteOWNS_MEMORY()),
    eIS_RELEASABLE(PxBaseFlagEnum.geteIS_RELEASABLE());

    public final int value;

    private PxBaseFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteOWNS_MEMORY();

    private static int geteOWNS_MEMORY() {
        Loader.load();
        return PxBaseFlagEnum._geteOWNS_MEMORY();
    }

    private static native int _geteIS_RELEASABLE();

    private static int geteIS_RELEASABLE() {
        Loader.load();
        return PxBaseFlagEnum._geteIS_RELEASABLE();
    }

    public static PxBaseFlagEnum forValue(int value) {
        for (int i = 0; i < PxBaseFlagEnum.values().length; ++i) {
            if (PxBaseFlagEnum.values()[i].value != value) continue;
            return PxBaseFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxBaseFlagEnum: " + value);
    }
}

