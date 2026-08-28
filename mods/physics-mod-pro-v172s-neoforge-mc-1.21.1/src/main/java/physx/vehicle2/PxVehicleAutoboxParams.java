/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.vehicle2.PxVehicleFrame;
import physx.vehicle2.PxVehicleGearboxParams;
import physx.vehicle2.PxVehicleScale;

public class PxVehicleAutoboxParams
extends NativeObject {
    public static final int SIZEOF = PxVehicleAutoboxParams.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleAutoboxParams wrapPointer(long address) {
        return address != 0L ? new PxVehicleAutoboxParams(address) : null;
    }

    public static PxVehicleAutoboxParams arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleAutoboxParams.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleAutoboxParams(long address) {
        super(address);
    }

    public static PxVehicleAutoboxParams createAt(long address) {
        PxVehicleAutoboxParams.__placement_new_PxVehicleAutoboxParams(address);
        PxVehicleAutoboxParams createdObj = PxVehicleAutoboxParams.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxVehicleAutoboxParams createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxVehicleAutoboxParams.__placement_new_PxVehicleAutoboxParams(address);
        PxVehicleAutoboxParams createdObj = PxVehicleAutoboxParams.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxVehicleAutoboxParams(long var0);

    public PxVehicleAutoboxParams() {
        this.address = PxVehicleAutoboxParams._PxVehicleAutoboxParams();
    }

    private static native long _PxVehicleAutoboxParams();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleAutoboxParams._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getUpRatios(int index) {
        this.checkNotNull();
        return PxVehicleAutoboxParams._getUpRatios(this.address, index);
    }

    private static native float _getUpRatios(long var0, int var2);

    public void setUpRatios(int index, float value) {
        this.checkNotNull();
        PxVehicleAutoboxParams._setUpRatios(this.address, index, value);
    }

    private static native void _setUpRatios(long var0, int var2, float var3);

    public float getDownRatios(int index) {
        this.checkNotNull();
        return PxVehicleAutoboxParams._getDownRatios(this.address, index);
    }

    private static native float _getDownRatios(long var0, int var2);

    public void setDownRatios(int index, float value) {
        this.checkNotNull();
        PxVehicleAutoboxParams._setDownRatios(this.address, index, value);
    }

    private static native void _setDownRatios(long var0, int var2, float var3);

    public float getLatency() {
        this.checkNotNull();
        return PxVehicleAutoboxParams._getLatency(this.address);
    }

    private static native float _getLatency(long var0);

    public void setLatency(float value) {
        this.checkNotNull();
        PxVehicleAutoboxParams._setLatency(this.address, value);
    }

    private static native void _setLatency(long var0, float var2);

    public PxVehicleAutoboxParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
        this.checkNotNull();
        return PxVehicleAutoboxParams.wrapPointer(PxVehicleAutoboxParams._transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
    }

    private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

    public boolean isValid(PxVehicleGearboxParams gearboxParams) {
        this.checkNotNull();
        return PxVehicleAutoboxParams._isValid(this.address, gearboxParams.getAddress());
    }

    private static native boolean _isValid(long var0, long var2);
}

