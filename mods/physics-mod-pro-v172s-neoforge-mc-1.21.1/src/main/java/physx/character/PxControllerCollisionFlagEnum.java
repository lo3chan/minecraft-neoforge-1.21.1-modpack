/*
 * Decompiled with CFR 0.152.
 */
package physx.character;

import de.fabmax.physxjni.Loader;

public enum PxControllerCollisionFlagEnum {
    eCOLLISION_SIDES(PxControllerCollisionFlagEnum.geteCOLLISION_SIDES()),
    eCOLLISION_UP(PxControllerCollisionFlagEnum.geteCOLLISION_UP()),
    eCOLLISION_DOWN(PxControllerCollisionFlagEnum.geteCOLLISION_DOWN());

    public final int value;

    private PxControllerCollisionFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteCOLLISION_SIDES();

    private static int geteCOLLISION_SIDES() {
        Loader.load();
        return PxControllerCollisionFlagEnum._geteCOLLISION_SIDES();
    }

    private static native int _geteCOLLISION_UP();

    private static int geteCOLLISION_UP() {
        Loader.load();
        return PxControllerCollisionFlagEnum._geteCOLLISION_UP();
    }

    private static native int _geteCOLLISION_DOWN();

    private static int geteCOLLISION_DOWN() {
        Loader.load();
        return PxControllerCollisionFlagEnum._geteCOLLISION_DOWN();
    }

    public static PxControllerCollisionFlagEnum forValue(int value) {
        for (int i = 0; i < PxControllerCollisionFlagEnum.values().length; ++i) {
            if (PxControllerCollisionFlagEnum.values()[i].value != value) continue;
            return PxControllerCollisionFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxControllerCollisionFlagEnum: " + value);
    }
}

