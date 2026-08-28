/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.particles.PxParticleClothConstraint;

public class PxParticleClothCooker
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected PxParticleClothCooker() {
    }

    private static native int __sizeOf();

    public static PxParticleClothCooker wrapPointer(long address) {
        return address != 0L ? new PxParticleClothCooker(address) : null;
    }

    public static PxParticleClothCooker arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxParticleClothCooker.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxParticleClothCooker(long address) {
        super(address);
    }

    public void release() {
        this.checkNotNull();
        PxParticleClothCooker._release(this.address);
    }

    private static native void _release(long var0);

    public void cookConstraints() {
        this.checkNotNull();
        PxParticleClothCooker._cookConstraints(this.address);
    }

    private static native void _cookConstraints(long var0);

    public void cookConstraints(PxParticleClothConstraint constraints) {
        this.checkNotNull();
        PxParticleClothCooker._cookConstraints(this.address, constraints.getAddress());
    }

    private static native void _cookConstraints(long var0, long var2);

    public void cookConstraints(PxParticleClothConstraint constraints, int numConstraints) {
        this.checkNotNull();
        PxParticleClothCooker._cookConstraints(this.address, constraints.getAddress(), numConstraints);
    }

    private static native void _cookConstraints(long var0, long var2, int var4);

    public NativeObject getTriangleIndices() {
        this.checkNotNull();
        return NativeObject.wrapPointer(PxParticleClothCooker._getTriangleIndices(this.address));
    }

    private static native long _getTriangleIndices(long var0);

    public int getTriangleIndicesCount() {
        this.checkNotNull();
        return PxParticleClothCooker._getTriangleIndicesCount(this.address);
    }

    private static native int _getTriangleIndicesCount(long var0);

    public PxParticleClothConstraint getConstraints() {
        this.checkNotNull();
        return PxParticleClothConstraint.wrapPointer(PxParticleClothCooker._getConstraints(this.address));
    }

    private static native long _getConstraints(long var0);

    public int getConstraintCount() {
        this.checkNotNull();
        return PxParticleClothCooker._getConstraintCount(this.address);
    }

    private static native int _getConstraintCount(long var0);

    public void calculateMeshVolume() {
        this.checkNotNull();
        PxParticleClothCooker._calculateMeshVolume(this.address);
    }

    private static native void _calculateMeshVolume(long var0);

    public float getMeshVolume() {
        this.checkNotNull();
        return PxParticleClothCooker._getMeshVolume(this.address);
    }

    private static native float _getMeshVolume(long var0);

    static {
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleClothCooker");
        SIZEOF = PxParticleClothCooker.__sizeOf();
    }
}

