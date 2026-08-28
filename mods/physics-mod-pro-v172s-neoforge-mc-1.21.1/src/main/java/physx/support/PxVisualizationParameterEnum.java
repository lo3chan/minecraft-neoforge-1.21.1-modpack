/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import de.fabmax.physxjni.Loader;

public enum PxVisualizationParameterEnum {
    eSCALE(PxVisualizationParameterEnum.geteSCALE()),
    eWORLD_AXES(PxVisualizationParameterEnum.geteWORLD_AXES()),
    eBODY_AXES(PxVisualizationParameterEnum.geteBODY_AXES()),
    eBODY_MASS_AXES(PxVisualizationParameterEnum.geteBODY_MASS_AXES()),
    eBODY_LIN_VELOCITY(PxVisualizationParameterEnum.geteBODY_LIN_VELOCITY()),
    eBODY_ANG_VELOCITY(PxVisualizationParameterEnum.geteBODY_ANG_VELOCITY()),
    eCONTACT_POINT(PxVisualizationParameterEnum.geteCONTACT_POINT()),
    eCONTACT_NORMAL(PxVisualizationParameterEnum.geteCONTACT_NORMAL()),
    eCONTACT_ERROR(PxVisualizationParameterEnum.geteCONTACT_ERROR()),
    eCONTACT_FORCE(PxVisualizationParameterEnum.geteCONTACT_FORCE()),
    eACTOR_AXES(PxVisualizationParameterEnum.geteACTOR_AXES()),
    eCOLLISION_AABBS(PxVisualizationParameterEnum.geteCOLLISION_AABBS()),
    eCOLLISION_SHAPES(PxVisualizationParameterEnum.geteCOLLISION_SHAPES()),
    eCOLLISION_AXES(PxVisualizationParameterEnum.geteCOLLISION_AXES()),
    eCOLLISION_COMPOUNDS(PxVisualizationParameterEnum.geteCOLLISION_COMPOUNDS()),
    eCOLLISION_FNORMALS(PxVisualizationParameterEnum.geteCOLLISION_FNORMALS()),
    eCOLLISION_EDGES(PxVisualizationParameterEnum.geteCOLLISION_EDGES()),
    eCOLLISION_STATIC(PxVisualizationParameterEnum.geteCOLLISION_STATIC()),
    eCOLLISION_DYNAMIC(PxVisualizationParameterEnum.geteCOLLISION_DYNAMIC()),
    eJOINT_LOCAL_FRAMES(PxVisualizationParameterEnum.geteJOINT_LOCAL_FRAMES()),
    eJOINT_LIMITS(PxVisualizationParameterEnum.geteJOINT_LIMITS()),
    eCULL_BOX(PxVisualizationParameterEnum.geteCULL_BOX()),
    eMBP_REGIONS(PxVisualizationParameterEnum.geteMBP_REGIONS()),
    eSIMULATION_MESH(PxVisualizationParameterEnum.geteSIMULATION_MESH()),
    eSDF(PxVisualizationParameterEnum.geteSDF()),
    eNUM_VALUES(PxVisualizationParameterEnum.geteNUM_VALUES()),
    eFORCE_DWORD(PxVisualizationParameterEnum.geteFORCE_DWORD());

    public final int value;

    private PxVisualizationParameterEnum(int value) {
        this.value = value;
    }

    private static native int _geteSCALE();

    private static int geteSCALE() {
        Loader.load();
        return PxVisualizationParameterEnum._geteSCALE();
    }

    private static native int _geteWORLD_AXES();

    private static int geteWORLD_AXES() {
        Loader.load();
        return PxVisualizationParameterEnum._geteWORLD_AXES();
    }

    private static native int _geteBODY_AXES();

    private static int geteBODY_AXES() {
        Loader.load();
        return PxVisualizationParameterEnum._geteBODY_AXES();
    }

    private static native int _geteBODY_MASS_AXES();

    private static int geteBODY_MASS_AXES() {
        Loader.load();
        return PxVisualizationParameterEnum._geteBODY_MASS_AXES();
    }

    private static native int _geteBODY_LIN_VELOCITY();

    private static int geteBODY_LIN_VELOCITY() {
        Loader.load();
        return PxVisualizationParameterEnum._geteBODY_LIN_VELOCITY();
    }

    private static native int _geteBODY_ANG_VELOCITY();

    private static int geteBODY_ANG_VELOCITY() {
        Loader.load();
        return PxVisualizationParameterEnum._geteBODY_ANG_VELOCITY();
    }

    private static native int _geteCONTACT_POINT();

    private static int geteCONTACT_POINT() {
        Loader.load();
        return PxVisualizationParameterEnum._geteCONTACT_POINT();
    }

    private static native int _geteCONTACT_NORMAL();

    private static int geteCONTACT_NORMAL() {
        Loader.load();
        return PxVisualizationParameterEnum._geteCONTACT_NORMAL();
    }

    private static native int _geteCONTACT_ERROR();

    private static int geteCONTACT_ERROR() {
        Loader.load();
        return PxVisualizationParameterEnum._geteCONTACT_ERROR();
    }

    private static native int _geteCONTACT_FORCE();

    private static int geteCONTACT_FORCE() {
        Loader.load();
        return PxVisualizationParameterEnum._geteCONTACT_FORCE();
    }

    private static native int _geteACTOR_AXES();

    private static int geteACTOR_AXES() {
        Loader.load();
        return PxVisualizationParameterEnum._geteACTOR_AXES();
    }

    private static native int _geteCOLLISION_AABBS();

    private static int geteCOLLISION_AABBS() {
        Loader.load();
        return PxVisualizationParameterEnum._geteCOLLISION_AABBS();
    }

    private static native int _geteCOLLISION_SHAPES();

    private static int geteCOLLISION_SHAPES() {
        Loader.load();
        return PxVisualizationParameterEnum._geteCOLLISION_SHAPES();
    }

    private static native int _geteCOLLISION_AXES();

    private static int geteCOLLISION_AXES() {
        Loader.load();
        return PxVisualizationParameterEnum._geteCOLLISION_AXES();
    }

    private static native int _geteCOLLISION_COMPOUNDS();

    private static int geteCOLLISION_COMPOUNDS() {
        Loader.load();
        return PxVisualizationParameterEnum._geteCOLLISION_COMPOUNDS();
    }

    private static native int _geteCOLLISION_FNORMALS();

    private static int geteCOLLISION_FNORMALS() {
        Loader.load();
        return PxVisualizationParameterEnum._geteCOLLISION_FNORMALS();
    }

    private static native int _geteCOLLISION_EDGES();

    private static int geteCOLLISION_EDGES() {
        Loader.load();
        return PxVisualizationParameterEnum._geteCOLLISION_EDGES();
    }

    private static native int _geteCOLLISION_STATIC();

    private static int geteCOLLISION_STATIC() {
        Loader.load();
        return PxVisualizationParameterEnum._geteCOLLISION_STATIC();
    }

    private static native int _geteCOLLISION_DYNAMIC();

    private static int geteCOLLISION_DYNAMIC() {
        Loader.load();
        return PxVisualizationParameterEnum._geteCOLLISION_DYNAMIC();
    }

    private static native int _geteJOINT_LOCAL_FRAMES();

    private static int geteJOINT_LOCAL_FRAMES() {
        Loader.load();
        return PxVisualizationParameterEnum._geteJOINT_LOCAL_FRAMES();
    }

    private static native int _geteJOINT_LIMITS();

    private static int geteJOINT_LIMITS() {
        Loader.load();
        return PxVisualizationParameterEnum._geteJOINT_LIMITS();
    }

    private static native int _geteCULL_BOX();

    private static int geteCULL_BOX() {
        Loader.load();
        return PxVisualizationParameterEnum._geteCULL_BOX();
    }

    private static native int _geteMBP_REGIONS();

    private static int geteMBP_REGIONS() {
        Loader.load();
        return PxVisualizationParameterEnum._geteMBP_REGIONS();
    }

    private static native int _geteSIMULATION_MESH();

    private static int geteSIMULATION_MESH() {
        Loader.load();
        return PxVisualizationParameterEnum._geteSIMULATION_MESH();
    }

    private static native int _geteSDF();

    private static int geteSDF() {
        Loader.load();
        return PxVisualizationParameterEnum._geteSDF();
    }

    private static native int _geteNUM_VALUES();

    private static int geteNUM_VALUES() {
        Loader.load();
        return PxVisualizationParameterEnum._geteNUM_VALUES();
    }

    private static native int _geteFORCE_DWORD();

    private static int geteFORCE_DWORD() {
        Loader.load();
        return PxVisualizationParameterEnum._geteFORCE_DWORD();
    }

    public static PxVisualizationParameterEnum forValue(int value) {
        for (int i = 0; i < PxVisualizationParameterEnum.values().length; ++i) {
            if (PxVisualizationParameterEnum.values()[i].value != value) continue;
            return PxVisualizationParameterEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxVisualizationParameterEnum: " + value);
    }
}

