/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.NativeObject;
import physx.physics.PxGeomSweepHit;
import physx.physics.PxRigidActor;
import physx.physics.PxShape;

public class PxSweepHit
extends PxGeomSweepHit {
    public static final int SIZEOF = PxSweepHit.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxSweepHit wrapPointer(long address) {
        return address != 0L ? new PxSweepHit(address) : null;
    }

    public static PxSweepHit arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxSweepHit.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxSweepHit(long address) {
        super(address);
    }

    public static PxSweepHit createAt(long address) {
        PxSweepHit.__placement_new_PxSweepHit(address);
        PxSweepHit createdObj = PxSweepHit.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxSweepHit createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxSweepHit.__placement_new_PxSweepHit(address);
        PxSweepHit createdObj = PxSweepHit.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxSweepHit(long var0);

    public PxSweepHit() {
        this.address = PxSweepHit._PxSweepHit();
    }

    private static native long _PxSweepHit();

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxSweepHit._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxRigidActor getActor() {
        this.checkNotNull();
        return PxRigidActor.wrapPointer(PxSweepHit._getActor(this.address));
    }

    private static native long _getActor(long var0);

    public void setActor(PxRigidActor value) {
        this.checkNotNull();
        PxSweepHit._setActor(this.address, value.getAddress());
    }

    private static native void _setActor(long var0, long var2);

    public PxShape getShape() {
        this.checkNotNull();
        return PxShape.wrapPointer(PxSweepHit._getShape(this.address));
    }

    private static native long _getShape(long var0);

    public void setShape(PxShape value) {
        this.checkNotNull();
        PxSweepHit._setShape(this.address, value.getAddress());
    }

    private static native void _setShape(long var0, long var2);
}

