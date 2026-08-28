/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxForceModeEnum {
    eFORCE(PxForceModeEnum.geteFORCE()),
    eIMPULSE(PxForceModeEnum.geteIMPULSE()),
    eVELOCITY_CHANGE(PxForceModeEnum.geteVELOCITY_CHANGE()),
    eACCELERATION(PxForceModeEnum.geteACCELERATION());

    public final int value;

    private PxForceModeEnum(int value) {
        this.value = value;
    }

    private static native int _geteFORCE();

    private static int geteFORCE() {
        Loader.load();
        return PxForceModeEnum._geteFORCE();
    }

    private static native int _geteIMPULSE();

    private static int geteIMPULSE() {
        Loader.load();
        return PxForceModeEnum._geteIMPULSE();
    }

    private static native int _geteVELOCITY_CHANGE();

    private static int geteVELOCITY_CHANGE() {
        Loader.load();
        return PxForceModeEnum._geteVELOCITY_CHANGE();
    }

    private static native int _geteACCELERATION();

    private static int geteACCELERATION() {
        Loader.load();
        return PxForceModeEnum._geteACCELERATION();
    }

    public static PxForceModeEnum forValue(int value) {
        for (int i = 0; i < PxForceModeEnum.values().length; ++i) {
            if (PxForceModeEnum.values()[i].value != value) continue;
            return PxForceModeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxForceModeEnum: " + value);
    }
}

