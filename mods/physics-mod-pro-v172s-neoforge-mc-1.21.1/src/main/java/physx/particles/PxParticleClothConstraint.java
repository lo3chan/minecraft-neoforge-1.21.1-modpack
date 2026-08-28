/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxParticleClothConstraint
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxParticleClothConstraint wrapPointer(long address) {
        return address != 0L ? new PxParticleClothConstraint(address) : null;
    }

    public static PxParticleClothConstraint arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxParticleClothConstraint.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxParticleClothConstraint(long address) {
        super(address);
    }

    public PxParticleClothConstraint() {
        this.address = PxParticleClothConstraint._PxParticleClothConstraint();
    }

    private static native long _PxParticleClothConstraint();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxParticleClothConstraint._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public int getParticleIndexA() {
        this.checkNotNull();
        return PxParticleClothConstraint._getParticleIndexA(this.address);
    }

    private static native int _getParticleIndexA(long var0);

    public void setParticleIndexA(int value) {
        this.checkNotNull();
        PxParticleClothConstraint._setParticleIndexA(this.address, value);
    }

    private static native void _setParticleIndexA(long var0, int var2);

    public int getParticleIndexB() {
        this.checkNotNull();
        return PxParticleClothConstraint._getParticleIndexB(this.address);
    }

    private static native int _getParticleIndexB(long var0);

    public void setParticleIndexB(int value) {
        this.checkNotNull();
        PxParticleClothConstraint._setParticleIndexB(this.address, value);
    }

    private static native void _setParticleIndexB(long var0, int var2);

    public float getLength() {
        this.checkNotNull();
        return PxParticleClothConstraint._getLength(this.address);
    }

    private static native float _getLength(long var0);

    public void setLength(float value) {
        this.checkNotNull();
        PxParticleClothConstraint._setLength(this.address, value);
    }

    private static native void _setLength(long var0, float var2);

    public int getConstraintType() {
        this.checkNotNull();
        return PxParticleClothConstraint._getConstraintType(this.address);
    }

    private static native int _getConstraintType(long var0);

    public void setConstraintType(int value) {
        this.checkNotNull();
        PxParticleClothConstraint._setConstraintType(this.address, value);
    }

    private static native void _setConstraintType(long var0, int var2);

    static {
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleClothConstraint");
        SIZEOF = PxParticleClothConstraint.__sizeOf();
    }
}

