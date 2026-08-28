/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import de.fabmax.physxjni.Loader;

public enum PxGeometryTypeEnum {
    eSPHERE(PxGeometryTypeEnum.geteSPHERE()),
    ePLANE(PxGeometryTypeEnum.getePLANE()),
    eCAPSULE(PxGeometryTypeEnum.geteCAPSULE()),
    eBOX(PxGeometryTypeEnum.geteBOX()),
    eCONVEXMESH(PxGeometryTypeEnum.geteCONVEXMESH()),
    eTRIANGLEMESH(PxGeometryTypeEnum.geteTRIANGLEMESH()),
    eHEIGHTFIELD(PxGeometryTypeEnum.geteHEIGHTFIELD()),
    eCUSTOM(PxGeometryTypeEnum.geteCUSTOM());

    public final int value;

    private PxGeometryTypeEnum(int value) {
        this.value = value;
    }

    private static native int _geteSPHERE();

    private static int geteSPHERE() {
        Loader.load();
        return PxGeometryTypeEnum._geteSPHERE();
    }

    private static native int _getePLANE();

    private static int getePLANE() {
        Loader.load();
        return PxGeometryTypeEnum._getePLANE();
    }

    private static native int _geteCAPSULE();

    private static int geteCAPSULE() {
        Loader.load();
        return PxGeometryTypeEnum._geteCAPSULE();
    }

    private static native int _geteBOX();

    private static int geteBOX() {
        Loader.load();
        return PxGeometryTypeEnum._geteBOX();
    }

    private static native int _geteCONVEXMESH();

    private static int geteCONVEXMESH() {
        Loader.load();
        return PxGeometryTypeEnum._geteCONVEXMESH();
    }

    private static native int _geteTRIANGLEMESH();

    private static int geteTRIANGLEMESH() {
        Loader.load();
        return PxGeometryTypeEnum._geteTRIANGLEMESH();
    }

    private static native int _geteHEIGHTFIELD();

    private static int geteHEIGHTFIELD() {
        Loader.load();
        return PxGeometryTypeEnum._geteHEIGHTFIELD();
    }

    private static native int _geteCUSTOM();

    private static int geteCUSTOM() {
        Loader.load();
        return PxGeometryTypeEnum._geteCUSTOM();
    }

    public static PxGeometryTypeEnum forValue(int value) {
        for (int i = 0; i < PxGeometryTypeEnum.values().length; ++i) {
            if (PxGeometryTypeEnum.values()[i].value != value) continue;
            return PxGeometryTypeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxGeometryTypeEnum: " + value);
    }
}

