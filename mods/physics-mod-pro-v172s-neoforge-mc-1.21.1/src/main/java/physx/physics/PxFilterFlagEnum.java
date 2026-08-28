/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxFilterFlagEnum {
    eKILL(PxFilterFlagEnum.geteKILL()),
    eSUPPRESS(PxFilterFlagEnum.geteSUPPRESS()),
    eCALLBACK(PxFilterFlagEnum.geteCALLBACK()),
    eNOTIFY(PxFilterFlagEnum.geteNOTIFY()),
    eDEFAULT(PxFilterFlagEnum.geteDEFAULT());

    public final int value;

    private PxFilterFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteKILL();

    private static int geteKILL() {
        Loader.load();
        return PxFilterFlagEnum._geteKILL();
    }

    private static native int _geteSUPPRESS();

    private static int geteSUPPRESS() {
        Loader.load();
        return PxFilterFlagEnum._geteSUPPRESS();
    }

    private static native int _geteCALLBACK();

    private static int geteCALLBACK() {
        Loader.load();
        return PxFilterFlagEnum._geteCALLBACK();
    }

    private static native int _geteNOTIFY();

    private static int geteNOTIFY() {
        Loader.load();
        return PxFilterFlagEnum._geteNOTIFY();
    }

    private static native int _geteDEFAULT();

    private static int geteDEFAULT() {
        Loader.load();
        return PxFilterFlagEnum._geteDEFAULT();
    }

    public static PxFilterFlagEnum forValue(int value) {
        for (int i = 0; i < PxFilterFlagEnum.values().length; ++i) {
            if (PxFilterFlagEnum.values()[i].value != value) continue;
            return PxFilterFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxFilterFlagEnum: " + value);
    }
}

