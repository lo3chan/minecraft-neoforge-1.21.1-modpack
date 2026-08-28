/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import de.fabmax.physxjni.Loader;

public enum PxD6DriveEnum {
    eX(PxD6DriveEnum.geteX()),
    eY(PxD6DriveEnum.geteY()),
    eZ(PxD6DriveEnum.geteZ()),
    eSWING(PxD6DriveEnum.geteSWING()),
    eTWIST(PxD6DriveEnum.geteTWIST()),
    eSLERP(PxD6DriveEnum.geteSLERP());

    public final int value;

    private PxD6DriveEnum(int value) {
        this.value = value;
    }

    private static native int _geteX();

    private static int geteX() {
        Loader.load();
        return PxD6DriveEnum._geteX();
    }

    private static native int _geteY();

    private static int geteY() {
        Loader.load();
        return PxD6DriveEnum._geteY();
    }

    private static native int _geteZ();

    private static int geteZ() {
        Loader.load();
        return PxD6DriveEnum._geteZ();
    }

    private static native int _geteSWING();

    private static int geteSWING() {
        Loader.load();
        return PxD6DriveEnum._geteSWING();
    }

    private static native int _geteTWIST();

    private static int geteTWIST() {
        Loader.load();
        return PxD6DriveEnum._geteTWIST();
    }

    private static native int _geteSLERP();

    private static int geteSLERP() {
        Loader.load();
        return PxD6DriveEnum._geteSLERP();
    }

    public static PxD6DriveEnum forValue(int value) {
        for (int i = 0; i < PxD6DriveEnum.values().length; ++i) {
            if (PxD6DriveEnum.values()[i].value != value) continue;
            return PxD6DriveEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxD6DriveEnum: " + value);
    }
}

