/*
 * Decompiled with CFR 0.152.
 */
package physx.character;

import de.fabmax.physxjni.Loader;

public enum PxControllerShapeTypeEnum {
    eBOX(PxControllerShapeTypeEnum.geteBOX()),
    eCAPSULE(PxControllerShapeTypeEnum.geteCAPSULE());

    public final int value;

    private PxControllerShapeTypeEnum(int value) {
        this.value = value;
    }

    private static native int _geteBOX();

    private static int geteBOX() {
        Loader.load();
        return PxControllerShapeTypeEnum._geteBOX();
    }

    private static native int _geteCAPSULE();

    private static int geteCAPSULE() {
        Loader.load();
        return PxControllerShapeTypeEnum._geteCAPSULE();
    }

    public static PxControllerShapeTypeEnum forValue(int value) {
        for (int i = 0; i < PxControllerShapeTypeEnum.values().length; ++i) {
            if (PxControllerShapeTypeEnum.values()[i].value != value) continue;
            return PxControllerShapeTypeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxControllerShapeTypeEnum: " + value);
    }
}

