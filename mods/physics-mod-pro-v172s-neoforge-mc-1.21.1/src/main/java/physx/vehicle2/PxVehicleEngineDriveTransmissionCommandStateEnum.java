/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleEngineDriveTransmissionCommandStateEnum {
    eAUTOMATIC_GEAR(PxVehicleEngineDriveTransmissionCommandStateEnum.geteAUTOMATIC_GEAR());

    public final int value;

    private PxVehicleEngineDriveTransmissionCommandStateEnum(int value) {
        this.value = value;
    }

    private static native int _geteAUTOMATIC_GEAR();

    private static int geteAUTOMATIC_GEAR() {
        Loader.load();
        return PxVehicleEngineDriveTransmissionCommandStateEnum._geteAUTOMATIC_GEAR();
    }

    public static PxVehicleEngineDriveTransmissionCommandStateEnum forValue(int value) {
        for (int i = 0; i < PxVehicleEngineDriveTransmissionCommandStateEnum.values().length; ++i) {
            if (PxVehicleEngineDriveTransmissionCommandStateEnum.values()[i].value != value) continue;
            return PxVehicleEngineDriveTransmissionCommandStateEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxVehicleEngineDriveTransmissionCommandStateEnum: " + value);
    }
}

