/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import de.fabmax.physxjni.Loader;

public enum PxMeshFlagEnum {
    eFLIPNORMALS(PxMeshFlagEnum.geteFLIPNORMALS()),
    e16_BIT_INDICES(PxMeshFlagEnum.gete16_BIT_INDICES());

    public final int value;

    private PxMeshFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteFLIPNORMALS();

    private static int geteFLIPNORMALS() {
        Loader.load();
        return PxMeshFlagEnum._geteFLIPNORMALS();
    }

    private static native int _gete16_BIT_INDICES();

    private static int gete16_BIT_INDICES() {
        Loader.load();
        return PxMeshFlagEnum._gete16_BIT_INDICES();
    }

    public static PxMeshFlagEnum forValue(int value) {
        for (int i = 0; i < PxMeshFlagEnum.values().length; ++i) {
            if (PxMeshFlagEnum.values()[i].value != value) continue;
            return PxMeshFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxMeshFlagEnum: " + value);
    }
}

