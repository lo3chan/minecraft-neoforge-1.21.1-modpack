/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleCommandValueResponseTableEnum {
    eMAX_NB_SPEED_RESPONSES(PxVehicleCommandValueResponseTableEnum.geteMAX_NB_SPEED_RESPONSES());

    public final int value;

    private PxVehicleCommandValueResponseTableEnum(int value) {
        this.value = value;
    }

    private static native int _geteMAX_NB_SPEED_RESPONSES();

    private static int geteMAX_NB_SPEED_RESPONSES() {
        Loader.load();
        return PxVehicleCommandValueResponseTableEnum._geteMAX_NB_SPEED_RESPONSES();
    }

    public static PxVehicleCommandValueResponseTableEnum forValue(int value) {
        for (int i = 0; i < PxVehicleCommandValueResponseTableEnum.values().length; ++i) {
            if (PxVehicleCommandValueResponseTableEnum.values()[i].value != value) continue;
            return PxVehicleCommandValueResponseTableEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxVehicleCommandValueResponseTableEnum: " + value);
    }
}

