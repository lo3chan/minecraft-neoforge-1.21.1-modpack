/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import physx.NativeObject;
import physx.common.PxIDENTITYEnum;
import physx.common.PxQuat;
import physx.common.PxVec3;

public class PxTransform
extends NativeObject {
    public static final int SIZEOF = PxTransform.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxTransform wrapPointer(long address) {
        return address != 0L ? new PxTransform(address) : null;
    }

    public static PxTransform arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxTransform.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxTransform(long address) {
        super(address);
    }

    public static PxTransform createAt(long address) {
        PxTransform.__placement_new_PxTransform(address);
        PxTransform createdObj = PxTransform.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxTransform createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxTransform.__placement_new_PxTransform(address);
        PxTransform createdObj = PxTransform.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxTransform(long var0);

    public static PxTransform createAt(long address, PxIDENTITYEnum r) {
        PxTransform.__placement_new_PxTransform(address, r.value);
        PxTransform createdObj = PxTransform.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxTransform createAt(T allocator, NativeObject.Allocator<T> allocate, PxIDENTITYEnum r) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxTransform.__placement_new_PxTransform(address, r.value);
        PxTransform createdObj = PxTransform.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxTransform(long var0, int var2);

    public static PxTransform createAt(long address, PxVec3 p0, PxQuat q0) {
        PxTransform.__placement_new_PxTransform(address, p0.getAddress(), q0.getAddress());
        PxTransform createdObj = PxTransform.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxTransform createAt(T allocator, NativeObject.Allocator<T> allocate, PxVec3 p0, PxQuat q0) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxTransform.__placement_new_PxTransform(address, p0.getAddress(), q0.getAddress());
        PxTransform createdObj = PxTransform.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxTransform(long var0, long var2, long var4);

    public PxTransform() {
        this.address = PxTransform._PxTransform();
    }

    private static native long _PxTransform();

    public PxTransform(PxIDENTITYEnum r) {
        this.address = PxTransform._PxTransform(r.value);
    }

    private static native long _PxTransform(int var0);

    public PxTransform(PxVec3 p0, PxQuat q0) {
        this.address = PxTransform._PxTransform(p0.getAddress(), q0.getAddress());
    }

    private static native long _PxTransform(long var0, long var2);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxTransform._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxQuat getQ() {
        this.checkNotNull();
        return PxQuat.wrapPointer(PxTransform._getQ(this.address));
    }

    private static native long _getQ(long var0);

    public void setQ(PxQuat value) {
        this.checkNotNull();
        PxTransform._setQ(this.address, value.getAddress());
    }

    private static native void _setQ(long var0, long var2);

    public PxVec3 getP() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxTransform._getP(this.address));
    }

    private static native long _getP(long var0);

    public void setP(PxVec3 value) {
        this.checkNotNull();
        PxTransform._setP(this.address, value.getAddress());
    }

    private static native void _setP(long var0, long var2);
}

