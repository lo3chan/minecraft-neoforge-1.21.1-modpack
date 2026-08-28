/*
 * Decompiled with CFR 0.152.
 */
package physx.cooking;

import de.fabmax.physxjni.Loader;

public enum PxMeshCookingHintEnum {
    eSIM_PERFORMANCE(PxMeshCookingHintEnum.geteSIM_PERFORMANCE()),
    eCOOKING_PERFORMANCE(PxMeshCookingHintEnum.geteCOOKING_PERFORMANCE());

    public final int value;

    private PxMeshCookingHintEnum(int value) {
        this.value = value;
    }

    private static native int _geteSIM_PERFORMANCE();

    private static int geteSIM_PERFORMANCE() {
        Loader.load();
        return PxMeshCookingHintEnum._geteSIM_PERFORMANCE();
    }

    private static native int _geteCOOKING_PERFORMANCE();

    private static int geteCOOKING_PERFORMANCE() {
        Loader.load();
        return PxMeshCookingHintEnum._geteCOOKING_PERFORMANCE();
    }

    public static PxMeshCookingHintEnum forValue(int value) {
        for (int i = 0; i < PxMeshCookingHintEnum.values().length; ++i) {
            if (PxMeshCookingHintEnum.values()[i].value != value) continue;
            return PxMeshCookingHintEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxMeshCookingHintEnum: " + value);
    }
}

