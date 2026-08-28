/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxArticulationDriveTypeEnum {
    eFORCE(PxArticulationDriveTypeEnum.geteFORCE()),
    eACCELERATION(PxArticulationDriveTypeEnum.geteACCELERATION()),
    eTARGET(PxArticulationDriveTypeEnum.geteTARGET()),
    eVELOCITY(PxArticulationDriveTypeEnum.geteVELOCITY()),
    eNONE(PxArticulationDriveTypeEnum.geteNONE());

    public final int value;

    private PxArticulationDriveTypeEnum(int value) {
        this.value = value;
    }

    private static native int _geteFORCE();

    private static int geteFORCE() {
        Loader.load();
        return PxArticulationDriveTypeEnum._geteFORCE();
    }

    private static native int _geteACCELERATION();

    private static int geteACCELERATION() {
        Loader.load();
        return PxArticulationDriveTypeEnum._geteACCELERATION();
    }

    private static native int _geteTARGET();

    private static int geteTARGET() {
        Loader.load();
        return PxArticulationDriveTypeEnum._geteTARGET();
    }

    private static native int _geteVELOCITY();

    private static int geteVELOCITY() {
        Loader.load();
        return PxArticulationDriveTypeEnum._geteVELOCITY();
    }

    private static native int _geteNONE();

    private static int geteNONE() {
        Loader.load();
        return PxArticulationDriveTypeEnum._geteNONE();
    }

    public static PxArticulationDriveTypeEnum forValue(int value) {
        for (int i = 0; i < PxArticulationDriveTypeEnum.values().length; ++i) {
            if (PxArticulationDriveTypeEnum.values()[i].value != value) continue;
            return PxArticulationDriveTypeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxArticulationDriveTypeEnum: " + value);
    }
}

