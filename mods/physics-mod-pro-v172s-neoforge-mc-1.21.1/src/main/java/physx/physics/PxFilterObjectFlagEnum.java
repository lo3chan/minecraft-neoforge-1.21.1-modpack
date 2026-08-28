/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxFilterObjectFlagEnum {
    eKINEMATIC(PxFilterObjectFlagEnum.geteKINEMATIC()),
    eTRIGGER(PxFilterObjectFlagEnum.geteTRIGGER());

    public final int value;

    private PxFilterObjectFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteKINEMATIC();

    private static int geteKINEMATIC() {
        Loader.load();
        return PxFilterObjectFlagEnum._geteKINEMATIC();
    }

    private static native int _geteTRIGGER();

    private static int geteTRIGGER() {
        Loader.load();
        return PxFilterObjectFlagEnum._geteTRIGGER();
    }

    public static PxFilterObjectFlagEnum forValue(int value) {
        for (int i = 0; i < PxFilterObjectFlagEnum.values().length; ++i) {
            if (PxFilterObjectFlagEnum.values()[i].value != value) continue;
            return PxFilterObjectFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxFilterObjectFlagEnum: " + value);
    }
}

