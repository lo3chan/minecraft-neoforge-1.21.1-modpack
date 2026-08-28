/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.common.PxVec3;
import physx.physics.PxHitFlags;
import physx.physics.PxQueryHit;

public class PxLocationHit
extends PxQueryHit {
    public static final int SIZEOF = PxLocationHit.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxLocationHit() {
    }

    private static native int __sizeOf();

    public static PxLocationHit wrapPointer(long address) {
        return address != 0L ? new PxLocationHit(address) : null;
    }

    public static PxLocationHit arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxLocationHit.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxLocationHit(long address) {
        super(address);
    }

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxLocationHit._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxHitFlags getFlags() {
        this.checkNotNull();
        return PxHitFlags.wrapPointer(PxLocationHit._getFlags(this.address));
    }

    private static native long _getFlags(long var0);

    public void setFlags(PxHitFlags value) {
        this.checkNotNull();
        PxLocationHit._setFlags(this.address, value.getAddress());
    }

    private static native void _setFlags(long var0, long var2);

    public PxVec3 getPosition() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxLocationHit._getPosition(this.address));
    }

    private static native long _getPosition(long var0);

    public void setPosition(PxVec3 value) {
        this.checkNotNull();
        PxLocationHit._setPosition(this.address, value.getAddress());
    }

    private static native void _setPosition(long var0, long var2);

    public PxVec3 getNormal() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxLocationHit._getNormal(this.address));
    }

    private static native long _getNormal(long var0);

    public void setNormal(PxVec3 value) {
        this.checkNotNull();
        PxLocationHit._setNormal(this.address, value.getAddress());
    }

    private static native void _setNormal(long var0, long var2);

    public float getDistance() {
        this.checkNotNull();
        return PxLocationHit._getDistance(this.address);
    }

    private static native float _getDistance(long var0);

    public void setDistance(float value) {
        this.checkNotNull();
        PxLocationHit._setDistance(this.address, value);
    }

    private static native void _setDistance(long var0, float var2);
}

