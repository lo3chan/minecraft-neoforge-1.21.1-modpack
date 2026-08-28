/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.vehicle2.PxVehicleFrame;
import physx.vehicle2.PxVehicleScale;
import physx.vehicle2.PxVehicleTorqueCurveLookupTable;

public class PxVehicleEngineParams
extends NativeObject {
    public static final int SIZEOF = PxVehicleEngineParams.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleEngineParams wrapPointer(long address) {
        return address != 0L ? new PxVehicleEngineParams(address) : null;
    }

    public static PxVehicleEngineParams arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleEngineParams.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleEngineParams(long address) {
        super(address);
    }

    public static PxVehicleEngineParams createAt(long address) {
        PxVehicleEngineParams.__placement_new_PxVehicleEngineParams(address);
        PxVehicleEngineParams createdObj = PxVehicleEngineParams.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxVehicleEngineParams createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxVehicleEngineParams.__placement_new_PxVehicleEngineParams(address);
        PxVehicleEngineParams createdObj = PxVehicleEngineParams.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxVehicleEngineParams(long var0);

    public PxVehicleEngineParams() {
        this.address = PxVehicleEngineParams._PxVehicleEngineParams();
    }

    private static native long _PxVehicleEngineParams();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleEngineParams._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxVehicleTorqueCurveLookupTable getTorqueCurve() {
        this.checkNotNull();
        return PxVehicleTorqueCurveLookupTable.wrapPointer(PxVehicleEngineParams._getTorqueCurve(this.address));
    }

    private static native long _getTorqueCurve(long var0);

    public void setTorqueCurve(PxVehicleTorqueCurveLookupTable value) {
        this.checkNotNull();
        PxVehicleEngineParams._setTorqueCurve(this.address, value.getAddress());
    }

    private static native void _setTorqueCurve(long var0, long var2);

    public float getMoi() {
        this.checkNotNull();
        return PxVehicleEngineParams._getMoi(this.address);
    }

    private static native float _getMoi(long var0);

    public void setMoi(float value) {
        this.checkNotNull();
        PxVehicleEngineParams._setMoi(this.address, value);
    }

    private static native void _setMoi(long var0, float var2);

    public float getPeakTorque() {
        this.checkNotNull();
        return PxVehicleEngineParams._getPeakTorque(this.address);
    }

    private static native float _getPeakTorque(long var0);

    public void setPeakTorque(float value) {
        this.checkNotNull();
        PxVehicleEngineParams._setPeakTorque(this.address, value);
    }

    private static native void _setPeakTorque(long var0, float var2);

    public float getIdleOmega() {
        this.checkNotNull();
        return PxVehicleEngineParams._getIdleOmega(this.address);
    }

    private static native float _getIdleOmega(long var0);

    public void setIdleOmega(float value) {
        this.checkNotNull();
        PxVehicleEngineParams._setIdleOmega(this.address, value);
    }

    private static native void _setIdleOmega(long var0, float var2);

    public float getMaxOmega() {
        this.checkNotNull();
        return PxVehicleEngineParams._getMaxOmega(this.address);
    }

    private static native float _getMaxOmega(long var0);

    public void setMaxOmega(float value) {
        this.checkNotNull();
        PxVehicleEngineParams._setMaxOmega(this.address, value);
    }

    private static native void _setMaxOmega(long var0, float var2);

    public float getDampingRateFullThrottle() {
        this.checkNotNull();
        return PxVehicleEngineParams._getDampingRateFullThrottle(this.address);
    }

    private static native float _getDampingRateFullThrottle(long var0);

    public void setDampingRateFullThrottle(float value) {
        this.checkNotNull();
        PxVehicleEngineParams._setDampingRateFullThrottle(this.address, value);
    }

    private static native void _setDampingRateFullThrottle(long var0, float var2);

    public float getDampingRateZeroThrottleClutchEngaged() {
        this.checkNotNull();
        return PxVehicleEngineParams._getDampingRateZeroThrottleClutchEngaged(this.address);
    }

    private static native float _getDampingRateZeroThrottleClutchEngaged(long var0);

    public void setDampingRateZeroThrottleClutchEngaged(float value) {
        this.checkNotNull();
        PxVehicleEngineParams._setDampingRateZeroThrottleClutchEngaged(this.address, value);
    }

    private static native void _setDampingRateZeroThrottleClutchEngaged(long var0, float var2);

    public float getDampingRateZeroThrottleClutchDisengaged() {
        this.checkNotNull();
        return PxVehicleEngineParams._getDampingRateZeroThrottleClutchDisengaged(this.address);
    }

    private static native float _getDampingRateZeroThrottleClutchDisengaged(long var0);

    public void setDampingRateZeroThrottleClutchDisengaged(float value) {
        this.checkNotNull();
        PxVehicleEngineParams._setDampingRateZeroThrottleClutchDisengaged(this.address, value);
    }

    private static native void _setDampingRateZeroThrottleClutchDisengaged(long var0, float var2);

    public PxVehicleEngineParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
        this.checkNotNull();
        return PxVehicleEngineParams.wrapPointer(PxVehicleEngineParams._transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
    }

    private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

    public boolean isValid() {
        this.checkNotNull();
        return PxVehicleEngineParams._isValid(this.address);
    }

    private static native boolean _isValid(long var0);
}

