/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import de.fabmax.physxjni.Loader;

public enum PxMeshGeometryFlagEnum {
    eDOUBLE_SIDED(PxMeshGeometryFlagEnum.geteDOUBLE_SIDED());

    public final int value;

    private PxMeshGeometryFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteDOUBLE_SIDED();

    private static int geteDOUBLE_SIDED() {
        Loader.load();
        return PxMeshGeometryFlagEnum._geteDOUBLE_SIDED();
    }

    public static PxMeshGeometryFlagEnum forValue(int value) {
        for (int i = 0; i < PxMeshGeometryFlagEnum.values().length; ++i) {
            if (PxMeshGeometryFlagEnum.values()[i].value != value) continue;
            return PxMeshGeometryFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxMeshGeometryFlagEnum: " + value);
    }
}

