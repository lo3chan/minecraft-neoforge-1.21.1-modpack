/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import de.fabmax.physxjni.Loader;

public enum PxJointActorIndexEnum {
    eACTOR0(PxJointActorIndexEnum.geteACTOR0()),
    eACTOR1(PxJointActorIndexEnum.geteACTOR1());

    public final int value;

    private PxJointActorIndexEnum(int value) {
        this.value = value;
    }

    private static native int _geteACTOR0();

    private static int geteACTOR0() {
        Loader.load();
        return PxJointActorIndexEnum._geteACTOR0();
    }

    private static native int _geteACTOR1();

    private static int geteACTOR1() {
        Loader.load();
        return PxJointActorIndexEnum._geteACTOR1();
    }

    public static PxJointActorIndexEnum forValue(int value) {
        for (int i = 0; i < PxJointActorIndexEnum.values().length; ++i) {
            if (PxJointActorIndexEnum.values()[i].value != value) continue;
            return PxJointActorIndexEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxJointActorIndexEnum: " + value);
    }
}

