/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehiclePhysXActorUpdateModeEnum {
    eAPPLY_VELOCITY(PxVehiclePhysXActorUpdateModeEnum.geteAPPLY_VELOCITY()),
    eAPPLY_ACCELERATION(PxVehiclePhysXActorUpdateModeEnum.geteAPPLY_ACCELERATION());

    public final int value;

    private PxVehiclePhysXActorUpdateModeEnum(int value) {
        this.value = value;
    }

    private static native int _geteAPPLY_VELOCITY();

    private static int geteAPPLY_VELOCITY() {
        Loader.load();
        return PxVehiclePhysXActorUpdateModeEnum._geteAPPLY_VELOCITY();
    }

    private static native int _geteAPPLY_ACCELERATION();

    private static int geteAPPLY_ACCELERATION() {
        Loader.load();
        return PxVehiclePhysXActorUpdateModeEnum._geteAPPLY_ACCELERATION();
    }

    public static PxVehiclePhysXActorUpdateModeEnum forValue(int value) {
        for (int i = 0; i < PxVehiclePhysXActorUpdateModeEnum.values().length; ++i) {
            if (PxVehiclePhysXActorUpdateModeEnum.values()[i].value != value) continue;
            return PxVehiclePhysXActorUpdateModeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxVehiclePhysXActorUpdateModeEnum: " + value);
    }
}

