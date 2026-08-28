/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxArticulationKinematicFlagEnum {
    ePOSITION(PxArticulationKinematicFlagEnum.getePOSITION()),
    eVELOCITY(PxArticulationKinematicFlagEnum.geteVELOCITY());

    public final int value;

    private PxArticulationKinematicFlagEnum(int value) {
        this.value = value;
    }

    private static native int _getePOSITION();

    private static int getePOSITION() {
        Loader.load();
        return PxArticulationKinematicFlagEnum._getePOSITION();
    }

    private static native int _geteVELOCITY();

    private static int geteVELOCITY() {
        Loader.load();
        return PxArticulationKinematicFlagEnum._geteVELOCITY();
    }

    public static PxArticulationKinematicFlagEnum forValue(int value) {
        for (int i = 0; i < PxArticulationKinematicFlagEnum.values().length; ++i) {
            if (PxArticulationKinematicFlagEnum.values()[i].value != value) continue;
            return PxArticulationKinematicFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxArticulationKinematicFlagEnum: " + value);
    }
}

