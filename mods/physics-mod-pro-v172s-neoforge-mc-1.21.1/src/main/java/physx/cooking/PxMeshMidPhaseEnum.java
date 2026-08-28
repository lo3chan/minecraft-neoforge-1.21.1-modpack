/*
 * Decompiled with CFR 0.152.
 */
package physx.cooking;

import de.fabmax.physxjni.Loader;

public enum PxMeshMidPhaseEnum {
    eBVH33(PxMeshMidPhaseEnum.geteBVH33()),
    eBVH34(PxMeshMidPhaseEnum.geteBVH34());

    public final int value;

    private PxMeshMidPhaseEnum(int value) {
        this.value = value;
    }

    private static native int _geteBVH33();

    private static int geteBVH33() {
        Loader.load();
        return PxMeshMidPhaseEnum._geteBVH33();
    }

    private static native int _geteBVH34();

    private static int geteBVH34() {
        Loader.load();
        return PxMeshMidPhaseEnum._geteBVH34();
    }

    public static PxMeshMidPhaseEnum forValue(int value) {
        for (int i = 0; i < PxMeshMidPhaseEnum.values().length; ++i) {
            if (PxMeshMidPhaseEnum.values()[i].value != value) continue;
            return PxMeshMidPhaseEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxMeshMidPhaseEnum: " + value);
    }
}

