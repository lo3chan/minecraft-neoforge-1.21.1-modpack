/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxArticulationFlagEnum {
    eFIX_BASE(PxArticulationFlagEnum.geteFIX_BASE()),
    eDRIVE_LIMITS_ARE_FORCES(PxArticulationFlagEnum.geteDRIVE_LIMITS_ARE_FORCES()),
    eDISABLE_SELF_COLLISION(PxArticulationFlagEnum.geteDISABLE_SELF_COLLISION()),
    eCOMPUTE_JOINT_FORCES(PxArticulationFlagEnum.geteCOMPUTE_JOINT_FORCES());

    public final int value;

    private PxArticulationFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteFIX_BASE();

    private static int geteFIX_BASE() {
        Loader.load();
        return PxArticulationFlagEnum._geteFIX_BASE();
    }

    private static native int _geteDRIVE_LIMITS_ARE_FORCES();

    private static int geteDRIVE_LIMITS_ARE_FORCES() {
        Loader.load();
        return PxArticulationFlagEnum._geteDRIVE_LIMITS_ARE_FORCES();
    }

    private static native int _geteDISABLE_SELF_COLLISION();

    private static int geteDISABLE_SELF_COLLISION() {
        Loader.load();
        return PxArticulationFlagEnum._geteDISABLE_SELF_COLLISION();
    }

    private static native int _geteCOMPUTE_JOINT_FORCES();

    private static int geteCOMPUTE_JOINT_FORCES() {
        Loader.load();
        return PxArticulationFlagEnum._geteCOMPUTE_JOINT_FORCES();
    }

    public static PxArticulationFlagEnum forValue(int value) {
        for (int i = 0; i < PxArticulationFlagEnum.values().length; ++i) {
            if (PxArticulationFlagEnum.values()[i].value != value) continue;
            return PxArticulationFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxArticulationFlagEnum: " + value);
    }
}

