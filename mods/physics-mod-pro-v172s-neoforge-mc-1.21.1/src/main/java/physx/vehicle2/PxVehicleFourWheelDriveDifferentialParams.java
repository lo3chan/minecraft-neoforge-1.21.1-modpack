/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.vehicle2.PxVehicleFrame;
import physx.vehicle2.PxVehicleMultiWheelDriveDifferentialParams;
import physx.vehicle2.PxVehicleScale;

public class PxVehicleFourWheelDriveDifferentialParams
extends PxVehicleMultiWheelDriveDifferentialParams {
    public static final int SIZEOF = PxVehicleFourWheelDriveDifferentialParams.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleFourWheelDriveDifferentialParams wrapPointer(long address) {
        return address != 0L ? new PxVehicleFourWheelDriveDifferentialParams(address) : null;
    }

    public static PxVehicleFourWheelDriveDifferentialParams arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleFourWheelDriveDifferentialParams.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleFourWheelDriveDifferentialParams(long address) {
        super(address);
    }

    public static PxVehicleFourWheelDriveDifferentialParams createAt(long address) {
        PxVehicleFourWheelDriveDifferentialParams.__placement_new_PxVehicleFourWheelDriveDifferentialParams(address);
        PxVehicleFourWheelDriveDifferentialParams createdObj = PxVehicleFourWheelDriveDifferentialParams.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxVehicleFourWheelDriveDifferentialParams createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxVehicleFourWheelDriveDifferentialParams.__placement_new_PxVehicleFourWheelDriveDifferentialParams(address);
        PxVehicleFourWheelDriveDifferentialParams createdObj = PxVehicleFourWheelDriveDifferentialParams.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxVehicleFourWheelDriveDifferentialParams(long var0);

    public PxVehicleFourWheelDriveDifferentialParams() {
        this.address = PxVehicleFourWheelDriveDifferentialParams._PxVehicleFourWheelDriveDifferentialParams();
    }

    private static native long _PxVehicleFourWheelDriveDifferentialParams();

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleFourWheelDriveDifferentialParams._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public int getFrontWheelIds(int index) {
        this.checkNotNull();
        return PxVehicleFourWheelDriveDifferentialParams._getFrontWheelIds(this.address, index);
    }

    private static native int _getFrontWheelIds(long var0, int var2);

    public void setFrontWheelIds(int index, int value) {
        this.checkNotNull();
        PxVehicleFourWheelDriveDifferentialParams._setFrontWheelIds(this.address, index, value);
    }

    private static native void _setFrontWheelIds(long var0, int var2, int var3);

    public int getRearWheelIds(int index) {
        this.checkNotNull();
        return PxVehicleFourWheelDriveDifferentialParams._getRearWheelIds(this.address, index);
    }

    private static native int _getRearWheelIds(long var0, int var2);

    public void setRearWheelIds(int index, int value) {
        this.checkNotNull();
        PxVehicleFourWheelDriveDifferentialParams._setRearWheelIds(this.address, index, value);
    }

    private static native void _setRearWheelIds(long var0, int var2, int var3);

    public float getFrontBias() {
        this.checkNotNull();
        return PxVehicleFourWheelDriveDifferentialParams._getFrontBias(this.address);
    }

    private static native float _getFrontBias(long var0);

    public void setFrontBias(float value) {
        this.checkNotNull();
        PxVehicleFourWheelDriveDifferentialParams._setFrontBias(this.address, value);
    }

    private static native void _setFrontBias(long var0, float var2);

    public float getFrontTarget() {
        this.checkNotNull();
        return PxVehicleFourWheelDriveDifferentialParams._getFrontTarget(this.address);
    }

    private static native float _getFrontTarget(long var0);

    public void setFrontTarget(float value) {
        this.checkNotNull();
        PxVehicleFourWheelDriveDifferentialParams._setFrontTarget(this.address, value);
    }

    private static native void _setFrontTarget(long var0, float var2);

    public float getRearBias() {
        this.checkNotNull();
        return PxVehicleFourWheelDriveDifferentialParams._getRearBias(this.address);
    }

    private static native float _getRearBias(long var0);

    public void setRearBias(float value) {
        this.checkNotNull();
        PxVehicleFourWheelDriveDifferentialParams._setRearBias(this.address, value);
    }

    private static native void _setRearBias(long var0, float var2);

    public float getRearTarget() {
        this.checkNotNull();
        return PxVehicleFourWheelDriveDifferentialParams._getRearTarget(this.address);
    }

    private static native float _getRearTarget(long var0);

    public void setRearTarget(float value) {
        this.checkNotNull();
        PxVehicleFourWheelDriveDifferentialParams._setRearTarget(this.address, value);
    }

    private static native void _setRearTarget(long var0, float var2);

    public float getCenterBias() {
        this.checkNotNull();
        return PxVehicleFourWheelDriveDifferentialParams._getCenterBias(this.address);
    }

    private static native float _getCenterBias(long var0);

    public void setCenterBias(float value) {
        this.checkNotNull();
        PxVehicleFourWheelDriveDifferentialParams._setCenterBias(this.address, value);
    }

    private static native void _setCenterBias(long var0, float var2);

    public float getCenterTarget() {
        this.checkNotNull();
        return PxVehicleFourWheelDriveDifferentialParams._getCenterTarget(this.address);
    }

    private static native float _getCenterTarget(long var0);

    public void setCenterTarget(float value) {
        this.checkNotNull();
        PxVehicleFourWheelDriveDifferentialParams._setCenterTarget(this.address, value);
    }

    private static native void _setCenterTarget(long var0, float var2);

    public float getRate() {
        this.checkNotNull();
        return PxVehicleFourWheelDriveDifferentialParams._getRate(this.address);
    }

    private static native float _getRate(long var0);

    public void setRate(float value) {
        this.checkNotNull();
        PxVehicleFourWheelDriveDifferentialParams._setRate(this.address, value);
    }

    private static native void _setRate(long var0, float var2);

    @Override
    public void setToDefault() {
        this.checkNotNull();
        PxVehicleFourWheelDriveDifferentialParams._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);

    @Override
    public PxVehicleFourWheelDriveDifferentialParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
        this.checkNotNull();
        return PxVehicleFourWheelDriveDifferentialParams.wrapPointer(PxVehicleFourWheelDriveDifferentialParams._transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
    }

    private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);
}

