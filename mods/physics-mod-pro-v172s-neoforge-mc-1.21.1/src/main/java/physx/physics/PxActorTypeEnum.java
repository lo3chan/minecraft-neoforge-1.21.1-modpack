/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxActorTypeEnum {
    eRIGID_STATIC(PxActorTypeEnum.geteRIGID_STATIC()),
    eRIGID_DYNAMIC(PxActorTypeEnum.geteRIGID_DYNAMIC()),
    eARTICULATION_LINK(PxActorTypeEnum.geteARTICULATION_LINK()),
    eSOFTBODY(PxActorTypeEnum.geteSOFTBODY()),
    eFEMCLOTH(PxActorTypeEnum.geteFEMCLOTH()),
    ePBD_PARTICLESYSTEM(PxActorTypeEnum.getePBD_PARTICLESYSTEM()),
    eFLIP_PARTICLESYSTEM(PxActorTypeEnum.geteFLIP_PARTICLESYSTEM()),
    eMPM_PARTICLESYSTEM(PxActorTypeEnum.geteMPM_PARTICLESYSTEM()),
    eHAIRSYSTEM(PxActorTypeEnum.geteHAIRSYSTEM());

    public final int value;

    private PxActorTypeEnum(int value) {
        this.value = value;
    }

    private static native int _geteRIGID_STATIC();

    private static int geteRIGID_STATIC() {
        Loader.load();
        return PxActorTypeEnum._geteRIGID_STATIC();
    }

    private static native int _geteRIGID_DYNAMIC();

    private static int geteRIGID_DYNAMIC() {
        Loader.load();
        return PxActorTypeEnum._geteRIGID_DYNAMIC();
    }

    private static native int _geteARTICULATION_LINK();

    private static int geteARTICULATION_LINK() {
        Loader.load();
        return PxActorTypeEnum._geteARTICULATION_LINK();
    }

    private static native int _geteSOFTBODY();

    private static int geteSOFTBODY() {
        Loader.load();
        return PxActorTypeEnum._geteSOFTBODY();
    }

    private static native int _geteFEMCLOTH();

    private static int geteFEMCLOTH() {
        Loader.load();
        return PxActorTypeEnum._geteFEMCLOTH();
    }

    private static native int _getePBD_PARTICLESYSTEM();

    private static int getePBD_PARTICLESYSTEM() {
        Loader.load();
        return PxActorTypeEnum._getePBD_PARTICLESYSTEM();
    }

    private static native int _geteFLIP_PARTICLESYSTEM();

    private static int geteFLIP_PARTICLESYSTEM() {
        Loader.load();
        return PxActorTypeEnum._geteFLIP_PARTICLESYSTEM();
    }

    private static native int _geteMPM_PARTICLESYSTEM();

    private static int geteMPM_PARTICLESYSTEM() {
        Loader.load();
        return PxActorTypeEnum._geteMPM_PARTICLESYSTEM();
    }

    private static native int _geteHAIRSYSTEM();

    private static int geteHAIRSYSTEM() {
        Loader.load();
        return PxActorTypeEnum._geteHAIRSYSTEM();
    }

    public static PxActorTypeEnum forValue(int value) {
        for (int i = 0; i < PxActorTypeEnum.values().length; ++i) {
            if (PxActorTypeEnum.values()[i].value != value) continue;
            return PxActorTypeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxActorTypeEnum: " + value);
    }
}

