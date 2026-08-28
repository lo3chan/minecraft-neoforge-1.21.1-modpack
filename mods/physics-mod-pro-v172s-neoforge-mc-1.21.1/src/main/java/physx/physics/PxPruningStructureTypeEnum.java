/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxPruningStructureTypeEnum {
    eNONE(PxPruningStructureTypeEnum.geteNONE()),
    eDYNAMIC_AABB_TREE(PxPruningStructureTypeEnum.geteDYNAMIC_AABB_TREE()),
    eSTATIC_AABB_TREE(PxPruningStructureTypeEnum.geteSTATIC_AABB_TREE());

    public final int value;

    private PxPruningStructureTypeEnum(int value) {
        this.value = value;
    }

    private static native int _geteNONE();

    private static int geteNONE() {
        Loader.load();
        return PxPruningStructureTypeEnum._geteNONE();
    }

    private static native int _geteDYNAMIC_AABB_TREE();

    private static int geteDYNAMIC_AABB_TREE() {
        Loader.load();
        return PxPruningStructureTypeEnum._geteDYNAMIC_AABB_TREE();
    }

    private static native int _geteSTATIC_AABB_TREE();

    private static int geteSTATIC_AABB_TREE() {
        Loader.load();
        return PxPruningStructureTypeEnum._geteSTATIC_AABB_TREE();
    }

    public static PxPruningStructureTypeEnum forValue(int value) {
        for (int i = 0; i < PxPruningStructureTypeEnum.values().length; ++i) {
            if (PxPruningStructureTypeEnum.values()[i].value != value) continue;
            return PxPruningStructureTypeEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxPruningStructureTypeEnum: " + value);
    }
}

