/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum EngineDriveVehicleEnum {
    eDIFFTYPE_FOURWHEELDRIVE(EngineDriveVehicleEnum.geteDIFFTYPE_FOURWHEELDRIVE()),
    eDIFFTYPE_MULTIWHEELDRIVE(EngineDriveVehicleEnum.geteDIFFTYPE_MULTIWHEELDRIVE()),
    eDIFFTYPE_TANKDRIVE(EngineDriveVehicleEnum.geteDIFFTYPE_TANKDRIVE());

    public final int value;

    private EngineDriveVehicleEnum(int value) {
        this.value = value;
    }

    private static native int _geteDIFFTYPE_FOURWHEELDRIVE();

    private static int geteDIFFTYPE_FOURWHEELDRIVE() {
        Loader.load();
        return EngineDriveVehicleEnum._geteDIFFTYPE_FOURWHEELDRIVE();
    }

    private static native int _geteDIFFTYPE_MULTIWHEELDRIVE();

    private static int geteDIFFTYPE_MULTIWHEELDRIVE() {
        Loader.load();
        return EngineDriveVehicleEnum._geteDIFFTYPE_MULTIWHEELDRIVE();
    }

    private static native int _geteDIFFTYPE_TANKDRIVE();

    private static int geteDIFFTYPE_TANKDRIVE() {
        Loader.load();
        return EngineDriveVehicleEnum._geteDIFFTYPE_TANKDRIVE();
    }

    public static EngineDriveVehicleEnum forValue(int value) {
        for (int i = 0; i < EngineDriveVehicleEnum.values().length; ++i) {
            if (EngineDriveVehicleEnum.values()[i].value != value) continue;
            return EngineDriveVehicleEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum EngineDriveVehicleEnum: " + value);
    }
}

