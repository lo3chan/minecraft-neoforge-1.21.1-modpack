/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.vehicle2.PxVehicleFrame;
import physx.vehicle2.PxVehicleScale;
import physx.vehicle2.PxVehicleTireAxisStickyParams;

public class PxVehicleTireStickyParams
extends NativeObject {
    public static final int SIZEOF = PxVehicleTireStickyParams.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleTireStickyParams wrapPointer(long address) {
        return address != 0L ? new PxVehicleTireStickyParams(address) : null;
    }

    public static PxVehicleTireStickyParams arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleTireStickyParams.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleTireStickyParams(long address) {
        super(address);
    }

    public static PxVehicleTireStickyParams createAt(long address) {
        PxVehicleTireStickyParams.__placement_new_PxVehicleTireStickyParams(address);
        PxVehicleTireStickyParams createdObj = PxVehicleTireStickyParams.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxVehicleTireStickyParams createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxVehicleTireStickyParams.__placement_new_PxVehicleTireStickyParams(address);
        PxVehicleTireStickyParams createdObj = PxVehicleTireStickyParams.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxVehicleTireStickyParams(long var0);

    public PxVehicleTireStickyParams() {
        this.address = PxVehicleTireStickyParams._PxVehicleTireStickyParams();
    }

    private static native long _PxVehicleTireStickyParams();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleTireStickyParams._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxVehicleTireAxisStickyParams getStickyParams(int index) {
        this.checkNotNull();
        return PxVehicleTireAxisStickyParams.wrapPointer(PxVehicleTireStickyParams._getStickyParams(this.address, index));
    }

    private static native long _getStickyParams(long var0, int var2);

    public void setStickyParams(int index, PxVehicleTireAxisStickyParams value) {
        this.checkNotNull();
        PxVehicleTireStickyParams._setStickyParams(this.address, index, value.getAddress());
    }

    private static native void _setStickyParams(long var0, int var2, long var3);

    public void setToDefault() {
        this.checkNotNull();
        PxVehicleTireStickyParams._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);

    public PxVehicleTireStickyParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
        this.checkNotNull();
        return PxVehicleTireStickyParams.wrapPointer(PxVehicleTireStickyParams._transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
    }

    private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

    public boolean isValid() {
        this.checkNotNull();
        return PxVehicleTireStickyParams._isValid(this.address);
    }

    private static native boolean _isValid(long var0);
}

