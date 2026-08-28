/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleCommandNonLinearResponseParamsEnum {
    eMAX_NB_COMMAND_VALUES(PxVehicleCommandNonLinearResponseParamsEnum.geteMAX_NB_COMMAND_VALUES());

    public final int value;

    private PxVehicleCommandNonLinearResponseParamsEnum(int value) {
        this.value = value;
    }

    private static native int _geteMAX_NB_COMMAND_VALUES();

    private static int geteMAX_NB_COMMAND_VALUES() {
        Loader.load();
        return PxVehicleCommandNonLinearResponseParamsEnum._geteMAX_NB_COMMAND_VALUES();
    }

    public static PxVehicleCommandNonLinearResponseParamsEnum forValue(int value) {
        for (int i = 0; i < PxVehicleCommandNonLinearResponseParamsEnum.values().length; ++i) {
            if (PxVehicleCommandNonLinearResponseParamsEnum.values()[i].value != value) continue;
            return PxVehicleCommandNonLinearResponseParamsEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxVehicleCommandNonLinearResponseParamsEnum: " + value);
    }
}

