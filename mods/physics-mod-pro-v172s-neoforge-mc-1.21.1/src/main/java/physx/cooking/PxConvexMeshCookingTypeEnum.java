/*
 * Decompiled with CFR 0.152.
 */
package physx.cooking;

import de.fabmax.physxjni.Loader;

public enum PxConvexMeshCookingTypeEnum {
    eQUICKHULL(PxConvexMeshCookingTypeEnum.geteQUICKHULL());

    public final int value;

    private PxConvexMeshCookingTypeEnum(int value) {
        this.value = value;
    }

    private static native int _geteQUICKHULL();

    private static int geteQUICKHULL() {
        Loader.load();
        return PxConvexMeshCookingTypeEnum._geteQUICKHULL();
    }

    public static PxConvexMeshCookingTypeEnum forValue(int value) {
        for (int i = 0; i < PxConvexMeshCookingTypeEnum.values().length; ++i) {
            if (PxConvexMeshCookingTypeEnum.values()[i].value != value) continue;
            return PxConvexMeshCookingTypeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxConvexMeshCookingTypeEnum: " + value);
    }
}

