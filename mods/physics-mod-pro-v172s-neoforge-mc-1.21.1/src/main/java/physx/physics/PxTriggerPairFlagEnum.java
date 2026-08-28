/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxTriggerPairFlagEnum {
    eREMOVED_SHAPE_TRIGGER(PxTriggerPairFlagEnum.geteREMOVED_SHAPE_TRIGGER()),
    eREMOVED_SHAPE_OTHER(PxTriggerPairFlagEnum.geteREMOVED_SHAPE_OTHER()),
    eNEXT_FREE(PxTriggerPairFlagEnum.geteNEXT_FREE());

    public final int value;

    private PxTriggerPairFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteREMOVED_SHAPE_TRIGGER();

    private static int geteREMOVED_SHAPE_TRIGGER() {
        Loader.load();
        return PxTriggerPairFlagEnum._geteREMOVED_SHAPE_TRIGGER();
    }

    private static native int _geteREMOVED_SHAPE_OTHER();

    private static int geteREMOVED_SHAPE_OTHER() {
        Loader.load();
        return PxTriggerPairFlagEnum._geteREMOVED_SHAPE_OTHER();
    }

    private static native int _geteNEXT_FREE();

    private static int geteNEXT_FREE() {
        Loader.load();
        return PxTriggerPairFlagEnum._geteNEXT_FREE();
    }

    public static PxTriggerPairFlagEnum forValue(int value) {
        for (int i = 0; i < PxTriggerPairFlagEnum.values().length; ++i) {
            if (PxTriggerPairFlagEnum.values()[i].value != value) continue;
            return PxTriggerPairFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxTriggerPairFlagEnum: " + value);
    }
}

