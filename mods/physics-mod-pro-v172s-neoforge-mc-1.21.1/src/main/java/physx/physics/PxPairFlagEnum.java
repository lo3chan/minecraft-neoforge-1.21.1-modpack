/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxPairFlagEnum {
    eSOLVE_CONTACT(PxPairFlagEnum.geteSOLVE_CONTACT()),
    eMODIFY_CONTACTS(PxPairFlagEnum.geteMODIFY_CONTACTS()),
    eNOTIFY_TOUCH_FOUND(PxPairFlagEnum.geteNOTIFY_TOUCH_FOUND()),
    eNOTIFY_TOUCH_PERSISTS(PxPairFlagEnum.geteNOTIFY_TOUCH_PERSISTS()),
    eNOTIFY_TOUCH_LOST(PxPairFlagEnum.geteNOTIFY_TOUCH_LOST()),
    eNOTIFY_TOUCH_CCD(PxPairFlagEnum.geteNOTIFY_TOUCH_CCD()),
    eNOTIFY_THRESHOLD_FORCE_FOUND(PxPairFlagEnum.geteNOTIFY_THRESHOLD_FORCE_FOUND()),
    eNOTIFY_THRESHOLD_FORCE_PERSISTS(PxPairFlagEnum.geteNOTIFY_THRESHOLD_FORCE_PERSISTS()),
    eNOTIFY_THRESHOLD_FORCE_LOST(PxPairFlagEnum.geteNOTIFY_THRESHOLD_FORCE_LOST()),
    eNOTIFY_CONTACT_POINTS(PxPairFlagEnum.geteNOTIFY_CONTACT_POINTS()),
    eDETECT_DISCRETE_CONTACT(PxPairFlagEnum.geteDETECT_DISCRETE_CONTACT()),
    eDETECT_CCD_CONTACT(PxPairFlagEnum.geteDETECT_CCD_CONTACT()),
    ePRE_SOLVER_VELOCITY(PxPairFlagEnum.getePRE_SOLVER_VELOCITY()),
    ePOST_SOLVER_VELOCITY(PxPairFlagEnum.getePOST_SOLVER_VELOCITY()),
    eCONTACT_EVENT_POSE(PxPairFlagEnum.geteCONTACT_EVENT_POSE()),
    eNEXT_FREE(PxPairFlagEnum.geteNEXT_FREE()),
    eCONTACT_DEFAULT(PxPairFlagEnum.geteCONTACT_DEFAULT()),
    eTRIGGER_DEFAULT(PxPairFlagEnum.geteTRIGGER_DEFAULT());

    public final int value;

    private PxPairFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteSOLVE_CONTACT();

    private static int geteSOLVE_CONTACT() {
        Loader.load();
        return PxPairFlagEnum._geteSOLVE_CONTACT();
    }

    private static native int _geteMODIFY_CONTACTS();

    private static int geteMODIFY_CONTACTS() {
        Loader.load();
        return PxPairFlagEnum._geteMODIFY_CONTACTS();
    }

    private static native int _geteNOTIFY_TOUCH_FOUND();

    private static int geteNOTIFY_TOUCH_FOUND() {
        Loader.load();
        return PxPairFlagEnum._geteNOTIFY_TOUCH_FOUND();
    }

    private static native int _geteNOTIFY_TOUCH_PERSISTS();

    private static int geteNOTIFY_TOUCH_PERSISTS() {
        Loader.load();
        return PxPairFlagEnum._geteNOTIFY_TOUCH_PERSISTS();
    }

    private static native int _geteNOTIFY_TOUCH_LOST();

    private static int geteNOTIFY_TOUCH_LOST() {
        Loader.load();
        return PxPairFlagEnum._geteNOTIFY_TOUCH_LOST();
    }

    private static native int _geteNOTIFY_TOUCH_CCD();

    private static int geteNOTIFY_TOUCH_CCD() {
        Loader.load();
        return PxPairFlagEnum._geteNOTIFY_TOUCH_CCD();
    }

    private static native int _geteNOTIFY_THRESHOLD_FORCE_FOUND();

    private static int geteNOTIFY_THRESHOLD_FORCE_FOUND() {
        Loader.load();
        return PxPairFlagEnum._geteNOTIFY_THRESHOLD_FORCE_FOUND();
    }

    private static native int _geteNOTIFY_THRESHOLD_FORCE_PERSISTS();

    private static int geteNOTIFY_THRESHOLD_FORCE_PERSISTS() {
        Loader.load();
        return PxPairFlagEnum._geteNOTIFY_THRESHOLD_FORCE_PERSISTS();
    }

    private static native int _geteNOTIFY_THRESHOLD_FORCE_LOST();

    private static int geteNOTIFY_THRESHOLD_FORCE_LOST() {
        Loader.load();
        return PxPairFlagEnum._geteNOTIFY_THRESHOLD_FORCE_LOST();
    }

    private static native int _geteNOTIFY_CONTACT_POINTS();

    private static int geteNOTIFY_CONTACT_POINTS() {
        Loader.load();
        return PxPairFlagEnum._geteNOTIFY_CONTACT_POINTS();
    }

    private static native int _geteDETECT_DISCRETE_CONTACT();

    private static int geteDETECT_DISCRETE_CONTACT() {
        Loader.load();
        return PxPairFlagEnum._geteDETECT_DISCRETE_CONTACT();
    }

    private static native int _geteDETECT_CCD_CONTACT();

    private static int geteDETECT_CCD_CONTACT() {
        Loader.load();
        return PxPairFlagEnum._geteDETECT_CCD_CONTACT();
    }

    private static native int _getePRE_SOLVER_VELOCITY();

    private static int getePRE_SOLVER_VELOCITY() {
        Loader.load();
        return PxPairFlagEnum._getePRE_SOLVER_VELOCITY();
    }

    private static native int _getePOST_SOLVER_VELOCITY();

    private static int getePOST_SOLVER_VELOCITY() {
        Loader.load();
        return PxPairFlagEnum._getePOST_SOLVER_VELOCITY();
    }

    private static native int _geteCONTACT_EVENT_POSE();

    private static int geteCONTACT_EVENT_POSE() {
        Loader.load();
        return PxPairFlagEnum._geteCONTACT_EVENT_POSE();
    }

    private static native int _geteNEXT_FREE();

    private static int geteNEXT_FREE() {
        Loader.load();
        return PxPairFlagEnum._geteNEXT_FREE();
    }

    private static native int _geteCONTACT_DEFAULT();

    private static int geteCONTACT_DEFAULT() {
        Loader.load();
        return PxPairFlagEnum._geteCONTACT_DEFAULT();
    }

    private static native int _geteTRIGGER_DEFAULT();

    private static int geteTRIGGER_DEFAULT() {
        Loader.load();
        return PxPairFlagEnum._geteTRIGGER_DEFAULT();
    }

    public static PxPairFlagEnum forValue(int value) {
        for (int i = 0; i < PxPairFlagEnum.values().length; ++i) {
            if (PxPairFlagEnum.values()[i].value != value) continue;
            return PxPairFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxPairFlagEnum: " + value);
    }
}

