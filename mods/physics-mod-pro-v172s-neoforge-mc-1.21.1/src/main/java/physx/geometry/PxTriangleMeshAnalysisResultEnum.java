/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import de.fabmax.physxjni.Loader;

public enum PxTriangleMeshAnalysisResultEnum {
    eVALID(PxTriangleMeshAnalysisResultEnum.geteVALID()),
    eZERO_VOLUME(PxTriangleMeshAnalysisResultEnum.geteZERO_VOLUME()),
    eOPEN_BOUNDARIES(PxTriangleMeshAnalysisResultEnum.geteOPEN_BOUNDARIES()),
    eSELF_INTERSECTIONS(PxTriangleMeshAnalysisResultEnum.geteSELF_INTERSECTIONS()),
    eINCONSISTENT_TRIANGLE_ORIENTATION(PxTriangleMeshAnalysisResultEnum.geteINCONSISTENT_TRIANGLE_ORIENTATION()),
    eCONTAINS_ACUTE_ANGLED_TRIANGLES(PxTriangleMeshAnalysisResultEnum.geteCONTAINS_ACUTE_ANGLED_TRIANGLES()),
    eEDGE_SHARED_BY_MORE_THAN_TWO_TRIANGLES(PxTriangleMeshAnalysisResultEnum.geteEDGE_SHARED_BY_MORE_THAN_TWO_TRIANGLES()),
    eCONTAINS_DUPLICATE_POINTS(PxTriangleMeshAnalysisResultEnum.geteCONTAINS_DUPLICATE_POINTS()),
    eCONTAINS_INVALID_POINTS(PxTriangleMeshAnalysisResultEnum.geteCONTAINS_INVALID_POINTS()),
    eREQUIRES_32BIT_INDEX_BUFFER(PxTriangleMeshAnalysisResultEnum.geteREQUIRES_32BIT_INDEX_BUFFER()),
    eTRIANGLE_INDEX_OUT_OF_RANGE(PxTriangleMeshAnalysisResultEnum.geteTRIANGLE_INDEX_OUT_OF_RANGE()),
    eMESH_IS_PROBLEMATIC(PxTriangleMeshAnalysisResultEnum.geteMESH_IS_PROBLEMATIC()),
    eMESH_IS_INVALID(PxTriangleMeshAnalysisResultEnum.geteMESH_IS_INVALID());

    public final int value;

    private PxTriangleMeshAnalysisResultEnum(int value) {
        this.value = value;
    }

    private static native int _geteVALID();

    private static int geteVALID() {
        Loader.load();
        return PxTriangleMeshAnalysisResultEnum._geteVALID();
    }

    private static native int _geteZERO_VOLUME();

    private static int geteZERO_VOLUME() {
        Loader.load();
        return PxTriangleMeshAnalysisResultEnum._geteZERO_VOLUME();
    }

    private static native int _geteOPEN_BOUNDARIES();

    private static int geteOPEN_BOUNDARIES() {
        Loader.load();
        return PxTriangleMeshAnalysisResultEnum._geteOPEN_BOUNDARIES();
    }

    private static native int _geteSELF_INTERSECTIONS();

    private static int geteSELF_INTERSECTIONS() {
        Loader.load();
        return PxTriangleMeshAnalysisResultEnum._geteSELF_INTERSECTIONS();
    }

    private static native int _geteINCONSISTENT_TRIANGLE_ORIENTATION();

    private static int geteINCONSISTENT_TRIANGLE_ORIENTATION() {
        Loader.load();
        return PxTriangleMeshAnalysisResultEnum._geteINCONSISTENT_TRIANGLE_ORIENTATION();
    }

    private static native int _geteCONTAINS_ACUTE_ANGLED_TRIANGLES();

    private static int geteCONTAINS_ACUTE_ANGLED_TRIANGLES() {
        Loader.load();
        return PxTriangleMeshAnalysisResultEnum._geteCONTAINS_ACUTE_ANGLED_TRIANGLES();
    }

    private static native int _geteEDGE_SHARED_BY_MORE_THAN_TWO_TRIANGLES();

    private static int geteEDGE_SHARED_BY_MORE_THAN_TWO_TRIANGLES() {
        Loader.load();
        return PxTriangleMeshAnalysisResultEnum._geteEDGE_SHARED_BY_MORE_THAN_TWO_TRIANGLES();
    }

    private static native int _geteCONTAINS_DUPLICATE_POINTS();

    private static int geteCONTAINS_DUPLICATE_POINTS() {
        Loader.load();
        return PxTriangleMeshAnalysisResultEnum._geteCONTAINS_DUPLICATE_POINTS();
    }

    private static native int _geteCONTAINS_INVALID_POINTS();

    private static int geteCONTAINS_INVALID_POINTS() {
        Loader.load();
        return PxTriangleMeshAnalysisResultEnum._geteCONTAINS_INVALID_POINTS();
    }

    private static native int _geteREQUIRES_32BIT_INDEX_BUFFER();

    private static int geteREQUIRES_32BIT_INDEX_BUFFER() {
        Loader.load();
        return PxTriangleMeshAnalysisResultEnum._geteREQUIRES_32BIT_INDEX_BUFFER();
    }

    private static native int _geteTRIANGLE_INDEX_OUT_OF_RANGE();

    private static int geteTRIANGLE_INDEX_OUT_OF_RANGE() {
        Loader.load();
        return PxTriangleMeshAnalysisResultEnum._geteTRIANGLE_INDEX_OUT_OF_RANGE();
    }

    private static native int _geteMESH_IS_PROBLEMATIC();

    private static int geteMESH_IS_PROBLEMATIC() {
        Loader.load();
        return PxTriangleMeshAnalysisResultEnum._geteMESH_IS_PROBLEMATIC();
    }

    private static native int _geteMESH_IS_INVALID();

    private static int geteMESH_IS_INVALID() {
        Loader.load();
        return PxTriangleMeshAnalysisResultEnum._geteMESH_IS_INVALID();
    }

    public static PxTriangleMeshAnalysisResultEnum forValue(int value) {
        for (int i = 0; i < PxTriangleMeshAnalysisResultEnum.values().length; ++i) {
            if (PxTriangleMeshAnalysisResultEnum.values()[i].value != value) continue;
            return PxTriangleMeshAnalysisResultEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxTriangleMeshAnalysisResultEnum: " + value);
    }
}

