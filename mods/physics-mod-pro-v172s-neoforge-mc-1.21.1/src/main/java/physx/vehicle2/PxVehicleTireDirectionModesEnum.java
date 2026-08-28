/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleTireDirectionModesEnum {
    eLONGITUDINAL(PxVehicleTireDirectionModesEnum.geteLONGITUDINAL()),
    eLATERAL(PxVehicleTireDirectionModesEnum.geteLATERAL());

    public final int value;

    private PxVehicleTireDirectionModesEnum(int value) {
        this.value = value;
    }

    private static native int _geteLONGITUDINAL();

    private static int geteLONGITUDINAL() {
        Loader.load();
        return PxVehicleTireDirectionModesEnum._geteLONGITUDINAL();
    }

    private static native int _geteLATERAL();

    private static int geteLATERAL() {
        Loader.load();
        return PxVehicleTireDirectionModesEnum._geteLATERAL();
    }

    public static PxVehicleTireDirectionModesEnum forValue(int value) {
        for (int i = 0; i < PxVehicleTireDirectionModesEnum.values().length; ++i) {
            if (PxVehicleTireDirectionModesEnum.values()[i].value != value) continue;
            return PxVehicleTireDirectionModesEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxVehicleTireDirectionModesEnum: " + value);
    }
}

