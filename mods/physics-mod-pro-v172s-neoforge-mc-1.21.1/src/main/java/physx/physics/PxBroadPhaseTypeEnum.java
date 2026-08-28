/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxBroadPhaseTypeEnum {
    eSAP(PxBroadPhaseTypeEnum.geteSAP()),
    eMBP(PxBroadPhaseTypeEnum.geteMBP()),
    eABP(PxBroadPhaseTypeEnum.geteABP()),
    ePABP(PxBroadPhaseTypeEnum.getePABP()),
    eGPU(PxBroadPhaseTypeEnum.geteGPU());

    public final int value;

    private PxBroadPhaseTypeEnum(int value) {
        this.value = value;
    }

    private static native int _geteSAP();

    private static int geteSAP() {
        Loader.load();
        return PxBroadPhaseTypeEnum._geteSAP();
    }

    private static native int _geteMBP();

    private static int geteMBP() {
        Loader.load();
        return PxBroadPhaseTypeEnum._geteMBP();
    }

    private static native int _geteABP();

    private static int geteABP() {
        Loader.load();
        return PxBroadPhaseTypeEnum._geteABP();
    }

    private static native int _getePABP();

    private static int getePABP() {
        Loader.load();
        return PxBroadPhaseTypeEnum._getePABP();
    }

    private static native int _geteGPU();

    private static int geteGPU() {
        Loader.load();
        return PxBroadPhaseTypeEnum._geteGPU();
    }

    public static PxBroadPhaseTypeEnum forValue(int value) {
        for (int i = 0; i < PxBroadPhaseTypeEnum.values().length; ++i) {
            if (PxBroadPhaseTypeEnum.values()[i].value != value) continue;
            return PxBroadPhaseTypeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxBroadPhaseTypeEnum: " + value);
    }
}

