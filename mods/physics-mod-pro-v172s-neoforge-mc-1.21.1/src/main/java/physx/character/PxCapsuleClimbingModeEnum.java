/*
 * Decompiled with CFR 0.152.
 */
package physx.character;

import de.fabmax.physxjni.Loader;

public enum PxCapsuleClimbingModeEnum {
    eEASY(PxCapsuleClimbingModeEnum.geteEASY()),
    eCONSTRAINED(PxCapsuleClimbingModeEnum.geteCONSTRAINED());

    public final int value;

    private PxCapsuleClimbingModeEnum(int value) {
        this.value = value;
    }

    private static native int _geteEASY();

    private static int geteEASY() {
        Loader.load();
        return PxCapsuleClimbingModeEnum._geteEASY();
    }

    private static native int _geteCONSTRAINED();

    private static int geteCONSTRAINED() {
        Loader.load();
        return PxCapsuleClimbingModeEnum._geteCONSTRAINED();
    }

    public static PxCapsuleClimbingModeEnum forValue(int value) {
        for (int i = 0; i < PxCapsuleClimbingModeEnum.values().length; ++i) {
            if (PxCapsuleClimbingModeEnum.values()[i].value != value) continue;
            return PxCapsuleClimbingModeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxCapsuleClimbingModeEnum: " + value);
    }
}

