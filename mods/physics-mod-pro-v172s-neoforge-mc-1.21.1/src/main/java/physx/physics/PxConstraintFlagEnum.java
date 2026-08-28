/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxConstraintFlagEnum {
    eBROKEN(PxConstraintFlagEnum.geteBROKEN()),
    eCOLLISION_ENABLED(PxConstraintFlagEnum.geteCOLLISION_ENABLED()),
    eVISUALIZATION(PxConstraintFlagEnum.geteVISUALIZATION()),
    eDRIVE_LIMITS_ARE_FORCES(PxConstraintFlagEnum.geteDRIVE_LIMITS_ARE_FORCES()),
    eIMPROVED_SLERP(PxConstraintFlagEnum.geteIMPROVED_SLERP()),
    eDISABLE_PREPROCESSING(PxConstraintFlagEnum.geteDISABLE_PREPROCESSING()),
    eENABLE_EXTENDED_LIMITS(PxConstraintFlagEnum.geteENABLE_EXTENDED_LIMITS()),
    eGPU_COMPATIBLE(PxConstraintFlagEnum.geteGPU_COMPATIBLE()),
    eALWAYS_UPDATE(PxConstraintFlagEnum.geteALWAYS_UPDATE()),
    eDISABLE_CONSTRAINT(PxConstraintFlagEnum.geteDISABLE_CONSTRAINT());

    public final int value;

    private PxConstraintFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteBROKEN();

    private static int geteBROKEN() {
        Loader.load();
        return PxConstraintFlagEnum._geteBROKEN();
    }

    private static native int _geteCOLLISION_ENABLED();

    private static int geteCOLLISION_ENABLED() {
        Loader.load();
        return PxConstraintFlagEnum._geteCOLLISION_ENABLED();
    }

    private static native int _geteVISUALIZATION();

    private static int geteVISUALIZATION() {
        Loader.load();
        return PxConstraintFlagEnum._geteVISUALIZATION();
    }

    private static native int _geteDRIVE_LIMITS_ARE_FORCES();

    private static int geteDRIVE_LIMITS_ARE_FORCES() {
        Loader.load();
        return PxConstraintFlagEnum._geteDRIVE_LIMITS_ARE_FORCES();
    }

    private static native int _geteIMPROVED_SLERP();

    private static int geteIMPROVED_SLERP() {
        Loader.load();
        return PxConstraintFlagEnum._geteIMPROVED_SLERP();
    }

    private static native int _geteDISABLE_PREPROCESSING();

    private static int geteDISABLE_PREPROCESSING() {
        Loader.load();
        return PxConstraintFlagEnum._geteDISABLE_PREPROCESSING();
    }

    private static native int _geteENABLE_EXTENDED_LIMITS();

    private static int geteENABLE_EXTENDED_LIMITS() {
        Loader.load();
        return PxConstraintFlagEnum._geteENABLE_EXTENDED_LIMITS();
    }

    private static native int _geteGPU_COMPATIBLE();

    private static int geteGPU_COMPATIBLE() {
        Loader.load();
        return PxConstraintFlagEnum._geteGPU_COMPATIBLE();
    }

    private static native int _geteALWAYS_UPDATE();

    private static int geteALWAYS_UPDATE() {
        Loader.load();
        return PxConstraintFlagEnum._geteALWAYS_UPDATE();
    }

    private static native int _geteDISABLE_CONSTRAINT();

    private static int geteDISABLE_CONSTRAINT() {
        Loader.load();
        return PxConstraintFlagEnum._geteDISABLE_CONSTRAINT();
    }

    public static PxConstraintFlagEnum forValue(int value) {
        for (int i = 0; i < PxConstraintFlagEnum.values().length; ++i) {
            if (PxConstraintFlagEnum.values()[i].value != value) continue;
            return PxConstraintFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxConstraintFlagEnum: " + value);
    }
}

