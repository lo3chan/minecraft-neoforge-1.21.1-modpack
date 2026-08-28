/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxDynamicTreeSecondaryPrunerEnum {
    eNONE(PxDynamicTreeSecondaryPrunerEnum.geteNONE()),
    eBUCKET(PxDynamicTreeSecondaryPrunerEnum.geteBUCKET()),
    eINCREMENTAL(PxDynamicTreeSecondaryPrunerEnum.geteINCREMENTAL()),
    eBVH(PxDynamicTreeSecondaryPrunerEnum.geteBVH());

    public final int value;

    private PxDynamicTreeSecondaryPrunerEnum(int value) {
        this.value = value;
    }

    private static native int _geteNONE();

    private static int geteNONE() {
        Loader.load();
        return PxDynamicTreeSecondaryPrunerEnum._geteNONE();
    }

    private static native int _geteBUCKET();

    private static int geteBUCKET() {
        Loader.load();
        return PxDynamicTreeSecondaryPrunerEnum._geteBUCKET();
    }

    private static native int _geteINCREMENTAL();

    private static int geteINCREMENTAL() {
        Loader.load();
        return PxDynamicTreeSecondaryPrunerEnum._geteINCREMENTAL();
    }

    private static native int _geteBVH();

    private static int geteBVH() {
        Loader.load();
        return PxDynamicTreeSecondaryPrunerEnum._geteBVH();
    }

    public static PxDynamicTreeSecondaryPrunerEnum forValue(int value) {
        for (int i = 0; i < PxDynamicTreeSecondaryPrunerEnum.values().length; ++i) {
            if (PxDynamicTreeSecondaryPrunerEnum.values()[i].value != value) continue;
            return PxDynamicTreeSecondaryPrunerEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxDynamicTreeSecondaryPrunerEnum: " + value);
    }
}

