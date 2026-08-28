/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxArticulationCacheFlagEnum {
    eVELOCITY(PxArticulationCacheFlagEnum.geteVELOCITY()),
    eACCELERATION(PxArticulationCacheFlagEnum.geteACCELERATION()),
    ePOSITION(PxArticulationCacheFlagEnum.getePOSITION()),
    eFORCE(PxArticulationCacheFlagEnum.geteFORCE()),
    eLINK_VELOCITY(PxArticulationCacheFlagEnum.geteLINK_VELOCITY()),
    eLINK_ACCELERATION(PxArticulationCacheFlagEnum.geteLINK_ACCELERATION()),
    eROOT_TRANSFORM(PxArticulationCacheFlagEnum.geteROOT_TRANSFORM()),
    eROOT_VELOCITIES(PxArticulationCacheFlagEnum.geteROOT_VELOCITIES()),
    eSENSOR_FORCES(PxArticulationCacheFlagEnum.geteSENSOR_FORCES()),
    eJOINT_SOLVER_FORCES(PxArticulationCacheFlagEnum.geteJOINT_SOLVER_FORCES()),
    eALL(PxArticulationCacheFlagEnum.geteALL());

    public final int value;

    private PxArticulationCacheFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteVELOCITY();

    private static int geteVELOCITY() {
        Loader.load();
        return PxArticulationCacheFlagEnum._geteVELOCITY();
    }

    private static native int _geteACCELERATION();

    private static int geteACCELERATION() {
        Loader.load();
        return PxArticulationCacheFlagEnum._geteACCELERATION();
    }

    private static native int _getePOSITION();

    private static int getePOSITION() {
        Loader.load();
        return PxArticulationCacheFlagEnum._getePOSITION();
    }

    private static native int _geteFORCE();

    private static int geteFORCE() {
        Loader.load();
        return PxArticulationCacheFlagEnum._geteFORCE();
    }

    private static native int _geteLINK_VELOCITY();

    private static int geteLINK_VELOCITY() {
        Loader.load();
        return PxArticulationCacheFlagEnum._geteLINK_VELOCITY();
    }

    private static native int _geteLINK_ACCELERATION();

    private static int geteLINK_ACCELERATION() {
        Loader.load();
        return PxArticulationCacheFlagEnum._geteLINK_ACCELERATION();
    }

    private static native int _geteROOT_TRANSFORM();

    private static int geteROOT_TRANSFORM() {
        Loader.load();
        return PxArticulationCacheFlagEnum._geteROOT_TRANSFORM();
    }

    private static native int _geteROOT_VELOCITIES();

    private static int geteROOT_VELOCITIES() {
        Loader.load();
        return PxArticulationCacheFlagEnum._geteROOT_VELOCITIES();
    }

    private static native int _geteSENSOR_FORCES();

    private static int geteSENSOR_FORCES() {
        Loader.load();
        return PxArticulationCacheFlagEnum._geteSENSOR_FORCES();
    }

    private static native int _geteJOINT_SOLVER_FORCES();

    private static int geteJOINT_SOLVER_FORCES() {
        Loader.load();
        return PxArticulationCacheFlagEnum._geteJOINT_SOLVER_FORCES();
    }

    private static native int _geteALL();

    private static int geteALL() {
        Loader.load();
        return PxArticulationCacheFlagEnum._geteALL();
    }

    public static PxArticulationCacheFlagEnum forValue(int value) {
        for (int i = 0; i < PxArticulationCacheFlagEnum.values().length; ++i) {
            if (PxArticulationCacheFlagEnum.values()[i].value != value) continue;
            return PxArticulationCacheFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxArticulationCacheFlagEnum: " + value);
    }
}

