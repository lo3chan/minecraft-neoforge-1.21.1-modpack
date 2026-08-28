/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import de.fabmax.physxjni.Loader;

public enum PxErrorCodeEnum {
    eNO_ERROR(PxErrorCodeEnum.geteNO_ERROR()),
    eDEBUG_INFO(PxErrorCodeEnum.geteDEBUG_INFO()),
    eDEBUG_WARNING(PxErrorCodeEnum.geteDEBUG_WARNING()),
    eINVALID_PARAMETER(PxErrorCodeEnum.geteINVALID_PARAMETER()),
    eINVALID_OPERATION(PxErrorCodeEnum.geteINVALID_OPERATION()),
    eOUT_OF_MEMORY(PxErrorCodeEnum.geteOUT_OF_MEMORY()),
    eINTERNAL_ERROR(PxErrorCodeEnum.geteINTERNAL_ERROR()),
    eABORT(PxErrorCodeEnum.geteABORT()),
    ePERF_WARNING(PxErrorCodeEnum.getePERF_WARNING()),
    eMASK_ALL(PxErrorCodeEnum.geteMASK_ALL());

    public final int value;

    private PxErrorCodeEnum(int value) {
        this.value = value;
    }

    private static native int _geteNO_ERROR();

    private static int geteNO_ERROR() {
        Loader.load();
        return PxErrorCodeEnum._geteNO_ERROR();
    }

    private static native int _geteDEBUG_INFO();

    private static int geteDEBUG_INFO() {
        Loader.load();
        return PxErrorCodeEnum._geteDEBUG_INFO();
    }

    private static native int _geteDEBUG_WARNING();

    private static int geteDEBUG_WARNING() {
        Loader.load();
        return PxErrorCodeEnum._geteDEBUG_WARNING();
    }

    private static native int _geteINVALID_PARAMETER();

    private static int geteINVALID_PARAMETER() {
        Loader.load();
        return PxErrorCodeEnum._geteINVALID_PARAMETER();
    }

    private static native int _geteINVALID_OPERATION();

    private static int geteINVALID_OPERATION() {
        Loader.load();
        return PxErrorCodeEnum._geteINVALID_OPERATION();
    }

    private static native int _geteOUT_OF_MEMORY();

    private static int geteOUT_OF_MEMORY() {
        Loader.load();
        return PxErrorCodeEnum._geteOUT_OF_MEMORY();
    }

    private static native int _geteINTERNAL_ERROR();

    private static int geteINTERNAL_ERROR() {
        Loader.load();
        return PxErrorCodeEnum._geteINTERNAL_ERROR();
    }

    private static native int _geteABORT();

    private static int geteABORT() {
        Loader.load();
        return PxErrorCodeEnum._geteABORT();
    }

    private static native int _getePERF_WARNING();

    private static int getePERF_WARNING() {
        Loader.load();
        return PxErrorCodeEnum._getePERF_WARNING();
    }

    private static native int _geteMASK_ALL();

    private static int geteMASK_ALL() {
        Loader.load();
        return PxErrorCodeEnum._geteMASK_ALL();
    }

    public static PxErrorCodeEnum forValue(int value) {
        for (int i = 0; i < PxErrorCodeEnum.values().length; ++i) {
            if (PxErrorCodeEnum.values()[i].value != value) continue;
            return PxErrorCodeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxErrorCodeEnum: " + value);
    }
}

