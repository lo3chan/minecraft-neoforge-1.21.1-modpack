/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxCombineModeEnum {
    eAVERAGE(PxCombineModeEnum.geteAVERAGE()),
    eMIN(PxCombineModeEnum.geteMIN()),
    eMULTIPLY(PxCombineModeEnum.geteMULTIPLY()),
    eMAX(PxCombineModeEnum.geteMAX());

    public final int value;

    private PxCombineModeEnum(int value) {
        this.value = value;
    }

    private static native int _geteAVERAGE();

    private static int geteAVERAGE() {
        Loader.load();
        return PxCombineModeEnum._geteAVERAGE();
    }

    private static native int _geteMIN();

    private static int geteMIN() {
        Loader.load();
        return PxCombineModeEnum._geteMIN();
    }

    private static native int _geteMULTIPLY();

    private static int geteMULTIPLY() {
        Loader.load();
        return PxCombineModeEnum._geteMULTIPLY();
    }

    private static native int _geteMAX();

    private static int geteMAX() {
        Loader.load();
        return PxCombineModeEnum._geteMAX();
    }

    public static PxCombineModeEnum forValue(int value) {
        for (int i = 0; i < PxCombineModeEnum.values().length; ++i) {
            if (PxCombineModeEnum.values()[i].value != value) continue;
            return PxCombineModeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxCombineModeEnum: " + value);
    }
}

