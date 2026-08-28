/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxBVHBuildStrategyEnum {
    eFAST(PxBVHBuildStrategyEnum.geteFAST()),
    eDEFAULT(PxBVHBuildStrategyEnum.geteDEFAULT()),
    eSAH(PxBVHBuildStrategyEnum.geteSAH());

    public final int value;

    private PxBVHBuildStrategyEnum(int value) {
        this.value = value;
    }

    private static native int _geteFAST();

    private static int geteFAST() {
        Loader.load();
        return PxBVHBuildStrategyEnum._geteFAST();
    }

    private static native int _geteDEFAULT();

    private static int geteDEFAULT() {
        Loader.load();
        return PxBVHBuildStrategyEnum._geteDEFAULT();
    }

    private static native int _geteSAH();

    private static int geteSAH() {
        Loader.load();
        return PxBVHBuildStrategyEnum._geteSAH();
    }

    public static PxBVHBuildStrategyEnum forValue(int value) {
        for (int i = 0; i < PxBVHBuildStrategyEnum.values().length; ++i) {
            if (PxBVHBuildStrategyEnum.values()[i].value != value) continue;
            return PxBVHBuildStrategyEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxBVHBuildStrategyEnum: " + value);
    }
}

