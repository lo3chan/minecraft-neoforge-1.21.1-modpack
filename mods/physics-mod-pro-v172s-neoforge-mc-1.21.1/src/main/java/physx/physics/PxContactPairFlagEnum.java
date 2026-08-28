/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxContactPairFlagEnum {
    eREMOVED_SHAPE_0(PxContactPairFlagEnum.geteREMOVED_SHAPE_0()),
    eREMOVED_SHAPE_1(PxContactPairFlagEnum.geteREMOVED_SHAPE_1()),
    eACTOR_PAIR_HAS_FIRST_TOUCH(PxContactPairFlagEnum.geteACTOR_PAIR_HAS_FIRST_TOUCH()),
    eACTOR_PAIR_LOST_TOUCH(PxContactPairFlagEnum.geteACTOR_PAIR_LOST_TOUCH()),
    eINTERNAL_HAS_IMPULSES(PxContactPairFlagEnum.geteINTERNAL_HAS_IMPULSES()),
    eINTERNAL_CONTACTS_ARE_FLIPPED(PxContactPairFlagEnum.geteINTERNAL_CONTACTS_ARE_FLIPPED());

    public final int value;

    private PxContactPairFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteREMOVED_SHAPE_0();

    private static int geteREMOVED_SHAPE_0() {
        Loader.load();
        return PxContactPairFlagEnum._geteREMOVED_SHAPE_0();
    }

    private static native int _geteREMOVED_SHAPE_1();

    private static int geteREMOVED_SHAPE_1() {
        Loader.load();
        return PxContactPairFlagEnum._geteREMOVED_SHAPE_1();
    }

    private static native int _geteACTOR_PAIR_HAS_FIRST_TOUCH();

    private static int geteACTOR_PAIR_HAS_FIRST_TOUCH() {
        Loader.load();
        return PxContactPairFlagEnum._geteACTOR_PAIR_HAS_FIRST_TOUCH();
    }

    private static native int _geteACTOR_PAIR_LOST_TOUCH();

    private static int geteACTOR_PAIR_LOST_TOUCH() {
        Loader.load();
        return PxContactPairFlagEnum._geteACTOR_PAIR_LOST_TOUCH();
    }

    private static native int _geteINTERNAL_HAS_IMPULSES();

    private static int geteINTERNAL_HAS_IMPULSES() {
        Loader.load();
        return PxContactPairFlagEnum._geteINTERNAL_HAS_IMPULSES();
    }

    private static native int _geteINTERNAL_CONTACTS_ARE_FLIPPED();

    private static int geteINTERNAL_CONTACTS_ARE_FLIPPED() {
        Loader.load();
        return PxContactPairFlagEnum._geteINTERNAL_CONTACTS_ARE_FLIPPED();
    }

    public static PxContactPairFlagEnum forValue(int value) {
        for (int i = 0; i < PxContactPairFlagEnum.values().length; ++i) {
            if (PxContactPairFlagEnum.values()[i].value != value) continue;
            return PxContactPairFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxContactPairFlagEnum: " + value);
    }
}

