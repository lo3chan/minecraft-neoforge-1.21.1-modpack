/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxArticulationJointTypeEnum {
    eFIX(PxArticulationJointTypeEnum.geteFIX()),
    ePRISMATIC(PxArticulationJointTypeEnum.getePRISMATIC()),
    eREVOLUTE(PxArticulationJointTypeEnum.geteREVOLUTE()),
    eSPHERICAL(PxArticulationJointTypeEnum.geteSPHERICAL()),
    eUNDEFINED(PxArticulationJointTypeEnum.geteUNDEFINED());

    public final int value;

    private PxArticulationJointTypeEnum(int value) {
        this.value = value;
    }

    private static native int _geteFIX();

    private static int geteFIX() {
        Loader.load();
        return PxArticulationJointTypeEnum._geteFIX();
    }

    private static native int _getePRISMATIC();

    private static int getePRISMATIC() {
        Loader.load();
        return PxArticulationJointTypeEnum._getePRISMATIC();
    }

    private static native int _geteREVOLUTE();

    private static int geteREVOLUTE() {
        Loader.load();
        return PxArticulationJointTypeEnum._geteREVOLUTE();
    }

    private static native int _geteSPHERICAL();

    private static int geteSPHERICAL() {
        Loader.load();
        return PxArticulationJointTypeEnum._geteSPHERICAL();
    }

    private static native int _geteUNDEFINED();

    private static int geteUNDEFINED() {
        Loader.load();
        return PxArticulationJointTypeEnum._geteUNDEFINED();
    }

    public static PxArticulationJointTypeEnum forValue(int value) {
        for (int i = 0; i < PxArticulationJointTypeEnum.values().length; ++i) {
            if (PxArticulationJointTypeEnum.values()[i].value != value) continue;
            return PxArticulationJointTypeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxArticulationJointTypeEnum: " + value);
    }
}

