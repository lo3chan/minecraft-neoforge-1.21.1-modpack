/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxRigidDynamicLockFlagEnum {
    eLOCK_LINEAR_X(PxRigidDynamicLockFlagEnum.geteLOCK_LINEAR_X()),
    eLOCK_LINEAR_Y(PxRigidDynamicLockFlagEnum.geteLOCK_LINEAR_Y()),
    eLOCK_LINEAR_Z(PxRigidDynamicLockFlagEnum.geteLOCK_LINEAR_Z()),
    eLOCK_ANGULAR_X(PxRigidDynamicLockFlagEnum.geteLOCK_ANGULAR_X()),
    eLOCK_ANGULAR_Y(PxRigidDynamicLockFlagEnum.geteLOCK_ANGULAR_Y()),
    eLOCK_ANGULAR_Z(PxRigidDynamicLockFlagEnum.geteLOCK_ANGULAR_Z());

    public final int value;

    private PxRigidDynamicLockFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteLOCK_LINEAR_X();

    private static int geteLOCK_LINEAR_X() {
        Loader.load();
        return PxRigidDynamicLockFlagEnum._geteLOCK_LINEAR_X();
    }

    private static native int _geteLOCK_LINEAR_Y();

    private static int geteLOCK_LINEAR_Y() {
        Loader.load();
        return PxRigidDynamicLockFlagEnum._geteLOCK_LINEAR_Y();
    }

    private static native int _geteLOCK_LINEAR_Z();

    private static int geteLOCK_LINEAR_Z() {
        Loader.load();
        return PxRigidDynamicLockFlagEnum._geteLOCK_LINEAR_Z();
    }

    private static native int _geteLOCK_ANGULAR_X();

    private static int geteLOCK_ANGULAR_X() {
        Loader.load();
        return PxRigidDynamicLockFlagEnum._geteLOCK_ANGULAR_X();
    }

    private static native int _geteLOCK_ANGULAR_Y();

    private static int geteLOCK_ANGULAR_Y() {
        Loader.load();
        return PxRigidDynamicLockFlagEnum._geteLOCK_ANGULAR_Y();
    }

    private static native int _geteLOCK_ANGULAR_Z();

    private static int geteLOCK_ANGULAR_Z() {
        Loader.load();
        return PxRigidDynamicLockFlagEnum._geteLOCK_ANGULAR_Z();
    }

    public static PxRigidDynamicLockFlagEnum forValue(int value) {
        for (int i = 0; i < PxRigidDynamicLockFlagEnum.values().length; ++i) {
            if (PxRigidDynamicLockFlagEnum.values()[i].value != value) continue;
            return PxRigidDynamicLockFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxRigidDynamicLockFlagEnum: " + value);
    }
}

