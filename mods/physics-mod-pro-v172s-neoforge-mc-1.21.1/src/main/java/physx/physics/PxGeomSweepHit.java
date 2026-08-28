/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.physics.PxLocationHit;

public class PxGeomSweepHit
extends PxLocationHit {
    public static final int SIZEOF = PxGeomSweepHit.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxGeomSweepHit() {
    }

    private static native int __sizeOf();

    public static PxGeomSweepHit wrapPointer(long address) {
        return address != 0L ? new PxGeomSweepHit(address) : null;
    }

    public static PxGeomSweepHit arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxGeomSweepHit.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxGeomSweepHit(long address) {
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
        PxGeomSweepHit._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean hadInitialOverlap() {
        this.checkNotNull();
        return PxGeomSweepHit._hadInitialOverlap(this.address);
    }

    private static native boolean _hadInitialOverlap(long var0);
}

