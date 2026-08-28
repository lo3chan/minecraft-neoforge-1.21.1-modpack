/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleAxesEnum {
    ePosX(PxVehicleAxesEnum.getePosX()),
    eNegX(PxVehicleAxesEnum.geteNegX()),
    ePosY(PxVehicleAxesEnum.getePosY()),
    eNegY(PxVehicleAxesEnum.geteNegY()),
    ePosZ(PxVehicleAxesEnum.getePosZ()),
    eNegZ(PxVehicleAxesEnum.geteNegZ());

    public final int value;

    private PxVehicleAxesEnum(int value) {
        this.value = value;
    }

    private static native int _getePosX();

    private static int getePosX() {
        Loader.load();
        return PxVehicleAxesEnum._getePosX();
    }

    private static native int _geteNegX();

    private static int geteNegX() {
        Loader.load();
        return PxVehicleAxesEnum._geteNegX();
    }

    private static native int _getePosY();

    private static int getePosY() {
        Loader.load();
        return PxVehicleAxesEnum._getePosY();
    }

    private static native int _geteNegY();

    private static int geteNegY() {
        Loader.load();
        return PxVehicleAxesEnum._geteNegY();
    }

    private static native int _getePosZ();

    private static int getePosZ() {
        Loader.load();
        return PxVehicleAxesEnum._getePosZ();
    }

    private static native int _geteNegZ();

    private static int geteNegZ() {
        Loader.load();
        return PxVehicleAxesEnum._geteNegZ();
    }

    public static PxVehicleAxesEnum forValue(int value) {
        for (int i = 0; i < PxVehicleAxesEnum.values().length; ++i) {
            if (PxVehicleAxesEnum.values()[i].value != value) continue;
            return PxVehicleAxesEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxVehicleAxesEnum: " + value);
    }
}

