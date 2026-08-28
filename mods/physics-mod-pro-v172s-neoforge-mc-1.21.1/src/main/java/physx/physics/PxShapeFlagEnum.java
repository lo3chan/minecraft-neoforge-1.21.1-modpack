/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxShapeFlagEnum {
    eSIMULATION_SHAPE(PxShapeFlagEnum.geteSIMULATION_SHAPE()),
    eSCENE_QUERY_SHAPE(PxShapeFlagEnum.geteSCENE_QUERY_SHAPE()),
    eTRIGGER_SHAPE(PxShapeFlagEnum.geteTRIGGER_SHAPE()),
    eVISUALIZATION(PxShapeFlagEnum.geteVISUALIZATION());

    public final int value;

    private PxShapeFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteSIMULATION_SHAPE();

    private static int geteSIMULATION_SHAPE() {
        Loader.load();
        return PxShapeFlagEnum._geteSIMULATION_SHAPE();
    }

    private static native int _geteSCENE_QUERY_SHAPE();

    private static int geteSCENE_QUERY_SHAPE() {
        Loader.load();
        return PxShapeFlagEnum._geteSCENE_QUERY_SHAPE();
    }

    private static native int _geteTRIGGER_SHAPE();

    private static int geteTRIGGER_SHAPE() {
        Loader.load();
        return PxShapeFlagEnum._geteTRIGGER_SHAPE();
    }

    private static native int _geteVISUALIZATION();

    private static int geteVISUALIZATION() {
        Loader.load();
        return PxShapeFlagEnum._geteVISUALIZATION();
    }

    public static PxShapeFlagEnum forValue(int value) {
        for (int i = 0; i < PxShapeFlagEnum.values().length; ++i) {
            if (PxShapeFlagEnum.values()[i].value != value) continue;
            return PxShapeFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxShapeFlagEnum: " + value);
    }
}

