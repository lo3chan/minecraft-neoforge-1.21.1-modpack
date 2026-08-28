/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.PxVec4;
import physx.particles.PxParticleCloth;
import physx.particles.PxParticleSpring;
import physx.support.PxU32Ptr;

public class PxParticleClothDesc
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxParticleClothDesc wrapPointer(long address) {
        return address != 0L ? new PxParticleClothDesc(address) : null;
    }

    public static PxParticleClothDesc arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxParticleClothDesc.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxParticleClothDesc(long address) {
        super(address);
    }

    public static PxParticleClothDesc createAt(long address) {
        PxParticleClothDesc.__placement_new_PxParticleClothDesc(address);
        PxParticleClothDesc createdObj = PxParticleClothDesc.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxParticleClothDesc createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxParticleClothDesc.__placement_new_PxParticleClothDesc(address);
        PxParticleClothDesc createdObj = PxParticleClothDesc.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxParticleClothDesc(long var0);

    public PxParticleClothDesc() {
        this.address = PxParticleClothDesc._PxParticleClothDesc();
    }

    private static native long _PxParticleClothDesc();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxParticleClothDesc._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxParticleCloth getCloths() {
        this.checkNotNull();
        return PxParticleCloth.wrapPointer(PxParticleClothDesc._getCloths(this.address));
    }

    private static native long _getCloths(long var0);

    public void setCloths(PxParticleCloth value) {
        this.checkNotNull();
        PxParticleClothDesc._setCloths(this.address, value.getAddress());
    }

    private static native void _setCloths(long var0, long var2);

    public PxU32Ptr getTriangles() {
        this.checkNotNull();
        return PxU32Ptr.wrapPointer(PxParticleClothDesc._getTriangles(this.address));
    }

    private static native long _getTriangles(long var0);

    public void setTriangles(PxU32Ptr value) {
        this.checkNotNull();
        PxParticleClothDesc._setTriangles(this.address, value.getAddress());
    }

    private static native void _setTriangles(long var0, long var2);

    public PxParticleSpring getSprings() {
        this.checkNotNull();
        return PxParticleSpring.wrapPointer(PxParticleClothDesc._getSprings(this.address));
    }

    private static native long _getSprings(long var0);

    public void setSprings(PxParticleSpring value) {
        this.checkNotNull();
        PxParticleClothDesc._setSprings(this.address, value.getAddress());
    }

    private static native void _setSprings(long var0, long var2);

    public PxVec4 getRestPositions() {
        this.checkNotNull();
        return PxVec4.wrapPointer(PxParticleClothDesc._getRestPositions(this.address));
    }

    private static native long _getRestPositions(long var0);

    public void setRestPositions(PxVec4 value) {
        this.checkNotNull();
        PxParticleClothDesc._setRestPositions(this.address, value.getAddress());
    }

    private static native void _setRestPositions(long var0, long var2);

    public int getNbCloths() {
        this.checkNotNull();
        return PxParticleClothDesc._getNbCloths(this.address);
    }

    private static native int _getNbCloths(long var0);

    public void setNbCloths(int value) {
        this.checkNotNull();
        PxParticleClothDesc._setNbCloths(this.address, value);
    }

    private static native void _setNbCloths(long var0, int var2);

    public int getNbSprings() {
        this.checkNotNull();
        return PxParticleClothDesc._getNbSprings(this.address);
    }

    private static native int _getNbSprings(long var0);

    public void setNbSprings(int value) {
        this.checkNotNull();
        PxParticleClothDesc._setNbSprings(this.address, value);
    }

    private static native void _setNbSprings(long var0, int var2);

    public int getNbTriangles() {
        this.checkNotNull();
        return PxParticleClothDesc._getNbTriangles(this.address);
    }

    private static native int _getNbTriangles(long var0);

    public void setNbTriangles(int value) {
        this.checkNotNull();
        PxParticleClothDesc._setNbTriangles(this.address, value);
    }

    private static native void _setNbTriangles(long var0, int var2);

    public int getNbParticles() {
        this.checkNotNull();
        return PxParticleClothDesc._getNbParticles(this.address);
    }

    private static native int _getNbParticles(long var0);

    public void setNbParticles(int value) {
        this.checkNotNull();
        PxParticleClothDesc._setNbParticles(this.address, value);
    }

    private static native void _setNbParticles(long var0, int var2);

    static {
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleClothDesc");
        SIZEOF = PxParticleClothDesc.__sizeOf();
    }
}

