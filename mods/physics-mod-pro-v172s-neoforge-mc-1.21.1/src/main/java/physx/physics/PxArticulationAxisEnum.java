/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxArticulationAxisEnum {
    eTWIST(PxArticulationAxisEnum.geteTWIST()),
    eSWING1(PxArticulationAxisEnum.geteSWING1()),
    eSWING2(PxArticulationAxisEnum.geteSWING2()),
    eX(PxArticulationAxisEnum.geteX()),
    eY(PxArticulationAxisEnum.geteY()),
    eZ(PxArticulationAxisEnum.geteZ());

    public final int value;

    private PxArticulationAxisEnum(int value) {
        this.value = value;
    }

    private static native int _geteTWIST();

    private static int geteTWIST() {
        Loader.load();
        return PxArticulationAxisEnum._geteTWIST();
    }

    private static native int _geteSWING1();

    private static int geteSWING1() {
        Loader.load();
        return PxArticulationAxisEnum._geteSWING1();
    }

    private static native int _geteSWING2();

    private static int geteSWING2() {
        Loader.load();
        return PxArticulationAxisEnum._geteSWING2();
    }

    private static native int _geteX();

    private static int geteX() {
        Loader.load();
        return PxArticulationAxisEnum._geteX();
    }

    private static native int _geteY();

    private static int geteY() {
        Loader.load();
        return PxArticulationAxisEnum._geteY();
    }

    private static native int _geteZ();

    private static int geteZ() {
        Loader.load();
        return PxArticulationAxisEnum._geteZ();
    }

    public static PxArticulationAxisEnum forValue(int value) {
        for (int i = 0; i < PxArticulationAxisEnum.values().length; ++i) {
            if (PxArticulationAxisEnum.values()[i].value != value) continue;
            return PxArticulationAxisEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxArticulationAxisEnum: " + value);
    }
}

