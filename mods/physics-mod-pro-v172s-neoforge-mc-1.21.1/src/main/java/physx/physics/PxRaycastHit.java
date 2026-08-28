/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.NativeObject;
import physx.physics.PxGeomRaycastHit;
import physx.physics.PxRigidActor;
import physx.physics.PxShape;

public class PxRaycastHit
extends PxGeomRaycastHit {
    public static final int SIZEOF = PxRaycastHit.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxRaycastHit wrapPointer(long address) {
        return address != 0L ? new PxRaycastHit(address) : null;
    }

    public static PxRaycastHit arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxRaycastHit.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxRaycastHit(long address) {
        super(address);
    }

    public static PxRaycastHit createAt(long address) {
        PxRaycastHit.__placement_new_PxRaycastHit(address);
        PxRaycastHit createdObj = PxRaycastHit.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxRaycastHit createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxRaycastHit.__placement_new_PxRaycastHit(address);
        PxRaycastHit createdObj = PxRaycastHit.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxRaycastHit(long var0);

    public PxRaycastHit() {
        this.address = PxRaycastHit._PxRaycastHit();
    }

    private static native long _PxRaycastHit();

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxRaycastHit._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxRigidActor getActor() {
        this.checkNotNull();
        return PxRigidActor.wrapPointer(PxRaycastHit._getActor(this.address));
    }

    private static native long _getActor(long var0);

    public void setActor(PxRigidActor value) {
        this.checkNotNull();
        PxRaycastHit._setActor(this.address, value.getAddress());
    }

    private static native void _setActor(long var0, long var2);

    public PxShape getShape() {
        this.checkNotNull();
        return PxShape.wrapPointer(PxRaycastHit._getShape(this.address));
    }

    private static native long _getShape(long var0);

    public void setShape(PxShape value) {
        this.checkNotNull();
        PxRaycastHit._setShape(this.address, value.getAddress());
    }

    private static native void _setShape(long var0, long var2);
}

