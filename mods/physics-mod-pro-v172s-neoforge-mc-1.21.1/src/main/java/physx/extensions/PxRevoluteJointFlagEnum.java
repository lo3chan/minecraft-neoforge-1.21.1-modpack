/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import de.fabmax.physxjni.Loader;

public enum PxRevoluteJointFlagEnum {
    eLIMIT_ENABLED(PxRevoluteJointFlagEnum.geteLIMIT_ENABLED()),
    eDRIVE_ENABLED(PxRevoluteJointFlagEnum.geteDRIVE_ENABLED()),
    eDRIVE_FREESPIN(PxRevoluteJointFlagEnum.geteDRIVE_FREESPIN());

    public final int value;

    private PxRevoluteJointFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteLIMIT_ENABLED();

    private static int geteLIMIT_ENABLED() {
        Loader.load();
        return PxRevoluteJointFlagEnum._geteLIMIT_ENABLED();
    }

    private static native int _geteDRIVE_ENABLED();

    private static int geteDRIVE_ENABLED() {
        Loader.load();
        return PxRevoluteJointFlagEnum._geteDRIVE_ENABLED();
    }

    private static native int _geteDRIVE_FREESPIN();

    private static int geteDRIVE_FREESPIN() {
        Loader.load();
        return PxRevoluteJointFlagEnum._geteDRIVE_FREESPIN();
    }

    public static PxRevoluteJointFlagEnum forValue(int value) {
        for (int i = 0; i < PxRevoluteJointFlagEnum.values().length; ++i) {
            if (PxRevoluteJointFlagEnum.values()[i].value != value) continue;
            return PxRevoluteJointFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxRevoluteJointFlagEnum: " + value);
    }
}

