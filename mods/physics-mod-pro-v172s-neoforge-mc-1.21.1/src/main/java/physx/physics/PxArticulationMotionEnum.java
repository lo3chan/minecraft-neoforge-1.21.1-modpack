/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxArticulationMotionEnum {
    eLOCKED(PxArticulationMotionEnum.geteLOCKED()),
    eLIMITED(PxArticulationMotionEnum.geteLIMITED()),
    eFREE(PxArticulationMotionEnum.geteFREE());

    public final int value;

    private PxArticulationMotionEnum(int value) {
        this.value = value;
    }

    private static native int _geteLOCKED();

    private static int geteLOCKED() {
        Loader.load();
        return PxArticulationMotionEnum._geteLOCKED();
    }

    private static native int _geteLIMITED();

    private static int geteLIMITED() {
        Loader.load();
        return PxArticulationMotionEnum._geteLIMITED();
    }

    private static native int _geteFREE();

    private static int geteFREE() {
        Loader.load();
        return PxArticulationMotionEnum._geteFREE();
    }

    public static PxArticulationMotionEnum forValue(int value) {
        for (int i = 0; i < PxArticulationMotionEnum.values().length; ++i) {
            if (PxArticulationMotionEnum.values()[i].value != value) continue;
            return PxArticulationMotionEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxArticulationMotionEnum: " + value);
    }
}

