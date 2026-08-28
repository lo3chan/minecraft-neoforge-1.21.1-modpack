/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxContactPairHeaderFlagEnum {
    eREMOVED_ACTOR_0(PxContactPairHeaderFlagEnum.geteREMOVED_ACTOR_0()),
    eREMOVED_ACTOR_1(PxContactPairHeaderFlagEnum.geteREMOVED_ACTOR_1());

    public final int value;

    private PxContactPairHeaderFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteREMOVED_ACTOR_0();

    private static int geteREMOVED_ACTOR_0() {
        Loader.load();
        return PxContactPairHeaderFlagEnum._geteREMOVED_ACTOR_0();
    }

    private static native int _geteREMOVED_ACTOR_1();

    private static int geteREMOVED_ACTOR_1() {
        Loader.load();
        return PxContactPairHeaderFlagEnum._geteREMOVED_ACTOR_1();
    }

    public static PxContactPairHeaderFlagEnum forValue(int value) {
        for (int i = 0; i < PxContactPairHeaderFlagEnum.values().length; ++i) {
            if (PxContactPairHeaderFlagEnum.values()[i].value != value) continue;
            return PxContactPairHeaderFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxContactPairHeaderFlagEnum: " + value);
    }
}

