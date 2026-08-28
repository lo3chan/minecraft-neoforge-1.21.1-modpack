/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import de.fabmax.physxjni.Loader;

public enum PxD6JointDriveFlagEnum {
    eACCELERATION(PxD6JointDriveFlagEnum.geteACCELERATION());

    public final int value;

    private PxD6JointDriveFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteACCELERATION();

    private static int geteACCELERATION() {
        Loader.load();
        return PxD6JointDriveFlagEnum._geteACCELERATION();
    }

    public static PxD6JointDriveFlagEnum forValue(int value) {
        for (int i = 0; i < PxD6JointDriveFlagEnum.values().length; ++i) {
            if (PxD6JointDriveFlagEnum.values()[i].value != value) continue;
            return PxD6JointDriveFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxD6JointDriveFlagEnum: " + value);
    }
}

