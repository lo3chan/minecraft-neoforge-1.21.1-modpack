/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.vehicle2.PxVehicleClutchAccuracyModeEnum;
import physx.vehicle2.PxVehicleFrame;
import physx.vehicle2.PxVehicleScale;

public class PxVehicleClutchParams
extends NativeObject {
    public static final int SIZEOF = PxVehicleClutchParams.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleClutchParams wrapPointer(long address) {
        return address != 0L ? new PxVehicleClutchParams(address) : null;
    }

    public static PxVehicleClutchParams arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleClutchParams.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleClutchParams(long address) {
        super(address);
    }

    public static PxVehicleClutchParams createAt(long address) {
        PxVehicleClutchParams.__placement_new_PxVehicleClutchParams(address);
        PxVehicleClutchParams createdObj = PxVehicleClutchParams.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxVehicleClutchParams createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxVehicleClutchParams.__placement_new_PxVehicleClutchParams(address);
        PxVehicleClutchParams createdObj = PxVehicleClutchParams.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxVehicleClutchParams(long var0);

    public PxVehicleClutchParams() {
        this.address = PxVehicleClutchParams._PxVehicleClutchParams();
    }

    private static native long _PxVehicleClutchParams();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleClutchParams._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxVehicleClutchAccuracyModeEnum getAccuracyMode() {
        this.checkNotNull();
        return PxVehicleClutchAccuracyModeEnum.forValue(PxVehicleClutchParams._getAccuracyMode(this.address));
    }

    private static native int _getAccuracyMode(long var0);

    public void setAccuracyMode(PxVehicleClutchAccuracyModeEnum value) {
        this.checkNotNull();
        PxVehicleClutchParams._setAccuracyMode(this.address, value.value);
    }

    private static native void _setAccuracyMode(long var0, int var2);

    public int getEstimateIterations() {
        this.checkNotNull();
        return PxVehicleClutchParams._getEstimateIterations(this.address);
    }

    private static native int _getEstimateIterations(long var0);

    public void setEstimateIterations(int value) {
        this.checkNotNull();
        PxVehicleClutchParams._setEstimateIterations(this.address, value);
    }

    private static native void _setEstimateIterations(long var0, int var2);

    public PxVehicleClutchParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
        this.checkNotNull();
        return PxVehicleClutchParams.wrapPointer(PxVehicleClutchParams._transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
    }

    private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

    public boolean isValid() {
        this.checkNotNull();
        return PxVehicleClutchParams._isValid(this.address);
    }

    private static native boolean _isValid(long var0);
}

