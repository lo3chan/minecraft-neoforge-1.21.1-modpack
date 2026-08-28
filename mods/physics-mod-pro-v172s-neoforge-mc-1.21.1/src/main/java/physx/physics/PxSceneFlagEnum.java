/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxSceneFlagEnum {
    eENABLE_ACTIVE_ACTORS(PxSceneFlagEnum.geteENABLE_ACTIVE_ACTORS()),
    eENABLE_CCD(PxSceneFlagEnum.geteENABLE_CCD()),
    eDISABLE_CCD_RESWEEP(PxSceneFlagEnum.geteDISABLE_CCD_RESWEEP()),
    eENABLE_PCM(PxSceneFlagEnum.geteENABLE_PCM()),
    eDISABLE_CONTACT_REPORT_BUFFER_RESIZE(PxSceneFlagEnum.geteDISABLE_CONTACT_REPORT_BUFFER_RESIZE()),
    eDISABLE_CONTACT_CACHE(PxSceneFlagEnum.geteDISABLE_CONTACT_CACHE()),
    eREQUIRE_RW_LOCK(PxSceneFlagEnum.geteREQUIRE_RW_LOCK()),
    eENABLE_STABILIZATION(PxSceneFlagEnum.geteENABLE_STABILIZATION()),
    eENABLE_AVERAGE_POINT(PxSceneFlagEnum.geteENABLE_AVERAGE_POINT()),
    eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS(PxSceneFlagEnum.geteEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS()),
    eENABLE_GPU_DYNAMICS(PxSceneFlagEnum.geteENABLE_GPU_DYNAMICS()),
    eENABLE_ENHANCED_DETERMINISM(PxSceneFlagEnum.geteENABLE_ENHANCED_DETERMINISM()),
    eENABLE_FRICTION_EVERY_ITERATION(PxSceneFlagEnum.geteENABLE_FRICTION_EVERY_ITERATION()),
    eENABLE_DIRECT_GPU_API(PxSceneFlagEnum.geteENABLE_DIRECT_GPU_API()),
    eMUTABLE_FLAGS(PxSceneFlagEnum.geteMUTABLE_FLAGS());

    public final int value;

    private PxSceneFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteENABLE_ACTIVE_ACTORS();

    private static int geteENABLE_ACTIVE_ACTORS() {
        Loader.load();
        return PxSceneFlagEnum._geteENABLE_ACTIVE_ACTORS();
    }

    private static native int _geteENABLE_CCD();

    private static int geteENABLE_CCD() {
        Loader.load();
        return PxSceneFlagEnum._geteENABLE_CCD();
    }

    private static native int _geteDISABLE_CCD_RESWEEP();

    private static int geteDISABLE_CCD_RESWEEP() {
        Loader.load();
        return PxSceneFlagEnum._geteDISABLE_CCD_RESWEEP();
    }

    private static native int _geteENABLE_PCM();

    private static int geteENABLE_PCM() {
        Loader.load();
        return PxSceneFlagEnum._geteENABLE_PCM();
    }

    private static native int _geteDISABLE_CONTACT_REPORT_BUFFER_RESIZE();

    private static int geteDISABLE_CONTACT_REPORT_BUFFER_RESIZE() {
        Loader.load();
        return PxSceneFlagEnum._geteDISABLE_CONTACT_REPORT_BUFFER_RESIZE();
    }

    private static native int _geteDISABLE_CONTACT_CACHE();

    private static int geteDISABLE_CONTACT_CACHE() {
        Loader.load();
        return PxSceneFlagEnum._geteDISABLE_CONTACT_CACHE();
    }

    private static native int _geteREQUIRE_RW_LOCK();

    private static int geteREQUIRE_RW_LOCK() {
        Loader.load();
        return PxSceneFlagEnum._geteREQUIRE_RW_LOCK();
    }

    private static native int _geteENABLE_STABILIZATION();

    private static int geteENABLE_STABILIZATION() {
        Loader.load();
        return PxSceneFlagEnum._geteENABLE_STABILIZATION();
    }

    private static native int _geteENABLE_AVERAGE_POINT();

    private static int geteENABLE_AVERAGE_POINT() {
        Loader.load();
        return PxSceneFlagEnum._geteENABLE_AVERAGE_POINT();
    }

    private static native int _geteEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS();

    private static int geteEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS() {
        Loader.load();
        return PxSceneFlagEnum._geteEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS();
    }

    private static native int _geteENABLE_GPU_DYNAMICS();

    private static int geteENABLE_GPU_DYNAMICS() {
        Loader.load();
        return PxSceneFlagEnum._geteENABLE_GPU_DYNAMICS();
    }

    private static native int _geteENABLE_ENHANCED_DETERMINISM();

    private static int geteENABLE_ENHANCED_DETERMINISM() {
        Loader.load();
        return PxSceneFlagEnum._geteENABLE_ENHANCED_DETERMINISM();
    }

    private static native int _geteENABLE_FRICTION_EVERY_ITERATION();

    private static int geteENABLE_FRICTION_EVERY_ITERATION() {
        Loader.load();
        return PxSceneFlagEnum._geteENABLE_FRICTION_EVERY_ITERATION();
    }

    private static native int _geteENABLE_DIRECT_GPU_API();

    private static int geteENABLE_DIRECT_GPU_API() {
        Loader.load();
        return PxSceneFlagEnum._geteENABLE_DIRECT_GPU_API();
    }

    private static native int _geteMUTABLE_FLAGS();

    private static int geteMUTABLE_FLAGS() {
        Loader.load();
        return PxSceneFlagEnum._geteMUTABLE_FLAGS();
    }

    public static PxSceneFlagEnum forValue(int value) {
        for (int i = 0; i < PxSceneFlagEnum.values().length; ++i) {
            if (PxSceneFlagEnum.values()[i].value != value) continue;
            return PxSceneFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxSceneFlagEnum: " + value);
    }
}

