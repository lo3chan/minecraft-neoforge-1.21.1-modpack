/*
 * Decompiled with CFR 0.152.
 */
package physx.character;

import de.fabmax.physxjni.Loader;

public enum PxControllerNonWalkableModeEnum {
    ePREVENT_CLIMBING(PxControllerNonWalkableModeEnum.getePREVENT_CLIMBING()),
    ePREVENT_CLIMBING_AND_FORCE_SLIDING(PxControllerNonWalkableModeEnum.getePREVENT_CLIMBING_AND_FORCE_SLIDING());

    public final int value;

    private PxControllerNonWalkableModeEnum(int value) {
        this.value = value;
    }

    private static native int _getePREVENT_CLIMBING();

    private static int getePREVENT_CLIMBING() {
        Loader.load();
        return PxControllerNonWalkableModeEnum._getePREVENT_CLIMBING();
    }

    private static native int _getePREVENT_CLIMBING_AND_FORCE_SLIDING();

    private static int getePREVENT_CLIMBING_AND_FORCE_SLIDING() {
        Loader.load();
        return PxControllerNonWalkableModeEnum._getePREVENT_CLIMBING_AND_FORCE_SLIDING();
    }

    public static PxControllerNonWalkableModeEnum forValue(int value) {
        for (int i = 0; i < PxControllerNonWalkableModeEnum.values().length; ++i) {
            if (PxControllerNonWalkableModeEnum.values()[i].value != value) continue;
            return PxControllerNonWalkableModeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxControllerNonWalkableModeEnum: " + value);
    }
}

