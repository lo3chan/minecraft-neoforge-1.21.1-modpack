/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxHitFlagEnum {
    ePOSITION(PxHitFlagEnum.getePOSITION()),
    eNORMAL(PxHitFlagEnum.geteNORMAL()),
    eUV(PxHitFlagEnum.geteUV()),
    eASSUME_NO_INITIAL_OVERLAP(PxHitFlagEnum.geteASSUME_NO_INITIAL_OVERLAP()),
    eMESH_MULTIPLE(PxHitFlagEnum.geteMESH_MULTIPLE()),
    eMESH_ANY(PxHitFlagEnum.geteMESH_ANY()),
    eMESH_BOTH_SIDES(PxHitFlagEnum.geteMESH_BOTH_SIDES()),
    ePRECISE_SWEEP(PxHitFlagEnum.getePRECISE_SWEEP()),
    eMTD(PxHitFlagEnum.geteMTD()),
    eFACE_INDEX(PxHitFlagEnum.geteFACE_INDEX()),
    eDEFAULT(PxHitFlagEnum.geteDEFAULT()),
    eMODIFIABLE_FLAGS(PxHitFlagEnum.geteMODIFIABLE_FLAGS());

    public final int value;

    private PxHitFlagEnum(int value) {
        this.value = value;
    }

    private static native int _getePOSITION();

    private static int getePOSITION() {
        Loader.load();
        return PxHitFlagEnum._getePOSITION();
    }

    private static native int _geteNORMAL();

    private static int geteNORMAL() {
        Loader.load();
        return PxHitFlagEnum._geteNORMAL();
    }

    private static native int _geteUV();

    private static int geteUV() {
        Loader.load();
        return PxHitFlagEnum._geteUV();
    }

    private static native int _geteASSUME_NO_INITIAL_OVERLAP();

    private static int geteASSUME_NO_INITIAL_OVERLAP() {
        Loader.load();
        return PxHitFlagEnum._geteASSUME_NO_INITIAL_OVERLAP();
    }

    private static native int _geteMESH_MULTIPLE();

    private static int geteMESH_MULTIPLE() {
        Loader.load();
        return PxHitFlagEnum._geteMESH_MULTIPLE();
    }

    private static native int _geteMESH_ANY();

    private static int geteMESH_ANY() {
        Loader.load();
        return PxHitFlagEnum._geteMESH_ANY();
    }

    private static native int _geteMESH_BOTH_SIDES();

    private static int geteMESH_BOTH_SIDES() {
        Loader.load();
        return PxHitFlagEnum._geteMESH_BOTH_SIDES();
    }

    private static native int _getePRECISE_SWEEP();

    private static int getePRECISE_SWEEP() {
        Loader.load();
        return PxHitFlagEnum._getePRECISE_SWEEP();
    }

    private static native int _geteMTD();

    private static int geteMTD() {
        Loader.load();
        return PxHitFlagEnum._geteMTD();
    }

    private static native int _geteFACE_INDEX();

    private static int geteFACE_INDEX() {
        Loader.load();
        return PxHitFlagEnum._geteFACE_INDEX();
    }

    private static native int _geteDEFAULT();

    private static int geteDEFAULT() {
        Loader.load();
        return PxHitFlagEnum._geteDEFAULT();
    }

    private static native int _geteMODIFIABLE_FLAGS();

    private static int geteMODIFIABLE_FLAGS() {
        Loader.load();
        return PxHitFlagEnum._geteMODIFIABLE_FLAGS();
    }

    public static PxHitFlagEnum forValue(int value) {
        for (int i = 0; i < PxHitFlagEnum.values().length; ++i) {
            if (PxHitFlagEnum.values()[i].value != value) continue;
            return PxHitFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxHitFlagEnum: " + value);
    }
}

