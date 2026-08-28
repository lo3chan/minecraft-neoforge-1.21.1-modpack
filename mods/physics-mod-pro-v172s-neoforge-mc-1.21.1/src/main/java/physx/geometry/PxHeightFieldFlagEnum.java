/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import de.fabmax.physxjni.Loader;

public enum PxHeightFieldFlagEnum {
    eNO_BOUNDARY_EDGES(PxHeightFieldFlagEnum.geteNO_BOUNDARY_EDGES());

    public final int value;

    private PxHeightFieldFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteNO_BOUNDARY_EDGES();

    private static int geteNO_BOUNDARY_EDGES() {
        Loader.load();
        return PxHeightFieldFlagEnum._geteNO_BOUNDARY_EDGES();
    }

    public static PxHeightFieldFlagEnum forValue(int value) {
        for (int i = 0; i < PxHeightFieldFlagEnum.values().length; ++i) {
            if (PxHeightFieldFlagEnum.values()[i].value != value) continue;
            return PxHeightFieldFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxHeightFieldFlagEnum: " + value);
    }
}

