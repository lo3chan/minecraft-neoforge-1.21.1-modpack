/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleSuspensionJounceCalculationTypeEnum {
    eRAYCAST(PxVehicleSuspensionJounceCalculationTypeEnum.geteRAYCAST()),
    eSWEEP(PxVehicleSuspensionJounceCalculationTypeEnum.geteSWEEP());

    public final int value;

    private PxVehicleSuspensionJounceCalculationTypeEnum(int value) {
        this.value = value;
    }

    private static native int _geteRAYCAST();

    private static int geteRAYCAST() {
        Loader.load();
        return PxVehicleSuspensionJounceCalculationTypeEnum._geteRAYCAST();
    }

    private static native int _geteSWEEP();

    private static int geteSWEEP() {
        Loader.load();
        return PxVehicleSuspensionJounceCalculationTypeEnum._geteSWEEP();
    }

    public static PxVehicleSuspensionJounceCalculationTypeEnum forValue(int value) {
        for (int i = 0; i < PxVehicleSuspensionJounceCalculationTypeEnum.values().length; ++i) {
            if (PxVehicleSuspensionJounceCalculationTypeEnum.values()[i].value != value) continue;
            return PxVehicleSuspensionJounceCalculationTypeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxVehicleSuspensionJounceCalculationTypeEnum: " + value);
    }
}

