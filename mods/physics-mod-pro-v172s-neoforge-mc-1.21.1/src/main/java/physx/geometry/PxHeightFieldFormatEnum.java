/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import de.fabmax.physxjni.Loader;

public enum PxHeightFieldFormatEnum {
    eS16_TM(PxHeightFieldFormatEnum.geteS16_TM());

    public final int value;

    private PxHeightFieldFormatEnum(int value) {
        this.value = value;
    }

    private static native int _geteS16_TM();

    private static int geteS16_TM() {
        Loader.load();
        return PxHeightFieldFormatEnum._geteS16_TM();
    }

    public static PxHeightFieldFormatEnum forValue(int value) {
        for (int i = 0; i < PxHeightFieldFormatEnum.values().length; ++i) {
            if (PxHeightFieldFormatEnum.values()[i].value != value) continue;
            return PxHeightFieldFormatEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxHeightFieldFormatEnum: " + value);
    }
}

