/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import de.fabmax.physxjni.Loader;

public enum PxD6AxisEnum {
    eX(PxD6AxisEnum.geteX()),
    eY(PxD6AxisEnum.geteY()),
    eZ(PxD6AxisEnum.geteZ()),
    eTWIST(PxD6AxisEnum.geteTWIST()),
    eSWING1(PxD6AxisEnum.geteSWING1()),
    eSWING2(PxD6AxisEnum.geteSWING2());

    public final int value;

    private PxD6AxisEnum(int value) {
        this.value = value;
    }

    private static native int _geteX();

    private static int geteX() {
        Loader.load();
        return PxD6AxisEnum._geteX();
    }

    private static native int _geteY();

    private static int geteY() {
        Loader.load();
        return PxD6AxisEnum._geteY();
    }

    private static native int _geteZ();

    private static int geteZ() {
        Loader.load();
        return PxD6AxisEnum._geteZ();
    }

    private static native int _geteTWIST();

    private static int geteTWIST() {
        Loader.load();
        return PxD6AxisEnum._geteTWIST();
    }

    private static native int _geteSWING1();

    private static int geteSWING1() {
        Loader.load();
        return PxD6AxisEnum._geteSWING1();
    }

    private static native int _geteSWING2();

    private static int geteSWING2() {
        Loader.load();
        return PxD6AxisEnum._geteSWING2();
    }

    public static PxD6AxisEnum forValue(int value) {
        for (int i = 0; i < PxD6AxisEnum.values().length; ++i) {
            if (PxD6AxisEnum.values()[i].value != value) continue;
            return PxD6AxisEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxD6AxisEnum: " + value);
    }
}

