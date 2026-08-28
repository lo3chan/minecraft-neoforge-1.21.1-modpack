/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import de.fabmax.physxjni.Loader;

public enum PxSphericalJointFlagEnum {
    eLIMIT_ENABLED(PxSphericalJointFlagEnum.geteLIMIT_ENABLED());

    public final int value;

    private PxSphericalJointFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteLIMIT_ENABLED();

    private static int geteLIMIT_ENABLED() {
        Loader.load();
        return PxSphericalJointFlagEnum._geteLIMIT_ENABLED();
    }

    public static PxSphericalJointFlagEnum forValue(int value) {
        for (int i = 0; i < PxSphericalJointFlagEnum.values().length; ++i) {
            if (PxSphericalJointFlagEnum.values()[i].value != value) continue;
            return PxSphericalJointFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxSphericalJointFlagEnum: " + value);
    }
}

