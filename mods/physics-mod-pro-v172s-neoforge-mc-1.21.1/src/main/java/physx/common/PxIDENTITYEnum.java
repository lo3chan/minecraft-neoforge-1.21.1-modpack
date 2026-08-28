/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import de.fabmax.physxjni.Loader;

public enum PxIDENTITYEnum {
    PxIdentity(PxIDENTITYEnum.getPxIdentity());

    public final int value;

    private PxIDENTITYEnum(int value) {
        this.value = value;
    }

    private static native int _getPxIdentity();

    private static int getPxIdentity() {
        Loader.load();
        return PxIDENTITYEnum._getPxIdentity();
    }

    public static PxIDENTITYEnum forValue(int value) {
        for (int i = 0; i < PxIDENTITYEnum.values().length; ++i) {
            if (PxIDENTITYEnum.values()[i].value != value) continue;
            return PxIDENTITYEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxIDENTITYEnum: " + value);
    }
}

