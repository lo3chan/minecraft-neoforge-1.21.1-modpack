/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehiclePhysXConstraintLimitsEnum {
    eNB_DOFS_PER_PXCONSTRAINT(PxVehiclePhysXConstraintLimitsEnum.geteNB_DOFS_PER_PXCONSTRAINT()),
    eNB_DOFS_PER_WHEEL(PxVehiclePhysXConstraintLimitsEnum.geteNB_DOFS_PER_WHEEL()),
    eNB_WHEELS_PER_PXCONSTRAINT(PxVehiclePhysXConstraintLimitsEnum.geteNB_WHEELS_PER_PXCONSTRAINT()),
    eNB_CONSTRAINTS_PER_VEHICLE(PxVehiclePhysXConstraintLimitsEnum.geteNB_CONSTRAINTS_PER_VEHICLE());

    public final int value;

    private PxVehiclePhysXConstraintLimitsEnum(int value) {
        this.value = value;
    }

    private static native int _geteNB_DOFS_PER_PXCONSTRAINT();

    private static int geteNB_DOFS_PER_PXCONSTRAINT() {
        Loader.load();
        return PxVehiclePhysXConstraintLimitsEnum._geteNB_DOFS_PER_PXCONSTRAINT();
    }

    private static native int _geteNB_DOFS_PER_WHEEL();

    private static int geteNB_DOFS_PER_WHEEL() {
        Loader.load();
        return PxVehiclePhysXConstraintLimitsEnum._geteNB_DOFS_PER_WHEEL();
    }

    private static native int _geteNB_WHEELS_PER_PXCONSTRAINT();

    private static int geteNB_WHEELS_PER_PXCONSTRAINT() {
        Loader.load();
        return PxVehiclePhysXConstraintLimitsEnum._geteNB_WHEELS_PER_PXCONSTRAINT();
    }

    private static native int _geteNB_CONSTRAINTS_PER_VEHICLE();

    private static int geteNB_CONSTRAINTS_PER_VEHICLE() {
        Loader.load();
        return PxVehiclePhysXConstraintLimitsEnum._geteNB_CONSTRAINTS_PER_VEHICLE();
    }

    public static PxVehiclePhysXConstraintLimitsEnum forValue(int value) {
        for (int i = 0; i < PxVehiclePhysXConstraintLimitsEnum.values().length; ++i) {
            if (PxVehiclePhysXConstraintLimitsEnum.values()[i].value != value) continue;
            return PxVehiclePhysXConstraintLimitsEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxVehiclePhysXConstraintLimitsEnum: " + value);
    }
}

