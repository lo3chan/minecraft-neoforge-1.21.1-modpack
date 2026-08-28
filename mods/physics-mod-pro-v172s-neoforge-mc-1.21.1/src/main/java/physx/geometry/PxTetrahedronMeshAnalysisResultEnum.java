/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import de.fabmax.physxjni.Loader;

public enum PxTetrahedronMeshAnalysisResultEnum {
    eVALID(PxTetrahedronMeshAnalysisResultEnum.geteVALID()),
    eDEGENERATE_TETRAHEDRON(PxTetrahedronMeshAnalysisResultEnum.geteDEGENERATE_TETRAHEDRON()),
    eMESH_IS_PROBLEMATIC(PxTetrahedronMeshAnalysisResultEnum.geteMESH_IS_PROBLEMATIC()),
    eMESH_IS_INVALID(PxTetrahedronMeshAnalysisResultEnum.geteMESH_IS_INVALID());

    public final int value;

    private PxTetrahedronMeshAnalysisResultEnum(int value) {
        this.value = value;
    }

    private static native int _geteVALID();

    private static int geteVALID() {
        Loader.load();
        return PxTetrahedronMeshAnalysisResultEnum._geteVALID();
    }

    private static native int _geteDEGENERATE_TETRAHEDRON();

    private static int geteDEGENERATE_TETRAHEDRON() {
        Loader.load();
        return PxTetrahedronMeshAnalysisResultEnum._geteDEGENERATE_TETRAHEDRON();
    }

    private static native int _geteMESH_IS_PROBLEMATIC();

    private static int geteMESH_IS_PROBLEMATIC() {
        Loader.load();
        return PxTetrahedronMeshAnalysisResultEnum._geteMESH_IS_PROBLEMATIC();
    }

    private static native int _geteMESH_IS_INVALID();

    private static int geteMESH_IS_INVALID() {
        Loader.load();
        return PxTetrahedronMeshAnalysisResultEnum._geteMESH_IS_INVALID();
    }

    public static PxTetrahedronMeshAnalysisResultEnum forValue(int value) {
        for (int i = 0; i < PxTetrahedronMeshAnalysisResultEnum.values().length; ++i) {
            if (PxTetrahedronMeshAnalysisResultEnum.values()[i].value != value) continue;
            return PxTetrahedronMeshAnalysisResultEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxTetrahedronMeshAnalysisResultEnum: " + value);
    }
}

