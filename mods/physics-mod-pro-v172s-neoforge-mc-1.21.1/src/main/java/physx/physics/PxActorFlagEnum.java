/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxActorFlagEnum {
    eVISUALIZATION(PxActorFlagEnum.geteVISUALIZATION()),
    eDISABLE_GRAVITY(PxActorFlagEnum.geteDISABLE_GRAVITY()),
    eSEND_SLEEP_NOTIFIES(PxActorFlagEnum.geteSEND_SLEEP_NOTIFIES()),
    eDISABLE_SIMULATION(PxActorFlagEnum.geteDISABLE_SIMULATION());

    public final int value;

    private PxActorFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteVISUALIZATION();

    private static int geteVISUALIZATION() {
        Loader.load();
        return PxActorFlagEnum._geteVISUALIZATION();
    }

    private static native int _geteDISABLE_GRAVITY();

    private static int geteDISABLE_GRAVITY() {
        Loader.load();
        return PxActorFlagEnum._geteDISABLE_GRAVITY();
    }

    private static native int _geteSEND_SLEEP_NOTIFIES();

    private static int geteSEND_SLEEP_NOTIFIES() {
        Loader.load();
        return PxActorFlagEnum._geteSEND_SLEEP_NOTIFIES();
    }

    private static native int _geteDISABLE_SIMULATION();

    private static int geteDISABLE_SIMULATION() {
        Loader.load();
        return PxActorFlagEnum._geteDISABLE_SIMULATION();
    }

    public static PxActorFlagEnum forValue(int value) {
        for (int i = 0; i < PxActorFlagEnum.values().length; ++i) {
            if (PxActorFlagEnum.values()[i].value != value) continue;
            return PxActorFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxActorFlagEnum: " + value);
    }
}

