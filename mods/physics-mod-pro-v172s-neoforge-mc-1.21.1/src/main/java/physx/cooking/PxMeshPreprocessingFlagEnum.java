/*
 * Decompiled with CFR 0.152.
 */
package physx.cooking;

import de.fabmax.physxjni.Loader;

public enum PxMeshPreprocessingFlagEnum {
    eWELD_VERTICES(PxMeshPreprocessingFlagEnum.geteWELD_VERTICES()),
    eDISABLE_CLEAN_MESH(PxMeshPreprocessingFlagEnum.geteDISABLE_CLEAN_MESH()),
    eDISABLE_ACTIVE_EDGES_PRECOMPUTE(PxMeshPreprocessingFlagEnum.geteDISABLE_ACTIVE_EDGES_PRECOMPUTE()),
    eFORCE_32BIT_INDICES(PxMeshPreprocessingFlagEnum.geteFORCE_32BIT_INDICES());

    public final int value;

    private PxMeshPreprocessingFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteWELD_VERTICES();

    private static int geteWELD_VERTICES() {
        Loader.load();
        return PxMeshPreprocessingFlagEnum._geteWELD_VERTICES();
    }

    private static native int _geteDISABLE_CLEAN_MESH();

    private static int geteDISABLE_CLEAN_MESH() {
        Loader.load();
        return PxMeshPreprocessingFlagEnum._geteDISABLE_CLEAN_MESH();
    }

    private static native int _geteDISABLE_ACTIVE_EDGES_PRECOMPUTE();

    private static int geteDISABLE_ACTIVE_EDGES_PRECOMPUTE() {
        Loader.load();
        return PxMeshPreprocessingFlagEnum._geteDISABLE_ACTIVE_EDGES_PRECOMPUTE();
    }

    private static native int _geteFORCE_32BIT_INDICES();

    private static int geteFORCE_32BIT_INDICES() {
        Loader.load();
        return PxMeshPreprocessingFlagEnum._geteFORCE_32BIT_INDICES();
    }

    public static PxMeshPreprocessingFlagEnum forValue(int value) {
        for (int i = 0; i < PxMeshPreprocessingFlagEnum.values().length; ++i) {
            if (PxMeshPreprocessingFlagEnum.values()[i].value != value) continue;
            return PxMeshPreprocessingFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxMeshPreprocessingFlagEnum: " + value);
    }
}

