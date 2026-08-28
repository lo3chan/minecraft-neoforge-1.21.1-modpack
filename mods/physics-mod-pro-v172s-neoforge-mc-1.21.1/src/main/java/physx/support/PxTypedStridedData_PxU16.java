/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.NativeObject;
import physx.support.PxU16ConstPtr;

public class PxTypedStridedData_PxU16
extends NativeObject {
    public static final int SIZEOF = PxTypedStridedData_PxU16.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxTypedStridedData_PxU16() {
    }

    private static native int __sizeOf();

    public static PxTypedStridedData_PxU16 wrapPointer(long address) {
        return address != 0L ? new PxTypedStridedData_PxU16(address) : null;
    }

    public static PxTypedStridedData_PxU16 arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxTypedStridedData_PxU16.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxTypedStridedData_PxU16(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxTypedStridedData_PxU16._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public int getStride() {
        this.checkNotNull();
        return PxTypedStridedData_PxU16._getStride(this.address);
    }

    private static native int _getStride(long var0);

    public void setStride(int value) {
        this.checkNotNull();
        PxTypedStridedData_PxU16._setStride(this.address, value);
    }

    private static native void _setStride(long var0, int var2);

    public PxU16ConstPtr getData() {
        this.checkNotNull();
        return PxU16ConstPtr.wrapPointer(PxTypedStridedData_PxU16._getData(this.address));
    }

    private static native long _getData(long var0);

    public void setData(PxU16ConstPtr value) {
        this.checkNotNull();
        PxTypedStridedData_PxU16._setData(this.address, value.getAddress());
    }

    private static native void _setData(long var0, long var2);
}

