/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxRigidBodyFlagEnum {
    eKINEMATIC(PxRigidBodyFlagEnum.geteKINEMATIC()),
    eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES(PxRigidBodyFlagEnum.geteUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES()),
    eENABLE_CCD(PxRigidBodyFlagEnum.geteENABLE_CCD()),
    eENABLE_CCD_FRICTION(PxRigidBodyFlagEnum.geteENABLE_CCD_FRICTION()),
    eENABLE_POSE_INTEGRATION_PREVIEW(PxRigidBodyFlagEnum.geteENABLE_POSE_INTEGRATION_PREVIEW()),
    eENABLE_SPECULATIVE_CCD(PxRigidBodyFlagEnum.geteENABLE_SPECULATIVE_CCD()),
    eENABLE_CCD_MAX_CONTACT_IMPULSE(PxRigidBodyFlagEnum.geteENABLE_CCD_MAX_CONTACT_IMPULSE()),
    eRETAIN_ACCELERATIONS(PxRigidBodyFlagEnum.geteRETAIN_ACCELERATIONS());

    public final int value;

    private PxRigidBodyFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteKINEMATIC();

    private static int geteKINEMATIC() {
        Loader.load();
        return PxRigidBodyFlagEnum._geteKINEMATIC();
    }

    private static native int _geteUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES();

    private static int geteUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES() {
        Loader.load();
        return PxRigidBodyFlagEnum._geteUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES();
    }

    private static native int _geteENABLE_CCD();

    private static int geteENABLE_CCD() {
        Loader.load();
        return PxRigidBodyFlagEnum._geteENABLE_CCD();
    }

    private static native int _geteENABLE_CCD_FRICTION();

    private static int geteENABLE_CCD_FRICTION() {
        Loader.load();
        return PxRigidBodyFlagEnum._geteENABLE_CCD_FRICTION();
    }

    private static native int _geteENABLE_POSE_INTEGRATION_PREVIEW();

    private static int geteENABLE_POSE_INTEGRATION_PREVIEW() {
        Loader.load();
        return PxRigidBodyFlagEnum._geteENABLE_POSE_INTEGRATION_PREVIEW();
    }

    private static native int _geteENABLE_SPECULATIVE_CCD();

    private static int geteENABLE_SPECULATIVE_CCD() {
        Loader.load();
        return PxRigidBodyFlagEnum._geteENABLE_SPECULATIVE_CCD();
    }

    private static native int _geteENABLE_CCD_MAX_CONTACT_IMPULSE();

    private static int geteENABLE_CCD_MAX_CONTACT_IMPULSE() {
        Loader.load();
        return PxRigidBodyFlagEnum._geteENABLE_CCD_MAX_CONTACT_IMPULSE();
    }

    private static native int _geteRETAIN_ACCELERATIONS();

    private static int geteRETAIN_ACCELERATIONS() {
        Loader.load();
        return PxRigidBodyFlagEnum._geteRETAIN_ACCELERATIONS();
    }

    public static PxRigidBodyFlagEnum forValue(int value) {
        for (int i = 0; i < PxRigidBodyFlagEnum.values().length; ++i) {
            if (PxRigidBodyFlagEnum.values()[i].value != value) continue;
            return PxRigidBodyFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxRigidBodyFlagEnum: " + value);
    }
}

