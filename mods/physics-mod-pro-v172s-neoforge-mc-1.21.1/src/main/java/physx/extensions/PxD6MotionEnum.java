/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import de.fabmax.physxjni.Loader;

public enum PxD6MotionEnum {
    eLOCKED(PxD6MotionEnum.geteLOCKED()),
    eLIMITED(PxD6MotionEnum.geteLIMITED()),
    eFREE(PxD6MotionEnum.geteFREE());

    public final int value;

    private PxD6MotionEnum(int value) {
        this.value = value;
    }

    private static native int _geteLOCKED();

    private static int geteLOCKED() {
        Loader.load();
        return PxD6MotionEnum._geteLOCKED();
    }

    private static native int _geteLIMITED();

    private static int geteLIMITED() {
        Loader.load();
        return PxD6MotionEnum._geteLIMITED();
    }

    private static native int _geteFREE();

    private static int geteFREE() {
        Loader.load();
        return PxD6MotionEnum._geteFREE();
    }

    public static PxD6MotionEnum forValue(int value) {
        for (int i = 0; i < PxD6MotionEnum.values().length; ++i) {
            if (PxD6MotionEnum.values()[i].value != value) continue;
            return PxD6MotionEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxD6MotionEnum: " + value);
    }
}

