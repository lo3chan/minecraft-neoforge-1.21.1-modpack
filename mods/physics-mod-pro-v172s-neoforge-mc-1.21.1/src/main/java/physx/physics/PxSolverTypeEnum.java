/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxSolverTypeEnum {
    ePGS(PxSolverTypeEnum.getePGS()),
    eTGS(PxSolverTypeEnum.geteTGS());

    public final int value;

    private PxSolverTypeEnum(int value) {
        this.value = value;
    }

    private static native int _getePGS();

    private static int getePGS() {
        Loader.load();
        return PxSolverTypeEnum._getePGS();
    }

    private static native int _geteTGS();

    private static int geteTGS() {
        Loader.load();
        return PxSolverTypeEnum._geteTGS();
    }

    public static PxSolverTypeEnum forValue(int value) {
        for (int i = 0; i < PxSolverTypeEnum.values().length; ++i) {
            if (PxSolverTypeEnum.values()[i].value != value) continue;
            return PxSolverTypeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxSolverTypeEnum: " + value);
    }
}

