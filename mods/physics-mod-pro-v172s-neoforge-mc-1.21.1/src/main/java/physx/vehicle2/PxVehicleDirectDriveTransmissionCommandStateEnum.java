/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleDirectDriveTransmissionCommandStateEnum {
    eREVERSE(PxVehicleDirectDriveTransmissionCommandStateEnum.geteREVERSE()),
    eNEUTRAL(PxVehicleDirectDriveTransmissionCommandStateEnum.geteNEUTRAL()),
    eFORWARD(PxVehicleDirectDriveTransmissionCommandStateEnum.geteFORWARD());

    public final int value;

    private PxVehicleDirectDriveTransmissionCommandStateEnum(int value) {
        this.value = value;
    }

    private static native int _geteREVERSE();

    private static int geteREVERSE() {
        Loader.load();
        return PxVehicleDirectDriveTransmissionCommandStateEnum._geteREVERSE();
    }

    private static native int _geteNEUTRAL();

    private static int geteNEUTRAL() {
        Loader.load();
        return PxVehicleDirectDriveTransmissionCommandStateEnum._geteNEUTRAL();
    }

    private static native int _geteFORWARD();

    private static int geteFORWARD() {
        Loader.load();
        return PxVehicleDirectDriveTransmissionCommandStateEnum._geteFORWARD();
    }

    public static PxVehicleDirectDriveTransmissionCommandStateEnum forValue(int value) {
        for (int i = 0; i < PxVehicleDirectDriveTransmissionCommandStateEnum.values().length; ++i) {
            if (PxVehicleDirectDriveTransmissionCommandStateEnum.values()[i].value != value) continue;
            return PxVehicleDirectDriveTransmissionCommandStateEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxVehicleDirectDriveTransmissionCommandStateEnum: " + value);
    }
}

