/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum {
    eSUSPENSION(PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum.geteSUSPENSION()),
    eROAD_GEOMETRY_NORMAL(PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum.geteROAD_GEOMETRY_NORMAL()),
    eNONE(PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum.geteNONE());

    public final int value;

    private PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum(int value) {
        this.value = value;
    }

    private static native int _geteSUSPENSION();

    private static int geteSUSPENSION() {
        Loader.load();
        return PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum._geteSUSPENSION();
    }

    private static native int _geteROAD_GEOMETRY_NORMAL();

    private static int geteROAD_GEOMETRY_NORMAL() {
        Loader.load();
        return PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum._geteROAD_GEOMETRY_NORMAL();
    }

    private static native int _geteNONE();

    private static int geteNONE() {
        Loader.load();
        return PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum._geteNONE();
    }

    public static PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum forValue(int value) {
        for (int i = 0; i < PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum.values().length; ++i) {
            if (PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum.values()[i].value != value) continue;
            return PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum: " + value);
    }
}

