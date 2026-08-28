/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleClutchAccuracyModeEnum {
    eESTIMATE(PxVehicleClutchAccuracyModeEnum.geteESTIMATE()),
    eBEST_POSSIBLE(PxVehicleClutchAccuracyModeEnum.geteBEST_POSSIBLE());

    public final int value;

    private PxVehicleClutchAccuracyModeEnum(int value) {
        this.value = value;
    }

    private static native int _geteESTIMATE();

    private static int geteESTIMATE() {
        Loader.load();
        return PxVehicleClutchAccuracyModeEnum._geteESTIMATE();
    }

    private static native int _geteBEST_POSSIBLE();

    private static int geteBEST_POSSIBLE() {
        Loader.load();
        return PxVehicleClutchAccuracyModeEnum._geteBEST_POSSIBLE();
    }

    public static PxVehicleClutchAccuracyModeEnum forValue(int value) {
        for (int i = 0; i < PxVehicleClutchAccuracyModeEnum.values().length; ++i) {
            if (PxVehicleClutchAccuracyModeEnum.values()[i].value != value) continue;
            return PxVehicleClutchAccuracyModeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxVehicleClutchAccuracyModeEnum: " + value);
    }
}

