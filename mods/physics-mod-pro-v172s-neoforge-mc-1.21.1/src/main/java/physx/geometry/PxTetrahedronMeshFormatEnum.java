/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import de.fabmax.physxjni.Loader;

public enum PxTetrahedronMeshFormatEnum {
    eTET_MESH(PxTetrahedronMeshFormatEnum.geteTET_MESH()),
    eHEX_MESH(PxTetrahedronMeshFormatEnum.geteHEX_MESH());

    public final int value;

    private PxTetrahedronMeshFormatEnum(int value) {
        this.value = value;
    }

    private static native int _geteTET_MESH();

    private static int geteTET_MESH() {
        Loader.load();
        return PxTetrahedronMeshFormatEnum._geteTET_MESH();
    }

    private static native int _geteHEX_MESH();

    private static int geteHEX_MESH() {
        Loader.load();
        return PxTetrahedronMeshFormatEnum._geteHEX_MESH();
    }

    public static PxTetrahedronMeshFormatEnum forValue(int value) {
        for (int i = 0; i < PxTetrahedronMeshFormatEnum.values().length; ++i) {
            if (PxTetrahedronMeshFormatEnum.values()[i].value != value) continue;
            return PxTetrahedronMeshFormatEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxTetrahedronMeshFormatEnum: " + value);
    }
}

