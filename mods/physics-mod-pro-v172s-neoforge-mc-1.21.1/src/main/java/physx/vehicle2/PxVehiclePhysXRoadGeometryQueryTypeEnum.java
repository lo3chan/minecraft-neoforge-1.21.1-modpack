/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehiclePhysXRoadGeometryQueryTypeEnum {
    eNONE(PxVehiclePhysXRoadGeometryQueryTypeEnum.geteNONE()),
    eRAYCAST(PxVehiclePhysXRoadGeometryQueryTypeEnum.geteRAYCAST()),
    eSWEEP(PxVehiclePhysXRoadGeometryQueryTypeEnum.geteSWEEP());

    public final int value;

    private PxVehiclePhysXRoadGeometryQueryTypeEnum(int value) {
        this.value = value;
    }

    private static native int _geteNONE();

    private static int geteNONE() {
        Loader.load();
        return PxVehiclePhysXRoadGeometryQueryTypeEnum._geteNONE();
    }

    private static native int _geteRAYCAST();

    private static int geteRAYCAST() {
        Loader.load();
        return PxVehiclePhysXRoadGeometryQueryTypeEnum._geteRAYCAST();
    }

    private static native int _geteSWEEP();

    private static int geteSWEEP() {
        Loader.load();
        return PxVehiclePhysXRoadGeometryQueryTypeEnum._geteSWEEP();
    }

    public static PxVehiclePhysXRoadGeometryQueryTypeEnum forValue(int value) {
        for (int i = 0; i < PxVehiclePhysXRoadGeometryQueryTypeEnum.values().length; ++i) {
            if (PxVehiclePhysXRoadGeometryQueryTypeEnum.values()[i].value != value) continue;
            return PxVehiclePhysXRoadGeometryQueryTypeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxVehiclePhysXRoadGeometryQueryTypeEnum: " + value);
    }
}

