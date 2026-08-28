/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import de.fabmax.physxjni.Loader;

public enum PxPvdInstrumentationFlagEnum {
    eDEBUG(PxPvdInstrumentationFlagEnum.geteDEBUG()),
    ePROFILE(PxPvdInstrumentationFlagEnum.getePROFILE()),
    eMEMORY(PxPvdInstrumentationFlagEnum.geteMEMORY()),
    eALL(PxPvdInstrumentationFlagEnum.geteALL());

    public final int value;

    private PxPvdInstrumentationFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteDEBUG();

    private static int geteDEBUG() {
        Loader.load();
        return PxPvdInstrumentationFlagEnum._geteDEBUG();
    }

    private static native int _getePROFILE();

    private static int getePROFILE() {
        Loader.load();
        return PxPvdInstrumentationFlagEnum._getePROFILE();
    }

    private static native int _geteMEMORY();

    private static int geteMEMORY() {
        Loader.load();
        return PxPvdInstrumentationFlagEnum._geteMEMORY();
    }

    private static native int _geteALL();

    private static int geteALL() {
        Loader.load();
        return PxPvdInstrumentationFlagEnum._geteALL();
    }

    public static PxPvdInstrumentationFlagEnum forValue(int value) {
        for (int i = 0; i < PxPvdInstrumentationFlagEnum.values().length; ++i) {
            if (PxPvdInstrumentationFlagEnum.values()[i].value != value) continue;
            return PxPvdInstrumentationFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxPvdInstrumentationFlagEnum: " + value);
    }
}

