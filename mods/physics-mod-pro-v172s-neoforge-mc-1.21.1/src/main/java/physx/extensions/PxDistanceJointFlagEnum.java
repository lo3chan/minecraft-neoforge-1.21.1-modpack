/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import de.fabmax.physxjni.Loader;

public enum PxDistanceJointFlagEnum {
    eMAX_DISTANCE_ENABLED(PxDistanceJointFlagEnum.geteMAX_DISTANCE_ENABLED()),
    eMIN_DISTANCE_ENABLED(PxDistanceJointFlagEnum.geteMIN_DISTANCE_ENABLED()),
    eSPRING_ENABLED(PxDistanceJointFlagEnum.geteSPRING_ENABLED());

    public final int value;

    private PxDistanceJointFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteMAX_DISTANCE_ENABLED();

    private static int geteMAX_DISTANCE_ENABLED() {
        Loader.load();
        return PxDistanceJointFlagEnum._geteMAX_DISTANCE_ENABLED();
    }

    private static native int _geteMIN_DISTANCE_ENABLED();

    private static int geteMIN_DISTANCE_ENABLED() {
        Loader.load();
        return PxDistanceJointFlagEnum._geteMIN_DISTANCE_ENABLED();
    }

    private static native int _geteSPRING_ENABLED();

    private static int geteSPRING_ENABLED() {
        Loader.load();
        return PxDistanceJointFlagEnum._geteSPRING_ENABLED();
    }

    public static PxDistanceJointFlagEnum forValue(int value) {
        for (int i = 0; i < PxDistanceJointFlagEnum.values().length; ++i) {
            if (PxDistanceJointFlagEnum.values()[i].value != value) continue;
            return PxDistanceJointFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxDistanceJointFlagEnum: " + value);
    }
}

