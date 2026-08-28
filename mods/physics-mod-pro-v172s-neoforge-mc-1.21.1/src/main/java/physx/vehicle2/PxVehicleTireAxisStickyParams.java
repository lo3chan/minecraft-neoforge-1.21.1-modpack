/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.vehicle2.PxVehicleFrame;
import physx.vehicle2.PxVehicleScale;

public class PxVehicleTireAxisStickyParams
extends NativeObject {
    public static final int SIZEOF = PxVehicleTireAxisStickyParams.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleTireAxisStickyParams wrapPointer(long address) {
        return address != 0L ? new PxVehicleTireAxisStickyParams(address) : null;
    }

    public static PxVehicleTireAxisStickyParams arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleTireAxisStickyParams.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleTireAxisStickyParams(long address) {
        super(address);
    }

    public static PxVehicleTireAxisStickyParams createAt(long address) {
        PxVehicleTireAxisStickyParams.__placement_new_PxVehicleTireAxisStickyParams(address);
        PxVehicleTireAxisStickyParams createdObj = PxVehicleTireAxisStickyParams.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxVehicleTireAxisStickyParams createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxVehicleTireAxisStickyParams.__placement_new_PxVehicleTireAxisStickyParams(address);
        PxVehicleTireAxisStickyParams createdObj = PxVehicleTireAxisStickyParams.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxVehicleTireAxisStickyParams(long var0);

    public PxVehicleTireAxisStickyParams() {
        this.address = PxVehicleTireAxisStickyParams._PxVehicleTireAxisStickyParams();
    }

    private static native long _PxVehicleTireAxisStickyParams();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleTireAxisStickyParams._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getThresholdSpeed() {
        this.checkNotNull();
        return PxVehicleTireAxisStickyParams._getThresholdSpeed(this.address);
    }

    private static native float _getThresholdSpeed(long var0);

    public void setThresholdSpeed(float value) {
        this.checkNotNull();
        PxVehicleTireAxisStickyParams._setThresholdSpeed(this.address, value);
    }

    private static native void _setThresholdSpeed(long var0, float var2);

    public float getThresholdTime() {
        this.checkNotNull();
        return PxVehicleTireAxisStickyParams._getThresholdTime(this.address);
    }

    private static native float _getThresholdTime(long var0);

    public void setThresholdTime(float value) {
        this.checkNotNull();
        PxVehicleTireAxisStickyParams._setThresholdTime(this.address, value);
    }

    private static native void _setThresholdTime(long var0, float var2);

    public float getDamping() {
        this.checkNotNull();
        return PxVehicleTireAxisStickyParams._getDamping(this.address);
    }

    private static native float _getDamping(long var0);

    public void setDamping(float value) {
        this.checkNotNull();
        PxVehicleTireAxisStickyParams._setDamping(this.address, value);
    }

    private static native void _setDamping(long var0, float var2);

    public PxVehicleTireAxisStickyParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
        this.checkNotNull();
        return PxVehicleTireAxisStickyParams.wrapPointer(PxVehicleTireAxisStickyParams._transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
    }

    private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

    public boolean isValid() {
        this.checkNotNull();
        return PxVehicleTireAxisStickyParams._isValid(this.address);
    }

    private static native boolean _isValid(long var0);
}

