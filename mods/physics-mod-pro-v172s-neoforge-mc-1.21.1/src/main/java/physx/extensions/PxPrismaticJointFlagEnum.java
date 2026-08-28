/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import de.fabmax.physxjni.Loader;

public enum PxPrismaticJointFlagEnum {
    eLIMIT_ENABLED(PxPrismaticJointFlagEnum.geteLIMIT_ENABLED());

    public final int value;

    private PxPrismaticJointFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteLIMIT_ENABLED();

    private static int geteLIMIT_ENABLED() {
        Loader.load();
        return PxPrismaticJointFlagEnum._geteLIMIT_ENABLED();
    }

    public static PxPrismaticJointFlagEnum forValue(int value) {
        for (int i = 0; i < PxPrismaticJointFlagEnum.values().length; ++i) {
            if (PxPrismaticJointFlagEnum.values()[i].value != value) continue;
            return PxPrismaticJointFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxPrismaticJointFlagEnum: " + value);
    }
}

