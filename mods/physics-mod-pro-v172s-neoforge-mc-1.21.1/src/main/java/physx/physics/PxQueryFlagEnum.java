/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxQueryFlagEnum {
    eSTATIC(PxQueryFlagEnum.geteSTATIC()),
    eDYNAMIC(PxQueryFlagEnum.geteDYNAMIC()),
    ePREFILTER(PxQueryFlagEnum.getePREFILTER()),
    ePOSTFILTER(PxQueryFlagEnum.getePOSTFILTER()),
    eANY_HIT(PxQueryFlagEnum.geteANY_HIT()),
    eNO_BLOCK(PxQueryFlagEnum.geteNO_BLOCK());

    public final int value;

    private PxQueryFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteSTATIC();

    private static int geteSTATIC() {
        Loader.load();
        return PxQueryFlagEnum._geteSTATIC();
    }

    private static native int _geteDYNAMIC();

    private static int geteDYNAMIC() {
        Loader.load();
        return PxQueryFlagEnum._geteDYNAMIC();
    }

    private static native int _getePREFILTER();

    private static int getePREFILTER() {
        Loader.load();
        return PxQueryFlagEnum._getePREFILTER();
    }

    private static native int _getePOSTFILTER();

    private static int getePOSTFILTER() {
        Loader.load();
        return PxQueryFlagEnum._getePOSTFILTER();
    }

    private static native int _geteANY_HIT();

    private static int geteANY_HIT() {
        Loader.load();
        return PxQueryFlagEnum._geteANY_HIT();
    }

    private static native int _geteNO_BLOCK();

    private static int geteNO_BLOCK() {
        Loader.load();
        return PxQueryFlagEnum._geteNO_BLOCK();
    }

    public static PxQueryFlagEnum forValue(int value) {
        for (int i = 0; i < PxQueryFlagEnum.values().length; ++i) {
            if (PxQueryFlagEnum.values()[i].value != value) continue;
            return PxQueryFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxQueryFlagEnum: " + value);
    }
}

