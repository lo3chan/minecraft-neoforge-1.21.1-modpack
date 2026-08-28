/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxFrictionTypeEnum {
    ePATCH(PxFrictionTypeEnum.getePATCH()),
    eONE_DIRECTIONAL(PxFrictionTypeEnum.geteONE_DIRECTIONAL()),
    eTWO_DIRECTIONAL(PxFrictionTypeEnum.geteTWO_DIRECTIONAL()),
    eFRICTION_COUNT(PxFrictionTypeEnum.geteFRICTION_COUNT());

    public final int value;

    private PxFrictionTypeEnum(int value) {
        this.value = value;
    }

    private static native int _getePATCH();

    private static int getePATCH() {
        Loader.load();
        return PxFrictionTypeEnum._getePATCH();
    }

    private static native int _geteONE_DIRECTIONAL();

    private static int geteONE_DIRECTIONAL() {
        Loader.load();
        return PxFrictionTypeEnum._geteONE_DIRECTIONAL();
    }

    private static native int _geteTWO_DIRECTIONAL();

    private static int geteTWO_DIRECTIONAL() {
        Loader.load();
        return PxFrictionTypeEnum._geteTWO_DIRECTIONAL();
    }

    private static native int _geteFRICTION_COUNT();

    private static int geteFRICTION_COUNT() {
        Loader.load();
        return PxFrictionTypeEnum._geteFRICTION_COUNT();
    }

    public static PxFrictionTypeEnum forValue(int value) {
        for (int i = 0; i < PxFrictionTypeEnum.values().length; ++i) {
            if (PxFrictionTypeEnum.values()[i].value != value) continue;
            return PxFrictionTypeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxFrictionTypeEnum: " + value);
    }
}

