/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxMaterialFlagEnum {
    eDISABLE_FRICTION(PxMaterialFlagEnum.geteDISABLE_FRICTION()),
    eDISABLE_STRONG_FRICTION(PxMaterialFlagEnum.geteDISABLE_STRONG_FRICTION()),
    eIMPROVED_PATCH_FRICTION(PxMaterialFlagEnum.geteIMPROVED_PATCH_FRICTION());

    public final int value;

    private PxMaterialFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteDISABLE_FRICTION();

    private static int geteDISABLE_FRICTION() {
        Loader.load();
        return PxMaterialFlagEnum._geteDISABLE_FRICTION();
    }

    private static native int _geteDISABLE_STRONG_FRICTION();

    private static int geteDISABLE_STRONG_FRICTION() {
        Loader.load();
        return PxMaterialFlagEnum._geteDISABLE_STRONG_FRICTION();
    }

    private static native int _geteIMPROVED_PATCH_FRICTION();

    private static int geteIMPROVED_PATCH_FRICTION() {
        Loader.load();
        return PxMaterialFlagEnum._geteIMPROVED_PATCH_FRICTION();
    }

    public static PxMaterialFlagEnum forValue(int value) {
        for (int i = 0; i < PxMaterialFlagEnum.values().length; ++i) {
            if (PxMaterialFlagEnum.values()[i].value != value) continue;
            return PxMaterialFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxMaterialFlagEnum: " + value);
    }
}

