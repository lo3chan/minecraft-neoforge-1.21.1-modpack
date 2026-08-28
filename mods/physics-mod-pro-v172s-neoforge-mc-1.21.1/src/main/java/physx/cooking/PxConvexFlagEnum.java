/*
 * Decompiled with CFR 0.152.
 */
package physx.cooking;

import de.fabmax.physxjni.Loader;

public enum PxConvexFlagEnum {
    e16_BIT_INDICES(PxConvexFlagEnum.gete16_BIT_INDICES()),
    eCOMPUTE_CONVEX(PxConvexFlagEnum.geteCOMPUTE_CONVEX()),
    eCHECK_ZERO_AREA_TRIANGLES(PxConvexFlagEnum.geteCHECK_ZERO_AREA_TRIANGLES()),
    eQUANTIZE_INPUT(PxConvexFlagEnum.geteQUANTIZE_INPUT()),
    eDISABLE_MESH_VALIDATION(PxConvexFlagEnum.geteDISABLE_MESH_VALIDATION()),
    ePLANE_SHIFTING(PxConvexFlagEnum.getePLANE_SHIFTING()),
    eFAST_INERTIA_COMPUTATION(PxConvexFlagEnum.geteFAST_INERTIA_COMPUTATION()),
    eGPU_COMPATIBLE(PxConvexFlagEnum.geteGPU_COMPATIBLE()),
    eSHIFT_VERTICES(PxConvexFlagEnum.geteSHIFT_VERTICES());

    public final int value;

    private PxConvexFlagEnum(int value) {
        this.value = value;
    }

    private static native int _gete16_BIT_INDICES();

    private static int gete16_BIT_INDICES() {
        Loader.load();
        return PxConvexFlagEnum._gete16_BIT_INDICES();
    }

    private static native int _geteCOMPUTE_CONVEX();

    private static int geteCOMPUTE_CONVEX() {
        Loader.load();
        return PxConvexFlagEnum._geteCOMPUTE_CONVEX();
    }

    private static native int _geteCHECK_ZERO_AREA_TRIANGLES();

    private static int geteCHECK_ZERO_AREA_TRIANGLES() {
        Loader.load();
        return PxConvexFlagEnum._geteCHECK_ZERO_AREA_TRIANGLES();
    }

    private static native int _geteQUANTIZE_INPUT();

    private static int geteQUANTIZE_INPUT() {
        Loader.load();
        return PxConvexFlagEnum._geteQUANTIZE_INPUT();
    }

    private static native int _geteDISABLE_MESH_VALIDATION();

    private static int geteDISABLE_MESH_VALIDATION() {
        Loader.load();
        return PxConvexFlagEnum._geteDISABLE_MESH_VALIDATION();
    }

    private static native int _getePLANE_SHIFTING();

    private static int getePLANE_SHIFTING() {
        Loader.load();
        return PxConvexFlagEnum._getePLANE_SHIFTING();
    }

    private static native int _geteFAST_INERTIA_COMPUTATION();

    private static int geteFAST_INERTIA_COMPUTATION() {
        Loader.load();
        return PxConvexFlagEnum._geteFAST_INERTIA_COMPUTATION();
    }

    private static native int _geteGPU_COMPATIBLE();

    private static int geteGPU_COMPATIBLE() {
        Loader.load();
        return PxConvexFlagEnum._geteGPU_COMPATIBLE();
    }

    private static native int _geteSHIFT_VERTICES();

    private static int geteSHIFT_VERTICES() {
        Loader.load();
        return PxConvexFlagEnum._geteSHIFT_VERTICES();
    }

    public static PxConvexFlagEnum forValue(int value) {
        for (int i = 0; i < PxConvexFlagEnum.values().length; ++i) {
            if (PxConvexFlagEnum.values()[i].value != value) continue;
            return PxConvexFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxConvexFlagEnum: " + value);
    }
}

