/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.vehicle2.PxVehicleFrame;
import physx.vehicle2.PxVehicleScale;

public class PxVehicleTireSlipParams
extends NativeObject {
    public static final int SIZEOF = PxVehicleTireSlipParams.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleTireSlipParams wrapPointer(long address) {
        return address != 0L ? new PxVehicleTireSlipParams(address) : null;
    }

    public static PxVehicleTireSlipParams arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleTireSlipParams.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleTireSlipParams(long address) {
        super(address);
    }

    public static PxVehicleTireSlipParams createAt(long address) {
        PxVehicleTireSlipParams.__placement_new_PxVehicleTireSlipParams(address);
        PxVehicleTireSlipParams createdObj = PxVehicleTireSlipParams.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxVehicleTireSlipParams createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxVehicleTireSlipParams.__placement_new_PxVehicleTireSlipParams(address);
        PxVehicleTireSlipParams createdObj = PxVehicleTireSlipParams.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxVehicleTireSlipParams(long var0);

    public PxVehicleTireSlipParams() {
        this.address = PxVehicleTireSlipParams._PxVehicleTireSlipParams();
    }

    private static native long _PxVehicleTireSlipParams();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleTireSlipParams._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getMinLatSlipDenominator() {
        this.checkNotNull();
        return PxVehicleTireSlipParams._getMinLatSlipDenominator(this.address);
    }

    private static native float _getMinLatSlipDenominator(long var0);

    public void setMinLatSlipDenominator(float value) {
        this.checkNotNull();
        PxVehicleTireSlipParams._setMinLatSlipDenominator(this.address, value);
    }

    private static native void _setMinLatSlipDenominator(long var0, float var2);

    public float getMinPassiveLongSlipDenominator() {
        this.checkNotNull();
        return PxVehicleTireSlipParams._getMinPassiveLongSlipDenominator(this.address);
    }

    private static native float _getMinPassiveLongSlipDenominator(long var0);

    public void setMinPassiveLongSlipDenominator(float value) {
        this.checkNotNull();
        PxVehicleTireSlipParams._setMinPassiveLongSlipDenominator(this.address, value);
    }

    private static native void _setMinPassiveLongSlipDenominator(long var0, float var2);

    public float getMinActiveLongSlipDenominator() {
        this.checkNotNull();
        return PxVehicleTireSlipParams._getMinActiveLongSlipDenominator(this.address);
    }

    private static native float _getMinActiveLongSlipDenominator(long var0);

    public void setMinActiveLongSlipDenominator(float value) {
        this.checkNotNull();
        PxVehicleTireSlipParams._setMinActiveLongSlipDenominator(this.address, value);
    }

    private static native void _setMinActiveLongSlipDenominator(long var0, float var2);

    public void setToDefault() {
        this.checkNotNull();
        PxVehicleTireSlipParams._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);

    public PxVehicleTireSlipParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
        this.checkNotNull();
        return PxVehicleTireSlipParams.wrapPointer(PxVehicleTireSlipParams._transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
    }

    private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

    public boolean isValid() {
        this.checkNotNull();
        return PxVehicleTireSlipParams._isValid(this.address);
    }

    private static native boolean _isValid(long var0);
}

