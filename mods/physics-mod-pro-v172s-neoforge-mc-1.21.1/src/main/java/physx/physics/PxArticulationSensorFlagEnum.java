/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxArticulationSensorFlagEnum {
    eFORWARD_DYNAMICS_FORCES(PxArticulationSensorFlagEnum.geteFORWARD_DYNAMICS_FORCES()),
    eCONSTRAINT_SOLVER_FORCES(PxArticulationSensorFlagEnum.geteCONSTRAINT_SOLVER_FORCES()),
    eWORLD_FRAME(PxArticulationSensorFlagEnum.geteWORLD_FRAME());

    public final int value;

    private PxArticulationSensorFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteFORWARD_DYNAMICS_FORCES();

    private static int geteFORWARD_DYNAMICS_FORCES() {
        Loader.load();
        return PxArticulationSensorFlagEnum._geteFORWARD_DYNAMICS_FORCES();
    }

    private static native int _geteCONSTRAINT_SOLVER_FORCES();

    private static int geteCONSTRAINT_SOLVER_FORCES() {
        Loader.load();
        return PxArticulationSensorFlagEnum._geteCONSTRAINT_SOLVER_FORCES();
    }

    private static native int _geteWORLD_FRAME();

    private static int geteWORLD_FRAME() {
        Loader.load();
        return PxArticulationSensorFlagEnum._geteWORLD_FRAME();
    }

    public static PxArticulationSensorFlagEnum forValue(int value) {
        for (int i = 0; i < PxArticulationSensorFlagEnum.values().length; ++i) {
            if (PxArticulationSensorFlagEnum.values()[i].value != value) continue;
            return PxArticulationSensorFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxArticulationSensorFlagEnum: " + value);
    }
}

