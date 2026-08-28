/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxPairFilteringModeEnum {
    eKEEP(PxPairFilteringModeEnum.geteKEEP()),
    eSUPPRESS(PxPairFilteringModeEnum.geteSUPPRESS()),
    eKILL(PxPairFilteringModeEnum.geteKILL()),
    eDEFAULT(PxPairFilteringModeEnum.geteDEFAULT());

    public final int value;

    private PxPairFilteringModeEnum(int value) {
        this.value = value;
    }

    private static native int _geteKEEP();

    private static int geteKEEP() {
        Loader.load();
        return PxPairFilteringModeEnum._geteKEEP();
    }

    private static native int _geteSUPPRESS();

    private static int geteSUPPRESS() {
        Loader.load();
        return PxPairFilteringModeEnum._geteSUPPRESS();
    }

    private static native int _geteKILL();

    private static int geteKILL() {
        Loader.load();
        return PxPairFilteringModeEnum._geteKILL();
    }

    private static native int _geteDEFAULT();

    private static int geteDEFAULT() {
        Loader.load();
        return PxPairFilteringModeEnum._geteDEFAULT();
    }

    public static PxPairFilteringModeEnum forValue(int value) {
        for (int i = 0; i < PxPairFilteringModeEnum.values().length; ++i) {
            if (PxPairFilteringModeEnum.values()[i].value != value) continue;
            return PxPairFilteringModeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxPairFilteringModeEnum: " + value);
    }
}

