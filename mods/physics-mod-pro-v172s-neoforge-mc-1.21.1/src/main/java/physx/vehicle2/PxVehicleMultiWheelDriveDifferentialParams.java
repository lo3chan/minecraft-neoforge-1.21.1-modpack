/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.vehicle2.PxVehicleAxleDescription;
import physx.vehicle2.PxVehicleFrame;
import physx.vehicle2.PxVehicleScale;

public class PxVehicleMultiWheelDriveDifferentialParams
extends NativeObject {
    public static final int SIZEOF = PxVehicleMultiWheelDriveDifferentialParams.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleMultiWheelDriveDifferentialParams wrapPointer(long address) {
        return address != 0L ? new PxVehicleMultiWheelDriveDifferentialParams(address) : null;
    }

    public static PxVehicleMultiWheelDriveDifferentialParams arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleMultiWheelDriveDifferentialParams.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleMultiWheelDriveDifferentialParams(long address) {
        super(address);
    }

    public static PxVehicleMultiWheelDriveDifferentialParams createAt(long address) {
        PxVehicleMultiWheelDriveDifferentialParams.__placement_new_PxVehicleMultiWheelDriveDifferentialParams(address);
        PxVehicleMultiWheelDriveDifferentialParams createdObj = PxVehicleMultiWheelDriveDifferentialParams.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxVehicleMultiWheelDriveDifferentialParams createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxVehicleMultiWheelDriveDifferentialParams.__placement_new_PxVehicleMultiWheelDriveDifferentialParams(address);
        PxVehicleMultiWheelDriveDifferentialParams createdObj = PxVehicleMultiWheelDriveDifferentialParams.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxVehicleMultiWheelDriveDifferentialParams(long var0);

    public PxVehicleMultiWheelDriveDifferentialParams() {
        this.address = PxVehicleMultiWheelDriveDifferentialParams._PxVehicleMultiWheelDriveDifferentialParams();
    }

    private static native long _PxVehicleMultiWheelDriveDifferentialParams();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleMultiWheelDriveDifferentialParams._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getTorqueRatios(int index) {
        this.checkNotNull();
        return PxVehicleMultiWheelDriveDifferentialParams._getTorqueRatios(this.address, index);
    }

    private static native float _getTorqueRatios(long var0, int var2);

    public void setTorqueRatios(int index, float value) {
        this.checkNotNull();
        PxVehicleMultiWheelDriveDifferentialParams._setTorqueRatios(this.address, index, value);
    }

    private static native void _setTorqueRatios(long var0, int var2, float var3);

    public float getAveWheelSpeedRatios(int index) {
        this.checkNotNull();
        return PxVehicleMultiWheelDriveDifferentialParams._getAveWheelSpeedRatios(this.address, index);
    }

    private static native float _getAveWheelSpeedRatios(long var0, int var2);

    public void setAveWheelSpeedRatios(int index, float value) {
        this.checkNotNull();
        PxVehicleMultiWheelDriveDifferentialParams._setAveWheelSpeedRatios(this.address, index, value);
    }

    private static native void _setAveWheelSpeedRatios(long var0, int var2, float var3);

    public void setToDefault() {
        this.checkNotNull();
        PxVehicleMultiWheelDriveDifferentialParams._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);

    public PxVehicleMultiWheelDriveDifferentialParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
        this.checkNotNull();
        return PxVehicleMultiWheelDriveDifferentialParams.wrapPointer(PxVehicleMultiWheelDriveDifferentialParams._transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
    }

    private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

    public boolean isValid(PxVehicleAxleDescription axleDesc) {
        this.checkNotNull();
        return PxVehicleMultiWheelDriveDifferentialParams._isValid(this.address, axleDesc.getAddress());
    }

    private static native boolean _isValid(long var0, long var2);
}

