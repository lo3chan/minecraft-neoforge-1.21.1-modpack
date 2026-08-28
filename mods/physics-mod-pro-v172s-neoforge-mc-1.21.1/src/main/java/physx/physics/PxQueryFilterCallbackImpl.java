/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.physics.PxFilterData;
import physx.physics.PxHitFlags;
import physx.physics.PxQueryHit;
import physx.physics.PxRigidActor;
import physx.physics.PxShape;
import physx.physics.SimpleQueryFilterCallback;

public class PxQueryFilterCallbackImpl
extends SimpleQueryFilterCallback {
    public static final int SIZEOF = PxQueryFilterCallbackImpl.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxQueryFilterCallbackImpl wrapPointer(long address) {
        return address != 0L ? new PxQueryFilterCallbackImpl(address) : null;
    }

    public static PxQueryFilterCallbackImpl arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxQueryFilterCallbackImpl.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxQueryFilterCallbackImpl(long address) {
        super(address);
    }

    protected PxQueryFilterCallbackImpl() {
        this.address = this._PxQueryFilterCallbackImpl();
    }

    private native long _PxQueryFilterCallbackImpl();

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxQueryFilterCallbackImpl._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    private int _simplePreFilter(long filterData, long shape, long actor, long queryFlags) {
        return this.simplePreFilter(PxFilterData.wrapPointer(filterData), PxShape.wrapPointer(shape), PxRigidActor.wrapPointer(actor), PxHitFlags.wrapPointer(queryFlags));
    }

    @Override
    public int simplePreFilter(PxFilterData filterData, PxShape shape, PxRigidActor actor, PxHitFlags queryFlags) {
        return 0;
    }

    private int _simplePostFilter(long filterData, long hit, long shape, long actor) {
        return this.simplePostFilter(PxFilterData.wrapPointer(filterData), PxQueryHit.wrapPointer(hit), PxShape.wrapPointer(shape), PxRigidActor.wrapPointer(actor));
    }

    @Override
    public int simplePostFilter(PxFilterData filterData, PxQueryHit hit, PxShape shape, PxRigidActor actor) {
        return 0;
    }
}

