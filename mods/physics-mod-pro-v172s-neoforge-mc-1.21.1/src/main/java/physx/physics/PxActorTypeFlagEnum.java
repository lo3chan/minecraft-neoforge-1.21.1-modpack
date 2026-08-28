/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxActorTypeFlagEnum {
    eRIGID_STATIC(PxActorTypeFlagEnum.geteRIGID_STATIC()),
    eRIGID_DYNAMIC(PxActorTypeFlagEnum.geteRIGID_DYNAMIC());

    public final int value;

    private PxActorTypeFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteRIGID_STATIC();

    private static int geteRIGID_STATIC() {
        Loader.load();
        return PxActorTypeFlagEnum._geteRIGID_STATIC();
    }

    private static native int _geteRIGID_DYNAMIC();

    private static int geteRIGID_DYNAMIC() {
        Loader.load();
        return PxActorTypeFlagEnum._geteRIGID_DYNAMIC();
    }

    public static PxActorTypeFlagEnum forValue(int value) {
        for (int i = 0; i < PxActorTypeFlagEnum.values().length; ++i) {
            if (PxActorTypeFlagEnum.values()[i].value != value) continue;
            return PxActorTypeFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxActorTypeFlagEnum: " + value);
    }
}

