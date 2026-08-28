/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleGearboxParamsEnum {
    eMAX_NB_GEARS(PxVehicleGearboxParamsEnum.geteMAX_NB_GEARS());

    public final int value;

    private PxVehicleGearboxParamsEnum(int value) {
        this.value = value;
    }

    private static native int _geteMAX_NB_GEARS();

    private static int geteMAX_NB_GEARS() {
        Loader.load();
        return PxVehicleGearboxParamsEnum._geteMAX_NB_GEARS();
    }

    public static PxVehicleGearboxParamsEnum forValue(int value) {
        for (int i = 0; i < PxVehicleGearboxParamsEnum.values().length; ++i) {
            if (PxVehicleGearboxParamsEnum.values()[i].value != value) continue;
            return PxVehicleGearboxParamsEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxVehicleGearboxParamsEnum: " + value);
    }
}

