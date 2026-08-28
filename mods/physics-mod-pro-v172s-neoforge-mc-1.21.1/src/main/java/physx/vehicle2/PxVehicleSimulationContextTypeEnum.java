/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleSimulationContextTypeEnum {
    eDEFAULT(PxVehicleSimulationContextTypeEnum.geteDEFAULT()),
    ePHYSX(PxVehicleSimulationContextTypeEnum.getePHYSX());

    public final int value;

    private PxVehicleSimulationContextTypeEnum(int value) {
        this.value = value;
    }

    private static native int _geteDEFAULT();

    private static int geteDEFAULT() {
        Loader.load();
        return PxVehicleSimulationContextTypeEnum._geteDEFAULT();
    }

    private static native int _getePHYSX();

    private static int getePHYSX() {
        Loader.load();
        return PxVehicleSimulationContextTypeEnum._getePHYSX();
    }

    public static PxVehicleSimulationContextTypeEnum forValue(int value) {
        for (int i = 0; i < PxVehicleSimulationContextTypeEnum.values().length; ++i) {
            if (PxVehicleSimulationContextTypeEnum.values()[i].value != value) continue;
            return PxVehicleSimulationContextTypeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxVehicleSimulationContextTypeEnum: " + value);
    }
}

